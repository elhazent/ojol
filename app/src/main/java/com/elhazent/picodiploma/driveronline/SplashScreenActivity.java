package com.elhazent.picodiploma.driveronline;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.elhazent.picodiploma.driveronline.helper.SessionManager;


public class SplashScreenActivity extends AppCompatActivity {

    Context c;
    private SessionManager manager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        Handler handler = new Handler();
        c = this;
        manager = new SessionManager(this);
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (manager.isLogin() == true) {
                    pindahclass(MainActivity.class);
                } else {
                    pindahclass(LoginRegisterActivity.class);
                }
            }
        }, 4000);
    }

    public void pindahclass(Class kelastujan) {
        startActivity(new Intent(c, kelastujan));
    }

    public void pesan(String isipesan) {
        Toast.makeText(c, isipesan, Toast.LENGTH_SHORT).show();
    }
}
