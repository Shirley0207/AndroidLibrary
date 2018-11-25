package com.shirley.android.buttongroup;

import android.content.Context;
import android.view.Display;
import android.view.WindowManager;

public class Utils {

    /**
     * 获取屏幕的宽度
     */

    @SuppressWarnings("deprecation")
    public static int getScreenWidth(Context context) {
        WindowManager manager = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        Display display = manager.getDefaultDisplay();
        return display.getWidth();
    }

}
