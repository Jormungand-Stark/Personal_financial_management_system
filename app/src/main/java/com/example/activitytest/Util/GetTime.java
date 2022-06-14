package com.example.activitytest.Util;

import android.icu.text.SimpleDateFormat;

import java.util.Date;

/** 获取当前时间的小时单位，以便于登陆界面壁纸和问候语的更换 */
public class GetTime {
    public static int NowTime(){
        SimpleDateFormat formatter = new SimpleDateFormat ("HH");
        Date curDate = new Date(System.currentTimeMillis());
        String str = formatter.format(curDate);
        return Integer.parseInt(str);
    }
}
