package ru.magnat.smnavigator.auth;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class SmAuthenticatorService extends Service {
    @Override
    public IBinder onBind(Intent intent) {

        SmAuthenticator authenticator = new SmAuthenticator(this);
        return authenticator.getIBinder();
    }
}
