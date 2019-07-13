package com.frederick.remocon.controller;

import com.frederick.lib.bean.TextMessage;
import com.frederick.remocon.service.TCPClient;
import com.frederick.lib.Protocol;
import com.frederick.lib.bean.CommandMessage;

/**
 * Created by Frederick.
 */

public class TCPSmartCarController implements SmartCarController {
    private TCPClient.MyBinder binder;
    public TCPSmartCarController(TCPClient.MyBinder binder) {
        this.binder = binder;
    }

    public void text(String vocieResult) {
        binder.sendtext(new TextMessage(vocieResult));
    }

    @Override
    public void turnLeft(int degree) {
        binder.send(new CommandMessage(Protocol.CMD_TYPE_TURNLEFT,degree));
    }

    @Override
    public void turnRight(int degree) {
        binder.send(new CommandMessage(Protocol.CMD_TYPE_TURNRIGHT,degree));
    }

    @Override
    public void speedUp(int amount) {
        binder.send(new CommandMessage(Protocol.CMD_TYPE_SPEEDUP,amount));
    }

    @Override
    public void speedDown(int amount) {
        binder.send(new CommandMessage(Protocol.CMD_TYPE_SPEEDDOWN,amount));
    }

    public void setSpeed(int amount){
        binder.send(new CommandMessage(Protocol.CMD_TYPE_SETSPEED,amount));
    }

    public void forward(){
        binder.send(new CommandMessage(Protocol.CMD_TYPE_FORWARD,0));
    }

    public void backward(){
        binder.send(new CommandMessage(Protocol.CMD_TYPE_BACKWARD,0));
    }

    public void stop(){
        binder.send(new CommandMessage(Protocol.CMD_TYPE_STOP, 0));
    }

}
