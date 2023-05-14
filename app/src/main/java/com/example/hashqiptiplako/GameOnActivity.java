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

        for (int k = 0; k < 7; k++) {
            int hViewId = getResources().getIdentifier("h" + (k+1), "id", getPackageName());
            TextView hTextView = findViewById(hViewId);
            if (k < i) {
                hTextView.setVisibility(View.VISIBLE);
            } else {
                hTextView.setVisibility(View.INVISIBLE);
            }
        }
        if (i == 7) {
            int hViewId = getResources().getIdentifier("h7", "id", getPackageName());
            TextView hTextView = findViewById(hViewId);
            hTextView.setVisibility(View.VISIBLE);
        }






        TextInputLayout inputLayout = findViewById(R.id.inputLayout);
        TextInputEditText inputEditText = findViewById(R.id.inputEditText);
        InputListener listen = (new InputListener(inputLayout, this, randomWord, i));

        for (int j = 0; j < i; j++) {
            inputEditText.setOnEditorActionListener(listen);
        }
    }
}
