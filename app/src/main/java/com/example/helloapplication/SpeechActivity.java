package com.example.helloapplication;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.RecognizerListener;
import com.iflytek.cloud.RecognizerResult;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechEvent;
import com.iflytek.cloud.SpeechRecognizer;
import com.iflytek.cloud.VoiceWakeuper;
import com.iflytek.cloud.WakeuperListener;
import com.iflytek.cloud.WakeuperResult;
import com.iflytek.cloud.util.ResourceUtil;

public class SpeechActivity extends AppCompatActivity {

    private final static String TAG = "SpeechTest";

    private static final int PERMISSION_REQUEST_CODE = 202;

    // 语音听写对象
    private SpeechRecognizer mIat;

    private String[] mPermissions = new String[] {
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE
    };

    private interface Action {

        void execute();

    }

    // 语音唤醒对象
    private VoiceWakeuper mIvw;
    // 唤醒结果内容
    private String resultString;

    // 设置门限值 ： 门限值越低越容易被唤醒
    private TextView tvThresh;
    private SeekBar seekbarThresh;
    private final static int MAX = 3000;
    private final static int MIN = 0;
    private int curThresh = 1450;
    private String threshStr = "门限值：";
    private String keep_alive = "1";
    private String ivwNetMode = "0";

    /**
     * 初始化监听器。
     */
    private InitListener mInitListener = code -> {
        Log.e(TAG, String.format("SpeechRecognizer init() code = %s", code));
        if (code != ErrorCode.SUCCESS) {
            Log.e(TAG, String.format("初始化失败，错误码：%s", code));
        }
    };


    /**
     * 听写监听器。
     */
    private RecognizerListener mRecognizerListener = new RecognizerListener() {

        @Override
        public void onBeginOfSpeech() {
            // 此回调表示：sdk内部录音机已经准备好了，用户可以开始语音输入
            Log.e(TAG, "开始说话");
        }

        @Override
        public void onError(SpeechError error) {
            // Tips：
            // 错误码：10118(您没有说话)，可能是录音机权限被禁，需要提示用户打开应用的录音权限。
            Log.e(TAG, String.format("语音识别出错, errorCode=%s", error.getErrorCode()));
        }

        @Override
        public void onEndOfSpeech() {
            // 此回调表示：检测到了语音的尾端点，已经进入识别过程，不再接受语音输入
            Log.e(TAG, "结束说话");
        }

        @Override
        public void onResult(RecognizerResult results, boolean isLast) {
            Log.e(TAG, results.getResultString());

            if (isLast) {
                //TODO 最后的结果
            }
        }

        @Override
        public void onVolumeChanged(int volume, byte[] data) {
            Log.d(TAG, String.format("当前正在说话, 音量大小: %s, 返回音频数据：%s", volume, data.length));
        }

        @Override
        public void onEvent(int eventType, int arg1, int arg2, Bundle obj) {
            // 以下代码用于获取与云端的会话id，当业务出错时将会话id提供给技术支持人员，可用于查询会话日志，定位出错原因
            // 若使用本地能力，会话id为null
            //	if (SpeechEvent.EVENT_SESSION_ID == eventType) {
            //		String sid = obj.getString(SpeechEvent.KEY_EVENT_SESSION_ID);
            //		Log.d(TAG, "session id =" + sid);
            //	}
        }
    };

    private Action mRecognizerAction = () -> {
        // 不显示听写对话框
        int ret = mIat.startListening(mRecognizerListener);
        if (ret != ErrorCode.SUCCESS) {
            Log.e(TAG, String.format("听写失败,错误码：%s", ret));
        }
    };

