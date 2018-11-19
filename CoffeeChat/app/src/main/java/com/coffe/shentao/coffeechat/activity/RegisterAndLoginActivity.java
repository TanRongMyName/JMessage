package com.coffe.shentao.coffeechat.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;

import com.coffe.shentao.coffeechat.R;
import com.coffe.shentao.coffeechat.activity.setting.RegisterActivity;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Locale;

import cn.jmessage.support.google.gson.Gson;
import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.android.api.callback.RequestCallback;
import cn.jpush.im.android.api.model.DeviceInfo;
import cn.jpush.im.android.api.model.UserInfo;
import cn.jpush.im.api.BasicCallback;



/**
 * Created by ${chenyn} on 16/3/23.
 *
 * @desc : 注册和登陆界面
 */
public class RegisterAndLoginActivity extends Activity {
    public static final String KEY_USERNAME = "username";
    public static final String KEY_PWD = "pwd";
    public static final int REQ_CODE_FOR_REGISTER = 1;
    public EditText mEd_userName;
    public EditText mEd_password;
    private Button mBt_login;
    private Button mBt_login_with_infos;
    private Button mBt_gotoRegister;
    private ProgressDialog mProgressDialog = null;
    private CheckBox mCb_isTest;
    private boolean isTestVisibility = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        initData();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQ_CODE_FOR_REGISTER && RESULT_OK == resultCode && data != null) {
            mEd_userName.setText(data.getStringExtra(KEY_USERNAME));
            mEd_password.setText(data.getStringExtra(KEY_PWD));
        }
    }

    /**
     * #################    应用入口,登陆或者是注册    #################
     */
    private void initData() {
        /**=================     获取个人信息不是null，说明已经登陆，无需再次登陆，则直接进入type界面    =================*/
        UserInfo myInfo = JMessageClient.getMyInfo();
        if (myInfo != null) {
            Intent intent = new Intent(RegisterAndLoginActivity.this, TypeActivity.class);
            startActivity(intent);
            finish();
        }
        /**=================     调用注册接口    =================*/
        mBt_gotoRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(getApplicationContext(), RegisterActivity.class);
                startActivityForResult(intent, REQ_CODE_FOR_REGISTER);
            }
        });
        mBt_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mProgressDialog = ProgressDialog.show(RegisterAndLoginActivity.this, "提示：", "正在加载中。。。");
                mProgressDialog.setCanceledOnTouchOutside(true);
                String userName = mEd_userName.getText().toString();
                String password = mEd_password.getText().toString();
                /**=================     调用SDk登陆接口    =================*/
                JMessageClient.login(userName, password, new BasicCallback() {
                    @Override
                    public void gotResult(int responseCode, String LoginDesc) {
                        if (responseCode == 0) {
                            mProgressDialog.dismiss();
                            Toast.makeText(getApplicationContext(), "登录成功", Toast.LENGTH_SHORT).show();
                            Log.i("MainActivity", "JMessageClient.login" + ", responseCode = " + responseCode + " ; LoginDesc = " + LoginDesc);
                            Intent intent = new Intent();
                            intent.setClass(getApplicationContext(), TypeActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            mProgressDialog.dismiss();
                            Toast.makeText(getApplicationContext(), "登录失败", Toast.LENGTH_SHORT).show();
                            Log.i("MainActivity", "JMessageClient.login" + ", responseCode = " + responseCode + " ; LoginDesc = " + LoginDesc);
                        }
                    }
                });
            }
        });

        mBt_login_with_infos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mProgressDialog = ProgressDialog.show(RegisterAndLoginActivity.this, "提示：", "正在加载中。。。");
                mProgressDialog.setCanceledOnTouchOutside(true);
                String userName = mEd_userName.getText().toString();
                String password = mEd_password.getText().toString();
                /**=================     调用SDk登陆接口    =================*/
                JMessageClient.login(userName, password, new RequestCallback<List<DeviceInfo>>() {
                    @Override
                    public void gotResult(int responseCode, String responseMessage, List<DeviceInfo> result) {
                        if (responseCode == 0) {
                            mProgressDialog.dismiss();
                            Toast.makeText(getApplicationContext(), "登陆成功", Toast.LENGTH_SHORT).show();
                            Log.i("MainActivity", "JMessageClient.login" + ", responseCode = " + responseCode + " ; LoginDesc = " + responseMessage);
                            Intent intent = new Intent(getApplicationContext(), TypeActivity.class);
                            Gson gson = new Gson();
                            intent.putExtra("deviceInfos", gson.toJson(result));
                            startActivity(intent);
                            finish();
                        } else {
                            mProgressDialog.dismiss();
                            Toast.makeText(getApplicationContext(), "登陆失败", Toast.LENGTH_SHORT).show();
                            Log.i("MainActivity", "JMessageClient.login" + ", responseCode = " + responseCode + " ; LoginDesc = " + responseMessage);
                        }
                    }
                });
            }
        });

        //供jmessage sdk测试使用，开发者无需关心。
        mCb_isTest.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    swapEnvironment(RegisterAndLoginActivity.this.getApplicationContext(), true);
                    buttonView.setText("测试环境");
                } else {
                    buttonView.setText("生产环境");
                    swapEnvironment(RegisterAndLoginActivity.this.getApplicationContext(), false);
                }
            }
        });
    }

    private void initView() {
        setContentView(R.layout.activity_login);
        mEd_userName = (EditText) findViewById(R.id.ed_login_username);
        mEd_password = (EditText) findViewById(R.id.ed_login_password);
        mBt_login = (Button) findViewById(R.id.bt_login);
        mBt_login_with_infos = (Button) findViewById(R.id.bt_login_with_infos);
        mBt_gotoRegister = (Button) findViewById(R.id.bt_goto_regester);
        mCb_isTest = (CheckBox) findViewById(R.id.cb_is_test);
        if (!isTestVisibility) {
            mCb_isTest.setVisibility(View.GONE);
        } else {
            //供jmessage sdk测试使用，开发者无需关心。
            Boolean isTestEvn = invokeIsTestEvn();
            if (isTestEvn) {
                mCb_isTest.setText("测试环境");
            } else {
                mCb_isTest.setText("生产环境");
            }
            mCb_isTest.setChecked(isTestEvn);
        }
    }

    public static String getAppKey(Context context) {
        Bundle metaData = null;
        String appkey = null;
        try {
            ApplicationInfo ai = context.getPackageManager().getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);
            if (null != ai) {
                metaData = ai.metaData;
            } else {
                return null;
            }
        } catch (PackageManager.NameNotFoundException e) {
            return null;
        }
        if (null != metaData) {
            appkey = metaData.getString("JPUSH_APPKEY");
            if (TextUtils.isEmpty(appkey)) {
                return null;
            } else if (appkey.length() != 24) {
                return null;
            }
            appkey = appkey.toLowerCase(Locale.getDefault());
        }
        return appkey;
    }

    public static Boolean invokeIsTestEvn() {
        try {
            Class cls = Class.forName("cn.jpush.im.android.api.EnvironmentManager");
            Method method = cls.getDeclaredMethod("isTestEnvironment");
            Object result = method.invoke(null);
            return (Boolean) result;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static void swapEnvironment(Context context, boolean isTest) {
        try {
            Class cls = Class.forName("cn.jpush.im.android.api.EnvironmentManager");
            Method method = cls.getDeclaredMethod("swapEnvironment", Context.class, Boolean.class);
            method.invoke(null, context, isTest);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
