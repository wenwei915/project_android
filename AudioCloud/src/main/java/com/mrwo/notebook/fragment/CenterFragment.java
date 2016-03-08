package com.mrwo.notebook.fragment;

import android.content.Intent;
import android.hardware.usb.UsbInterface;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;

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
import com.mrwo.notebook.bean.User;
import com.mrwo.notebook.utils.ConstantValue;
import com.mrwo.notebook.utils.GlobalConstants;
import com.mrwo.notebook.utils.ParsonJsonToUser;
import com.mrwo.notebook.utils.SpUtil;
import com.mrwo.notebook.utils.StringUtils;
import com.mrwo.notebook.utils.ToastUtil;
import com.mrwo.notebook.utils.UIUtils;
import com.mrwo.notebook.widget.RoundImageView;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Administrator on 2015/12/2.
 */
public class CenterFragment extends BaseFragment{

    @ViewInject(R.id.tv_email)
    private TextView tv_email;

    @ViewInject(R.id.ev_nick)
    private EditText et_nick;

    @ViewInject(R.id.rb_male)
    private RadioButton rb_male;

    @ViewInject(R.id.rb_female)
    private RadioButton rb_female;

    @ViewInject(R.id.et_des)
    private EditText et_des;

    @ViewInject(R.id.bt_logout)
    private Button bt_logout;

    @ViewInject(R.id.id_usericon)
    private RoundImageView usericon;

    @Override
    public View initView() {
        View view = UIUtils.inflate(R.layout.fragment_center);
        //注解
        ViewUtils.inject(this, view);
        //回显数据
        initData();

        bt_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                ToastUtil.show(UIUtils.getContext(), "退出登录");
                //从sp中删除login_user节点
                SpUtil.remove(UIUtils.getContext(),ConstantValue.LOGIN_USER);
                getActivity().startActivity(new Intent(getActivity(), MainActivity.class));
            }
        });

        //从本地选择图片设置头像
        usericon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //从本地选择图片
                ToastUtil.show(UIUtils.getContext(), "从本地选择图片");
            }
        });

        return view;
    }

    private String email;
    private String nick;
    private String sex;
    private String describe;
    //提交用户修改的信息到服务器
    public void submitInfo() {
        email = tv_email.getText().toString();
        nick = et_nick.getText().toString();
        sex = null;
        if(rb_male.isChecked()){
            sex = "男";
        }else if(rb_female.isChecked()){
            sex = "女";
        }
        describe = et_des.getText().toString();
        //请求网络，提交数据
        RequestParams params = new RequestParams();
        params.addBodyParameter("nick", nick);
        params.addBodyParameter("sex", sex);
        params.addBodyParameter("describe", describe);
        params.addBodyParameter("email", email);
        HttpUtils http = new HttpUtils();
        http.send(HttpRequest.HttpMethod.POST,
                GlobalConstants.SERVER_URL_COMPLETE,
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
                        System.out.println("请求失败" + msg);
                    }
                });
    }

    private void parsonResult(String result) {
        if("ok".equals(result)){
            //提示更新成功，并且返回
            ToastUtil.show(UIUtils.getContext(), "更新成功");
            //将请求的参数封装成json，覆盖Login
            String loginUser = makeJaon(email,nick,sex,describe);
            //写到sp中
            SpUtil.setString(UIUtils.getContext(),ConstantValue.LOGIN_USER,loginUser);
            getActivity().startActivity(new Intent(getActivity(), MainActivity.class));
        }

    }

    private String makeJaon(String email, String nick, String sex, String describe) {
        JSONObject object = new JSONObject();
        try {
            object.put("email", email);
            object.put("nick", nick);
            object.put("describe", describe);
            object.put("sex", sex);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return object.toString();
    }

    @Override
    public void initData() {
        User user = ParsonJsonToUser.parson(SpUtil.getString(UIUtils.getContext(), ConstantValue.LOGIN_USER,""));
        tv_email.setText(user.email);
        et_nick.setText(user.nick);
        if(!StringUtils.isEmpty(user.sex)){
            if(user.sex.equals("男")){
                rb_male.setChecked(true);
            }else if (user.sex.equals("女")){
                rb_female.setChecked(true);
            }
        }
        et_des.setText(user.describe);
    }
}
