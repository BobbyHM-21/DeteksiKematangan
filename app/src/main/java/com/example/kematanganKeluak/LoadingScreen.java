package com.example.kematanganKeluak;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class LoadingScreen extends AppCompatActivity {
    private ProgressBar progressBar;
    private TextView progressTextView;
    private TextView loadingTextView;
    private TextView titleTextView;
    private TextView glcmtext;
    private TextView knntext;

    private int progressStatus = 0;
    private Handler handler = new Handler(Looper.getMainLooper());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading_screen);

        titleTextView = findViewById(R.id.titleTextView);
        progressBar = findViewById(R.id.progressBar);
        progressTextView = findViewById(R.id.progressTextView);
        loadingTextView = findViewById(R.id.loadingTextView);
        glcmtext = findViewById(R.id.glcmtext);
        knntext = findViewById(R.id.knntext);

        Animation fadeAnimation = new AlphaAnimation(0, 1);
        fadeAnimation.setDuration(1500);
        titleTextView.startAnimation(fadeAnimation);
        glcmtext.startAnimation(fadeAnimation);
        knntext.startAnimation(fadeAnimation);

        // Menggunakan Thread untuk mensimulasikan proses loading
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (progressStatus < 100) {
                    progressStatus += 10;

                    // Update progress bar dan angka persentase pada UI Thread
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            progressBar.setProgress(progressStatus);
                            progressTextView.setText(progressStatus + "%");

                            // Animasi teks "Loading..."
                            loadingTextView.setText("Loading");
                            for (int i = 0; i < progressStatus % 4; i++) {
                                loadingTextView.append(".");
                            }
                        }
                    });

                    try {
                        // Simulasi proses loading dengan sleep
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

                // Setelah selesai loading, lanjut ke tampilan berikutnya (misalnya MainActivity)
                // Ganti Login.class dengan tampilan berikutnya yang diinginkan
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        startActivity(new Intent(LoadingScreen.this, Login.class));
                        finish();
                    }
                });
            }
        }).start();
    }
}
