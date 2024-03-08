package com.khokan_gorain_nsubuschalak_app.ModelClass;

public class LocationUpdate {

    String dv_name,bus_number,from_area,to_area,status,dv_phone_no,dv_id;
    Double latitude_gmap,longitude_gmap;

    public LocationUpdate(String dv_name, String bus_number, String from_area, String to_area, String status, String dv_phone_no, String dv_id, Double latitude_gmap, Double longitude_gmap) {
        this.dv_name = dv_name;
        this.bus_number = bus_number;
        this.from_area = from_area;
        this.to_area = to_area;
        this.status = status;
        this.dv_phone_no = dv_phone_no;
        this.dv_id = dv_id;
        this.latitude_gmap = latitude_gmap;
        this.longitude_gmap = longitude_gmap;
    }

    public LocationUpdate() {
    }

    public String getDv_name() {
        return dv_name;
    }

    public void setDv_name(String dv_name) {
        this.dv_name = dv_name;
    }

    public String getBus_number() {
        return bus_number;
    }

    public void setBus_number(String bus_number) {
        this.bus_number = bus_number;
    }

    public String getFrom_area() {
        return from_area;
    }

    public void setFrom_area(String from_area) {
        this.from_area = from_area;
    }

    public String getTo_area() {
        return to_area;
    }

    public void setTo_area(String to_area) {
        this.to_area = to_area;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDv_phone_no() {
        return dv_phone_no;
    }

    public void setDv_phone_no(String dv_phone_no) {
        this.dv_phone_no = dv_phone_no;
    }

    public String getDv_id() {
        return dv_id;
    }

    public void setDv_id(String dv_id) {
        this.dv_id = dv_id;
    }

    public Double getLatitude_gmap() {
        return latitude_gmap;
    }

    public void setLatitude_gmap(Double latitude_gmap) {
        this.latitude_gmap = latitude_gmap;
    }

    public Double getLongitude_gmap() {
        return longitude_gmap;
    }

    public void setLongitude_gmap(Double longitude_gmap) {
        this.longitude_gmap = longitude_gmap;
    }
}
