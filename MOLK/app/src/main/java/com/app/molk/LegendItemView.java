package com.app.molk;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.view.View;

public class LegendItemView extends LinearLayout {

    private View colorBox;
    private TextView textView;

    public LegendItemView(Context context) {
        super(context);
        init(null);
    }

    public LegendItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public LegendItemView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    private void init(AttributeSet attrs) {
        setOrientation(HORIZONTAL);
        setGravity(Gravity.CENTER_VERTICAL);
        int color = Color.BLACK;
        String text = "";

        if (attrs != null) {
            TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.LegendItemView);
            color = a.getColor(R.styleable.LegendItemView_legendColor, Color.BLACK);
            text = a.getString(R.styleable.LegendItemView_legendText);
            a.recycle();
        }

        colorBox = new View(getContext());
        LayoutParams colorParams = new LayoutParams(40, 40);
        colorBox.setLayoutParams(colorParams);
        colorBox.setBackgroundColor(color);

        textView = new TextView(getContext());
        LayoutParams textParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        textParams.leftMargin = 16;
        textView.setLayoutParams(textParams);
        textView.setText(text);
        textView.setTextSize(16f);
        textView.setTextColor(Color.DKGRAY);

        addView(colorBox);
        addView(textView);
    }
}
