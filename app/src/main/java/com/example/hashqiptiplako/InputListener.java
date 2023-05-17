package com.example.hashqiptiplako;
import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.widget.Toast;
import java.util.Arrays;

public class InputListener implements TextView.OnEditorActionListener {
    private final Context context;
    private final String randomWord;
    private final int maxAttempts;
    private int attempts;

    private final int[] hTextViewIds;
    private final char[] dest;


    public InputListener(Context context, String randomWord, int maxAttempts, int [] hTextViewIds) {
        this.context = context;
        this.randomWord = randomWord;
        this.maxAttempts = maxAttempts;
        this.attempts = 0;
        this.dest = new char[randomWord.length()];
        Arrays.fill(dest, '#');
        this.hTextViewIds = hTextViewIds;




    }

    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (actionId == EditorInfo.IME_ACTION_DONE) {
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

    private boolean isGameOver(String input) {

        if (input.equalsIgnoreCase(randomWord) || Arrays.equals(dest, randomWord.toCharArray())) {
            TextView gameWordTextView = ((Activity) context).findViewById(R.id.gameWord);
            gameWordTextView.setText(randomWord);
            Toast.makeText(context, "Correct! You Won!!", Toast.LENGTH_SHORT).show();
            new Handler().postDelayed(() -> ((Activity) context).finish(), 3000);
            return true;
        } else if (attempts == maxAttempts) {
                TextView gameWordTextView = ((Activity) context).findViewById(R.id.gameWord);
                gameWordTextView.setText(randomWord);
                Toast.makeText(context, "Almost there! The correct word was " + randomWord, Toast.LENGTH_LONG).show();
            new Handler().postDelayed(() -> ((Activity) context).finish(), 3000);
                return true;
            }

        return false;
    }

}
