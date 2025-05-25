package com.example.universe.ui.register;

import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import androidx.lifecycle.ViewModel;

import com.example.universe.AuthActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class RegisterViewModel extends ViewModel {

    private final FirebaseAuth auth = FirebaseAuth.getInstance();
    private final DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("Users");

    public void registerUser(String name, String surname, String studentId, String email, String password, Context context) {
        // Firebase Authentication işlemi
        auth.createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener(authResult -> {
                    String uid = authResult.getUser().getUid();

                    // Kullanıcı verilerini hazırlama
                    Map<String, Object> userMap = new HashMap<>();
                    userMap.put("name", name);
                    userMap.put("surname", surname);
                    userMap.put("studentId", studentId);
                    userMap.put("email", email);

                    // Firebase Realtime Database'e veri ekleme
                    usersRef.child(uid).setValue(userMap)
                            .addOnSuccessListener(aVoid -> {
                                Toast.makeText(context, "Kayıt başarılı!", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(context, AuthActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                context.startActivity(intent);
                            })
                            .addOnFailureListener(e -> {
                                Toast.makeText(context, "Veri kaydı başarısız: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            });
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(context, "Kayıt başarısız: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}
