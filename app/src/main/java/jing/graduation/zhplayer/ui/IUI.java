package jing.graduation.zhplayer.ui;

import android.view.View;

/**
 * Created by Administrator on 2016/3/27.
 *
 */
public interface IUI {
    /**
     *获取资源ID
     * @return
     */
    int getLayoutID();
    /**
     * 初始化布局
     */
    void initView();
    /**
     *添加监听
     */
    void initListener();
    /**
     *初始化数据
     */
    void initData();


    /**
     *点击事件
     */
    void processClick(View v);
}
