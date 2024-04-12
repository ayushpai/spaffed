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

public class HomeActivity extends AppCompatActivity {

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





    }

    private void generateSpotifyWrapped() {
        SharedPreferences sharedPreferences = getSharedPreferences("SpotifyAuth", MODE_PRIVATE);
        String mAccessToken = sharedPreferences.getString("mAccessToken", null);

    }
}