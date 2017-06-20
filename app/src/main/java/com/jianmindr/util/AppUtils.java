package com.jianmindr.util;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by brightman on 5/5/2017.
 */

public class AppUtils {

    private static SharedPreferences shared;
    private static SharedPreferences.Editor editor;

    private final static String CUR_LAT = "CUR_LAT";
    private final static String CUR_LON = "CUR_LON";
    private final static String CUR_ADDR = "CUR_ADDR";

    public static float getCurLat(Context context) {
        shared = context.getSharedPreferences("userInfo", 0);
        return shared.getFloat(CUR_LAT, 39.941272f);
    }

    public static void setCurLat(Context context, float curLat) {
        shared = context.getSharedPreferences("userInfo", 0);
        editor = shared.edit();

        editor.putFloat(CUR_LAT, curLat).commit();
    }

    public static float getCurLon(Context context) {
        shared = context.getSharedPreferences("userInfo", 0);
        return shared.getFloat(CUR_LON, 116.400819f);
    }

    public static void setCurLon(Context context, float curLon) {
        shared = context.getSharedPreferences("userInfo", 0);
        editor = shared.edit();

        editor.putFloat(CUR_LON, curLon).commit();
    }
    public static String getCurAddr(Context context) {
        shared = context.getSharedPreferences("userInfo", 0);
        return shared.getString(CUR_ADDR, "定位失败");
    }

    public static void setCurAddr(Context context, String curAddr) {
        shared = context.getSharedPreferences("userInfo", 0);
        editor = shared.edit();

        editor.putString(CUR_ADDR, curAddr).commit();
    }
}
