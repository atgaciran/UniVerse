package com.example.universe.ui.profile;

import android.os.Bundle;
import android.transition.ChangeBounds;
import android.transition.Fade;
import android.transition.TransitionManager;
import android.transition.TransitionSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.universe.R;
import com.example.universe.databinding.FragmentProfileBinding;
import com.example.universe.ui.BaseFragment;

public class ProfileFragment extends Fragment {

    private FragmentProfileBinding binding;
    private boolean isUserInfoExpanded = false;
    private boolean isPasswordSectionExpanded = false;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        ProfileViewModel profileViewModel =
                new ViewModelProvider(this).get(ProfileViewModel.class);

        binding = FragmentProfileBinding.inflate(inflater, container, false);
        View root = binding.getRoot();


        // Başlangıçta her şeyi gizle
        hideUserDetails();
        hidePasswordSection();

        // Kullanıcı bilgileri aç/kapa animasyonu
        binding.userDown.setOnClickListener(v -> {
            TransitionManager.beginDelayedTransition((ViewGroup) binding.getRoot(), createTransition());
            if (isUserInfoExpanded) {
                hideUserDetails();
                binding.userDown.setImageResource(R.drawable.down);
            } else {
                showUserDetails();
                binding.userDown.setImageResource(R.drawable.up);
            }
            isUserInfoExpanded = !isUserInfoExpanded;
        });

        // Şifre değiştirme kutusu aç/kapa animasyonu
        binding.passwordDown.setOnClickListener(v -> {
            TransitionManager.beginDelayedTransition((ViewGroup) binding.getRoot(), createTransition());
            if (isPasswordSectionExpanded) {
                hidePasswordSection();
                binding.passwordDown.setImageResource(R.drawable.down);
            } else {
                showPasswordSection();
                binding.passwordDown.setImageResource(R.drawable.up);
            }
            isPasswordSectionExpanded = !isPasswordSectionExpanded;
        });

        profileViewModel.getName().observe(getViewLifecycleOwner(), name -> {
            binding.textDatabaseName.setText(name);
        });

        profileViewModel.getSurname().observe(getViewLifecycleOwner(), surname -> {
            binding.textDatabaseSurname.setText(surname);
        });

        profileViewModel.getStudentId().observe(getViewLifecycleOwner(), studentId -> {
            binding.textDatabaseStudentId.setText(studentId);
        });

        profileViewModel.getEmail().observe(getViewLifecycleOwner(), email -> {
            binding.textDatabaseEmail.setText(email);
        });

// Firebase'den verileri çek
        profileViewModel.loadUserDataFromFirebase();

        // ... ProfileFragment.java içinde diğer kodların arasında onCreateView'in sonunda aşağıyı EKLE:

        binding.resetPassword.setOnClickListener(v -> {
            String emailInput = binding.edittextEmail.getText().toString().trim();
            String oldPassword = binding.edittextOldpassword.getText().toString().trim();
            String newPassword = binding.edittextNewpassword.getText().toString().trim();

            if (emailInput.isEmpty() || oldPassword.isEmpty() || newPassword.isEmpty()) {
                Toast.makeText(requireContext(), "Lütfen tüm alanları doldurun.", Toast.LENGTH_SHORT).show();
                return;
            }

            profileViewModel.changePassword(emailInput, oldPassword, newPassword);
        });

        profileViewModel.getPasswordChangeStatus().observe(getViewLifecycleOwner(), status -> {
            Toast.makeText(requireContext(), status, Toast.LENGTH_LONG).show();
        });


        return root;

    }

    private TransitionSet createTransition() {
        TransitionSet set = new TransitionSet();
        set.setOrdering(TransitionSet.ORDERING_TOGETHER);
        set.addTransition(new ChangeBounds());
        set.addTransition(new Fade(Fade.IN));
        set.addTransition(new Fade(Fade.OUT));
        set.setDuration(300); // Geçiş süresi (ms)
        return set;
    }

    private void hideUserDetails() {
        binding.textName.setVisibility(View.GONE);
        binding.textDatabaseName.setVisibility(View.GONE);
        binding.textSurname.setVisibility(View.GONE);
        binding.textDatabaseSurname.setVisibility(View.GONE);
        binding.textStudentId.setVisibility(View.GONE);
        binding.textDatabaseStudentId.setVisibility(View.GONE);
        binding.textEmail.setVisibility(View.GONE);
        binding.textDatabaseEmail.setVisibility(View.GONE);
    }

    private void showUserDetails() {
        binding.textName.setVisibility(View.VISIBLE);
        binding.textDatabaseName.setVisibility(View.VISIBLE);
        binding.textSurname.setVisibility(View.VISIBLE);
        binding.textDatabaseSurname.setVisibility(View.VISIBLE);
        binding.textStudentId.setVisibility(View.VISIBLE);
        binding.textDatabaseStudentId.setVisibility(View.VISIBLE);
        binding.textEmail.setVisibility(View.VISIBLE);
        binding.textDatabaseEmail.setVisibility(View.VISIBLE);
    }

    private void hidePasswordSection() {
        binding.edittextEmail.setVisibility(View.GONE);
        binding.edittextOldpassword.setVisibility(View.GONE);
        binding.edittextNewpassword.setVisibility(View.GONE);
        binding.resetPassword.setVisibility(View.GONE);
    }

    private void showPasswordSection() {
        binding.edittextEmail.setVisibility(View.VISIBLE);
        binding.edittextOldpassword.setVisibility(View.VISIBLE);
        binding.edittextNewpassword.setVisibility(View.VISIBLE);
        binding.resetPassword.setVisibility(View.VISIBLE);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
