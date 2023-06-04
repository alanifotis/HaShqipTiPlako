package com.example.hashqiptiplako;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Arrays;

public class InputListener implements TextView.OnEditorActionListener {
    private static Context context;
    private static String randomWord;
    private final int maxAttempts;
    private int attempts;

    private final int[] hTextViewIds;
    private final char[] dest;

    private boolean multiPlayer;



    public InputListener(Context context, String randomWord, int maxAttempts, int [] hTextViewIds, boolean multiplayer) {
        this.context = context;
        this.randomWord = randomWord;
        this.maxAttempts = maxAttempts;
        this.attempts = 0;
        this.dest = new char[randomWord.length()];
        Arrays.fill(dest, '#');
        this.hTextViewIds = hTextViewIds;
        this.multiPlayer = multiplayer;

    }

    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (actionId == EditorInfo.IME_ACTION_DONE || (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN)) {
            // hide the keyboard

            InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(v.getWindowToken(), 0);

            String input = v.getText().toString().trim();

            if (!isValidInput(input)) {
                return false;
            }

            TextView gameHintTextView = ((Activity) context).findViewById(R.id.gameWord);
            String newHintString = updateGameHint(input, randomWord, dest);
            gameHintTextView.setText(newHintString);


            attempts++;

            if (isGameOver(input, multiPlayer)) {
                return true;
            } else {
                Toast.makeText(context, "Incorrect, try again! Attempts left: " + (maxAttempts - attempts), Toast.LENGTH_SHORT).show();
                TextView hTextView = (((Activity) context).findViewById(hTextViewIds[(maxAttempts - attempts)]));
                hTextView.setVisibility(View.INVISIBLE);
                return false;
            }
        }
        return false;
    }

    private String updateGameHint(String input, String randomWord, char[] currentHint) {
        StringBuilder newHint = new StringBuilder();
        int rWLength = randomWord.length();

        for (int i = 0; i < rWLength; i++) {
            char guessedChar = input.charAt(i);
            if (guessedChar == randomWord.charAt(i)) {
                // Guessed character is correct, so show it in the game word
                newHint.append(guessedChar);
                currentHint[i] = guessedChar;
            } else if (currentHint[i] != '#' && currentHint[i] != '\0') {
                // Character has already been correctly guessed in a previous round,
                // so leave it as is in the game word
                // (i.e., don't replace it with '#')
                newHint.append(currentHint[i]);
            } else {
                // Guessed character is incorrect, so hide it with a '#'
                newHint.append('#');
                // Check if the guessed character is present in the random word
                for (int j = 0; j < rWLength; j++) {
                    if (guessedChar == randomWord.charAt(j)) {
                        // Guessed character is present in the random word, so make the corresponding TextView visible
                        int[] gTextViewIds = { R.id.g0, R.id.g1, R.id.g2, R.id.g3, R.id.g4, R.id.g5, R.id.g6, R.id.g7};
                        TextView textView = ((Activity) context).findViewById(gTextViewIds[j]);
                        textView.setText(String.valueOf(guessedChar));
                        ((Activity) context).findViewById(R.id.overHint).setVisibility(View.VISIBLE);
                        ((Activity) context).findViewById(gTextViewIds[j]).setVisibility(View.VISIBLE);
                    }
                }
            }
        }
        return newHint.toString();
    }






    private boolean isValidInput(String input) {
        String regexPattern = "^[a-zA-Z]{" + randomWord.length() + "}$";

        if (!input.matches(regexPattern)) {
            Toast.makeText(context, "A i Ke Syte Ne Ball?!", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private boolean isGameOver(String input, boolean multiPlayer) {
        if (input.equalsIgnoreCase(randomWord) || Arrays.equals(dest, randomWord.toCharArray())) {
            if (multiPlayer) {
                updateGameStatus("winner");
                launchNewActivity("winner");
            } else {
                String status = "winner";
                TextView gameWordTextView = ((Activity) context).findViewById(R.id.gameWord);
                gameWordTextView.setText(randomWord);
                Toast.makeText(context, "Correct! You Won!!", Toast.LENGTH_SHORT).show();
                new Handler().postDelayed(() -> launchNewActivity(status), 3000);
            }
            return true;
        } else if (attempts == maxAttempts) {
            if (multiPlayer) {
                updateGameStatus("loser");
                launchNewActivity("loser");
            } else {
                String status = "loser";
                TextView gameWordTextView = ((Activity) context).findViewById(R.id.gameWord);
                gameWordTextView.setText(randomWord);
                Toast.makeText(context, "Almost there! The correct word was " + randomWord, Toast.LENGTH_LONG).show();
                new Handler().postDelayed(() -> launchNewActivity(status), 3000);
            }
            return true;
        }

        return false;
    }

    private void updateGameStatus(String status) {
        // Update the game status in the multiplayer database
        FirebaseDatabase.getInstance().getReference("multiplayerGameStatus").setValue(status);
    }

    private static void launchNewActivity(String status) {
        Intent intent = new Intent(context, GameOver.class);
        intent.putExtra("length", randomWord.length());
        intent.putExtra("status", status);
        context.startActivity(intent);
        ((Activity) context).finish(); // finish this Activity to prevent going back to it
    }

    static final ValueEventListener gameStatusListener = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            String status = dataSnapshot.getValue(String.class);
            if (status != null) {
                if (status.equalsIgnoreCase("winner")) {
                    handleGameOver("winner");
                } else if (status.equalsIgnoreCase("loser")) {
                    handleGameOver("loser");
                }
            }
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {
            // Handle error
        }
    };

    private static void handleGameOver(String status) {
        // Perform any necessary actions based on the game over status
        if (status.equalsIgnoreCase("winner")) {
            // Handle game over for winner
            TextView gameWordTextView = ((Activity) context).findViewById(R.id.gameWord);
            gameWordTextView.setText(randomWord);
            new Handler().postDelayed(() -> Toast.makeText(context, "You Won!!", Toast.LENGTH_SHORT).show(), 3000);
        } else if (status.equalsIgnoreCase("loser")) {
            // Handle game over for loser
            TextView gameWordTextView = ((Activity) context).findViewById(R.id.gameWord);
            gameWordTextView.setText(randomWord);
            new Handler().postDelayed(() -> Toast.makeText(context, "Almost there! The correct word was " + randomWord, Toast.LENGTH_LONG).show(), 3000);
        }

        DatabaseReference playingRoomRef = FirebaseDatabase.getInstance().getReference("playingRoom");
        FirebaseDatabase.getInstance().getReference("secretWord").removeValue();

        playingRoomRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String playerId = snapshot.getKey();
                    // Remove player from playingRoom
                    playingRoomRef.child(playerId).removeValue();
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle error
            }
        });
        // Launch the game over activity for the other player with the corresponding status
        String otherStatus = status.equalsIgnoreCase("winner") ? "loser" : "winner";
        launchNewActivity(otherStatus);
    }

}
