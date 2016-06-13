package jing.graduation.zhplayer.utils;

import android.database.Cursor;

/**
 * Created by zhjing03 on 2016/5/8.
 * Desc:
 * Version:
 * History:
 */
public class CursorUtils {
    private static final String TAG = "CursorUtils";

    /** 打印cursor里的所有数据 */
    public static void printCursor(Cursor cursor) {
        LogUtils.e("CursorUtils.printCursor:  查询到的数据量为：" + cursor.getCount());
        while (cursor.moveToNext()) {
            LogUtils.e("===================================");
            for (int i = 0; i < cursor.getColumnCount(); i++) {
                LogUtils.e("CursorUtils.printCursor: name=" + cursor.getColumnName(i) +
                        ";value=" + cursor.getString(i));
            }
        }
    }
}
