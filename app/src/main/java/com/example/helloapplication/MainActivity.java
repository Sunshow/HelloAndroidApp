package com.example.helloapplication;

import android.Manifest;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;

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

    private static final int AMAP_REQUEST_CODE = 201;

    Button button;

    EditText text;

    private Handler mHandler;

    private Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            mHandler.sendEmptyMessage(EDIT_OK);
        }
    };

    //声明AMapLocationClient类对象
    public AMapLocationClient mLocationClient = null;
    //声明定位回调监听器
    public AMapLocationListener mLocationListener = new AMapLocationListener() {
        @Override
        public void onLocationChanged(AMapLocation aMapLocation) {
            Log.e(TAG, String.format("address=%s, longitude=%s, latitude=%s", aMapLocation.getAddress(), aMapLocation.getLongitude(), aMapLocation.getLatitude()));
        }
    };

    //声明AMapLocationClientOption对象
    public AMapLocationClientOption mLocationOption = null;

    private String[] mAMapPermissions = new String[] {
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.READ_PHONE_STATE
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
                //testSensor();
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

        Button btnTestSensor = findViewById(R.id.btn_test_sensor);
        btnTestSensor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                testSensor();
            }
        });

        /*
        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if (EDIT_OK == msg.what) {
                    Log.d(TAG, "Input completed" );
                }

            }
        };
        */

        Button btnTestAMap = findViewById(R.id.btn_test_amap);
        btnTestAMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                testAMap();
            }
        });

        mHandler = new TextInputCompletedHandler(button);

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

    private void testSensor() {
        startActivity(new Intent(this, SensorActivity.class));
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
        //asyncTask1.execute("guys");
        //asyncTask1.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, "guys");

        Executor executor = new ThreadPoolExecutor(10,50,
                10, TimeUnit.SECONDS, new LinkedBlockingDeque<Runnable>(100));
        asyncTask1.executeOnExecutor(executor, "guys");
    }

    private void testUpdateUI_1() {
        MyAsyncTask task = new MyAsyncTask(button);
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

    private void testAMap() {
        boolean allGranted = true;

        for (String permission : mAMapPermissions) {
            if (ContextCompat.checkSelfPermission(this, permission)
                    != PackageManager.PERMISSION_GRANTED) {
                allGranted = false;
            }
        }

        if (allGranted) {
            startLocation();
        } else {
            ActivityCompat.requestPermissions(this, mAMapPermissions, AMAP_REQUEST_CODE);
        }
    }

    private void startLocation() {
        //初始化定位
        mLocationClient = new AMapLocationClient(getApplicationContext());
        //设置定位回调监听
        mLocationClient.setLocationListener(mLocationListener);

        //初始化AMapLocationClientOption对象
        mLocationOption = new AMapLocationClientOption();
        //设置定位模式为AMapLocationMode.Hight_Accuracy，高精度模式。
        mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        //获取一次定位结果：
        //该方法默认为false。
        mLocationOption.setOnceLocation(true);
        //获取最近3s内精度最高的一次定位结果：
        //设置setOnceLocationLatest(boolean b)接口为true，启动定位时SDK会返回最近3s内精度最高的一次定位结果。如果设置其为true，setOnceLocation(boolean b)接口也会被设置为true，反之不会，默认为false。
        mLocationOption.setOnceLocationLatest(true);
        //设置是否返回地址信息（默认返回地址信息）
        mLocationOption.setNeedAddress(true);
        //设置是否允许模拟位置,默认为true，允许模拟位置
        mLocationOption.setMockEnable(true);

        mLocationClient.setLocationOption(mLocationOption);

        mLocationClient.startLocation();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == AMAP_REQUEST_CODE) {
            // 省略掉了对应权限判断和授权结果判断
            startLocation();
        }
    }

    static class MyAsyncTask extends AsyncTask<String, Integer, String> {

        WeakReference<Button> buttonWeakReference;

        MyAsyncTask(Button button) {
            buttonWeakReference = new WeakReference<>(button);
        }

        @Override
        protected String doInBackground(String... params) {
            Log.e(TAG, "downloading");
            return params[0];
        }

        @Override
        protected void onPostExecute(String s) {
            Button button = buttonWeakReference.get();
            if (button != null) {
                button.setText("Completed");
            } else {
                Log.e(TAG, "Button has been destroyed.");
            }
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
