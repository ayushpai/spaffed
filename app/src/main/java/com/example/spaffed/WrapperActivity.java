package com.example.spaffed;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ViewFlipper;

public class WrapperActivity extends AppCompatActivity {

    private ViewFlipper viewFlipper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wrapper);

        viewFlipper = findViewById(R.id.wrappedFlipper);
    }

    public void previousView(View view) {
        viewFlipper.showPrevious();
    }

    public void nextView(View view) {
        viewFlipper.showNext();
    }
}