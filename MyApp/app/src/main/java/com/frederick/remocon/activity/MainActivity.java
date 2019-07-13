package com.frederick.remocon.activity;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.IBinder;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.Gravity;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;


import com.frederick.lib.Logger;
import com.frederick.lib.Protocol;
import com.frederick.lib.bean.TextMessage;
import com.frederick.remocon.R;
import com.frederick.remocon.fragment.ButtonFragment;
import com.frederick.remocon.fragment.GestureFragment;
import com.frederick.remocon.fragment.GravitySensorFragment;
import com.frederick.remocon.fragment.JoyStickFragment;
import com.frederick.remocon.fragment.VoiceFragment;
import com.frederick.remocon.service.TCPClient;
import com.frederick.remocon.GlobalEnv;
import com.frederick.remocon.CONSTANT;
import com.frederick.lib.ImageCache;
import com.frederick.remocon.controller.TCPSmartCarController;
import com.frederick.remocon.view.ImageSurfaceView;


import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.regex.Pattern;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {


    private Button btn_link;
    private EditText et_ip;
    private ImageButton btn_qr;


    private final static int SCANNIN_GREQUEST_CODE = 2;
    private final int MY_PERMISSIONS_REQUEST_CAMERA=1;


    private ViewGroup mContainer;
    private View mFragmentContainer;
    private ViewGroup mDirectionContainer;
    private View mWelcome;
    private View mDirection;
    private ImageView mBackground;
    private View mVideo;

    private LinearLayout.LayoutParams lp_fragment_onVideo;
    private LinearLayout.LayoutParams lp_fragment_origin;
    private LinearLayout.LayoutParams lp_video;


    private final String ip_pattern="^((25[0-5]|2[0-4]\\d|((1\\d{2})|([1-9]?\\d)))\\.){3}(25[0-5]|2[0-4]\\d|((1\\d{2})|([1-9]?\\d)))$";
    private String ipAdr;


    private TCPClient.MyBinder binder;
    private TCPClientConnection conn;
    private TCPSmartCarController mTCPSmartCarController;


    IntentFilter intentFilter;
    MainThreadReceiver receiver;


    boolean receiveVideoInput = false;
    private ImageSurfaceView surfaceView;
    Thread thread;
    DatagramSocket socket;
    byte data[] = new byte[1024*64];
    Bitmap bmp1;
    DatagramPacket packet=new DatagramPacket(data, data.length);


    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            try {
                socket = new DatagramSocket(Protocol.UDP_CLIENT_PORT);
                receiveVideoInput = true;
                receive();
            }catch (IOException ex){
                ex.printStackTrace();
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        ImageCache.init(this);
        GlobalEnv.init();
        startService(new Intent(this, TCPClient.class));
        intentFilter = new IntentFilter();
        intentFilter.addAction(CONSTANT.ACTION_CONN);
        receiver = new MainThreadReceiver();



        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        lp_fragment_origin = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        lp_fragment_onVideo = new LinearLayout.LayoutParams(400,0,2);
        lp_fragment_onVideo.gravity = Gravity.CENTER_HORIZONTAL;
        lp_video = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,0,5);


        View headerLayout = navigationView.getHeaderView(0);
        btn_link = (Button)headerLayout.findViewById(R.id.btn_link);
        et_ip = (EditText)headerLayout.findViewById(R.id.et_ip);
        btn_qr = (ImageButton) headerLayout.findViewById(R.id.btn_qr);

        View appBarMain = findViewById(R.id.app_bar_main);
        mContainer = (FrameLayout)appBarMain.findViewById(R.id.myContainer);
        mBackground = mContainer.findViewById(R.id.background);


        mWelcome =(View) LayoutInflater.from(this).inflate(R.layout.welcome_main,mContainer,false);
        mContainer.addView(mWelcome);

        mDirection = (View) LayoutInflater.from(this).inflate(R.layout.direction_main, mContainer, false);
        mDirectionContainer = (ViewGroup)mDirection.findViewById(R.id.myDirectionContainer);
        mFragmentContainer = mDirectionContainer.findViewById(R.id.fragment_container);

        mVideo = (View) LayoutInflater.from(this).inflate(R.layout.video_main, mContainer, false);
        mVideo.setLayoutParams(lp_video);

        surfaceView = mVideo.findViewById(R.id.surfaceview_camera);




        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();


        btn_link.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                String inIpAdr = et_ip.getText().toString();
                if(Pattern.matches(ip_pattern, inIpAdr)) {
                    ipAdr = inIpAdr;
                    Log.i("MainActivity", "IPValidated"+ipAdr);
                    binder.connectServer(ipAdr,"12345","frederick");
                }
                else{
                    Toast.makeText(MainActivity.this, "Wrong IP address!",Toast.LENGTH_SHORT).show();
                }

            }
        });

        //Shut down the InputMethod when the EditText lost focus.
        et_ip.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                } else {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(et_ip.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                }
            }
        });

        btn_qr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkAndScan(view);
            }
        });


    }

    private void checkAndScan(View view){
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            if (!ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.CAMERA)) {
                showMessageOKCancel("需要打开相机权限才能扫描二维码",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                ActivityCompat.requestPermissions(MainActivity.this, new String[] {Manifest.permission.CAMERA},
                                        MY_PERMISSIONS_REQUEST_CAMERA);
                            }
                        });
                return;
            }
            ActivityCompat.requestPermissions(this,new String[] {Manifest.permission.CAMERA},
                    MY_PERMISSIONS_REQUEST_CAMERA);
            return;
        }
        startActivityForResult(
                new Intent(this, MipcaActivityCapture.class),
                SCANNIN_GREQUEST_CODE);
    }

    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(MainActivity.this)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_CAMERA:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission Granted
                    startActivityForResult(
                            new Intent(this, MipcaActivityCapture.class),
                            SCANNIN_GREQUEST_CODE);
                } else {
                    // Permission Denied
                    Toast.makeText(this, "相机权限未打开", Toast.LENGTH_SHORT)
                            .show();
                }
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode,resultCode,data);
        switch (requestCode) {
            case SCANNIN_GREQUEST_CODE:
                if (resultCode == RESULT_OK) {
                    Bundle bundle = data.getExtras();
                    ipAdr=bundle.getString("result");
                    et_ip.setText(ipAdr);
                    binder.connectServer(ipAdr,
                            "12345", "frederick");
                }
                break;
        }
    }

    @Override
    protected  void onStart(){
        super.onStart();
        registerReceiver(receiver, intentFilter);
        bindService(new Intent(this,TCPClient.class),conn = new TCPClientConnection(), BIND_AUTO_CREATE);
    }

    @Override
    protected void onStop() {
        super.onStop();
        unregisterReceiver(receiver);
        unbindService(conn);
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        stopService(new Intent(this, TCPClient.class));
    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        if(!GlobalEnv.getBoolean(CONSTANT.ACTION_CONN, false)){
            Toast.makeText(MainActivity.this, "Disconnected!",Toast.LENGTH_SHORT).show();
            return false;
        }

        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
       /* if (id == R.id.action_video) {
            if(!receiveVideoInput) {
                item.setIcon(R.drawable.ic_bar_video_close);
                receiveVideoInput = true;
                binder.send(new TextMessage("[req]"));
                receiveVideoInput = true;
                thread = new Thread((runnable));
                thread.start();
                mFragmentContainer.setLayoutParams(lp_fragment_onVideo);
                mDirectionContainer.addView(mVideo, 0);
            }
            else{

                item.setIcon(R.drawable.ic_bar_video_open);
                receiveVideoInput = false;
                binder.send(new TextMessage(Protocol.REQ_END_VIDEO));
                socket.close();
                thread.interrupt();
                bmp1=Bitmap.createBitmap(surfaceView.getWidth(),surfaceView.getHeight(),Bitmap.Config.ARGB_8888);
                bmp1.eraseColor(Color.BLACK);
                surfaceView.drawBitmap(bmp1);

                mFragmentContainer.setLayoutParams(lp_fragment_origin);
                mDirectionContainer.removeView(mVideo);
            }
        }else if(id == R.id.action_illuminate){
            binder.send(new TextMessage("[torch]"));
        }else if(id == R.id.action_alarm){
            binder.send(new TextMessage("[alarm]"));
        }
        */
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        if(!GlobalEnv.getBoolean(CONSTANT.ACTION_CONN, false)){
            Toast.makeText(MainActivity.this, "Disconnected!",Toast.LENGTH_SHORT).show();
            return false;
        }

        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            Toast.makeText(MainActivity.this, "Button Mode",Toast.LENGTH_SHORT).show();
            replaceFragment(new ButtonFragment());

        } else if (id == R.id.nav_gallery) {
            Toast.makeText(MainActivity.this, "Gesture Mode",Toast.LENGTH_SHORT).show();
            replaceFragment(new GestureFragment());

        } else if (id == R.id.nav_slideshow) {
            Toast.makeText(MainActivity.this, "Gravity Mode",Toast.LENGTH_SHORT).show();
            replaceFragment(new GravitySensorFragment());
        } else if (id == R.id.nav_manage) {
            Toast.makeText(MainActivity.this, "Joystick Mode",Toast.LENGTH_SHORT).show();
            replaceFragment(new JoyStickFragment());
        } else if (id == R.id.nav_share) {
            Toast.makeText(MainActivity.this, "Voice Mode",Toast.LENGTH_SHORT).show();
            replaceFragment(new VoiceFragment());
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void replaceFragment(Fragment fragment){
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.fragment_container, fragment);
        transaction.commit();
    }



    private class MainThreadReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getAction().equals(CONSTANT.ACTION_CONN)){
                et_ip.setText(ipAdr);
                btn_link.setText("已连接");
                btn_link.setEnabled(false);
                et_ip.setEnabled(false);;
                btn_qr.setImageResource(R.drawable.ic_header_linked);
                btn_qr.setEnabled(false);
                GlobalEnv.put(CONSTANT.ACTION_CONN,true);
                Log.d("...","hope");

                mContainer.removeView(mWelcome);
                mBackground.setImageResource(R.drawable.bg_whtie);
                mContainer.addView(mDirection);
                replaceFragment(new ButtonFragment());
            }
        }
    }

    private class TCPClientConnection implements ServiceConnection {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            binder = (TCPClient.MyBinder)iBinder;
            mTCPSmartCarController = new TCPSmartCarController(binder);

        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
        }
    }

    public TCPClient.MyBinder getBinder() {
        return this.binder;
    }

    public TCPSmartCarController getTCPSmartCarController() {return this.mTCPSmartCarController;}

    private void receive(){
        while(receiveVideoInput){
            try {
                socket.receive(packet);
                try {
                    bmp1 = null;
                    bmp1 = BitmapFactory.decodeByteArray(packet.getData(), 0, packet.getLength());
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            surfaceView.drawBitmap(bmp1);
                        }
                    });
                } catch (Exception ex) {
                    Logger.i("EXCEPTION!!!");
                    ex.printStackTrace();
                    //break;
                }
            }catch (IOException e){
                e.printStackTrace();
            }
        }
    }

}
