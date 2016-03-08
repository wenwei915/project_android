package com.mrwo.notebook.fragment;

import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;
import com.mrwo.notebook.R;
import com.mrwo.notebook.activity.MainActivity;
import com.mrwo.notebook.bean.User;
import com.mrwo.notebook.utils.ConstantValue;
import com.mrwo.notebook.utils.GlobalConstants;
import com.mrwo.notebook.utils.ParsonJsonToUser;
import com.mrwo.notebook.utils.SpUtil;
import com.mrwo.notebook.utils.StringUtils;
import com.mrwo.notebook.utils.ToastUtil;
import com.mrwo.notebook.utils.UIUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;

/**
 * Created by Administrator on 2015/12/2.
 */
public class LoginFragment extends BaseFragment implements OnClickListener {

    private MainActivity mActivity;

    private EditText et_email;
    private EditText et_psd;

    //返回的字符串
    private String mResult;
    private String email;

    @Override
    public View initView() {
        mActivity = (MainActivity) getActivity();
        //加载布局
        View view = UIUtils.inflate(R.layout.fragment_login);
        ImageView iv_close = (ImageView) view.findViewById(R.id.iv_close);
        TextView tv_rgs = (TextView) view.findViewById(R.id.tv_rgs);
        view.findViewById(R.id.bt_login).setOnClickListener(this);
        iv_close.setOnClickListener(this);
        tv_rgs.setOnClickListener(this);

        et_email = (EditText) view.findViewById(R.id.et_email);
        //设置已经保存的email
        et_email.setText(SpUtil.getString(UIUtils.getContext(), ConstantValue.REGIST_EMAIL,""));
        et_psd = (EditText) view.findViewById(R.id.et_psd);

        return view;
    }

    @Override
    public void onClick(View v) {
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        switch (v.getId()){
            case R.id.iv_close:
                //关闭自己
                transaction.setCustomAnimations(R.anim.next_in_anim,R.anim.next_out_anim);
                transaction.remove(this).commit();
                break;
            case R.id.tv_rgs:
                //跳转到注册页面
                //setCustomAnimations (int enter, int exit, int popEnter, int popExit)
                transaction.setCustomAnimations(R.anim.regst_next_in_anim, R.anim.regst_next_out_anim);
                transaction.replace(android.R.id.content, new RegisterFragment()).commit();
                break;
            case R.id.bt_login:
                //点击登录
                login();
                break;
        }
    }

    private void login() {
        //获取用户输入的数据
        email = et_email.getText().toString().trim();
        String psd = et_psd.getText().toString().trim();
        //作判断，并给出提示
        if (StringUtils.isEmpty(email)){
            ToastUtil.show(UIUtils.getContext(),"请输入邮箱");
        }else if(!StringUtils.isEmpty(email)&& !email.matches("\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*")){
            ToastUtil.show(UIUtils.getContext(),"邮箱格式不正确");
        }else if(!StringUtils.isEmpty(email)&& StringUtils.isEmpty(psd)){
            ToastUtil.show(UIUtils.getContext(),"请输入密码");
        }else{
            //请求网络
            RequestParams params = new RequestParams();
            params.addBodyParameter("email", email);
            params.addBodyParameter("passWord", psd);
            HttpUtils http = new HttpUtils();
            http.send(HttpRequest.HttpMethod.POST,
                    GlobalConstants.SERVER_URL_LOGIN,
                    params,
                    new RequestCallBack<String>() {
                        @Override
                        public void onSuccess(ResponseInfo<String> responseInfo) {
                            String result = responseInfo.result;
                            //解析返回的结果作
                            parsonResult(result);
                        }
                        @Override
                        public void onFailure(HttpException error, String msg) {
                            ToastUtil.show(UIUtils.getContext(),"服务器忙");
                            System.out.println("请求失败" + msg);
                        }
                    });
        }
    }

    private void parsonResult(String result) {
        mResult = result;
        if (result.equals("error")){
            ToastUtil.show(UIUtils.getContext(),"邮箱或密码错误");
        }else{
            //解析返回的Json字符串
            System.out.println("返回的Json串:"+result);
            User user = ParsonJsonToUser.parson(result);
            //将注册的邮箱名写入sp中
            SpUtil.setString(UIUtils.getContext(), ConstantValue.LOGIN_USER, result);
            //设置主页的状态
            mActivity.setUser(user.nick,user.describe);
            //关闭自己
            getFragmentManager().beginTransaction().setCustomAnimations(R.anim.next_in_anim, R.anim.next_out_anim);
            getFragmentManager().beginTransaction().remove(this).commit();
        }

        System.out.println("mResult.........." + mResult);
    }

    @Override
    public void initData() {
    }
}

