package com.frederick.remocon.fragment;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.frederick.remocon.activity.MainActivity;
import com.google.gson.Gson;

import com.frederick.remocon.CONSTANT;
import com.frederick.remocon.R;

import java.util.ArrayList;

import com.iflytek.cloud.RecognizerResult ;
import com.iflytek.cloud.SpeechConstant ;
import com.iflytek.cloud.SpeechError ;
import com.iflytek.cloud.SpeechUtility ;
import com.iflytek.cloud.ui.RecognizerDialog ;
import com.iflytek.cloud.ui.RecognizerDialogListener ;


/**
 * Created by Frederick.
 */

public class VoiceFragment extends Fragment{

    private Button recog_btn;
    private final int MY_PERMISSIONS_REQUEST_RECORD_AUDIO = 1;
    private boolean viewonFlag=false;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_voice, container, false);

        recog_btn = (Button) view.findViewById(R.id.btn_recog);
        recog_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                initSpeech(getActivity());
            }
        });
        SpeechUtility.createUtility(getActivity(), SpeechConstant.APPID + CONSTANT.IFLY_APPID);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if(viewonFlag)getPermission();
        else viewonFlag=true;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        viewonFlag=false;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (getUserVisibleHint()){
            if(viewonFlag) getPermission();
            else viewonFlag=true;
        }
    }

    public void getPermission() {
        if (ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED) {
            if (!ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),Manifest.permission.RECORD_AUDIO)) {
                showMessageOKCancel("需要打开录音权限才能使用语音控制",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.RECORD_AUDIO},
                                        MY_PERMISSIONS_REQUEST_RECORD_AUDIO);
                            }
                        });
                return;
            }
            ActivityCompat.requestPermissions(getActivity(),new String[] {Manifest.permission.CAMERA}, MY_PERMISSIONS_REQUEST_RECORD_AUDIO);
        }
        return;
    }

    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(getActivity())
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }

    private void initSpeech(final Context context) {
        //1.创建RecognizerDialog对象
        RecognizerDialog mDialog = new RecognizerDialog(context, null);
        //2.设置accent、language等参数
        mDialog.setParameter(SpeechConstant.LANGUAGE, "zh_cn");
        mDialog.setParameter(SpeechConstant.ACCENT, "mandarin");
        mDialog.setListener(new RecognizerDialogListener() {
            @Override
            public void onResult(RecognizerResult recognizerResult, boolean isLast) {
                if (!isLast) {
                    //解析语音
                    String result = recognizerResult.getResultString();
                    Gson gson=new Gson();
                    StringBuffer sb=new StringBuffer();
                    ArrayList<Voice.WSBean> ws =  gson.fromJson(result, Voice.class).ws;
                    for (Voice.WSBean wsBean : ws) {
                        String word = wsBean.cw.get(0).w;
                        sb.append(word);
                    }
                    result=sb.toString();
                    Toast.makeText(getActivity(),result,Toast.LENGTH_SHORT).show();
                    processRecog(result);
                }
            }

            @Override
            public void onError(SpeechError speechError) {
                int error = speechError.getErrorCode();
                switch (error) {
                    case 20006:
                        Toast.makeText(getActivity(), "未打开录音权限", Toast.LENGTH_SHORT).show();
                        break;
                    default:
                        break;
                }
            }
        });
        //4.显示dialog，接收语音输入
        mDialog.show();
    }

    private void processRecog(String result){
        if(getActivity() != null) {
            /*if (result.contains("前")) {
                ((MainActivity)getActivity()).getTCPSmartCarController().forward();
            } else if (result.contains("后")) {
                ((MainActivity)getActivity()).getTCPSmartCarController().backward();
            } else if (result.contains("停")) {
                ((MainActivity)getActivity()).getTCPSmartCarController().stop();
            } else if (result.contains("左")) {
                ((MainActivity)getActivity()).getTCPSmartCarController().turnLeft(0);
            } else if (result.contains("右")) {
                ((MainActivity)getActivity()).getTCPSmartCarController().turnRight(0);
            }*/
            ((MainActivity)getActivity()).getTCPSmartCarController().text(result);
        }
    }

    public class Voice {

        public ArrayList<WSBean> ws;

        public class WSBean {
            public ArrayList<CWBean> cw;
        }

        public class CWBean {
            public String w;
        }
    }

}
