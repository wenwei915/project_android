package com.mrwo.notebook.activity;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.task.PriorityAsyncTask;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.mrwo.notebook.R;
import com.mrwo.notebook.bean.AudioText;
import com.mrwo.notebook.db.AudioDao;
import com.mrwo.notebook.db.MySqliteOpenHelper;
import com.mrwo.notebook.fragment.CenterFragment;
import com.mrwo.notebook.utils.ToastUtil;
import com.mrwo.notebook.utils.UIUtils;
import com.mrwo.notebook.widget.StatusBarCompat;

public class EditAudioActivity extends AppCompatActivity {

    @ViewInject(R.id.et_title)
    private EditText et_title;

    @ViewInject(R.id.et_note)
    private EditText et_note;

    @ViewInject(R.id.toolbar)
    private Toolbar mToolbar;

    //传过来的语音
    private String mAudioText;
    private TextView tv_complete;
    private String mId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_audio);
        mAudioText = getIntent().getStringExtra("audioText");
        mId = getIntent().getStringExtra("id");
//        System.out.println("获得的角标是" + mPosition);
        //使用注解
        ViewUtils.inject(this);
        //如果是点击条目过来的，要根据position的查询后，回显
        if(mId!=null){
            initData();
        }else{
            //设置语音识别出的值
            et_note.setText(mAudioText);
        }

        et_note.setOnTouchListener(new EditViewOnTouchListener());

        initToolbar();
    }

    private void initData() {
        //先查询，
        AudioText audioText = new AudioDao(UIUtils.getContext()).queryOne(Integer.valueOf(mId));
        if(audioText!=null){
            et_title.setText(audioText.title);
            et_note.setText(audioText.content);
        }
    }

    /**
     * 初始化UI控件
     */
    private void initToolbar() {
        //通过自定义的View（StatusBarCompat）来设置状态栏的颜色
        StatusBarCompat.compat(this, getResources().getColor(R.color.status_bar_color));

        setSupportActionBar(mToolbar);
        //设置最左的图标
        mToolbar.setNavigationIcon(R.mipmap.back);

        final ActionBar ab = getSupportActionBar();
        //设置为显示自定义view的模式
        ab.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);

        View toolbarView = View.inflate(getApplication(), R.layout.menubar, null);
        tv_complete = (TextView) toolbarView.findViewById(R.id.tv_complete);
        tv_complete.setVisibility(View.VISIBLE);
        //设置点击事件
        tv_complete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //保存记事，保存到数据库
                AudioText audioText = new AudioText();
                String date = audioText.date = System.currentTimeMillis()+"";
                audioText.title = et_title.getText().toString().trim();
                audioText.content = et_note.getText().toString().trim();
                //判断mPosition的值
                if (mId==null){
                    //新增记事
                    //调用方法插入数据
                    boolean result = new AudioDao(UIUtils.getContext()).add(audioText);
                    if (result){
                        ToastUtil.show(UIUtils.getContext(),"保存成功");
                        //再根据当前的的时间查找已经存入数据库的内容给mId赋值
                        int i = new AudioDao(UIUtils.getContext()).queryByDate(date);
                        if(i!=-1){
                            mId = i+"";
                        }
                    }else{
                        ToastUtil.show(UIUtils.getContext(),"保存失败");
                    }
                }else{
                    //修改记事
                    audioText.id = mId;
                    int update = new AudioDao(UIUtils.getContext()).update(audioText);
                    if (update>0){
                        ToastUtil.show(UIUtils.getContext(),"更新成功");
                    }else{
                        ToastUtil.show(UIUtils.getContext(),"更新失败");
                    }
                }
            }
        });

        ab.setCustomView(toolbarView);
    }

    //保存记事

    //更新记事
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){
            case android.R.id.home:
                //跳转到MainActivity
                startActivity(new Intent(this,MainActivity.class));
                overridePendingTransition(R.anim.regst_prev_in_anim,R.anim.regst_prev_out_anim);
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    //处理返回事件
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN){
            //跳转到MainActivity
            startActivity(new Intent(this,MainActivity.class));
            overridePendingTransition(R.anim.regst_prev_in_anim,R.anim.regst_prev_out_anim);
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }

    // 计算点击的次数
    private int count = 0;
    // 第一次点击的时间 long型
    private long firstClick = 0;
    // 最后一次点击的时间
    private long lastClick = 0;

    //处理双击事件，被双击时加载图片
    private class EditViewOnTouchListener implements View.OnTouchListener{
        @Override
        public boolean onTouch(View v, MotionEvent event) {

            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                // 如果第二次点击 距离第一次点击时间过长 那么将第二次点击看为第一次点击
                if (firstClick != 0 && System.currentTimeMillis() - firstClick > 500) {
                    count = 0;
                }
                count++;
                if (count == 1) {
                    firstClick = System.currentTimeMillis();
                } else if (count == 2) {
                    lastClick = System.currentTimeMillis();
                    // 两次点击小于500ms 也就是连续点击
                    if (lastClick - firstClick < 500) {
                        ToastUtil.show(UIUtils.getContext(), "我被双击了");
                    }
                    clear();
                }
            }

            return false;
        }

            // 清空状态
        private void clear(){
            count = 0;
            firstClick = 0;
            lastClick = 0;
        }
    }
}
