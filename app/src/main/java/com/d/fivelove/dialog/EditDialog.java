package com.d.fivelove.dialog;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.d.fivelove.databinding.EditNameLayoutBinding;
import com.google.android.material.button.MaterialButtonToggleGroup;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Objects;

/**
 * Created by Nguyen Kim Khanh on 9/3/2020.
 */
public class EditDialog extends DialogFragment {
    private EditNameLayoutBinding binding;
    private EditText edtName;
    private RadioButton btnMan, btnFemale;
    private Button btnOk, btnCancel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = EditNameLayoutBinding.inflate(getLayoutInflater());

        return binding.getRoot();


    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init();
        setBtnOk();
        setBtnCancel();
    }

    private void init() {
        edtName = binding.edtName;
        btnOk = binding.btnOk;
        btnCancel = binding.btnCancel;
        btnMan = binding.btnMan;
        btnFemale = binding.btnFemale;
    }

    private void setBtnOk() {
        btnOk.setOnClickListener(view -> {
            String id = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
            String name = edtName.getText().toString().trim();
            if (name.length() < 5) {
                Toast.makeText(requireContext(), "Tên người tối thiểu 5 ký tự", Toast.LENGTH_SHORT).show();
            } else {
                FirebaseFirestore.getInstance().collection("users")
                        .document(id)
                        .update("name", name);

                requireDialog().dismiss();
            }
            if (btnFemale.isChecked()) {
                FirebaseFirestore.getInstance().collection("users")
                        .document(id)
                        .update("sex", "nữ");
            }
            if (btnMan.isChecked()) {
                FirebaseFirestore.getInstance().collection("users")
                        .document(id)
                        .update("sex", "nam");
            }
        });
    }

    private void setBtnCancel() {
        btnCancel.setOnClickListener(view -> {
            requireDialog().dismiss();
        });
    }
}
