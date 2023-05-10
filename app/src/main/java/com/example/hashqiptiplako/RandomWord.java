package com.example.hashqiptiplako;

import android.content.Context;
import android.content.res.AssetManager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Random;

public class RandomWord {

    public static String selectWord(Context context, int length) throws IOException {
        ArrayList<String> words = new ArrayList<>();
        AssetManager assetManager = context.getAssets();
        InputStream inputStream = assetManager.open(length + ".txt");
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        String line;
        while ((line = reader.readLine()) != null) {
            words.add(line);
        }
        reader.close();
        Random rand = new Random();
        int index = rand.nextInt(words.size());
        return words.get(index);
    }
}
