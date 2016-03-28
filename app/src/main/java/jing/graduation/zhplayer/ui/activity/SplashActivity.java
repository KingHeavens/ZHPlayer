package jing.graduation.zhplayer.ui.activity;


import android.content.Intent;
import android.os.Handler;
import android.view.View;
import android.widget.TextView;

import jing.graduation.zhplayer.R;

public class SplashActivity extends BaseActivity {
    @Override
    public int getLayoutID() {
        return R.layout.activity_splash;
    }

    @Override
    public void initView() {
    }

    /**
     * 2秒后进入主页
     */
    @Override
    public void initData() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent it = new Intent(SplashActivity.this,MainActivity.class);
                startActivity(it);
            }
        },2000);
    }

    @Override
    public void initListener() {

    }

    @Override
    public void processClick(View v) {

    }


}
