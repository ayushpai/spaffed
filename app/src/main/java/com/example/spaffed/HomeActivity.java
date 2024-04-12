package com.example.spaffed;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class HomeActivity extends AppCompatActivity {

    Button editProfilePage;
    Button generateWrapped;

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

        generateWrapped = findViewById(R.id.wrapped_button);
        generateWrapped.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                generateSpotifyWrapped();
            }
        });
    }

    private void generateSpotifyWrapped() {
        SharedPreferences sharedPreferences = getSharedPreferences("SpotifyAuth", MODE_PRIVATE);
        String mAccessToken = sharedPreferences.getString("mAccessToken", null);

        // Use the current timestamp as the document name
        String timestamp = String.valueOf(System.currentTimeMillis());

        generateSpotifyWrappedForTimeRange("short_term", mAccessToken, timestamp);
        generateSpotifyWrappedForTimeRange("long_term", mAccessToken, timestamp);

    }

    private void generateSpotifyWrappedForTimeRange(String timeRange, String mAccessToken, String timestamp) {
        // Generate Spotify Wrapped
        // Create a request to get the top artists
        final Request requestArtists = new Request.Builder()
                .url("https://api.spotify.com/v1/me/top/artists?time_range=" + timeRange + "&limit=5")
                .addHeader("Authorization", "Bearer " + mAccessToken)
                .build();

        // Create a request to get the top tracks
        final Request requestTracks = new Request.Builder()
                .url("https://api.spotify.com/v1/me/top/tracks?time_range=" + timeRange + "&limit=5")
                .addHeader("Authorization", "Bearer " + mAccessToken)
                .build();

        OkHttpClient mOkHttpClient = new OkHttpClient();
        Call mCallArtists = mOkHttpClient.newCall(requestArtists);
        Call mCallTracks = mOkHttpClient.newCall(requestTracks);

        mCallArtists.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d("HTTP", "Failed to fetch data: " + e);
                Toast.makeText(HomeActivity.this, "Failed to fetch data, watch Logcat for more details",
                        Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    final JSONObject jsonObject = new JSONObject(response.body().string());
                    Log.d("JSON", "Received data: " + jsonObject.toString());

                    // Parse the JSON response
                    JSONArray items = jsonObject.getJSONArray("items");

                    // Get the names of the top artists and the image URL of the #1 top artist
                    List<String> topArtists = new ArrayList<>();
                    String topArtistImageUrl = null;

                    for (int i = 0; i < items.length(); i++) {
                        JSONObject item = items.getJSONObject(i);
                        String artistName = item.getString("name");
                        topArtists.add(artistName);

                        if (i == 0) { // #1 top artist
                            JSONArray images = item.getJSONArray("images");
                            if (images.length() > 0) {
                                topArtistImageUrl = images.getJSONObject(0).getString("url"); // Get the URL of the first image
                            }
                        }
                    }

                    // Save the data to Firestore
                    FirebaseFirestore db = FirebaseFirestore.getInstance();
                    Map<String, Object> data = new HashMap<>();
                    data.put("topArtists", topArtists);
                    data.put("topArtistImageUrl", topArtistImageUrl);

                    Log.d("Firestore", "Writing data to Firestore: " + data);

                    db.collection("users").document(mAccessToken).collection("spotifyData").document(timestamp)
                            .collection(timeRange)
                            .document("artists")
                            .set(data)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Log.d("Firestore", "DocumentSnapshot successfully written!");
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.w("Firestore", "Error writing document", e);
                                }
                            });

                } catch (JSONException e) {
                    Log.d("JSON", "Failed to parse data: " + e);
                }
            }
        });

        mCallTracks.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d("HTTP", "Failed to fetch data: " + e);
                Toast.makeText(HomeActivity.this, "Failed to fetch data, watch Logcat for more details",
                        Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    final JSONObject jsonObject = new JSONObject(response.body().string());
                    Log.d("JSON", "Received data: " + jsonObject.toString());

                    // Parse the JSON response
                    JSONArray items = jsonObject.getJSONArray("items");

                    // Get the names of the top tracks and the image URL of the #1 top track
                    List<String> topTracks = new ArrayList<>();
                    String topTrackImageUrl = null;

                    for (int i = 0; i < items.length(); i++) {
                        JSONObject item = items.getJSONObject(i);
                        String trackName = item.getString("name");
                        topTracks.add(trackName);

                        if (i == 0) { // #1 top track
                            JSONObject album = item.getJSONObject("album");
                            JSONArray images = album.getJSONArray("images");
                            if (images.length() > 0) {
                                topTrackImageUrl = images.getJSONObject(0).getString("url"); // Get the URL of the first image
                            }
                        }
                    }

                    // Save the data to Firestore
                    FirebaseFirestore db = FirebaseFirestore.getInstance();
                    Map<String, Object> data = new HashMap<>();
                    data.put("topTracks", topTracks);
                    data.put("topTrackImageUrl", topTrackImageUrl);

                    Log.d("Firestore", "Writing data to Firestore: " + data);

                    db.collection("users").document(mAccessToken).collection("spotifyData").document(timestamp)
                            .collection(timeRange)
                            .document("tracks")
                            .set(data)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Log.d("Firestore", "DocumentSnapshot successfully written!");
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.w("Firestore", "Error writing document", e);
                                }
                            });

                } catch (JSONException e) {
                    Log.d("JSON", "Failed to parse data: " + e);
                }
            }
        });
    }
}