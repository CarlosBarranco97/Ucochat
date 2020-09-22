package com.example.ucochat.Notifications;

public class Data {

    private String user;
    private String sented;
    private String body;
    private String title;
    private int icon;

    public Data(String user, String body, String title, String sented, int icon){
        this.user = user;
        this.body = body;
        this.sented = sented;
        this.title = title;
        this.icon = icon;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getSented() {
        return sented;
    }

    public void setSented(String sented) {
        this.sented = sented;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
