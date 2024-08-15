package com.example.kematanganKeluak;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class RiwayatDeteksi extends AppCompatActivity {

    private ListView listView;
    private List<ListItem> listItems;
    private CustomAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_riwayat_deteksi);

        listView = findViewById(R.id.listView);
        listItems = new ArrayList<>();
        adapter = new CustomAdapter(this, listItems);
        listView.setAdapter(adapter);

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference itemsRef = db.collection("results");

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            String currentUserEmail = currentUser.getEmail();

            Query query = itemsRef.whereEqualTo("userEmail", currentUserEmail);

            query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String imageUrl = document.getString("imageURL");
                            String id = document.getId();
                            String text = document.getString("username");
                            String energy = document.getString("energy");
                            String entropy = document.getString("entropy");
                            String correlation = document.getString("correlation");
                            String homogeneity = document.getString("homogeneity");
                            String contrast = document.getString("contrast");
                            String descriptionName = document.getString("Kelas");

                            ListItem item = new ListItem(id, imageUrl, text, energy, entropy, correlation, homogeneity, contrast, descriptionName);
                            listItems.add(item);
                        }
                        adapter.notifyDataSetChanged();
                    } else {
                        Log.d("Firestore", "Error getting documents: ", task.getException());
                    }
                }
            });

            // Delete data
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    ListItem selectedItem = listItems.get(position);
                    String selectedId = selectedItem.getId();

                    itemsRef.document(selectedId).delete()
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    // Data deleted successfully
                                    Toast.makeText(RiwayatDeteksi.this, "Riwayat deteksi berhasil dihapus", Toast.LENGTH_SHORT).show();
                                    listItems.remove(position);
                                    adapter.notifyDataSetChanged();
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    // An error occurred while deleting data
                                    Toast.makeText(RiwayatDeteksi.this, "Gagal menghapus Riwayat deteksi", Toast.LENGTH_SHORT).show();
                                }
                            });
                }
            });
        }
    }
}

