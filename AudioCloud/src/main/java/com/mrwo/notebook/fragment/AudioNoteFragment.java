package com.mrwo.notebook.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.iflytek.cloud.RecognizerResult;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechSynthesizer;
import com.iflytek.cloud.SpeechUtility;
import com.iflytek.cloud.SynthesizerListener;
import com.iflytek.cloud.ui.RecognizerDialog;
import com.iflytek.cloud.ui.RecognizerDialogListener;
import com.mrwo.notebook.R;
import com.mrwo.notebook.activity.EditAudioActivity;
import com.mrwo.notebook.activity.MainActivity;
import com.mrwo.notebook.bean.AudioInfo;
import com.mrwo.notebook.bean.AudioText;
import com.mrwo.notebook.db.AudioDao;
import com.mrwo.notebook.fragment.BaseFragment;
import com.mrwo.notebook.utils.ToastUtil;
import com.mrwo.notebook.utils.UIUtils;
import com.mrwo.notebook.widget.SwipeLayout;
import com.mrwo.notebook.widget.SwipeLayoutManager;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Administrator on 2015/11/29.
 */
public class AudioNoteFragment extends BaseFragment {

    //初始化一个StringBuilger
    private StringBuilder mSb = new StringBuilder();;
    private RecyclerView mRecyclerView;
    private List<String> mData;
    private List<AudioText> mAudioData;
    private FrameLayout fl_content;
    private View noteEmptyPage;
    private View mRecyClerViewPage;
    private MyAdapter mMyAdapter;

    @Override
    public View initView() {

        return initUI();
    }

    private View initUI() {
        View view = UIUtils.inflate(R.layout.fragment_audionote);
        //要放置lRecyclerView的FrameLayout
        fl_content = (FrameLayout) view.findViewById(R.id.fl_content);
        FloatingActionButton fab = (FloatingActionButton) view.findViewById(R.id.fab);

        initRecyclerView();

        //加载空布局
        noteEmptyPage = UIUtils.inflate(R.layout.note_empty_page);
        //将空布局加入
        fl_content.addView(noteEmptyPage);
        //将RecyclerView布局加入
        fl_content.addView(mRecyClerViewPage);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                ToastUtil.show(UIUtils.getContext(),"FloatingActionButton");
                //开始录音，用科大讯飞 834576
                StartAudio();
            }
        });

        initData();
        return view;
    }

    /**
     * 初始化RecyclerView
     */
    private void initRecyclerView() {
        //测试数据
//        mData = new ArrayList<String>();
//
//        for (int i = 1;i<=100;i++) {
//            mData.add("测试数据"+i);
//        }

//        //初始化RecyclerView
//        initRecyclerView(mAudioData);
        mRecyClerViewPage = UIUtils.inflate(R.layout.recycleview_page);
        mRecyclerView = (RecyclerView) mRecyClerViewPage.findViewById(R.id.recyclerview);
        //设置一个布局
        LinearLayoutManager layoutManager = new LinearLayoutManager(UIUtils.getContext());
        mRecyclerView.setLayoutManager(layoutManager);
        mMyAdapter = new MyAdapter();
        mRecyclerView.setAdapter(mMyAdapter);

        mRecyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                //关闭已经打开的layout
                SwipeLayoutManager.getInstance().closeCurrentLayout();
//                ToastUtil.show(UIUtils.getContext(),"滑动监听");
            }
        });
    }

    class MyAdapter extends RecyclerView.Adapter<MyViewHolder>{

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = UIUtils.inflate(R.layout.recyclerview_item);
            return new MyViewHolder(view);
        }

        @Override
        public void onBindViewHolder(MyViewHolder holder, int position) {
            holder.setData(position);
        }

        @Override
        public int getItemCount() {
            return mAudioData.size();
        }
    }

    class MyViewHolder extends RecyclerView.ViewHolder{
        TextView tv_title;
        TextView tv_content;
        TextView tv_date;
        Button bt_start;
        TextView tv_delete;
        LinearLayout ll_root;
        SwipeLayout sl_root;
        ArrayList<SwipeLayout> opendItems = new ArrayList<SwipeLayout>();

        public MyViewHolder(View itemView) {
            super(itemView);
            tv_title = (TextView) itemView.findViewById(R.id.tv_title);
            tv_content = (TextView) itemView.findViewById(R.id.tv_content);
            tv_date = (TextView) itemView.findViewById(R.id.tv_date);
            bt_start = (Button) itemView.findViewById(R.id.bt_start);
            ll_root = (LinearLayout) itemView.findViewById(R.id.ll_root);
            sl_root = (SwipeLayout) itemView.findViewById(R.id.sl_root);
            tv_delete = (TextView) itemView.findViewById(R.id.tv_delete);
        }

        public void setData(final int position){
            tv_title.setText(mAudioData.get(position).title);
            tv_content.setText(mAudioData.get(position).content);
            //格式化毫秒值得日期
            String date = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(new Date(Long.valueOf(mAudioData.get(position).date)));
            tv_date.setText(date);

            //设置按钮的点击事件播放
            itemView.findViewById(R.id.bt_start).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ToastUtil.show(UIUtils.getContext(), "播放语音"+position);
                    readAudio(mAudioData.get(position).content);
                }
            });

            tv_delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    ToastUtil.show(UIUtils.getContext(), "删除");
                    //删除前，先关闭已经打开的layout
                    SwipeLayoutManager.getInstance().closeCurrentLayout();
                    //从数据库中删除
                    int result = new AudioDao(UIUtils.getContext()).delete(mAudioData.get(position).id);
                    if(result!=0){
                        //睡一会
                        SystemClock.sleep(1000);
                        //删除成功，刷新
                        mMyAdapter.notifyItemRemoved(position);
                        //重新加载数据
                        int size = mAudioData.size()-1;
                        if(size==0){
                            mRecyClerViewPage.setVisibility(View.GONE);
                            noteEmptyPage.setVisibility(View.VISIBLE);
                        }
                        initData();
                        mMyAdapter.notifyDataSetChanged();
//                        initRecyclerView();
                        ToastUtil.show(UIUtils.getContext(),"删除删除");
                    }
                }
            });

            //设置条目的点击事件
            tv_title.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    //跳转前，先关闭已经打开的layout
