package com.example.helloapplication;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

/**
 * Created by sunshow.
 */
public class OrderedBroadcastReceiver extends BroadcastReceiver {

    private final String TAG = "OrderedReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        //Bundle bundle = this.getResultExtras(true);
        Bundle bundle = intent.getExtras();

        String name = bundle.getString("name");

        Log.e(TAG, String.format("OrderedBroadcast received: name=%s", name));
    }
}
