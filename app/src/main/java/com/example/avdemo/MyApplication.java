package com.example.avdemo;

import android.app.Application;

import com.alibaba.android.arouter.launcher.ARouter;

/**
 * desc: Application <br/>
 * time: 2019/10/24 14:18 <br/>
 * author: 吕昊臻 <br/>
 * since V 1.0<br/>
 */

public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        if (BuildConfig.DEBUG) {
            ARouter.openLog();
            ARouter.openDebug();
        }
        ARouter.init(this);
    }
}
