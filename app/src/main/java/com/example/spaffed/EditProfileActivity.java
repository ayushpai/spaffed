package com.example.spaffed;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.spotify.sdk.android.auth.AuthorizationClient;
import com.spotify.sdk.android.auth.AuthorizationRequest;
import com.spotify.sdk.android.auth.AuthorizationResponse;

public class EditProfileActivity extends AppCompatActivity {

    EditText editEmail;
    EditText editPassword;


    EditText newPassword;

    Button deleteAccountPage, confirmChanges, logoutButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        editEmail = findViewById(R.id.email);
        editPassword = findViewById(R.id.password);
        newPassword = findViewById(R.id.newPassword);

        deleteAccountPage = findViewById(R.id.deleteAccountButton);
        confirmChanges = findViewById(R.id.confirmChangesButton);
        logoutButton = findViewById(R.id.logoutButton);

        deleteAccountPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Retrieve userId and mAccessToken from SharedPreferences
                FirebaseAuth.getInstance().getCurrentUser().delete();
                SharedPreferences sharedPreferences = getSharedPreferences("SpotifyAuth", MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.remove("userId");
                editor.apply();


                FirebaseFirestore db = FirebaseFirestore.getInstance();
                String mAccessToken = sharedPreferences.getString("mAccessToken", null);
                db.collection("users").document(mAccessToken).delete();

                Intent intent = new Intent(EditProfileActivity.this, LoginActivity.class);
                startActivity(intent);

            }
        });

        logoutButton.setOnClickListener(v -> {

            FirebaseAuth.getInstance().signOut();
            SharedPreferences sharedPreferences = getSharedPreferences("SpotifyAuth", MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.remove("userId");
            editor.apply();
            Intent intent = new Intent(EditProfileActivity.this, MainActivity.class);
            startActivity(intent);


        });


        //
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        confirmChanges.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // First reauthenticate the user based on the current email and password
                String email = editEmail.getText().toString();
                String password = editPassword.getText().toString();
                String newPasswordString = newPassword.getText().toString();

                if (email.isEmpty() || password.isEmpty()) {
                    Toast.makeText(EditProfileActivity.this, "Please enter your current credentials", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (newPasswordString.isEmpty()) {
                    Toast.makeText(EditProfileActivity.this, "Please enter a new password", Toast.LENGTH_SHORT).show();
                    return;
                }


                user.reauthenticate(com.google.firebase.auth.EmailAuthProvider.getCredential(email, password))
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                // If reauthentication is successful, update the password
                                if (task.isSuccessful()) {
                                    user.updatePassword(newPasswordString).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                Toast.makeText(EditProfileActivity.this, "Password updated", Toast.LENGTH_SHORT).show();
                                            } else {
                                                Toast.makeText(EditProfileActivity.this, "Password update failed", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });

                                } else {
                                    Toast.makeText(EditProfileActivity.this, "Reauthentication failed", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });


            }
        });



    }


}