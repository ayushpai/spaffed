package com.example.spaffed;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.media.Image;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.widget.ViewFlipper;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Date;
import java.util.Map;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
import java.util.Map;
import java.util.Random;
import java.text.SimpleDateFormat;

import com.google.firebase.firestore.Query;

public class PublicWrappersActivity extends AppCompatActivity {

    private FirebaseFirestore db;
    private String mAccessToken;
    private String[] userIds = new String[8];
    private long[] firebaseIds = new long[8];
    private int[] cardIds = {R.id.card_1, R.id.card_2, R.id.card_3, R.id.card_4, R.id.card_5, R.id.card_6, R.id.card_7, R.id.card_8};
    private int[] imageIds = {R.id.header_image_1, R.id.header_image_2, R.id.header_image_3, R.id.header_image_4, R.id.header_image_5, R.id.header_image_6, R.id.header_image_7, R.id.header_image_8};
    private int[] textIds =  {R.id.title_1, R.id.title_2, R.id.title_3, R.id.title_4, R.id.title_5, R.id.title_6, R.id.title_7, R.id.title_8};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_public_wrappers);

        db = FirebaseFirestore.getInstance();

        // Retrieve mAccessToken from SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("SpotifyAuth", MODE_PRIVATE);
        mAccessToken = sharedPreferences.getString("mAccessToken", null);

        // get all of the data from users -> mAccessToken -> spotifyData (timestamps) and log it
        db.collection("users")
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            QuerySnapshot queryDocumentSnapshots = task.getResult();
                            Log.d("FIREBASE QUERY DOC", "onComplete: " + task.getResult().size());
                            int i = 0;
                            // For each user
                            for (QueryDocumentSnapshot userDocument : queryDocumentSnapshots) {
                                if (!userDocument.exists()) {
                                    break;
                                }
                                userIds[i] = userDocument.getId();

                                String[] terms = {"short_term", "medium_term", "long_term"};
                                String[] options = {"artists", "tracks"};

                                Random random = new Random();
                                String term = terms[random.nextInt(terms.length)];
                                String option = options[random.nextInt(options.length)];

                                Log.d("TAGGGG", "onComplete: " + userDocument.getReference().toString());

                                // FETCH MOST RECENT WRAPPED FROM USER DOC
                                // Inside the loop
                                int finalI = i;
                                userDocument.getReference().collection("spotifyData")
                                        .get()
                                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                if (task.isSuccessful()) {
                                                    Log.d("CHECK ME OUT", "onComplete: " + task.getResult().getDocuments().toString());
                                                    if (task.getResult().getDocuments().size() > 0) {
                                                        DocumentSnapshot wrappedDocument = null;
                                                        for (DocumentSnapshot doc : task.getResult().getDocuments()) {
                                                            if (doc.getBoolean("public") == true) {
                                                                wrappedDocument = doc;
                                                                break;
                                                            }
                                                        }
                                                        if (wrappedDocument != null) {
                                                            firebaseIds[finalI] = Long.valueOf(wrappedDocument.getId());
                                                            String term = terms[random.nextInt(terms.length)];
                                                            String option = options[random.nextInt(options.length)];
                                                            wrappedDocument.getReference()
                                                                    .collection(term)
                                                                    .document(option)
                                                                    .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                                        @Override
                                                                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                                            if (task.isSuccessful()) {
                                                                                DocumentSnapshot document = task.getResult();
                                                                                Map<String, Object> data = document.getData();
                                                                                assert data != null;
                                                                                String topImageUrl = (String) data.get("topImageUrl");
                                                                                // Display image
                                                                                ImageView img = findViewById(imageIds[finalI]);
                                                                                Log.d("FINAL IMAGE", "onComplete: " + topImageUrl);
                                                                                Picasso.get().load(topImageUrl).into(img);
                                                                            } else {
                                                                                Log.d("fail", "UNSUCESS");
                                                                            }
                                                                        }
                                                                    });
                                                        } else {
                                                            ImageView img = findViewById(imageIds[finalI]);
                                                            TextView title = findViewById(textIds[finalI]);
                                                            CardView card = findViewById(cardIds[finalI]);
                                                            img.setVisibility(View.GONE);
                                                            title.setVisibility(View.GONE);
                                                            card.setVisibility(View.GONE);
                                                        }
                                                    }
                                                } else {
                                                    Log.e("TAG", "Error getting artists document for user " + userDocument.getId(), task.getException());
                                                }
                                            }
                                        });

                                TextView title = findViewById(textIds[i]);
                                title.setText(userDocument.getString("display_name"));

                                i++;
                                if (i >= cardIds.length) {
                                    break; // Break the loop if we have initialized all cards
                                }
                            }

                            // Remaining images
                            for(;i < cardIds.length; i++) {
                                ImageView img = findViewById(imageIds[i]);
                                TextView title = findViewById(textIds[i]);
                                CardView card = findViewById(cardIds[i]);
                                img.setVisibility(View.GONE);
                                title.setVisibility(View.GONE);
                                card.setVisibility(View.GONE);
                            }
                        }
                    }
                });

        for (int i = 0; i < cardIds.length; i++) {
            int cardId = cardIds[i];
            int finalI = i;
            findViewById(cardId).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d("CLICK", "onClick: ");
                    SharedPreferences sharedPreferences =  getSharedPreferences("SpotifyAuth",MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("timestamp", String.valueOf(firebaseIds[finalI]));

                    editor.putString("wrapper_user_id", userIds[finalI]);
                    editor.apply();
                    Intent intent = new Intent(PublicWrappersActivity.this, WrapperActivity.class);
                    startActivity(intent);
                }
            });
        }
    }
}