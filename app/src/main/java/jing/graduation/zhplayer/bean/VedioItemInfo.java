package jing.graduation.zhplayer.bean;

import android.database.Cursor;
import android.provider.MediaStore.Video.Media;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by zhjing03 on 2016/5/3.
 * Desc:视频信息
 * Version:
 * History:
 */
public class VedioItemInfo implements Serializable{
    private String title;
    private int duration;
    private int size;
    private String path;

    public static VedioItemInfo instanceDataFromCursor(Cursor cursor){
        VedioItemInfo info = new VedioItemInfo();
        if(cursor == null || cursor.getCount() <= 0){
            return null;
        }
        info.title = cursor.getString(cursor.getColumnIndex(Media.TITLE));
        info.duration = cursor.getInt(cursor.getColumnIndex(Media.DURATION));
        info.size = cursor.getInt(cursor.getColumnIndex(Media.SIZE));
        info.path = cursor.getString(cursor.getColumnIndex(Media.DATA));
        return info;
    }
    /** 从cursor里解析出整个播放列表 */
    public static ArrayList<VedioItemInfo> instanceListFromCursor(Cursor cursor) {
        ArrayList<VedioItemInfo> videoItems = new ArrayList<>();
        if (cursor==null||cursor.getCount()==0)
            return videoItems;

        cursor.moveToPosition(-1); // 把游标移动到列表头部
        while (cursor.moveToNext()){
            VedioItemInfo videoItem = instanceDataFromCursor(cursor);
            videoItems.add(videoItem);
        }
        return videoItems;
    }
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
    @Override
    public String toString() {
        return "VedioItemInfo{" +
                "title='" + title + '\'' +
                ", duration=" + duration +
                ", size=" + size +
                ", path='" + path + '\'' +
                '}';
    }
}
