package com.mrwo.notebook.fragment;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.mrwo.notebook.R;
import com.mrwo.notebook.activity.MainActivity;
import com.mrwo.notebook.utils.ConstantValue;
import com.mrwo.notebook.utils.GlobalConstants;
import com.mrwo.notebook.utils.SpUtil;
import com.mrwo.notebook.utils.StringUtils;
import com.mrwo.notebook.utils.ToastUtil;
import com.mrwo.notebook.utils.UIUtils;

/**
 * Created by Administrator on 2015/12/2.
 */
public class RegisterFragment extends BaseFragment implements View.OnClickListener {

    @ViewInject(R.id.et_email)
    private EditText et_email;

    @ViewInject(R.id.et_nick)
    private EditText et_nick;

    @ViewInject(R.id.et_psd)
    private EditText et_psd;

    @ViewInject(R.id.et_repsd)
    private EditText et_repsd;

    private String email;
    private String psd;


    @Override
    public View initView() {

        View view = UIUtils.inflate(R.layout.fragment_register);
        ImageView iv_back = (ImageView) view.findViewById(R.id.iv_back);
        view.findViewById(R.id.bt_regst).setOnClickListener(this);
        iv_back.setOnClickListener(this);
        //使用注解
        ViewUtils.inject(this,view);
        return view;
    }

    @Override
    public void onClick(View v) {
        FragmentTransaction Transaction = getFragmentManager().beginTransaction();
        switch (v.getId()){
            case R.id.iv_back:
                //返回登录界面
                Transaction.setCustomAnimations(R.anim.regst_prev_in_anim,R.anim.regst_prev_out_anim);
                Transaction.replace(android.R.id.content, new LoginFragment()).commit();
                break;
            case R.id.bt_regst:
                //注册
                regist();
                break;
        }
    }

    private void regist() {
        //获取用户输入的数据
        email = et_email.getText().toString().trim();
        String nick = et_nick.getText().toString().trim();
        psd = et_psd.getText().toString().trim();
        String repsd = et_repsd.getText().toString().trim();
        //作判断，并给出提示
        if (StringUtils.isEmpty(email)){
            ToastUtil.show(UIUtils.getContext(),"请输入邮箱");
        }else if(!StringUtils.isEmpty(email)&& !email.matches("\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*")){
            ToastUtil.show(UIUtils.getContext(),"邮箱格式不正确");
        }else if (!StringUtils.isEmpty(email)&& StringUtils.isEmpty(nick)){
            ToastUtil.show(UIUtils.getContext(),"请输入昵称");
        }else if(!StringUtils.isEmpty(email)&& !StringUtils.isEmpty(nick)&& StringUtils.isEmpty(psd)){
            ToastUtil.show(UIUtils.getContext(),"请输入密码");
        }else if (!psd.equals(repsd)){
            ToastUtil.show(UIUtils.getContext(),"两次密码不相同");
        }else{
            //请求网络
            RequestParams params = new RequestParams();
            params.addBodyParameter("email", email);
            params.addBodyParameter("nick", nick);
            params.addBodyParameter("passWord", psd);
            HttpUtils http = new HttpUtils();
            http.send(HttpRequest.HttpMethod.POST,
                    GlobalConstants.SERVER_URL_REGIST,
                    params,
                    new RequestCallBack<String>() {
                        @Override
                        public void onSuccess(ResponseInfo<String> responseInfo) {
                            String result = responseInfo.result;
                            //解析返回的结果
                            parsonResult(result);
                            System.out.println("请求结果为:" + result);
                        }

                        @Override
                        public void onFailure(HttpException error, String msg) {
                            System.out.println("请求失败" + msg);
                        }
                    });
        }
    }

    private void parsonResult(String result) {
        switch (result){
            case "ok":
                //将注册的邮箱名写入sp中
                SpUtil.setString(UIUtils.getContext(), ConstantValue.REGIST_EMAIL,email);
                //返回登录界面
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.setCustomAnimations(R.anim.regst_prev_in_anim, R.anim.regst_prev_out_anim);
                transaction.replace(android.R.id.content, new LoginFragment(),"loginFragment").commit();
                break;
            case "exist":
                ToastUtil.show(UIUtils.getContext(), "该邮箱已被注册");
                break;
            case "error":
                ToastUtil.show(UIUtils.getContext(),"服务器忙");
                break;
        }
    }

    @Override
    public void initData() {

    }

}