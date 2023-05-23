package com.example.hashqiptiplako;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;


public class GameOver extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game_over);

        TextView overHint = findViewById(R.id.overHint);
        TextView overHint2 = findViewById(R.id.overHint2);
        String status = getIntent().getStringExtra("status");

        if (status.equalsIgnoreCase("loser")) {
            overHint.setText(R.string.loser_1);
            overHint2.setText(R.string.loser_2);
        } else if (status.equalsIgnoreCase("winner")) {
            overHint.setText(R.string.winner_1);
            overHint2.setText(R.string.winner_2);
        }

        // Set click listeners for buttons
        Button settings = findViewById(R.id.settings);
        Button reset = findViewById(R.id.reset);
        Button restart = findViewById(R.id.restart);

        settings.setOnClickListener(this);
        reset.setOnClickListener(this);
        restart.setOnClickListener(this);

        // Rest of your code...
    }

    @Override
    public void onClick(View view) {
        // Handle button clicks here
        int i = getIntent().getIntExtra("length", 5);

        int id = view.getId();
        if (id == R.id.reset) {
            try {
                GameOnActivity game = new GameOnActivity();
                String randomWord = RandomWord.selectWord(this, i);
                PushTheButton.launchNewGameOnActivity(i, randomWord, this, game);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else if (id == R.id.restart) {
            // Handle restart button click
            Intent intent = new Intent(this, MainActivity.class);
            this.startActivity(intent);
            this.finish();
        } else if (id == R.id.settings) {
            // Handle settings button click
            Intent intent = new Intent(this, Settings.class);
            this.startActivity(intent);
            this.finish();
        }
    }





}



