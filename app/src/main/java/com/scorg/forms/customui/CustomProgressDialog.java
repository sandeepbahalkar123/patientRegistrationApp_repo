package com.scorg.forms.customui;

import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.WindowManager;
import android.widget.ProgressBar;

import com.scorg.forms.R;

public class CustomProgressDialog extends Dialog {

    public CustomProgressDialog(Context context) {
        super(context, R.style.TransparentProgressDialog);
        WindowManager.LayoutParams wlmp = getWindow().getAttributes();
        wlmp.gravity = Gravity.CENTER_HORIZONTAL;
        getWindow().setAttributes(wlmp);

        setContentView(R.layout.mydialog);
        setTitle(null);
        setCancelable(false);
        setOnCancelListener(null);
    }

}
