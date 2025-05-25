package com.example.universe.ui.forgot_password;

import android.app.Application;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.*;

public class ForgotPasswordViewModel extends AndroidViewModel {

    private static final String TAG = "ForgotPasswordViewModel";
    private final FirebaseAuth mAuth;
    private final DatabaseReference databaseRef;

    public MutableLiveData<String> toastMessage = new MutableLiveData<>();
    public MutableLiveData<Boolean> passwordResetSuccess = new MutableLiveData<>();

    public ForgotPasswordViewModel(@NonNull Application application) {
        super(application);
        mAuth = FirebaseAuth.getInstance();
        databaseRef = FirebaseDatabase.getInstance().getReference("Users");
    }

    public void resetPassword(String email, String studentNumber, String newPassword) {
        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(studentNumber) || TextUtils.isEmpty(newPassword)) {
            toastMessage.setValue("Lütfen tüm alanları doldurun.");
            return;
        }

        if (newPassword.length() < 6) {
            toastMessage.setValue("Yeni şifre en az 6 karakter olmalıdır.");
            return;
        }

        // Öğrenci numarasına göre sorgu (studentId alanını kontrol edecek)
        databaseRef.orderByChild("studentId").equalTo(studentNumber)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (!snapshot.exists()) {
                            toastMessage.setValue("Öğrenci numarası bulunamadı.");
                            return;
                        }

                        // Eşleşen kullanıcıyı bul
                        for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                            String registeredEmail = userSnapshot.child("email").getValue(String.class);

                            if (registeredEmail == null || !registeredEmail.equalsIgnoreCase(email)) {
                                toastMessage.setValue("E-posta, öğrenci numarasıyla eşleşmiyor.");
                                return;
                            }

                            FirebaseUser user = mAuth.getCurrentUser();
                            if (user == null || !user.getEmail().equalsIgnoreCase(email)) {
                                toastMessage.setValue("Şifre değiştirmek için giriş yapmış olmalısınız.");
                                return;
                            }

                            // Şifreyi güncelle
                            user.updatePassword(newPassword)
                                    .addOnCompleteListener(task -> {
                                        if (task.isSuccessful()) {
                                            toastMessage.setValue("Şifre başarıyla değiştirildi.");
                                            passwordResetSuccess.setValue(true);
                                        } else {
                                            toastMessage.setValue("Şifre güncellenemedi: " + getMessage(task.getException()));
                                        }
                                    });
                            return; // İlk eşleşmeden sonra döngüyü bitir
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        toastMessage.setValue("Veritabanı hatası: " + error.getMessage());
                    }
                });
    }

    private String getMessage(Exception e) {
        return e != null ? e.getLocalizedMessage() : "Bilinmeyen hata";
    }
}