package com.d.fivelove.model;

import java.util.Date;

/**
 * Created by Nguyen Kim Khanh on 8/18/2020.
 */
public class Image {
    private String bitmap;
    private Date timeStamp;

    public Image() {
    }

    public Image(String bitmap) {
        this.bitmap = bitmap;
        setTimeStamp(new Date(System.currentTimeMillis()));
    }

    public String getBitmap() {
        return bitmap;
    }

    public void setBitmap(String bitmap) {
        this.bitmap = bitmap;
    }

    public Date getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(Date timeStamp) {
        this.timeStamp = timeStamp;
    }
}
