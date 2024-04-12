package com.example.spaffed;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        /*final EditText emailEditText = findViewById(R.id.emailEditText);
        final EditText passwordEditText = findViewById(R.id.passwordEditText);
        Button signUpButton = findViewById(R.id.loginButton);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                authenticateUser(emailEditText.getText().toString(), passwordEditText.getText().toString());
            }
        });


        private void authenticateUser(String email, String password) {
            // Hardcoded credentials for demonstration
            String hardcodedEmail = "user@example.com";
            String hardcodedPassword = "password123";

            if (email.equals(hardcodedEmail) && password.equals(hardcodedPassword)) {
                Toast.makeText(MainActivity.this, "Authentication successful", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(MainActivity.this, "Authentication failed", Toast.LENGTH_LONG).show();
            }
        }*/
    }
}