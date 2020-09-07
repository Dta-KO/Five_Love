package com.d.fivelove.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Outline;
import android.os.Bundle;
import android.view.View;
import android.view.ViewOutlineProvider;
import android.widget.ImageView;
import android.widget.Toast;

import com.d.fivelove.databinding.ActivityOutgoingCallAudioBinding;
import com.d.fivelove.model.User;
import com.d.fivelove.network.ApiClient;
import com.d.fivelove.network.ApiService;
import com.d.fivelove.utils.Constants;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.iid.FirebaseInstanceId;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OutgoingCallAudioActivity extends AppCompatActivity {
    private ActivityOutgoingCallAudioBinding binding;
    private ImageView btnCancel;
    private User partner;

    private String inviterToken = null;
    private BroadcastReceiver invitationResponseReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String type = intent.getStringExtra(Constants.REMOTE_MSG_INVITATION_RESPONSE);
            if (type != null) {
                if (type.equals(Constants.REMOTE_MSG_INVITATION_ACCEPTED)) {
                    Toast.makeText(getApplicationContext(), "Invitation accepted!", Toast.LENGTH_SHORT).show();
                } else if (type.equals(Constants.REMOTE_MSG_INVITATION_REFUSED)) {
                    Toast.makeText(getApplicationContext(), "Invitation refused!", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityOutgoingCallAudioBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        init();
        getData();
        setBtnCancel();

        FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(instanceIdResult -> {
            if (instanceIdResult != null) {
                inviterToken = instanceIdResult.getToken();
            }
        });
    }

    private void getData() {
        Intent intent = getIntent();
        partner = (User) intent.getSerializableExtra("user");

        assert partner != null;
        initiateMeeting(Constants.REMOTE_MSG_MEETING_AUDIO_TYPE, partner.getFcmToken());
        binding.setUser(partner);
    }

    private void init() {
        btnCancel = binding.btnCancel;
    }

    private void setBtnCancel() {
        btnCancel.setOutlineProvider(new ViewOutlineProvider() {
            @Override
            public void getOutline(View view, Outline outline) {
                outline.setRoundRect(0, 0, view.getWidth() + 10, view.getHeight() + 10, 1000);
            }
        });
        btnCancel.setOnClickListener(view -> {
            if (partner != null) {
                cancelInvitation(partner.getFcmToken());
            }
        });
    }

    private void initiateMeeting(String meetingType, String receiverToken) {
        try {
            FirebaseFirestore.getInstance().collection("users")
                    .document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {
                        try {
                            JSONArray tokens = new JSONArray();
                            tokens.put(receiverToken);

                            JSONObject body = new JSONObject();
                            JSONObject data = new JSONObject();

                            data.put(Constants.REMOTE_MSG_TYPE, Constants.REMOTE_MSG_INVITATION);
                            data.put(Constants.REMOTE_MSG_MEETING_TYPE, meetingType);
                            data.put(Constants.REMOTE_MSG_INVITER_TOKEN, inviterToken);
                            if (documentSnapshot.toObject(User.class).getImages() == null) {
                                data.put(Constants.REMOTE_MSG_AVATAR_USER, null);
                            } else {
                                data.put(Constants.REMOTE_MSG_AVATAR_USER, documentSnapshot.toObject(User.class).getImages().get(0).getBitmap());
                            }

                            body.put(Constants.REMOTE_MSG_DATA, data);
                            body.put(Constants.REMOTE_MSG_REGISTRATION_IDS, tokens);

                            sendRemoteMessage(body.toString(), Constants.REMOTE_MSG_INVITATION);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    });


        } catch (Exception e) {
            Toast.makeText(OutgoingCallAudioActivity.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void sendRemoteMessage(String remoteMessageBody, String type) {
        ApiClient.getClient().create(ApiService.class)
                .sendRemoteMessage(Constants.getRemoteMessageHeaders(), remoteMessageBody)
                .enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(@NotNull Call<String> call, @NotNull Response<String> response) {
                        if (response.isSuccessful()) {
                            if (type.equals(Constants.REMOTE_MSG_INVITATION)) {
                                Toast.makeText(OutgoingCallAudioActivity.this, "Message remote success", Toast.LENGTH_SHORT).show();
                            } else if (type.equals(Constants.REMOTE_MSG_INVITATION_RESPONSE)) {
                                Toast.makeText(OutgoingCallAudioActivity.this, "Message cancel!", Toast.LENGTH_SHORT).show();
                                finish();
                            }
                        } else {
                            Toast.makeText(OutgoingCallAudioActivity.this, response.message(), Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    }

                    @Override
                    public void onFailure(@NotNull Call<String> call, @NotNull Throwable t) {
                        Toast.makeText(OutgoingCallAudioActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                        finish();
                    }
                });
    }

    private void cancelInvitation(String receiverToken) {
        try {
            JSONArray tokens = new JSONArray();
            tokens.put(receiverToken);

            JSONObject body = new JSONObject();
            JSONObject data = new JSONObject();

            data.put(Constants.REMOTE_MSG_TYPE, Constants.REMOTE_MSG_INVITATION_RESPONSE);
            data.put(Constants.REMOTE_MSG_INVITATION_RESPONSE, Constants.REMOTE_MSG_INVITATION_CANCELED);

            body.put(Constants.REMOTE_MSG_DATA, data);
            body.put(Constants.REMOTE_MSG_REGISTRATION_IDS, tokens);

            sendRemoteMessage(body.toString(), Constants.REMOTE_MSG_INVITATION_RESPONSE);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(
                invitationResponseReceiver, new IntentFilter(Constants.REMOTE_MSG_INVITATION_RESPONSE)
        );
    }

    @Override
    protected void onStop() {
        super.onStop();
        LocalBroadcastManager.getInstance(getApplicationContext()).unregisterReceiver(invitationResponseReceiver);
    }
}