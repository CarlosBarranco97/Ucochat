package com.example.ucochat.Adapter;

public class Message {

    private String transmitter, message, type;
    private Number time;

    public Message(String transmitter, String message, Number time, String type){
        this.transmitter = transmitter;
        this.message = message;
        this.time = time;
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTransmitter() {
        return transmitter;
    }

    public void setTransmitter(String transmitter) {
        this.transmitter = transmitter;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Number getTime() {
        return time;
    }

    public void setTime(Number time) {
        this.time = time;
    }
}
