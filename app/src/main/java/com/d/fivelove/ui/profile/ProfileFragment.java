package com.d.fivelove.ui.profile;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Outline;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewOutlineProvider;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.d.fivelove.activities.LoginActivity;
import com.d.fivelove.activities.MainActivity;
import com.d.fivelove.databinding.ProfileFragmentBinding;
import com.d.fivelove.dialog.EditDialog;
import com.d.fivelove.model.User;
import com.d.fivelove.activities.ImagesActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class ProfileFragment extends Fragment implements MainActivity.Callback {

    public static final int IMAGE = 1403;
    private ProfileViewModel mViewModel;
    private ProfileFragmentBinding binding;
    private FrameLayout btnAddImage, btnLogout;
    private ImageButton btnEdt, btnCopyId;

    public static ProfileFragment newInstance() {
        return new ProfileFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = ProfileFragmentBinding.inflate(getLayoutInflater());
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init();
        setupBtnLogout();
        setupBtnAddImage();
        setBtnEdtName();
        MainActivity.callback = this;
    }

    private void setupBtnLogout() {
        btnLogout.setOutlineProvider(new ViewOutlineProvider() {
            @Override
            public void getOutline(View view, Outline outline) {
                outline.setRoundRect(0, 0, view.getWidth() + 10, view.getHeight() + 10, 100);

            }
        });
        btnLogout.setOnClickListener(view -> {
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(requireActivity(), LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            requireActivity().finish();
        });
    }

    private void init() {
        btnAddImage = binding.btnAddImage;
        btnLogout = binding.btnLogout;
        btnEdt = binding.editName;
        btnCopyId = binding.copyId;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(ProfileViewModel.class);
        // TODO: Use the ViewModel
        getCurrentUser();
    }

    public void getCurrentUser() {
        String id = FirebaseAuth.getInstance().getCurrentUser().getUid();
        FirebaseFirestore.getInstance().collection("users").document(id).addSnapshotListener((value, error) -> {
            if (error != null) {
                Log.d("Error in ProfileVM: ", error.toString());
            }
            if (value != null) {
                binding.setUser(value.toObject(User.class));
            }
        });
    }

    private void setupBtnAddImage() {
        btnAddImage.setOutlineProvider(new ViewOutlineProvider() {
            @Override
            public void getOutline(View view, Outline outline) {
                outline.setRoundRect(0, 0, view.getWidth() + 10, view.getHeight() + 10, 100);

            }
        });
        btnAddImage.setOnClickListener(view -> {
            requestExternalStoragePermission();
            if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                pickImage();
            } else {
                Toast.makeText(requireContext(), "Ứng dụng cần truy cập bộ nhớ trong \n Vui lòng cấp quyền và thử lại.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setBtnEdtName() {
        btnEdt.setOnClickListener(view -> {
            EditDialog editDialog = new EditDialog();
            editDialog.show(requireActivity().getSupportFragmentManager(), "name_dialog");
        });
    }

    private void pickImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        requireActivity().startActivityForResult(intent, IMAGE);

    }

    private void requestExternalStoragePermission() {
        int grant = ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_EXTERNAL_STORAGE);
        int grant2 = ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (grant != PackageManager.PERMISSION_GRANTED && grant2 != PackageManager.PERMISSION_GRANTED) {
            String[] permissions = new String[2];
            String permission = Manifest.permission.READ_EXTERNAL_STORAGE;
            String permission2 = Manifest.permission.WRITE_EXTERNAL_STORAGE;
            permissions[0] = permission;
            permissions[1] = permission2;
            ActivityCompat.requestPermissions(requireActivity(), permissions, 1);
        }
    }


    @Override
    public void onGetImageSuccess(Uri imgUri) {
        Intent intent = new Intent(getContext(), ImagesActivity.class);
        intent.putExtra("uriImg", String.valueOf(imgUri));
        startActivity(intent);
    }
}