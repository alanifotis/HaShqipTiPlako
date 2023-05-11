package com.example.hashqiptiplako;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class GameOnActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game_on);
        TextView gameWordTextView = findViewById(R.id.gameWord);

        String secretWord = getIntent().getStringExtra("secretWord");
        gameWordTextView.setText(secretWord);

    }
}
