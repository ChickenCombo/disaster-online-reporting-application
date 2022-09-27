package com.app.dorav4.bluetoothchat.gui;

import android.content.Context;
import android.util.DisplayMetrics;
import android.util.TypedValue;

public class GuiTools {
    public static int convertDpToPixels(Context context, float dp) {
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, metrics);
    }
}
