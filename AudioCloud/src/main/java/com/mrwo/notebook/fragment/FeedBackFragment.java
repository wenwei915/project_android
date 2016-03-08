package com.mrwo.notebook.fragment;

import android.view.View;

import com.mrwo.notebook.R;
import com.mrwo.notebook.utils.ToastUtil;
import com.mrwo.notebook.utils.UIUtils;

/**
 * Created by Administrator on 2015/12/4.
 */
public class FeedBackFragment extends BaseFragment{
    @Override
    public View initView() {
        View view = UIUtils.inflate(R.layout.fragment_feedback);
        view.findViewById(R.id.bt_feedback).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ToastUtil.show(UIUtils.getContext(),"反馈成功");
            }
        });
        return view;
    }

    @Override
    public void initData() {

    }
}
