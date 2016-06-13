package jing.graduation.zhplayer.ui.activity;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import com.nineoldandroids.view.ViewHelper;
import com.nineoldandroids.view.ViewPropertyAnimator;
import java.util.ArrayList;
import jing.graduation.zhplayer.R;
import jing.graduation.zhplayer.bean.VedioItemInfo;
import jing.graduation.zhplayer.utils.StringUtils;
import jing.graduation.zhplayer.widget.VideoView;

/**
 * Created by zhjing03 on 2016/5/3.
 * Desc:
 * Version:
 * History:
 */
public class VedioPlayerActivity extends BaseActivity {

    private static final int MSG_UPDATE_SYSTEM_TIME = 0;
    private static final int MSG_UPDATE_POSITION = 1;
    private static final int MSG_HIDE_CONTROLOR = 2;
    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case  MSG_UPDATE_SYSTEM_TIME:
                    startUpdateSystemTime();
                    break;
                case  MSG_UPDATE_POSITION:
                    startUpdatePosition();
                    break;
                case  MSG_HIDE_CONTROLOR:
                    hideControlor();
                    break;
            }
        }
    };
    private int mCurrentVolume;
    private float mStartY;
    private int mStartVolume;
    private View alpha_cover;
    private float mStartAlpha;
    private TextView tv_position;
    private SeekBar sk_position;
    private TextView tv_duration;
    private ImageView iv_pre;
    private ImageView iv_next;
    private ArrayList<VedioItemInfo> mVideoItems;
    private int mPosition;
    private View ll_top;
    private View ll_bottom;
    private GestureDetector gestureDetector;
    private boolean isShowControlor;
    private ImageView iv_fullscreen;
    private View ll_loading_cover;
    private ProgressBar pb_buffering;

    private class OnVideoErrorListener implements MediaPlayer.OnErrorListener {
        @Override
        public boolean onError(MediaPlayer mp, int what, int extra) {
            // 播放发生错误
            AlertDialog.Builder builder = new AlertDialog.Builder(VedioPlayerActivity.this);
            builder.setTitle("提示");
            builder.setMessage("此视频无法播放");
            builder.setPositiveButton("退出播放", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    finish();
                }
            });
            builder.show();
            return false;
        }
    }

    private class OnVideoInfoListener implements MediaPlayer.OnInfoListener {
        @Override
        /** 当播放状态发生变更时会回调此方法 */
        public boolean onInfo(MediaPlayer mp, int what, int extra) {
            switch (what){
                case  MediaPlayer.MEDIA_INFO_BUFFERING_START:
                    pb_buffering.setVisibility(View.VISIBLE);
                    break;
                case  MediaPlayer.MEDIA_INFO_BUFFERING_END:
                    pb_buffering.setVisibility(View.GONE);
                    break;
            }

            return false;
        }
    }

    private class OnVideoBufferingUpdateListener implements MediaPlayer.OnBufferingUpdateListener {
        @Override
        /** 视频缓冲进度发生变更时会回调此方法 */
        public void onBufferingUpdate(MediaPlayer mp, int percent) {
//            /logE("OnVideoBufferingUpdateListener.onBufferingUpdate: percent="+percent);
            // 计算缓冲百分比
            float bufferPercent = percent / 100f;

            // 计算缓冲了多少时间
            int bufferTime = (int) (bufferPercent * sk_position.getMax());

            // 更新第二进度条
            sk_position.setSecondaryProgress(bufferTime);
        }
    }

    private class OnVideoGestureListener extends GestureDetector.SimpleOnGestureListener {

        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            // 单击事件
//            logE("OnVideoGestureListener.onSingleTapConfirmed: ");
            switchControlor();
            return super.onSingleTapConfirmed(e);
        }

        @Override
        public boolean onDoubleTap(MotionEvent e) {
            // 双击事件
//            logE("OnVideoGestureListener.onDoubleTap: ");
            switchFullScreen();
            return super.onDoubleTap(e);
        }

        @Override
        public void onLongPress(MotionEvent e) {
            // 长按事件
            super.onLongPress(e);
            switchPauseStatus();
        }
    }

    private class OnVideoCompletionListener implements MediaPlayer.OnCompletionListener {
        @Override
        public void onCompletion(MediaPlayer mp) {
            // 视频播放结束

            // 为比避开系统错误，播放技术后主动更新一下已播放时间
            mHandler.removeMessages(MSG_UPDATE_POSITION);
            updatePosition(videoView.getDuration());

            // 更新暂停按钮的图片
            updatePauseBtn();

        }
    }

    private class OnVideoSeekBarChangeListener implements SeekBar.OnSeekBarChangeListener {
        @Override
        /** 当进度值发生变更的时候被回调 */
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
//            logE("OnVideoSeekBarChangeListener.onProgressChanged: progress="+progress+";fromUser="+fromUser);
            // 不是用户的处理，则不做处理
            if (!fromUser){
                return;
            }

            switch (seekBar.getId()){
                case R.id.video_player_sk_volume:
                    updateVolume(progress);
                    break;
                case R.id.video_player_sk_position:
                    videoView.seekTo(progress);
                    updatePosition(progress);
                    break;
            }
        }

        @Override
        /** 手指压到Seekbar上时回调 */
        public void onStartTrackingTouch(SeekBar seekBar) {
            mHandler.removeMessages(MSG_HIDE_CONTROLOR);
        }

        @Override
        /** 手指离开Seekbbar时回调 */
        public void onStopTrackingTouch(SeekBar seekBar) {
            notifyHideControlor();
        }
    }

    private class OnVideoPreparedListener implements MediaPlayer.OnPreparedListener {
        @Override
        public void onPrepared(MediaPlayer mp) {
            // 视频准备完成
            ll_loading_cover.setVisibility(View.GONE);
            videoView.start();

            // 更新播放按钮
            updatePauseBtn();

            // 更新播放进度
            int duration = videoView.getDuration();
            tv_duration.setText(StringUtils.formatDuration(duration));
            sk_position.setMax(duration);
            startUpdatePosition();

            // 更新全屏按钮
            updateFullscreenBtn();
        }
    }

    private class OnVideoReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            // 获取系统电量
            int level = intent.getIntExtra("level", 0);

            updateBatteryBtn(level);
        }
    }

    private TextView tv_title;
    private OnVideoReceiver onVideoReceiver;
    private ImageView iv_pause;
    private VideoView videoView;
    private ImageView iv_battery;
    private TextView tv_system_time;
    private SeekBar sk_volume;
    private AudioManager mAudioManager;
    private ImageView iv_mute;

    @Override
    public int getLayoutID() {
        return R.layout.video_player;
    }

    @Override
    public void initView() {

        videoView = (VideoView) findViewById(R.id.video_player_videoview);
        alpha_cover = findViewById(R.id.video_player_alpha_cover);
        ll_top = findViewById(R.id.video_player_ll_top);
        ll_bottom = findViewById(R.id.video_player_ll_bottom);
        ll_loading_cover = findViewById(R.id.video_player_ll_loading_cover);
        pb_buffering = (ProgressBar) findViewById(R.id.video_player_pb_buffering);

        tv_title = (TextView) findViewById(R.id.video_player_tv_title);
        iv_battery = (ImageView) findViewById(R.id.video_player_iv_battery);
        tv_system_time = (TextView) findViewById(R.id.video_player_tv_system_time);
        sk_volume = (SeekBar) findViewById(R.id.video_player_sk_volume);
        iv_mute = (ImageView) findViewById(R.id.video_player_iv_mute);

        tv_position = (TextView) findViewById(R.id.video_player_tv_position);
        sk_position = (SeekBar) findViewById(R.id.video_player_sk_position);
        tv_duration = (TextView) findViewById(R.id.video_player_tv_duration);
        iv_pause = (ImageView) findViewById(R.id.video_player_iv_pause);
        iv_pre = (ImageView) findViewById(R.id.video_player_iv_pre);
        iv_next = (ImageView) findViewById(R.id.video_player_iv_next);
        iv_fullscreen = (ImageView) findViewById(R.id.video_player_iv_fullscreen);
    }

    @Override
    public void initListener() {
        iv_pause.setOnClickListener(this);
        iv_mute.setOnClickListener(this);
        iv_pre.setOnClickListener(this);
        iv_next.setOnClickListener(this);
        iv_fullscreen.setOnClickListener(this);

        OnVideoSeekBarChangeListener onSeekBarChangeListener = new OnVideoSeekBarChangeListener();
        sk_volume.setOnSeekBarChangeListener(onSeekBarChangeListener);
        sk_position.setOnSeekBarChangeListener(onSeekBarChangeListener);

        gestureDetector = new GestureDetector(this, new OnVideoGestureListener());

        // 注册视频相关的监听器
        videoView.setOnPreparedListener(new OnVideoPreparedListener());
        videoView.setOnCompletionListener(new OnVideoCompletionListener());
        videoView.setOnBufferingUpdateListener(new OnVideoBufferingUpdateListener());
        videoView.setOnInfoListener(new OnVideoInfoListener());
        videoView.setOnErrorListener(new OnVideoErrorListener());

        IntentFilter filter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        onVideoReceiver = new OnVideoReceiver();
        registerReceiver(onVideoReceiver, filter);
    }

    @Override
    public void initData() {
        // 显示加载遮罩
        ll_loading_cover.setVisibility(View.VISIBLE);

        Uri uri = getIntent().getData();
        //logE("VideoPlayerActivity.initData: uri="+uri);
        if (uri != null) {
            // 从外部发起的调用
            videoView.setVideoURI(uri);
            tv_title.setText(uri.getPath());
            iv_pre.setEnabled(false);
            iv_next.setEnabled(false);
        } else {
            // 从内部发起的调用
            mVideoItems = (ArrayList<VedioItemInfo>) getIntent().getSerializableExtra("videoItems");
            mPosition = getIntent().getIntExtra("position", -1);
            playItem();
        }

        // 开启系统时间更新
        startUpdateSystemTime();

        // 初始化音量
        mAudioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
        int maxVolume = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        sk_volume.setMax(maxVolume);
        int currentVolume = getCurrentVolume();
//        logE("VideoPlayerActivity.initData: maxVolume=" + maxVolume + ";currentVolume=" + currentVolume);
        sk_volume.setProgress(currentVolume);

        // 初始化屏幕亮度为0
        ViewHelper.setAlpha(alpha_cover, 0);

        // 隐藏控制面板
        initHideControlor();
    }

    @Override
    public void processClick(View v) {
        switch (v.getId()) {
            case R.id.video_player_iv_pause:
                switchPauseStatus();
                break;
            case R.id.video_player_iv_mute:
                switchMuteStatus();
                break;
            case R.id.video_player_iv_pre:
                playPre();
                break;
            case R.id.video_player_iv_next:
                playNext();
                break;
            case R.id.video_player_iv_fullscreen:
                switchFullScreen();
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(onVideoReceiver);
        mHandler.removeCallbacksAndMessages(null);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        gestureDetector.onTouchEvent(event);

//        手指划过屏幕的距离 = 手指当前位置 - 手指压下时的位置
//        手指划过屏幕的百分比 = 手指划过屏幕的距离 / 屏幕高度
//        滑动导致变化的音量 = 最大音量 * 手指划过屏幕的百分比
//        最终音量 = 手指压下时的音量 + 滑动导致变化的音量
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                // 记录手指压下时的数据
                mStartY = event.getY();
                mStartVolume = getCurrentVolume();
                mStartAlpha = ViewHelper.getAlpha(alpha_cover);

                mHandler.removeMessages(MSG_HIDE_CONTROLOR);
                break;
            case MotionEvent.ACTION_MOVE:
                // 获取手指当前位置
                float moveY = event.getY();

                // 计算手指移动距离
                float offsetY = moveY - mStartY;

                // 计算手指划过屏幕的百分比
                int halfScreenH = getWindowManager().getDefaultDisplay().getHeight() / 2;
                int halfScreenW = getWindowManager().getDefaultDisplay().getWidth() / 2;
                float movePercent = offsetY / halfScreenH;

                // 在屏幕的左半侧，滑动时修改屏幕亮度，在右半侧，滑动时修改系统音量
                if (event.getX() < halfScreenW){
                    // 修改亮度
                    moveAlpha(movePercent);
                }else {
                    // 修改音量
                    moveVolume(movePercent);
                }

                break;
            case MotionEvent.ACTION_UP:
                notifyHideControlor();
                break;
        }

        return super.onTouchEvent(event);
    }

    /** 根据手指划过屏幕的百分比，修改屏幕亮度 */
    private void moveAlpha(float movePercent) {
        float finalAlpha = mStartAlpha + movePercent;

       // logE("VideoPlayerActivity.moveAlpha: start=" + mStartAlpha + ";move=" + movePercent + ";final=" + finalAlpha);
        if (finalAlpha >= 0 && finalAlpha <= 1) {
            ViewHelper.setAlpha(alpha_cover, finalAlpha);
        }
    }

    /** 根据手指划过屏幕的百分比修改系统音量 */
    private void moveVolume(float movePercent) {
        // 计算要变化的音量
        int offsetVolume = (int) (sk_volume.getMax() * movePercent);

        // 计算最终要设置的音量
        int finalVolume = mStartVolume + offsetVolume;
        updateVolume(finalVolume);
    }

    /**
     * 切换视频播放状态
     */
    private void switchPauseStatus() {
        if (videoView.isPlaying()) {
            // 正在播放
            videoView.pause();
        } else {
            // 暂停状态
            videoView.start();
        }

        updatePauseBtn();
    }

    /**
     * 更新暂停按钮使用的图片
     */
    private void updatePauseBtn() {
        if (videoView.isPlaying()) {
            // 播放状态
            iv_pause.setImageResource(R.drawable.video_pause_selector);
            startUpdatePosition();
        } else {
            // 暂停状态
            iv_pause.setImageResource(R.drawable.video_play_selector);
            mHandler.removeMessages(MSG_UPDATE_POSITION);
        }
    }

    /** 根据当前系统电量更新电池使用的图片 */
    private void updateBatteryBtn(int level) {
        if (level < 10) {
            iv_battery.setImageResource(R.drawable.ic_battery_0);
        } else if (level < 20) {
            iv_battery.setImageResource(R.drawable.ic_battery_10);
        } else if (level < 40) {
            iv_battery.setImageResource(R.drawable.ic_battery_20);
        } else if (level < 60) {
            iv_battery.setImageResource(R.drawable.ic_battery_40);
        } else if (level < 80) {
            iv_battery.setImageResource(R.drawable.ic_battery_60);
        } else if (level < 100) {
            iv_battery.setImageResource(R.drawable.ic_battery_80);
        } else {
            iv_battery.setImageResource(R.drawable.ic_battery_100);
        }
    }

    /** 更新系统时间，并延迟一段时间后再次更新 */
    private void startUpdateSystemTime() {
//        logE("VideoPlayerActivity.startUpdateSystemTime: time="+System.currentTimeMillis());
        tv_system_time.setText(StringUtils.formatSystemTime());

        mHandler.sendEmptyMessageDelayed(MSG_UPDATE_SYSTEM_TIME, 500);
    }

    /** 如果音量不为0，则记录当前音量，并设置因量为0；如果音量为0，则恢复音量为之前记录的值 */
    private void switchMuteStatus() {
        if (getCurrentVolume() !=0){
            // 非静音状态
            mCurrentVolume = getCurrentVolume();
            updateVolume(0);
        }else {
            // 静音状态
            updateVolume(mCurrentVolume);
        }
    }

    /** 获取当前系统STREAM_MUSIC的音量 */
    private int getCurrentVolume() {
        return mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
    }

    /** 更新STREAM_MUSIC的音量为volume,并且更新音量控制条 */
    private void updateVolume(int volume) {
//        logE("VideoPlayerActivity.updateVolume: volume="+volume);
        mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, volume, 0);
        sk_volume.setProgress(volume);
    }

    /** 更新已播放时间，并延迟一段时间后再次更新 */
    private void startUpdatePosition() {
        int position = videoView.getCurrentPosition();
        updatePosition(position);

        mHandler.sendEmptyMessageDelayed(MSG_UPDATE_POSITION, 500);
    }

    /** 根据当前已播放时间，更新播放进度 */
    private void updatePosition(int position) {
//        logE("VideoPlayerActivity.updatePosition: position="+position);
        tv_position.setText(StringUtils.formatDuration(position));
        sk_position.setProgress(position);
    }

    /** 播放上一个视频 */
    private void playPre() {
        if (mPosition != 0) {
            mPosition--;
            playItem();
        }
    }

    /** 播放下一个视频 */
    private void playNext() {
        if (mPosition != mVideoItems.size() - 1) {
            mPosition++;
            playItem();
        }
    }

    /** 更新前一曲和后一曲使用的图片 */
    private void updatePreAndNextBtn() {
        iv_pre.setEnabled(mPosition != 0);
        iv_next.setEnabled(mPosition != mVideoItems.size() - 1);
    }

    /** 播放的当前mPosition选中的视频 */
    private void playItem() {
        if (mVideoItems == null || mVideoItems.size() == 0 || mPosition == -1) {
            return ;
        }

        VedioItemInfo videoItem = mVideoItems.get(mPosition);
//        VideoItem videoItem = (VideoItem) getIntent().getSerializableExtra("videoItem");
        //logE("VideoPlayerActivity.initData: videoItem=" + videoItem);

        // 播放视频
        videoView.setVideoPath(videoItem.getPath());
//        videoView.setMediaController(new MediaController(this));

        // 初始化标题
        tv_title.setText(videoItem.getTitle());

        // 更新按钮
        updatePreAndNextBtn();
    }

    /** 初始化时将控制面板隐藏 */
    private void initHideControlor() {
        // 使用getMeasuredHeight获取高度
        ll_top.measure(0, 0);
        int topH = ll_top.getMeasuredHeight();
        ViewPropertyAnimator.animate(ll_top).translationY(-ll_top.getMeasuredHeight());
//        logE("VideoPlayerActivity.initHideControlor: topH=" + topH + ";H=" + ll_top.getHeight());

        // 使用getHeight方法获取view高度
        ll_bottom.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
//                logE("VideoPlayerActivity.onGlobalLayout: ");
                ll_bottom.getViewTreeObserver().removeGlobalOnLayoutListener(this);

                ViewPropertyAnimator.animate(ll_bottom).translationY(ll_bottom.getHeight());
            }
        });

        isShowControlor = false;
    }

    /** 显示/隐藏控制面板 */
    private void switchControlor() {
        if (isShowControlor){
            // 显示状态
            hideControlor();
        }else {
            // 隐藏状态
            showControlor();
            notifyHideControlor();
        }
    }

    /** 隐藏控制面板 */
    private void hideControlor() {
        ViewPropertyAnimator.animate(ll_top).translationY(-ll_top.getHeight());
        ViewPropertyAnimator.animate(ll_bottom).translationY(ll_bottom.getHeight());
        isShowControlor = false;
    }

    /** 显示控制面板 */
    private void showControlor() {
        ViewPropertyAnimator.animate(ll_top).translationY(0);
        ViewPropertyAnimator.animate(ll_bottom).translationY(0);
        isShowControlor = true;
    }

    /** 通知延迟一段时间后隐藏控制面板 */
    private void notifyHideControlor() {
        mHandler.sendEmptyMessageDelayed(MSG_HIDE_CONTROLOR, 5000);
    }

    /** 切换全屏和默认比例 */
    private void switchFullScreen() {
        videoView.switchFullscreen();

        updateFullscreenBtn();
    }

    /** 根据当前是否是全屏，切换全屏按钮使用的图片 */
    private void updateFullscreenBtn() {
        if (videoView.isFullScreen()){
            // 全屏状态
            iv_fullscreen.setImageResource(R.drawable.video_defaultscreen_selector);
        }else {
            // 默认状态
            iv_fullscreen.setImageResource(R.drawable.video_fullscreen_selector);
        }
    }
}
