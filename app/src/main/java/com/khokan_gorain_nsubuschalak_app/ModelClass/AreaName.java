package com.khokan_gorain_nsubuschalak_app.ModelClass;

public class AreaName {

    int id,status;
    String name;

    public AreaName(String areaName) {
    }

    public AreaName(int id, int status, String name) {
        this.id = id;
        this.status = status;
        this.name = name;
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

    public String getAreaName() {
        return name;
    }

    public void setAreaName(String areaName) {
        this.name = areaName;
    }
}
