package jing.graduation.zhplayer.utils;

import android.util.Log;

/**
 * Created by ZHJing03 on
 * Date:2016/2/27
 * Desc:打印Log的工具类
 */
public class LogUtils {

    private static final String mTag = "ZHJING";

    /*输出Error级别的错误*/
    public static void e(String errorInfo){
        if(!Contance.isRelease){
            Log.e(mTag,errorInfo);
        }
    }
    /*输出Debug级别的错误*/
    public static void d(String errorInfo){
        if(!Contance.isRelease){
            Log.d(mTag,errorInfo);
        }
    }
    /*输出Warning级别的错误*/
    public static void w(String errorInfo){
        if(!Contance.isRelease){
            Log.w(mTag,errorInfo);
        }
    }
    /*输出Info*/
    public static void i(String errorInfo){
        if(!Contance.isRelease){
            Log.i(mTag,errorInfo);
        }
    }
    /*输出Verbose*/
    public static void v(String errorInfo){
        if(!Contance.isRelease){
            Log.v(mTag,errorInfo);
        }
    }

}
