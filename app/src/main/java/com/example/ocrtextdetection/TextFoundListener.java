package com.example.ocrtextdetection;

import androidx.camera.core.ImageProxy;

import com.google.mlkit.vision.text.Text;

public interface TextFoundListener {
    public void onTextFound(Text text, ImageProxy imageProxy);
}
