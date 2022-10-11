package com.example.ocrtextdetection;

import static android.graphics.Typeface.DEFAULT;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.View;

import com.google.mlkit.vision.text.Text;


public class PaintView extends View {

    // below we are creating variables for our paint
    private Point[] points;
    private Text.TextBlock textBlock;
    private static float ratio = 0f;
    private Context context;

    // and a floating variable for our left arc.
    public void setValues(Text.TextBlock textBlock) {
        this.points = textBlock.getCornerPoints();
        this.textBlock = textBlock;
        postInvalidate();
    }

    @SuppressLint("ResourceAsColor")
    public PaintView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.context = context;
//        this.points = textBlock.getCornerPoints();
//        this.textBlock = textBlock;
        // on below line we are initializing our paint variable for our text


        // on below line we are creating a display metrics
        DisplayMetrics displayMetrics = new DisplayMetrics();

        // on below line we are getting display metrics.
        ((Activity) getContext()).getWindowManager()
                .getDefaultDisplay()
                .getMetrics(displayMetrics);
        ratio = ((float) displayMetrics.heightPixels / (float) displayMetrics.widthPixels);
        // on below line we are assigning
        // the value to the arc left.
        // on below line we are creating
        // a new variable for our paint
    }

    // below method is use to generate px from DP.
    public static float pxFromDp(final Context context, final float dp) {
        return dp * ratio;
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
        if (textBlock != null) {
            @SuppressLint("DrawAllocation")
            Paint paint = new Paint(Paint.LINEAR_TEXT_FLAG | Paint.ANTI_ALIAS_FLAG);
            paint.setTypeface(Typeface.create(DEFAULT, Typeface.BOLD));
            paint.setColor(Color.WHITE);
            for (int i = 0; i < points.length; i++) {
                points[i].x = (int) pxFromDp(context, points[i].x);
                points[i].y = (int) pxFromDp(context, points[i].y);
            }
            float pts[] = {
                    points[0].x, points[0].y, points[1].x, points[1].y,
                    points[1].x, points[1].y, points[2].x, points[2].y,
                    points[2].x, points[2].y, points[3].x, points[3].y,
                    points[3].x, points[3].y, points[0].x, points[0].y
            };
            canvas.drawLines(pts, paint);
            for (Text.Line line : textBlock.getLines()) {
                String lineText = line.getText();
                Point[] linePoints = line.getCornerPoints();
                float averageHeight = line.getBoundingBox().height();
                float textSize = averageHeight * 0.8f;
                float offset = averageHeight / 4;
                paint.setTextSize(textSize);
                Path path = new Path();
                assert linePoints != null;
                path.moveTo(linePoints[3].x * ratio, (linePoints[3].y * ratio)-offset);
                path.lineTo(linePoints[2].x * ratio, (linePoints[2].y * ratio)-offset);
                canvas.drawTextOnPath(lineText,
                        path,
                        0f, 0f,
                        paint
                );
            }
            //    on below line we are adding text using paint in our canvas.
//            canvas.drawText(textBlock.getText(),
//                    (float) (getWidth() * 0.3),
//                    (float) (getHeight() * 0.8), textPaint);

        }

    }
}