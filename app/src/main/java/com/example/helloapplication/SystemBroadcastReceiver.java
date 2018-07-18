package com.example.helloapplication;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Created by sunshow.
 */
public class SystemBroadcastReceiver extends BroadcastReceiver {

    private final String TAG = "SystemReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.e(TAG, "SystemBroadcast received");
    }
}
