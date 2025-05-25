package com.example.universe.ui.course_schedule;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.universe.R;
import com.example.universe.databinding.FragmentCourseScheduleBinding;
import com.google.firebase.auth.FirebaseAuth;

public class CourseScheduleFragment extends Fragment {

    private FragmentCourseScheduleBinding binding;
    private CourseScheduleViewModel viewModel;
    private CourseAdapter adapter;

    private final String[] daysOfWeek = {"Pazartesi", "Salı", "Çarşamba", "Perşembe", "Cuma"};
    private int currentDayIndex = 0;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Kullanıcı giriş yapmış mı kontrolü
        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            // Giriş yapılmamışsa login ekranına yönlendir
            // Bu kısmı kendi login yönlendirme kodunuzla değiştirin
            return inflater.inflate(R.layout.fragment_login, container, false);
        }

        viewModel = new ViewModelProvider(this).get(CourseScheduleViewModel.class);
        binding = FragmentCourseScheduleBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // RecyclerView
        adapter = new CourseAdapter();
        binding.recyclerLectures.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.recyclerLectures.setAdapter(adapter);

        // Silme listener'ı
        adapter.setOnItemDeleteListener(course -> {
            new AlertDialog.Builder(getContext())
                    .setTitle("Dersi Sil")
                    .setMessage("Dersi silmek istediğinize emin misiniz?")
                    .setPositiveButton("Evet", (dialog, which) -> viewModel.removeCourse(course))
                    .setNegativeButton("Hayır", null)
                    .show();
        });

        // Gözlemleme
        viewModel.getFilteredCourses().observe(getViewLifecycleOwner(), adapter::setCourseList);
        viewModel.getSelectedDay().observe(getViewLifecycleOwner(), day -> binding.textDate.setText(day));

        // FAB -> dialog
        binding.fabAddSchedule.setOnClickListener(v -> showAddScheduleDialog());

        // Oklarla gün geçişi
        binding.buttonNextDay.setOnClickListener(v -> {
            currentDayIndex = (currentDayIndex + 1) % daysOfWeek.length;
            viewModel.setSelectedDay(daysOfWeek[currentDayIndex]);
        });

        binding.buttonPrevDay.setOnClickListener(v -> {
            currentDayIndex = (currentDayIndex - 1 + daysOfWeek.length) % daysOfWeek.length;
            viewModel.setSelectedDay(daysOfWeek[currentDayIndex]);
        });

        // Başlangıç günü
        viewModel.setSelectedDay(daysOfWeek[currentDayIndex]);

        return root;
    }

    private void showAddScheduleDialog() {
        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_add_course, null);
        Spinner spinnerDay = dialogView.findViewById(R.id.spinner_day);
        Spinner spinnerStart = dialogView.findViewById(R.id.spinner_start_time);
        Spinner spinnerEnd = dialogView.findViewById(R.id.spinner_end_time);
        EditText editCourseName = dialogView.findViewById(R.id.edit_course_name);
        Button buttonAdd = dialogView.findViewById(R.id.button_add);

        // Spinnerlara veri
        ArrayAdapter<String> dayAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, daysOfWeek);
        dayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerDay.setAdapter(dayAdapter);
        spinnerDay.setSelection(currentDayIndex);

        String[] startHours = {
                "08:30", "09:30", "10:30", "11:30", "12:30", "13:30",
                "14:30", "15:30", "16:30", "17:30", "18:30", "19:30", "20:30"
        };

        String[] endHours = {
                "09:20", "10:20", "11:20", "12:20", "13:20", "14:20",
                "15:20", "16:20", "17:20", "18:20", "19:20", "20:20", "21:20"
        };

        ArrayAdapter<String> startAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, startHours);
        startAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerStart.setAdapter(startAdapter);

        ArrayAdapter<String> endAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, endHours);
        endAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerEnd.setAdapter(endAdapter);

        AlertDialog dialog = new AlertDialog.Builder(getContext())
                .setView(dialogView)
                .create();

        buttonAdd.setOnClickListener(v -> {
            String selectedDay = spinnerDay.getSelectedItem().toString();
            String startTime = spinnerStart.getSelectedItem().toString();
            String endTime = spinnerEnd.getSelectedItem().toString();
            String courseName = editCourseName.getText().toString().trim();

            if (courseName.isEmpty()) {
                editCourseName.setError("Ders adı girin");
                return;
            }

            Course newCourse = new Course(courseName, selectedDay, startTime, endTime);
            viewModel.addCourse(newCourse);

            if (selectedDay.equals(viewModel.getSelectedDay().getValue())) {
            // Aynı gün eklenmişse listeyi güncellemek için
                viewModel.setSelectedDay(selectedDay);
            }
            dialog.dismiss();
        });
        dialog.show();
    }
}
