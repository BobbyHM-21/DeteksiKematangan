package com.example.kematanganKeluak;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Map;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import com.google.firebase.firestore.FirebaseFirestore;

public class ResultDataLatih extends AppCompatActivity {

    // Arrays for predictions and their corresponding labels
    private float[] predictionsArray;
    private final String[] predictionLabelsAndDesc = new String[15];
    private FirebaseFirestore db;

    // ON PAGE CREATE
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result_data_latih);

        db = FirebaseFirestore.getInstance();

        // convert passed image from last screen back to bitmap for displaying
        if (getIntent().hasExtra("predictionImage")) {
            Bitmap bitmap = BitmapFactory.decodeByteArray(
                    getIntent().getByteArrayExtra("predictionImage"), 0,
                    getIntent().getByteArrayExtra("predictionImage").length);
            ImageView imageView = findViewById(R.id.resultsImage);
            imageView.setImageBitmap(bitmap);
        }

        // Display Prediction Probabilities
        displayPredictions();

        //Buttons //////////////////////////////////////////////////////////////////

        // button logic for back button
        Button backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> finish());


        Button saveButton = findViewById(R.id.saveButton);
        saveButton.setOnClickListener(v -> saveToFirestore());
        ///////////////////////////////////////////////////////////////////////////

    }

    public void openInfoPage() {
        Intent intent = new Intent(this, InfoPage.class);
        startActivity(intent);
    }

    // function to get prediction labels and display ordered probabilities on the results page
    private void displayPredictions() {
        Intent previousPage = getIntent();
        String species = previousPage.getStringExtra("species");
        String labelFileName = species + "_Labels_Descriptions.txt";

        try {
            InputStream inputStream = getAssets().open(labelFileName);
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            for (int i = 0; i < 15; i++) {
                String fullLine = reader.readLine();
                predictionLabelsAndDesc[i] = fullLine;
            }
            reader.close();
        } catch (IOException e) {
            Log.d("myapp", "Label file not found");
        }

        if (getIntent().hasExtra("predictions")) {
            predictionsArray = sortPredictions(getIntent().getFloatArrayExtra("predictions"));
        }

        StringBuilder fullPredictionString = new StringBuilder();
        for (int i = 0; i < 3; i++) {
            String[] labelDescriptionArr = predictionLabelsAndDesc[i].split(";", 3);
            String data = String.format(Locale.getDefault(), "%s: %1.2f", labelDescriptionArr[0], predictionsArray[i]) + "%";
            if (i == 0) {
                String topDescription = labelDescriptionArr[1];
                String[] newLineDescArr = topDescription.split("\\\\n", 0);
                StringBuilder fullDesc = new StringBuilder();
                for (String s : newLineDescArr) {
                    fullDesc.append(s).append("\n\n");
                }
//                TextView description = findViewById(R.id.descriptionText);
//                description.setText(fullDesc);
//
//                TextView nameDescription = findViewById(R.id.descriptionNameText);
//                nameDescription.setText(labelDescriptionArr[0]);
//
//                String topLink = labelDescriptionArr[2];
//                TextView linkTextView = findViewById(R.id.linkText);
//                linkTextView.setText(topLink);
            }
            fullPredictionString.append(data).append("\n");
        }

//        TextView predictionText = findViewById(R.id.Prediction1);
//        predictionText.setText(fullPredictionString.toString());
    }


    // function to sort both the predictions array and labels array in desc order of probability
    private float[] sortPredictions(float[] predictArray) {
        for (int i = 0; i < predictArray.length; i++) {
            for (int j = i + 1; j < predictArray.length; j++) {
                float tmp;
                String ltmp;
                if (predictArray[i] < predictArray[j]) {
                    tmp = predictArray[i];
                    ltmp = predictionLabelsAndDesc[i];

                    predictArray[i] = predictArray[j];
                    predictionLabelsAndDesc[i] = predictionLabelsAndDesc[j];

                    predictArray[j] = tmp;
                    predictionLabelsAndDesc[j] = ltmp;

                }
            }
        }

        for (int i = 0; i < predictArray.length; i++) {
            predictArray[i] = predictArray[i] * 100.0f;
        }
        return predictArray;
    }

    private void saveToFirestore() {
        ImageView resultsImage = findViewById(R.id.resultsImage);
        BitmapDrawable bitmapDrawable = (BitmapDrawable) resultsImage.getDrawable();
        Bitmap bitmap = bitmapDrawable.getBitmap();

        // Resize the bitmap to 300x300
        Bitmap resizedBitmap = Bitmap.createScaledBitmap(bitmap, 224, 224, true);

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        resizedBitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] byteArray = stream.toByteArray();

        // Generate a unique filename for the image
        String fileName = UUID.randomUUID().toString() + ".png";

        // Get a reference to the Firebase Storage location
        StorageReference storageRef = FirebaseStorage.getInstance().getReference().child("images/" + fileName);

        // Upload the image to Firebase Storage
        UploadTask uploadTask = storageRef.putBytes(byteArray);
        uploadTask.addOnSuccessListener(taskSnapshot -> {
            // Get the download URL of the uploaded image
            storageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                String imageUrl = uri.toString();

                // Get the values from TextViews
       
//                String descriptionName = ((TextView) findViewById(R.id.descriptionNameText)).getText().toString();

                // Get the selected radio button from RadioGroup
                RadioGroup radioGroup = findViewById(R.id.radioGroupKematangan);
                int selectedId = radioGroup.getCheckedRadioButtonId();
                String doneness = "";

                if (selectedId != -1) {
                    RadioButton radioButton = findViewById(selectedId);
                    doneness = radioButton.getText().toString();
                }

                // Get the currently logged in user's email or username
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                String userEmail = user != null ? user.getEmail() : "";
                String username = user != null ? user.getDisplayName() : "";

                // Create a new document in Firestore
                Map<String, Object> data = new HashMap<>();
                data.put("imageURL", imageUrl); // Store the image URL instead of byte array
//                data.put("descriptionName", descriptionName);
                data.put("kelas", doneness);

                FirebaseFirestore.getInstance().collection("datalatih")
                        .add(data)
                        .addOnSuccessListener(documentReference -> {
                            Toast.makeText(ResultDataLatih.this, "Data saved to Firestore", Toast.LENGTH_SHORT).show();
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(ResultDataLatih.this, "Failed to save data to Firestore", Toast.LENGTH_SHORT).show();
                            Log.e("Firestore", "Error saving data: " + e.getMessage());
                        });
            });
        }).addOnFailureListener(e -> {
            Toast.makeText(ResultDataLatih.this, "Failed to upload image to Firebase Storage", Toast.LENGTH_SHORT).show();
            Log.e("Firebase Storage", "Error uploading image: " + e.getMessage());
        });
    }



}