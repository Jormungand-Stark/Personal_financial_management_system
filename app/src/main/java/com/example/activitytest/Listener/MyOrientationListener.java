package com.example.activitytest.Listener;

import static android.content.Context.SENSOR_SERVICE;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;

import java.util.Arrays;

/** 方向传感器 */
public class MyOrientationListener implements SensorEventListener {
    private static final String TAG = "WeatherActivity";
    private final Context context;
    private SensorManager sensorManager; // 传感器管理器
    private Sensor magneticSensor, accelerometerSensor; // 磁场、加速度传感器
    private float[] gravity = new float[3]; // 存储加速度变化值
    private float[] geomagnetic= new float[3]; // 存储磁场变化值
    private OnOrientationListener onOrientationListener;  //内部接口实现回调
    private double lastX;

    /** 当有新的传感器事件时(手机方向改变时调用)调用。 */
    @Override
    public void onSensorChanged(SensorEvent event) {
        //Log.e(TAG, "onSensorChanged 开始");
        // SensorEvent:保存精度(accuracy)、传感器类型(sensor)、时间戳(timestamp)
        // 以及不同传感器(Sensor)具有的不同传感器数组(values)。

        // TYPE_MAGNETIC_FIELD:描述磁场传感器类型的常量。
        if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
            geomagnetic = event.values;
            //Log.e(TAG, "onSensorChanged 得到磁场传感器: " + Arrays.toString(geomagnetic));
        }

        // TYPE_ACCELEROMETER:描述加速度传感器类型的常量。
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            gravity = event.values;
            //Log.e(TAG, "onSensorChanged 得到加速度传感器: " + Arrays.toString(gravity));
        }
        getValue();
    }

    /** 当注册传感器的精度发生变化时调用。 */
    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    /** 通过加速度和磁场变化获取方向变化的信息 */
    public void getValue() {
        //初始化数组
        float[] values = new float[3]; // 用来保存手机的旋转弧度
        float[] r = new float[9]; // 被填充的旋转矩阵

        // 传入gravity和geomagnetic，通过计算它们得到旋转矩阵R。
        // 而第二个参数倾斜矩阵I是用于将磁场数据转换进实际的重力坐标系中的，一般默认设置为NULL即可。
        SensorManager.getRotationMatrix(r, null, gravity, geomagnetic);

        // 根据旋转矩阵R计算设备的方向，将结果存储在values中。
        // values[0]记录着手机围绕 Z 轴的旋转弧度，
        // values[1]记录着手机围绕 X 轴的旋转弧度，
        // values[2]记录着手机围绕 Y 轴的旋转弧度。
        SensorManager.getOrientation(r, values);
        //Log.e(TAG, "getValue R: " + Arrays.toString(r));
        //Log.e(TAG, "getValue values: " + Arrays.toString(values));

        // 旋转弧度转为角度
        float pitch = (float) Math.toDegrees(values[0]);
        //Log.e(TAG, "getValue pitch: "+ pitch);
        if (Math.abs(lastX) > 1.0) { // 设置条件防止频繁回调
            onOrientationListener.onOrientationChanged(pitch);
        }
        lastX = pitch;
    }

    public interface  OnOrientationListener{
        void onOrientationChanged(float x);
    }

    /** 通过接口传递旋转角度变化信息 */
    public void setOnOrientationListener(OnOrientationListener onOrientationListener){
        this.onOrientationListener = onOrientationListener;
    }

    public MyOrientationListener(Context context){
        this.context=context;
    }

    public void onStart(){
        sensorManager = (SensorManager) context.getSystemService(SENSOR_SERVICE);

        if (sensorManager != null) { // 初始化两个传感器
            // getDefaultSensor:获取Sensor,使用给定的类型和唤醒属性返回传感器。
            magneticSensor = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
            accelerometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        }

        if (magneticSensor != null) {
            assert sensorManager != null;
            sensorManager.registerListener(this, magneticSensor,
                    SensorManager.SENSOR_DELAY_UI);
        }

        if (accelerometerSensor != null) {
            assert sensorManager != null;
            sensorManager.registerListener(this, accelerometerSensor,
                    SensorManager.SENSOR_DELAY_UI);
        }
    }

    public void onStop(){
        sensorManager.unregisterListener(this); // 传感器解除绑定
    }
}