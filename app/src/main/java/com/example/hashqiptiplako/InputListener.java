package com.example.hashqiptiplako;
import android.app.Activity;
import android.content.Context;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputLayout;

import java.util.Arrays;

public class InputListener implements TextView.OnEditorActionListener {
    private final TextInputLayout inputLayout;
    private final Context context;
    private final String randomWord;
    private final int maxAttempts;
    private int attempts;

    private final int[] hTextViewIds;
    private final char[] dest;


    public InputListener(TextInputLayout inputLayout, Context context, String randomWord, int maxAttempts, int [] hTextViewIds) {
        this.inputLayout = inputLayout;
        this.context = context;
        this.randomWord = randomWord;
        this.maxAttempts = maxAttempts;
        this.attempts = 0;
        this.dest = new char[randomWord.length()];
        Arrays.fill(dest, '#');
        this.hTextViewIds = hTextViewIds;




    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_NULL && event.getAction() == KeyEvent.ACTION_DOWN) {
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

            if (isGameOver(input)) {
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
                        ((Activity) context).findViewById(R.id.gameHint).setVisibility(View.VISIBLE);
                        ((Activity) context).findViewById(gTextViewIds[j]).setVisibility(View.VISIBLE);
                    }
                }
            }
        }
        return newHint.toString();
    }






    private boolean isValidInput(String input) {
        if (input.isEmpty()) {
            inputLayout.setError("Input cannot be empty");
            return false;
        }

        if (!input.matches("[A-Za-z]+")) {
            inputLayout.setError("Input should contain only letters from A-Z");
            return false;
        }

        inputLayout.setError(null);

        // Remove trailing and leading white spaces
        input = input.trim();

        if (input.length() != randomWord.length()) {
            Toast.makeText(context, "Input should have the same length as the random word", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    private boolean isGameOver(String input) {
        TextView gameWordTextView = ((Activity) context).findViewById(R.id.gameWord);
        gameWordTextView.setText(randomWord);

        if (input.equalsIgnoreCase(randomWord) || Arrays.equals(dest, randomWord.toCharArray())) {
            Toast.makeText(context, "Correct! You Won!!", Toast.LENGTH_SHORT).show();
            return true;
        } else {
            if (attempts == maxAttempts) {
                Toast.makeText(context, "Almost there! The correct word was " + randomWord, Toast.LENGTH_LONG).show();
                return true;
            }
        }
        return false;
    }

}
