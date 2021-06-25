package com.example.lbma.Models;

public class BatteryModel {
    private int id;
    private int battery_level;
    private boolean battery_status;

    public BatteryModel(int id, int battery_level, boolean battery_status) {
        this.id = id;
        this.battery_level = battery_level;
        this.battery_status = battery_status;
    }

    @Override
    public String toString() {
        return "BatteryModel{" +
                "id=" + id +
                ", battery_level=" + battery_level +
                ", battery_status=" + battery_status +
                '}';
    }

    public int getBatteryId() {
        return id;
    }

    public void setBatteryId(int id) {
        this.id = id;
    }

    public int getBattery_level() {
        return battery_level;
    }

    public void setBattery_level(int battery_level) {
        this.battery_level = battery_level;
    }

    public boolean isBattery_status() {
        return battery_status;
    }

    public void setBattery_status(boolean battery_status) {
        this.battery_status = battery_status;
    }
}
