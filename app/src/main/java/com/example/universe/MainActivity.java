package com.example.universe;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.navigation.NavController;
import androidx.navigation.NavOptions;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;

import com.example.universe.databinding.ActivityMainBinding;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private Button logoutButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // BottomNavigationView ve özel ImageButton'ı al
        BottomNavigationView navView = findViewById(R.id.nav_view);
        ImageButton home = findViewById(R.id.homepage);  // Gazi logosunun bulunduğu ImageButton
        ImageButton logout = findViewById(R.id.logout_button);
        ImageButton profile = findViewById(R.id.profile_button);

        // Başlangıçta orta buton (home) seçili olmalı
        navView.setSelectedItemId(R.id.navigation_home);

        // BottomNavigationView item'larının renklerini belirle
        ColorStateList colorStateList = new ColorStateList(
                new int[][]{
                        new int[]{android.R.attr.state_checked}, // Seçili sekme
                        new int[]{-android.R.attr.state_checked} // Diğerleri
                },
                new int[]{
                        Color.parseColor("#00205B"), // Seçili sekmenin rengi (örnek: mavi)
                        Color.parseColor("#808080")  // Diğer sekmelerin rengi (gri)
                }
        );
        navView.setItemIconTintList(colorStateList);
        navView.setItemTextColor(colorStateList);

        // NavController tanımla
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);

        // Geri tuşu davranışı için üst seviye destinasyonları tanımla
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_material_chat,
                R.id.navigation_exam_schedule,
                R.id.navigation_home,
                R.id.navigation_course_schedule,
                R.id.navigation_menu
        ).build();

        // Ana sayfa (orta Gazi butonu) için tıklama
        home.setOnClickListener(v -> {
            // BottomNavigationView'deki seçili item'ı kaldır
            navView.getMenu().setGroupCheckable(0, true, false);
            for (int i = 0; i < navView.getMenu().size(); i++) {
                navView.getMenu().getItem(i).setChecked(false);
            }
            navView.getMenu().setGroupCheckable(0, true, true);

            // Ana sayfaya yönlendirme, animasyonları kapat
            if (navController.getCurrentDestination().getId() != R.id.navigation_home) {
                NavOptions navOptions = new NavOptions.Builder()
                        .setEnterAnim(0)
                        .setExitAnim(0)
                        .setPopEnterAnim(0)
                        .setPopExitAnim(0)
                        .build();
                navController.navigate(R.id.navigation_home, null, navOptions);
            }
        });

        // Logout butonu
        logout.setOnClickListener(v -> {
            new androidx.appcompat.app.AlertDialog.Builder(MainActivity.this)
                    .setTitle("Çıkış Yap")
                    .setCancelable(false)
                    .setMessage("Çıkış yapmak istediğinize emin misiniz?")
                    .setPositiveButton("Evet", (dialog, which) -> {
                        // Çıkış işlemleri
                        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.clear();
                        editor.apply();

                        Intent intent = new Intent(MainActivity.this, AuthActivity.class);
                        startActivity(intent);
                        finish();
                    })
                    .setNegativeButton("Hayır", (dialog, which) -> dialog.dismiss())
                    .show();
        });

        profile.setOnClickListener(v -> {
            NavOptions navOptions = new NavOptions.Builder()
                    .setEnterAnim(0)
                    .setExitAnim(0)
                    .setPopEnterAnim(0)
                    .setPopExitAnim(0)
                    .build();

            if (navController.getCurrentDestination().getId() != R.id.navigation_profile) {
                navController.navigate(R.id.navigation_profile, null, navOptions);
            }
        });

        // BottomNavigationView ile animasyonsuz geçiş
        navView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            int currentDestination = navController.getCurrentDestination().getId();

            // Navigasyon animasyonlarını kapat
            NavOptions navOptions = new NavOptions.Builder()
                    .setEnterAnim(0)
                    .setExitAnim(0)
                    .setPopEnterAnim(0)
                    .setPopExitAnim(0)
                    .build();

            if (itemId == R.id.navigation_home && currentDestination != R.id.navigation_home) {
                navController.navigate(R.id.navigation_home, null, navOptions);
            } else if (itemId == R.id.navigation_exam_schedule && currentDestination != R.id.navigation_exam_schedule) {
                navController.navigate(R.id.navigation_exam_schedule, null, navOptions);
            } else if (itemId == R.id.navigation_course_schedule && currentDestination != R.id.navigation_course_schedule) {
                navController.navigate(R.id.navigation_course_schedule, null, navOptions);
            } else if (itemId == R.id.navigation_material_chat && currentDestination != R.id.navigation_material_chat) {
                navController.navigate(R.id.navigation_material_chat, null, navOptions);
            } else if (itemId == R.id.navigation_menu && currentDestination != R.id.navigation_menu) {
                navController.navigate(R.id.navigation_menu, null, navOptions);
            }

            return true;
        });

        // Karanlık mod devre dışı
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
    }
}
