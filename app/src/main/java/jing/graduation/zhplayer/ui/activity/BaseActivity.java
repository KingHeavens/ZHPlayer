package jing.graduation.zhplayer.ui.activity;


import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.Toast;

import jing.graduation.zhplayer.R;
import jing.graduation.zhplayer.ui.IUI;
import jing.graduation.zhplayer.utils.LogUtils;

/**
 * Created by ZhJing03 on 2016/2/27.
 * Desc:所有Activity的基类
 */
abstract class BaseActivity extends FragmentActivity implements View.OnClickListener,IUI{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutID());
        initView();
        initListener();
        initData();
        //注册公共功能
        regCommcon();
    }

    /**
     * 注册返回按钮
     */
    private void regCommcon(){
        View back = findViewById(R.id.back);
        if(back != null){
            back.setOnClickListener(this);
        }
    }
    /*点击事件*/
    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.back:
                finish();
                break;
            default:
                processClick(v);
        }
    }

    /*打印Error级别的Log*/
    protected void showLogE(String error){
        LogUtils.e(error);
    }
    /*传入资源ID弹出吐司*/
    protected  void showToast(int resid){
        Toast.makeText(getApplicationContext(),resid ,Toast.LENGTH_LONG).show();
    }
    /*传入String弹出吐司*/
    protected  void showToast(String info){
        Toast.makeText(getApplicationContext(),info,Toast.LENGTH_LONG).show();
    }
}
