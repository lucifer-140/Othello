package com.example.othello;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import android.widget.ImageButton;


public class GameActivity extends AppCompatActivity {
    private BoardView boardView;
    private TextView turnIndicator;
    private boolean isVsCPU;
    private int playerColor;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        boardView = findViewById(R.id.boardView);
        turnIndicator = findViewById(R.id.turnIndicator);


        String mode = getIntent().getStringExtra("mode");
        playerColor = getIntent().getIntExtra("playerColor", 1);
        isVsCPU = mode.equals("1vCPU");

        boardView.setGameMode(isVsCPU, playerColor);
        boardView.setTurnIndicator(turnIndicator);

        if (isVsCPU && playerColor == 2) {
            boardView.postDelayed(() -> boardView.triggerCpuMove(), 250);
        }


        ImageButton btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> {
            Intent intent = new Intent(GameActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        });


        ImageButton btnRestart = findViewById(R.id.btnRestart);
        btnRestart.setOnClickListener(v -> boardView.resetGame());


        ImageView gameOverImage = findViewById(R.id.gameOverImage);
        boardView.setGameOverImage(gameOverImage);

    }
}
