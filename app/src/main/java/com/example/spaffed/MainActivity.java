package com.example.spaffed;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.spotify.android.appremote.api.ConnectionParams;
import com.spotify.android.appremote.api.Connector;
import com.spotify.android.appremote.api.SpotifyAppRemote;
import com.spotify.protocol.client.Subscription;
import com.spotify.protocol.types.PlayerState;
import com.spotify.protocol.types.Track;
import com.spotify.sdk.android.auth.AuthorizationClient;
import com.spotify.sdk.android.auth.AuthorizationRequest;
import com.spotify.sdk.android.auth.AuthorizationResponse;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.TextView.OnEditorActionListener;
import androidx.appcompat.app.AppCompatActivity;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.textfield.TextInputEditText;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_CODE = 1337;
    private static final String REDIRECT_URI = "com.example.spaffed://auth";
    private static final String CLIENT_ID = "96cae4dc28e4467a8dffaa0c7b92135d";
    private SpotifyAppRemote mSpotifyAppRemote;

    private String openai_api_url = "https://api.openai.com/v1/completions";

    Button loginBtn;
    Button signupBtn;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getResponse("How do you feel?");

        // Check if user is already logged in
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        SharedPreferences sharedPreferences = getSharedPreferences("SpotifyAuth", MODE_PRIVATE);
        String userId = sharedPreferences.getString("userId", null);

        if (user != null || userId != null) {
            // User is already logged in, redirect to HomeActivity
            Intent intent = new Intent(MainActivity.this, HomeActivity.class);
            startActivity(intent);
            finish();
            return;
        }

        loginBtn = findViewById(R.id.login_button);
        signupBtn = findViewById(R.id.signup_button);

        loginBtn.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
        });

        signupBtn.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, SignupActivity.class);
            startActivity(intent);
        });
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    private void getResponse(String query) {
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("model", "babbage-002");
            jsonObject.put("prompt", query);
            jsonObject.put("temperature", 0);
            jsonObject.put("max_tokens", 100);
            jsonObject.put("top_p", 1);
            jsonObject.put("frequency_penalty", 0.0);
            jsonObject.put("presence_penalty", 0.0);
        } catch (Exception e) {
            e.printStackTrace();
        }

        JsonObjectRequest postRequest = new JsonObjectRequest(JsonObjectRequest.Method.POST, openai_api_url, jsonObject,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            String responseMsg = response.getJSONArray("choices").getJSONObject(0).getString("text");
                            Log.e("OPENAI API RESPONSE", "onResponse: " + responseMsg);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("TAGAPI", "Error is : " + error.getMessage() + "\n" + error);
                    }
                }) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> params = new HashMap<>();
                params.put("Content-Type", "application/json");
                params.put("Authorization", "Bearer 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");
                return params;
            }
        };

        postRequest.setRetryPolicy(new RetryPolicy() {
            @Override
            public int getCurrentTimeout() {
                return 50000;
            }

            @Override
            public int getCurrentRetryCount() {
                return 50000;
            }

            @Override
            public void retry(VolleyError error) throws VolleyError {
            }
        });

        queue.add(postRequest);
    }
}