package com.scorg.forms.customui;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;

import com.scorg.forms.R;

public class CustomButton extends AppCompatButton {
    public CustomButton(Context context, AttributeSet attrs) {
        super(context, attrs);

        if (isInEditMode()) {
            return;
        }

        setCustomFont(context, attrs);
    }

    private void setCustomFont(Context ctx, AttributeSet attrs) {
        TypedArray a = ctx.obtainStyledAttributes(attrs, R.styleable.CustomTextView);
        String customFont = a.getString(R.styleable.CustomTextView_customFont);
        setCustomFont(ctx, customFont == null ? ctx.getResources().getString(R.string.roboto_regular) : customFont);
        a.recycle();
    }

    public void setCustomFont(Context ctx, String asset) {
        Typeface typeface = Typeface.createFromAsset(ctx.getAssets(), "font/" + asset);
        setTypeface(typeface);
    }
}