//                    SwipeLayoutManager.getInstance().closeCurrentLayout();
//                    //睡一会
//                    SystemClock.sleep(1000);
                    //跳转到编辑页面，修改数据
                    Intent intent = new Intent(mActivity, EditAudioActivity.class);
                    intent.putExtra("id", mAudioData.get(position).id);
                    startActivity(intent);
                    mActivity.overridePendingTransition(R.anim.regst_next_in_anim, R.anim.regst_next_out_anim);
                    //销毁当前的Activity
                    mActivity.finish();
                }
            });
            //swipeLayout的滑动事件
//            sl_root.setOnSwipeStateChangeListener(new SwipeLayout.OnSwipeStateChangeListener() {
//                @Override
//                public void onOpen(Object tag) {
//                    ToastUtil.show(UIUtils.getContext(), "滑动打开");
//                }
//
//                @Override
//                public void onClose(Object tag) {
//                    ToastUtil.show(UIUtils.getContext(), "滑动关闭");
//                }
//            });
        }
    }

    private void readAudio(String cotent) {
        SpeechUtility.createUtility(mActivity, SpeechConstant.APPID + "=5661d0f2");
        //1.创建SpeechSynthesizer对象, 第二个参数：本地合成时传InitListener
        SpeechSynthesizer mTts= SpeechSynthesizer.createSynthesizer(UIUtils.getContext(), null);
        //2.合成参数设置
        mTts.setParameter(SpeechConstant.VOICE_NAME, "xiaoyan");//设置发音人
        mTts.setParameter(SpeechConstant.SPEED, "50");//设置语速
        mTts.setParameter(SpeechConstant.VOLUME, "80");//设置音量，范围0~100
        mTts.setParameter(SpeechConstant.ENGINE_TYPE, SpeechConstant.TYPE_CLOUD); //设置云端
        //设置合成音频保存位置（可自定义保存位置），保存在“./sdcard/iflytek.pcm”
        mTts.setParameter(SpeechConstant.TTS_AUDIO_PATH, "./sdcard/AudioCloud/audio.wav");
        //3.开始合成
        mTts.startSpeaking(cotent, mSynListener);

    }
    //合成监听器
    private SynthesizerListener mSynListener = new SynthesizerListener() {
        //会话结束回调接口，没有错误时，error为null
        public void onCompleted(SpeechError error) {
        }
        //缓冲进度回调
        //percent为缓冲进度0~100，beginPos为缓冲音频在文本中开始位置，endPos表示缓冲音频在文本中结束位置，info为附加信息。
        public void onBufferProgress(int percent, int beginPos, int endPos, String info) {
        }
        //开始播放
        public void onSpeakBegin() {
        }
        //暂停播放
        public void onSpeakPaused() {
        }
        //播放进度回调
        //percent为播放进度0~100,beginPos为播放音频在文本中开始位置，endPos表示播放音频在文本中结束位置.
        public void onSpeakProgress(int percent, int beginPos, int endPos) {
        }
        //恢复播放回调接口
        public void onSpeakResumed() {
        }
        //会话事件回调接口
        public void onEvent(int arg0, int arg1, int arg2, Bundle arg3) {
        }
    };

    //带显示框的
    private void StartAudio() {
        SpeechUtility.createUtility(mActivity, SpeechConstant.APPID + "=5661d0f2");

        //1.RecognizerDialog
        RecognizerDialog mDialog = new RecognizerDialog(mActivity, null);
        //2.accentlanguage
        mDialog.setParameter(SpeechConstant.LANGUAGE, "zh_cn");
        mDialog.setParameter(SpeechConstant.ACCENT, "mandarin");

        //3.
        mDialog.setListener(new RecognizerDialogListener() {
            @Override
            public void onResult(RecognizerResult recognizerResult, boolean b) {
                String result = recognizerResult.getResultString();
                //解析结果
                parseResult(result, b);
            }

            @Override
            public void onError(SpeechError arg0) {

            }
        });
        mDialog.show();
    }

    /**
     * 解析识别到的语音
     * @param result
     */
    private void parseResult(String result,boolean b) {
        Gson gson = new Gson();
        AudioInfo audioInfo = gson.fromJson(result, AudioInfo.class);

        StringBuilder sb = new StringBuilder();
        ArrayList<AudioInfo.WsBean> ws = audioInfo.ws;
        for(AudioInfo.WsBean wsBean:ws){
            String w = wsBean.cw.get(0).w;
            sb = sb.append(w);
        }
//        System.out.println("单句的打印结果是:"+sb.toString());
        mSb.append(sb.toString());
        if(b){

            //跳转到下一个Activity
            System.out.println("语音结果是:" + mSb.toString());
            Intent intent = new Intent(mActivity, EditAudioActivity.class);
            intent.putExtra("audioText", mSb.toString());
            startActivity(intent);
            mActivity.overridePendingTransition(R.anim.regst_next_in_anim, R.anim.regst_next_out_anim);
            //删除前，先关闭已经打开的layout
            SwipeLayoutManager.getInstance().closeCurrentLayout();
            SystemClock.sleep(1000);
            //跳转之后先将布局显示
            mRecyClerViewPage.setVisibility(View.VISIBLE);
            noteEmptyPage.setVisibility(View.GONE);
            //将msb清空
            mSb.delete(0, mSb.length());
//            System.out.println("清空后的结果是:" + mSb.toString());
            //销毁当前的Activity
            mActivity.finish();
        }
    }


    @Override
    public void initData() {
        //先将集合清空
        if(mAudioData!=null){
            mAudioData.clear();
        }
//        初始化数据,查找数据库,耗时操作，放到子线程中
        new Thread(new Runnable() {
            @Override
            public void run() {
                List<AudioText> result = new AudioDao(UIUtils.getContext()).query();
                //初始化RecyclerView
//                initRecyclerView();
                mAudioData = result;
                if(mAudioData.size()>0){
                    mRecyClerViewPage.setVisibility(View.VISIBLE);
                    noteEmptyPage.setVisibility(View.GONE);
                }else{
//                    mRecyClerViewPage.setVisibility(View.GONE);
//                    noteEmptyPage.setVisibility(View.VISIBLE);
                }
            }
        }).start();
        System.out.println("我被调用了");
    }
}