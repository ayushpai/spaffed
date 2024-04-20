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
import com.spotify.sdk.android.auth.AuthorizationClient;
import com.spotify.sdk.android.auth.AuthorizationRequest;
import com.spotify.sdk.android.auth.AuthorizationResponse;

public class EditProfileActivity extends AppCompatActivity {

    EditText editEmail;
    EditText editPassword;

    Button deleteAccountPage, confirmChanges;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        editEmail = findViewById(R.id.email);
        editPassword = findViewById(R.id.password);

        deleteAccountPage = findViewById(R.id.deleteAccountButton);
        confirmChanges = findViewById(R.id.confirmChangesButton);

        deleteAccountPage.setOnClickListener(v -> {
            // Redirect to DeleteAccount
            Intent intent = new Intent(EditProfileActivity.this, DeleteAccount.class);
            startActivity(intent);
        });


        //
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        confirmChanges.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // if email is not empty
                if (!editEmail.getText().toString().isEmpty()) {
                    user.updateEmail(editEmail.getText().toString())
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Log.d("Updated Email", "User email address updated.");
                                    }
                                    else {

                                        //get error message
                                        String error = task.getException().getMessage();
                                        Log.d("EMAIL ERROR", error);
                                        // invalid email - give a toast
                                        Toast.makeText(EditProfileActivity.this, "Invalid email", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }

                // if password is not empty
                if (!editPassword.getText().toString().isEmpty()) {
                    user.updatePassword(editPassword.getText().toString())
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {

                                        Log.d("Updated Password", "User password updated.");
                                        // toast

                                    }
                                    else {
                                        // invalid password - give a toast
                                        Toast.makeText(EditProfileActivity.this, "Invalid password", Toast.LENGTH_SHORT).show();
                                        // write to log
                                        String error = task.getException().getMessage();
                                        Log.d("PASSWORD ERROR", error);
                                    }
                                }
                            });
                }

            }
        });



    }


}