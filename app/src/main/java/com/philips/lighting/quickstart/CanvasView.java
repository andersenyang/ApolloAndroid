package com.philips.lighting.quickstart;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.View;

/**
 * Created by supreet on 2016-03-09.
 */
public class CanvasView extends View {

    Paint paint;
    int x;
    int y;

    public CanvasView(Context context) {
        super(context);
        paint = new Paint();
        x = 0;
        y = 0;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        paint.setColor(Color.GREEN);

        canvas.drawCircle(x,y,10,paint);
        invalidate();

    }
}
