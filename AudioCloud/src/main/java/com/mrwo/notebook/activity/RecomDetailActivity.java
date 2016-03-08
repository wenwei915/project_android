package com.mrwo.notebook.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebSettings.TextSize;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.mrwo.notebook.R;
import com.mrwo.notebook.utils.UIUtils;
import com.mrwo.notebook.widget.StatusBarCompat;

import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.onekeyshare.OnekeyShare;

/**
 * Created by Administrator on 2015/11/30.
 */
public class RecomDetailActivity extends AppCompatActivity implements View.OnClickListener {

    @ViewInject(R.id.webView)
    private WebView mWebView;

    @ViewInject(R.id.toolbar)
    private Toolbar mToolbar;

    private ImageView iv_share;
    private ImageView iv_textsize;

    private int mWhich;//临时选中的字体大小(点击确认前)
    private int mCurrent = 2;//设置选中的字体大小(点击确认之后)

    private String recomUrl;
    private WebSettings settings;
    private View mToolbarView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activtty_recomdetail);
        //通过自定义的View（StatusBarCompat）来设置状态栏的颜色
        StatusBarCompat.compat(this, getResources().getColor(R.color.status_bar_color));
        //获取传过来的地址
        recomUrl = getIntent().getStringExtra("recomUrl");

        //使用注解获取控件
        ViewUtils.inject(this);
        //初始化toolbar
        initToolbar();
        //初始化wenView
        initWebView();

    }

    private void initToolbar() {
        setSupportActionBar(mToolbar);
        mToolbar.setNavigationIcon(R.mipmap.back);

        //获取actionbar
        ActionBar ab = getSupportActionBar();
        //设置view的显示模式
        ab.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        mToolbarView = UIUtils.inflate(R.layout.recomdetailbar);
        ab.setCustomView(mToolbarView);
        iv_share = (ImageView) mToolbarView.findViewById(R.id.iv_share);
        iv_textsize = (ImageView) mToolbarView.findViewById(R.id.iv_textsize);
        //找到控件，设置点击事件
        iv_share.setOnClickListener(this);
        iv_textsize.setOnClickListener(this);

    }
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            //分享
            case R.id.iv_share:
                showShare();
                break;
            //设置字体大小
            case R.id.iv_textsize:
                showTextSize();
                break;
        }
    }

    private void showTextSize() {
        String[] textSize = new String[]{"超大字体","大字体","正常字体","小字体","超小字体"};

        //弹出窗口
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("设置字体大小");
        builder.setSingleChoiceItems(textSize, mCurrent, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                mWhich = which;
            }
        } );

        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //设置字体大小
                switch (mWhich) {
                    case 0:
                        settings.setTextSize(TextSize.LARGEST);
                        //可以指定具体设置的字体的大小
//					settings.setTextZoom(20);
                        break;
                    case 1:
                        settings.setTextSize(TextSize.LARGER);
                        break;
                    case 2:
                        settings.setTextSize(TextSize.NORMAL);
                        break;
                    case 3:
                        settings.setTextSize(TextSize.SMALLER);
                        break;
                    case 4:
                        settings.setTextSize(TextSize.SMALLEST);
                        break;
                    default:
                        break;
                }
                mCurrent = mWhich;
            }
        });

        builder.setNegativeButton("取消", null);

        builder.show();
    }

    private void showShare() {
        ShareSDK.initSDK(this);
        OnekeyShare oks = new OnekeyShare();
        //关闭sso授权
        oks.disableSSOWhenAuthorize();

// 分享时Notification的图标和文字  2.5.9以后的版本不调用此方法
        //oks.setNotification(R.drawable.ic_launcher, getString(R.string.app_name));
        // title标题，印象笔记、邮箱、信息、微信、人人网和QQ空间使用
        oks.setTitle(getString(R.string.share));
        // titleUrl是标题的网络链接，仅在人人网和QQ空间使用
        oks.setTitleUrl("http://sharesdk.cn");
        // text是分享文本，所有平台都需要这个字段
        oks.setText("我是分享文本");
        // imagePath是图片的本地路径，Linked-In以外的平台都支持此参数
        oks.setImagePath("/sdcard/test.jpg");//确保SDcard下面存在此张图片
        // url仅在微信（包括好友和朋友圈）中使用
        oks.setUrl("http://sharesdk.cn");
        // comment是我对这条分享的评论，仅在人人网和QQ空间使用
        oks.setComment("我是测试评论文本");
        // site是分享此内容的网站名称，仅在QQ空间使用
        oks.setSite(getString(R.string.app_name));
        // siteUrl是分享此内容的网站地址，仅在QQ空间使用
        oks.setSiteUrl("http://sharesdk.cn");

// 启动分享GUI
        oks.show(this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){
            case android.R.id.home:
                RecomDetailActivity.this.finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void initWebView() {

        //加载网页
        mWebView.loadUrl(recomUrl);

        settings = mWebView.getSettings();
        //显示缩放按钮(wap网页不支持)
//        settings.setBuiltInZoomControls(true);
        //支持双击缩放(wap网页不支持)
        settings.setUseWideViewPort(true);
        //支持js功能
        settings.setJavaScriptEnabled(true);

        //设置网页加载的状态
        mWebView.setWebViewClient(new WebViewClient(){
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                //网页开始加载了
                System.out.println("网页开始加载了");
//                pb_news.setVisibility(View.VISIBLE);
            }
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                System.out.println("网页加载完了");
                //网页加载完了
//                pb_news.setVisibility(View.INVISIBLE);
            }
            //所有连接跳转都会走此方法
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                // 在跳转链接时强制在当前webview中加载
                view.loadUrl(url);
                return true;
            }
        });
    }
}
