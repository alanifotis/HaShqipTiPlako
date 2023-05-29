package com.example.hashqiptiplako;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class Multiplayer extends AppCompatActivity {

    private EditText inputEditText;
    private Handler handler;
    private Runnable toastRunnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.username);

        inputEditText = findViewById(R.id.inputEditText);
        Button startGameButton = findViewById(R.id.startGameButton);



        startGameButton.setOnClickListener(v -> {
            String username = inputEditText.getText().toString().trim();

            // Validate input
            if (TextUtils.isEmpty(username) || username.length() < 5) {
                Toast.makeText(Multiplayer.this, "Username should contain at least 5 characters", Toast.LENGTH_SHORT).show();
            } else {
                // Generate a unique player ID
                String playerId = generatePlayerId();

                saveUsernameToFile(username, playerId);

                // Create a player object with their information
                Map<String, Object> player = new HashMap<>();
                player.put("username", username);
                player.put("playerId", playerId);

                // Save the player information to Firebase Realtime Database
                DatabaseReference playersRef = FirebaseDatabase.getInstance().getReference("players");
                playersRef.child(playerId).setValue(player);

                // Proceed to the waiting room or start the game directly
                joinWaitingRoom(playerId);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Stop displaying the toast message when the activity is destroyed
        if (handler != null && toastRunnable != null) {
            handler.removeCallbacks(toastRunnable);
        }
    }

    private String generatePlayerId() {
        String characters = "abcdefghijklmnopqrstuvwxyz0123456789!@%^&*()";
        StringBuilder playerId = new StringBuilder();

        Random random = new Random();
        for (int i = 0; i < 5; i++) {
            int index = random.nextInt(characters.length());
            playerId.append(characters.charAt(index));
        }

        return playerId.toString();
    }

    private void joinWaitingRoom(String playerId) {

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference waitingRoomRef = database.getReference("waitingRoom");

        // Create a player object with their information
        Map<String, Object> player = new HashMap<>();
        player.put("playerId", playerId);

        // Add the player to the waiting room
        waitingRoomRef.child(playerId).setValue(player);

        // Listen for changes in the waiting room count
        listenForWaitingRoomCount();
    }

    private void listenForWaitingRoomCount() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference waitingRoomRef = database.getReference("waitingRoom");

        // Create a handler to schedule the toast message
        handler = new Handler();
        toastRunnable = new Runnable() {
            @Override
            public void run() {
                Toast.makeText(Multiplayer.this, "Waiting for other players to join", Toast.LENGTH_SHORT).show();
                handler.postDelayed(this, 9000); // Repeat the toast message every 5 seconds
            }
        };

        // Start displaying the toast message
        handler.postDelayed(toastRunnable, 9000);

        // Listen for changes in the number of players in the waiting room
        waitingRoomRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                long playerCount = dataSnapshot.getChildrenCount();
                if (playerCount >= 2) {
                    // At least two players are in the waiting room, stop displaying the toast message
                    handler.removeCallbacks(toastRunnable);
                    startGame();
                } else {
                    // No players in the waiting room, update UI accordingly
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle database error
            }
        });
    }

    private void startGame() {
        // Game logic here
    }


    private void saveUsernameToFile(String username, String playerId) {
        try {
            FileOutputStream fileOutputStream = openFileOutput("user_data.csv", Context.MODE_PRIVATE);
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(fileOutputStream);

            // Write the CSV header
            outputStreamWriter.write("userName,usrId\n");

            // Write the username and player ID as a new row
            outputStreamWriter.write(username + "," + playerId + "\n");

            // Close the streams
            outputStreamWriter.close();
            fileOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
