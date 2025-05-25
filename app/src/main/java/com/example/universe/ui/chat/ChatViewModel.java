package com.example.universe.ui.chat;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ChatViewModel extends ViewModel {
    private final MutableLiveData<List<Message>> messagesLiveData = new MutableLiveData<>();
    private final DatabaseReference messagesRef = FirebaseDatabase.getInstance().getReference("Messages");

    public ChatViewModel() {
        messagesRef.orderByChild("timestamp").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                List<Message> messages = new ArrayList<>();
                for (DataSnapshot data : snapshot.getChildren()) {
                    Message message = data.getValue(Message.class);
                    if (message != null) {
                        messages.add(message);
                    }
                }
                messagesLiveData.setValue(messages);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // İstersen buraya hata loglama koyabilirsin
            }
        });
    }

    public LiveData<List<Message>> getMessages() {
        return messagesLiveData;
    }

    public void sendMessage(Message message) {
        messagesRef.push().setValue(message);
    }
}
