package com.example.othello;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class CPUSelectionActivity extends AppCompatActivity {
    private Button btnPlayAsBlack, btnPlayAsWhite;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cpu_selection);

        btnPlayAsBlack = findViewById(R.id.btnPlayAsBlack);
        btnPlayAsWhite = findViewById(R.id.btnPlayAsWhite);

        btnPlayAsBlack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startGameWithCPU(1); // Player is Black (1)
            }
        });

        btnPlayAsWhite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startGameWithCPU(2); // Player is White (2)
            }
        });
    }

    private void startGameWithCPU(int playerColor) {
        Intent intent = new Intent(CPUSelectionActivity.this, GameActivity.class);
        intent.putExtra("mode", "1vCPU");
        intent.putExtra("playerColor", playerColor);
        startActivity(intent);
    }
}
