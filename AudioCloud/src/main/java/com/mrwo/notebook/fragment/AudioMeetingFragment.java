package com.mrwo.notebook.fragment;

import android.view.View;
import android.widget.TextView;

import com.mrwo.notebook.fragment.BaseFragment;

/**
 * Created by Administrator on 2015/11/29.
 */
public class AudioMeetingFragment extends BaseFragment {
    @Override
    public View initView() {
        TextView view = new TextView(getActivity());
        view.setText(this.getClass().getSimpleName());
        return view;
    }

    @Override
    public void initData() {

    }
}
