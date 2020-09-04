package com.d.fivelove.model;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * Created by Nguyen Kim Khanh on 8/22/2020.
 */
public class User implements Serializable {
    private String id;
    private String tel;
    private String name;
    private List<Image> images;
    private Date timeJoin;
    private String fcmToken;
    private String latitude, longitude;
    private String abilityListener, sex;


    public User() {
    }

    public User(String id, String tel, String name, List<Image> images, String fcmToken) {
        this.id = id;
        this.tel = tel;
        this.name = name;
        long timeStamp = System.currentTimeMillis();
        this.timeJoin = new Date(timeStamp);
        this.images = images;
        this.fcmToken = fcmToken;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getAbilityListener() {
        return abilityListener;
    }

    public void setAbilityListener(String abilityListener) {
        this.abilityListener = abilityListener;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getFcmToken() {
        return fcmToken;
    }

    public void setFcmToken(String fcmToken) {
        this.fcmToken = fcmToken;
    }

    public Date getTimeJoin() {
        return timeJoin;
    }

    public void setTimeJoin(Date timeJoin) {
        this.timeJoin = timeJoin;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTel() {
        return tel;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Image> getImages() {
        return images;
    }

    public void setImages(List<Image> images) {
        this.images = images;
    }
}
