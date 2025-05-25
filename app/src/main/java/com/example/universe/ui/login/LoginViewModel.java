package com.example.universe.ui.login;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.widget.Toast;

import androidx.lifecycle.ViewModel;

import com.example.universe.AuthActivity;
import com.example.universe.MainActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class LoginViewModel extends ViewModel {

    private FirebaseAuth auth;
    private DatabaseReference usersRef;
    private SharedPreferences sharedPreferences;

    public LoginViewModel() {
        auth = FirebaseAuth.getInstance();
        usersRef = FirebaseDatabase.getInstance().getReference("Users");
    }

    public void loginUser(String email, String password, String selectedUniversity, boolean rememberMeChecked, Context context) {
        auth.signInWithEmailAndPassword(email, password)
                .addOnSuccessListener(authResult -> {
                    String userId = auth.getCurrentUser().getUid();
                    usersRef.child(userId).get().addOnSuccessListener(dataSnapshot -> {
                        if (dataSnapshot.exists()) {
                            String storedUniversity = dataSnapshot.child("university").getValue(String.class);

                            if (sharedPreferences == null) {
                                sharedPreferences = context.getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
                            }

                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putBoolean("remember_me", rememberMeChecked); // artık checkbox durumuna göre
                            if (rememberMeChecked) {
                                editor.putString("email", email);
                                editor.putString("password", password);
                            } else {
                                editor.remove("email");
                                editor.remove("password");
                            }
                            editor.apply();

                            Toast.makeText(context, "Giriş başarılı!", Toast.LENGTH_SHORT).show();
                            context.startActivity(new Intent(context, MainActivity.class));

                            if (context instanceof AuthActivity) {
                                ((AuthActivity) context).finish();
                            }
                        }
                    });
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(context, "Giriş başarısız: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}
