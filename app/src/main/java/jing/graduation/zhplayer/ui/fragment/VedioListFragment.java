package jing.graduation.zhplayer.ui.fragment;

import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.provider.MediaStore.Video.Media;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;

import jing.graduation.zhplayer.R;
import jing.graduation.zhplayer.adapter.VedioListAdapter;
import jing.graduation.zhplayer.bean.VedioItemInfo;
import jing.graduation.zhplayer.ui.activity.VitamioPlayerActivity;
import jing.graduation.zhplayer.utils.CursorUtils;

/**
 * Created by zhjing03 on 2016/5/2.
 * Desc:视频页面Fragment
 * Version:
 * History:
 */
public class VedioListFragment extends BaseFragment{

    private ListView mListView;
    private VedioListAdapter mVedioAdapter;

    @Override
    public int getLayoutID() {
        return R.layout.main_video_list;
    }

    @Override
    public void initView() {
        mListView = (ListView) findViewById(R.id.simple_listview);
    }
    @Override
    public void initListener() {
        mVedioAdapter = new VedioListAdapter(getActivity(), null);
        mListView.setAdapter(mVedioAdapter);
        mListView.setOnItemClickListener(new OnVedioItemClickListener());
    }
    @Override
    public void initData() {
        ContentResolver contentResolver = getActivity().getContentResolver();
        Cursor cursor = contentResolver.query(Media.EXTERNAL_CONTENT_URI, new String[]{Media._ID, Media.DATA, Media.TITLE, Media.SIZE, Media.DURATION}, null, null, null);

        CursorUtils.printCursor(cursor);
        mVedioAdapter.swapCursor(cursor);
        /*AsyncQueryHandler asyncQueryHandler = new MobilAsyncQueryHandler(contentResolver);
        asyncQueryHandler.startQuery(0, mVedioAdapter,
                Media.EXTERNAL_CONTENT_URI,
                new String[]{Media._ID, Media.DATA, Media.TITLE, Media.SIZE, Media.DURATION},
                null,
                null,
                null);*/
    }
    @Override
    public void processClick(View v) {

    }

    /**
     * 视频条目点击事件
     */
    private class OnVedioItemClickListener implements AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            //获取点击数据
            Cursor cursor = (Cursor)parent.getItemAtPosition(position);
            ArrayList<VedioItemInfo> videoItems = VedioItemInfo.instanceListFromCursor(cursor);
            //跳转到播放界面
            Intent intent = new Intent(getActivity(), VitamioPlayerActivity.class);
//            intent.putExtra("videoItem",videoItem);
            intent.putExtra("videoItems",videoItems);
            intent.putExtra("position",position);
            startActivity(intent);
        }
    }
}
