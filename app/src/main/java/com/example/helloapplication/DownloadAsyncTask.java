package com.example.helloapplication;

import android.os.AsyncTask;
import android.util.Log;

/**
 * Created by sunshow.
 */
public class DownloadAsyncTask extends AsyncTask<String, Integer, String> {

    private final static String TAG = "DownloadAsyncTask";

    private DownloadTaskListener mListener;

    public DownloadAsyncTask() {

    }

    public DownloadAsyncTask(DownloadTaskListener listener) {
        mListener = listener;
    }

    @Override
    protected String doInBackground(String... params) {
        int progress = 0;
        for(int i = 0;!isCancelled() && i <= 10;i ++){
            progress += 10;
            synchronized (this) {
                try {
                    wait(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            publishProgress(progress);
        }
        if (isCancelled()) {
            Log.e(TAG, "downloading task cancelled");
        } else {
            Log.e(TAG, "download completed");
        }
        return params[0];
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        if (mListener != null) {
            mListener.onCompleted(s);
        }
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
        for (Integer value : values) {
            Log.e(TAG, String.format("Current progress is %s", value));
        }
    }

    public interface DownloadTaskListener {

        void onCompleted(String s);

    }
}
