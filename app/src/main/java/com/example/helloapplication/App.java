package com.example.helloapplication;

import android.app.Application;

import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechUtility;

/**
 * Created by sunshow.
 */
public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        SpeechUtility.createUtility(this, SpeechConstant.APPID + "=5b6908cc");
    }

}
