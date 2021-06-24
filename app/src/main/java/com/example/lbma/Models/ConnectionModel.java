package com.example.lbma.Models;

public class ConnectionModel {
    private int id;
    private String network_type;

    public ConnectionModel(int id, String network_type) {
        this.id = id;
        this.network_type = network_type;
    }

    @Override
    public String toString() {
        return "ConnectionModel{" +
                "id=" + id +
                ", network_type='" + network_type + '\'' +
                '}';
    }

    public int getConnId() {
        return id;
    }

    public void setConnId(int id) {
        this.id = id;
    }

    public String getNetwork_type() {
        return network_type;
    }

    public void setNetwork_type(String network_type) {
        this.network_type = network_type;
    }
}
