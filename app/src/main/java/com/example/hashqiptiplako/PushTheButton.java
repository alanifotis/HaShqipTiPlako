package com.example.hashqiptiplako;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.content.Context;


public class PushTheButton implements View.OnClickListener {
    private final Button b5;
    private final Button b6;
    private final Button b7;
    private final Button b8;
    private final Context mContext;
    private final GameOnActivity mActivity;


    public PushTheButton(Context context, GameOnActivity mActivity, Button b5, Button b6, Button b7, Button b8) {
        this.mContext = context;
        this.mActivity = mActivity;
        this.b5 = b5;
        this.b6 = b6;
        this.b7 = b7;
        this.b8 = b8;

        // Set the click listener for each button to this object
        b5.setOnClickListener(this);
        b6.setOnClickListener(this);
        b7.setOnClickListener(this);
        b8.setOnClickListener(this);
    }

    private void launchNewActivity(int i) {
        Intent intent = new Intent(mContext, GameOnActivity.class);
        int resourceId = mContext.getResources().getIdentifier("q" + i, "string", mContext.getPackageName());
        String secretWord = mContext.getResources().getString(resourceId);
        intent.putExtra("secretWord", secretWord);
        mContext.startActivity(intent);
        mActivity.finish(); // finish the MainActivity to prevent going back to it
    }



    //String word = RandomWord.selectWord(mContext, i);
        //TextView secretWordTextView = ((Activity) mContext).findViewById(R.id.gameWord);
        //int resourceId = mContext.getResources().getIdentifier("q" + i, "string", mContext.getPackageName());
        //String secretWord = mContext.getResources().getString(resourceId);
        //secretWordTextView.setText(secretWord);
        //} catch (IOException e) {
        //    throw new RuntimeException(e);
        //}
        //hideButtons();
        // Do something with buttonNumber here

        // Get the secret word TextView from the GameOnActivity layout
        //TextView secretWordTextView = mContext.TextViewfindViewById(R.id.secretWord);

        // Set the secret word text using a string resource ID from game_on.xml
        /*int resourceId = mActivity.getResources().getIdentifier("q" + currentButtonNumber, "string", mContext.getPackageName());
        String secretWord = mActivity.getResources().getString(resourceId);
        secretWordTextView.setText(secretWord);

        // Get the hint TextView from the GameOnActivity layout
        TextView hintTextView = mActivity.findViewById(R.id.textHint);

        // Set the hint text using a string resource ID from game_on.xml
        int hintResourceId = mActivity.getResources().getIdentifier("q" + currentButtonNumber + "_hint", "string", mContext.getPackageName());
        String hint = mActivity.getResources().getString(hintResourceId);
        hintTextView.setText(hint);

         */
    //}



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

    private void hideButtons() {
        b5.setVisibility(View.GONE);
        b6.setVisibility(View.GONE);
        b7.setVisibility(View.GONE);
        b8.setVisibility(View.GONE);
        TextView hint = ((MainActivity) mContext).findViewById(R.id.textHint);
        hint.setVisibility(View.GONE);
    }

    @Override
    public void onClick(View view) {
        Button button = (Button) view;
        //try {

            int i = getButtonNumber(button);
            launchNewActivity(i);
            //String word = RandomWord.selectWord(mContext, i);
            //TextView secretWordTextView = ((MainActivity) mContext).findViewById(R.id.secretWord);
            //int resourceId = mContext.getResources().getIdentifier("q" + i, "string", mContext.getPackageName());
            //String secretWord = mContext.getResources().getString(resourceId);
            //secretWordTextView.setText(secretWord);
        //} catch (IOException e) {
        //    throw new RuntimeException(e);
        //}
        //hideButtons();
        // Do something with buttonNumber here
    }
}




    /*public int getSize() {
        // Assuming that the size is determined by the number of visible buttons
        int size = 0;
        if (b5.getVisibility() == View.VISIBLE) size++;
        if (b6.getVisibility() == View.VISIBLE) size++;
        if (b7.getVisibility() == View.VISIBLE) size++;
        if (b8.getVisibility() == View.VISIBLE) size++;
        return size;
    }
}*/