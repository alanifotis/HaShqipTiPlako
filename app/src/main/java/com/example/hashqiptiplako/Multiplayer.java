package com.example.hashqiptiplako;

import static com.example.hashqiptiplako.PushTheButton.launchNewGameOnActivity;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Scanner;

public class Multiplayer extends AppCompatActivity {

    private EditText inputEditText;
    private Handler handler;
    private Runnable toastRunnable;

    private String username = "";
    private String playerId = "";
    private final Random random = new Random();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.username);

        File file = new File(getFilesDir(), "user_data.csv");
        if (file.exists()) {
            try {
                FileInputStream fileInputStream = openFileInput("user_data.csv");
                Scanner scanner = new Scanner(fileInputStream);

                // Skip the header line
                scanner.nextLine();

                // Read the first (and only) data line
                if (scanner.hasNextLine()) {
                    String dataLine = scanner.nextLine();

                    // Split the data line into fields
                    String[] fields = dataLine.split(",");

                    if (fields.length >= 1) {
                        // Extract the username
                        username = fields[0];
                        playerId = fields[1];
                    }

                    Map<String, Object> player = new HashMap<>();
                    player.put("username", username);
                    player.put("playerId", playerId);

                    // Save the player information to Firebase Realtime Database
                    DatabaseReference playersRef = FirebaseDatabase.getInstance().getReference("players");
                    playersRef.child(playerId).setValue(player);

                    // Proceed to the waiting room or start the game directly
                    joinWaitingRoom(playerId);
                }

                // Close the streams
                scanner.close();
                fileInputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            // The file does not exist, prompt the user for a username
            userName();
        }

    }

    public void userName() {
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


        for (int i = 0; i < 5; i++) {
            int index = random.nextInt(characters.length());
            playerId.append(characters.charAt(index));
        }

        return playerId.toString();
    }

    private void joinWaitingRoom(String playerId) {
        findViewById(R.id.inputEditText).setVisibility(View.INVISIBLE);
        findViewById(R.id.inputLayout).setVisibility(View.INVISIBLE);
        findViewById(R.id.startGameButton).setVisibility(View.INVISIBLE);
        findViewById(R.id.waitingText).setVisibility(View.VISIBLE);

        DatabaseReference waitingRoomRef =  FirebaseDatabase.getInstance().getReference("waitingRoom");

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

        // Declare 'count' variable as final
        final int[] count = {0};

        // Create a handler to schedule the count update
        handler = new Handler();
        Runnable countRunnable = new Runnable() {
            @Override
            public void run() {
                count[0]++;
                TextView countTextView = findViewById(R.id.count);
                countTextView.setVisibility(View.VISIBLE);
                countTextView.setText(getString(R.string.life_seconds_lost, count[0]));
                handler.postDelayed(this, 1000); // Repeat the count update every 1 second
            }
        };

        // Start the count update
        handler.postDelayed(countRunnable, 1000);

        // Listen for changes in the number of players in the waiting room
        waitingRoomRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                long playerCount = dataSnapshot.getChildrenCount();
                if (playerCount >= 2) {
                    // At least two players are in the waiting room, stop the count update
                    handler.removeCallbacks(countRunnable);
                    try {
                        startGame();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle database error
            }
        });
    }




    private void startGame() throws IOException {
        int i = random.nextInt(4) + 5;
        GameOnActivity game = new GameOnActivity();
        String word = RandomWord.selectWord(this, i);
        DatabaseReference secretWordRef = FirebaseDatabase.getInstance().getReference("secretWord");
        Map<String, Object> wordData = new HashMap<>();
        wordData.put("word", word);
        wordData.put("length", i);

        secretWordRef.setValue(wordData);

        launchNewGameOnActivity(i, word, this, game);
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
