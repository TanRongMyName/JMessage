package com.coffe.shentao.coffeechat.application;

import android.app.Application;
import android.util.Log;

import com.coffe.shentao.coffeechat.GlobalEventListener;

import cn.jpush.im.android.api.JMessageClient;
/**
 * Created by ${chenyn} on 16/3/22.
 *
 * @desc :
 */
public class IMDebugApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i("IMDebugApplication", "init");
        JMessageClient.setDebugMode(true);
        JMessageClient.init(getApplicationContext(), true);
        //注册全局事件监听类
        JMessageClient.registerEventReceiver(new GlobalEventListener(getApplicationContext()));
    }
}

