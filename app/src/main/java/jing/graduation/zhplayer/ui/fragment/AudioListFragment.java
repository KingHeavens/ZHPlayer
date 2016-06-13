package jing.graduation.zhplayer.ui.fragment;

import android.content.AsyncQueryHandler;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.provider.MediaStore.Audio.Media;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;

import jing.graduation.zhplayer.R;
import jing.graduation.zhplayer.adapter.AudioListAdapter;
import jing.graduation.zhplayer.bean.AudioItem;
import jing.graduation.zhplayer.db.MobilAsyncQueryHandler;
import jing.graduation.zhplayer.ui.activity.AudioPlayerActivity;

/**
 * Created by zhjing03 on 2016/5/2.
 * Desc:
 * Version:
 * History:
 */
public class AudioListFragment extends BaseFragment{
    private ListView listView;
    private AudioListAdapter mAdapter;

    @Override
    public int getLayoutID() {
        return R.layout.main_audio_list;
    }

    @Override
    public void initView() {
        listView = (ListView) findViewById(R.id.simple_listview);
    }

    @Override
    public void initListener() {
        mAdapter = new AudioListAdapter(getActivity(),null);
        listView.setAdapter(mAdapter);
        listView.setOnItemClickListener(new OnAudioItemClickListener());
    }

    @Override
    public void initData() {
        // 从MediaProvider查询数据
        ContentResolver resolver = getActivity().getContentResolver();
       /* Cursor cursor = resolver.query(Media.EXTERNAL_CONTENT_URI,new String[]{Media._ID,Media.DATA,Media.DISPLAY_NAME,Media.ARTIST},null,null,null);
        CursorUtils.printCursor(cursor);

        mAdapter.swapCursor(cursor);*/

        AsyncQueryHandler asyncQueryHandler = new MobilAsyncQueryHandler(resolver);
        asyncQueryHandler.startQuery(1,
                mAdapter,
                Media.EXTERNAL_CONTENT_URI,
                new String[]{
                        Media._ID,Media.DATA,
                        Media.DISPLAY_NAME,
                        Media.ARTIST
                },null,null,null);
    }

    @Override
    public void processClick(View v) {

    }

    private class OnAudioItemClickListener implements AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            // 获取被点击的数据
            Cursor cursor = (Cursor) parent.getItemAtPosition(position);
            ArrayList<AudioItem> audioItems = AudioItem.instanceListFromCursor(cursor);

            // 跳转到播放界面
            Intent intent = new Intent(getActivity(), AudioPlayerActivity.class);
            intent.putExtra("audioItems",audioItems);
            intent.putExtra("position",position);
            startActivity(intent);
        }
    }
}
