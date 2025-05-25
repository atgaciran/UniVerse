package com.example.universe.ui.material_chat;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.universe.R;
import com.example.universe.ui.BaseFragment;
import com.example.universe.ui.chat.ChatFragment;
import com.example.universe.ui.material.MaterialFragment;
import com.google.android.material.tabs.TabLayout;

public class MaterialChatFragment extends Fragment {

    private TabLayout tabLayout;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_material_chat, container, false);

        // TabLayout
        tabLayout = root.findViewById(R.id.tablayout);

        // TabLayout'a tıklama olayları ekleyelim
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                switch (tab.getPosition()) {
                    case 0:
                        // "Chat" sekmesine tıklandığında ChatFragment'ı yükle
                        replaceFragment(new ChatFragment());
                        break;
                    case 1:
                        // "Materyal" sekmesine tıklandığında MaterialFragment'ı yükle
                        replaceFragment(new MaterialFragment());
                        break;
                    default:
                        break;
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                // Burada herhangi bir işlem yapmaya gerek yok
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                // Burada da herhangi bir işlem yapmaya gerek yok
            }
        });

        // İlk olarak ChatFragment'ı yükle
        if (savedInstanceState == null) {
            replaceFragment(new ChatFragment());
        }

        return root;
    }

    // Fragment'ı değiştirme işlemi
    private void replaceFragment(Fragment fragment) {
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, fragment);  // container'ı belirlemelisin
        transaction.addToBackStack(null);  // Geri tuşuyla çıkmak için
        transaction.commit();
    }
}
