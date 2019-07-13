package com.frederick.remocon.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.MotionEventCompat;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;
import com.frederick.remocon.R;
import com.frederick.remocon.activity.MainActivity;

/**
 * Created by Frederick.
 */

public class GestureFragment extends Fragment {

    private GestureDetector mDetector;
    private ImageView mImageView;
    private float MotionTracker[];
    private  float[] initPos = new float[2];
    private  float[] currentPos = new float[2];
    private  String add_init = "";
    private  String add_New = "";
    private  String add_Last = "";
    private  String add_Record = "";
    private  String testpoint = "";
    private  int posindex = 0;

    public String getAdd(float X, float Y){
        String Add;
        if (X<=340 && X>=0 && Y<=560 && Y>=200)
        {
            Add = "厕所";
        }
        else if(X<=1080 && X>=670 && Y<=810 && Y>=200)
        {
            Add = "厨房";
        }
        else if(X<=345 && X>=0 && Y<=1210 && Y>=860)
        {
            Add = "仓库";
        }
        else if(X<=1080 && X>=670 && Y<=1920 && Y>=1120)
        {
            Add = "卧室";
        }
        else if(X<=345 && X>=0 && Y<=1920 && Y>=1210)
        {
            Add = "书房";
        }
        else
        {
            Add = "走廊";
        }
        return Add;
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDetector=new GestureDetector(getActivity(),new MyGestureListener());
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_gesture, container, false);
        view.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                mDetector.onTouchEvent(motionEvent);
                return true;
            }
        });
        //mImageView=view.findViewById(R.id.imgArrowGesture);
        //mImageView.setImageDrawable(getResources().getDrawable(R.drawable.ic_arrow_stop));

        return view;
    }
    private class MyGestureListener extends GestureDetector.SimpleOnGestureListener{
        public MyGestureListener(){}
        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2,float distanceX, float distanceY){
            initPos[0] = e1.getX();
            initPos[1] = e1.getY();
            currentPos[0] = e2.getX();
            currentPos[1] = e2.getY();

            switch (e2.getAction()) {
                case MotionEvent.ACTION_UP:{
                    Toast.makeText(getActivity(), "up", Toast.LENGTH_SHORT).show();
                    break;
                }
            }
            if(add_init == "")
            {
                add_init = getAdd(initPos[0], initPos[1]);
                add_Last = add_init;
                add_Record = add_Record + "F" + add_init;
            }

            add_New = getAdd(currentPos[0], currentPos[1]);

            if(add_Last == "")
            {
                add_Last = add_New;
                add_Record = add_Record + add_New;
            }
            else if(add_Last != add_New)
            {
                add_Last = add_New;
                add_Record = add_Record+ "/" + add_New;
                add_Last = add_New;
            }
            else
            {
                add_Last = add_New;
            }

       // ((MainActivity)getActivity()).getTCPSmartCarController().text(s3);
            //Toast.makeText(getActivity(), s3, Toast.LENGTH_SHORT).show();
            return super.onScroll(e1,e2,distanceX,distanceY);
        }

       @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float v, float v2){

            if(add_Record != null)
            {
                ((MainActivity)getActivity()).getTCPSmartCarController().text(add_Record);
                Toast.makeText(getActivity(), add_Record, Toast.LENGTH_SHORT).show();
                add_Last = "";
                add_Record = "";
                add_New = "";
                add_init = "";
            }


            /*if(motionEvent.getX()-motionEvent2.getX()>100&&Math.abs(v)>50){
                mImageView.setImageDrawable(getResources().getDrawable(R.drawable.ic_arrow_left));
                if (getActivity() != null) {
                    ((MainActivity)getActivity()).getTCPSmartCarController().text("F左");
                }
            }else if(motionEvent2.getX()-motionEvent.getX()>100&&Math.abs(v)>50){
                mImageView.setImageDrawable(getResources().getDrawable(R.drawable.ic_arrow_right));
                if (getActivity() != null) {
                    ((MainActivity)getActivity()).getTCPSmartCarController().text("F右");
                }
            }else if(motionEvent2.getY()-motionEvent.getY()>100&&Math.abs(v)>50){
                mImageView.setImageDrawable(getResources().getDrawable(R.drawable.ic_arrow_down));
                if (getActivity() != null) {
                    ((MainActivity)getActivity()).getTCPSmartCarController().text("F后");
                }
            }else if(motionEvent.getY()-motionEvent2.getY()>100&&Math.abs(v)>50){
                mImageView.setImageDrawable(getResources().getDrawable(R.drawable.ic_arrow_up));
                if (getActivity() != null) {
                    ((MainActivity)getActivity()).getTCPSmartCarController().text("F前");
                }
            }*/
            return super.onFling(e1,e2,v,v2);
        }

        @Override
        public boolean onDoubleTap(MotionEvent e){
            float x = e.getX();
            float y = e.getY();
            testpoint = "" + x + " " + y;
            Toast.makeText(getActivity(), testpoint, Toast.LENGTH_SHORT).show();
            /*mImageView.setImageDrawable(getResources().getDrawable(R.drawable.ic_arrow_stop));
            if (getActivity() != null) {
                ((MainActivity)getActivity()).getTCPSmartCarController().text("F停");
            }*/
            return super.onDoubleTap(e);
        }


    }
}
