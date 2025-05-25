package com.example.universe.ui.course_schedule;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class CourseScheduleViewModel extends ViewModel {

    private final MutableLiveData<List<Course>> coursesLiveData = new MutableLiveData<>(new ArrayList<>());
    private final MutableLiveData<String> selectedDayLiveData = new MutableLiveData<>("Pazartesi");
    private final MediatorLiveData<List<Course>> filteredCoursesLiveData = new MediatorLiveData<>();

    private DatabaseReference coursesRef;
    private String userId;

    public CourseScheduleViewModel() {
        try {
            userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
            coursesRef = FirebaseDatabase.getInstance().getReference("Courses").child(userId);

            filteredCoursesLiveData.addSource(coursesLiveData, courses -> filterCourses());
            filteredCoursesLiveData.addSource(selectedDayLiveData, day -> filterCourses());

            loadCoursesFromFirebase();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadCoursesFromFirebase() {
        coursesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                try {
                    List<Course> courses = new ArrayList<>();
                    for (DataSnapshot courseSnapshot : snapshot.getChildren()) {
                        Course course = courseSnapshot.getValue(Course.class);
                        if (course != null) {
                            courses.add(course);
                        }
                    }
                    coursesLiveData.postValue(courses);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                error.toException().printStackTrace();
            }
        });
    }

    private void filterCourses() {
        try {
            List<Course> all = coursesLiveData.getValue();
            String selectedDay = selectedDayLiveData.getValue();
            if (all == null || selectedDay == null) return;

            List<Course> filtered = new ArrayList<>();
            for (Course course : all) {
                if (course != null && course.getDay() != null && course.getDay().equals(selectedDay)) {
                    filtered.add(course);
                }
            }

            // Başlangıç saatine göre sıralama
            filtered.sort((c1, c2) -> {
                try {
                    return parseTime(c1.getStartTime()).compareTo(parseTime(c2.getStartTime()));
                } catch (Exception e) {
                    return 0; // Hatalı zaman varsa sıralamayı bozmamak için eşit say
                }
            });

            filteredCoursesLiveData.postValue(filtered);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void addCourse(Course course) {
        try {
            if (course != null && coursesRef != null) {
                coursesRef.push().setValue(course)
                        .addOnFailureListener(e -> e.printStackTrace());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void removeCourse(Course course) {
        if (course == null || coursesRef == null) return;

        coursesRef.orderByChild("name").equalTo(course.getName())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot childSnapshot : snapshot.getChildren()) {
                            Course c = childSnapshot.getValue(Course.class);
                            if (c != null
                                    && c.getDay().equals(course.getDay())
                                    && c.getStartTime().equals(course.getStartTime())
                                    && c.getEndTime().equals(course.getEndTime())) {
                                childSnapshot.getRef().removeValue();
                                break;
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        error.toException().printStackTrace();
                    }
                });
    }

    private LocalTime parseTime(String timeStr) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
        return LocalTime.parse(timeStr, formatter);
    }

    public void setSelectedDay(String day) {
        selectedDayLiveData.postValue(day);
    }

    public LiveData<String> getSelectedDay() {
        return selectedDayLiveData;
    }

    public LiveData<List<Course>> getFilteredCourses() {
        return filteredCoursesLiveData;
    }
}
