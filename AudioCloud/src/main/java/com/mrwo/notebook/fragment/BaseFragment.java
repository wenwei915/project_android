package com.mrwo.notebook.fragment;


import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * A simple {@link Fragment} subclass.
 */
public abstract class BaseFragment extends Fragment {

    public Activity mActivity;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mActivity = getActivity();
        return initView();
    }

    //fragment所依赖的activity的oncreate方法执行结束后调用
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
//        initData();
    }

    /**
     * 初始化fragment的布局,具体样式由子类实现
     *
     * @return 返回一个布局文件
     */
    public abstract View initView();

    //初始化数据
    public abstract void initData();
}
