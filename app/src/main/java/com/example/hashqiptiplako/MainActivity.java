package com.example.hashqiptiplako;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;


public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button b5 = findViewById(R.id.b5);
        Button b6 = findViewById(R.id.b6);
        Button b7 = findViewById(R.id.b7);
        Button b8 = findViewById(R.id.b8);
        GameOnActivity game = new GameOnActivity();

        new PushTheButton(this, game, b5, b6, b7, b8);
    }

    public void multiplayerLayout(View view) {
        // Start the multiplayer layout
        Intent intent = new Intent(this, Multiplayer.class);
        startActivity(intent);
    }


}
