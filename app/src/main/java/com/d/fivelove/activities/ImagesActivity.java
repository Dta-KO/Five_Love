package com.d.fivelove.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStore;

import com.d.fivelove.R;
import com.d.fivelove.databinding.ActivityImagesBinding;
import com.d.fivelove.utils.ImageCoroutines;
import com.yalantis.ucrop.callback.BitmapCropCallback;
import com.yalantis.ucrop.view.GestureCropImageView;
import com.yalantis.ucrop.view.UCropView;

import java.io.File;
import java.util.UUID;

public class ImagesActivity extends AppCompatActivity {

    private UCropView cropImageView;
    private ActivityImagesBinding binding;
    private SeekBar seekBar;
    private TextView txtRotate;
    private Button resetBtn;
    private ImageButton btnOk, btnCancel;
    private Uri uriImg;
    private GestureCropImageView gestureCropImageView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityImagesBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        init();
        Intent intent = getIntent();

        uriImg = Uri.parse(intent.getStringExtra("uriImg"));
        setCropImageView(uriImg);
        setSeekBar();
        setResetBtn();
        setBtnCancel();
        setBtnOk();
    }

    private void init() {
        cropImageView = binding.cropImageView;
        seekBar = binding.seekBar;
        txtRotate = binding.txtRotate;
        resetBtn = binding.btnReset;
        btnOk = binding.btnOk;
        btnCancel = binding.btnCancel;
    }

    private void setResetBtn() {
        resetBtn.setOnClickListener(view -> {
                    cropImageView.resetCropImageView();
                    setCropImageView(uriImg);
                    txtRotate.setText(R.string._0_rotate);
                    seekBar.setProgress(180);
                }
        );
    }

    private void setCropImageView(Uri uriImg) {
        gestureCropImageView = cropImageView.getCropImageView();
        String destinationFileName = UUID.randomUUID().toString() + ".jpeg";
        Uri outputUri = Uri.fromFile(new File(getCacheDir(), destinationFileName));
        try {
            gestureCropImageView.setImageUri(uriImg, outputUri);
        } catch (Exception e) {
            e.printStackTrace();
        }
        gestureCropImageView.setRotateEnabled(false);
        gestureCropImageView.setImageToWrapCropBounds(true);
        gestureCropImageView.setScaleEnabled(true);
        gestureCropImageView.setTargetAspectRatio(0.75f);
        gestureCropImageView.setAdjustViewBounds(true);

    }

    private void setSeekBar() {
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            float zero = 180;

            @SuppressLint("SetTextI18n")
            @Override
            public void onProgressChanged(SeekBar seekBar, int progressValue, boolean fromUser) {
                float zeroTxt = 180;
                float rotateDegreeTxt = progressValue - zeroTxt;
                txtRotate.setText(rotateDegreeTxt + "°");
                seekBar.getProgress();
                float rotateDegree = seekBar.getProgress() - zero;
                try {
                    cropImageView.getCropImageView().postRotate(rotateDegree);
                } catch (NullPointerException e) {
                    Log.d("Exception: ", e.getMessage());
                }
                zero = seekBar.getProgress();
                cropImageView.getCropImageView().setImageToWrapCropBounds(true);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

    }

    private void setBtnOk() {
        cropImageView.getCropImageView().setImageToWrapCropBounds(false);
        btnOk.setOnClickListener(view -> {
            cropImageView.getCropImageView().cropAndSaveImage(Bitmap.CompressFormat.JPEG, 85, new BitmapCropCallback() {
                @Override
                public void onBitmapCropped(@NonNull Uri uri, int offsetX, int offsetY, int imageWidth, int imageHeight) {
                    final ImageCoroutines imageCoroutines = new ViewModelProvider(ViewModelStore::new).get(ImageCoroutines.class);
                    imageCoroutines.uploadImage(getApplicationContext(), uri);
                    Intent intent = new Intent(ImagesActivity.this, MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtra("positionViewPager", 0);
                    startActivity(intent);
                }

                @Override
                public void onCropFailure(@NonNull Throwable throwable) {
                    Intent intent = new Intent(ImagesActivity.this, MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    Toast.makeText(getApplicationContext(), "Error!", Toast.LENGTH_SHORT).show();
                }
            });
        });
    }

    private void setBtnCancel() {
        btnCancel.setOnClickListener(view -> {
            Intent intent = new Intent(ImagesActivity.this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra("positionViewPager", 0);
            startActivity(intent);
        });
    }
}