package com.google.ar.sceneform.samples.hellosceneform;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageButton;

public class LoginActivity extends AppCompatActivity {

    public static final String USER_PREFS = "user_prefs";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_screen);

        ImageButton redGangButton = findViewById(R.id.redSide);
        ImageButton greenGangButton = findViewById(R.id.greenSide);

        redGangButton.setOnClickListener(v -> processTeamChoose(false));
        greenGangButton.setOnClickListener(v -> processTeamChoose(true));
    }

    private void processTeamChoose(boolean greenSide) {

        SharedPreferences sharedPref = getSharedPreferences(USER_PREFS, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();

        int userId = 1;
        String userToken = "1";


        if (greenSide) {
            userId = 1;
            userToken = "1";
        } else {
            userId = 2;
            userToken = "2";
        }

        editor.putInt("user", userId);
        editor.putString("userToken", userToken);
        editor.apply();

        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(intent);
    }
}
