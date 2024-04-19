package com.example.spaffed;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class WrapperActivity extends AppCompatActivity {

    private ViewFlipper viewFlipper;

    public String full_name;

    private TextView shortLLMText, mediumLLMText, longLLMText;

    private String openai_api_url = "https://api.openai.com/v1/completions";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wrapper);

        viewFlipper = findViewById(R.id.wrappedFlipper);

        shortLLMText = findViewById(R.id.short_top_genre_text);
        mediumLLMText = findViewById(R.id.medium_top_genre_text);
        longLLMText = findViewById(R.id.long_top_genre_text);

        // waits 5 second before running the method
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                updateTextWithLLM("Human is to burger as pineapple is to what? Explain.", "short-term");
                updateTextWithLLM("Sing for me.", "medium-term");
                updateTextWithLLM("Cachow. So uh how you doin?", "long-term");
            }
        }, 5000);   //5 seconds
    }

    public void previousView(View view) {
        viewFlipper.showPrevious();
    }

    public void nextView(View view) {
        viewFlipper.showNext();
    }

    private void updateTextWithLLM(String query, String id) {
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
                            if (id.equals("short-term")) {
                                shortLLMText.setText(responseMsg);
                            } else if (id.equals("medium-term")) {
                                mediumLLMText.setText(responseMsg);
                            } else if (id.equals("long-term")) {
                                longLLMText.setText(responseMsg);
                            }
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
}