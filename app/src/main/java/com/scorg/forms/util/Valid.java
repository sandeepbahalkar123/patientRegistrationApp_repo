package com.scorg.forms.util;

import android.content.Context;
import android.widget.Toast;

import com.scorg.forms.R;

/**
 * Created by ganeshshirole on 10/10/17.
 */

public class Valid {

    public static boolean validateMobileNo(String mobile, Context context, boolean isVisible) {
        String message = null;
        if (mobile.isEmpty()) {
            message = context.getString(R.string.enter_mobile_no);
            if (isVisible)
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
        } else if ((mobile.trim().length() < 10) || !(mobile.trim().startsWith("7") || mobile.trim().startsWith("8") || mobile.trim().startsWith("9"))) {
            message = context.getString(R.string.err_invalid_mobile_no);
            if (isVisible)
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
        }
        return message == null;
    }

    public static boolean validateEmail(String email, Context context, boolean isVisible) {
        String message = null;
        if (email.isEmpty()) {
            message = context.getString(R.string.enter_email);
            if (isVisible)
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            message = context.getString(R.string.err_invalid_email);
            if (isVisible)
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
        }
        return message == null;
    }
}
