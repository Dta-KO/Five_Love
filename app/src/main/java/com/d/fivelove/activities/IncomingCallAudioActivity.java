package com.d.fivelove.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.d.fivelove.databinding.ActivityCallAudioIncomingBinding;
import com.d.fivelove.network.ApiClient;
import com.d.fivelove.network.ApiService;
import com.d.fivelove.utils.Constants;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class IncomingCallAudioActivity extends AppCompatActivity {

    private ActivityCallAudioIncomingBinding binding;
    private ImageView btnAccept, btnCancel;
    private int roomId;
    private String partnerToken;

    private BroadcastReceiver invitationResponseReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String type = intent.getStringExtra(Constants.REMOTE_MSG_INVITATION_RESPONSE);
            if (type != null) {
                if (type.equals(Constants.REMOTE_MSG_INVITATION_CANCELED)) {
                    Toast.makeText(getApplicationContext(), "Invitation canceled!", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
        }
    };
    private Ringtone ringtone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCallAudioIncomingBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        init();
        getData();
        setBtnAccept();
        setBtnCancel();
        roomId = Integer.parseInt(Objects.requireNonNull(getIntent().getStringExtra(Constants.ROOM_ID)));
        partnerToken = getIntent().getStringExtra(Constants.REMOTE_MSG_INVITER_TOKEN);
        //sound for incoming call
        Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
        ringtone = RingtoneManager.getRingtone(getApplicationContext(), notification);
        ringtone.play();
    }

    private void getData() {
        Intent intent = getIntent();
        String avatarPartner = intent.getStringExtra(Constants.REMOTE_MSG_AVATAR_USER);
        binding.setAvatarPartner(avatarPartner);
    }

    private void init() {
        btnAccept = binding.btnAccept;
        btnCancel = binding.btnCancel;
    }

    private void setBtnAccept() {
        btnAccept.setOnClickListener(view -> {
            ringtone.stop();
            sendInvitationResponse(Constants.REMOTE_MSG_INVITATION_ACCEPTED, getIntent().getStringExtra(Constants.REMOTE_MSG_INVITER_TOKEN));
        });
    }

    private void setBtnCancel() {
        btnCancel.setOnClickListener(view -> {
            ringtone.stop();
            sendInvitationResponse(Constants.REMOTE_MSG_INVITATION_REFUSED, getIntent().getStringExtra(Constants.REMOTE_MSG_INVITER_TOKEN));

        });
    }

    private void sendInvitationResponse(String type, String receiverToken) {
        try {
            JSONArray tokens = new JSONArray();
            tokens.put(receiverToken);

            JSONObject body = new JSONObject();
            JSONObject data = new JSONObject();

            data.put(Constants.REMOTE_MSG_TYPE, Constants.REMOTE_MSG_INVITATION_RESPONSE);
            data.put(Constants.REMOTE_MSG_INVITATION_RESPONSE, type);

            body.put(Constants.REMOTE_MSG_DATA, data);
            body.put(Constants.REMOTE_MSG_REGISTRATION_IDS, tokens);

            sendRemoteMessage(body.toString(), type);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void sendRemoteMessage(String body, String type) {
        ApiClient.getClient().create(ApiService.class)
                .sendRemoteMessage(Constants.getRemoteMessageHeaders(), body)
                .enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(@NotNull Call<String> call, @NotNull Response<String> response) {
                        if (response.isSuccessful()) {
                            if (type.equals(Constants.REMOTE_MSG_INVITATION_ACCEPTED)) {
                                Toast.makeText(getApplicationContext(), "Accepted invitation!", Toast.LENGTH_SHORT).show();
                                //start call activity
                                Intent callIntent = new Intent(IncomingCallAudioActivity.this, AudioCallActivity.class);
                                callIntent.putExtra(Constants.ROOM_ID, roomId);
                                callIntent.putExtra(Constants.PARTNER_TOKEN, partnerToken);
//                                callIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(callIntent);
                                finish();
                            }
                            if (type.equals(Constants.REMOTE_MSG_INVITATION_REFUSED)) {
                                Toast.makeText(getApplicationContext(), "Refused invitation!", Toast.LENGTH_SHORT).show();
                                finish();
                            }
                        }
                    }

                    @Override
                    public void onFailure(@NotNull Call<String> call, @NotNull Throwable t) {
                        Toast.makeText(getApplicationContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
                        finish();
                    }
                });

    }


    @Override
    protected void onStart() {
        super.onStart();
        LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(invitationResponseReceiver,
                new IntentFilter(Constants.REMOTE_MSG_INVITATION_RESPONSE));
    }

    @Override
    protected void onStop() {
        super.onStop();
        LocalBroadcastManager.getInstance(getApplicationContext()).unregisterReceiver(invitationResponseReceiver);
    }
}