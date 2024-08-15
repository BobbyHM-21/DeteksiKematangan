package com.example.kematanganKeluak;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class CustomAdapter extends ArrayAdapter<ListItem> {

    private Context context;
    private List<ListItem> listItems;
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    public CustomAdapter(Context context, List<ListItem> items) {
        super(context, 0, items);
        this.context = context;
        this.listItems = items;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_riwayat_deteksi, parent, false);
        }

        ListItem item = listItems.get(position);

        ImageView imageView = convertView.findViewById(R.id.imageView);
        TextView textView = convertView.findViewById(R.id.textView);
        TextView energyTextView = convertView.findViewById(R.id.energyTextview);
        TextView entropyTextView = convertView.findViewById(R.id.entropyTextView);
        TextView correlationTextView = convertView.findViewById(R.id.correlationTextView);
        TextView homogeneityTextView = convertView.findViewById(R.id.homogeneityTextView);
        TextView contrastTextView = convertView.findViewById(R.id.contrastTextView);
        TextView descriptionNameTextView = convertView.findViewById(R.id.descriptionNameTextView);
        Button deleteButton = convertView.findViewById(R.id.deleteButton); // Tombol delete di dalam item

        // Set text
        textView.setText(item.getText());
        energyTextView.setText("Energy: " + item.getEnergy());
        entropyTextView.setText("Entropy: " + item.getEntropy());
        correlationTextView.setText("Correlation: " + item.getCorrelation());
        homogeneityTextView.setText("Homogeneity: " + item.getHomogeneity());
        contrastTextView.setText("Contrast: " + item.getContrast());
        descriptionNameTextView.setText("Description Name: " + item.getDescriptionName());

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Dapatkan posisi item yang ingin dihapus
                int deletePosition = position;

                // Dapatkan referensi koleksi "results" dari Firebase Firestore
                CollectionReference itemsRef = db.collection("results");

                // Dapatkan ID item yang akan dihapus berdasarkan posisi
                String deleteItemId = item.getId();

                // Hapus item dari database Firestore
                itemsRef.document(deleteItemId)
                        .delete()
                        .addOnSuccessListener(aVoid -> {
                            // Hapus item dari listItems
                            listItems.remove(deletePosition);

                            // Perbarui tampilan ListView
                            notifyDataSetChanged();
                        })
                        .addOnFailureListener(e -> {
                            Log.d("Firestore", "Error deleting document: " + e);
                            // Tambahkan logika penanganan kesalahan jika diperlukan
                        });
            }
        });

        // Load image from URL using Glide library
        Glide.with(context)
                .load(item.getImageUrl())
                .into(imageView);

        return convertView;
    }
}
