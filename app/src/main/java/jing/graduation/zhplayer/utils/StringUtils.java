package jing.graduation.zhplayer.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by zhjing03 on 2016/5/8.
 * Desc:
 * Version:
 * History:
 */
public class StringUtils {
    private static final int HOUR = 60 * 60 * 1000;
    private static final int MIN = 60 * 1000;
    private static final int SEC = 1000;

    /**
     * 格式化duration为时间格式，如 01:01:01 或 01:01
     */
    public static String formatDuration(int duration) {
        String time = null;

        int hour = duration / HOUR;

        int min = duration % HOUR / MIN;

        int sec = duration % MIN / SEC;

        if (hour == 0) {
            // 01：01
            time = String.format("%02d:%02d", min, sec);
        } else {
            // 01:01:01
            time = String.format("%02d:%02d:%02d", hour, min, sec);
        }

        return time;
    }

    /** 格式化当前系统时间为 01:01:01 */
    public static String formatSystemTime() {
        SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
        return format.format(new Date());
    }

    /** 格式化 woxiangxin.mp3 为 woxiangxin */
    public static String formatDisplayName(String displayName) {
        return displayName.substring(0, displayName.indexOf("."));
    }
}
