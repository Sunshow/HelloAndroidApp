package com.example.helloapplication;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.lang.ref.WeakReference;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    public static final int REQUEST_CODE_101 = 101;

    private ServiceConnection serviceConnection;

    private static final int EDIT_OK = 301;

    Button button;

    EditText text;

    private Handler mHandler;

    private Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            mHandler.sendEmptyMessage(EDIT_OK);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        button = findViewById(R.id.btn_test);
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
                //testDownloadAsyncTask();
                //testUpdateUI_1();
                //testUpdateUI_2();
            }
        });

        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if (EDIT_OK == msg.what) {
                    Log.d(TAG, "Input completed" );
                }

            }
        };

        //mHandler = new TextInputCompletedHandler(button);

        text = findViewById(R.id.et_text);
        text.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mHandler.removeCallbacks(mRunnable);
                //800毫秒没有输入认为输入完毕
                mHandler.postDelayed(mRunnable, 800);
            }

            @Override
            public void afterTextChanged(Editable s) {

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

    private void testUpdateUI_1() {
        MyAsyncTask task = new MyAsyncTask();
        task.execute("hi");
    }

    private void testUpdateUI_2() {
        DownloadAsyncTask asyncTask = new DownloadAsyncTask(new DownloadAsyncTask.DownloadTaskListener() {
            @Override
            public void onCompleted(String s) {
                button.setText("Completed");
            }
        });
        asyncTask.execute("hi");
    }

    class MyAsyncTask extends AsyncTask<String, Integer, String> {
        @Override
        protected String doInBackground(String... params) {
            Log.e(TAG, "download completed");
            return params[0];
        }

        @Override
        protected void onPostExecute(String s) {
            button.setText("Completed");
        }
    }

    static class TextInputCompletedHandler extends Handler {

        WeakReference<Button> buttonWeakReference;

        TextInputCompletedHandler(Button button) {
            buttonWeakReference = new WeakReference<>(button);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (EDIT_OK == msg.what) {
                Log.d(TAG, "Input completed" );
                Button button = buttonWeakReference.get();
                if (button != null) {
                    button.setText("Completed");
                }
            }
        }
    }
}
