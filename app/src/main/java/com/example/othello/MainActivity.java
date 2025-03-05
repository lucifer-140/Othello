package com.example.othello;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private Button btn1v1, btn1vCPU;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btn1v1 = findViewById(R.id.btn1v1);
        btn1vCPU = findViewById(R.id.btn1vCPU);

        btn1v1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, GameActivity.class);
                intent.putExtra("mode", "1v1");
                startActivity(intent);
            }
        });

        btn1vCPU.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, CPUSelectionActivity.class);
                startActivity(intent);
            }
        });
    }
}
