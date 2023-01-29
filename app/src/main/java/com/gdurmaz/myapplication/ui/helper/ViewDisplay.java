package com.gdurmaz.myapplication.ui.helper;

import android.content.Context;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.WindowManager;

public class ViewDisplay {
    private static final String TAG = ViewDisplay.class.getSimpleName();

    public static DisplayMetrics getDisplayMetrics(Context context) {
        Log.i(TAG, "getScreenHeight() -- Enter.");
        if (context == null) {
            Log.w(TAG, "getScreenHeight() -- context is null");
            return null;
        }
        DisplayMetrics displayMetrics = new DisplayMetrics();
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        windowManager.getDefaultDisplay().getMetrics(displayMetrics);
        return displayMetrics;
    }
}
