package com.example.lbma.Models;

public class InfoModel {
    private int id;
    private boolean device_interactive;
    private int display_state;
    private long system_time;

    public InfoModel(int id, boolean device_interactive, int display_state, long system_time) {
        this.id = id;
        this.device_interactive = device_interactive;
        this.display_state = display_state;
        this.system_time = system_time;
    }

    @Override
    public String toString() {
        return "InfoModel{" +
                "id=" + id +
                ", device_interactive=" + device_interactive +
                ", display_state=" + display_state +
                ", system_time=" + system_time +
                '}';
    }

    public int getInfoId() {
        return id;
    }

    public void setInfoId(int id) {
        this.id = id;
    }

    public boolean isDevice_interactive() {
        return device_interactive;
    }

    public void setDevice_interactive(boolean device_interactive) {
        this.device_interactive = device_interactive;
    }

    public int getDisplay_state() {
        return display_state;
    }

    public void setDisplay_state(int display_state) {
        this.display_state = display_state;
    }

    public long getSystem_time() {
        return system_time;
    }

    public void setSystem_time(long system_time) {
        this.system_time = system_time;
    }
}
