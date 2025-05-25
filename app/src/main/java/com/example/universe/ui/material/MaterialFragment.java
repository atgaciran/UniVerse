package com.example.universe.ui.material;

import android.app.AlertDialog;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.universe.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class MaterialFragment extends Fragment {

    private Button lectureNotesButton, examQuestionsButton;
    private FloatingActionButton fabMain;
    private MaterialViewModel viewModel;
    private MaterialAdapter adapter;

    private Button selectedButton = null;
    private String currentCategoryKey = null;
    private EditText currentFileEditText = null;

    private ActivityResultLauncher<String[]> filePickerLauncher;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_material, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        lectureNotesButton = view.findViewById(R.id.lecture_notes);
        examQuestionsButton = view.findViewById(R.id.exam_questions);
        fabMain = view.findViewById(R.id.fab_main);

        RecyclerView recyclerView = view.findViewById(R.id.materialRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new MaterialAdapter();
        recyclerView.setAdapter(adapter);

        viewModel = new ViewModelProvider(this).get(MaterialViewModel.class);

        lectureNotesButton.setOnClickListener(v -> {
            setSelectedCategory(lectureNotesButton);
            currentCategoryKey = "ders_notlari";
            viewModel.loadMaterials(currentCategoryKey);
        });

        examQuestionsButton.setOnClickListener(v -> {
            setSelectedCategory(examQuestionsButton);
            currentCategoryKey = "cikmis_sorular";
            viewModel.loadMaterials(currentCategoryKey);
        });

        viewModel.getMaterialList().observe(getViewLifecycleOwner(), items -> {
            adapter.updateList(items);
        });

        fabMain.setOnClickListener(v -> openAddMaterialDialog());

        filePickerLauncher = registerForActivityResult(new ActivityResultContracts.OpenDocument(), uri -> {
            if (uri != null && currentFileEditText != null) {
                currentFileEditText.setText(uri.toString());
            }
        });
    }

    private void setSelectedCategory(Button clickedButton) {
        if (selectedButton != null) {
            selectedButton.setBackgroundColor(0xFFFFFFFF); // Beyaz
            selectedButton.setTextColor(0xFF000000); // Siyah
        }
        if (selectedButton == clickedButton) {
            selectedButton = null;
            currentCategoryKey = null;
        } else {
            selectedButton = clickedButton;
            selectedButton.setBackgroundColor(0xFF101A38); // #101a38
            selectedButton.setTextColor(0xFFFFFFFF); // Beyaz
        }
    }

    private void openAddMaterialDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        View dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_add_material, null);

        // Görünümler
        RadioGroup categoryGroup = dialogView.findViewById(R.id.category_radio_group);
        RadioGroup typeGroup = dialogView.findViewById(R.id.type_radio_group);
        EditText titleEditText = dialogView.findViewById(R.id.title_edit_text);
        EditText linkEditText = dialogView.findViewById(R.id.link_edit_text);
        EditText filePathEditText = dialogView.findViewById(R.id.file_or_image_file_path);
        Button fileOrImageButton = dialogView.findViewById(R.id.file_or_image_button);

        // Başlangıçta gizli olanlar
        linkEditText.setVisibility(View.GONE);
        filePathEditText.setVisibility(View.GONE);
        fileOrImageButton.setVisibility(View.GONE);

        final String[] selectedType = {null};
        final String[] selectedCategoryKey = {null};

        // Kategori seçimi
        categoryGroup.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.type_lecture_notes) {
                selectedCategoryKey[0] = "lecture_notes";
            } else if (checkedId == R.id.type_exam_questions) {
                selectedCategoryKey[0] = "exam_questions";
            }
        });

        // Tür seçimi
        typeGroup.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.type_link) {
                selectedType[0] = "link";
                linkEditText.setVisibility(View.VISIBLE);
                filePathEditText.setVisibility(View.GONE);
                fileOrImageButton.setVisibility(View.GONE);
            } else {
                linkEditText.setVisibility(View.GONE);
                filePathEditText.setVisibility(View.VISIBLE);
                fileOrImageButton.setVisibility(View.VISIBLE);
                selectedType[0] = (checkedId == R.id.type_photo) ? "fotoğraf" : "dosya";
            }
        });

        // Dosya/Fotograf seçimi
        fileOrImageButton.setOnClickListener(v -> {
            currentFileEditText = filePathEditText;
            filePickerLauncher.launch(new String[]{"*/*"});
        });

        builder.setView(dialogView)
                .setTitle("Yeni Materyal Ekle")
                .setPositiveButton("Ekle", (dialog, which) -> {
                    String title = titleEditText.getText().toString().trim();
                    String type = selectedType[0];
                    String categoryKey = selectedCategoryKey[0];
                    String fileUrl;

                    if (type == null || categoryKey == null || title.isEmpty()) {
                        Toast.makeText(requireContext(), "Lütfen tüm alanları doldurun", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if ("link".equals(type)) {
                        fileUrl = linkEditText.getText().toString().trim();
                    } else {
                        fileUrl = filePathEditText.getText().toString().trim();
                    }

                    if (fileUrl.isEmpty()) {
                        Toast.makeText(requireContext(), "Dosya veya bağlantı boş olamaz", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    MaterialItem newItem = new MaterialItem(title, "", type, fileUrl);

                    viewModel.addMaterial(categoryKey, newItem,
                            () -> Toast.makeText(requireContext(), "Materyal eklendi", Toast.LENGTH_SHORT).show(),
                            () -> Toast.makeText(requireContext(), "Ekleme başarısız", Toast.LENGTH_SHORT).show()
                    );
                })
                .setNegativeButton("İptal", (dialog, which) -> dialog.dismiss())
                .show();
    }


}
