package com.example.spaffed;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.content.SharedPreferences;
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

public class PublicWrappersActivity extends AppCompatActivity {

    private FirebaseFirestore db;
    private String mAccessToken;

    private long[] firebaseIds = new long[8];
    private int num_docs = 0;
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
                .document("BQAUo8aJTWmYzrdB-W25H7bRJ-1-BY1B40x5X2WQt1vhjcQaKZx_hGOb68Wc4PdbGrlxXSLJgFelBWs0lxijmfM0Pa-SSRP4Odz3FD1n4rR1DQLNBj9ROKP9g0qHPuG_bQkg9FWZNGG8o1srX3XeFtcKKOAe4PsxROUzFNgmWNuJGRU7_YCyXdiZB7AU6Q")
                .collection("spotifyData")
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            QuerySnapshot queryDocumentSnapshots = task.getResult();
                            Log.d("FIREBASE QUERY DOC", "onComplete: " + task.getResult().size());
                            int i = 0;
                            for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                                if (!documentSnapshot.exists()) {
                                    break;
                                }
                                firebaseIds[i] = Long.valueOf(documentSnapshot.getId());

                                String[] terms = {"short_term", "medium_term", "long_term"};
                                String[] options = {"artists", "tracks"};

                                Random random = new Random();
                                String term = terms[random.nextInt(terms.length)];
                                String option = options[random.nextInt(options.length)];

                                int finalI = i;
                                documentSnapshot.getReference()
                                        .collection(term)
                                        .document(option)
                                        .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                if (task.isSuccessful()) {
                                                    DocumentSnapshot document = task.getResult();
                                                    Map<String, Object> data = document.getData();
                                                    assert data != null;
                                                    String topArtistImageUrl = (String) data.get("topImageUrl");
                                                    // Display image
                                                    ImageView img = findViewById(imageIds[finalI]);
                                                    Log.d("FINAL IMAGE", "onComplete: " + topArtistImageUrl);
                                                    Picasso.get().load(topArtistImageUrl).into(img);
                                                } else {
                                                    Log.d("fail", "UNSUCESS");
                                                }
                                            }
                                        });

                                TextView title = findViewById(textIds[i]);
                                long millis = Long.parseLong(documentSnapshot.getId());
                                Date date = new Date(millis);
                                SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yyyy\nhh:mm:ss a");
                                String formattedDate = sdf.format(date);
                                title.setText(formattedDate);

                                i++;
                                num_docs = i;
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
                    editor.apply();
                    Intent intent = new Intent(PublicWrappersActivity.this, WrapperActivity.class);
                    startActivity(intent);
                }
            });
        }
    }
}