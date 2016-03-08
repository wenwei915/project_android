package com.mrwo.notebook.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ListView;

import com.mrwo.notebook.R;
import com.mrwo.notebook.utils.ToastUtil;
import com.mrwo.notebook.utils.UIUtils;

/**
 * Created by Administrator on 2015/12/1.
 */
public class MyListView extends ListView{

    private boolean isLoadMore;// 标记是否正在加载更多
    private View loadMoreview;
    private int measureHeight;

    public MyListView(Context context) {
        super(context);
        initFootView();
    }

    public MyListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initFootView();
    }

    public MyListView(Context context, AttributeSet attrs) {

        super(context, attrs);
        initFootView();
    }

    private void initFootView() {
        loadMoreview = UIUtils.inflate(R.layout.refresh_loadmore);
        loadMoreview.measure(0, 0);

        measureHeight = loadMoreview.getMeasuredHeight();
        loadMoreview.setPadding(0, -measureHeight, 0, 0);

        //将布局添加到listView中
        addFooterView(loadMoreview);

        //监听滑动事件
        setOnScrollListener(new OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if (scrollState == SCROLL_STATE_IDLE) {// 空闲状态
                    int lastVisiblePosition = getLastVisiblePosition();

                    if (lastVisiblePosition == getCount() - 1 && !isLoadMore) {// 当前显示的是最后一个item并且没有正在加载更多
                        // 到底了
                        isLoadMore = true;

                        loadMoreview.setPadding(0, 0, 0, 0);// 显示加载更多的布局

                        setSelection(getCount() - 1);// 将listview显示在最后一个item上,
                        // 从而加载更多会直接展示出来, 无需手动滑动

                        //通知主界面加载下一页数据
                        if (mListener != null) {
                            mListener.onLoadMore();
                        }
                    }
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
            }
        });

        //给脚布局添加点击事件
        loadMoreview.findViewById(R.id.ll_root).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                ToastUtil.show(UIUtils.getContext(),"加载更多");
            }
        });
    }

    //加载更多结束后执行的方法
    public void loadMoreComplete(){
        if(isLoadMore){
            //将布局隐藏
            loadMoreview.setPadding(0,measureHeight,0,0);
            isLoadMore = false;
        }
    }

    //定义成员变量，接收监听对象
    private OnLoadMoreListener mListener;

    //暴露接口，设置监听
    public void setLoadMore(OnLoadMoreListener listener){
        mListener = listener;
    }

    //定义huid回调接口，用于加载更多
    public interface OnLoadMoreListener{
        //用于刷新的方法
        public void onLoadMore();
    }
}
