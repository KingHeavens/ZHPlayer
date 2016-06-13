package jing.graduation.zhplayer.db;

import android.content.AsyncQueryHandler;
import android.content.ContentResolver;
import android.database.Cursor;
import android.widget.CursorAdapter;

import jing.graduation.zhplayer.utils.LogUtils;

/**
 * Created by zhjing03 on 2016/5/3.
 * Desc:
 * Version:
 * History:
 */
public class MobilAsyncQueryHandler extends AsyncQueryHandler{
    public MobilAsyncQueryHandler(ContentResolver cr) {
        super(cr);
    }

    @Override
    protected void onQueryComplete(int token, Object cookie, Cursor cursor) {
        CursorAdapter cursorAdapter;
        switch(token){
            case 0:{
                LogUtils.e("cookie:" + cookie);
                CursorAdapter adapter = (CursorAdapter) cookie;
                adapter.swapCursor(cursor);
            }
            break;
            case 1: {
                CursorAdapter adapter = (CursorAdapter) cookie;
                adapter.swapCursor(cursor);
            }
            break;
        }
    }
}
