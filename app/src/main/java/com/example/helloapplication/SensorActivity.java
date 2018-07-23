package com.example.helloapplication;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.util.List;

public class SensorActivity extends AppCompatActivity {

    private final String TAG = "SensorActivity";

    private SensorManager mSensorManager;

    private SensorEventListener mOrientationListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sensor);

        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

        Button btnGetList = findViewById(R.id.btn_get_list);
        btnGetList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                testSensorGetList();
            }
        });

        Button btnOrientationStart = findViewById(R.id.btn_orientation_start);
        btnOrientationStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                testOrientation();
            }
        });

        Button btnOrientationStop = findViewById(R.id.btn_orientation_stop);
        btnOrientationStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                unregisterOrientationListener();
            }
        });
    }

    private String getChineseName(int type) {
        switch (type) {
            case Sensor.TYPE_ACCELEROMETER:
                return "加速度传感器";
            case Sensor.TYPE_GYROSCOPE:
                return "陀螺仪传感器";
            case Sensor.TYPE_LIGHT:
                return "环境光线传感器";
            case Sensor.TYPE_MAGNETIC_FIELD:
                return "电磁场传感器";
            case Sensor.TYPE_ORIENTATION:
                return "方向传感器";
            case Sensor.TYPE_PRESSURE:
                return "压力传感器";
            case Sensor.TYPE_PROXIMITY:
                return "距离传感器";
            case Sensor.TYPE_TEMPERATURE:
                return "温度传感器";
            case Sensor.TYPE_GRAVITY:
                return "重场传感器";
            case Sensor.TYPE_LINEAR_ACCELERATION:
                return "线性加速度传感器";
            case Sensor.TYPE_ROTATION_VECTOR:
                return "旋转矢量传感器";
            case Sensor.TYPE_RELATIVE_HUMIDITY:
                return "湿度传感器";
            case Sensor.TYPE_AMBIENT_TEMPERATURE:
                return "温度传感器";
            case Sensor.TYPE_GAME_ROTATION_VECTOR:
                return "游戏旋转矢量传感器";
            case Sensor.TYPE_STEP_COUNTER:
                return "计步器（记录历史步数累加值）";
            case Sensor.TYPE_STEP_DETECTOR:
                return "检测器（检测每次步伐数据）";
            case Sensor.TYPE_GEOMAGNETIC_ROTATION_VECTOR:
                return "地磁旋转矢量传感器";
            case Sensor.TYPE_SIGNIFICANT_MOTION:
                return "特殊动作触发传感器";
            default:
                return "未知传感器";
        }
    }

    private void testSensorGetList() {
        if (mSensorManager != null) {
            List<Sensor> sensorList = mSensorManager.getSensorList(Sensor.TYPE_ALL);

            for (Sensor sensor : sensorList) {
                Log.e(TAG, String.format("Sensor name: %s, type: %s-%s, version: %s", sensor.getName(), sensor.getType(), getChineseName(sensor.getType()), sensor.getVersion()));
            }
        }
    }

    private void testOrientation() {
        if (mSensorManager != null) {
            unregisterOrientationListener();
            // Android 获取手机旋转的方向和角度是通过加速度传感器和地磁传感器共同计算得出的

            // 通过 getDefaultSensor() 得到加速度传感器和地磁传感器的实例
            Sensor accelerometerSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            Sensor magneticSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);

            mOrientationListener = new SensorEventListener() {
                float[] accelerometerValues = new float[3];
                float[] magneticValues = new float[3];

                String orientation = "";

                @Override
                public void onAccuracyChanged(Sensor sensor, int accuracy) {
                }

                @Override
                public void onSensorChanged(SensorEvent event) {
                    // 判断当前是加速度传感器还是地磁传感器
                    if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
                        // 注意赋值时要调用clone()方法
                        accelerometerValues = event.values.clone();
                    } else if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
                        // 注意赋值时要调用clone()方法
                        magneticValues = event.values.clone();
                    }
                    float[] R = new float[9];
                    float[] values = new float[3];
                    SensorManager.getRotationMatrix(R, null, accelerometerValues, magneticValues);

                    // values 是一个长度为 3 的 float 数组，手机在各个方向上的旋转数据都会被存放到这个数组当中
                    // values[0] 记录着手机围绕 Z 轴的旋转弧度
                    // values[1] 记录着手机围绕 X 轴的旋转弧度
                    // values[2] 记录着手机围绕 Y 轴的旋转弧度
                    /*
                    values[0]：方位角，手机绕着Z轴旋转的角度。0表示正北(North)，90表示正东(East)，180表示正南(South)，270表示正西(West)。

                    values[1]：倾斜角，手机翘起来的程度，当手机绕着x轴倾斜时该值会发生变化。取值范围是[-180,180]之间。假如把手机放在桌面上，而桌面是完全水平的话，values1的则应该是0。

                    value[2]：滚动角，沿着Y轴的滚动角度，取值范围为：[-90,90]，假设将手机屏幕朝上水平放在桌面上，这时如果桌面是平的，values2的值应为0。
                    */

                    SensorManager.getOrientation(R, values);

                    Log.e(TAG, String.format("values[0]=%s, values[1]=%s, values[2]=%s", Math.toDegrees(values[0]), Math.toDegrees(values[1]), Math.toDegrees(values[2])));

                    double degreeZ = Math.toDegrees(values[0]);

                    // 思考：传感器数值受精度以及地磁场变化等影响始终变化，如何减少触发次数？
                    /*
                    if (degreeZ >= -5 && degreeZ < 5) {
                        Log.e(TAG, "正北");
                    } else if (degreeZ >= 5 && degreeZ < 85) {
                        Log.e(TAG, "东北");
                    } else if (degreeZ >= 85 && degreeZ <= 95) {
                        Log.e(TAG, "正东");
                    } else if (degreeZ >= 95 && degreeZ < 175) {
                        Log.e(TAG, "东南");
                    } else if ((degreeZ >= 175 && degreeZ <= 180)
                            || (degreeZ) >= -180 && degreeZ < -175) {
                        Log.e(TAG, "正南");
                    } else if (degreeZ >= -175 && degreeZ < -95) {
                        Log.e(TAG, "西南");
                    } else if (degreeZ >= -95 && degreeZ < -85) {
                        Log.e(TAG, "正西");
                    } else if (degreeZ >= -85 && degreeZ < -5) {
                        Log.e(TAG, "西北");
                    }
                    */

                    /*
                    String newOrientation = "";
                    if (degreeZ >= -5 && degreeZ < 5) {
                        newOrientation = "正北";
                    } else if (degreeZ >= 5 && degreeZ < 85) {
                        newOrientation = "东北";
                    } else if (degreeZ >= 85 && degreeZ <= 95) {
                        newOrientation = "正东";
                    } else if (degreeZ >= 95 && degreeZ < 175) {
                        newOrientation = "东南";
                    } else if ((degreeZ >= 175 && degreeZ <= 180)
                            || (degreeZ) >= -180 && degreeZ < -175) {
                        newOrientation = "正南";
                    } else if (degreeZ >= -175 && degreeZ < -95) {
                        newOrientation = "西南";
                    } else if (degreeZ >= -95 && degreeZ < -85) {
                        newOrientation = "正西";
                    } else if (degreeZ >= -85 && degreeZ < -5) {
                        newOrientation = "西北";
                    }

                    if (!newOrientation.equals(orientation)) {
                        Log.e(TAG, newOrientation);
                        orientation = newOrientation;
                    }
                    */
                }
            };

            mSensorManager.registerListener(mOrientationListener, accelerometerSensor, SensorManager.SENSOR_DELAY_GAME);
            mSensorManager.registerListener(mOrientationListener, magneticSensor, SensorManager.SENSOR_DELAY_GAME);
        }
    }

    private void unregisterOrientationListener() {
        if (mOrientationListener != null) {
            mSensorManager.unregisterListener(mOrientationListener);
            mOrientationListener = null;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        unregisterOrientationListener();
    }
}
