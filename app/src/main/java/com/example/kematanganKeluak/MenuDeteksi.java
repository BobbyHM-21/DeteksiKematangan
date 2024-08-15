package com.example.kematanganKeluak;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MenuDeteksi extends AppCompatActivity {
    // ON PAGE CREATE
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_deteksi);

        // Logic for back button
//        Button backButton = findViewById(R.id.backButton);
//        backButton.setOnClickListener(v -> goBack());
    }

    // Pass selected species type to image page depending on card clicked
    public void cardClick(View view) {
        Intent intent = null;
        switch (view.getId()) {
            case R.id.appleCard:
                intent = new Intent(this, ImagePage.class);
                intent.putExtra("species", "Apple");
                break;
            case R.id.Datalatih:
                intent = new Intent(this, DataLatih.class);
                intent.putExtra("species", "Apple");
        }
        if (intent != null) {
            startActivity(intent);
        }
    }


    public void goBack() {
        finish();
    }
}