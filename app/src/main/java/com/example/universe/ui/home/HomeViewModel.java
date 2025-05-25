package com.example.universe.ui.home;

import android.app.Application;
import android.content.res.AssetManager;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.universe.ui.exam_schedule.Exam;
import com.example.universe.ui.menu.MenuItem;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.ArrayList;

public class HomeViewModel extends AndroidViewModel {

    private final MutableLiveData<String> todayDate = new MutableLiveData<>();
    private final MutableLiveData<List<MenuItem>> todayMeals = new MutableLiveData<>();
    private final MutableLiveData<Integer> totalCalories = new MutableLiveData<>();
    private final MutableLiveData<String> nextLecture = new MutableLiveData<>();
    private final MutableLiveData<Exam> closestExam = new MutableLiveData<>();

    public HomeViewModel(@NonNull Application application) {
        super(application);
        updateDate();
        loadTodayMealsFromJson();
        setupCourseListener();
        setupExamListener();
    }

    public LiveData<String> getTodayDate() {
        return todayDate;
    }

    public LiveData<List<MenuItem>> getTodayMeals() {
        return todayMeals;
    }

    public LiveData<Integer> getTotalCalories() {
        return totalCalories;
    }

    public LiveData<String> getNextLecture() {
        return nextLecture;
    }

    public LiveData<Exam> getClosestExam() {
        return closestExam;
    }

    private void updateDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("d MMMM yyyy EEEE", new Locale("tr", "TR"));
        String currentDate = sdf.format(new Date());
        todayDate.setValue(currentDate);
    }

    private void setupCourseListener() {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference coursesRef = FirebaseDatabase.getInstance().getReference("Courses").child(userId);

        coursesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String currentDay = todayDate.getValue().split(" ")[3];
                String currentTime = new SimpleDateFormat("HH:mm", Locale.getDefault()).format(new Date());

                String closestUpcomingLecture = null;
                String closestTime = null;
                long minTimeDifference = Long.MAX_VALUE;

                String firstLectureName = null;
                String firstLectureTime = null;
                long earliestTime = Long.MAX_VALUE;

                for (DataSnapshot courseSnapshot : snapshot.getChildren()) {
                    String day = courseSnapshot.child("day").getValue(String.class);
                    String name = courseSnapshot.child("name").getValue(String.class);
                    String startTime = courseSnapshot.child("startTime").getValue(String.class);

                    if (day != null && day.equals(currentDay)) {
                        long lectureTime = convertTimeToMinutes(startTime);
                        long currentTimeInMinutes = convertTimeToMinutes(currentTime);
                        long timeDifference = lectureTime - currentTimeInMinutes;

                        if (timeDifference >= 0 && timeDifference < minTimeDifference) {
                            minTimeDifference = timeDifference;
                            closestUpcomingLecture = name;
                            closestTime = startTime;
                        }

                        if (lectureTime < earliestTime) {
                            earliestTime = lectureTime;
                            firstLectureName = name;
                            firstLectureTime = startTime;
                        }
                    }
                }

                if (closestUpcomingLecture != null) {
                    nextLecture.postValue(closestUpcomingLecture + " " + closestTime);
                } else if (firstLectureName != null) {
                    nextLecture.postValue(firstLectureName + " " + firstLectureTime + " (Bugünün İlk Dersi)");
                } else {
                    nextLecture.postValue("Bugün ders yok");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                nextLecture.postValue("Ders bilgisi alınamadı");
            }
        });
    }

    private void setupExamListener() {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference examsRef = FirebaseDatabase.getInstance().getReference("Exams").child(userId);

        examsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<Exam> upcomingExams = new ArrayList<>();
                String todayStr = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());

                for (DataSnapshot examSnapshot : snapshot.getChildren()) {
                    Exam exam = examSnapshot.getValue(Exam.class);
                    if (exam != null && exam.getDate() != null && exam.getDate().compareTo(todayStr) >= 0) {
                        exam.setKey(examSnapshot.getKey());
                        upcomingExams.add(exam);
                    }
                }

                // Tarihe göre sırala (en yakın tarih en başta olacak)
                upcomingExams.sort((e1, e2) -> e1.getDate().compareTo(e2.getDate()));

                if (!upcomingExams.isEmpty()) {
                    closestExam.postValue(upcomingExams.get(0));
                } else {
                    closestExam.postValue(null);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                closestExam.postValue(null);
            }
        });
    }

    private long convertTimeToMinutes(String time) {
        try {
            String[] parts = time.split(":");
            int hour = Integer.parseInt(parts[0]);
            int minute = Integer.parseInt(parts[1]);
            return hour * 60 + minute;
        } catch (Exception e) {
            return Long.MAX_VALUE;
        }
    }

    private void loadTodayMealsFromJson() {
        try {
            AssetManager assetManager = getApplication().getAssets();
            InputStream is = assetManager.open("yemek_listesi.json");
            byte[] buffer = new byte[is.available()];
            is.read(buffer);
            is.close();

            String jsonStr = new String(buffer, StandardCharsets.UTF_8);
            JSONArray jsonArray = new JSONArray(jsonStr);

            String currentDate = todayDate.getValue();

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject obj = jsonArray.getJSONObject(i);
                if (obj.getString("tarih").equals(currentDate)) {
                    List<MenuItem> meals = new ArrayList<>();

                    meals.add(new MenuItem(obj.getString("corba"), 0));
                    meals.add(new MenuItem(obj.getString("ana_yemek"), 0));
                    meals.add(new MenuItem(obj.getString("alternatif_yemek"), 0));
                    meals.add(new MenuItem(obj.getString("yardimci_yemek"), 0));
                    meals.add(new MenuItem(obj.getString("tatli"), 0));

                    todayMeals.setValue(meals);
                    totalCalories.setValue(obj.optInt("kalori", 0));
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            totalCalories.setValue(0);
            todayMeals.setValue(new ArrayList<>());
        }
    }
}