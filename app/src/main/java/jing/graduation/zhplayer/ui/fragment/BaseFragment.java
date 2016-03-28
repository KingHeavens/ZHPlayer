package jing.graduation.zhplayer.ui.fragment;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import jing.graduation.zhplayer.R;
import jing.graduation.zhplayer.ui.IUI;

/**
 * Created by Administrator on 2016/3/27.
 * Desc:
 * Version:
 * History:
 */
public abstract class BaseFragment extends Fragment implements IUI,View.OnClickListener{
    protected Activity mActivity;
    private Context mContext;
    private View view;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mActivity = getActivity();
        mContext = mActivity.getApplicationContext();
        view = inflater.inflate(getLayoutID(),null);
        initView();
        initData();
        initListener();
        return view;
    }

    /**
     * 处理findViewById
     * @param id
     * @return
     */
    public View findViewById(int id) {
        return view.findViewById(id);
    }

    /**
     * 处理公共事件
     */
    public void regCommonBtn(){
       View back = findViewById(R.id.back);
       if(back != null){
            back.setOnClickListener(this);
       }
    }

    /**
     * 处理点击事件
     * @param v
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.back:
                getFragmentManager().popBackStack();
                break;
            default:
                processClick(v);
                break;
        }
    }

}
