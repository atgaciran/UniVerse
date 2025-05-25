package com.example.universe.ui.exam_schedule;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.*;

import java.text.SimpleDateFormat;
import java.util.*;

public class ExamScheduleViewModel extends ViewModel {

    private final MutableLiveData<List<Exam>> examList = new MutableLiveData<>();
    private DatabaseReference examsRef;
    private String userId;

    public ExamScheduleViewModel() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            userId = user.getUid();
            examsRef = FirebaseDatabase.getInstance().getReference("Exams").child(userId);
            loadExamsFromFirebase();
        } else {
            examList.setValue(new ArrayList<>());
        }
    }

    public LiveData<List<Exam>> getExamList() {
        return examList;
    }

    public void addExam(Exam exam) {
        if (examsRef != null) {
            String examId = examsRef.push().getKey();
            if (examId != null) {
                exam.setKey(examId); // Anahtar ekleniyor
                examsRef.child(examId).setValue(exam)
                        .addOnSuccessListener(aVoid -> {})
                        .addOnFailureListener(Throwable::printStackTrace);
            }
        }
    }

    private void loadExamsFromFirebase() {
        examsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<Exam> exams = new ArrayList<>();
                String todayStr = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());

                for (DataSnapshot examSnapshot : snapshot.getChildren()) {
                    Exam exam = examSnapshot.getValue(Exam.class);
                    if (exam != null && exam.getDate() != null && exam.getDate().compareTo(todayStr) >= 0) {
                        exam.setKey(examSnapshot.getKey()); // Anahtar setleniyor
                        exams.add(exam);
                    }
                }

                exams.sort(Comparator.comparing(Exam::getDate));
                examList.postValue(exams);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                examList.postValue(new ArrayList<>());
            }
        });
    }
}
