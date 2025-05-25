package com.example.universe.ui.exam_schedule;

import android.app.AlertDialog;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.universe.databinding.FragmentExamScheduleBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class ExamScheduleFragment extends Fragment {

    private FragmentExamScheduleBinding binding;
    private ExamAdapter adapter;
    private ExamScheduleViewModel viewModel;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentExamScheduleBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(this).get(ExamScheduleViewModel.class);

        adapter = new ExamAdapter(new ArrayList<>());
        binding.recyclerExamSchedule.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.recyclerExamSchedule.setAdapter(adapter);

        adapter.setOnDeleteClickListener(this::deleteExam);

        viewModel.getExamList().observe(getViewLifecycleOwner(), exams -> {
            if (exams != null) {
                adapter.updateList(exams);
                if (exams.isEmpty()) {
                    Toast.makeText(requireContext(), "Henüz sınav eklenmedi", Toast.LENGTH_SHORT).show();
                }
            }
        });

        binding.fabAddSchedule.setOnClickListener(v -> showAddExamDialog());
    }

    private void showAddExamDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Sınav Ekle");

        LinearLayout layout = new LinearLayout(requireContext());
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(50, 30, 50, 30);

        final EditText inputName = new EditText(requireContext());
        inputName.setHint("Sınav Adı");
        inputName.setInputType(InputType.TYPE_CLASS_TEXT);
        layout.addView(inputName);

        final EditText inputDate = new EditText(requireContext());
        inputDate.setHint("Tarih (YYYY-AA-GG)");
        inputDate.setInputType(InputType.TYPE_CLASS_DATETIME);
        layout.addView(inputDate);

        builder.setView(layout);

        builder.setPositiveButton("Ekle", (dialog, which) -> {
            String name = inputName.getText().toString().trim();
            String date = inputDate.getText().toString().trim();

            if (name.isEmpty() || date.isEmpty()) {
                Toast.makeText(requireContext(), "Lütfen tüm alanları doldurun", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!date.matches("\\d{4}-\\d{2}-\\d{2}")) {
                Toast.makeText(requireContext(), "Geçersiz tarih formatı (YYYY-AA-GG olmalı)", Toast.LENGTH_SHORT).show();
                return;
            }

            viewModel.addExam(new Exam(name, date));
            Toast.makeText(requireContext(), "Sınav eklendi", Toast.LENGTH_SHORT).show();
        });

        builder.setNegativeButton("İptal", (dialog, which) -> dialog.cancel());

        builder.create().show();
    }

    private void deleteExam(Exam exam) {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        String examKey = exam.getKey();

        if (examKey == null) {
            Toast.makeText(getContext(), "Sınav anahtarı bulunamadı", Toast.LENGTH_SHORT).show();
            return;
        }

        DatabaseReference ref = FirebaseDatabase.getInstance()
                .getReference("Exams")
                .child(userId)
                .child(examKey);

        ref.removeValue().addOnSuccessListener(aVoid -> {
            Toast.makeText(getContext(), "Sınav silindi", Toast.LENGTH_SHORT).show();
        }).addOnFailureListener(e -> {
            Toast.makeText(getContext(), "Silinemedi: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
