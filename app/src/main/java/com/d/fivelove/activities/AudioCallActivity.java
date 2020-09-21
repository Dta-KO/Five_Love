package com.d.fivelove.activities;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.TransitionDrawable;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.widget.Chronometer;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.d.fivelove.R;
import com.d.fivelove.databinding.ActivityAudioCallBinding;
import com.d.fivelove.network.ApiClient;
import com.d.fivelove.network.ApiService;
import com.google.firebase.auth.FirebaseAuth;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;
import java.util.TimeZone;

import io.agora.rtc.Constants;
import io.agora.rtc.IRtcEngineEventHandler;
import io.agora.rtc.RtcEngine;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AudioCallActivity extends AppCompatActivity implements View.OnClickListener {
    private static final int PERMISSION_REQ_ID_RECORD_AUDIO = 22;
    private ImageButton btnVolume, btnEndCall, btnMic, btnAddFriend;
    private Chronometer timeCall;
    private final IRtcEngineEventHandler mRtcEventHandler = new IRtcEngineEventHandler() {
        @Override
        public void onUserJoined(int uid, int elapsed) {
            super.onUserJoined(uid, elapsed);

            Toast.makeText(getApplicationContext(), "partner join success!", Toast.LENGTH_SHORT).show();

        }

        @Override
        public void onJoinChannelSuccess(String channel, int uid, int elapsed) {
            super.onJoinChannelSuccess(channel, uid, elapsed);
            Toast.makeText(getApplicationContext(), "Join success!", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onUserOffline(int uid, int reason) {
            super.onUserOffline(uid, reason);
            endCall();
            Toast.makeText(getApplicationContext(), "partner leaved success!", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onLeaveChannel(RtcStats stats) {
            super.onLeaveChannel(stats);
            endCall();
        }
    };
    private RtcEngine mRtcEngine;
    private boolean enableSpeaker = true;
    private boolean enableMic = true;
    private int roomId;
    private BroadcastReceiver invitationResponseReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String type = intent.getStringExtra(com.d.fivelove.utils.Constants.INVITATION_ADD_FRIEND);
            if (type != null) {
                Toast.makeText(getApplicationContext(), "Bạn nhận 1 lời mời kết bạn!", Toast.LENGTH_SHORT).show();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityAudioCallBinding binding = ActivityAudioCallBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        btnEndCall = binding.endCall;
        btnMic = binding.mic;
        btnVolume = binding.volume;
        timeCall = binding.timeCall;
        btnAddFriend = binding.addFriend;

        timeCall.setBase(SystemClock.elapsedRealtime());
        timeCall.start();

        btnMic.setOnClickListener(this);
        btnVolume.setOnClickListener(this);
        btnEndCall.setOnClickListener(this);
        btnAddFriend.setOnClickListener(this);

        roomId = getIntent().getIntExtra(com.d.fivelove.utils.Constants.ROOM_ID, 0);

        if (checkSelfPermission(Manifest.permission.RECORD_AUDIO, PERMISSION_REQ_ID_RECORD_AUDIO)) {
            initializeAgoraEngine(roomId);
        }
        Log.d("roomId: ", roomId + "");

        timeCall.setOnChronometerTickListener(chronometer -> {
            long callTimes = SystemClock.elapsedRealtime() - timeCall.getBase();
            DateFormat formatter = new SimpleDateFormat("mm:ss", Locale.US);
            formatter.setTimeZone(TimeZone.getTimeZone("UTC"));
            String text = formatter.format(new Date(callTimes));
            if (text.equals("00:05")) {
                btnAddFriend.setVisibility(View.VISIBLE);
            }
        });
    }

    @Override
    protected void onDestroy() {
        mRtcEngine.leaveChannel();
        RtcEngine.destroy();
        mRtcEngine = null;
        super.onDestroy();

    }

    @Override
    protected void onStart() {
        super.onStart();
        LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(
                invitationResponseReceiver, new IntentFilter(com.d.fivelove.utils.Constants.INVITATION_ADD_FRIEND)
        );
    }

    @Override
    protected void onStop() {
        super.onStop();
        LocalBroadcastManager.getInstance(getApplicationContext()).unregisterReceiver(invitationResponseReceiver);
    }

    @Override
    public void onClick(View view) {
        if (btnEndCall.equals(view)) {
            endCall();
        } else if (btnMic.equals(view)) {
            toggleMic();
        } else if (btnVolume.equals(view)) {
            toggleVolume();
        } else if (btnAddFriend.equals(view)) {
            addFriend();
        }
    }

    private void addFriend() {
        btnAddFriend.setBackgroundColor(getResources().getColor(R.color.primaryColor));
        String receiverToken = getIntent().getStringExtra(com.d.fivelove.utils.Constants.PARTNER_TOKEN);
        inviteAddFriend(receiverToken);
    }

    private void toggleVolume() {
        Drawable drawableVolume = btnVolume.getDrawable();
        if (drawableVolume instanceof TransitionDrawable) {
            btnVolume.setImageDrawable(drawableVolume);
            ((TransitionDrawable) drawableVolume).setCrossFadeEnabled(true);
            ((TransitionDrawable) drawableVolume).reverseTransition(0);
            if (enableSpeaker) {
                mRtcEngine.setEnableSpeakerphone(true);
                enableSpeaker = false;
            } else {
                mRtcEngine.setEnableSpeakerphone(false);
                enableSpeaker = true;
            }
        }

    }

    private void toggleMic() {
        Drawable drawableMic = btnMic.getDrawable();
        if (drawableMic instanceof TransitionDrawable) {
            btnMic.setImageDrawable(drawableMic);
            ((TransitionDrawable) drawableMic).setCrossFadeEnabled(true);
            ((TransitionDrawable) drawableMic).reverseTransition(0);
            if (enableMic) {
                mRtcEngine.muteLocalAudioStream(true);
                enableMic = false;
            } else {
                mRtcEngine.muteLocalAudioStream(false);
                enableMic = true;
            }
        }
    }

    private void endCall() {
        long callTimes = SystemClock.elapsedRealtime() - timeCall.getBase();
        DateFormat formatter = new SimpleDateFormat("mm:ss", Locale.US);
        formatter.setTimeZone(TimeZone.getTimeZone("UTC"));
        String text = formatter.format(new Date(callTimes));

        String preText = getString(R.string.end_call);
        Toast.makeText(getApplicationContext(), preText + text, Toast.LENGTH_SHORT).show();
        finish();
    }

    private void initializeAgoraEngine(int roomId) {
        try {
            mRtcEngine = RtcEngine.create(getBaseContext(), getString(R.string.agora_app_id), mRtcEventHandler);
            mRtcEngine.setChannelProfile(Constants.CHANNEL_PROFILE_COMMUNICATION);

            String accessToken = getString(R.string.agora_access_token);
            mRtcEngine.joinChannel(accessToken, "hello", "A FiveLover", roomId);

        } catch (Exception e) {
            Log.e("LOG_TAG", Log.getStackTraceString(e));

            throw new RuntimeException("NEED TO check rtc sdk init fatal error\n" + Log.getStackTraceString(e));
        }
    }

    private void sendRemoteMessage(String remoteMessageBody) {
        ApiClient.getClient().create(ApiService.class)
                .sendRemoteMessage(com.d.fivelove.utils.Constants.getRemoteMessageHeaders(), remoteMessageBody)
                .enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(@NotNull Call<String> call, @NotNull Response<String> response) {
                        if (response.isSuccessful()) {
                            Toast.makeText(AudioCallActivity.this, "Đã gửi lời mời kết bạn!", Toast.LENGTH_SHORT).show();

                        } else {
                            Toast.makeText(AudioCallActivity.this, response.message(), Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    }

                    @Override
                    public void onFailure(@NotNull Call<String> call, @NotNull Throwable t) {
                        Toast.makeText(AudioCallActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                        finish();
                    }
                });
    }

    private void inviteAddFriend(String receiverToken) {
        try {
            JSONArray tokens = new JSONArray();
            tokens.put(receiverToken);

            JSONObject body = new JSONObject();
            JSONObject data = new JSONObject();

            data.put(com.d.fivelove.utils.Constants.REMOTE_MSG_TYPE, com.d.fivelove.utils.Constants.INVITATION_ADD_FRIEND);
            data.put(com.d.fivelove.utils.Constants.INVITATION_ADD_FRIEND, Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid());

            body.put(com.d.fivelove.utils.Constants.REMOTE_MSG_DATA, data);
            body.put(com.d.fivelove.utils.Constants.REMOTE_MSG_REGISTRATION_IDS, tokens);

            sendRemoteMessage(body.toString());

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public boolean checkSelfPermission(String permission, int requestCode) {
        Log.i("LOG_TAG", "checkSelfPermission " + permission + " " + requestCode);
        if (ContextCompat.checkSelfPermission(this,
                permission)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{permission},
                    requestCode);
            return false;
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.i("LOG_TAG", "onRequestPermissionsResult " + grantResults[0] + " " + requestCode);

        if (requestCode == PERMISSION_REQ_ID_RECORD_AUDIO) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                initializeAgoraEngine(roomId);
            } else {
                Toast.makeText(getApplicationContext(), "No permission for " + Manifest.permission.RECORD_AUDIO, Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

}