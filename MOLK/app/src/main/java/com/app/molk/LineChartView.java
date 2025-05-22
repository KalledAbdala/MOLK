package com.app.molk;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

public class LineChartView extends View {

    private Paint linePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint pointPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint axisPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    private int[] values = {10, 20, 35, 40, 30, 45};
    private String[] months = {"Jan", "Fev", "Mar", "Abr", "Mai", "Jun"};

    public LineChartView(Context context) {
        super(context);
        init();
    }

    public LineChartView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public LineChartView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        linePaint.setColor(0xFF3EB48D);
        linePaint.setStrokeWidth(6f);
        linePaint.setStyle(Paint.Style.STROKE);

        pointPaint.setColor(0xFF3EB48D);
        pointPaint.setStyle(Paint.Style.FILL);

        axisPaint.setColor(Color.DKGRAY);
        axisPaint.setStrokeWidth(2f);

        textPaint.setColor(Color.DKGRAY);
        textPaint.setTextSize(32f);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int width = getWidth();
        int height = getHeight();
        int paddingLeft = 80;
        int paddingRight = 40;
        int paddingTop = 40;
        int paddingBottom = 80;

        // desenha eixo X e Y
        canvas.drawLine(paddingLeft, height - paddingBottom, width - paddingRight, height - paddingBottom, axisPaint); // eixo X
        canvas.drawLine(paddingLeft, paddingTop, paddingLeft, height - paddingBottom, axisPaint); // eixo Y

        int graphWidth = width - paddingLeft - paddingRight;
        int graphHeight = height - paddingTop - paddingBottom;

        int maxValue = 50; // máximo do eixo Y

        // desenha linhas do gráfico
        float prevX = 0, prevY = 0;
        for (int i = 0; i < values.length; i++) {
            float x = paddingLeft + (graphWidth / (values.length - 1f)) * i;
            float y = paddingTop + graphHeight - ((values[i] / (float) maxValue) * graphHeight);

            // desenha ponto
            canvas.drawCircle(x, y, 12, pointPaint);

            // desenha linha ligando pontos
            if (i > 0) {
                canvas.drawLine(prevX, prevY, x, y, linePaint);
            }
            prevX = x;
            prevY = y;

            // desenha label mês eixo X
            float textWidth = textPaint.measureText(months[i]);
            canvas.drawText(months[i], x - textWidth / 2, height - paddingBottom + 40, textPaint);

            // desenha valor eixo Y na esquerda (em 0, 10, 20, 30, 40, 50)
            if (i == 0) {
                for (int v = 0; v <= maxValue; v += 10) {
                    float yPos = paddingTop + graphHeight - ((v / (float) maxValue) * graphHeight);
                    String valText = String.valueOf(v);
                    canvas.drawText(valText, paddingLeft - 60, yPos + 10, textPaint);
                    canvas.drawLine(paddingLeft - 10, yPos, paddingLeft, yPos, axisPaint);
                }
            }
        }
    }
}
