[Problems]
- A spirit level is a tool that uses a liquid-filled tube and an air bubble to determine whether a surface is horizontally or vertically aligned. Your task is to develop a mobile application that replicates the functionality of a physical spirit level using the device’s accelerometer sensor. Your app should:
  - Measure the tilt of the device using real-time sensor data from the device’s accelerometer
  - Display the tilt angle numerically (in degrees) for both X and Y axes.
  - Simulate a traditional bubble level:
    - Visually represent the bubble moving in response to device orientation.
    - Center the bubble when the surface is level.
  - Bonus challenges:
    - Include sound/vibration feedback when level is reached.
    - Include a horizontal and vertical mode.
    - Be creative and think about how you could use animations to further enhance the user experience.
[Examples]
- MainActivity.java
``` kotlin
package com.example.shenhaichen.spiritlevelapp;

import android.content.res.Configuration;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.widget.TextView;

import java.text.DecimalFormat;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    private SpiritView spiritView;
    private SensorManager mSensorManager;
    private float[] accelerometerValues = new float[3];
    private float[] magneticFieldValues = new float[3];
    private float values[] = new float[3];
    private float[] inR = new float[9];
    private float[] inclineMatrix = new float[9];
    private TextView degree_tv;
    private float degree = 0f;
    private DecimalFormat df;
    private float coordinateX = 0f;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        spiritView = (SpiritView) findViewById(R.id.mySpiritView);
        degree_tv = (TextView) this.findViewById(R.id.degree_tv);
        // get the sensor of the system
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        df = new DecimalFormat("#.#°");
    }

    @Override
    protected void onResume() {
        super.onResume();
    
        mSensorManager.registerListener(this,
                mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                Sensor.TYPE_ACCELEROMETER);
    
        mSensorManager.registerListener(this,
                mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD),
                Sensor.TYPE_MAGNETIC_FIELD);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        if (event.getAction() != MotionEvent.ACTION_UP) {
            return super.onTouchEvent(event);
        }




        return super.onTouchEvent(event);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        float[] values = event.values;
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            accelerometerValues = event.values;
        } else if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
            magneticFieldValues = event.values;
        }
        // calculate the orientation of the sensor
        calculateOrientation();
        int orientation = getResources().getConfiguration().orientation;


        int sensorType = event.sensor.getType();
        switch (sensorType) {

            case Sensor.TYPE_ACCELEROMETER:

                switch (orientation) {
                    case Configuration.ORIENTATION_PORTRAIT:
                        spiritView.setCycleX(0);
                        if ((0 < values[0] && values[0] < 9.81) && (0 < values[1] && values[1] < 9.81)) {
                            spiritView.setCycleX(-(degree - 90) * 10);
                        } else if ((-9.81 < values[0] && values[0] < 0) && (0 < values[1] && values[1] < 9.81)) {
                            spiritView.setCycleX((degree - 90) * 10);
                        }
                        break;

                    case Configuration.ORIENTATION_LANDSCAPE:
                        spiritView.setCycleX(0);
                        if ((0 < values[0] && values[0] < 9.81) && (-9.81 < values[1] && values[1] < 0)) {
                            spiritView.setCycleX(degree * 10);
                        }else  if ((0 < values[0] && values[0] < 9.81) && (0 < values[1] && values[1] < 9.81)){
                            spiritView.setCycleX(-degree * 10);
                        }

                        break;
                }
                break;
        }
    }

    private void calculateOrientation() {
        SensorManager.getRotationMatrix(inR, inclineMatrix, accelerometerValues, magneticFieldValues);
        SensorManager.getOrientation(inR, values);
        degree = (float) Math.toDegrees(values[1] - 0);
        degree = Math.abs(degree);
        degree_tv.setText(df.format(degree));

    }


    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
```
- ScreenUtil.java
``` kotlin
package com.example.shenhaichen.spiritlevelapp;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.WindowManager;

public class ScreenUtil {

    public ScreenUtil() {
        throw new UnsupportedOperationException("cannot be instantiated");
    }

    /**
     *  get the width of the screen, in order to draw the rectangle
     * @param context
     * @return
     */
    public static int getScreenWidth(Context context){
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics metrics = new DisplayMetrics();
        windowManager.getDefaultDisplay().getMetrics(metrics);
        return metrics.widthPixels;
    }

    /**
     *  get the height of screen, in order to draw the rectangle
     * @param context
     * @return
     */
    public static int getScreenHeight(Context context){
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics metrics = new DisplayMetrics();
        windowManager.getDefaultDisplay().getMetrics(metrics);
        return metrics.heightPixels;
    }
}
```
- SpiritView.java
``` kotlin
package com.example.shenhaichen.spiritlevelapp;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

public class SpiritView extends View {

    private float cycleX;
    private float cycleY;
    private int screenWidth, screenHeight;
    private Paint horizonalPaint;
    private Bitmap mBitmap;

    public SpiritView(Context context) {
        super(context);
    }

    public SpiritView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public SpiritView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

    }

    public void init() {
        horizonalPaint = new Paint();
        horizonalPaint.setAntiAlias(true);
        horizonalPaint.setColor(Color.BLUE);
        horizonalPaint.setDither(true);
        horizonalPaint.setStrokeWidth(5);
    }


    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        float x = getWidth()/2;
        float y = getHeight()/2;

        x += cycleX;

        if (x < 0){
            x = 0;
        }else if ( x > getWidth()){
            x = getWidth();
        }
        canvas.drawCircle(x,y,50,horizonalPaint);

    }

    public void setCycleX(float cycleX) {
        this.cycleX = cycleX;
        invalidate();
    }

    public void setCycleY(float cycleY) {
        this.cycleY = cycleY;
    }
}
```
- MainActivity Layout:
``` kotlin
<?xml version="1.0" encoding="utf-8"?>
<RelativeLayout
    xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:tools="http://schemas.android.com/tools"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:orientation="vertical"
    tools:context="com.example.shenhaichen.spiritlevelapp.MainActivity">

    <com.example.shenhaichen.spiritlevelapp.SpiritView
        android:layout_width="match_parent"
        android:layout_height="200px"
        android:id="@+id/mySpiritView"
        android:layout_centerInParent="true"
        android:background="@color/yellow"
        />

    <TextView
        android:id="@+id/degree_tv"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:textSize="50sp"
        android:textColor="@android:color/black"
        android:layout_below="@+id/mySpiritView"
        android:layout_centerHorizontal="true"
        />

</RelativeLayout>
```
[Input]
- MainActivity.kt monitoring sensor events
``` kotlin

```