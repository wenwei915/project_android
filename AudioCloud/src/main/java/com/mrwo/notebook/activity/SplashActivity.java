package com.mrwo.notebook.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Window;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.widget.LinearLayout;

import com.mrwo.notebook.R;

/**
 * Created by Administrator on 2015/11/27.
 */
public class SplashActivity extends Activity {

    private LinearLayout ll_root;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_splash);
        initUI();
        initAnimation();
    }

    /**
     * 初始化动画
     */
    private void initAnimation() {
        AnimationSet animationSet = new AnimationSet(false);
        // 缩放动画
        ScaleAnimation acaleAnimation = new ScaleAnimation(0, 1, 0, 1,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
                0.5f);
        // 设置动画执行时间
        acaleAnimation.setDuration(1000);
        // 设置动画执行后的状态
        acaleAnimation.setFillAfter(true);

        // 渐变动画
        AlphaAnimation alphaAnimation = new AlphaAnimation(0, 1);
        // 设置动画执行时间
        alphaAnimation.setDuration(3000);
        // 设置动画执行后的状态
        alphaAnimation.setFillAfter(true);

        //将动画添加给集合
        animationSet.addAnimation(acaleAnimation);
        animationSet.addAnimation(alphaAnimation);

        // 将动画设置给ll_root
        ll_root.startAnimation(acaleAnimation);

        // 给动画设置监听,动画执行结束后跳转到主页面
        acaleAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                startActivity(new Intent(SplashActivity.this,
                        MainActivity.class));
                finish();
            }
        });
    }

    /**
     * 初始化UI
     */
    private void initUI() {
        ll_root = (LinearLayout) findViewById(R.id.ll_root);
    }
}
