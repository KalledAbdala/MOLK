package com.app.molk;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

public class PieChartView extends View {

    private Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private RectF rectF = new RectF();
    private float[] values = {40, 25, 15, 10, 10};
    private int[] colors = {
            0xFF0AAE77,
            0xFF3EB48D,
            0xFFF2B705,
            0xFFE25B45,
            0xFF9B59B6
    };

    public PieChartView(Context context) {
        super(context);
    }

    public PieChartView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public PieChartView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int width = getWidth();
        int height = getHeight();

        float padding = 40;
        rectF.set(padding, padding, width - padding, height - padding);

        float total = 0;
        for (float v : values) {
            total += v;
        }

        float startAngle = -90f;

        for (int i = 0; i < values.length; i++) {
            paint.setColor(colors[i]);
            float sweepAngle = (values[i] / total) * 360f;
            canvas.drawArc(rectF, startAngle, sweepAngle, true, paint);
            startAngle += sweepAngle;
        }
    }
}
