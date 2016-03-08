package com.mrwo.notebook.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.mrwo.notebook.R;
import com.mrwo.notebook.bean.User;
import com.mrwo.notebook.fragment.AudioMeetingFragment;
import com.mrwo.notebook.fragment.AudioNoteFragment;
import com.mrwo.notebook.fragment.BaseFragment;
import com.mrwo.notebook.fragment.LoginFragment;
import com.mrwo.notebook.fragment.RecomendFragment;
import com.mrwo.notebook.utils.ConstantValue;
import com.mrwo.notebook.utils.ParsonJsonToUser;
import com.mrwo.notebook.utils.SpUtil;
import com.mrwo.notebook.utils.StringUtils;
import com.mrwo.notebook.utils.ToastUtil;
import com.mrwo.notebook.utils.UIUtils;
import com.mrwo.notebook.widget.NoScrollViewPager;
import com.mrwo.notebook.widget.StatusBarCompat;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private NavigationView mNavigationView;
    private DrawerLayout mDrawerLayout;
    private NoScrollViewPager mViewPager;
    private RadioGroup mGroup;
    private List<BaseFragment> mFragmentList;

    //是否被请求过的标记
    private boolean isRequest = false;
    private View login;
    private TextView tv_username;
    private TextView tv_des;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //通过自定义的View（StatusBarCompat）来设置状态栏的颜色
        StatusBarCompat.compat(this,getResources().getColor(R.color.status_bar_color));
        initUI();
        //初始化viewpage，将radiobutton与它绑定在一起
        initViewPage();
    }

    private void initViewPage() {
        //初始化存储Fragment的集合
        mFragmentList = new ArrayList<BaseFragment>();
        //初始化三个fragment
        mFragmentList.add(new RecomendFragment());
        mFragmentList.add(new AudioNoteFragment());
        mFragmentList.add(new AudioMeetingFragment());

//        System.out.println("集合的长度为:" + mFragmentList.size());
        mViewPager = (NoScrollViewPager) findViewById(R.id.viewpage);
        mGroup = (RadioGroup) findViewById(R.id.rg_group);

        //给viewpager设置适配器
        mViewPager.setAdapter(new MyPagerAdapter(getSupportFragmentManager()));
        //viewPager默认选中第二页
        mViewPager.setCurrentItem(1);

        //将RadioGroup和viewPager绑定
        mGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.rb_recomend:
                        mViewPager.setCurrentItem(0, false);
                        break;
                    case R.id.rb_audioNote:
                        mViewPager.setCurrentItem(1, false);
                        break;
                    case R.id.rb_audioMeeting:
                        mViewPager.setCurrentItem(2, false);
                        break;
                }
            }
        });

        //监听当前选中的页面，加载数据
        mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

                //选中当前页，加载数据
                BaseFragment baseFragment = mFragmentList.get(position);
                // 请求数据
                baseFragment.initData();
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        //第一页要手动加载数据
//        mFragmentList.get(1).initData();
    }

    class MyPagerAdapter extends FragmentPagerAdapter{

        public MyPagerAdapter(FragmentManager fm) {
            super(fm);
        }
        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        @Override
        public Fragment getItem(int position) {
//            System.out.println("放进去了第" + position + "个fragment"+mFragmentList.get(position));
            return mFragmentList.get(position);
        }
    }
    /**
     * 初始化UI
     */
    private void initUI() {
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mNavigationView = (NavigationView) findViewById(R.id.nv_menu);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //设置最左的图标
//        toolbar.setNavigationIcon(R.mipmap.ic_navigation);

        final ActionBar ab = getSupportActionBar();
        //设置为显示自定义view的模式
        ab.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);

        View toolbarView = View.inflate(getApplication(), R.layout.activitybar, null);
        ab.setCustomView(toolbarView);

        //点击头像，滑出侧边栏
        View usericon = toolbarView.findViewById(R.id.id_usericon);
        usericon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Toast.makeText(getApplication(),"头像被点击了",Toast.LENGTH_SHORT).show();
                //打开抽屉侧滑菜单
                mDrawerLayout.openDrawer(GravityCompat.START);
            }
        });

        //初始化侧边栏
        setupDrawerContent(mNavigationView);

    }

    private void setupDrawerContent(NavigationView navigationView) {
        //用户登录
        final View headerUser = UIUtils.inflate(R.layout.header_username);
        //将头布局添加到navigation中并指定宽高
        navigationView.addView(headerUser, ViewGroup.LayoutParams.MATCH_PARENT,UIUtils.dip2px(200));
        login = headerUser.findViewById(R.id.tv_login);
        tv_username = (TextView) headerUser.findViewById(R.id.tv_username);
        //当用户退出又进入之后，根据sp中是否有登录的标记，再设置值
        User user = ParsonJsonToUser.parson(SpUtil.getString(UIUtils.getContext(), ConstantValue.LOGIN_USER, ""));
        if (!StringUtils.isEmpty(user.nick)){
            //如果存在设置值
            tv_username.setText(user.nick);
            tv_username.setVisibility(View.VISIBLE);
            login.setVisibility(View.GONE);
        }
        //设置头中的签名的信息
        tv_des = (TextView) headerUser.findViewById(R.id.tv_userdes);
        if(!StringUtils.isEmpty(user.describe)){
            tv_des.setText("签名:"+user.describe);
        }
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                ToastUtil.show(UIUtils.getContext(), "立即登录");
                //跳转到登录页面，通过Fragment的方式
                //获取管理者
                FragmentManager fragmentManager = getSupportFragmentManager();
                //开启事物
                FragmentTransaction transaction = fragmentManager.beginTransaction();
                transaction.setCustomAnimations(R.anim.next_in_anim, R.anim.next_out_anim);
                //动态替换
                transaction.replace(android.R.id.content, new LoginFragment(),"loginFragment");
                //提交事物
                transaction.commit();
            }
        });

        //设置侧边栏navigationView的菜单按钮的监听
        navigationView.setNavigationItemSelectedListener(

                new NavigationView.OnNavigationItemSelectedListener() {
                    private MenuItem mPreMenuItem;

                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        if (mPreMenuItem != null) mPreMenuItem.setChecked(false);

                        menuItem.setChecked(true);
                        //关闭抽屉侧滑菜单
//                        mDrawerLayout.closeDrawers();
                        mPreMenuItem = menuItem;
                        switch (menuItem.getItemId()) {
                            case R.id.my_cloud:
                                if (!StringUtils.isEmpty(SpUtil.getString(UIUtils.getContext(), ConstantValue.LOGIN_USER, ""))) {
                                    Intent intent = new Intent(MainActivity.this, MenuActivity.class);
                                    intent.putExtra("value", "我的云端");
                                    startActivity(intent);
                                    overridePendingTransition(R.anim.next_in_anim, R.anim.next_out_anim);
                                } else {
                                    ToastUtil.show(UIUtils.getContext(), "请先登录");
                                }
                                break;
                            case R.id.my_center:
                                if (!StringUtils.isEmpty(SpUtil.getString(UIUtils.getContext(), ConstantValue.LOGIN_USER, ""))) {
                                    Intent intent1 = new Intent(MainActivity.this, MenuActivity.class);
                                    intent1.putExtra("value", "个人中心");
                                    startActivity(intent1);
                                    //动画
                                    overridePendingTransition(R.anim.next_in_anim, R.anim.next_out_anim);
                                } else {
                                    ToastUtil.show(UIUtils.getContext(), "请先登录");
                                }
                                break;

                            case R.id.my_night:
                                Toast.makeText(getApplication(), "夜间", Toast.LENGTH_SHORT).show();
                                mDrawerLayout.closeDrawers();
                                break;
                            case R.id.my_setting:
                                Intent intent3 = new Intent(MainActivity.this, MenuActivity.class);
                                intent3.putExtra("value", "设置");
                                startActivity(intent3);
                                overridePendingTransition(R.anim.next_in_anim, R.anim.next_out_anim);
                                break;
                        }
                        return true;
                    }
                });
    }

    //修改登陆标示的方法
    public void setUser(String nick,String describe){
        tv_username.setText(nick);
        if(!StringUtils.isEmpty(describe)){
            tv_des.setText("签名:"+describe);
        }
        tv_username.setVisibility(View.VISIBLE);
        login.setVisibility(View.GONE);
    }

    private long exitTime = 0;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN){
            if((System.currentTimeMillis()-exitTime) > 2000){
                ToastUtil.show(UIUtils.getContext(),"再按一次返回键退出程序");
                exitTime = System.currentTimeMillis();
            } else {
                finish();
                System.exit(0);
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

/*    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_navigation_view, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        if(item.getItemId() == android.R.id.home)
        {
            mDrawerLayout.openDrawer(GravityCompat.START);
            return true ;
        }
        return super.onOptionsItemSelected(item);
    }*/
}
