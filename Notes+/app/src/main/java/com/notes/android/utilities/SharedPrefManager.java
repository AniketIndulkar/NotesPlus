package com.notes.android.utilities;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Aniket on 29-03-2018.
 */

public class SharedPrefManager {

    private SharedPreferences preferences;
    private Context context;
    private SharedPreferences.Editor prefEditor;

    public SharedPrefManager(Context context) {
        this.context = context;
        preferences = context.getSharedPreferences(AppConstants.SHARED_PREF_NAME, Context.MODE_PRIVATE);
        prefEditor = preferences.edit();
    }

    public boolean addStringToPref(String key, String value) {
        if (key != null && value != null) {
            prefEditor.putString(key, value);
            return prefEditor.commit();
        }
        return false;
    }

    public String getStringFromPref(String key){
        if (key!=null && !key.equalsIgnoreCase("")){
            return preferences.getString(key,AppConstants.STRING_DEFAULT);
        }
        return AppConstants.STRING_DEFAULT;
    }

    public boolean isContains(String key){
        return preferences.contains(key);
    }


}
