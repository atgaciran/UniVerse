package com.example.universe;

import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.widget.VideoView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.universe.ui.forgot_password.ForgotPasswordFragment;
import com.example.universe.ui.login.LoginFragment;
import com.example.universe.ui.register.RegisterFragment;
import com.google.firebase.auth.FirebaseAuth;

public class AuthActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    private VideoView backgroundVideo;
    private int currentVideoPosition = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);

        firebaseAuth = FirebaseAuth.getInstance();

        // VideoView kurulumu
        backgroundVideo = findViewById(R.id.background_video);
        Uri videoUri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.midboxbg);
        backgroundVideo.setVideoURI(videoUri);
        backgroundVideo.setOnPreparedListener(mp -> {
            mp.setVolume(0f, 0f); // 🔇 Sesi kapatıyoruz, 2. sorun için
            mp.setLooping(true);
            backgroundVideo.seekTo(currentVideoPosition); // Pozisyon varsa oraya git
            backgroundVideo.start();
        });

        backgroundVideo.setOnCompletionListener(mp -> backgroundVideo.start());

        // SharedPreferences ile kontrol
        SharedPreferences prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        boolean rememberMe = prefs.getBoolean("remember_me", false);

        if (firebaseAuth.getCurrentUser() != null && rememberMe) {
            startActivity(new Intent(AuthActivity.this, MainActivity.class));
            finish();
        } else {
            loadFragment(new LoginFragment());
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (backgroundVideo != null && backgroundVideo.isPlaying()) {
            currentVideoPosition = backgroundVideo.getCurrentPosition();
            backgroundVideo.pause();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (backgroundVideo != null) {
            backgroundVideo.seekTo(currentVideoPosition);
            backgroundVideo.start();
        }
    }

    private void loadFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.auth_fragment_container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    public void navigateToRegister() {
        loadFragment(new RegisterFragment());
    }

    public void navigateToForgotPassword() {
        loadFragment(new ForgotPasswordFragment());
    }

    public void navigateToLogin() {
        loadFragment(new LoginFragment());
    }
}
