package com.example.universe.ui.profile;

import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ProfileViewModel extends ViewModel {

    private final MutableLiveData<String> name = new MutableLiveData<>();
    private final MutableLiveData<String> surname = new MutableLiveData<>();
    private final MutableLiveData<String> studentId = new MutableLiveData<>();
    private final MutableLiveData<String> email = new MutableLiveData<>();
    private final MutableLiveData<String> passwordChangeStatus = new MutableLiveData<>();

    public LiveData<String> getName() {
        return name;
    }

    public LiveData<String> getSurname() {
        return surname;
    }

    public LiveData<String> getStudentId() {
        return studentId;
    }

    public LiveData<String> getEmail() {
        return email;
    }

    public LiveData<String> getPasswordChangeStatus() {
        return passwordChangeStatus;
    }

    public void loadUserDataFromFirebase() {
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("Users").child(uid);

        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    name.setValue(snapshot.child("name").getValue(String.class));
                    surname.setValue(snapshot.child("surname").getValue(String.class));
                    studentId.setValue(snapshot.child("studentId").getValue(String.class));
                    email.setValue(snapshot.child("email").getValue(String.class));
                } else {
                    Log.e("ProfileViewModel", "Kullanıcı verisi bulunamadı.");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("ProfileViewModel", "Veri çekme iptal edildi: " + error.getMessage());
            }
        });
    }

    public void changePassword(String emailInput, String oldPassword, String newPassword) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user == null || emailInput == null || oldPassword == null || newPassword == null) {
            passwordChangeStatus.setValue("Lütfen tüm alanları doldurun.");
            return;
        }

        AuthCredential credential = EmailAuthProvider.getCredential(emailInput, oldPassword);

        user.reauthenticate(credential).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                user.updatePassword(newPassword).addOnCompleteListener(updateTask -> {
                    if (updateTask.isSuccessful()) {
                        passwordChangeStatus.setValue("Şifre başarıyla güncellendi.");
                    } else {
                        passwordChangeStatus.setValue("Şifre güncellenirken hata oluştu: " + updateTask.getException().getMessage());
                    }
                });
            } else {
                passwordChangeStatus.setValue("Kimlik doğrulama başarısız: " + task.getException().getMessage());
            }
        });
    }
}
