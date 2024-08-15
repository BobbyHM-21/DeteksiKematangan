package com.example.kematanganKeluak;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class Profil extends AppCompatActivity {

    private TextInputLayout textInputLayoutUsername, textInputLayoutEmail, textInputLayoutPhoneNumber, textInputLayoutAddress;
    private TextInputEditText editTextUsername, editTextEmail, editTextPhoneNumber, editTextAddress;
    private Button buttonSave;

    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profil2);

        textInputLayoutUsername = findViewById(R.id.textInputLayoutUsername);
        textInputLayoutEmail = findViewById(R.id.textInputLayoutEmail);
        textInputLayoutPhoneNumber = findViewById(R.id.textInputLayoutPhoneNumber);
        textInputLayoutAddress = findViewById(R.id.textInputLayoutAddress);
        editTextUsername = findViewById(R.id.editTextUsername);
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPhoneNumber = findViewById(R.id.editTextPhoneNumber);
        editTextAddress = findViewById(R.id.editTextAddress);
        buttonSave = findViewById(R.id.buttonSave);

        firebaseAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user != null) {
            String email = user.getEmail();
            editTextEmail.setText(email);

            // Mengambil data pengguna dari Firestore berdasarkan email
            firestore.collection("users")
                    .whereEqualTo("email", email)
                    .get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        if (!queryDocumentSnapshots.isEmpty()) {
                            DocumentSnapshot documentSnapshot = queryDocumentSnapshots.getDocuments().get(0);
                            String username = documentSnapshot.getString("username");
                            String phoneNumber = documentSnapshot.getString("phoneNumber");
                            String address = documentSnapshot.getString("address");

                            // Menampilkan data pengguna pada TextInputEditText
                            editTextUsername.setText(username);
                            editTextPhoneNumber.setText(phoneNumber);
                            editTextAddress.setText(address);
                        }
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(Profil.this, "Gagal mengambil data pengguna.", Toast.LENGTH_SHORT).show();
                    });
        }

        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Mendapatkan data yang diinputkan pengguna
                String username = editTextUsername.getText().toString().trim();
                String phoneNumber = editTextPhoneNumber.getText().toString().trim();
                String address = editTextAddress.getText().toString().trim();

                // Validasi data yang diinputkan
                if (username.isEmpty()) {
                    textInputLayoutUsername.setError("Username tidak boleh kosong");
                    return;
                } else {
                    textInputLayoutUsername.setError(null);
                }

                // Mengupdate data pengguna di Firestore
                firestore.collection("users")
                        .document(user.getUid())
                        .update("username", username, "phoneNumber", phoneNumber, "address", address)
                        .addOnSuccessListener(aVoid -> {
                            Toast.makeText(Profil.this, "Data berhasil disimpan", Toast.LENGTH_SHORT).show();
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(Profil.this, "Gagal menyimpan data", Toast.LENGTH_SHORT).show();
                        });
            }
        });
    }
}
