package jing.graduation.zhplayer.adapter;

import android.content.Context;
import android.database.Cursor;
import android.text.format.Formatter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import jing.graduation.zhplayer.R;
import jing.graduation.zhplayer.bean.VedioItemInfo;

/**
 * Created by zhjing03 on 2016/5/3.
 * Desc:
 * Version:
 * History:
 */
public class VedioListAdapter extends CursorAdapter{

    private ViewHolder holder;

    public VedioListAdapter(Context context, Cursor c, boolean autoRequery) {
        super(context, c, autoRequery);
    }

    public VedioListAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    public VedioListAdapter(Context activity, Cursor cursor) {
        super(activity,cursor);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view = View.inflate(context, R.layout.main_video_list_item,null);
        holder = new ViewHolder(view);
        view.setTag(holder);
        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        holder = (ViewHolder) view.getTag();
        VedioItemInfo info = VedioItemInfo.instanceDataFromCursor(cursor);
        holder.tvTitle.setText(info.getTitle());
        holder.tvDuration.setText(String.valueOf(info.getDuration()));
        holder.tvSize.setText(Formatter.formatFileSize(context,info.getSize()));
    }

    static class ViewHolder{
        public TextView tvTitle,tvDuration,tvSize;
        public ViewHolder(View view){
            tvTitle = (TextView) view.findViewById(R.id.main_video_item_tv_title);
            tvDuration = (TextView) view.findViewById(R.id.main_video_item_tv_duration);
            tvSize = (TextView) view.findViewById(R.id.main_video_item_tv_size);
        }
    }
}
