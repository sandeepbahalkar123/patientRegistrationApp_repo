package com.scorg.forms.preference;

import android.content.Context;
import android.content.SharedPreferences;
import android.provider.Settings;
import android.util.Base64;
import android.util.Log;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * Created by Sandeep Bahalkar
 */
public class AppPreferencesManager {

    private static SharedPreferences sharedPreferences = null;
    private static byte[] sKey;

    public static SharedPreferences getSharedPreference(final Context context) {
        if (context != null) {
            if (sharedPreferences == null) {
                sharedPreferences = context.getSharedPreferences(context.getPackageName(), Context.MODE_PRIVATE);
                try {
                    final String key = generateAesKeyName(context);
                    String value = sharedPreferences.getString(key, null);
                    if (value == null) {
                        value = generateAesKeyValue();
                        sharedPreferences.edit().putString(key, value).apply();
                    }
                    sKey = decode(value);
                } catch (Exception e) {
                    throw new IllegalStateException(e);
                }
            }
        }
        return sharedPreferences;
    }

    public static String getString(String key, Context context) {
        //String mKey = encrypt(key);
//        CommonMethods.log(TAG, "getString--mKey--->" + key + "<----mValue-------->" + getSharedPreference(context).getString(key, ""));
        return getSharedPreference(context).getString(key, "");

    }

    public static boolean getBoolean(String key, Context context) {
        //String mKey = encrypt(key);
        /*CommonMethods.log("value", "value--getBoolean----->" + mKey);
        final String encryptedValue = getSharedPreference(context).getString(mKey, "");
        if (encryptedValue == null || encryptedValue.isEmpty()) {
            return false;
        }
        try {
            return Boolean.parseBoolean(decrypt(encryptedValue));
        } catch (NumberFormatException e) {
            throw new ClassCastException(e.getMessage());
        }*/
        return getSharedPreference(context).getBoolean(key, false);

    }

    public static boolean putBoolean(String key, boolean value, Context context) {
        //String mKey = encrypt(key);
        // String mValue = encrypt(Boolean.toString(value));
//        CommonMethods.log(TAG, "--------------------------" + Boolean.toString(value));
        //getSharedPreference(context).edit().putString(mKey, encrypt(mValue)).commit();
        getSharedPreference(context).edit().putBoolean(key, value).apply();
        return value;
    }

    public static void putString(String key, String value, Context context) {
        //String mKey = encrypt(key);
        //String mValue = encrypt(value);
//        CommonMethods.log(TAG, "putString--mKey--->" + key + "<----mValue-------->" + value);
        getSharedPreference(context).edit().putString(key, value).apply();
    }

    public static void putLong(String key, long value, Context context) {
        //String mKey = encrypt(key);
        // String mValue = encrypt(Ln.toString(value));
        getSharedPreference(context).edit().putLong(key, value).apply();
    }

    public static void putInt(String key, int value, Context context) {
//        String mKey = encrypt(key);
        // String mValue = encrypt(Integer.toString(value));
        getSharedPreference(context).edit().putInt(key, value).apply();
    }

    public static int getInt(String key, Context context) {
//        String mKey = encrypt(key);
        /*CommonMethods.log("value", "value--getBoolean----->" + mKey);
        final String encryptedValue = getSharedPreference(context).getString(mKey, "");
        if (encryptedValue == null || encryptedValue.isEmpty()) {
            return 0;
        }
        try {
            return Integer.parseInt(decrypt(encryptedValue));
        } catch (NumberFormatException e) {
            throw new ClassCastException(e.getMessage());
        }*/

        return getSharedPreference(context).getInt(key, 0);

    }

    public static long getLong(String key, Context context) {
       /* String mKey = encrypt(key);
        CommonMethods.log("value", "value--getBoolean----->" + mKey);
        final String encryptedValue = getSharedPreference(context).getLong(mKey, "");
        if (encryptedValue == null || encryptedValue.isEmpty()) {
            return 0;
        }
        try {
//            return Integer.parseInt(decrypt(encryptedValue));
            return Long.valueOf(decrypt(encryptedValue));
        } catch (NumberFormatException e) {
            throw new ClassCastException(e.getMessage());
        }*/

        return getSharedPreference(context).getLong(key, 0);

    }

