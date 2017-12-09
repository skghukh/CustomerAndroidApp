package com.rodafleets.rodacustomer.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONObject;

public final class ApplicationSettings {

    private static final String SETTINGS_NAME = "RodaDriverSettings";
    private static final String APP_LANGUAGE = "APP_LANGUAGE";
    private static final String DRIVER_ID = "DRIVER_ID";
    private static final String OTP_SESSION_ID = "OTP_SESSION_ID";
    private static final String DRIVER = "DRIVER";
    private static final String VERIFIED = "VERIFIED";
    private static final String VEHICLE_INFO_SAVED = "VEHICLE_INFO_SAVED";
    private static final String LOGGED_IN = "LOGGED_IN";
    private static final String REGISTRATION_ID = "REGISTRATION_ID";
    private static final String VEHICLE_REQUEST = "VEHICLE_REQUEST";
    private static final String CASH_PREFERENCE = "CASH_PREFERENCE";
    private static LatLng sourceLoc;
    private static String sourcePlace;
    private static String destPlace;


    private static SharedPreferences getSharedPreferences(Context context) {
        return context.getSharedPreferences(SETTINGS_NAME, 0);
    }

    private static SharedPreferences.Editor getEditor(Context context) {
        return getSharedPreferences(context).edit();
    }

    public static void clearAllSettings(Context context) {
        SharedPreferences.Editor editor = getEditor(context);
        editor.clear();
        editor.commit();
    }

    public static void setAppLanguage(Context context, String language) {
        SharedPreferences.Editor editor = getEditor(context);
        editor.putString(APP_LANGUAGE, language);
        editor.commit();
    }

    public static String getAppLanguage(Context context) {
        return getSharedPreferences(context).getString(APP_LANGUAGE, "");
    }

    public static void setCustomerId(Context context, int driverId) {
        SharedPreferences.Editor editor = getEditor(context);
        editor.putInt(DRIVER_ID, driverId);
        editor.commit();
    }

    public static int getCustomerId(Context context) {
        return getSharedPreferences(context).getInt(DRIVER_ID, 0);
    }

    public static void setDriver(Context context, JSONObject jsonObject) {
        SharedPreferences.Editor editor = getEditor(context);
        editor.putString(DRIVER, jsonObject.toString());
        editor.commit();
    }

    public static String getDriver(Context context) {
        return getSharedPreferences(context).getString(DRIVER, "");
    }

    public static void setOtpSessionId(Context context, String OtpSessionId) {
        SharedPreferences.Editor editor = getEditor(context);
        editor.putString(OTP_SESSION_ID, OtpSessionId);
        editor.commit();
    }

    public static String getOtpSessionId(Context context) {
        return getSharedPreferences(context).getString(OTP_SESSION_ID, "");
    }

    public static void setVerified(Context context, Boolean verified) {
        SharedPreferences.Editor editor = getEditor(context);
        editor.putBoolean(VERIFIED, verified);
        editor.commit();
    }

    public static Boolean getVerified(Context context) {
        return getSharedPreferences(context).getBoolean(VERIFIED, false);
    }

    public static void setVehicleInfoSaved(Context context, Boolean saved) {
        SharedPreferences.Editor editor = getEditor(context);
        editor.putBoolean(VEHICLE_INFO_SAVED, saved);
        editor.commit();
    }

    public static Boolean getVehicleInfoSaved(Context context) {
        return getSharedPreferences(context).getBoolean(VEHICLE_INFO_SAVED, false);
    }

    public static void setLoggedIn(Context context, Boolean loggedIn) {
        SharedPreferences.Editor editor = getEditor(context);
        editor.putBoolean(LOGGED_IN, loggedIn);
        editor.commit();
    }

    public static Boolean getLoggedIn(Context context) {
        return getSharedPreferences(context).getBoolean(LOGGED_IN, false);
    }

    public static void setRegistrationId(Context context, String token) {
        SharedPreferences.Editor editor = getEditor(context);
        editor.putString(REGISTRATION_ID, token);
        editor.commit();
    }

    public static String getRegistrationId(Context context) {
        return getSharedPreferences(context).getString(REGISTRATION_ID, "");
    }

    public static void setVehicleRequest(Context context, JSONObject jsonObject) {
        SharedPreferences.Editor editor = getEditor(context);
        if (jsonObject != null) {
            editor.putString(VEHICLE_REQUEST, jsonObject.toString());
        } else {
            editor.putString(VEHICLE_REQUEST, "");
        }
        editor.commit();
    }

    public static String getVehicleRequest(Context context) {
        return getSharedPreferences(context).getString(VEHICLE_REQUEST, "");
    }

    public static LatLng getSourceLoc() {
        return sourceLoc;
    }

    public static void setSourceLoc(LatLng lat) {
        sourceLoc = lat;
    }

    public static String getSourcePlace() {
        return sourcePlace;
    }

    public static void setSourcePlace(String sourcePlace) {
        ApplicationSettings.sourcePlace = sourcePlace;
    }

    public static String getDestPlace() {
        return destPlace;
    }

    public static void setDestPlace(String destPlace) {
        ApplicationSettings.destPlace = destPlace;
    }

    public static boolean getPayOption(Context context){
        return getSharedPreferences(context).getBoolean(CASH_PREFERENCE, false);
    }

    public static void setPayOption(boolean isCash, Context context){
        SharedPreferences.Editor editor = getEditor(context);
        editor.putBoolean(CASH_PREFERENCE, isCash);
        editor.commit();
    }
}
