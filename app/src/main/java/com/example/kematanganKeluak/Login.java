package com.example.kematanganKeluak;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Login extends AppCompatActivity {
    private TextInputLayout usernameTextInputLayout, passwordTextInputLayout;
    private EditText usernameEditText, passwordEditText;
    private Button loginButton;
    private FirebaseAuth firebaseAuth;
    private CheckBox rememberMeCheckbox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Inisialisasi Firebase Authentication
        firebaseAuth = FirebaseAuth.getInstance();

        // Inisialisasi komponen tampilan
        usernameTextInputLayout = findViewById(R.id.usernameTextInputLayout);
        passwordTextInputLayout = findViewById(R.id.passwordTextInputLayout);
        usernameEditText = findViewById(R.id.usernameEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        loginButton = findViewById(R.id.loginButton);
        MaterialButton registerButton = findViewById(R.id.registerButton);
        rememberMeCheckbox = findViewById(R.id.rememberMeCheckbox);

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Login.this, Register.class);
                startActivity(intent);
            }
        });

        // Mendaftarkan OnClickListener untuk tombol login
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Mendapatkan nilai username dan password dari EditText
                String username = usernameEditText.getText().toString().trim();
                String password = passwordEditText.getText().toString().trim();

                // Memeriksa apakah username dan password tidak kosong
                if (username.isEmpty()) {
                    usernameTextInputLayout.setError("Masukkan username");
                    return;
                }

                if (password.isEmpty()) {
                    passwordTextInputLayout.setError("Masukkan password");
                    return;
                }

                // Melakukan proses login dengan Firebase Authentication
                firebaseAuth.signInWithEmailAndPassword(username, password)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    // Mendapatkan objek pengguna saat ini
                                    FirebaseUser currentUser = firebaseAuth.getCurrentUser();
                                    if (currentUser != null) {
                                        // Mendapatkan email pengguna saat ini
                                        String userEmail = currentUser.getEmail();

                                        if (rememberMeCheckbox.isChecked()) {
                                            // Jika checkbox "Mengingat Saya" dicentang,
                                            // simpan informasi login di SharedPreferences atau sesuai kebutuhan aplikasi Anda
                                            // Contoh:
                                            SharedPreferences preferences = getSharedPreferences("login", MODE_PRIVATE);
                                            SharedPreferences.Editor editor = preferences.edit();
                                            editor.putString("username", username);
                                            editor.putString("password", password);
                                            editor.putBoolean("rememberMe", true);
                                            editor.apply();
                                        }

                                        if (userEmail != null && userEmail.equalsIgnoreCase("admin@gmail.com")) {
                                            // Jika email pengguna adalah "admin@gmail.com",
                                            // pindah ke Activity Admin
                                            Intent intent = new Intent(Login.this, Admin.class);
                                            startActivity(intent);
                                        } else {
                                            // Jika email pengguna bukan "admin@gmail.com",
                                            // pindah ke MainActivity
                                            Intent intent = new Intent(Login.this, MainActivity.class);
                                            startActivity(intent);
                                        }
                                        Toast.makeText(Login.this, "Login berhasil.", Toast.LENGTH_SHORT).show();
                                        finish();
                                    }
                                } else {
                                    // Jika login gagal, tampilkan pesan kesalahan
                                    Toast.makeText(Login.this, "Login gagal. Silakan cek kembali username dan password Anda.", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });
    }
}