    public static void clearSharedPref(Context context) {
        getSharedPreference(context).edit().clear().apply();
    }

    private static String encode(byte[] input) {
        return Base64.encodeToString(input, Base64.NO_PADDING | Base64.NO_WRAP);
    }

    private static byte[] decode(String input) {
        return Base64.decode(input, Base64.NO_PADDING | Base64.NO_WRAP);
    }

    private static String encrypt(String cleartext) {
        if (cleartext == null || cleartext.length() == 0) {
            return cleartext;
        }
        try {
            final Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(AppPreferencesManager.sKey, "AES"));
            return encode(cipher.doFinal(cleartext.getBytes("UTF-8")));
        } catch (Exception e) {
            Log.e(AppPreferencesManager.class.getName(), "encrypt", e);
            return null;
        }
    }

    private static String decrypt(String ciphertext) {
        if (ciphertext == null || ciphertext.length() == 0) {
            return ciphertext;
        }
        try {
            final Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(AppPreferencesManager.sKey, "AES"));
            return new String(cipher.doFinal(AppPreferencesManager.decode(ciphertext)), "UTF-8");
        } catch (Exception e) {
            Log.e(AppPreferencesManager.class.getName(), "decrypt", e);
            return null;
        }
    }

    private static String generateAesKeyValue() throws NoSuchAlgorithmException {
        // Do *not* seed secureRandom! Automatically seeded from system entropy
        final SecureRandom random = new SecureRandom();

        // Use the largest AES key length which is supported by the OS
        final KeyGenerator generator = KeyGenerator.getInstance("AES");
        try {
            generator.init(256, random);
        } catch (Exception e) {
            try {
                generator.init(192, random);
            } catch (Exception e1) {
                generator.init(128, random);
            }
        }
        return encode(generator.generateKey().getEncoded());
    }

    private static String generateAesKeyName(Context context) throws InvalidKeySpecException,
            NoSuchAlgorithmException {
        final char[] password = context.getPackageName().toCharArray();
        final byte[] salt = Settings.Secure.getString(context.getContentResolver(),
                Settings.Secure.ANDROID_ID).getBytes();

        // Number of PBKDF2 hardening rounds to use, larger values increase
        // computation time, you should select a value that causes
        // computation to take >100ms
        final int iterations = 1000;

        // Generate a 256-bit key
        final int keyLength = 256;

        final KeySpec spec = new PBEKeySpec(password, salt, iterations, keyLength);
        return encode(SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1")
                .generateSecret(spec).getEncoded());
    }


    public interface PREFERENCES_KEY {
        String SERVER_PATH = "server_path";
        String EMAIL = "email";
        String IS_VALID_IP_CONFIG = "isvalidipconfig";
        String AUTHTOKEN = "authToken";
        String LOGIN_STATUS = "login_status";
        String MOBILE = "mobileNumber";
        String PASSWORD = "password";
        String USER_NAME = "doctorname";
        String PROFILE_PHOTO = "doctorprofile";
        String PROFILE_ID = "doctorid";
        String HOSPITAL_PAT_ID = "hospitalPatId";
    }

    public interface CLINIC_KEY {
        String CLINIC_ID = "clinic_id";
        String CLINIC_NAME = "clinic_name";
        String CLINIC_DOCTOR_NAME = "clinic_doctor_name";
        String CLINIC_ADDRESS = "clinic_address";
        String CLINIC_BIG_LOGO = "clinic_big_logo";
        String CLINIC_SMALL_LOGO = "clinic_small_logo";
        String CLINIC_TAG_LINE = "clinic_tag_line";
        String CLINIC_LOCATION_ID = "clinic_location_id";
        String CLINIC_DOCTOR_ID = "clinic_dr_id";
    }
}