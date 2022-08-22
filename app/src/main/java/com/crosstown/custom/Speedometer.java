package com.crosstown.custom;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.view.View;

import com.crosstown.utilities.Utilities;

public class Speedometer extends View
{
    public Speedometer(Context context) {
        super(context);
    }

    @Override
    protected void onDraw(Canvas canvas)
    {
        super.onDraw(canvas);
        Paint p = new Paint();
        p.setColor(Color.RED);
        int strokeWidth = Utilities.PX2DP(5);
        p.setStyle(Paint.Style.STROKE);
        p.setStrokeWidth(2f);
        int radius = Utilities.PX2DP(35) - strokeWidth;
        canvas.drawCircle(radius + strokeWidth, radius + strokeWidth, radius, p);

        Paint p1 = new Paint();
        DashPathEffect dashPath = new DashPathEffect(new float[] {2, 6}, (float)1.0);
        p1.setPathEffect(dashPath);
        p1.setStyle(Paint.Style.STROKE);
        p1.setStrokeWidth((float)strokeWidth);
        radius = Utilities.PX2DP(35) - strokeWidth / 2;
        canvas.drawCircle(radius + strokeWidth / 2, radius + strokeWidth / 2, radius, p1);
        invalidate();
    }
}
