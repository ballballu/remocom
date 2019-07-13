package com.frederick.remocon.fragment;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.frederick.remocon.activity.MainActivity;
import com.frederick.remocon.controller.TCPSmartCarController;
import com.frederick.remocon.R;
import com.frederick.remocon.CONSTANT;

/**
 * Created by Frederick.
 */

public class GravitySensorFragment extends Fragment {
    private static final String TAG = GravitySensorFragment.class.getCanonicalName();

    private TCPSmartCarController mTCPSmartcarController;
    private SensorManager mSensorManager;
    private MySensorEventLisener mMySensorEventLisener;

    private ImageView mImageView;
    private boolean viewonFlag=false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mSensorManager = (SensorManager) getActivity().getSystemService(Context.SENSOR_SERVICE);
        mMySensorEventLisener = new MySensorEventLisener();
        mTCPSmartcarController = new TCPSmartCarController(((MainActivity)getActivity()).getBinder());
    }


    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (getUserVisibleHint()) {
            if(viewonFlag) {
                if (mSensorManager != null) {
                    Sensor mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
                    mSensorManager.registerListener(mMySensorEventLisener, mSensor, SensorManager.SENSOR_DELAY_NORMAL);
                }
            }else viewonFlag=true;
        } else {
            if (mSensorManager != null) {
                mSensorManager.unregisterListener(mMySensorEventLisener);
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_gravity_sensor, container, false);
//        mTextView = (TextView) view.findViewById(R.id.textView);
        mImageView = (ImageView) view.findViewById(R.id.imgArrowGravity);
        if(viewonFlag){
            if(mSensorManager!=null){
                Sensor mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
                mSensorManager.registerListener(mMySensorEventLisener, mSensor, SensorManager.SENSOR_DELAY_NORMAL);
            }
        }else viewonFlag=true;
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        if(getUserVisibleHint() && mSensorManager!=null){
            Sensor mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            mSensorManager.registerListener(mMySensorEventLisener, mSensor, SensorManager.SENSOR_DELAY_NORMAL);
        }
        Log.d(TAG, "onResume");
    }

    @Override
    public void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(mMySensorEventLisener);
        Log.d(TAG, "onPause");

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        viewonFlag=false;
    }

    private class MySensorEventLisener implements SensorEventListener {
        @Override
        public void onSensorChanged(SensorEvent sensorEvent) {
            if (sensorEvent.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
                float x = sensorEvent.values[SensorManager.DATA_X];
                float y = sensorEvent.values[SensorManager.DATA_Y];
//                float z = sensorEvent.values[SensorManager.DATA_Z];
                switch (getDirecation(x, y)) {
                    case CONSTANT.FORWARDING: {
//                        mTextView.setText("FORWARDING");
                        Log.d(TAG, "forward");
                        mImageView.setImageDrawable(getResources().getDrawable(R.drawable.ic_arrow_up));
                        if (getActivity() != null) {
                            ((MainActivity)getActivity()).getTCPSmartCarController().text("G前");
                        }
                        break;
                    }
                    case CONSTANT.BACK: {
                        Log.d(TAG, "back");
//                        mTextView.setText("BACK");
                        mImageView.setImageDrawable(getResources().getDrawable(R.drawable.ic_arrow_down));
                        if (getActivity() != null) {
                            ((MainActivity)getActivity()).getTCPSmartCarController().text("G后");
                        }

                        break;
                    }
                    case CONSTANT.LEFT: {
                        Log.d(TAG, "left");
//                        mTextView.setText("LEFT");
                        mImageView.setImageDrawable(getResources().getDrawable(R.drawable.ic_arrow_left));
                        if (getActivity() != null) {
                            ((MainActivity)getActivity()).getTCPSmartCarController().text("G左");
                        }

                        break;
                    }
                    case CONSTANT.RIGHT: {
                        Log.d(TAG, "right");
//                        mTextView.setText("RIGHT");
                        mImageView.setImageDrawable(getResources().getDrawable(R.drawable.ic_arrow_right));
                        if (getActivity() != null) {
                            ((MainActivity)getActivity()).getTCPSmartCarController().text("G右");
                        }

                        break;
                    }
                    default:
                        if (getActivity() != null) {
                            TCPSmartCarController controller = ((MainActivity) getActivity()).getTCPSmartCarController();
                            if(controller!=null){
                                controller.text("G停");
                            }
                            mImageView.setImageDrawable(getResources().getDrawable(R.drawable.ic_arrow_stop));
                        }
                        break;

                }
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int i) {

        }
    }

    private int getDirecation(float x, float y) {
        if (-1 < x && x < 1) {
            if (y < -3) { //forwarding
                return CONSTANT.FORWARDING;
            } else if (y > 3) { //back
                return CONSTANT.BACK;
            }
        } else if (x > 4) { //left
            return CONSTANT.LEFT;
        } else if (x < -4) { //right
            return CONSTANT.RIGHT;
        }
        return -1;
    }



}
