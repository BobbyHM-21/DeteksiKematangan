package com.example.kematanganKeluak;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class Register extends AppCompatActivity {
    private TextInputLayout usernameTextInputLayout, emailTextInputLayout, passwordTextInputLayout;
    private EditText usernameEditText, emailEditText, passwordEditText;
    private Button registerButton;
    private TextView loginTextView;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Inisialisasi Firebase Authentication
        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseFirestore.getInstance();

        // Inisialisasi komponen tampilan
        usernameTextInputLayout = findViewById(R.id.usernameTextInputLayout);
        emailTextInputLayout = findViewById(R.id.emailTextInputLayout);
        passwordTextInputLayout = findViewById(R.id.passwordTextInputLayout);
        usernameEditText = findViewById(R.id.usernameEditText);
        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        registerButton = findViewById(R.id.registerButton);
        loginTextView = findViewById(R.id.loginTextView);

        // Mendaftarkan OnClickListener untuk tombol register
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Mendapatkan nilai username, email, dan password dari EditText
                String username = usernameEditText.getText().toString().trim();
                String email = emailEditText.getText().toString().trim();
                String password = passwordEditText.getText().toString().trim();
                if (password.length() < 6) {
                    Toast.makeText(Register.this, "Password Kurang Dari 6 Karakter", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Melakukan proses registrasi dengan Firebase Authentication
                firebaseAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    // Registrasi berhasil, dapatkan referensi user saat ini
                                    FirebaseUser currentUser = firebaseAuth.getCurrentUser();
                                    if (currentUser != null) {
                                        // Simpan data username ke Firebase Authentication
                                        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                                .setDisplayName(username)
                                                .build();
                                        currentUser.updateProfile(profileUpdates)
                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if (task.isSuccessful()) {
                                                            // Data username berhasil disimpan di Firebase Authentication
                                                            // Simpan data pengguna ke Firebase Firestore
                                                            FirebaseFirestore db = FirebaseFirestore.getInstance();
                                                            Map<String, Object> user = new HashMap<>();
                                                            user.put("username", username);
                                                            user.put("email", email);
                                                            // Simpan data pengguna di koleksi "users" dengan ID pengguna yang sesuai
                                                            db.collection("users").document(currentUser.getUid())
                                                                    .set(user)
                                                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                        @Override
                                                                        public void onSuccess(Void aVoid) {
                                                                            // Data pengguna berhasil disimpan di Firebase Firestore
                                                                            Intent intent = new Intent(Register.this, MainActivity.class);
                                                                            startActivity(intent);
                                                                            Toast.makeText(Register.this, "Berhasil Registrasi data pengguna.", Toast.LENGTH_SHORT).show();
                                                                            finish();
                                                                        }
                                                                    })
                                                                    .addOnFailureListener(new OnFailureListener() {
                                                                        @Override
                                                                        public void onFailure(@NonNull Exception e) {
                                                                            // Gagal menyimpan data pengguna di Firebase Firestore
                                                                            Toast.makeText(Register.this, "Gagal menyimpan data pengguna.", Toast.LENGTH_SHORT).show();
                                                                        }
                                                                    });
                                                        } else {
                                                            // Gagal menyimpan data username di Firebase Authentication
                                                            Toast.makeText(Register.this, "Gagal menyimpan data username.", Toast.LENGTH_SHORT).show();
                                                        }
                                                    }
                                                });
                                    } else {
                                        // Gagal mendapatkan user saat ini
                                        Toast.makeText(Register.this, "Gagal mendapatkan user saat ini.", Toast.LENGTH_SHORT).show();
                                    }
                                } else {
                                    // Jika registrasi gagal, tampilkan pesan kesalahan
                                    Toast.makeText(Register.this, "Registrasi gagal. Silakan coba lagi.", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });

//        registerButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                // Mendapatkan nilai username, email, dan password dari EditText
//                String username = usernameEditText.getText().toString().trim();
//                String email = emailEditText.getText().toString().trim();
//                String password = passwordEditText.getText().toString().trim();
//                if (password.length() < 6) {
//                    Toast.makeText(Register.this, "Password Kurang Dari 6 Karakter", Toast.LENGTH_SHORT).show();
//                    return;
//                }
//                // Melakukan proses registrasi dengan Firebase Authentication
//                firebaseAuth.createUserWithEmailAndPassword(email, password)
//                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
//                            @Override
//                            public void onComplete(@NonNull Task<AuthResult> task) {
//                                if (task.isSuccessful()) {
//                                    // Jika registrasi berhasil, pindah ke Activity Utama
//                                    Intent intent = new Intent(Register.this, Login.class);
//                                    startActivity(intent);
//                                    finish();
//                                } else {
//                                    // Jika registrasi gagal, tampilkan pesan kesalahan
//                                    Toast.makeText(Register.this, "Registrasi gagal. Silakan coba lagi.", Toast.LENGTH_SHORT).show();
//                                }
//                            }
//                        });
//            }
//        });

        // Mendaftarkan OnClickListener untuk teks login
        loginTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Pindah ke Activity Login
                Intent intent = new Intent(Register.this, Login.class);
                startActivity(intent);
                finish();
            }
        });
    }
}
