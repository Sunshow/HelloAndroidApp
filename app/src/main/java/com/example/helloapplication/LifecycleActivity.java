package com.example.helloapplication;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

public class LifecycleActivity extends AppCompatActivity {

    private final String TAG = "LifecycleTest";

    private DownloadAsyncTask mAsyncTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lifecycle);

        Log.e(TAG, "onCreate");

        mAsyncTask = new DownloadAsyncTask();
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.e(TAG, "onStart");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.e(TAG, "onStop");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.e(TAG, "onDestroy");
        if (mAsyncTask != null) {
            mAsyncTask.cancel(true);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.e(TAG, "onPause");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.e(TAG, "onResume");
        //testDownloadAsyncTask();
        mAsyncTask.execute("hi");
    }

    private void testDownloadAsyncTask() {
        DownloadAsyncTask asyncTask = new DownloadAsyncTask();
        asyncTask.execute("hi");
    }
}
