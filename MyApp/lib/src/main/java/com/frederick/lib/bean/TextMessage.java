package com.frederick.lib.bean;

import com.google.gson.annotations.SerializedName;
import com.frederick.lib.Protocol;

/**
 * Created by Frederick.
 */

public class TextMessage extends Message {
    @SerializedName("text")
    String text;

    public TextMessage(String from, String timestamp, String text) {
        super(from,timestamp, Protocol.MSG_TYPE_TEXT);
        this.text = text;
    }

    public TextMessage(String text) {
        super("","", Protocol.MSG_TYPE_TEXT);
        this.text = text;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
