package com.example.universe.ui.forgot_password;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.universe.AuthActivity;
import com.example.universe.databinding.FragmentForgotPasswordBinding;
import com.example.universe.ui.BaseFragment;

public class ForgotPasswordFragment extends BaseFragment {

    private FragmentForgotPasswordBinding binding;
    private ForgotPasswordViewModel viewModel;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentForgotPasswordBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        viewModel = new ViewModelProvider(this).get(ForgotPasswordViewModel.class);

        binding.resetButton.setOnClickListener(v -> {
            String email = binding.emailInput.getText().toString().trim();
            String studentNumber = binding.studentNumberInput.getText().toString().trim();
            String newPassword = binding.newPasswordInput.getText().toString().trim();

            viewModel.resetPassword(email, studentNumber, newPassword);
        });

        // Back to Login Button (Fragment ile LoginActivity'ye yönlendirme)
        binding.backToLogin.setOnClickListener(v -> {
            if (getActivity() instanceof AuthActivity) {
                ((AuthActivity) getActivity()).navigateToLogin();
            }
        });

        viewModel.toastMessage.observe(getViewLifecycleOwner(), msg -> {
            if (msg != null) {
                Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show();
            }
        });

        viewModel.passwordResetSuccess.observe(getViewLifecycleOwner(), success -> {
            if (success != null && success) {
                if (getActivity() instanceof AuthActivity) {
                    ((AuthActivity) getActivity()).navigateToLogin();
                }
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}