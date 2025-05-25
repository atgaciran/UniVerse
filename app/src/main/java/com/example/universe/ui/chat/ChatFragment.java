package com.example.universe.ui.chat;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.universe.R;
import com.example.universe.ui.BaseFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;

import java.util.ArrayList;
import java.util.List;

public class ChatFragment extends Fragment {

    private RecyclerView recyclerView;
    private EditText inputMessage;
    private ImageButton sendButton;
    private MessagesAdapter adapter;
    private List<Message> messageList;
    private ChatViewModel chatViewModel;

    private DatabaseReference usersRef;
    private FirebaseUser currentUser;
    private String currentUserName = "";
    private String currentUserSurname = "";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_chat, container, false);

        chatViewModel = new ViewModelProvider(this).get(ChatViewModel.class);

        recyclerView = root.findViewById(R.id.chatRecyclerView);
        inputMessage = root.findViewById(R.id.input_message);
        sendButton = root.findViewById(R.id.send_message);

        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(getContext(), "Kullanıcı girişi yapılmamış.", Toast.LENGTH_SHORT).show();
            return root;
        }

        usersRef = FirebaseDatabase.getInstance().getReference("Users");

        messageList = new ArrayList<>();
        adapter = new MessagesAdapter(messageList, currentUser.getUid());

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);

        fetchCurrentUserName();
        observeMessages();

        sendButton.setOnClickListener(v -> {
            String messageText = inputMessage.getText().toString().trim();
            if (TextUtils.isEmpty(messageText)) {
                Toast.makeText(getContext(), "Mesaj boş olamaz!", Toast.LENGTH_SHORT).show();
                return;
            }
            sendMessage(messageText);
        });

        return root;
    }

    private void fetchCurrentUserName() {
        usersRef.child(currentUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                currentUserName = snapshot.child("name").getValue(String.class);
                currentUserSurname = snapshot.child("surname").getValue(String.class);

                if (currentUserName == null) currentUserName = "Bilinmeyen";
                if (currentUserSurname == null) currentUserSurname = "Kullanıcı";
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Kullanıcı bilgileri alınamadı.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void sendMessage(String messageText) {
        long timestamp = System.currentTimeMillis();
        Message message = new Message(
                currentUser.getUid(),
                currentUserName,
                currentUserSurname,
                messageText,
                timestamp
        );

        chatViewModel.sendMessage(message);
        inputMessage.setText("");
    }

    private void observeMessages() {
        chatViewModel.getMessages().observe(getViewLifecycleOwner(), messages -> {
            adapter.updateMessages(messages);
            if (!messages.isEmpty()) {
                recyclerView.scrollToPosition(messages.size() - 1);
            }
        });
    }
}
