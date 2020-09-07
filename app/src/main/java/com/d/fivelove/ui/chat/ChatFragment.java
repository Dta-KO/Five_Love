package com.d.fivelove.ui.chat;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.d.fivelove.activities.OutgoingCallAudioActivity;
import com.d.fivelove.databinding.ChatFragmentBinding;
import com.d.fivelove.model.User;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class ChatFragment extends Fragment {
    private FloatingActionButton fab, fabCall, fabChat;
    private ChatFragmentBinding binding;
    private ChatViewModel mViewModel;
    private boolean hideFab = true;

    public static ChatFragment newInstance() {
        return new ChatFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = ChatFragmentBinding.inflate(getLayoutInflater());
        return binding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(ChatViewModel.class);
        // TODO: Use the ViewModel
        init();
        setFab();
        setFabCall();
        setFabChat();
    }

    private void init() {
        fab = binding.floatingActionButton;
        fabCall = binding.fabLayout.fabCall;
        fabChat = binding.fabLayout.fabChat;
    }

    private void setFab() {
        fab.setOnClickListener(view -> {
            if (hideFab) {
                hideFab = false;
                fabCall.show();
                fabChat.show();
            } else {
                hideFab = true;
                fabCall.hide();
                fabChat.hide();
            }

        });
    }

    private void setFabCall() {

        fabCall.setOnClickListener(view -> {
            String id = FirebaseAuth.getInstance().getCurrentUser().getUid();
            FirebaseFirestore.getInstance().collection("users").document(id)
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {
                        final User user = documentSnapshot.toObject(User.class);
                        assert user != null;
                        if (user.getSex() == null || user.getSex().isEmpty() || user.getSex().equals("")) {
                            Toast.makeText(requireContext(),
                                    "Bạn cần cập nhật thông tin trong profile trước khi thực hiện tính năng này!",
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            String sex = user.getSex();
                            if (sex.equals("nam")) {
                                setSexConnect("nữ");
                            } else if (sex.equals("nữ")) {
                                setSexConnect("nam");
                            }
                        }
                    });
        });

    }

    private void setSexConnect(String sexConnect) {
        FirebaseFirestore.getInstance().collection("users")
                .whereEqualTo("sex", sexConnect)
                .whereEqualTo("abilityListener", "true")
                .limit(1)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (queryDocumentSnapshots.isEmpty()) {
                        {
                            Toast.makeText(requireContext(), "Hiện không có ai online, bạn vui lòng thử lại sau ít phút!", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        User partner = queryDocumentSnapshots.toObjects(User.class).get(0);
                        Intent intent = new Intent(requireContext(), OutgoingCallAudioActivity.class);
                        intent.putExtra("user", partner);
                        startActivity(intent);
                    }

                });
    }

    private void setFabChat() {
        fabChat.setOnClickListener(view -> {

        });
    }

}