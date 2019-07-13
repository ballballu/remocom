package com.frederick.remocon;

/**
 * Created by Frederick.
 */

public interface CONSTANT {
    int PORT = 12345;

    String GLOBAL_IS_CLIENT_CONNECTED="is_client_connected";

    String KEY_MSG_TYPE = "type";

    //ui thread handler
    String KEY_IP_ADDR = "ip_addr";
    int MSG_IP_ADDR = 1;
    int MSG_SEND_MSG = 2;
    int MSG_NEW_IMG = 3;
    int MSG_NEW_MSG = 4;
    String KEY_MSG_DATA = "msg_data";

    //broadcast action
    String ACTION_SERVER_UP = "server_up";
    String ACTION_NEW_MSG = "new_msg";
    String ACTION_NEW_IMG = "new_img";
    String ACTION_CONN="connected";

    //Global environment
    String GLOBAL_IP_ADDRESS = "ip_addr";

    //Direction
    int FORWARDING = 1;
    int LEFT = 2;
    int RIGHT = 3;
    int BACK = 4;

    String IFLY_APPID="=5b1cbe3b";
}
