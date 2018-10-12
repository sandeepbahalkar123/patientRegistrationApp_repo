package com.scorg.regform.util;


/**
 * @author Sandeep Bahalkar
 */
public class Constants {

    public static final String YES = "yes";
    public static final String USER_ID = "User-ID";
    public static final String DEVICE_ID = "Device-Id";
    public static final String OS = "OS";
    public static final String OSVERSION = "OsVersion";
    public static final String DEVICE_TYPE = "DeviceType";
    public static final String ACCESS_TOKEN = "accessToken";
    public static final String TOKEN_TYPE = "token_type";
    public static final String REFRESH_TOKEN = "refresh_token";
    public static final String LDPI = "/LDPI/";
    public static final String MDPI = "/MDPI/";
    public static final String HDPI = "/HDPI/";
    public static final String XHDPI = "/XHDPI/";
    public static final String XXHDPI = "/XXHDPI/";
    public static final String XXXHDPI = "/XXXHDPI/";
    public static final String TABLET = "Tablet";
    public static final String PHONE = "Phone";
    public static final String CONTENT_TYPE = "Content-Type";
    public static final String AUTHORIZATION = "Authorization";
    public static final String AUTHORIZATION_TOKEN = "authtoken";
    public static final String AUTH_KEY = "Auth-Key";
    public static final String CLIENT_SERVICE = "Client-Service";
    public static final String GRANT_TYPE_KEY = "grant_type";

    public static final String APPLICATION_FORM_DATA = "multipart/form-data";
    public static final String APPLICATION_URL_ENCODED = "application/x-www-form-urlencoded; charset=UTF-8";
    public static final String APPLICATION_JSON = "application/json; charset=utf-8";
    //--- Request Params
    public static final String USERNAME = "username";
    public static final String PASSWORD = "password";
    public static final String LOGIN_SUCCESS = "LOGIN_SUCCESS";
    public static final String CLIENT_ID_KEY = "client_id";
    public static final String CLIENT_ID_VALUE = "334a059d82304f4e9892ee5932f81425";
    public static final String TRUE = "true";
    public static final String FALSE = "false";

    public static final String BLANK = "";
    //Click codes

    public static final String TASK_LOGIN = Constants.BLANK + 1;
    public static final String TASK_CHECK_SERVER_CONNECTION = Constants.BLANK + 2;
    public static final String GET_PROFILE_LIST = Constants.BLANK + 3;
    public static final String POST_PERSONAL_DATA = Constants.BLANK + 4;
    public static final String GET_MASTER_DATA = Constants.BLANK + 5;
    public static final String SAVE_FORM_DATA = Constants.BLANK + 6;
    public static final String GET_REGISTERED_USER = Constants.BLANK + 7;
    public static final String VALIDATE_FIELD = Constants.BLANK + 8;
    public static final Integer SUCCESS = 200;

    public interface INPUT_TYPE {
        String NAME = "name";
        String MOBILE = "mobile";
        String DATE = "date";
        String EMAIL = "email";
        String PIN_CODE = "pincode";
        String PAN_CARD = "pancard";
        String AADHAR_CARD = "aadharcard";
        String NUMBER = "number";
        String TEXT_BOX_BIG = "textboxbig";
    }

    public interface TYPE {
        String TEXT_BOX = "textbox";
        String AUTO_COMPLETE = "autosearch";
        String DROPDOWN = "dropdown";
        String RADIO_BUTTON = "radiobutton";
        String CHECKBOX = "checkbox";
        String RATING_BAR = "ratingbar";
        String RADIO_BUTTON_WITH_TEXT = "radiobuttonwithtext";
        String CHECKBOX_WITH_TEXT = "checkboxwithtext";
        String DROPDOWN_WITH_TEXT = "dropdownwithtext";
        String TEXT_BOX_GROUP = "textboxgroup";
    }

    public static class DATE_PATTERN {
        public static String YYYY_MM_DD_hh_mm_a = "yyyy-MM-dd hh:mm a";
        public static String DD_MM = "dd/MM";
        public static final String UTC_PATTERN = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
        public final static String YYYY_MM_DD = "yyyy-MM-dd";
        public final static String DD_MM_YYYY = "dd/MM/yyyy";
        public final static String DD_MMMM_YYYY = "dd MMMM yyyy"; // 12-September-2017
        public final static String hh_mm_a = "hh:mm a";
        public static final String TOTIMEZONE = "Asia/Kolkata";
        public final static String EEEE_dd_MMM_yyyy_hh_mm_a = "EEEE dd MMM yyyy | hh:mm a";
        public static String HH_mm_ss = "HH:mm:ss";
        public static String DD_MM_YYYY_hh_mm = "dd/MM/yyyy hh:mm aa";
        public static String HH_MM = "hh:mm";
        public static String MMM_YYYY = "MMM, yyyy";
        public static String DD_MM_YYYY_hh_mm_ss = "dd-MM-yyyy hh:mm:ss";
        public static String YYYY_MM_DD_hh_mm_ss = "yyyy-MM-dd hh:mm:ss";
        public static String YYYY_MM_DD_HH_mm_ss = "yyyy-MM-dd HH:mm:ss";
    }

    public static class TIME_STAMPS {
        public static int ONE_SECONDS = 1_000;
        public static int TWO_SECONDS = 2_000;
        public static int THREE_SECONDS = 3_000;
    }
}


