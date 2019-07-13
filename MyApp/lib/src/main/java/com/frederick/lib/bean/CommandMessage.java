package com.frederick.lib.bean;
import com.frederick.lib.Protocol;
import com.google.gson.annotations.SerializedName;
/**
 * Created by Frederick.
 */

public class CommandMessage extends Message{

    @SerializedName("text")
    Command command;

    public CommandMessage(String from, String timestamp, int cmdType, int cmdArg) {
        super(from,timestamp, Protocol.MSG_TYPE_CMD);
        command = new Command(cmdType,cmdArg);
    }

    public CommandMessage(int cmdType,int cmdArg) {
        super("","", Protocol.MSG_TYPE_CMD);
        command = new Command(cmdType,cmdArg);
    }

    public Command getCommand() {
        return command;
    }

    public void setCommand(Command command) {
        this.command = command;
    }
}
