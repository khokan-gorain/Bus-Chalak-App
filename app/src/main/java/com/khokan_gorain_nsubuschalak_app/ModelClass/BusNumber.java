package com.khokan_gorain_nsubuschalak_app.ModelClass;

public class BusNumber {

    int id,status;
    String bus_number;

    public BusNumber() {
    }

    public BusNumber(int id, int status, String bus_number) {
        this.id = id;
        this.status = status;
        this.bus_number = bus_number;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getBus_number() {
        return bus_number;
    }

    public void setBus_number(String bus_number) {
        this.bus_number = bus_number;
    }
}


