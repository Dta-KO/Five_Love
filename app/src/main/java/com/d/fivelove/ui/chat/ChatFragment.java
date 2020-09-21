package com.d.fivelove.ui.chat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.d.fivelove.activities.OutgoingCallAudioActivity;
import com.d.fivelove.databinding.ChatFragmentBinding;
import com.d.fivelove.model.User;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;
import java.util.Random;

public class ChatFragment extends Fragment {
    private FloatingActionButton fab, fabCall, fabChat;
    private ChatFragmentBinding binding;
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

            String permission = Manifest.permission.RECORD_AUDIO;
            String permission2 = Manifest.permission.MODIFY_AUDIO_SETTINGS;
            int grant = ContextCompat.checkSelfPermission(requireContext(), permission);
            int grant2 = ContextCompat.checkSelfPermission(requireContext(), permission2);

            if (grant != PackageManager.PERMISSION_GRANTED || grant2 != PackageManager.PERMISSION_GRANTED) {
                String[] permissions = new String[2];
                permissions[0] = permission;
                permissions[1] = permission2;
                ActivityCompat.requestPermissions(requireActivity(), permissions, 1);
            } else {
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
                                    initRandomConnect("nữ");
                                } else if (sex.equals("nữ")) {
                                    initRandomConnect("nam");
                                }
                            }
                        });
            }

        });

    }

    private void initRandomConnect(String sexConnect) {
        FirebaseFirestore.getInstance().collection("users")
                .whereEqualTo("sex", sexConnect)
                .whereEqualTo("abilityListener", "true")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (queryDocumentSnapshots.isEmpty()) {
                        {
                            Toast.makeText(requireContext(), "Hiện không có ai online, bạn vui lòng thử lại sau ít phút!", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        List<User> partners = queryDocumentSnapshots.toObjects(User.class);
                        User partner = partners.get(new Random().nextInt(partners.size()));
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