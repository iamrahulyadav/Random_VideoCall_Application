package com.example.abc.random_videocall_application;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class Splash extends AppCompatActivity {

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

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

                    Intent intent = new Intent(Splash.this, Home2.class);
                    startActivity(intent);
                    finish();
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
}
