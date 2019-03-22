package com.wave.digitalidentity.Services;

import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.Objects;

public class FirebaseInstanceService extends FirebaseInstanceIdService {
    private static final String TAG="FirebaseInstanceService";

    @Override
    public void onTokenRefresh() {
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        FirebaseMessaging.getInstance().subscribeToTopic("all");
        Log.v(TAG, "Refreshed token: " + refreshedToken);
        sendRegistrationToServer(Objects.requireNonNull(refreshedToken));
    }

    private void sendRegistrationToServer(String refreshedToken) {
        Log.v("TOKEN ", refreshedToken);
    }
}
