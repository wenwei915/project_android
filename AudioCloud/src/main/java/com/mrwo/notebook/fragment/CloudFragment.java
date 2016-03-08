package com.mrwo.notebook.fragment;

import android.view.View;

import com.mrwo.notebook.R;
import com.mrwo.notebook.utils.UIUtils;

/**
 * Created by Administrator on 2015/12/2.
 */
public class CloudFragment extends BaseFragment{
    @Override
    public View initView() {
        View view = UIUtils.inflate(R.layout.fragment_cloud);
        return view;
    }

    @Override
    public void initData() {

    }
}
