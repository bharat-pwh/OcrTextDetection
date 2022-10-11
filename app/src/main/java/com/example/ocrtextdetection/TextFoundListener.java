package com.example.ocrtextdetection;

import com.google.mlkit.vision.text.Text;

public interface TextFoundListener {
    public void onTextFound(Text.TextBlock text);
}
