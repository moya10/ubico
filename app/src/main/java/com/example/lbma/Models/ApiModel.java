package com.example.lbma.Models;

public class ApiModel {
    private int id;
    private String DEVICE;
    private String DEVICE_ID;
    private String DEVICE_CREDENTIALS;
    private String DEVICE_TOKEN;

    public ApiModel(int id, String DEVICE, String DEVICE_ID, String DEVICE_CREDENTIALS, String DEVICE_TOKEN) {
        this.id = id;
        this.DEVICE = DEVICE;
        this.DEVICE_ID = DEVICE_ID;
        this.DEVICE_CREDENTIALS = DEVICE_CREDENTIALS;
        this.DEVICE_TOKEN = DEVICE_TOKEN;
    }

    @Override
    public String toString() {
        return "ApiModel{" +
                "id=" + id +
                ", DEVICE='" + DEVICE + '\'' +
                ", DEVICE_ID='" + DEVICE_ID + '\'' +
                ", DEVICE_CREDENTIALS='" + DEVICE_CREDENTIALS + '\'' +
                ", DEVICE_TOKEN='" + DEVICE_TOKEN + '\'' +
                '}';
    }

    public int getAPIId() {
        return id;
    }

    public void setApiId(int id) {
        this.id = id;
    }

    public String getDEVICE() {
        return DEVICE;
    }

    public void setDEVICE(String DEVICE) {
        this.DEVICE = DEVICE;
    }

    public String getDEVICE_ID() {
        return DEVICE_ID;
    }

    public void setDEVICE_ID(String DEVICE_ID) {
        this.DEVICE_ID = DEVICE_ID;
    }

    public String getDEVICE_CREDENTIALS() {
        return DEVICE_CREDENTIALS;
    }

    public void setDEVICE_CREDENTIALS(String DEVICE_CREDENTIALS) {
        this.DEVICE_CREDENTIALS = DEVICE_CREDENTIALS;
    }

    public String getDEVICE_TOKEN() {
        return DEVICE_TOKEN;
    }

    public void setDEVICE_TOKEN(String DEVICE_TOKEN) {
        this.DEVICE_TOKEN = DEVICE_TOKEN;
    }

}
