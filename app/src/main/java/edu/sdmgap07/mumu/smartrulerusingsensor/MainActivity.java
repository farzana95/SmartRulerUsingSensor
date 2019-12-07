package edu.sdmgap07.mumu.smartrulerusingsensor;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements SensorEventListener, View.OnTouchListener{

    //last x, y, z axes values
    private float mLastX, mLastY, mLastZ;

    //for delta x, y, z
    private float deltaX, deltaY, deltaZ;

    //for grabbing button
    private Button time_spent = null;

    //signaling if button is pressed or sensor is started
    private boolean mInitialized;

    //for grabbing sensor manager OS
    private SensorManager mSensorManager;

    //for grabbing sensor you want to use
    private Sensor mAccelerometer;

    //some sensors read data while it is not required, in that cases we need NOISE cancellation
    private final float NOISE = (float) 2.0f;

    //used for keeping calculated distance
    private float calculatedDistance = 0;

    //used for keeping the timer's start time
    private long startTime = 0;

    // used for keeping the timer's stop time
    private long endTime = 0;

    // used for keeping time difference
    private long deltaTime = 0;

    TextView tvX, tvY, tvZ, distance, _time, _acceleration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mInitialized = false;

        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        time_spent = findViewById(R.id.time_spent);

        time_spent.setOnTouchListener(this);

        tvX = findViewById(R.id.x_axis);
        tvY = findViewById(R.id.y_axis);
        tvZ = findViewById(R.id.z_axis);

        distance = findViewById(R.id.distance);
        _time = findViewById(R.id.time);
        _acceleration = findViewById(R.id.acceleration);
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {

        float x = sensorEvent.values[0];
        float y = sensorEvent.values[1];
        float z = sensorEvent.values[2];

        if(!mInitialized){
            mLastX = x;
            mLastY = y;
            mLastZ = z;

            tvX.setText("0.0");
            tvY.setText("0.0");
            tvZ.setText("0.0");

            mInitialized = true;
        } else {

            deltaX = Math.abs(mLastX - x);
            deltaY = Math.abs(mLastY - y);
            deltaZ = Math.abs(mLastZ - z);


            if(deltaX < NOISE)
                deltaX = (float) 0.0;

            if (deltaY < NOISE)
                deltaY = (float) 0.0;

            if (deltaZ < NOISE)
                deltaZ = (float) 0.0;

            mLastX = x;
            mLastY = y;
            mLastZ = z;

            tvX.setText(Float.toString(deltaX));
            tvY.setText(Float.toString(deltaY));
            tvZ.setText(Float.toString(deltaZ));
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    public boolean onTouch(View v, MotionEvent motionEvent) {

        float time;
        float acceleration = 0.0f;

        if(motionEvent.getAction() == MotionEvent.ACTION_DOWN){

            mSensorManager.registerListener(this, mAccelerometer,
                    SensorManager.SENSOR_DELAY_NORMAL);

            _acceleration.setText("Acceleration = " + "0" + "m/s^2");

            startTime = java.lang.System.currentTimeMillis();

        } else if(motionEvent.getAction() == MotionEvent.ACTION_UP){

            mSensorManager.unregisterListener(this);

            endTime = java.lang.System.currentTimeMillis();

            deltaTime = endTime - startTime;

            time = (float)  deltaTime/1000.0f;


            if(deltaX <= NOISE || deltaY <= NOISE || (deltaX <= NOISE && deltaY <= NOISE)){
                calculatedDistance = 0;
                acceleration = 0;
            }

            if(deltaX > deltaY){
                acceleration = deltaX/9.81f;
            } else if(deltaY > deltaX){
                acceleration = deltaY/9.81f;
            }

            calculatedDistance = 0.5f * acceleration * ((time) * (time));

            distance.setText("Distance = " + calculatedDistance + " m or "
                    + calculatedDistance * 100.0f + " cm");

            _time.setText("Time = " + time + " s");

            _acceleration.setText("Acceleration = " + acceleration + " m/s^2");
        }
        return false;
    }
}
