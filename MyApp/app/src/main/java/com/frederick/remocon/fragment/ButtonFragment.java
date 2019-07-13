package com.frederick.remocon.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.frederick.remocon.R;
import com.frederick.remocon.activity.MainActivity;

/**
 * Created by Frederick.
 */

public class ButtonFragment extends Fragment {
    private static final String TAG = ButtonFragment.class.getCanonicalName();
    private ImageView btn_forward, btn_left, btn_right, btn_back, btn_stop, btn_clock, btn_anticlock;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_button, container, false);

        btn_forward = (ImageView) view.findViewById(R.id.forwarding);
        btn_forward.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                Log.d(TAG, "forward");
                if (getActivity() != null) {
                    ((MainActivity)getActivity()).getTCPSmartCarController().text("B前");
                    Toast.makeText(getActivity(), "前进", Toast.LENGTH_SHORT).show();
                }
                return false;
            }
        });
        btn_left = (ImageView) view.findViewById(R.id.left);
        btn_left.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if (getActivity() != null) {
                    ((MainActivity)getActivity()).getTCPSmartCarController().text("B左");
                    Toast.makeText(getActivity(), "左移", Toast.LENGTH_SHORT).show();
                }
                return false;
            }
        });
        btn_right = (ImageView) view.findViewById(R.id.right);
        btn_right.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if (getActivity() != null) {
                    ((MainActivity)getActivity()).getTCPSmartCarController().text("B右");
                    Toast.makeText(getActivity(), "右移", Toast.LENGTH_SHORT).show();
                }
                return false;
            }
        });
        btn_back = (ImageView) view.findViewById(R.id.back);
        btn_back.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if (getActivity() != null) {
                    ((MainActivity)getActivity()).getTCPSmartCarController().text("B后");
                    Toast.makeText(getActivity(), "后退", Toast.LENGTH_SHORT).show();
                }
                return false;
            }
        });
        btn_stop = (ImageView) view.findViewById(R.id.stop);
        btn_stop.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if (getActivity() != null) {
                    Log.d(TAG, "stop");
                    ((MainActivity)getActivity()).getTCPSmartCarController().text("B停");
                    Toast.makeText(getActivity(), "停", Toast.LENGTH_SHORT).show();
                }
                return false;
            }
        });
        btn_clock = (ImageView) view.findViewById(R.id.clockwise);
        btn_clock.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if (getActivity() != null) {
                    ((MainActivity)getActivity()).getTCPSmartCarController().text("B顺");
                    Toast.makeText(getActivity(), "顺时针自转", Toast.LENGTH_SHORT).show();
                }
                return false;
            }
        });
        btn_anticlock = (ImageView) view.findViewById(R.id.anticlockwise);
        btn_anticlock.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if (getActivity() != null) {
                    ((MainActivity)getActivity()).getTCPSmartCarController().text("B逆");
                    Toast.makeText(getActivity(), "逆时针自转", Toast.LENGTH_SHORT).show();
                }
                return false;
            }
        });

        return view;
    }
}
