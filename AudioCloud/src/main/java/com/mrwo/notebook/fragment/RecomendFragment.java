package com.mrwo.notebook.fragment;

import android.content.Intent;
import android.graphics.Color;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.format.DateUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.lidroid.xutils.BitmapUtils;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.mrwo.notebook.R;
import com.mrwo.notebook.activity.RecomDetailActivity;
import com.mrwo.notebook.bean.RecomInfo;
import com.mrwo.notebook.bean.RecomInfo.ReaderModuleEntity.ItemInfosEntity;
import com.mrwo.notebook.utils.BitmapHelper;
import com.mrwo.notebook.utils.CacheFileUtils;
import com.mrwo.notebook.utils.ConstantValue;
import com.mrwo.notebook.utils.GlobalConstants;
import com.mrwo.notebook.utils.SpUtil;
import com.mrwo.notebook.utils.ToastUtil;
import com.mrwo.notebook.utils.UIUtils;
import com.mrwo.notebook.widget.LoadmoreListView;
import com.mrwo.notebook.widget.MyListView;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.BlockingDeque;

/**
 * Created by Administrator on 2015/11/29.
 */
public class RecomendFragment extends BaseFragment {

    private MyListView mListView;
    private SwipeRefreshLayout mRefresh;

    private boolean isRequested = false;
    private boolean isLoadMore = false;

    private List<ItemInfosEntity> mItemInfos = new ArrayList<ItemInfosEntity>();
    private RecomInfo mRecomInfo;
    private MyAdapter myAdapter;
    private View loading_page;
    private View error_page;
    private View listview_page;
    private FrameLayout fl_root;

