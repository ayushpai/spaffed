package com.example.spaffed;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.spotify.android.appremote.api.SpotifyAppRemote;
import com.spotify.sdk.android.auth.AuthorizationClient;
import com.spotify.sdk.android.auth.AuthorizationRequest;
import com.spotify.sdk.android.auth.AuthorizationResponse;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import io.grpc.okhttp.internal.proxy.Request;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Response;

public class SpotifyLoginActivity extends AppCompatActivity {

    private static final int REQUEST_CODE = 1337;
    private static final String REDIRECT_URI = "com.example.spaffed://auth";
    private static final String CLIENT_ID = "96cae4dc28e4467a8dffaa0c7b92135d";


    private SpotifyAppRemote mSpotifyAppRemote;
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    Button spotifyButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spotify_login);

        spotifyButton = findViewById(R.id.spotify_button);
        spotifyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                authenticateSpotify();
            }
        });

    }

    private void authenticateSpotify() {
        AuthorizationRequest.Builder builder =
                new AuthorizationRequest.Builder(CLIENT_ID, AuthorizationResponse.Type.TOKEN, REDIRECT_URI);

        builder.setScopes(new String[]{"user-top-read", "user-read-email"});
        AuthorizationRequest request = builder.build();

        AuthorizationClient.openLoginActivity(this, REQUEST_CODE, request);



    }

    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        // Check if result comes from the correct activity
        if (requestCode == REQUEST_CODE) {
            AuthorizationResponse response = AuthorizationClient.getResponse(resultCode, intent);

            switch (response.getType()) {
                case TOKEN:
                    Log.d("SpotifyAuth", "Token received: " + response.getAccessToken());
                    // You can now use the token to make Spotify API calls
                    String mAccessToken = response.getAccessToken();
                    if (mAccessToken == null) {
                        Toast.makeText(this, "You need to get an access token first!", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    // Create a request to get the user profile
                    final okhttp3.Request request = new okhttp3.Request.Builder()
                            .url("https://api.spotify.com/v1/me")
                            .addHeader("Authorization", "Bearer " + mAccessToken)
                            .build();

                    OkHttpClient mOkHttpClient = new OkHttpClient();
                    Call mCall = mOkHttpClient.newCall(request);

                    mCall.enqueue(new Callback() {
                        @Override
                        public void onFailure(Call call, IOException e) {
                            Log.d("HTTP", "Failed to fetch data: " + e);
                            Toast.makeText(SpotifyLoginActivity.this, "Failed to fetch data, watch Logcat for more details",
                                    Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onResponse(Call call, Response response) throws IOException {
                            try {
                                final JSONObject jsonObject = new JSONObject(response.body().string());
                                Log.d("HTTP", "Received data: " + jsonObject);

                                // Extract display_name and email from the JSON object
                                String displayName = jsonObject.getString("display_name");
                                String email = jsonObject.getString("email");

                                // Create a HashMap to store user data
                                Map<String, Object> user = new HashMap<>();

                                user.put("display_name", displayName);
                                user.put("email", email);
                                user.put("access_token", mAccessToken);



                                // Create a DocumentReference pointing to a new document with ID mAccessToken
                                DocumentReference newUserRef = db.collection("users").document(mAccessToken);

                                newUserRef.set(user)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                Log.d("SpotifyLoginActivity", "DocumentSnapshot written with ID: " + mAccessToken);
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Log.w("SpotifyLoginActivity", "Error adding document", e);
                                            }
                                        });

                                // Navigate to the HomeActivity
                                Intent intent = new Intent(SpotifyLoginActivity.this, HomeActivity.class);
                                startActivity(intent);

                            } catch (JSONException e) {
                                Log.d("JSON", "Failed to parse data: " + e);

                            }
                        }
                    });


                    break;
                case ERROR:
                    Log.e("SpotifyAuth", "Authentication error: " + response.getError());
                    break;
                default:
                    Log.w("SpotifyAuth", "Received response of unknown type: " + response.getType());
            }
        } else {
            Log.w("SpotifyAuth", "Unexpected request code: " + requestCode);
        }
    }
}