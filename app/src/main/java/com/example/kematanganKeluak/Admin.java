package com.example.kematanganKeluak;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.viewpager2.widget.ViewPager2;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.OvershootInterpolator;
import android.widget.ScrollView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;

public class Admin extends AppCompatActivity {
    private ViewPager2 imageSlider;
    private CardView cardDeteksi, cardInformasi, cardTentang,cardRiwayat,cardPengguna,cardPanduan;
    private ScrollView menuScrollView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_admin);
        menuScrollView = findViewById(R.id.linearLayout);
        imageSlider = findViewById(R.id.image_slider);
        cardDeteksi = findViewById(R.id.card_deteksi);
        cardInformasi = findViewById(R.id.card_informasi);
        cardTentang = findViewById(R.id.card_tentang);
        cardPengguna = findViewById(R.id.card_pengguna);
        cardRiwayat = findViewById(R.id.card_riwayat);
        cardPanduan = findViewById(R.id.card_panduan);

        menuScrollView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                menuScrollView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                menuScrollView.setVisibility(View.VISIBLE);

                // Tambahkan animasi di sini menggunakan ObjectAnimator dengan interpolator OvershootInterpolator
                ObjectAnimator bounceAnim = ObjectAnimator.ofFloat(menuScrollView, "translationY", 0f, -100f, 0f);
                bounceAnim.setInterpolator(new OvershootInterpolator());
                bounceAnim.setDuration(1000);
                bounceAnim.start();
            }
        });


        // Setup ImageSlider
        ImageSliderAdapter sliderAdapter = new ImageSliderAdapter();
        imageSlider.setAdapter(sliderAdapter);
        // Tambahkan gambar ke slider
        sliderAdapter.addImage(R.drawable.keluak1);
        sliderAdapter.addImage(R.drawable.keluak2);
        sliderAdapter.addImage(R.drawable.keluak8);
        sliderAdapter.addImage(R.drawable.keluak6);

        FloatingActionButton logoutFab = findViewById(R.id.logout_fab);
        logoutFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(Admin.this, Login.class);
                startActivity(intent);
                finish();
                Toast.makeText(Admin.this, "Berhasil Logout", Toast.LENGTH_SHORT).show();
            }
        });
        // Tambahkan listener untuk setiap CardView
        cardDeteksi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Aksi yang dijalankan saat CardView Deteksi diklik
                Intent intent = new Intent(Admin.this, MenuDeteksi.class);
                startActivity(intent);
                Toast.makeText(Admin.this, "Berhasil Masuk Ke Menu Menu Deteksi ", Toast.LENGTH_SHORT).show();
            }
        });

        cardInformasi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Aksi yang dijalankan saat CardView Informasi diklik
                Intent intent = new Intent(Admin.this, Informasi.class);
                startActivity(intent);
                Toast.makeText(Admin.this, "Berhasil Masuk Ke Menu Informasi ", Toast.LENGTH_SHORT).show();
            }
        });

        cardTentang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Aksi yang dijalankan saat CardView Tentang diklik
                Intent intent = new Intent(Admin.this, tentang.class);
                startActivity(intent);
                Toast.makeText(Admin.this, "Berhasil Masuk Ke Menu Tentang ", Toast.LENGTH_SHORT).show();
            }
        });

        cardRiwayat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Aksi yang dijalankan saat CardView Petunjuk diklik
                Intent intent = new Intent(Admin.this, RiwayatDeteksi.class);
                startActivity(intent);
                Toast.makeText(Admin.this, "Berhasil Masuk Ke Menu Riwayat Deteksi", Toast.LENGTH_SHORT).show();
            }
        });
        cardPanduan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Aksi yang dijalankan saat CardView Petunjuk diklik
                Intent intent = new Intent(Admin.this, Panduan.class);
                startActivity(intent);
                Toast.makeText(Admin.this, "Berhasil Masuk Ke Menu Panduan ", Toast.LENGTH_SHORT).show();
            }
        });
        cardPengguna.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Aksi yang dijalankan saat CardView Petunjuk diklik
                Intent intent = new Intent(Admin.this, DataPengguna.class);
                startActivity(intent);
                Toast.makeText(Admin.this, "Berhasil Masuk Ke Menu Daftar Pengguna", Toast.LENGTH_SHORT).show();
            }
        });
    }
}