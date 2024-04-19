package com.example.spaffed;

import androidx.appcompat.app.AppCompatActivity;
import com.spotify.android.appremote.api.ConnectionParams;
import com.spotify.android.appremote.api.Connector;
import com.spotify.android.appremote.api.SpotifyAppRemote;

import com.spotify.protocol.client.Subscription;
import com.spotify.protocol.types.PlayerState;
import com.spotify.protocol.types.Track;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

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
import java.util.concurrent.CountDownLatch;

public class HomeActivity extends AppCompatActivity {

    private String openai_api_url = "https://api.openai.com/v1/completions";

    Button editProfilePage;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // Retrieve mAccessToken from SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("SpotifyAuth", MODE_PRIVATE);
        String mAccessToken = sharedPreferences.getString("mAccessToken", null);

        editProfilePage = findViewById(R.id.edit_account);
        editProfilePage.setOnClickListener(v -> {
            // Redirect to EditProfileActivity
            Intent intent = new Intent(HomeActivity.this, EditProfileActivity.class);
            startActivity(intent);
        });

        Log.d("PRE-OPENAI", "nothing to see");
        try {
            getResponse("How can I make a salad?");
        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.d("POST-OPENAI", "nothing to see");
    }

    private void generateSpotifyWrapped() {
        SharedPreferences sharedPreferences = getSharedPreferences("SpotifyAuth", MODE_PRIVATE);
        String mAccessToken = sharedPreferences.getString("mAccessToken", null);

    }

    private void getResponse(String query) {
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("model", "gpt-3.5-turbo-instruct");
            jsonObject.put("prompt", query);
            jsonObject.put("temperature", 0.7);
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
                            Log.d("OPENAI API RESPONSE", "onResponse: " + responseMsg);
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

    private String getResponseWait(String query) {
        final String[] responseMsg = {null};
        final CountDownLatch latch = new CountDownLatch(1);

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
            return null;
        }

        JsonObjectRequest postRequest = new JsonObjectRequest(JsonObjectRequest.Method.POST, openai_api_url, jsonObject,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            responseMsg[0] = response.getJSONArray("choices").getJSONObject(0).getString("text");
                            Log.d("OPENAI API RESPONSE", "onResponse: " + responseMsg[0]);
                        } catch (Exception e) {
                            e.printStackTrace();
                        } finally {
                            latch.countDown();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("TAGAPI", "Error is : " + error.getMessage() + "\n" + error);
                        latch.countDown();
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

        try {
            latch.await(); // Wait for the response to be received
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return responseMsg[0];
    }

}