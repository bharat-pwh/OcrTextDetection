package com.example.ocrtextdetection;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Matrix;
import android.media.Image;
import android.util.Size;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageProxy;

import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.text.Text;
import com.google.mlkit.vision.text.TextRecognition;
import com.google.mlkit.vision.text.latin.TextRecognizerOptions;


public class TextReader implements ImageAnalysis.Analyzer {

    private TextFoundListener listener;

    public TextReader(Context context, TextFoundListener listener) {
        this.listener = listener;
    }

    @SuppressLint("UnsafeOptInUsageError")
    @Override
    public void analyze(@NonNull ImageProxy imageProxy) {

        if (imageProxy != null) {
            process(imageProxy.getImage(), imageProxy);
        }
    }

    private void process(Image image, ImageProxy imageProxy) {
        readTextFromImage(InputImage.fromMediaImage(image, imageProxy.getImageInfo().getRotationDegrees()), imageProxy);
    }

    private void readTextFromImage(InputImage image, ImageProxy imageProxy) {
        TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
                .process(image)
                .addOnSuccessListener(visionText -> {
                            processTextFromImage(visionText, imageProxy);
                            imageProxy.close();
                        }
                ).addOnFailureListener(error -> {
                    error.printStackTrace();
                    imageProxy.close();
                });
    }

    private void processTextFromImage(Text visionText, ImageProxy imageProxy) {
        if (visionText.getTextBlocks().size() > 0)
            listener.onTextFound(visionText, imageProxy);

    }


    @Nullable
    @Override
    public Size getDefaultTargetResolution() {
        return ImageAnalysis.Analyzer.super.getDefaultTargetResolution();
    }

    @Override
    public int getTargetCoordinateSystem() {
        return ImageAnalysis.Analyzer.super.getTargetCoordinateSystem();
    }

    @Override
    public void updateTransform(@Nullable Matrix matrix) {
        ImageAnalysis.Analyzer.super.updateTransform(matrix);
    }
}
