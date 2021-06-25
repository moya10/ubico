package com.example.lbma.Models;

public class CustomArrayList {
    long ts;
    String activity;
    float activity_conf;

    public CustomArrayList(long ts, String activity, float activity_conf) {
        this.ts = ts;
        this.activity = activity;
        this.activity_conf = activity_conf;
    }

    @Override
    public String toString() {
        return "CustomArrayList{" +
                "ts=" + ts +
                ", activity='" + activity + '\'' +
                ", activity_conf=" + activity_conf +
                '}';
    }

    public long getTs() {
        return ts;
    }

    public void setTs(long ts) {
        this.ts = ts;
    }

    public String getActivity() {
        return activity;
    }

    public void setActivity(String activity) {
        this.activity = activity;
    }

    public float getActivity_conf() {
        return activity_conf;
    }

    public void setActivity_conf(float activity_conf) {
        this.activity_conf = activity_conf;
    }
}
