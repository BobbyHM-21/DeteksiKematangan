package com.example.kematanganKeluak;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import com.example.kematanganKeluak.ml.MobileKeluakModel;

import org.tensorflow.lite.DataType;
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
public class ImagePage extends AppCompatActivity {
    // Define species string
    String species = null;
    // Define photo and display imageview variables
    private Bitmap selectedImage;
    Bitmap photo = null;
    ImageView selectedImageDisplay;
    TextView errorText;
    // ON PAGE CREATE
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_page);

        // get selected species type from last screen and set title
        Intent selectedSpecies = getIntent();
//        species = selectedSpecies.getStringExtra("species");
//        ((TextView)findViewById(R.id.header)).setText(species.concat(" Detection"));
        // set variables for image display imageview and error text below it
        selectedImageDisplay = findViewById(R.id.Image);
        errorText = findViewById(R.id.noPhotoErrorText);
        //Buttons //////////////////////////////////////////////////////////////////

        Button backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> goBack());
        // button logic for photo upload button
        Button uploadButton = findViewById(R.id.uploadPhotoButton);
        uploadButton.setOnClickListener(v -> openPhotoLibrary());
        // button logic for predict button
        Button predictButton = findViewById(R.id.predictButton);
        predictButton.setOnClickListener(v -> openPredictionsPage());
        Button takePhotoButton = findViewById(R.id.takePhotoButton);
        takePhotoButton.setOnClickListener(v -> openCameraFunction());
        ////////////////////////////////////////////////////////////////////////////
    }
//    public void openInfoPage() {
//        Intent intent = new Intent(this, InfoPage.class);
//        startActivity(intent);
//    }
    public void goBack() {
        finish();
    }
    public void openPhotoLibrary() {
        // create an intent for a photo library instance to select an image
        Intent i = new Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        // open the intent and pass the selected image and request code
        startActivityForResult(i, 2);
    }
    private void openCameraFunction() {
        // create an intent for a camera instance to take a new photo
        Intent camera_intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // open the intent and pass the taken photo and request code
        startActivityForResult(camera_intent, 1);
    }
    // function to pass image into the required model and pass the predicted
    // probabilities to the results page
    // function to pass image into the required model and pass the predicted probabilities to the results page
    public void openPredictionsPage() {
        if (selectedImage == null) {
            errorText.setText("Tidak ada gambar... Silahkan tambah Gambar");
        } else {
            float[] resultsArray = null;
            // Process bitmap image into a ByteBuffer and then into a TensorBuffer
            ByteBuffer byteBuffer = convertBitmapToByteBuffer(selectedImage);
            TensorBuffer inputFeature0 = TensorBuffer.createFixedSize(new int[]{1, 224, 224, 3}, DataType.FLOAT32);
            inputFeature0.loadBuffer(byteBuffer);

            try {
                MobileKeluakModel model = MobileKeluakModel.newInstance(this);
                MobileKeluakModel.Outputs outputs = model.process(inputFeature0);
                TensorBuffer outputFeature0 = outputs.getOutputFeature0AsTensorBuffer();
                resultsArray = outputFeature0.getFloatArray();
                model.close();
            } catch (Exception e) {
                Log.d("myapp", "error with model predictions");
                return;
            }

            // Convert back to RGB
            Bitmap rgbBitmap = convertToRGB(selectedImage);

            // Add required information to results page intent and start activity
            Intent intent = new Intent(this, ResultsPage.class);
            // Convert bitmap image to byteArray to be able to pass to results page
            ByteArrayOutputStream bs = new ByteArrayOutputStream();
            rgbBitmap.compress(Bitmap.CompressFormat.JPEG, 50, bs);
            intent.putExtra("predictionImage", bs.toByteArray());
            intent.putExtra("predictions", resultsArray);
            intent.putExtra("species", "Apple");
            startActivity(intent);
        }
    }
    private void displaySelectedImage(Bitmap imageBitmap) {
        Bitmap grayscaleBitmap = convertToGrayscale(imageBitmap);
        selectedImageDisplay.setImageBitmap(grayscaleBitmap);
    }
    // This method will help to retrieve the images
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK) {
            Bitmap imageBitmap = (Bitmap) data.getExtras().get("data");
            selectedImage = resizeBitmap(imageBitmap, 224, 224);
            displaySelectedImage(selectedImage);
            errorText.setText("");
        } else if (requestCode == 2 && resultCode == RESULT_OK) {
            if (data != null) {
                Uri selectedImageUri = data.getData();
                try {
                    Bitmap imageBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImageUri);
                    selectedImage = resizeBitmap(imageBitmap, 224, 224);
                    displaySelectedImage(selectedImage);
                    errorText.setText("");
                } catch (IOException e) {
                    Log.d("myapp", "error Galery: " + e.getMessage());
                }
            }
        }
    }

    // Method to resize bitmap
    private Bitmap resizeBitmap(Bitmap bitmap, int width, int height) {
        return Bitmap.createScaledBitmap(bitmap, width, height, true);
    }

    // function to convert an image bitmap into a normalized bytebuffer to be used as an input for the ml model
    private ByteBuffer convertBitmapToByteBuffer(Bitmap bp) {
        Bitmap grayscaleBitmap = convertToGrayscale(bp);
        ByteBuffer imgData = ByteBuffer.allocateDirect(Float.BYTES*224*224*3); // allocate required memory
        imgData.order(ByteOrder.nativeOrder());
        Bitmap bitmap = Bitmap.createScaledBitmap(grayscaleBitmap,224,224,true); // scale the bitmap dimensions to required size for model input
        int [] intValues = new int[224*224];
        bitmap.getPixels(intValues, 0, bitmap.getWidth(), 0, 0, bitmap.getWidth(), bitmap.getHeight());
        // Convert the image to floating point.
        int pixel = 0;
        for (int i = 0; i < 224; ++i) {
            for (int j = 0; j < 224; ++j) {
                final int val = intValues[pixel++];
                imgData.putFloat(((val>> 16) & 0xFF) / 255.f);
                imgData.putFloat(((val>> 8) & 0xFF) / 255.f);
                imgData.putFloat((val & 0xFF) / 255.f);
            }
        }
        return imgData;
    }



    private Bitmap convertToRGB(Bitmap bitmap) {
        Bitmap rgbBitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(rgbBitmap);
        canvas.drawBitmap(bitmap, 0, 0, null);
        return rgbBitmap;
    }

    private Bitmap convertToGrayscale(Bitmap bitmap) {
        Bitmap grayscaleBitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(grayscaleBitmap);
        Paint paint = new Paint();
        ColorMatrix colorMatrix = new ColorMatrix();
        colorMatrix.setSaturation(0); // Set the saturation level to 0 to convert the image to grayscale
        ColorMatrixColorFilter colorMatrixFilter = new ColorMatrixColorFilter(colorMatrix);
        paint.setColorFilter(colorMatrixFilter);
        canvas.drawBitmap(bitmap, 0, 0, paint);
        return grayscaleBitmap;
    }

}