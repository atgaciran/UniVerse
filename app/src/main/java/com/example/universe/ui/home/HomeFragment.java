package com.example.universe.ui.home;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.universe.R;
import com.example.universe.ui.menu.MenuAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class HomeFragment extends Fragment {

    private static final String TAG = "HomeFragment";
    private HomeViewModel homeViewModel;
    private MenuAdapter menuAdapter;
    private RatingBar ratingBar;
    private Button sendVoteButton;
    private TextView averageRatingText;
    private TextView upcomingExamText;
    private TextView nextLectureText;
    private DatabaseReference ratingsRef;
    private FirebaseAuth mAuth;
    private String currentDateKey;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        initializeFirebaseComponents();
        bindViews(view);
        setupDate();
        setupRecyclerView(view);
        setupViewModel();
        setupRatingSystem();

        return view;
    }

    private void initializeFirebaseComponents() {
        mAuth = FirebaseAuth.getInstance();
        ratingsRef = FirebaseDatabase.getInstance().getReference("ratings");
    }

    private void bindViews(View view) {
        upcomingExamText = view.findViewById(R.id.upcoming_exam);
        nextLectureText = view.findViewById(R.id.next_lecture);
        ratingBar = view.findViewById(R.id.ratingBar_menu);
        sendVoteButton = view.findViewById(R.id.send_vote);
        averageRatingText = view.findViewById(R.id.text_average_rating);
    }

    private void setupDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        currentDateKey = sdf.format(new Date());
    }

    private void setupRecyclerView(View view) {
        menuAdapter = new MenuAdapter();
        RecyclerView recyclerView = view.findViewById(R.id.recycler_meals);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()) {
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        });
        recyclerView.setAdapter(menuAdapter);
    }

    private void setupViewModel() {
        homeViewModel = new ViewModelProvider(this,
                ViewModelProvider.AndroidViewModelFactory.getInstance(requireActivity().getApplication()))
                .get(HomeViewModel.class);

        // Bugünün tarihini gözle
        homeViewModel.getTodayDate().observe(getViewLifecycleOwner(),
                date -> ((TextView) getView().findViewById(R.id.text_today_date)).setText(date));

        // Yemek listesini gözle
        homeViewModel.getTodayMeals().observe(getViewLifecycleOwner(), menuAdapter::setItems);

        // Toplam kaloriyi gözle
        homeViewModel.getTotalCalories().observe(getViewLifecycleOwner(), kcal -> {
            ((TextView) getView().findViewById(R.id.text_total_calories)).setText("Toplam: " + kcal + " kcal");
        });

        // Sonraki dersi gözle
        homeViewModel.getNextLecture().observe(getViewLifecycleOwner(), lecture -> {
            if (lecture != null && !lecture.isEmpty()) {
                nextLectureText.setText(lecture);
            } else {
                nextLectureText.setText("Bugün ders yok");
            }
        });

        // Yaklaşan sınavı gözle
        homeViewModel.getClosestExam().observe(getViewLifecycleOwner(), exam -> {
            if (exam != null) {
                String displayText = exam.getName() + " - " + exam.getDate();
                upcomingExamText.setText(displayText);
            } else {
                upcomingExamText.setText("Yaklaşan sınav yok");
            }
        });
    }

    private void setupRatingSystem() {
        if (mAuth.getCurrentUser() == null) {
            handleUnauthenticatedUser();
            return;
        }

        String userId = mAuth.getCurrentUser().getUid();
        checkUserPreviousRating(userId);
        setupAverageRatingListener();
        setupRatingButton(userId);
    }

    private void handleUnauthenticatedUser() {
        sendVoteButton.setEnabled(false);
        ratingBar.setEnabled(false);
        Toast.makeText(getContext(), "Puan vermek için giriş yapmalısınız", Toast.LENGTH_LONG).show();
    }

    private void checkUserPreviousRating(String userId) {
        ratingsRef.child(currentDateKey).child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    try {
                        float userRating = snapshot.getValue(Float.class);
                        ratingBar.setRating(userRating);
                        sendVoteButton.setEnabled(false);
                        sendVoteButton.setText("Puan Verildi");
                    } catch (Exception e) {
                        Log.e(TAG, "Puan okunurken hata: ", e);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w(TAG, "Kullanıcı puanı okunamadı: ", error.toException());
            }
        });
    }

    private void setupAverageRatingListener() {
        ratingsRef.child(currentDateKey).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                try {
                    calculateAndDisplayAverageRating(snapshot);
                } catch (Exception e) {
                    Log.e(TAG, "Ortalama puan hesaplanırken hata: ", e);
                    averageRatingText.setText("Ortalama hesaplanamadı");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w(TAG, "Ortalama puan dinleyici iptal edildi: ", error.toException());
                averageRatingText.setText("Veri alınamadı");
            }
        });
    }

    private void setupRatingButton(String userId) {
        ratingsRef.child(currentDateKey).child(userId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            Float rating = snapshot.getValue(Float.class);
                            if (rating != null) {
                                ratingBar.setRating(rating);
                            }
                            ratingBar.setIsIndicator(true);
                            sendVoteButton.setEnabled(false);
                            sendVoteButton.setText("Puan Verildi");
                        } else {
                            sendVoteButton.setEnabled(true);
                            sendVoteButton.setText("Puan Ver");
                            ratingBar.setIsIndicator(false);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        error.toException().printStackTrace();
                    }
                });

        sendVoteButton.setOnClickListener(v -> {
            float rating = ratingBar.getRating();
            if (rating < 1 || rating > 5) {
                Toast.makeText(getContext(), "Lütfen 1 ile 5 arasında puan verin", Toast.LENGTH_SHORT).show();
                return;
            }

            ratingsRef.child(currentDateKey).child(userId).setValue(rating)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(getContext(), "Puanınız kaydedildi", Toast.LENGTH_SHORT).show();
                        sendVoteButton.setEnabled(false);
                        sendVoteButton.setText("Puan Verildi");
                        ratingBar.setIsIndicator(true);
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(getContext(), "Puan kaydedilemedi: " + e.getMessage(), Toast.LENGTH_LONG).show();
                        Log.e(TAG, "Puan kaydedilemedi: ", e);
                    });
        });
    }

    private void calculateAndDisplayAverageRating(DataSnapshot snapshot) {
        if (snapshot.exists() && snapshot.getChildrenCount() > 0) {
            float total = 0;
            int count = 0;

            for (DataSnapshot userRating : snapshot.getChildren()) {
                try {
                    Float rating = userRating.getValue(Float.class);
                    if (rating != null && rating >= 1 && rating <= 5) {
                        total += rating;
                        count++;
                    }
                } catch (Exception e) {
                    Log.w(TAG, "Geçersiz puan verisi atlandı: ", e);
                }
            }

            if (count > 0) {
                float average = total / count;
                averageRatingText.setText(String.format(Locale.getDefault(),
                        "Ortalama: %.1f/5 (%d oy)", average, count));
            } else {
                averageRatingText.setText("Geçerli puan bulunamadı");
            }
        } else {
            averageRatingText.setText("Henüz puan yok");
        }
    }
}