package com.example.hashqiptiplako;


import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class GameOnActivity extends AppCompatActivity {
    private ValueEventListener secretWordListener;
    private DatabaseReference secretWordRef;
    private String randomWord = "";
    private String secretWord = "";
    private int length = 5;

    private boolean isMultiPlayer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game_on);

        this.isMultiPlayer = getIntent().getBooleanExtra("isMultiplayer", false);

        if (isMultiPlayer) {
            secretWordRef = FirebaseDatabase.getInstance().getReference("secretWord");

            DatabaseReference gameStatusRef = FirebaseDatabase.getInstance().getReference("multiplayerGameStatus");
            gameStatusRef.addValueEventListener(InputListener.gameStatusListener);

            secretWordListener = new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    randomWord = dataSnapshot.child("word").getValue(String.class);
                    secretWord = dataSnapshot.child("camouflage").getValue(String.class);
                    length = dataSnapshot.child("length").getValue(Integer.class);
                    initializeGame();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    // Handle database error
                }
            };
        } else {
            randomWord = getIntent().getStringExtra("randomWord");
            secretWord = getIntent().getStringExtra("secretWord");
            length = getIntent().getIntExtra("length", 5);
            initializeGame();
        }
    }

    private void initializeGame() {
        TextView gameWordTextView = findViewById(R.id.gameWord);
        gameWordTextView.setText(secretWord);

        int[] hTextViewIds = {R.id.h0, R.id.h1, R.id.h2, R.id.h3, R.id.h4, R.id.h5, R.id.h6, R.id.h7, R.id.h8, R.id.h9};

        DatabaseReference lengthRef = FirebaseDatabase.getInstance().getReference("secretWord/length");
        lengthRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (isMultiPlayer) {
                    length = dataSnapshot.getValue(Integer.class);
                } else {
                    length = getIntent().getIntExtra("length", 5);
                }

                length = length == 8 ? 10 : length + 1;

                for (int k = 0; k < length; k++) {
                    TextView hTextView = findViewById(hTextViewIds[k]);
                    hTextView.setVisibility(View.VISIBLE);
                }

                if (length == 7) {
                    TextView h8TextView = findViewById(R.id.h7);
                    h8TextView.setVisibility(View.VISIBLE);
                }

                TextInputLayout inputLayout = findViewById(R.id.inputLayout);
                TextInputEditText inputEditText = inputLayout.findViewById(R.id.inputEditText);
                InputListener listen = new InputListener(GameOnActivity.this, randomWord, length, hTextViewIds, isMultiPlayer);

                for (int j = 0; j < length; j++) {
                    inputEditText.setOnEditorActionListener(listen);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle database error
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (secretWordListener != null) {
            secretWordRef.addValueEventListener(secretWordListener);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (secretWordListener != null) {
            secretWordRef.removeEventListener(secretWordListener);
        }
    }
}

