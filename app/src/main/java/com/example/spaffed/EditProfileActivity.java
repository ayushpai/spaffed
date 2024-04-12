package com.example.spaffed;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;

import com.spotify.sdk.android.auth.AuthorizationClient;
import com.spotify.sdk.android.auth.AuthorizationRequest;
import com.spotify.sdk.android.auth.AuthorizationResponse;

public class EditProfileActivity extends AppCompatActivity {

    EditText editEmail;
    EditText editPassword;

    Button deleteAccountPage;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        editEmail = findViewById(R.id.email);
        editPassword = findViewById(R.id.password);

        deleteAccountPage = findViewById(R.id.deleteAccountButton);



        deleteAccountPage.setOnClickListener(v -> {
            // Redirect to DeleteAccount
            Intent intent = new Intent(EditProfileActivity.this, DeleteAccount.class);
            startActivity(intent);
        });



    }


}