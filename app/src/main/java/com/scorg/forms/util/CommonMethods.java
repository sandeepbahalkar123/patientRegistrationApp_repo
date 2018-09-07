package com.scorg.forms.util;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.scorg.forms.R;
import com.scorg.forms.interfaces.CheckIpConnection;

import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.Months;
import org.joda.time.Years;

import java.lang.reflect.Field;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by ganeshshirole on 9/10/17.
 */

public class CommonMethods {

    private static final AtomicInteger sNextGeneratedId = new AtomicInteger(1);
    private static final String TAG = "CommonMethods";

    private static CheckIpConnection mCheckIpConnection;

    public static int getAge(int year, int month, int day) {

        Calendar cal = Calendar.getInstance();
        int y, m, d, noofyears;

        y = cal.get(Calendar.YEAR);// current year ,
        m = cal.get(Calendar.MONTH);// current month
        d = cal.get(Calendar.DAY_OF_MONTH);//current day
        cal.set(year, month, day);// here ur date
        noofyears = y - cal.get(Calendar.YEAR);
        if ((m < cal.get(Calendar.MONTH))
                || ((m == cal.get(Calendar.MONTH)) && (d < cal
                .get(Calendar.DAY_OF_MONTH)))) {
            --noofyears;
        }

        return noofyears;
    }

    public static String calculateAge(String dateStart, String dataFormat) {

        String ageText = "0 day";

        SimpleDateFormat format = new SimpleDateFormat(dataFormat, Locale.US);

        Date d1;
//        Date d2;

        try {
            d1 = format.parse(dateStart);
//            d2 = format.parse(dateStop);

            DateTime dt1 = new DateTime(d1);
//            DateTime dt2 = new DateTime(d2);
            DateTime dt2 = new DateTime();

            if (Years.yearsBetween(dt1, dt2).getYears() > 0)
                ageText = Years.yearsBetween(dt1, dt2).getYears() > 1 ? Years.yearsBetween(dt1, dt2).getYears() + " years" : Years.yearsBetween(dt1, dt2).getYears() + " year";
            else if (Months.monthsBetween(dt1, dt2).getMonths() > 0)
                ageText = Months.monthsBetween(dt1, dt2).getMonths() > 1 ? Months.monthsBetween(dt1, dt2).getMonths() + " months" : Months.monthsBetween(dt1, dt2).getMonths() + " month";
            else if (Days.daysBetween(dt1, dt2).getDays() > 0)
                ageText = Days.daysBetween(dt1, dt2).getDays() > 1 ? Days.daysBetween(dt1, dt2).getDays() + " days" : Days.daysBetween(dt1, dt2).getDays() + " day";

            System.out.print(ageText);

//            System.out.print(Hours.hoursBetween(dt1, dt2).getHours() % 24 + " hours, ");
//            System.out.print(Minutes.minutesBetween(dt1, dt2).getMinutes() % 60 + " minutes, ");
//            System.out.print(Seconds.secondsBetween(dt1, dt2).getSeconds() % 60 + " seconds.");

        } catch (Exception e) {
            e.printStackTrace();
        }

        return ageText;
    }

    /*public static Age calculateYearsMonthsDays(String dateStart, String dataFormat) {
        SimpleDateFormat format = new SimpleDateFormat(dataFormat, Locale.US);
        try {
            Date birthDate = format.parse(dateStart);
            int years;
            int months;
            int days;
            //create calendar object for birth day
            Calendar birthDay = Calendar.getInstance();
            birthDay.setTimeInMillis(birthDate.getTime());
            //create calendar object for current day
            long currentTime = System.currentTimeMillis();
            Calendar now = Calendar.getInstance();
            now.setTimeInMillis(currentTime);
            //Get difference between years
            years = now.get(Calendar.YEAR) - birthDay.get(Calendar.YEAR);
            int currMonth = now.get(Calendar.MONTH) + 1;
            int birthMonth = birthDay.get(Calendar.MONTH) + 1;
            //Get difference between months
            months = currMonth - birthMonth;
            //if month difference is in negative then reduce years by one and calculate the number of months.
            if (months < 0) {
                years--;
                months = 12 - birthMonth + currMonth;
                if (now.get(Calendar.DATE) < birthDay.get(Calendar.DATE))
                    months--;
            } else if (months == 0 && now.get(Calendar.DATE) < birthDay.get(Calendar.DATE)) {
                years--;
                months = 11;
            }
            //Calculate the days
            if (now.get(Calendar.DATE) > birthDay.get(Calendar.DATE))
                days = now.get(Calendar.DATE) - birthDay.get(Calendar.DATE);
            else if (now.get(Calendar.DATE) < birthDay.get(Calendar.DATE)) {
                int today = now.get(Calendar.DAY_OF_MONTH);
                now.add(Calendar.MONTH, -1);
                days = now.getActualMaximum(Calendar.DAY_OF_MONTH) - birthDay.get(Calendar.DAY_OF_MONTH) + today;
            } else {
                days = 0;
                if (months == 12) {
                    years++;
                    months = 0;
                }
            }
            //Create new Age object
            return new Age(days, months, years);
        } catch (ParseException e) {
            return new Age(0, 0, 0);
        }
    }*/

