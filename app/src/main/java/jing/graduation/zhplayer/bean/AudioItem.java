package jing.graduation.zhplayer.bean;

import android.database.Cursor;
import android.provider.MediaStore.Video.Media;

import java.io.Serializable;
import java.util.ArrayList;

import jing.graduation.zhplayer.utils.StringUtils;

/**
 * Created by Administrator on 2016/5/11.
 *
 */
public class AudioItem implements Serializable {
    private String title;
    private String arties;
    private String path;

    /**
     * 从cursor生成一个对象
     */
    public static AudioItem instanceFromCursor(Cursor cursor) {
        AudioItem audioItem = new AudioItem();
        if (cursor == null || cursor.getCount() == 0) {
            return audioItem;
        }

        // 解析cursor的内容
        audioItem.title = cursor.getString(cursor.getColumnIndex(Media.DISPLAY_NAME));
        audioItem.title = StringUtils.formatDisplayName(audioItem.title);
        audioItem.arties = cursor.getString(cursor.getColumnIndex(Media.ARTIST));
        audioItem.path = cursor.getString(cursor.getColumnIndex(Media.DATA));
        return audioItem;
    }

    /**
     * 从cursor里解析出完整的播放列表
     */
    public static ArrayList<AudioItem> instanceListFromCursor(Cursor cursor) {
        ArrayList<AudioItem> audioItems = new ArrayList<>();
        if (cursor == null || cursor.getCount() == 0) {
            return audioItems;
        }

        cursor.moveToPosition(-1);
        while (cursor.moveToNext()) {
            AudioItem videoItem = instanceFromCursor(cursor);
            audioItems.add(videoItem);
        }

        return audioItems;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getArties() {
        return arties;
    }

    public void setArties(String arties) {
        this.arties = arties;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    @Override
    public String toString() {
        return "AudioItem{" +
                "title='" + title + '\'' +
                ", arties='" + arties + '\'' +
                ", path='" + path + '\'' +
                '}';
    }
}
