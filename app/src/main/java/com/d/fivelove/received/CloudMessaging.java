package com.d.fivelove.received;

import android.content.Intent;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.d.fivelove.activities.IncomingCallAudioActivity;
import com.d.fivelove.utils.Constants;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

/**
 * Created by Nguyen Kim Khanh on 8/22/2020.
 */
public class CloudMessaging extends FirebaseMessagingService {
    @Override
    public void onNewToken(@NonNull String token) {
        super.onNewToken(token);
        Log.d("FCM: ", token);
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            String id = FirebaseAuth.getInstance().getCurrentUser().getUid();
            DocumentReference reference = FirebaseFirestore.getInstance()
                    .collection("users")
                    .document(id);
            reference.update("fcmToken", token);
        }

    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        String type = remoteMessage.getData().get(Constants.REMOTE_MSG_TYPE);
        if (type != null) {
            if (type.equals(Constants.REMOTE_MSG_INVITATION)) {
                Intent intent = new Intent(getApplicationContext(), IncomingCallAudioActivity.class);
                intent.putExtra(Constants.REMOTE_MSG_MEETING_TYPE, remoteMessage.getData().get(Constants.REMOTE_MSG_MEETING_TYPE));
                intent.putExtra(Constants.REMOTE_MSG_AVATAR_USER, remoteMessage.getData().get(Constants.REMOTE_MSG_AVATAR_USER));
                intent.putExtra(Constants.REMOTE_MSG_INVITER_TOKEN, remoteMessage.getData().get(Constants.REMOTE_MSG_INVITER_TOKEN));
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            } else if (type.equals(Constants.REMOTE_MSG_INVITATION_RESPONSE)) {
                Intent intent = new Intent(Constants.REMOTE_MSG_INVITATION_RESPONSE);
                intent.putExtra(Constants.REMOTE_MSG_INVITATION_RESPONSE, remoteMessage.getData().get(Constants.REMOTE_MSG_INVITATION_RESPONSE));
                LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
            }
        }
    }

}
