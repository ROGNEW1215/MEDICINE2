package com.example.medicine.util;

import android.content.Context;
import android.content.SharedPreferences;

public class PrefsHelper {

    private static final String PREFS_NAME = "user_prefs";
    private static final String KEY_ELDER_NAME = "elder_name";
    private static final String KEY_EMERGENCY_PHONE = "emergency_phone";
    private static final String DEFAULT_NAME = "长辈";

    private final SharedPreferences prefs;

    public PrefsHelper(Context context) {
        prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    public String getElderName() {
        return prefs.getString(KEY_ELDER_NAME, DEFAULT_NAME);
    }

    public void setElderName(String name) {
        prefs.edit().putString(KEY_ELDER_NAME, name).apply();
    }

    public String getEmergencyPhone() {
        return prefs.getString(KEY_EMERGENCY_PHONE, "");
    }

    public void setEmergencyPhone(String phone) {
        prefs.edit().putString(KEY_EMERGENCY_PHONE, phone).apply();
    }
}
