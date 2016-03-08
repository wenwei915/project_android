package com.mrwo.notebook.fragment;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.mrwo.notebook.R;
import com.mrwo.notebook.activity.MenuActivity;
import com.mrwo.notebook.utils.ConstantValue;
import com.mrwo.notebook.utils.SpUtil;
import com.mrwo.notebook.utils.ToastUtil;
import com.mrwo.notebook.utils.UIUtils;

import java.io.File;

/**
 * Created by Administrator on 2015/12/2.
 */
public class SettingFragment extends BaseFragment implements View.OnClickListener {

    @ViewInject(R.id.bt_updata)
    private Button bt_updata;

    @ViewInject(R.id.bt_send)
    private Button bt_send;

    @ViewInject(R.id.rl_clear)
    private RelativeLayout rl_clear;

    @ViewInject(R.id.rl_feedback)
    private RelativeLayout rl_feedback;

    @ViewInject(R.id.rl_about)
    private RelativeLayout rl_about;

    @Override
    public View initView() {
        View view = UIUtils.inflate(R.layout.fragment_setting);
        ViewUtils.inject(this,view);
        //设置点击事件
        rl_clear.setOnClickListener(this);
        rl_feedback.setOnClickListener(this);
        rl_about.setOnClickListener(this);
        bt_updata.setOnClickListener(this);
        bt_send.setOnClickListener(this);
        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.rl_clear:
                //拿到缓存的数量
                deleteFilesByDirectory(UIUtils.getContext().getCacheDir());
                ToastUtil.show(UIUtils.getContext(),"清除成功");
                break;
            case R.id.rl_feedback:
                Intent intent = new Intent(mActivity, MenuActivity.class);
                intent.putExtra("value", "意见反馈");
                startActivity(intent);
                mActivity.overridePendingTransition(R.anim.next_in_anim, R.anim.next_out_anim);
                break;
            case R.id.rl_about:
                Intent intent1 = new Intent(mActivity, MenuActivity.class);
                intent1.putExtra("value", "关于我们");
                startActivity(intent1);
                mActivity.overridePendingTransition(R.anim.next_in_anim, R.anim.next_out_anim);
                break;
            case R.id.bt_updata:
                checkUpdata();
                break;
            case R.id.bt_send:
                sendInfo();
                break;

        }
    }

    private void sendInfo() {
        boolean isSend = SpUtil.getBoolean(UIUtils.getContext(), ConstantValue.IS_SEND,false);
        //将设置的信息写到sp中
        if(isSend){
            bt_send.setBackgroundResource(R.mipmap.check_false);
            SpUtil.setBoolean(UIUtils.getContext(), ConstantValue.IS_SEND, isSend);
        }else
            bt_send.setBackgroundResource(R.mipmap.check_true);
            SpUtil.setBoolean(UIUtils.getContext(), ConstantValue.IS_SEND,!isSend);
    }

    private void checkUpdata() {
        boolean isUpdata = SpUtil.getBoolean(UIUtils.getContext(), ConstantValue.IS_UPDATA,false);
        if(isUpdata){
            bt_updata.setBackgroundResource(R.mipmap.check_false);
            SpUtil.setBoolean(UIUtils.getContext(), ConstantValue.IS_UPDATA, isUpdata);
        }else
            bt_updata.setBackgroundResource(R.mipmap.check_true);
            SpUtil.setBoolean(UIUtils.getContext(), ConstantValue.IS_UPDATA, !isUpdata);

    }

    //清除本地缓存的方法
    private static void deleteFilesByDirectory(File directory) {
        if (directory != null && directory.exists() && directory.isDirectory()) {
            for (File item : directory.listFiles()) {
                item.delete();
            }
        }
    }


    @Override
    public void initData() {

    }
}
