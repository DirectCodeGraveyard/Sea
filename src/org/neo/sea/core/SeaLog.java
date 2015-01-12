package org.neo.sea.core;

import android.util.Log;

public class SeaLog {
    public static final String TAG = "Sea";

    public static void info(String message) {
        Log.i(TAG, message);
    }

    public static void debug(String message) {
        Log.d(TAG, message);
    }

    public static void warn(String message) {
        Log.w(TAG, message);
    }

    public static void error(String message) {
        Log.e(TAG, message);
    }

    public static void error(String message, Exception e) {
        Log.e(TAG, message, e);
    }
}
