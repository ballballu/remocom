package com.frederick.remocon.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.os.Handler;

import com.jmedeisis.bugstick.Joystick;
import com.jmedeisis.bugstick.JoystickListener;

import com.frederick.remocon.activity.MainActivity;
import com.frederick.remocon.controller.TCPSmartCarController;
import com.frederick.remocon.CONSTANT;
import com.frederick.remocon.R;


/**
 * Created by Frederick.
 */

public class JoyStickFragment extends Fragment {
    private static final String TAG = JoyStickFragment.class.getCanonicalName();
    private TCPSmartCarController mTCPSmartCarController;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mTCPSmartCarController = new TCPSmartCarController(((MainActivity)getActivity()).getBinder());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_joystick, container, false);

        Joystick joystick = (Joystick) view.findViewById(R.id.joystick);
        joystick.setJoystickListener(new JoystickListener() {
            @Override
            public void onDown() {
            }

            @Override
            public void onDrag(float degrees, float offset) {
                int direction = getTheDirection(degrees);
                switch (direction) {
                    case CONSTANT.FORWARDING : {
                        if (getActivity() != null) {
                            ((MainActivity)getActivity()).getTCPSmartCarController().text("J前");
                        }
                        break;
                    }
                    case CONSTANT.LEFT: {
                        if (getActivity() != null) {
                            ((MainActivity)getActivity()).getTCPSmartCarController().text("J左");
                        }
                        break;
                    }
                    case CONSTANT.RIGHT : {
                        if (getActivity() != null) {
                            ((MainActivity)getActivity()).getTCPSmartCarController().text("J右");
                        }
                        break;
                    }
                    case CONSTANT.BACK : {
                        if (getActivity() != null) {
                            ((MainActivity)getActivity()).getTCPSmartCarController().text("J后");
                        }
                        break;
                    }
                    case -1:
                        if (getActivity() != null) {
                            ((MainActivity)getActivity()).getTCPSmartCarController().text("J停");
                        }
                        break;
                }
            }

            @Override
            public void onUp() {
                ((MainActivity)getActivity()).getTCPSmartCarController().text("J停");
            }
        });
        return view;
    }


    private int getTheDirection(float degrees) {
        if ( 70 < degrees && degrees < 110) {
            return CONSTANT.FORWARDING;
        } else if ((160 < degrees && degrees < 180) || (-180 < degrees && degrees < -160)) {
            return CONSTANT.LEFT;
        } else if (-110 < degrees && degrees < -70) {
            return CONSTANT.BACK;
        } else if (-20 < degrees && degrees < 20) {
            return CONSTANT.RIGHT;
        }
        else return -1;
    }



}
