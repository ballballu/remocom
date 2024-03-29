package com.frederick.lib;

/**
 * Created by Frederick.
 */

public interface Protocol {
    int TYPE_JSON = 1;
    int TYPE_IMAGE = 2;
    int TYPE_VIDEO = 3;

    int UDP_CLIENT_PORT = 12315;
    int UDP_SERVER_PORT = 15324;

    int MSG_TYPE_TEXT = 1;
    int MSG_TYPE_CMD = 2;

    //smart car controller
    int CMD_TYPE_TURNLEFT = 1;
    int CMD_TYPE_TURNRIGHT = 2;
    int CMD_TYPE_SPEEDUP = 3;
    int CMD_TYPE_SPEEDDOWN = 4;
    int CMD_TYPE_SETSPEED = 5;
    int CMD_TYPE_FORWARD = 6;
    int CMD_TYPE_BACKWARD = 7;
    int CMD_TYPE_STOP = 8;

    //some command
    String REQ_VIDEO = "[req]";
    String REQ_AUDIO = "[req_audio]";
    String REQ_FOLLOW = "[req_follow]";
    String REQ_END_VIDEO = "[req_end_video]";
    String REQ_STOP_FOLLOW = "[req_stop_follow]";
    String REQ_TORCH="[torch]";
    String REQ_ALARM="[alarm]";
    String REQ_CONNECT = "connect";

}
