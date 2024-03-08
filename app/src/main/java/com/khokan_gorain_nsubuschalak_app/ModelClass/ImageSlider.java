package com.khokan_gorain_nsubuschalak_app.ModelClass;

public class ImageSlider {
    int id;
    String name,img,status,msg;

    public ImageSlider(int id, String name, String img, String status, String msg) {
        this.id = id;
        this.name = name;
        this.img = img;
        this.status = status;
        this.msg = msg;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
