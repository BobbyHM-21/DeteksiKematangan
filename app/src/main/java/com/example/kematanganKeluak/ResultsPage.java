package com.example.kematanganKeluak;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

public class ResultsPage extends AppCompatActivity {

    // Arrays for predictions and their corresponding labels
    private float[] predictionsArray;
    private final String[] predictionLabelsAndDesc = new String[15];
    private FirebaseFirestore db;
    private int autoIncrementId = 1;
    private boolean isDataSaved = false;

    // ON PAGE CREATE
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results_page);

        db = FirebaseFirestore.getInstance();

        getLastDocumentId();

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

        Button saveToPdfButton = findViewById(R.id.printButton);
        saveToPdfButton.setOnClickListener(v -> saveToPdf());

        Button saveButton = findViewById(R.id.saveButton);

        saveButton.setOnClickListener(v -> {
            if (!isDataSaved) {
                saveToFirestore();
                isDataSaved = true;
            } else {
                Toast.makeText(this, "Data has already been saved", Toast.LENGTH_SHORT).show();
            }
        });
        ///////////////////////////////////////////////////////////////////////////
//        saveButton.setOnClickListener(v -> saveToFirestore());

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
        for (int i = 0; i < 1; i++) {
            String[] labelDescriptionArr = predictionLabelsAndDesc[i].split(";", 3);
            String data = String.format(Locale.getDefault(), "%s: %1.2f", labelDescriptionArr[0], predictionsArray[i]) + "%";
            if (i == 0) {
                String topDescription = labelDescriptionArr[1];
                String[] newLineDescArr = topDescription.split("\\\\n", 0);
                StringBuilder fullDesc = new StringBuilder();
                for (String s : newLineDescArr) {
                    fullDesc.append(s).append("\n\n");
                }
                TextView description = findViewById(R.id.descriptionText);
                description.setText(fullDesc);

                TextView nameDescription = findViewById(R.id.descriptionNameText);
                nameDescription.setText(labelDescriptionArr[0]);

                String topLink = labelDescriptionArr[2];
                TextView linkTextView = findViewById(R.id.linkText);
                linkTextView.setText(topLink);

                if (linkTextView.getText().toString().equals("Gambar Yang Anda Masukan Bukan Objek Keluak")) {
                    linkTextView.setTextColor(Color.RED);
                    findViewById(R.id.Prediction1).setVisibility(View.GONE);
                    findViewById(R.id.descriptionNameText).setVisibility(View.GONE);
                    findViewById(R.id.textView10).setVisibility(View.GONE);
                    findViewById(R.id.textView9).setVisibility(View.GONE);
                }
            }
            fullPredictionString.append(data).append("\n");
        }

        TextView predictionText = findViewById(R.id.Prediction1);
        predictionText.setText(fullPredictionString.toString());
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
        return predictArray;
    }

    private void saveToPdf() {
        // Create a new document
        Document document = new Document(PageSize.A4);

        try {
            // Provide a file path and name to save the PDF
            String filePath = getExternalFilesDir(null) + "/prediction_results.pdf";
            File pdfFile = new File(filePath);
            Uri pdfUri = FileProvider.getUriForFile(this, getApplicationContext().getPackageName() + ".fileprovider", pdfFile);

            PdfWriter.getInstance(document, new FileOutputStream(pdfFile));

            // Open the document
            document.open();

            // Add the title to the PDF
            Font titleFont = new Font(Font.FontFamily.HELVETICA, 18, Font.BOLD);
            Paragraph title = new Paragraph("APLIKASI PENENTUAN KEMATANGAN KELUAK DENGAN KNN DAN GLCM", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            document.add(title);

            // Add an empty line
            document.add(new Paragraph(" "));

            // Add the image to the PDF
            ImageView imageView = findViewById(R.id.resultsImage);
            Bitmap originalBitmap  = ((BitmapDrawable) imageView.getDrawable()).getBitmap();

            int targetWidth = 300;
            int targetHeight = 300;
            Bitmap resizedBitmap = Bitmap.createScaledBitmap(originalBitmap, targetWidth, targetHeight, false);

            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            resizedBitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
            Image image = Image.getInstance(stream.toByteArray());
            document.add(image);

            // Add the text to the PDF
            TextView descriptionName = findViewById(R.id.descriptionNameText);
            String description = descriptionName.getText().toString();
            document.add(new Paragraph("Hasil: " + description));

            // Close the document
            document.close();

            // Show a toast or display a success message to the user
            Toast.makeText(this, "Cetak Hasil Deteksi Ke PDF Berhasil", Toast.LENGTH_SHORT).show();

            // Open the PDF file using a file explorer
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(pdfUri, "application/pdf");
            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            startActivity(intent);
        } catch (Exception e) {
            Log.e("PDF", "Error Simpan PDF: " + e.getMessage());
        }
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
                String descriptionName = ((TextView) findViewById(R.id.descriptionNameText)).getText().toString();

                // Get the currently logged in user's email or username
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                String userEmail = user != null ? user.getEmail() : "";
                String username = user != null ? user.getDisplayName() : "";

                // ambil data waktu
                Calendar calendar = Calendar.getInstance();
                int day = calendar.get(Calendar.DAY_OF_MONTH);
                int month = calendar.get(Calendar.MONTH) + 1; // Ditambahkan 1 karena indeks bulan dimulai dari 0 (Januari = 0)
                int year = calendar.get(Calendar.YEAR);
                int hour = calendar.get(Calendar.HOUR_OF_DAY);
                int minute = calendar.get(Calendar.MINUTE);

                SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
                String dateTime = dateFormat.format(calendar.getTime());


                // Create a new document in Firestore
                Map<String, Object> data = new HashMap<>();
                data.put("id", autoIncrementId); // Tambahkan ID dengan nilai auto-increment
                data.put("dateTime", dateTime);
                data.put("userEmail", userEmail);
                data.put("username", username);
                data.put("imageURL", imageUrl);
         
                data.put("Kelas", descriptionName);

                FirebaseFirestore.getInstance().collection("results")
                        .add(data)
                        .addOnSuccessListener(documentReference -> {
                            Toast.makeText(ResultsPage.this, "Hasil Deteksi Berhasil Disimpan ke Database", Toast.LENGTH_SHORT).show();
                            autoIncrementId++; // Penambahan nilai auto-increment setelah berhasil menyimpan data
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(ResultsPage.this, "Failed to save data to Firestore", Toast.LENGTH_SHORT).show();
                            Log.e("Firestore", "Error saving data: " + e.getMessage());
                        });
            });
        }).addOnFailureListener(e -> {
            Toast.makeText(ResultsPage.this, "Failed to upload image to Firebase Storage", Toast.LENGTH_SHORT).show();
            Log.e("Firebase Storage", "Error uploading image: " + e.getMessage());
        });
    }

    private void getLastDocumentId() {
        db.collection("results")
                .orderBy("id", Query.Direction.DESCENDING)
                .limit(1)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        DocumentSnapshot lastDocument = queryDocumentSnapshots.getDocuments().get(0);
                        autoIncrementId = lastDocument.getLong("id").intValue() + 1;
                    }
                })
                .addOnFailureListener(e -> {
                    // Error handling
                    Log.e("Firestore", "Error getting last document id: " + e.getMessage());
                });
    }
}