package com.example.hashqiptiplako;


import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.content.Context;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.IOException;

public class PushTheButton implements View.OnClickListener {
    private final Context mContext;
    private final GameOnActivity mActivity;


    public PushTheButton(Context context, GameOnActivity mActivity, Button b5, Button b6, Button b7, Button b8) {
        this.mContext = context;
        this.mActivity = mActivity;



        // Set the click listener for each button to this object
        b5.setOnClickListener(this);
        b6.setOnClickListener(this);
        b7.setOnClickListener(this);
        b8.setOnClickListener(this);
    }

    public static void launchNewGameOnActivity(int i, String randomWord, Context mContext, GameOnActivity mActivity) {
        launchNewGameOnActivity(i, randomWord, mContext, mActivity, false);
    }

    public static void launchNewGameOnActivity(int i, String randomWord, Context mContext, GameOnActivity mActivity, boolean multiPlayer) {

        int[] qTextViewIds = {R.string.q5, R.string.q6, R.string.q7, R.string.q8};
        Intent intent = new Intent(mContext, GameOnActivity.class);
        String secretWord = mContext.getResources().getString(qTextViewIds[i - 5]);

        if (multiPlayer){
            DatabaseReference secretWordRef = FirebaseDatabase.getInstance().getReference("secretWord");
            secretWordRef.child("camouflage").setValue(secretWord);
        }

        intent.putExtra("secretWord", secretWord);
        intent.putExtra("randomWord", randomWord);
        intent.putExtra("length", i);
        intent.putExtra("isMultiplayer", multiPlayer);
        mContext.startActivity(intent);
        mActivity.finish(); // finish the MainActivity to prevent going back to it
    }



    private static int getButtonNumber(Button button) {
        if (button.getId() == R.id.b5) {
            return 5;
        } else if (button.getId() == R.id.b6) {
            return 6;
        } else if (button.getId() == R.id.b7) {
            return 7;
        } else if (button.getId() == R.id.b8) {
            return 8;
        } else {
            return -1;
        }
    }


    @Override
    public void onClick(View view) {
        Button button = (Button) view;
        int i = getButtonNumber(button);
        try {
            String word = RandomWord.selectWord(mContext, i);
            launchNewGameOnActivity(i, word, mContext, mActivity);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}

