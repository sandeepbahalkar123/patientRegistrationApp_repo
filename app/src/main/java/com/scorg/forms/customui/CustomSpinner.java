package com.scorg.forms.customui;

import android.content.Context;
import android.support.v7.widget.AppCompatSpinner;
import android.util.AttributeSet;

import com.scorg.forms.R;

public class CustomSpinner extends AppCompatSpinner {
    public CustomSpinner(Context context, AttributeSet attrs) {
        super(context, attrs);

        setCustomProperties(context);
    }

    private void setCustomProperties(Context context) {
        setPadding(context.getResources().getDimensionPixelSize(R.dimen.spinner_left), 0, context.getResources().getDimensionPixelSize(R.dimen.spinner_right), 0);
        setBackgroundResource(R.drawable.dropdown_selector);
    }
}