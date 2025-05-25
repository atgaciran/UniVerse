package com.example.universe.ui.menu;

import android.content.Context;
import android.util.Log;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;

public class HtmlMenuParser {

    public static List<String> getYemekMenusu(Context context, String tarih) {
        List<String> yemekler = new ArrayList<>();

        try {
            InputStream is = context.getAssets().open("yemek.html");
            Scanner scanner = new Scanner(is).useDelimiter("\\A");
            String html = scanner.hasNext() ? scanner.next() : "";
            scanner.close();

            Document doc = Jsoup.parse(html);

            // Yemek tablosunu bul (3. tablo)
            Element table = doc.select("table").get(2);
            Elements rows = table.select("tr");

            if (rows.isEmpty()) return yemekler;

            // İlk satır: Tarihler
            Elements headerCells = rows.get(0).select("td");

            int startIndex = -1;
            int endIndex = -1;

            // Tarihi başlıklarda ara
            for (int i = 0; i < headerCells.size(); i++) {
                String cellText = headerCells.get(i).text().trim();
                if (cellText.equalsIgnoreCase(tarih.trim())) {
                    startIndex = i;
                    break;
                }
            }

            if (startIndex == -1) {
                Log.d("HtmlMenuParser", "Tarih bulunamadı: " + tarih);
                return yemekler;
            }

            // Sonraki dolu tarih hücresi bulunursa endIndex olarak al
            for (int i = startIndex + 1; i < headerCells.size(); i++) {
                String cellText = headerCells.get(i).text().trim();
                if (!cellText.isEmpty()) {
                    endIndex = i;
                    break;
                }
            }

            // Eğer sonraki tarih yoksa, son sütuna kadar devam et
            if (endIndex == -1) endIndex = startIndex + 1;

            // Satır satır ilerleyerek sadece ilgili tarih sütunundaki hücreleri al
            for (int i = 1; i < rows.size(); i++) {
                Elements cells = rows.get(i).select("td");

                if (cells.size() > startIndex) {
                    String text = cells.get(startIndex).text().trim();

                    if (!text.isEmpty() && !text.toLowerCase(Locale.ROOT).contains("kalori")) {
                        yemekler.add(text);
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return yemekler;
    }
}
