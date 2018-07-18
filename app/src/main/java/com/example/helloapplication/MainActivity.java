package com.example.helloapplication;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    private final String TAG = "MainActivity";

    public static final int REQUEST_CODE_101 = 101;

    private ServiceConnection serviceConnection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button button = findViewById(R.id.btn_test);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //testTel();
                //testLifecycle();
                //testSecondary();
                //testSecondaryForResult();
                //testStartFirstService();
                //testStopFirstService();
                //testBindFirstService();
                //testSendBroadcast(false);
                testDownloadAsyncTask();
            }
        });

        serviceConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                FirstService.FirstServiceBinder binder = (FirstService.FirstServiceBinder) service;

                binder.getService().helloFromService();

                binder.helloFromBinder();
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
            }
        };
    }

    @Override
    protected void onDestroy() {
        if (serviceConnection != null) {
            unbindService(serviceConnection);
        }
        super.onDestroy();
    }

    private void testTel() {
        Intent intent = new Intent(Intent.ACTION_DIAL);
        Uri data = Uri.parse("tel:" + "10086");
        intent.setData(data);
        startActivity(intent);
    }

    private void testLifecycle() {
        startActivity(new Intent(this, LifecycleActivity.class));
    }

    private void testSecondary() {
        startActivity(new Intent(MainActivity.this, SecondaryActivity.class));
    }

    private void testSecondaryForResult() {
        Intent intent = new Intent(MainActivity.this, SecondaryActivity.class);
        intent.putExtra("name", "foobar");
        intent.putExtra("age", 18);
        /*
        Bundle bundle = new Bundle();
        bundle.putSerializable("object", xxx);
        intent.putExtras(bundle);
        */
        startActivityForResult(intent, REQUEST_CODE_101);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_101 && resultCode == SecondaryActivity.RESULT_CODE_201) {
            Bundle bundle = data.getExtras();
            String foo = bundle.getString("foo");
            Log.e(TAG, foo);
        }
    }

    private void testStartFirstService() {
        Intent startIntent = new Intent(this, FirstService.class);
        startService(startIntent);
    }

    private void testStopFirstService() {
        Intent stopIntent = new Intent(this, FirstService.class);
        stopService(stopIntent);
    }

    private void testBindFirstService() {
        Intent bindIntent = new Intent(this, FirstService.class);
        bindService(bindIntent, serviceConnection, Context.BIND_AUTO_CREATE);
    }

    private void testSendBroadcast(boolean ordered) {
        Intent intent = new Intent("CustomBroadcast");

        Bundle bundle = new Bundle();
        bundle.putString("name", "foobar");

        intent.putExtras(bundle);

        if (ordered) {
            sendOrderedBroadcast(intent, null);
        } else {
            sendBroadcast(intent);
        }
    }

    private void testDownloadAsyncTask() {
        DownloadAsyncTask asyncTask = new DownloadAsyncTask();
        asyncTask.execute("hi");
        // 一个任务run两次会怎样？
        //asyncTask.execute("guys");

        DownloadAsyncTask asyncTask1 = new DownloadAsyncTask();
        asyncTask1.execute("guys");
        //asyncTask1.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, "guys");

        Executor executor = new ThreadPoolExecutor(10,50,10,
                TimeUnit.SECONDS,new LinkedBlockingDeque<Runnable>(100));
        //asyncTask1.executeOnExecutor(executor, "guys");
    }
}
