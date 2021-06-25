package com.example.lbma.Models;

public class ActivityModel {
    private int id;
    private String activity;
    private String activity_conf;

    public ActivityModel(int id, String activity, String activity_conf) {
        this.id = id;
        this.activity = activity;
        this.activity_conf = activity_conf;
    }

    @Override
    public String toString() {
        return "ActivityModel{" +
                "id=" + id +
                ", activity='" + activity + '\'' +
                ", activity_conf='" + activity_conf + '\'' +
                '}';
    }

    public int getActivityId() {
        return id;
    }

    public void setActivityId(int id) {
        this.id = id;
    }

    public String getActivity() {
        return activity;
    }

    public void setActivity(String activity) {
        this.activity = activity;
    }

    public String getActivity_conf() {
        return activity_conf;
    }

    public void setActivity_conf(String activity_conf) {
        this.activity_conf = activity_conf;
    }
}
