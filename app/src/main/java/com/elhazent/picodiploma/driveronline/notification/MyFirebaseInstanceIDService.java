package com.elhazent.picodiploma.driveronline.notification;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService {

    @Override
    public void onTokenRefresh() {
        String refrestoken = FirebaseInstanceId.getInstance().getToken();
    }

}
