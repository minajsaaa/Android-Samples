package com.blueinno.android.alcoholsensor.util;

import android.content.Context;
import android.content.SharedPreferences;

public class PreferenceUtil {

    private static String PREFERENCE_NAME = "unist";

    public static String PREFERENCE_MAX_Y_SCALE = "maxYScale";
    public static String PREFERENCE_MIN_Y_SCALE = "minYScale";

    public static int REQUEST_GRAPH = 1;
    public static int REQUEST_TERMINAL = 2;

    public static void save(Context context,String key , String data) {
        SharedPreferences prefs = context.getSharedPreferences(PREFERENCE_NAME, context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(key, data);
        editor.commit();
    }

    public static String get(Context context,String key) {
        SharedPreferences prefs = context.getSharedPreferences(PREFERENCE_NAME, context.MODE_PRIVATE);
        if( prefs == null )
            return null;

        return prefs.getString(key, null);
    }

}
