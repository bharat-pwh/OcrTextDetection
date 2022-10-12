package com.example.ocrtextdetection;

import static android.graphics.Typeface.DEFAULT;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.os.Build;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.View;

import androidx.camera.core.ImageProxy;

import com.google.mlkit.vision.text.Text;


public class PaintView extends View {

    // below we are creating variables for our paint
    private Text text;
    private static float ratio = 0f;
    private Context context;
    private ImageProxy imageProxy;
    private Matrix transformationMatrix = new Matrix();
    float scaleFactor = 0;
    float postScaleWidthOffset = 0;
    float postScaleHeightOffset = 0;

    // and a floating variable for our left arc.
    public void setValues(Text text, ImageProxy imageProxy) {
        this.text = text;
        this.imageProxy = imageProxy;

        postInvalidate();
    }

    public float translateX(float x) {

        return (x * scaleFactor) - postScaleWidthOffset;

    }

    /**
     * Adjusts the y coordinate from the image's coordinate system to the view coordinate system.
     */
    public float translateY(float y) {
        return (y * scaleFactor) - postScaleHeightOffset;
    }

    @SuppressLint("ResourceAsColor")
    public PaintView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.context = context;
//        this.points = textBlock.getCornerPoints();
//        this.textBlock = textBlock;

        // on below line we are creating a display metrics

    }

    // below method is use to generate px from DP.
    public static float pxFromDp(final float dp) {
        return dp * ratio;
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
        if (text != null) {
            @SuppressLint("DrawAllocation")
            Paint paint = new Paint(Paint.LINEAR_TEXT_FLAG | Paint.ANTI_ALIAS_FLAG);
            paint.setTypeface(Typeface.create(DEFAULT, Typeface.BOLD));
            paint.setColor(Color.WHITE);
//            updateTransformationIfNeeded();
            DisplayMetrics displayMetrics = new DisplayMetrics();

            // on below line we are getting display metrics.
            ((Activity) getContext()).getWindowManager()
                    .getDefaultDisplay()
                    .getMetrics(displayMetrics);

            ratio = ((float) getHeight() / (float) imageProxy.getWidth());


            for (Text.TextBlock textBlock : text.getTextBlocks()) {
                Point[] points = textBlock.getCornerPoints();
                for (int i = 0; i < points.length; i++) {
                    points[i].x = (int) pxFromDp(points[i].x);
                    points[i].y = (int) pxFromDp(points[i].y);

                }
                assert points != null;
                float[] pts = {
                        points[0].x, points[0].y, points[1].x, points[1].y,
                        points[1].x, points[1].y, points[2].x, points[2].y,
                        points[2].x, points[2].y, points[3].x, points[3].y,
                        points[3].x, points[3].y, points[0].x, points[0].y
                };
                canvas.drawLines(pts, paint);
                for (Text.Line line : textBlock.getLines()) {
                    String lineText = line.getText();
                    Point[] linePoints = line.getCornerPoints();
                    assert linePoints != null;
                    float averageHeight = ((linePoints[3].y * ratio - linePoints[0].y * ratio)+ (linePoints[2].y* ratio - linePoints[1].y* ratio)) / 2.0f;
                    float offset = averageHeight / 4;

                    paint.setTextSize(averageHeight);
                    Path path = new Path();
                    assert linePoints != null;
                    path.moveTo(linePoints[3].x * ratio, (linePoints[3].y * ratio - offset));
                    path.lineTo(linePoints[2].x * ratio, (linePoints[2].y * ratio - offset));
//                    canvas.drawText
                    canvas.drawTextOnPath(lineText,
                            path,
                            0f, 0f,
                            paint
                    );
                }


            }


            //    on below line we are adding text using paint in our canvas.
//            canvas.drawText(textBlock.getText(),
//                    (float) (getWidth() * 0.3),
//                    (float) (getHeight() * 0.8), textPaint);

        }

    }


    private void updateTransformationIfNeeded() {
//        if (!needUpdateTransformation || imageWidth <= 0 || imageHeight <= 0) {
//            return;
//        }

        float viewAspectRatio = (float) getWidth() / getHeight();
        float imageAspectRatio = (float) imageProxy.getWidth() / imageProxy.getHeight();

        if (viewAspectRatio > imageAspectRatio) {
            // The image needs to be vertically cropped to be displayed in this view.
            scaleFactor = (float) getWidth() / imageProxy.getWidth();
            postScaleHeightOffset = ((float) getWidth() / imageAspectRatio - getHeight()) / 2;
        } else {
            // The image needs to be horizontally cropped to be displayed in this view.
            scaleFactor = (float) getHeight() / imageProxy.getHeight();
            postScaleWidthOffset = ((float) getHeight() * imageAspectRatio - getWidth()) / 2;
        }
        transformationMatrix.reset();
        transformationMatrix.setScale(scaleFactor, scaleFactor);
        transformationMatrix.postTranslate(-postScaleWidthOffset, -postScaleHeightOffset);
//        if (isImageFlipped) {
//            transformationMatrix.postScale(-1f, 1f, getWidth() / 2f, getHeight() / 2f);
//        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            transformMatrixToLocal(transformationMatrix);
        }

//        needUpdateTransformation = false;
    }
}