package com.example.hashqiptiplako;


import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class GameOnActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game_on);
        TextView gameWordTextView = findViewById(R.id.gameWord);
        String randomWord = getIntent().getStringExtra("randomWord");
        //TextView randomTextView = findViewById(R.id.gameHint);
        //randomTextView.setText(randomWord);
        String secretWord = getIntent().getStringExtra("secretWord");
        gameWordTextView.setText(secretWord);
        int i = getIntent().getIntExtra("length", 1);
            i = i == 8 ? 10: i+1;

        int[] hTextViewIds = { R.id.h0, R.id.h1, R.id.h2, R.id.h3, R.id.h4, R.id.h5, R.id.h6, R.id.h7, R.id.h8, R.id.h9 };

        for (int k = 0; k < i; k++) {
            TextView hTextView = findViewById(hTextViewIds[k]);
            hTextView.setVisibility(View.VISIBLE);

        }
        if (i == 7) {
            TextView h8TextView = findViewById(R.id.h7);
            h8TextView.setVisibility(View.VISIBLE);
        }

        TextInputLayout inputLayout = findViewById(R.id.inputLayout);
        TextInputEditText inputEditText = findViewById(R.id.inputEditText);
        InputListener listen = (new InputListener(inputLayout, this, randomWord, i, hTextViewIds));

        for (int j = 0; j < i; j++) {
            inputEditText.setOnEditorActionListener(listen);
        }
    }
}
