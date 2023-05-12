package com.example.hashqiptiplako;

import android.os.Bundle;
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
        TextView randomTextView = findViewById(R.id.gameHint);
        randomTextView.setText(randomWord);
        String secretWord = getIntent().getStringExtra("secretWord");
        gameWordTextView.setText(secretWord);
        int i = getIntent().getIntExtra("length", 1);
        TextInputLayout inputLayout = findViewById(R.id.inputLayout);
        TextInputEditText inputEditText = findViewById(R.id.inputEditText);
        InputListener listen = (new InputListener(inputLayout, this, randomWord, i + 1));

        for (int j = 0; j < i+1; i++) {
            inputEditText.setOnEditorActionListener(listen);
        }
    }
}
