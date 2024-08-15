package com.example.kematanganKeluak;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class DataPengguna extends AppCompatActivity {

    private ListView listViewUsers;
    private FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_pengguna);

        listViewUsers = findViewById(R.id.listViewUsers);

        // Inisialisasi Firestore
        firestore = FirebaseFirestore.getInstance();

        // Ambil semua data pengguna dari Firestore
        firestore.collection("users")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            List<String> userList = new ArrayList<>();

                            for (QueryDocumentSnapshot document : task.getResult()) {
                                // Dapatkan informasi pengguna
                                String username = document.getString("username");
                                String email = document.getString("email");
                                String phoneNumber = document.getString("phoneNumber");
                                String address = document.getString("address");

                                // Format informasi pengguna
                                String userInfo = "Username: " + username + "\n"
                                        + "Email: " + email + "\n"
                                        + "Phone Number: " + phoneNumber + "\n"
                                        + "Address: " + address;

                                userList.add(userInfo);
                            }

                            // Buat adapter untuk ListView
                            ArrayAdapter<String> adapter = new ArrayAdapter<>(DataPengguna.this,
                                    android.R.layout.simple_list_item_1, userList);

                            // Set adapter ke ListView
                            listViewUsers.setAdapter(adapter);
                        }
                    }
                });
    }
}
