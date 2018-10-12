package com.scorg.regform.customui;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatEditText;
import android.util.AttributeSet;

import com.scorg.regform.R;

public class CustomEditText extends AppCompatEditText {
    public CustomEditText(Context context, AttributeSet attrs) {
        super(context, attrs);

        if (isInEditMode()) {
            return;
        }

        setCustomFont(context, attrs);
        setBackgroundResource(R.drawable.edittext_selector);
        setTextColor(ContextCompat.getColor(context, R.color.edit_text_color));
        int padding = context.getResources().getDimensionPixelSize(R.dimen.textbox_padding);
        setPadding(padding, padding, padding, padding);
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