package com.d.fivelove.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStore;
import androidx.viewpager2.widget.ViewPager2;

import com.d.fivelove.R;
import com.d.fivelove.adapter.PagerAdapter;
import com.d.fivelove.databinding.MainActivityBinding;
import com.d.fivelove.model.User;
import com.d.fivelove.ui.profile.ProfileFragment;
import com.d.fivelove.utils.Constants;
import com.d.fivelove.utils.UpdateToServerCoroutines;
import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.Objects;
import java.util.Random;

import io.agora.rtc.IRtcEngineEventHandler;
import io.agora.rtc.RtcEngine;

public class MainActivity extends AppCompatActivity {
    private static final long LOCATION_REFRESH_TIME = 10;
    private static final float LOCATION_REFRESH_DISTANCE = 1000;
    public static Callback callback;

    final UpdateToServerCoroutines update = new ViewModelProvider(ViewModelStore::new).get(UpdateToServerCoroutines.class);
    private final LocationListener locationListener = location -> {
        String latitude = String.valueOf(location.getLatitude());
        String longitude = String.valueOf(location.getLongitude());
        try {
            FirebaseFirestore.getInstance().collection("users")
                    .document(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid())
                    .update("latitude", latitude);
            FirebaseFirestore.getInstance().collection("users")
                    .document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                    .update("longitude", longitude);
        } catch (Exception e) {
            e.printStackTrace();
        }
    };
    protected TabLayout tab;
    private MainActivityBinding binding;
    private ViewPager2 viewPager2;
    private boolean doubleBackToExitPressedOnce = false;
    private String id = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = MainActivityBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setViewPager2();
        setTabLayout();

        FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(instanceIdResult -> {
            updateFCMTokenToDB(instanceIdResult.getToken());
        });
        update.updateAbilityListener(id, "true");
    }

    @Override
    protected void onStart() {
        super.onStart();
        Intent intent = getIntent();
        int position = intent.getIntExtra(Constants.CURRENT_POSITION_VIEW_PAGER, 1);

        Objects.requireNonNull(tab.getTabAt(position)).select();
        setupLocationManager();
    }


    @Override
    protected void onStop() {
        super.onStop();
        update.updateAbilityListener(id, "false");

    }

    @Override
    protected void onResume() {
        super.onResume();
        update.updateAbilityListener(id, "true");
    }

    private void setupLocationManager() {
        LocationManager mLocationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        // getting GPS status
        boolean isGPSEnabled = mLocationManager
                .isProviderEnabled(LocationManager.GPS_PROVIDER);
        // getting network status
        boolean isNetworkEnabled = mLocationManager
                .isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        if (!isGPSEnabled && !isNetworkEnabled) {
            // no network provider is enabled
        } else {

            if (isNetworkEnabled) {
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                        && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
                        && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_BACKGROUND_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                    String permission = Manifest.permission.ACCESS_FINE_LOCATION;
                    String permission2 = Manifest.permission.ACCESS_COARSE_LOCATION;
                    String permission3 = null;
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
                        permission3 = Manifest.permission.ACCESS_BACKGROUND_LOCATION;
                        String[] permissions = new String[3];
                        permissions[0] = permission;
                        permissions[1] = permission2;
                        permissions[2] = permission3;
                        ActivityCompat.requestPermissions(this, permissions, 1);
                    } else {
                        String[] permissions = new String[2];
                        permissions[0] = permission;
                        permissions[1] = permission2;
                        ActivityCompat.requestPermissions(this, permissions, 1);
                    }
                    return;
                }
                mLocationManager.requestLocationUpdates(
                        LocationManager.NETWORK_PROVIDER,
                        LOCATION_REFRESH_TIME,
                        LOCATION_REFRESH_DISTANCE, locationListener);
            }
            if (isGPSEnabled) {
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                    String permission = Manifest.permission.ACCESS_FINE_LOCATION;
                    String permission2 = Manifest.permission.ACCESS_COARSE_LOCATION;
                    String[] permissions = new String[2];
                    permissions[0] = permission;
                    permissions[1] = permission2;
                    ActivityCompat.requestPermissions(this, permissions, 1);
                    return;
                }
                mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, LOCATION_REFRESH_TIME,
                        LOCATION_REFRESH_DISTANCE, locationListener);
            }
        }
    }

    private void setViewPager2() {
        viewPager2 = binding.viewPager;
        viewPager2.setUserInputEnabled(false);
        viewPager2.setAdapter(new PagerAdapter(this));
        viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                BadgeDrawable badgeDrawable = Objects.requireNonNull(tab.getTabAt(position)).getOrCreateBadge();
                badgeDrawable.setVisible(false);
                if (position == 0) {
                    tab.setVisibility(View.GONE);
                } else {
                    tab.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    private void setTabLayout() {
        tab = binding.tabLayout;
        new TabLayoutMediator(tab, viewPager2, true, (tab1, position) -> {
            BadgeDrawable badgeDrawable = tab1.getOrCreateBadge();
            switch (position) {
                case 0:
                    tab1.setIcon(R.drawable.ic_account);
                    badgeDrawable.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.badgeColor));
                    badgeDrawable.setVisible(true);
                    break;
                case 1:
                    tab1.setIcon(R.drawable.ic_love);
                    badgeDrawable.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.badgeColor));
                    badgeDrawable.setVisible(true);
                    badgeDrawable.setNumber(111);
                    badgeDrawable.setMaxCharacterCount(3);
                    break;
                case 2:
                    tab1.setIcon(R.drawable.ic_chat);
                    badgeDrawable.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.badgeColor));
                    badgeDrawable.setVisible(true);
                    badgeDrawable.setNumber(111);
                    badgeDrawable.setMaxCharacterCount(3);
                    break;
            }
        }).attach();
    }

    private void updateFCMTokenToDB(String token) {
        DocumentReference reference = FirebaseFirestore.getInstance()
                .collection("users")
                .document(id);
        reference.update("fcmToken", token);
    }

    @Override
    public void onBackPressed() {
        if (viewPager2.getCurrentItem() == 1) {
            if (doubleBackToExitPressedOnce) {
                super.onBackPressed();
            }
            this.doubleBackToExitPressedOnce = true;
            Toast.makeText(this, "Ấn lần nữa để thoát.", Toast.LENGTH_SHORT).show();

            new Handler().postDelayed(() -> doubleBackToExitPressedOnce = false, 2000);
        } else {
            viewPager2.setCurrentItem(1);
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ProfileFragment.IMAGE && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri imgUri = data.getData();
            callback.onGetImageSuccess(imgUri);
        }
    }

    public interface Callback {
        void onGetImageSuccess(Uri imgUri);
    }
}