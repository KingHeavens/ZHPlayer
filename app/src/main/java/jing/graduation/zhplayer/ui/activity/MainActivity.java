package jing.graduation.zhplayer.ui.activity;

import android.graphics.Point;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nineoldandroids.view.ViewHelper;
import com.nineoldandroids.view.ViewPropertyAnimator;

import java.util.ArrayList;
import java.util.List;

import jing.graduation.zhplayer.R;
import jing.graduation.zhplayer.ui.fragment.AudioListFragment;
import jing.graduation.zhplayer.ui.fragment.VedioListFragment;

public class MainActivity extends BaseActivity {

    private LinearLayout mMainTabs;
    private TextView mTvVideo;
    private TextView mTvAudio;
    private View mIndicateLine;
    private ViewPager mViewPager;
    private View mIndicateLIne;
    private List<Fragment> mFragments;
    private ViewPageAdapter mPageAdapter;
    private int screenW;


    @Override
    public int getLayoutID() {
        return R.layout.activity_main;
    }

    @Override
    public void initView() {
        mMainTabs = (LinearLayout) findViewById(R.id.main_ll_tabs);
        mTvVideo = (TextView) findViewById(R.id.main_tv_video);
        mTvAudio = (TextView) findViewById(R.id.main_tv_audio);
        mIndicateLine = findViewById(R.id.main_indicate_line);
        mViewPager = (ViewPager) findViewById(R.id.main_viewpager);
    }

    @Override
    public void initListener() {
        //按钮点击事件监听
        mTvVideo.setOnClickListener(this);
        mTvAudio.setOnClickListener(this);

        mFragments = new ArrayList<>();
        mPageAdapter = new ViewPageAdapter(getSupportFragmentManager(), mFragments);
        mViewPager.setAdapter(mPageAdapter);
        //viewpager滑动事件监听
        PageChangeListener mPageChangeListener = new PageChangeListener();
        mViewPager.addOnPageChangeListener(mPageChangeListener);
    }

    @Override
    public void initData() {
        //添加视频页面和音频页面
        mFragments.add(new VedioListFragment());
        mFragments.add(new AudioListFragment());
        mPageAdapter.notifyDataSetChanged();
        //初始化tab页数
        changeTab(0);
        //获取屏幕宽度，根据fragment个数计算indication宽度
        Point point = new Point();
        getWindowManager().getDefaultDisplay().getSize(point);
        screenW = point.x;
        screenW = screenW / mFragments.size();
        mIndicateLine.getLayoutParams().width = screenW;
        //重新计算大小，重新绘制界面
        mIndicateLine.requestLayout();
    }

    /**
     * 选择指定位置tab栏
     */
    private void changeTab(int position) {
        changetab(position, 0, mTvVideo);
        changetab(position, 1, mTvAudio);
    }

    /**
     * 判断当前位置是不是指示器所在位置 并修改指示器状态
     *
     * @param position    切换位置
     * @param tabPosition 指示器位置
     * @param tab         要变化的textView
     */
    private void changetab(int position, int tabPosition, TextView tab) {
        int selectColor = getResources().getColor(R.color.green);
        int unSelectColor = getResources().getColor(R.color.halfwhite);
        if (position == tabPosition) {
            tab.setTextColor(selectColor);
            ViewPropertyAnimator.animate(tab).scaleX(1.2f).scaleY(1.2f);
        } else {
            tab.setTextColor(unSelectColor);
            ViewPropertyAnimator.animate(tab).scaleX(1.0f).scaleY(1.0f);
        }
    }

    @Override
    public void processClick(View v) {
        switch (v.getId()) {
            case R.id.main_tv_video:
                mViewPager.setCurrentItem(0);
                break;
            case R.id.main_tv_audio:
                mViewPager.setCurrentItem(1);
                break;
            default:
                break;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }





    /**
     * viewapger 适配器
     */
    private class ViewPageAdapter extends FragmentPagerAdapter {
        List<Fragment> lists;

        public ViewPageAdapter(FragmentManager supportFragmentManager, List<Fragment> lists) {
            super(supportFragmentManager);
            this.lists = lists;
        }

        @Override
        public int getCount() {
            return lists.size();
        }

        @Override
        public Fragment getItem(int position) {
            return lists.get(position);
        }
    }

    /**
     * Viewpage页面滚动事件监听
     */
    private class PageChangeListener implements ViewPager.OnPageChangeListener {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            /*showLogE("position:" + position +" positionOffset:"+ positionOffset
                    + "positionOffsetPixels:" + positionOffsetPixels);*/
            //计算偏移位置
            int offset = (int) (positionOffset * screenW);
            //计算移动位置
            int translationX = position * screenW + offset;
            ViewHelper.setTranslationX(mIndicateLine,translationX);
        }

        @Override
        public void onPageSelected(int position) {
            changeTab(position);
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    }
}
