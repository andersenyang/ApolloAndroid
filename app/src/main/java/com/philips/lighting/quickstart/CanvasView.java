package com.philips.lighting.quickstart;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.View;
import android.util.Log;

import java.util.ArrayList;

/**
 * Created by supreet on 2016-03-09.
 */
public class CanvasView extends View {

    Paint paint;
    Paint backgroundPaint;
    ArrayList<Integer> x_arr;
    ArrayList<Integer> y_arr;
    int size;
    int viewWidth;
    int viewHeight;
    int blockWidth;

    public CanvasView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.paint = new Paint();
        this.backgroundPaint = new Paint();
        this.x_arr = new ArrayList<Integer>();
        this.y_arr = new ArrayList<Integer>();
        this.viewWidth = 0;
        this.viewHeight = 0;
        this.blockWidth = 0;
        this.size = 48;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        paint.setColor(Color.MAGENTA);
        backgroundPaint.setColor(Color.LTGRAY);
        for (int i = 0; i < this.size; i++) {
            int col = i % 8 + 1;
            int row = i / 8 + 1;
            int x = col * this.blockWidth;
            int y = row * this.blockWidth;
            canvas.drawCircle(x, y, 10, this.backgroundPaint);
        }

        for (int i = 0; i < this.size; i++) {
            if (i < this.x_arr.size() && i < this.y_arr.size()) {
                canvas.drawCircle(this.x_arr.get(i), this.y_arr.get(i), 10, this.paint);
            }
        }

        invalidate();

    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        this.viewWidth = this.getWidth();
        this.blockWidth = this.viewWidth / 9;
        this.viewHeight = this.blockWidth * 7;
        invalidate();
    }

    public void reset() {
        this.x_arr = new ArrayList<Integer>();
        this.y_arr = new ArrayList<Integer>();
    }
    public void setPoint(int x) {
        //Log.d("Imperium", String.valueOf(x));
        int col = x % 8 + 1;
        int row = x / 8 + 1;
        this.x_arr.add(col * this.blockWidth);
        this.y_arr.add(row * this.blockWidth);
        //Log.d("Imperium", String.valueOf(col * this.blockWidth));
        //Log.d("Imperium", String.valueOf(row * this.blockWidth));
        invalidate();
    }
}
