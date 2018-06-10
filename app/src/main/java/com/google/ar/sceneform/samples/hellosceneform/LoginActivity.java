package com.google.ar.sceneform.samples.hellosceneform;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;


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

        EditText userId = findViewById(R.id.user_id);
        EditText gang = findViewById(R.id.gang);
        Button login = findViewById(R.id.log_in);

        login.setOnClickListener(v -> {
            String userIdText = userId.getText().toString();
            String gangName = gang.getText().toString();
            if(userIdText == "" || gangName == "") {
                return;
            }

            Thread thread = new Thread(new Runnable() {

                @Override
                public void run() {
                    try  {
                        OkHttpClient client = new OkHttpClient();


                        MediaType mediaType = MediaType.parse("application/json");
                        RequestBody body = RequestBody.create(mediaType, String.format("{\n\t\"userid\":\t\"%s\",\n\t\"gang\":\t\"%s\"\n}", userIdText, gangName));
                        Log.d("login", "onCreate: " + body.toString());
                        Request request = new Request.Builder()
                                .url("http://176.9.2.82:6778/auth")
                                .post(body)
                                .addHeader("content-type", "application/json")
                                .addHeader("authorization", "Bearer 123")
                                .build();

                        Response response = client.newCall(request).execute();
                        if(response.isSuccessful()) {
                            //Toast.makeText(this,"succesfull!", Toast.LENGTH_LONG).show();
                            Log.d("login", "run: sucess");
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });

            thread.start();

        });




    }
}
