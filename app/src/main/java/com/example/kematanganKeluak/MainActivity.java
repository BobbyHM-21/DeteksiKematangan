package com.example.kematanganKeluak;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.viewpager2.widget.ViewPager2;

public class MainActivity extends AppCompatActivity {

    private ViewPager2 imageSlider;
    private CardView cardDeteksi, cardInformasi, cardTentang, cardPetunjuk;
    private TextView textViewUsername;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textViewUsername = findViewById(R.id.textViewUsername);

        // Mendapatkan pengguna saat ini dari Firebase Authentication
        // Inisialisasi Firebase Authentication
        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user != null) {
            String email = user.getEmail();

            firestore = FirebaseFirestore.getInstance();
            firestore.collection("users")
                    .whereEqualTo("email", email)
                    .get()
                    .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                            if (!queryDocumentSnapshots.isEmpty()) {
                                // Dapatkan data pengguna yang cocok dengan email
                                DocumentSnapshot documentSnapshot = queryDocumentSnapshots.getDocuments().get(0);
                                String username = documentSnapshot.getString("username");

                                // Tampilkan nilai username pada TextView "Selamat datang"
                                if (username != null) {
                                    String greeting = "Selamat datang, " + username + "!";
                                    textViewUsername.setText(greeting);
                                } else {
                                    // Tindakan yang diambil jika username tidak tersedia
                                    Toast.makeText(MainActivity.this, "Username tidak tersedia.", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                // Tindakan yang diambil jika tidak ditemukan data pengguna dengan email yang cocok
                                Toast.makeText(MainActivity.this, "Data pengguna tidak ditemukan.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            // Tindakan yang diambil jika terjadi kesalahan dalam mengambil data dari Firestore
                            Toast.makeText(MainActivity.this, "Gagal mengambil data pengguna.", Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            // Tindakan yang diambil jika pengguna tidak masuk
        }



        // Inisialisasi View
        imageSlider = findViewById(R.id.image_slider);
        cardDeteksi = findViewById(R.id.card_deteksi);
        cardInformasi = findViewById(R.id.card_informasi);
        cardTentang = findViewById(R.id.card_tentang);
        cardPetunjuk = findViewById(R.id.card_petunjuk);
        FloatingActionButton logoutFab = findViewById(R.id.logout_fab);

        // Setup ImageSlider
        ImageSliderAdapter sliderAdapter = new ImageSliderAdapter();
        imageSlider.setAdapter(sliderAdapter);
        // Tambahkan gambar ke slider
        sliderAdapter.addImage(R.drawable.keluak1);
        sliderAdapter.addImage(R.drawable.keluak2);
        sliderAdapter.addImage(R.drawable.keluak8);
        sliderAdapter.addImage(R.drawable.keluak6);

        //logout
        logoutFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(MainActivity.this, Login.class);
                startActivity(intent);
                finish();
                Toast.makeText(MainActivity.this, "Berhasil Logout", Toast.LENGTH_SHORT).show();
            }
        });

        // Tambahkan listener untuk setiap CardView
        cardDeteksi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Aksi yang dijalankan saat CardView Deteksi diklik
                Intent intent = new Intent(MainActivity.this, SpeciesPage.class);
                startActivity(intent);
                Toast.makeText(MainActivity.this, "Berhasil Masuk Ke Menu Deteksi", Toast.LENGTH_SHORT).show();
            }
        });

        cardInformasi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Aksi yang dijalankan saat CardView Informasi diklik
                Intent intent = new Intent(MainActivity.this, RiwayatDeteksi.class);
                startActivity(intent);
                Toast.makeText(MainActivity.this, "Berhasil Masuk Ke Menu Riwayat Deteksi", Toast.LENGTH_SHORT).show();
            }
        });

        cardTentang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Aksi yang dijalankan saat CardView Tentang diklik
                Intent intent = new Intent(MainActivity.this, Informasi.class);
                startActivity(intent);
                Toast.makeText(MainActivity.this, "Berhasil Masuk Ke Menu Informasi", Toast.LENGTH_SHORT).show();
            }
        });

        cardPetunjuk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Aksi yang dijalankan saat CardView Petunjuk diklik
                Intent intent = new Intent(MainActivity.this, Profil.class);
                startActivity(intent);
                Toast.makeText(MainActivity.this, "Berhasil Masuk Ke Menu Profil Pengguna", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
