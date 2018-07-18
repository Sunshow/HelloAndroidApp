package com.example.helloapplication;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

/**
 * Created by sunshow.
 */
public class FirstService extends Service {

    private final String TAG = "FirstService";

    private Thread serviceThread;

    private IBinder binder = new FirstServiceBinder();

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.e(TAG, "onBind");
        return binder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.e(TAG, "onUnbind");
        return super.onUnbind(intent);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.e(TAG, "onStartCommand");

        serviceThread = new Thread(new Runnable() {
            @Override
            public void run() {
                synchronized (this) {
                    try {
                        wait(5000);
                    } catch (InterruptedException e) {
                        Log.e(TAG, "InterruptedException");
                        return;
                    }

                    Log.e(TAG, "Service thread completed");
                }
            }
        });

        serviceThread.start();

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onCreate() {
        Log.e(TAG, "onCreate");
        //super.onCreate();
        startForeground(1, createNotification());
    }

    @Override
    public void onDestroy() {
        Log.e(TAG, "onDestroy");

        if (serviceThread != null) {
            serviceThread.interrupt();
        }

        super.onDestroy();
    }

    public void helloFromService() {
        Log.e(TAG, "Hello from service");
    }

    private Notification createNotification() {
        Notification.Builder builder = new Notification.Builder(this);
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, MainActivity.class), 0);
        builder.setContentIntent(contentIntent);
        builder.setSmallIcon(R.mipmap.ic_launcher);
        builder.setTicker("Foreground Service Start");
        builder.setContentTitle("Foreground Service");
        builder.setContentText("Make this service run in the foreground.");
        return builder.build();
    }

    public class FirstServiceBinder extends Binder {
        public FirstService getService() {
            return FirstService.this;
        }

        public void helloFromBinder() {
            Log.e(TAG, "Hello from binder");
        }
    }
}
