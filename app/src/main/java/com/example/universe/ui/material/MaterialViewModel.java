package com.example.universe.ui.material;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MaterialViewModel extends ViewModel {

    private final MutableLiveData<List<MaterialItem>> materialList = new MutableLiveData<>();
    private final List<MaterialItem> allItems = new ArrayList<>();
    private final DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("materials");

    public LiveData<List<MaterialItem>> getMaterialList() {
        return materialList;
    }

    public void loadMaterials(String categoryKey) {
        dbRef.child(categoryKey).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                allItems.clear();
                for (DataSnapshot snap : snapshot.getChildren()) {
                    MaterialItem item = snap.getValue(MaterialItem.class);
                    if (item != null) {
                        allItems.add(item);
                    }
                }
                materialList.setValue(new ArrayList<>(allItems));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Hata yönetimi
            }
        });
    }

    public void filterMaterials(String query) {
        List<MaterialItem> filtered = new ArrayList<>();
        for (MaterialItem item : allItems) {
            if (item.getTitle().toLowerCase().contains(query.toLowerCase())) {
                filtered.add(item);
            }
        }
        materialList.setValue(filtered);
    }

    public void filterMaterialsByOption(String filterKey) {
        List<MaterialItem> filtered = new ArrayList<>();
        for (MaterialItem item : allItems) {
            if (item.getType().equals(filterKey)) {
                filtered.add(item);
            }
        }
        materialList.setValue(filtered);
    }

    public void addMaterial(String categoryKey, MaterialItem item, Runnable onSuccess, Runnable onFailure) {
        String key = dbRef.child(categoryKey).push().getKey();
        if (key != null) {
            dbRef.child(categoryKey).child(key).setValue(item)
                    .addOnSuccessListener(aVoid -> {
                        onSuccess.run();
                        loadMaterials(categoryKey);
                    })
                    .addOnFailureListener(e -> onFailure.run());
        } else {
            onFailure.run();
        }
    }
}
