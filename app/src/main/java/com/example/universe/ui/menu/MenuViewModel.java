package com.example.universe.ui.menu;

import android.content.Context;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;

public class MenuViewModel extends ViewModel {

    private final MutableLiveData<List<MenuItem>> _menuItems = new MutableLiveData<>(new ArrayList<>());
    public LiveData<List<MenuItem>> getMealList() {
        return _menuItems;
    }

    private final MutableLiveData<String> _totalCaloriesText = new MutableLiveData<>("");
    public LiveData<String> getTotalCaloriesText() {
        return _totalCaloriesText;
    }

    private final MutableLiveData<String> _dateText = new MutableLiveData<>();
    public LiveData<String> getDateText() {
        return _dateText;
    }

    private LocalDate currentDate = LocalDate.now();
    private Context appContext;

    public void loadMenuForToday(Context context) {
        this.appContext = context.getApplicationContext();
        currentDate = LocalDate.now();
        loadMenuForDate(currentDate);
    }

    public void nextDay() {
        currentDate = currentDate.plusDays(1);
        loadMenuForDate(currentDate);
    }

    public void previousDay() {
        currentDate = currentDate.minusDays(1);
        loadMenuForDate(currentDate);
    }

    private void loadMenuForDate(LocalDate date) {
        _dateText.postValue(date.format(DateTimeFormatter.ofPattern("dd.MM.yyyy")));

        new Thread(() -> {
            List<MenuItem> items = new ArrayList<>();
            if (appContext == null) return;

            String formattedDate = date.format(DateTimeFormatter.ofPattern("d MMMM yyyy EEEE", new Locale("tr", "TR")));
            List<JSONObject> yemekListesi = getMenuFromJson();

            String kaloriMetni = "";

            for (JSONObject yemekObj : yemekListesi) {
                String tarih = yemekObj.optString("tarih", "");

                if (tarih.equals(formattedDate)) {
                    items.add(new MenuItem(yemekObj.optString("corba", ""), yemekObj.optInt("corba_cal", 0)));
                    items.add(new MenuItem(yemekObj.optString("ana_yemek", ""), yemekObj.optInt("ana_yemek_cal", 0)));
                    items.add(new MenuItem(yemekObj.optString("alternatif_yemek", ""), yemekObj.optInt("alternatif_yemek_cal", 0)));
                    items.add(new MenuItem(yemekObj.optString("yardimci_yemek", ""), yemekObj.optInt("yardimci_yemek_cal", 0)));
                    items.add(new MenuItem(yemekObj.optString("tatli", ""), yemekObj.optInt("tatli_cal", 0)));

                    kaloriMetni = yemekObj.optString("kalori", ""); // Örneğin: "1150 (1200)"
                    break;
                }
            }

            _menuItems.postValue(items);

            // Ekrana gösterilecek kalori metnini hazırla
            String kaloriGosterim = kaloriMetni.isEmpty() ? "Bilinmiyor" : kaloriMetni + " kcal";
            _totalCaloriesText.postValue("Toplam Kalori: " + kaloriGosterim);
        }).start();
    }

    private List<JSONObject> getMenuFromJson() {
        List<JSONObject> yemekListesi = new ArrayList<>();
        try {
            InputStream is = appContext.getAssets().open("yemek_listesi.json");
            Scanner scanner = new Scanner(is).useDelimiter("\\A");
            String jsonText = scanner.hasNext() ? scanner.next() : "";
            scanner.close();

            JSONArray jsonArray = new JSONArray(jsonText);
            for (int i = 0; i < jsonArray.length(); i++) {
                yemekListesi.add(jsonArray.getJSONObject(i));
            }

            Log.d("JsonCheck", "JSON içeriği: " + jsonText);
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("JsonCheck", "JSON yüklenemedi!");
        }
        return yemekListesi;
    }
}