    public static void hideKeyboard(Context cntx) {
        // Check if no view has focus:
        View view = ((Activity) cntx).getCurrentFocus();
        if (view != null) {
            InputMethodManager inputManager = (InputMethodManager) cntx.getSystemService(Context.INPUT_METHOD_SERVICE);
            inputManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    public static void setEditTextLineColor(EditText yourEditText, int color) {

        Drawable drawable = yourEditText.getBackground(); // get current EditText drawable
        drawable.setColorFilter(color, PorterDuff.Mode.SRC_ATOP); // change the drawable color

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            yourEditText.setBackground(drawable); // set the new drawable to EditText
        } else {
            yourEditText.setBackgroundDrawable(drawable); // use setBackgroundDrawable because setBackground required API 16
        }

        // Set Cursor Color

        try {
            Field fCursorDrawableRes = TextView.class.getDeclaredField("mCursorDrawableRes");
            fCursorDrawableRes.setAccessible(true);
            int mCursorDrawableRes = fCursorDrawableRes.getInt(yourEditText);
            Field fEditor = TextView.class.getDeclaredField("mEditor");
            fEditor.setAccessible(true);
            Object editor = fEditor.get(yourEditText);
            Class<?> clazz = editor.getClass();
            Field fCursorDrawable = clazz.getDeclaredField("mCursorDrawable");
            fCursorDrawable.setAccessible(true);
            Drawable[] drawables = new Drawable[2];
            drawables[0] = yourEditText.getContext().getResources().getDrawable(mCursorDrawableRes);
            drawables[1] = yourEditText.getContext().getResources().getDrawable(mCursorDrawableRes);
            drawables[0].setColorFilter(color, PorterDuff.Mode.SRC_IN);
            drawables[1].setColorFilter(color, PorterDuff.Mode.SRC_IN);
            fCursorDrawable.set(editor, drawables);
        } catch (Throwable ignored) {
        }
    }

    /**
     * Generate a value suitable for use in {#setId(int)}.
     * This value will not collide with ID values generated at build time by aapt for R.id.
     *
     * @return a generated ID value
     */
    public static int generateViewId() {
        for (; ; ) {
            final int result = sNextGeneratedId.get();
            // aapt-generated IDs have the high byte nonzero; clamp to the range under that.
            int newValue = result + 1;
            if (newValue > 0x00FFFFFF) newValue = 1; // Roll over to 1, not 0.
            if (sNextGeneratedId.compareAndSet(result, newValue)) {
                Log.d("GENERATED_ID:", " " + result);
                return result;
            }
        }
    }

    public static void log(String tag, String message) {
        Log.e(tag, "PatientRegApp" + message);
    }

    public static void showToast(Context context, String error) {
        Toast.makeText(context, error, Toast.LENGTH_SHORT).show();
    }


    public static void showSnack(View mViewById, String msg) {
        if (mViewById != null) {
            Snackbar.make(mViewById, msg, Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
        } else {
            Log.d(TAG, "null snacbar view" + msg);
        }
    }

    //this alert is shown for input of serverpath
    public static Dialog showAlertDialog(Context activity, String dialogHeader, CheckIpConnection checkIpConnection, final boolean isFinishActivity) {
        final Context mContext = activity;
        mCheckIpConnection = checkIpConnection;
        final Dialog dialog = new Dialog(activity);

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.ip_dialog_ok_cancel);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        if (dialogHeader != null)
            ((TextView) dialog.findViewById(R.id.textView_dialog_heading)).setText(dialogHeader);

        dialog.findViewById(R.id.button_ok).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                EditText etServerPath = dialog.findViewById(R.id.et_server_path);

                /*if (isValidIP(etServerPath.getText().toString())) {*/
                String mServerPath = etServerPath.getText().toString();
                Log.e(TAG, "SERVER PATH===" + mServerPath);
                mCheckIpConnection.onOkButtonClickListener(mServerPath, mContext, dialog);
                /*} else {
                    Toast.makeText(mContext, R.string.error_in_ip, Toast.LENGTH_LONG).show();
                }*/
            }
        });
        dialog.findViewById(R.id.button_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                if (isFinishActivity)
                    ((Activity) mContext).finish();
            }
        });

        dialog.show();

        return dialog;
    }

    private static boolean isValidIP(String ipAddr) {

        Pattern ptn = Pattern.compile("(\\b(1?[0-9]{1,2}|2[0-4][0-9]|25[0-5])\\b)\\.(\\b(1?[0-9]{1,2}|2[0-4][0-9]|25[0-5])\\b)\\.(\\b(1?[0-9]{1,2}|2[0-4][0-9]|25[0-5])\\b)\\.(\\b(1?[0-9]{1,2}|2[0-4][0-9]|25[0-5])\\b)\\:(\\d{1,4})$");
        Matcher mtch = ptn.matcher(ipAddr);
        return mtch.find();
    }

    public static String getFormattedDate(String strDate, String sourceFormat, String destinyFormat) {

        if (!strDate.equals("")) {
            SimpleDateFormat df;
            df = new SimpleDateFormat(sourceFormat, Locale.US);
            Date date;
            try {
                date = df.parse(strDate);
                df = new SimpleDateFormat(destinyFormat, Locale.US);
                return df.format(date);
            } catch (ParseException e) {
                return "";
            }
        } else return "";
    }
}
