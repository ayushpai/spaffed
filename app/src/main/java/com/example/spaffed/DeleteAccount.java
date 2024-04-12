package com.example.spaffed;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class DeleteAccount extends AppCompatActivity {
    Button deleteBtn;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete_account);

        deleteBtn = findViewById(R.id.deleteAccountButton);
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Retrieve userId and mAccessToken from SharedPreferences
                SharedPreferences sharedPreferences = getSharedPreferences("SpotifyAuth", MODE_PRIVATE);
                String userId = sharedPreferences.getString("userId", null);
                String mAccessToken = sharedPreferences.getString("mAccessToken", null);

                if (userId != null && mAccessToken != null) {
                    // Delete user account
                    mAuth.getCurrentUser().delete();

                    // Delete document in Firestore
                    db.collection("users").document(mAccessToken).delete();

                    // Clear userId and mAccessToken from SharedPreferences
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.remove("userId");
                    editor.remove("mAccessToken");
                    editor.apply();

                    // Redirect to MainActivity
                    Intent intent = new Intent(DeleteAccount.this, MainActivity.class);
                    startActivity(intent);
                }
            }
        });
    }
}