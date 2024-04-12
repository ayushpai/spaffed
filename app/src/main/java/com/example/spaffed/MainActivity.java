package com.example.spaffed;

import android.content.Intent;
import android.os.Bundle;

import android.util.Log;
import android.view.Menu;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.spotify.android.appremote.api.ConnectionParams;
import com.spotify.android.appremote.api.Connector;
import com.spotify.android.appremote.api.SpotifyAppRemote;

import com.spotify.protocol.client.Subscription;
import com.spotify.protocol.types.PlayerState;
import com.spotify.protocol.types.Track;
import com.spotify.sdk.android.auth.AuthorizationClient;
import com.spotify.sdk.android.auth.AuthorizationRequest;
import com.spotify.sdk.android.auth.AuthorizationResponse;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_CODE = 1337;
    private static final String REDIRECT_URI = "http://localhost:8888/callback";
    private static final String CLIENT_ID = "96cae4dc28e4467a8dffaa0c7b92135d";
    private SpotifyAppRemote mSpotifyAppRemote;

    Button loginBtn;
    Button signupBtn;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //authenticateSpotify();



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


    private void authenticateSpotify() {
        AuthorizationRequest.Builder builder =
                new AuthorizationRequest.Builder(CLIENT_ID, AuthorizationResponse.Type.TOKEN, REDIRECT_URI);

        builder.setScopes(new String[]{"streaming"});
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


    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }



}