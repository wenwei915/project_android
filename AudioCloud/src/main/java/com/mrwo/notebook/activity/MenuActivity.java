package com.mrwo.notebook.activity;

import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.mrwo.notebook.R;
import com.mrwo.notebook.fragment.AboutFragment;
import com.mrwo.notebook.fragment.CenterFragment;
import com.mrwo.notebook.fragment.CloudFragment;
import com.mrwo.notebook.fragment.FeedBackFragment;
import com.mrwo.notebook.fragment.SettingFragment;
import com.mrwo.notebook.utils.UIUtils;
import com.mrwo.notebook.widget.LoadmoreListView;
import com.mrwo.notebook.widget.StatusBarCompat;

public class MenuActivity extends AppCompatActivity {

    @ViewInject(R.id.toolbar)
    private Toolbar mToolbar;

    @ViewInject(R.id.fl_content)
    private FrameLayout fl_coOtent;

    private TextView tv_title;
    private String mValue;
    private TextView tv_complete;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        //使用注解获取控件
        ViewUtils.inject(this);

        //通过自定义的View（StatusBarCompat）来设置状态栏的颜色
        StatusBarCompat.compat(this, getResources().getColor(R.color.status_bar_color));
        //初始化Toolbar
        initToolbar();
        //获取传过来的标示
        mValue = getIntent().getStringExtra("value");
        System.out.println("传过来的值"+mValue);
        FragmentManager fragmentManager = getSupportFragmentManager();
        jumpFragment(fragmentManager, mValue);
    }

    private void initToolbar() {
        setSupportActionBar(mToolbar);
        //设置最左的图标
        mToolbar.setNavigationIcon(R.mipmap.back);

        final ActionBar ab = getSupportActionBar();
        //设置为显示自定义view的模式
        ab.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);

        View toolbarView = View.inflate(getApplication(), R.layout.menubar, null);
        tv_title = (TextView) toolbarView.findViewById(R.id.tv_title);
        tv_complete = (TextView) toolbarView.findViewById(R.id.tv_complete);
        //设置点击事件
        tv_complete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //提交个人中心用户填写的数据
                CenterFragment fragment = (CenterFragment) MenuActivity.this.getSupportFragmentManager().findFragmentByTag("CenterFragment");
                fragment.submitInfo();
                System.out.println(".................."+fragment);
            }
        });

        ab.setCustomView(toolbarView);
        //获取视图树，修改标题(也可以将此段代码放到jumpFragment方法中，注意的是时机问题)
        toolbarView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                tv_title.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                tv_title.setText(mValue);
            }
        });

    }

    private void jumpFragment(FragmentManager fragmentManager, String value) {
        switch (value){
            case "个人中心":
                tv_complete.setVisibility(View.VISIBLE);
                fragmentManager.beginTransaction().replace(R.id.fl_content,new CenterFragment(),"CenterFragment").commit();
                break;
            case "我的云端":
                fragmentManager.beginTransaction().replace(R.id.fl_content,new CloudFragment()).commit();
                break;
            case "设置":
                fragmentManager.beginTransaction().replace(R.id.fl_content,new SettingFragment()).commit();
                break;
            case "意见反馈":
                fragmentManager.beginTransaction().replace(R.id.fl_content,new FeedBackFragment()).commit();
                break;
            case "关于我们":
                fragmentManager.beginTransaction().replace(R.id.fl_content,new AboutFragment()).commit();
                break;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }


}
