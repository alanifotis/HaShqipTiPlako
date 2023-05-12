package com.example.hashqiptiplako;
import android.app.Activity;
import android.content.Context;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
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
    private StringBuilder stringBuilder = new StringBuilder();

    public InputListener(TextInputLayout inputLayout, Context context, String randomWord, int maxAttempts) {
        this.inputLayout = inputLayout;
        this.context = context;
        this.randomWord = randomWord;
        this.maxAttempts = maxAttempts;
        this.attempts = 0;
    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_NULL && event.getAction() == KeyEvent.ACTION_DOWN) {
            String input = v.getText().toString().trim();



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

            // Compare the input with the random word
            if (input.length() != randomWord.length()) {
                Toast.makeText(context, "Input should have the same length as the random word", Toast.LENGTH_SHORT).show();
                return false;
            }

            attempts++;
            TextView gameHintTextView = ((Activity) context).findViewById(R.id.gameWord);
            int l = randomWord.length();
            char[] letter = new char[l];
            for (int i = 0; i < l; i++) {
                if (randomWord.charAt(i) == input.charAt(i)) {
                    this.stringBuilder.append(input.charAt(i));
                } else {
                    if (this.stringBuilder.toString().charAt(i) == '#' || stringBuilder.toString().charAt(i) == '\0') {

                    }
                    this.stringBuilder.append("#");
                }
            }
            gameHintTextView.setText(String.valueOf(this.stringBuilder.toString()));


            if (input.equalsIgnoreCase(randomWord)) {
                Toast.makeText(context, "Correct!", Toast.LENGTH_SHORT).show();
                return true;

            } else {
                if (attempts == maxAttempts) {
                    Toast.makeText(context, "Incorrect! The correct word was " + randomWord, Toast.LENGTH_LONG).show();
                    return true;
                } else {
                    Toast.makeText(context, "Incorrect, try again! Attempts left: " + (maxAttempts - attempts), Toast.LENGTH_SHORT).show();
                    return false;
                }
            }
        }
        return false;
    }
}
