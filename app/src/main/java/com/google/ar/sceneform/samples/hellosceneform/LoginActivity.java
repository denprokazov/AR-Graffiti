package com.google.ar.sceneform.samples.hellosceneform;

import android.content.Context;
import android.content.SharedPreferences;
import android.media.Image;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;


import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class LoginActivity extends AppCompatActivity {
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

        SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
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
    }
}
