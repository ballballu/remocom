package com.frederick.remocon.service;

import android.app.Service;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

import com.frederick.remocon.CONSTANT;
import com.frederick.remocon.GlobalEnv;
import com.frederick.lib.ImageCache;
import com.frederick.lib.Protocol;
import com.frederick.lib.bean.TextMessage;
import com.frederick.lib.Logger;
import com.google.gson.Gson;



/**
 * Created by Frederick.
 */

public class TCPClient extends Service {
    Thread clientThread;
    String IP;
    private String name = null;
    private String ip = null;
    private String port = null;

    Socket socket;
    private InetSocketAddress isa = null;

    DataInputStream dis = null;
    DataOutputStream dos = null;
    private String reMsg = null;
    private int msgType;
    private boolean isConnect;

    HandlerThread networkThread;
    Handler networkHandler;

    byte[] buffer = new byte[1024 * 64];

    public TCPClient() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        isConnect = false;
        networkThread = new HandlerThread("network");
        networkThread.start();
        networkHandler = new Handler(networkThread.getLooper()){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                Intent intent;
                switch (msg.what){
                    case CONSTANT.MSG_SEND_MSG:
                        send((com.frederick.lib.bean.Message)msg.getData().getSerializable(CONSTANT.KEY_MSG_DATA));
                        break;
                    case CONSTANT.MSG_NEW_MSG:
                        intent = new Intent(CONSTANT.ACTION_NEW_MSG);
                        intent.putExtra(CONSTANT.KEY_MSG_DATA, msg.getData().getString(CONSTANT.KEY_MSG_DATA));
                        sendBroadcast(intent);
                        break;
                    case CONSTANT.MSG_NEW_IMG:
                        intent = new Intent(CONSTANT.ACTION_NEW_IMG);
                        intent.putExtra(CONSTANT.KEY_MSG_DATA, msg.getData().getString(CONSTANT.KEY_MSG_DATA));
                        sendBroadcast(intent);
                        break;
                }
            }
        };
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i("TCP","OnStartCommand");
        Logger.i("tcp client service created");
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return new MyBinder();
    }

    //runnable for server thread
    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            try{
                connect();
                ReceiveMsg();
            }catch (Exception ex){
                ex.printStackTrace();
            }
        }
    };

    private void ReceiveMsg() {
        while (isConnect) {
            try {
                msgType = dis.readInt();
                Message msg = networkHandler.obtainMessage();
                Bundle bundle = new Bundle();
                switch (msgType){
                    case Protocol.TYPE_JSON:
                        reMsg = dis.readUTF();
                        //System.out.println(reMsg);
                        msg.what = CONSTANT.MSG_NEW_MSG;
                        bundle.putString(CONSTANT.KEY_MSG_DATA,reMsg);
                        msg.setData(bundle);
                        msg.sendToTarget();
                        break;
                    case Protocol.TYPE_IMAGE:
                        int size = dis.readInt();
                        byte[] data = new byte[size];
                        int len = 0;
                        while (len < size) {
                            len += dis.read(data, len, size - len);
                        }
                        ByteArrayOutputStream outPut = new ByteArrayOutputStream();
                        Bitmap bmp = BitmapFactory.decodeByteArray(data, 0, data.length);
                        bmp.compress(Bitmap.CompressFormat.PNG, 100, outPut);
                        String name = System.currentTimeMillis()+"";
                        ImageCache.addBitmap(name,bmp);

                        msg.what = CONSTANT.MSG_NEW_IMG;
                        bundle.putString(CONSTANT.KEY_MSG_DATA,name);
                        msg.setData(bundle);
                        msg.sendToTarget();
                        break;
                    case Protocol.TYPE_VIDEO:
                        int frame_size = 1024;
                        int number = 0,num;

                        /*while (true) {

                        }*/
                        //Logger.i("video ready");
                        try {
                            int h264length = dis.readInt();
                            number = 0;
                            while (number < h264length) {
                                int left = h264length - number;
                                num = dis.read(buffer, number, frame_size < left ? frame_size : left);
                                //Logger.i(String.format("H264 %d,%d,%d", h264length, number, num));
                                number += num;
                            }
                            ByteArrayOutputStream outPut1 = new ByteArrayOutputStream();
                            Bitmap bmp1 = BitmapFactory.decodeByteArray(buffer, 0, h264length);
                            bmp1.compress(Bitmap.CompressFormat.JPEG, 40, outPut1);
                            String name1 = System.currentTimeMillis() + "";
                            ImageCache.addBitmap(name1, bmp1);

                            msg.what = CONSTANT.MSG_NEW_IMG;
                            bundle.putString(CONSTANT.KEY_MSG_DATA, name1);
                            msg.setData(bundle);
                            msg.sendToTarget();
                        } catch (Exception ex) {
                            Logger.i("EXCEPTION!!!");
                            ex.printStackTrace();
                            //break;
                        }
                }
            } catch (SocketException e) {
                System.out.println("exit!");
                isConnect = false;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void connect() {
        try {
            socket = new Socket();
            isa = new InetSocketAddress(ip, Integer.parseInt(port));
            socket.connect(isa,5000);

            if(socket.isConnected()){
                GlobalEnv.put(CONSTANT.GLOBAL_IS_CLIENT_CONNECTED,true);
                dos = new DataOutputStream(socket.getOutputStream());
                dis = new DataInputStream(socket.getInputStream());
                send(new TextMessage(name, System.currentTimeMillis()+"",Protocol.REQ_CONNECT));
                Intent intent=new Intent(CONSTANT.ACTION_CONN);
                sendBroadcast(intent);
                System.out.println("connect");
                isConnect=true;
            }
        }catch (UnknownHostException e) {
            System.out.println("unknown host");
            e.printStackTrace();
        }catch (SocketTimeoutException e) {
            System.out.println("timeout");
            e.printStackTrace();
        }catch (IOException e) {
            System.out.println("io error");
            e.printStackTrace();
        }
    }

    public void send(com.frederick.lib.bean.Message message){
        if(isConnect){
            try {
                dos.writeInt(Protocol.TYPE_JSON);
                dos.writeUTF(new Gson().toJson(message));
                dos.flush();
            }catch (IOException ex){
                ex.printStackTrace();
            }
        }
    }

    public void disConnect() {
        if(dos!=null){
            /*try {
                dos.writeUTF("offline:"+name);

            } catch (IOException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }*/
            try {
                dis.close();
                dos.close();
                socket.close();
                GlobalEnv.put(CONSTANT.GLOBAL_IS_CLIENT_CONNECTED,false);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public boolean onUnbind(Intent intent){
        Log.i("TCP","OnUnbind");
        //For that it can be bind again.
        //The next time it will call "onRebind"
        return true;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i("TCP","onDestroy");
        Logger.i("tcp client service destroyed");
        disConnect();
    }

    public class MyBinder extends Binder {

        public String getIP(){
            return IP;
        }

        public void connectServer(String s_ip, String s_port, String c_name){
            //start TCP client
            ip = s_ip;
            port = s_port;
            name = c_name;
            clientThread = new Thread(runnable);
            clientThread.start();
        }

        public void send(com.frederick.lib.bean.Message message){
            //fill in the necessary content!
            message.setFrom(name);
            message.setTimestamp(System.currentTimeMillis()+"");

            Message msg = networkHandler.obtainMessage(CONSTANT.MSG_SEND_MSG);
            Bundle bundle = new Bundle();
            bundle.putSerializable(CONSTANT.KEY_MSG_DATA, message);
            msg.setData(bundle);
            msg.sendToTarget();
        }
        public void sendtext(com.frederick.lib.bean.Message message){
            //fill in the necessary content!
            message.setFrom(name);
            message.setTimestamp(System.currentTimeMillis()+"");
            Message msg = networkHandler.obtainMessage(CONSTANT.MSG_SEND_MSG);
            Bundle bundle = new Bundle();
            bundle.putSerializable(CONSTANT.KEY_MSG_DATA, message);
            msg.setData(bundle);
            msg.sendToTarget();
        }
    }


}