    private WakeuperListener mWakeuperListener = new WakeuperListener() {

        @Override
        public void onResult(WakeuperResult result) {
            Log.e(TAG, "onResult");
            try {
                String text = result.getResultString();
                Log.e(TAG, String.format("result string=%s", text));
            } catch (Exception e) {
                Log.e(TAG, e.getMessage());
            }
        }

        @Override
        public void onError(SpeechError error) {
            Log.e(TAG, error.getPlainDescription(true));
        }

        @Override
        public void onBeginOfSpeech() {
        }

        @Override
        public void onEvent(int eventType, int isLast, int arg2, Bundle obj) {
            switch( eventType ){
                // EVENT_RECORD_DATA 事件仅在 NOTIFY_RECORD_DATA 参数值为 真 时返回
                case SpeechEvent.EVENT_RECORD_DATA:
                    final byte[] audio = obj.getByteArray( SpeechEvent.KEY_EVENT_RECORD_DATA );
                    Log.e(TAG, String.format("ivw audio length: %s", audio.length));
                    break;
            }
        }

        @Override
        public void onVolumeChanged(int volume) {
            // Log.e(TAG, String.format("当前音量：%s", volume));

        }
    };

    private Action mWakeuperAction = () -> {
        mIvw = VoiceWakeuper.getWakeuper();
        if(mIvw != null) {
            // 清空参数
            mIvw.setParameter(SpeechConstant.PARAMS, null);
            // 唤醒门限值，根据资源携带的唤醒词个数按照“id:门限;id:门限”的格式传入
            mIvw.setParameter(SpeechConstant.IVW_THRESHOLD, "0:"+ curThresh);
            // 设置唤醒模式
            mIvw.setParameter(SpeechConstant.IVW_SST, "wakeup");
            // 设置持续进行唤醒
            mIvw.setParameter(SpeechConstant.KEEP_ALIVE, keep_alive);
            // 设置闭环优化网络模式
            mIvw.setParameter(SpeechConstant.IVW_NET_MODE, ivwNetMode);
            // 设置唤醒资源路径
            mIvw.setParameter(SpeechConstant.IVW_RES_PATH, getResource());
            // 设置唤醒录音保存路径，保存最近一分钟的音频
            mIvw.setParameter( SpeechConstant.IVW_AUDIO_PATH, Environment.getExternalStorageDirectory().getPath()+"/msc/ivw.wav" );
            mIvw.setParameter( SpeechConstant.AUDIO_FORMAT, "wav" );
            // 如有需要，设置 NOTIFY_RECORD_DATA 以实时通过 onEvent 返回录音音频流字节
            //mIvw.setParameter( SpeechConstant.NOTIFY_RECORD_DATA, "1" );

            // 启动唤醒
            mIvw.startListening(mWakeuperListener);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_speech);

        // 初始化识别无UI识别对象
        // 使用SpeechRecognizer对象，可根据回调消息自定义界面；
        mIat = SpeechRecognizer.createRecognizer(this, mInitListener);

        Button btn_iat = findViewById(R.id.btn_test_iat);
        btn_iat.setOnClickListener(v -> requestPermission(mRecognizerAction));

        // 初始化唤醒对象
        mIvw = VoiceWakeuper.createWakeuper(this, null);

        Button btn_ivw = findViewById(R.id.btn_test_ivw);
        btn_ivw.setOnClickListener(v -> requestPermission(mWakeuperAction));
    }

    private void requestPermission(Action action) {
        boolean allGranted = true;

        for (String permission : mPermissions) {
            if (ContextCompat.checkSelfPermission(this, permission)
                    != PackageManager.PERMISSION_GRANTED) {
                allGranted = false;
            }
        }

        if (allGranted) {
            action.execute();
        } else {
            ActivityCompat.requestPermissions(this, mPermissions, PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            // 省略掉了对应权限判断和授权结果判断
            mRecognizerAction.execute();
        }
    }

    private String getResource() {
        final String resPath = ResourceUtil.generateResourcePath(SpeechActivity.this, ResourceUtil.RESOURCE_TYPE.assets, "ivw/5b6908cc.jet");
        Log.e(TAG, String.format("resPath: %s", resPath));
        return resPath;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mIat != null) {
            if (mIat.isListening()) {
                mIat.cancel();
            }
            mIat.destroy();
        }
        mIvw = VoiceWakeuper.getWakeuper();
        if (mIvw != null) {
            if (mIvw.isListening()) {
                mIvw.cancel();
            }
            mIvw.destroy();
        }
    }
}
