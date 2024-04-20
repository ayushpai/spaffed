package com.example.spaffed;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class WrapperActivity extends AppCompatActivity {

    private ViewFlipper viewFlipper;
    private FirebaseFirestore db;
    private String mAccessToken;

    private TextView shortLLMText, mediumLLMText, longLLMText;
    TextView shortTrack1, shortTrack2, shortTrack3, shortTrack4, shortTrack5;
    TextView mediumTrack1, mediumTrack2, mediumTrack3, mediumTrack4, mediumTrack5;
    TextView longTrack1, longTrack2, longTrack3, longTrack4, longTrack5;
    ImageView shortTrackImg, mediumTrackImg, longTrackImg;

    TextView shortArtist1, shortArtist2, shortArtist3, shortArtist4, shortArtist5;
    TextView mediumArtist1, mediumArtist2, mediumArtist3, mediumArtist4, mediumArtist5;
    TextView longArtist1, longArtist2, longArtist3, longArtist4, longArtist5;
    ImageView shortArtistImg, mediumArtistImg, longArtistImg;

    LinearLayout shortTermLayout, mediumTermLayout, longTermLayout;

    Button backButton;

    private String openai_api_url = "https://api.openai.com/v1/completions";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wrapper);

        viewFlipper = findViewById(R.id.wrappedFlipper);
        db = FirebaseFirestore.getInstance();

        // Retrieve mAccessToken from SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("SpotifyAuth", MODE_PRIVATE);
        mAccessToken = sharedPreferences.getString("mAccessToken", null);

        // Add more time frames as needed

        backButton = findViewById(R.id.backButton);

        shortTrack1 = findViewById(R.id.short_track_1);
        shortTrack2 = findViewById(R.id.short_track_2);
        shortTrack3 = findViewById(R.id.short_track_3);
        shortTrack4 = findViewById(R.id.short_track_4);
        shortTrack5 = findViewById(R.id.short_track_5);
        shortTrackImg = findViewById(R.id.short_track_img);

        mediumTrack1 = findViewById(R.id.medium_track_1);
        mediumTrack2 = findViewById(R.id.medium_track_2);
        mediumTrack3 = findViewById(R.id.medium_track_3);
        mediumTrack4 = findViewById(R.id.medium_track_4);
        mediumTrack5 = findViewById(R.id.medium_track_5);
        mediumTrackImg = findViewById(R.id.medium_track_img);

        longTrack1 = findViewById(R.id.long_track_1);
        longTrack2 = findViewById(R.id.long_track_2);
        longTrack3 = findViewById(R.id.long_track_3);
        longTrack4 = findViewById(R.id.long_track_4);
        longTrack5 = findViewById(R.id.long_track_5);
        longTrackImg = findViewById(R.id.long_track_img);

        shortArtist1 = findViewById(R.id.short_artist_1);
        shortArtist2 = findViewById(R.id.short_artist_2);
        shortArtist3 = findViewById(R.id.short_artist_3);
        shortArtist4 = findViewById(R.id.short_artist_4);
        shortArtist5 = findViewById(R.id.short_artist_5);
        shortArtistImg = findViewById(R.id.short_artist_img);

        mediumArtist1 = findViewById(R.id.medium_artist_1);
        mediumArtist2 = findViewById(R.id.medium_artist_2);
        mediumArtist3 = findViewById(R.id.medium_artist_3);
        mediumArtist4 = findViewById(R.id.medium_artist_4);
        mediumArtist5 = findViewById(R.id.medium_artist_5);
        mediumArtistImg = findViewById(R.id.medium_artist_img);

        longArtist1 = findViewById(R.id.long_artist_1);
        longArtist2 = findViewById(R.id.long_artist_2);
        longArtist3 = findViewById(R.id.long_artist_3);
        longArtist4 = findViewById(R.id.long_artist_4);
        longArtist5 = findViewById(R.id.long_artist_5);
        longArtistImg = findViewById(R.id.long_artist_img);


        shortLLMText = findViewById(R.id.short_top_genre_text);
        mediumLLMText = findViewById(R.id.medium_top_genre_text);
        longLLMText = findViewById(R.id.long_top_genre_text);

        shortTermLayout = findViewById(R.id.short_term_view);
        mediumTermLayout = findViewById(R.id.medium_term_view);
        longTermLayout = findViewById(R.id.long_term_view);



        // waits 5 second before running the method
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                // get the text of short term tracks ranking and the short term artists ranking
                String shortTermPrompt = "Based on the users top tracks and artists in the last week, write a fun response about their music taste." + "\nUser Top Tracks (in order): " + shortTrack1.getText().toString() + ", " + shortTrack2.getText().toString() + ", " + shortTrack3.getText().toString() + ", " + shortTrack4.getText().toString() + ", " + shortTrack5.getText().toString() + "\nUser Top Artists (in order): " + shortArtist1.getText().toString() + ", " + shortArtist2.getText().toString() + ", " + shortArtist3.getText().toString() + ", " + shortArtist4.getText().toString() + ", " + shortArtist5.getText().toString();
                String mediumTermPrompt = "Based on the users top tracks and artists in the last month, write a fun response about their music taste." + "\nUser Top Tracks (in order): " + mediumTrack1.getText().toString() + ", " + mediumTrack2.getText().toString() + ", " + mediumTrack3.getText().toString() + ", " + mediumTrack4.getText().toString() + ", " + mediumTrack5.getText().toString() + "\nUser Top Artists (in order): " + mediumArtist1.getText().toString() + ", " + mediumArtist2.getText().toString() + ", " + mediumArtist3.getText().toString() + ", " + mediumArtist4.getText().toString() + ", " + mediumArtist5.getText().toString();
                String longTermPrompt = "Based on the users top tracks and artists in the last 6 months, write a fun response about their music taste." + "\nUser Top Tracks (in order): " + longTrack1.getText().toString() + ", " + longTrack2.getText().toString() + ", " + longTrack3.getText().toString() + ", " + longTrack4.getText().toString() + ", " + longTrack5.getText().toString() + "\nUser Top Artists (in order): " + longArtist1.getText().toString() + ", " + longArtist2.getText().toString() + ", " + longArtist3.getText().toString() + ", " + longArtist4.getText().toString() + ", " + longArtist5.getText().toString();
                updateTextWithLLM(shortTermPrompt, "short-term");
                updateTextWithLLM(mediumTermPrompt, "medium-term");
                updateTextWithLLM(longTermPrompt, "long-term");

                //saveView(shortTermLayout);
                //saveView(mediumTermLayout);
                saveView(longTermLayout);

            }
        }, 4000);   //5 seconds







        String timeStampId = sharedPreferences.getString("timestamp", null);
        String wrapperUserId = sharedPreferences.getString("wrapper_user_id", null);
        Log.d("Wrapper USER ID", wrapperUserId);
        Log.d("Wrapper TIMESTAMP", timeStampId);


        // retrieve short term tracks from firestore
        // users -> mAccessToken -> spotifyData (timestamps) -> terms (medium_term) -> tracks -> topTracks, topTrackImageUrl
        db.collection("users").document(wrapperUserId).collection("spotifyData").document(timeStampId).collection("short_term").document("tracks").get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Map<String, Object> data = document.getData();
                        List<String> topTracks = (List<String>) data.get("topTracks");
                        String topTrackImageUrl = (String) data.get("topImageUrl");
                        Picasso.get().load(topTrackImageUrl).into(shortTrackImg);

                        shortTrack1.setText("1) " + topTracks.get(0));
                        shortTrack2.setText("2) " + topTracks.get(1));
                        shortTrack3.setText("3) " + topTracks.get(2));
                        shortTrack4.setText("4) " + topTracks.get(3));
                        shortTrack5.setText("5) " + topTracks.get(4));
                    }
                }
            }
        });

        // retrieve short term artists from firestore
        db.collection("users").document(wrapperUserId).collection("spotifyData").document(timeStampId).collection("short_term").document("artists").get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Map<String, Object> data = document.getData();
                        List<String> topArtists = (List<String>) data.get("topArtists");
                        String topArtistImageUrl = (String) data.get("topImageUrl");
                        Picasso.get().load(topArtistImageUrl).into(shortArtistImg);
                        shortArtist1.setText("1) " + topArtists.get(0));
                        shortArtist2.setText("2) " + topArtists.get(1));
                        shortArtist3.setText("3) " + topArtists.get(2));
                        shortArtist4.setText("4) " + topArtists.get(3));
                        shortArtist5.setText("5) " + topArtists.get(4));

                    }
                }
            }
        });

        // retrieve medium term tracks from firestore
        db.collection("users").document(wrapperUserId).collection("spotifyData").document(timeStampId).collection("medium_term").document("tracks").get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Map<String, Object> data = document.getData();
                        List<String> topTracks = (List<String>) data.get("topTracks");
                        String topTrackImageUrl = (String) data.get("topImageUrl");
                        Picasso.get().load(topTrackImageUrl).into(mediumTrackImg);
                        mediumTrack1.setText("1) " + topTracks.get(0));
                        mediumTrack2.setText("2) " + topTracks.get(1));
                        mediumTrack3.setText("3) " + topTracks.get(2));
                        mediumTrack4.setText("4) " + topTracks.get(3));
                        mediumTrack5.setText("5) " + topTracks.get(4));
                    }
                }
            }
        });

        // retrieve medium term artists from firestore
        db.collection("users").document(wrapperUserId).collection("spotifyData").document(timeStampId).collection("medium_term").document("artists").get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Map<String, Object> data = document.getData();
                        List<String> topArtists = (List<String>) data.get("topArtists");
                        String topArtistImageUrl = (String) data.get("topImageUrl");
                        Picasso.get().load(topArtistImageUrl).into(mediumArtistImg);
                        mediumArtist1.setText("1) " + topArtists.get(0));
                        mediumArtist2.setText("2) " + topArtists.get(1));
                        mediumArtist3.setText("3) " + topArtists.get(2));
                        mediumArtist4.setText("4) " + topArtists.get(3));
                        mediumArtist5.setText("5) " + topArtists.get(4));
                    }
                }
            }
        });

        // retrieve long term tracks from firestore
        db.collection("users").document(wrapperUserId).collection("spotifyData").document(timeStampId).collection("long_term").document("tracks").get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Map<String, Object> data = document.getData();
                        List<String> topTracks = (List<String>) data.get("topTracks");
                        String topTrackImageUrl = (String) data.get("topImageUrl");
                        Picasso.get().load(topTrackImageUrl).into(longTrackImg);
                        longTrack1.setText("1) " + topTracks.get(0));
                        longTrack2.setText("2) " + topTracks.get(1));
                        longTrack3.setText("3) " + topTracks.get(2));
                        longTrack4.setText("4) " + topTracks.get(3));
                        longTrack5.setText("5) " + topTracks.get(4));
                    }
                }
            }
        });



        // retrieve long term artists from firestore
        db.collection("users").document(wrapperUserId).collection("spotifyData").document(timeStampId).collection("long_term").document("artists").get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Map<String, Object> data = document.getData();
                        List<String> topArtists = (List<String>) data.get("topArtists");
                        String topArtistImageUrl = (String) data.get("topImageUrl");
                        Picasso.get().load(topArtistImageUrl).into(longArtistImg);
                        longArtist1.setText("1) " + topArtists.get(0));
                        longArtist2.setText("2) " + topArtists.get(1));
                        longArtist3.setText("3) " + topArtists.get(2));
                        longArtist4.setText("4) " + topArtists.get(3));
                        longArtist5.setText("5) " + topArtists.get(4));
                    }
                }
            }
        });


        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // open home activity
                finish();
            }
        });






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
            jsonObject.put("temperature", 1);
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

    private void saveView(View v){
        File mediaStorageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        if(mediaStorageDir.exists()){
            Random r = new Random();
            int num = r.nextInt(1000000000);
            String timeString = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            String imageName = "IMG_" + num + ".png";
            String selectedOutputPath = mediaStorageDir.getPath() + File.separator + imageName;

            // based on the view switch the visibility of the text

            if (v == shortTermLayout) {
                shortLLMText.setVisibility(View.INVISIBLE);
            } else if (v == mediumTermLayout) {
                mediumLLMText.setVisibility(View.INVISIBLE);
            } else {
                longLLMText.setVisibility(View.INVISIBLE);
            }

            // set background color to white
            v.setBackgroundColor(getResources().getColor(android.R.color.white));
            // add margin of the view


            v.setDrawingCacheEnabled(true);
            v.buildDrawingCache();
            Bitmap bm = Bitmap.createBitmap(v.getDrawingCache());
            v.setDrawingCacheEnabled(false);

            v.destroyDrawingCache();
            OutputStream f = null;
            try{
                File file = new File(selectedOutputPath);
                f = new FileOutputStream(file);
                bm.compress(Bitmap.CompressFormat.JPEG, 100, f);
                f.flush();
                f.close();
                System.out.println("Downloaded");

            } catch(Exception e){
                e.printStackTrace();
            }
            shortLLMText.setVisibility(View.VISIBLE);
            mediumLLMText.setVisibility(View.VISIBLE);
            longLLMText.setVisibility(View.VISIBLE);
            // set it back to what it was
            v.setBackgroundColor(getResources().getColor(android.R.color.transparent));
            // remove the padding
        }
        listDir(mediaStorageDir);
    }
    private void listDir(File path){
        for(File child: path.listFiles()){
            System.err.println(child.toString());
        }
    }

}