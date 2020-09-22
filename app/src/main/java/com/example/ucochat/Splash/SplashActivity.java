package com.example.ucochat.Splash;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.example.ucochat.Login.LoginActivity;
import com.example.ucochat.Main.MainActivity;
import com.example.ucochat.Main.MainActivityAdm;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SplashActivity extends AppCompatActivity {

    private SharedPreferences preferences;
    FirebaseUser firebaseUser;

    @Override
    protected void onStart() {
        super.onStart();

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        if (firebaseUser != null) {

            preferences = getSharedPreferences("data", Context.MODE_PRIVATE);
            String profesor = preferences.getString("profesor", "");

            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);

        }
        else {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        }
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }
}
