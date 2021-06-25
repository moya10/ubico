package com.example.lbma.Models;

public class PlaceModel {
    private int id;
    private String place;
    private String place_id;
    private double place_conf;

    public PlaceModel(int id, String place, String place_id, double place_conf) {
        this.id = id;
        this.place = place;
        this.place_id = place_id;
        this.place_conf = place_conf;
    }

    @Override
    public String toString() {
        return "PlaceModel{" +
                "id=" + id +
                ", place='" + place + '\'' +
                ", place_id='" + place_id + '\'' +
                ", place_conf=" + place_conf +
                '}';
    }

    public int getPlaceId() {
        return id;
    }

    public void setPlaceId(int id) {
        this.id = id;
    }

    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
    }

    public String getPlace_id() {
        return place_id;
    }

    public void setPlace_id(String place_id) {
        this.place_id = place_id;
    }

    public double getPlace_conf() {
        return place_conf;
    }

    public void setPlace_conf(double place_conf) {
        this.place_conf = place_conf;
    }
}
