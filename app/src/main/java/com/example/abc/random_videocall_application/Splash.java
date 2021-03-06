package com.example.abc.random_videocall_application;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.abc.random_videocall_application.VideoClasses.SharedPrefsHelper;
import com.example.abc.random_videocall_application.VideoClasses.activities.LoginActivity;
import com.example.abc.random_videocall_application.VideoClasses.activities.OpponentsActivity;
import com.example.abc.random_videocall_application.VideoClasses.services.CallService;
import com.quickblox.users.model.QBUser;

public class Splash extends AppCompatActivity {

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    private SharedPrefsHelper sharedPrefsHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        sharedPreferences = getSharedPreferences("userInfo", Context.MODE_PRIVATE);
        //editor = sharedPreferences.edit();
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {

                boolean hasLoggedIn = sharedPreferences.getBoolean("hasLoggedIn", false);
               // boolean hasLoggedIn = sharedPreferences.getString("hasLoggedIn", "");
                if (hasLoggedIn) {

                    sharedPrefsHelper = SharedPrefsHelper.getInstance();

                    if (sharedPrefsHelper.hasQbUser()) {
                        startLoginService(sharedPrefsHelper.getQbUser());
                    }
                    startHomeActivity();
                }
                else {
                    Intent intent = new Intent(Splash.this, New_Login.class);
                    editor = sharedPreferences.edit();
                    editor.putString("user", " ");
                    editor.putString("password", " ");

                    startActivity(intent);

                    finish();
                }
            }
        }, 3000);

    }
    protected void startLoginService(QBUser qbUser) {
        CallService.start(this, qbUser);
    }
    private void startHomeActivity() {
        Home2.start(Splash.this, false);
        finish();
    }
}
