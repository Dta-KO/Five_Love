package com.d.fivelove.received;

import android.util.Log;

import androidx.annotation.NonNull;

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
        Log.d("FCM remote: ", remoteMessage.getData().toString());
    }

}
