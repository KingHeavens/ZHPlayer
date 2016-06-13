package jing.graduation.zhplayer.adapter;

import android.content.Context;
import android.database.Cursor;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import jing.graduation.zhplayer.R;
import jing.graduation.zhplayer.bean.AudioItem;
import jing.graduation.zhplayer.utils.LogUtils;


/**
 * Created by zhjing03 on 2016/5/3.
 * Desc:
 * Version:
 * History:
 */
public class AudioListAdapter extends CursorAdapter {
    public AudioListAdapter(Context context, Cursor c) {
        super(context, c);
    }

    public AudioListAdapter(Context context, Cursor c, boolean autoRequery) {
        super(context, c, autoRequery);
    }

    public AudioListAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        // 生成新的 view
        View view = View.inflate(context, R.layout.main_audio_list_item, null);
        ViewHoder hoder = new ViewHoder(view);
        view.setTag(hoder);
        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        // 填充 view
        ViewHoder hoder = (ViewHoder) view.getTag();

        AudioItem audioItem = AudioItem.instanceFromCursor(cursor);
        LogUtils.e("==>>>.audioItem" + audioItem);
        hoder.tv_title.setText(audioItem.getTitle());
        hoder.tv_arties.setText(audioItem.getArties());
    }

    private class ViewHoder {
        TextView tv_title, tv_arties;

        public ViewHoder(View root) {
            tv_title = (TextView) root.findViewById(R.id.main_audio_item_tv_title);
            tv_arties = (TextView) root.findViewById(R.id.main_audio_item_tv_arties);
        }
    }
}
