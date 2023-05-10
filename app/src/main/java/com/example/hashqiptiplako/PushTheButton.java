package com.example.hashqiptiplako;

import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.IOException;


import android.content.Context;

public class PushTheButton implements View.OnClickListener {
    private final Button b5;
    private final Button b6;
    private final Button b7;
    private final Button b8;
    private final Context mContext;

    public PushTheButton(Context context, Button b5, Button b6, Button b7, Button b8) {
        this.mContext = context;
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
    }

    @Override
    public void onClick(View view) {
        Button button = (Button) view;
        try {
            String word = RandomWord.selectWord(mContext, getButtonNumber(button));
            TextView secretWordTextView = ((MainActivity) mContext).findViewById(R.id.secretWord);
            secretWordTextView.setText(word);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        hideButtons();
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