package com.example.universe.ui.register;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.universe.AuthActivity;
import com.example.universe.R;
import com.example.universe.ui.BaseFragment;

public class RegisterFragment extends BaseFragment {

    private EditText nameEditText, surnameEditText, emailEditText, studentIdEditText, passwordEditText, confirmPasswordEditText;
    private Button registerButton;
    private TextView loginTextView;
    private RegisterViewModel viewModel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_register, container, false);

        // View Binding
        nameEditText = view.findViewById(R.id.nameEditText);
        surnameEditText = view.findViewById(R.id.surnameEditText);
        emailEditText = view.findViewById(R.id.emailEditText);
        studentIdEditText = view.findViewById(R.id.studentIdEditText);
        passwordEditText = view.findViewById(R.id.passwordEditText);
        confirmPasswordEditText = view.findViewById(R.id.confirmPasswordEditText);
        registerButton = view.findViewById(R.id.registerButton);
        loginTextView = view.findViewById(R.id.loginTextView);

        // ViewModel Initialization
        viewModel = new ViewModelProvider(this).get(RegisterViewModel.class);

        // Register Button OnClickListener
        registerButton.setOnClickListener(v -> {
            String name = nameEditText.getText().toString().trim();
            String surname = surnameEditText.getText().toString().trim();
            String email = emailEditText.getText().toString().trim();
            String studentId = studentIdEditText.getText().toString().trim();
            String password = passwordEditText.getText().toString().trim();
            String confirmPassword = confirmPasswordEditText.getText().toString().trim();

            if (email.isEmpty() || name.isEmpty() || surname.isEmpty() || studentId.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                Toast.makeText(getActivity(), "Lütfen tüm alanları doldurun.", Toast.LENGTH_SHORT).show();
            } else if (!password.equals(confirmPassword)) {
                Toast.makeText(getActivity(), "Şifreler uyuşmuyor.", Toast.LENGTH_SHORT).show();
            } else if (!isValidEmail(email)) {
                Toast.makeText(getActivity(), "Geçerli bir Gazi e-posta adresi girin. (@gazi.edu.tr)", Toast.LENGTH_SHORT).show();
            } else {
                viewModel.registerUser(name, surname, studentId, email, password, getActivity());
            }
        });

        // Login TextView OnClickListener
        loginTextView.setOnClickListener(v -> {
            if (getActivity() instanceof AuthActivity) {
                ((AuthActivity) getActivity()).navigateToLogin();
            }
        });

        return view;
    }

    private boolean isValidEmail(String email) {
        return email.endsWith("@gazi.edu.tr");
    }
}
