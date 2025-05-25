package com.example.universe.ui.login;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.universe.AuthActivity;
import com.example.universe.R;
import com.example.universe.ui.BaseFragment;

public class LoginFragment extends BaseFragment {

    private EditText emailInput, passwordInput;
    private Button loginButton;
    private CheckBox rememberMe;
    private Spinner universitySpinner;
    private LoginViewModel loginViewModel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);

        // View Binding
        emailInput = view.findViewById(R.id.email_input);
        passwordInput = view.findViewById(R.id.password_input);
        rememberMe = view.findViewById(R.id.remember_me);
        loginButton = view.findViewById(R.id.login_button);
        universitySpinner = view.findViewById(R.id.university_spinner);

        // ViewModel Initialization
        loginViewModel = new ViewModelProvider(this).get(LoginViewModel.class);

        // Spinner setup
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(),
                android.R.layout.simple_spinner_item,
                new String[]{"Üniversite Seçiniz", "Gazi Üniversitesi"});
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        universitySpinner.setAdapter(adapter);

        // Login button click
        loginButton.setOnClickListener(v -> {
            String email = emailInput.getText().toString().trim();
            String password = passwordInput.getText().toString().trim();
            String selectedUniversity = universitySpinner.getSelectedItem().toString();
            boolean rememberMeChecked = rememberMe.isChecked(); // <-- checkbox kontrolü

            if (selectedUniversity.equals("Üniversite Seçiniz")) {
                Toast.makeText(getContext(), "Lütfen bir üniversite seçiniz.", Toast.LENGTH_SHORT).show();
                return;
            }

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(getContext(), "E-posta ve şifre boş bırakılamaz!", Toast.LENGTH_SHORT).show();
                return;
            }

            loginViewModel.loginUser(email, password, selectedUniversity, rememberMeChecked, getContext());
        });

        // Navigation links
        TextView registerLink = view.findViewById(R.id.register_link);
        registerLink.setOnClickListener(v -> ((AuthActivity) getActivity()).navigateToRegister());

        TextView forgotPasswordLink = view.findViewById(R.id.forgot_password_link);
        forgotPasswordLink.setOnClickListener(v -> ((AuthActivity) getActivity()).navigateToForgotPassword());

        return view;
    }
}
