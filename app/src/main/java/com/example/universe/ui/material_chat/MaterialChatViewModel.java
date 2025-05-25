package com.example.universe.ui.material_chat;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class MaterialChatViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public MaterialChatViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("Ekstra kaynak ve Chat sayfası");
    }

    public LiveData<String> getText() {
        return mText;
    }
}