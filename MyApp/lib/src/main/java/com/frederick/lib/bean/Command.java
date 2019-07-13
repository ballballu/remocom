package com.frederick.lib.bean;

/**
 * Created by Frederick.
 */

public class Command {
    int cmdType;

    int cmtArg;

    public Command(int cmdType, int cmtArg) {
        this.cmdType = cmdType;
        this.cmtArg = cmtArg;
    }

    public int getCmdType() {
        return cmdType;
    }

    public void setCmdType(int cmdType) {
        this.cmdType = cmdType;
    }

    public int getCmtArg() {
        return cmtArg;
    }

    public void setCmtArg(int cmtArg) {
        this.cmtArg = cmtArg;
    }
}
