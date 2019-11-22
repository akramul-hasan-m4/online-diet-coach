package com.daffodil.online.dietcoach.db.local;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPreferencesConfig {
    private static SharedPreferences sharedpreferences;
    private static final String PREFERENCE_NAME = "DIETPref";

    private SharedPreferencesConfig() {
    }

    public static void saveStringData(Context context, String key, String value){
        sharedpreferences = context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putString(key, value);
        editor.apply();
    }

    public static String getStringData(Context context, String key){
        sharedpreferences = context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
        if (sharedpreferences.contains(key)) {
            return sharedpreferences.getString(key, "");
        }
        return "";
    }
}
