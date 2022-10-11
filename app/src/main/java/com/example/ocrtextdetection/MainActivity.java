package com.example.ocrtextdetection;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Size;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.AspectRatio;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.ocrtextdetection.databinding.ActivityMainBinding;
import com.google.common.util.concurrent.ListenableFuture;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private Executor cameraExecutor;
    private ImageAnalysis imageAnalyser;
    private Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        handler = new Handler();

        imageAnalyser = new ImageAnalysis.Builder()
//                .setTargetResolution(new Size(binding.cameraPreviewView.getWidth(),
//                        binding.cameraPreviewView.getHeight()))
                .setTargetAspectRatio(AspectRatio.RATIO_16_9)
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build();

        cameraExecutor = Executors.newSingleThreadExecutor();
        imageAnalyser.setAnalyzer(cameraExecutor, new TextReader(this,
                textBlock -> {
                    binding.paintView.setValues(textBlock);
                    binding.paintView.destroyDrawingCache();
                }
        ));

        if (ContextCompat.checkSelfPermission(this, object.REQUIRED_PERMISSIONS[0]) == PackageManager.PERMISSION_GRANTED) {
            startCamera();
        } else {
            requestPermissions();
        }

    }

    private void requestPermissions() {
        ActivityCompat.requestPermissions(
                this,
                object.REQUIRED_PERMISSIONS,
                object.REQUEST_CODE_PERMISSIONS
        );
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == object.REQUEST_CODE_PERMISSIONS) {
            if (ContextCompat.checkSelfPermission(this, object.REQUIRED_PERMISSIONS[0]) == PackageManager.PERMISSION_GRANTED) {
                startCamera();
            } else {
                Toast.makeText(
                        this,
                        "Permissions not granted.",
                        Toast.LENGTH_SHORT
                ).show();
                finish();
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }


    private void startCamera() {
        ListenableFuture<ProcessCameraProvider> cameraProviderFuture = ProcessCameraProvider.getInstance(this);

        cameraProviderFuture.addListener(
                new Runnable() {
                    @Override
                    public void run() {
                        Preview preview = new Preview.Builder().build();
                        preview.setSurfaceProvider(
                                binding.cameraPreviewView.getSurfaceProvider()
                        );
                        try {
                            CameraSelector cameraSelector = new CameraSelector.Builder()
                                    .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                                    .build();
                            cameraProviderFuture.get().unbindAll();
                            cameraProviderFuture.get().bindToLifecycle(
                                    MainActivity.this,
                                    cameraSelector,
                                    imageAnalyser,
                                    preview);
                        } catch (ExecutionException e) {
                            e.printStackTrace();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                },
                ContextCompat.getMainExecutor(this)
        );


    }
}

class object {
    String TAG = MainActivity.class.getSimpleName();
    static final int REQUEST_CODE_PERMISSIONS = 10;
    static String[] REQUIRED_PERMISSIONS = new String[]{Manifest.permission.CAMERA};
}