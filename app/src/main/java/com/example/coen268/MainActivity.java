package com.example.coen268;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    Button btn_reservationActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btn_reservationActivity = findViewById(R.id.reservationActivity);
        btn_reservationActivity.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.reservationActivity) {
            Intent i = new Intent(MainActivity.this, BusinessReservation.class);
            startActivity(i);
        }
    }
}