    @Override
    public View initView() {
        View view = UIUtils.inflate(R.layout.fragment_recom);
        fl_root = (FrameLayout) view.findViewById(R.id.fl_root);
        //加载三个布局
        loading_page = UIUtils.inflate(R.layout.loading_page);
        error_page = UIUtils.inflate(R.layout.error_page);
        listview_page = UIUtils.inflate(R.layout.listview_page);

        mRefresh = (SwipeRefreshLayout) listview_page.findViewById(R.id.refresh);
        mListView = (MyListView) listview_page.findViewById(R.id.listView);

        //首先加载所有的布局，默认显示状态
        fl_root.addView(loading_page);
        fl_root.addView(error_page);
        fl_root.addView(listview_page);
        error_page.setVisibility(View.GONE);
        listview_page.setVisibility(View.GONE);

        error_page.findViewById(R.id.tv_reload).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //重新加载
                getDateFromServer("");
                error_page.setVisibility(View.GONE);
                loading_page.setVisibility(View.VISIBLE);
            }
        });
        return view;
    }

    @Override
    public void initData() {
        //判断，如果请求过数据，再次请求，直接刷新listViw
        if(!isRequested){
            isRequested=true;
            //调用链接网络的方法
            getDateFromServer("");
        }else{
            //直接填充数据
            setListViewDate();
            //显示布局
            loading_page.setVisibility(View.GONE);
            listview_page.setVisibility(View.VISIBLE);
        }
    }

    private void getDateFromServer(final String nextPageMark) {

        System.out.println("请求地址为:"+GlobalConstants.SERVER_URL_NET+nextPageMark+GlobalConstants.SUFFIX);

        String url = GlobalConstants.SERVER_URL_NET+nextPageMark+GlobalConstants.SUFFIX;
        //先从本地获取缓存数据
        String dataLocal = CacheFileUtils.getDataFromLocal(nextPageMark + GlobalConstants.SUFFIX);
        if(dataLocal==null){
            //本地无缓存，请求网络
            HttpUtils httpUtils = new HttpUtils();
            httpUtils.send(HttpRequest.HttpMethod.GET, url,
                    new RequestCallBack<String>() {
                        @Override
                        public void onSuccess(ResponseInfo<String> responseInfo) {
                            String result = responseInfo.result;
                            System.out.println("解析结果:" + result);
                            parsonJson(result);
                            //隐藏布局
                            loading_page.setVisibility(View.GONE);
                            error_page.setVisibility(View.GONE);
                            listview_page.setVisibility(View.VISIBLE);
                            //写缓存
                            CacheFileUtils.writeToLocal(result,nextPageMark+GlobalConstants.SUFFIX);
                        }

                        @Override
                        public void onFailure(HttpException e, String s) {
                            //请求失败
                            e.printStackTrace();
//                            ToastUtil.show(UIUtils.getContext(), s);
                            //隐藏布局
                            loading_page.setVisibility(View.GONE);
                            listview_page.setVisibility(View.GONE);
                            error_page.setVisibility(View.VISIBLE);
                            System.out.println("请求失败");
                        }
                    });
        }else {
            parsonJson(dataLocal);
            //隐藏布局
            loading_page.setVisibility(View.GONE);
            error_page.setVisibility(View.GONE);
            listview_page.setVisibility(View.VISIBLE);
        }
    }

    private void parsonJson(String result) {
        //用Gson解析json数据
        Gson gson = new Gson();
        mRecomInfo = gson.fromJson(result, RecomInfo.class);

        //清空集合,如果是下拉加载更多，不需要清空集合
        if(!isLoadMore){
            mItemInfos.clear();
        }

        //获取集合
        ArrayList<ItemInfosEntity> itemInfos = (ArrayList<ItemInfosEntity>) mRecomInfo.getReaderModule().getItemInfos();
        //将获得的集合追加到总的集合中
        mItemInfos.addAll(itemInfos);

        System.out.println("mItemInfos的长度:" + mItemInfos.size());

        //不过首先判断是否是加载更多，不用每次都要从新填充数据适配器，解决刷新过后回到ListView顶部的问题，注意书写代码的逻辑顺序
        if(!isLoadMore){
            //填充适配器的数据
            setListViewDate();
        }else {
            //刷新数据
            myAdapter.notifyDataSetChanged();
            //移除加载更多的布局
            mListView.loadMoreComplete();
        }
    }

    private void setListViewDate() {
        //首先绑定swipeRefreshLayout
        mRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mRefresh.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        //刷新的具体操作
                        ToastUtil.show(UIUtils.getContext(),"刷新数据");
//                        //下拉刷新应该重新请求，清空集合
//                        mItemInfos.clear();
                        //清空缓存
                        deleteFilesByDirectory(UIUtils.getContext().getCacheDir());
                        //将是否加载更多置为false，从新填充数据适配器
                        isLoadMore = false;
                        getDateFromServer("");
                        mRefresh.setRefreshing(false);
                    }
                },2000);
            }
        });
        //设置刷新图标的样式
        mRefresh.setColorSchemeResources(R.color.my_blue,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
        mRefresh.setProgressBackgroundColor(R.color.my_blue2);

        //给list设置数据适配器
        myAdapter = new MyAdapter();
        mListView.setAdapter(myAdapter);

        //给条目设置点击事件
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //设置点击条目为已读
                String read = SpUtil.getString(UIUtils.getContext(), ConstantValue.IS_READ, "");
                String itemDes = mItemInfos.get(position).getItem().getId() + ",";
                if (!read.contains(itemDes)) {
                    //不包含，则设置为已读
                    read = read + itemDes;
                    SpUtil.setString(UIUtils.getContext(), ConstantValue.IS_READ, read);
                    //设置当前条目的颜色为灰色
                    TextView tv_des = (TextView) view.findViewById(R.id.tv_des);
                    tv_des.setTextColor(Color.GRAY);
                }

                //跳转到详情页
                Intent intent = new Intent(mActivity, RecomDetailActivity.class);
                intent.putExtra("recomUrl", mItemInfos.get(position).getItem().getName());
                mActivity.startActivity(intent);
            }
        });

        //监听listView，载更多
        mListView.setLoadMore(new MyListView.OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                System.out.println("正在加载更多");
                //再次请求数据,不过先判断是否还有下一页数据
                long nextPageMark = mRecomInfo.getReaderModule().getNextPageMark();
                if(nextPageMark!=0){
                    isLoadMore = true;
                    //还有下一页数据
                    getDateFromServer("pageMark="+nextPageMark+"&");
//                    //刷新数据
//                    myAdapter.notifyDataSetChanged();
//                    //移除加载更多的布局
//                    mListView.loadMoreComplete();
                }else{
                    //没有更多数据了
                    ToastUtil.show(UIUtils.getContext(),"没有更多数据了");
                    //移除加载更多的布局
                    mListView.loadMoreComplete();
                }
            }
        });

    }

    class MyAdapter extends BaseAdapter{

        @Override
        public int getCount() {
            return mItemInfos.size();
        }
        @Override
        public ItemInfosEntity getItem(int position) {
            return mItemInfos.get(position);
        }
        @Override
        public long getItemId(int position) {
            return position;
        }
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder = null;
            if(convertView==null){
                holder = new ViewHolder();
                convertView = UIUtils.inflate(R.layout.recomend_item);
                holder.iv_image = (ImageView) convertView.findViewById(R.id.iv_image);
                holder.tv_des = (TextView) convertView.findViewById(R.id.tv_des);
                holder.tv_time = (TextView) convertView.findViewById(R.id.tv_time);
                holder.tv_like = (TextView) convertView.findViewById(R.id.tv_like);
                convertView.setTag(holder);
            }else {
                holder = (ViewHolder) convertView.getTag();
            }
            ItemInfosEntity itemInfo = mItemInfos.get(position);
            //给各个控件添加数据
            BitmapUtils bitmapUtils = BitmapHelper.getBitmapUtils();
            bitmapUtils.display(holder.iv_image, itemInfo.getItem().getThumbnailUrl());

            //判断是否为已读，从而设置字体的颜色
            holder.tv_des.setText(itemInfo.getItem().getMetadata());
            String readed = SpUtil.getString(UIUtils.getContext(), ConstantValue.IS_READ, "");
            if (readed.contains(itemInfo.getItem().getId())){
                holder.tv_des.setTextColor(Color.GRAY);
            }else{
                //此处一定要再次设置字体的颜色，否则listView的复用导致下面字体的颜色也会变成灰色。
                holder.tv_des.setTextColor(Color.BLACK);
            }

            //时间，需要格式化
            holder.tv_time.setText(getDateStr(itemInfo.getDisplayTime()));

            holder.tv_like.setText(""+(itemInfo.getNumUsersLikeIt()+itemInfo.getNumUsersWithoutNameLikeIt()));

            return convertView;
        }

    }
        static class ViewHolder{
            ImageView iv_image;
            TextView tv_des;
            TextView tv_time;
            TextView tv_like;
        }

    //清除本地缓存的方法
    private static void deleteFilesByDirectory(File directory) {
        if (directory != null && directory.exists() && directory.isDirectory()) {
            for (File item : directory.listFiles()) {
                item.delete();
            }
        }
    }

    private String getDateStr(String date){
        String date1 = splite(date);
        date1 = new java.text.SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(new java.util.Date(Long.valueOf(date1)*1000));
        System.out.println("................"+date1);
        return date1;
    }
    //将时间切割
    private String splite(String date){
        String[] split = date.split(":");
        return split[0];
    }

}
