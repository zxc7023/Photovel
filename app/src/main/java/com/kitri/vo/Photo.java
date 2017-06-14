package com.kitri.vo;

import android.graphics.Bitmap;
import android.support.annotation.NonNull;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;

import java.util.Date;

public class Photo implements Comparable<Photo>, ClusterItem {

    private int content_detail_id;
    private String photo_file_name;
    private int photo_top_flag;
    private Date photo_date;
    private double photo_latitude;
    private double photo_longitude;
    private LatLng position;
    private Bitmap bitmap;
    private String address;
    private String content;

    public Photo() {
    }

    public Photo(Bitmap bitmap, Date photoDate, String address, String content) {
        this.bitmap = bitmap;
        this.photo_date = photoDate;
        this.address = address;
        this.content = content;
    }

    public Photo(Bitmap bitmap, double latitude, double longitude, String fileName) {
        this.bitmap = bitmap;
        this.photo_latitude = latitude;
        this.photo_longitude = longitude;
        this.photo_file_name = fileName;
    }

    public Photo(Bitmap bitmap, Date photoDate, LatLng position, String photoFileName) {
        this.bitmap = bitmap;
        this.photo_date = photoDate;
        this.position = position;
        this.photo_file_name = photoFileName;
    }

    public int getContent_detail_id() {
        return content_detail_id;
    }

    public void setContent_detail_id(int content_detail_id) {
        this.content_detail_id = content_detail_id;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getContent() {
        return content;
    }
    public void setContent(String content) {
        this.content = content;
    }

    public String getPhoto_file_name() {
        return photo_file_name;
    }

    public void setPhoto_file_name(String photo_file_name) {
        this.photo_file_name = photo_file_name;
    }

    public int getPhoto_top_flag() {
        return photo_top_flag;
    }

    public void setPhoto_top_flag(int photo_top_flag) {
        this.photo_top_flag = photo_top_flag;
    }

    public Date getPhoto_date() {
        return photo_date;
    }

    public void setPhoto_date(Date photo_date) {
        this.photo_date = photo_date;
    }

    public double getPhoto_latitude() {
        return photo_latitude;
    }

    public void setPhoto_latitude(double photo_latitude) {
        this.photo_latitude = photo_latitude;
    }

    public double getPhoto_longitude() {
        return photo_longitude;
    }

    public void setPhoto_longitude(double photo_longitude) {
        this.photo_longitude = photo_longitude;
    }

    @Override
    public LatLng getPosition() {
        position = new LatLng(getPhoto_latitude(), getPhoto_longitude());
        return position;
    }


    // Photo 모델 복사
    public void CopyData(Photo param) {
        this.content_detail_id = param.getContent_detail_id();
        this.photo_file_name = param.getPhoto_file_name();
        this.photo_top_flag = param.getPhoto_top_flag();
        this.photo_date = param.getPhoto_date();
        this.photo_latitude = param.getPhoto_latitude();
        this.photo_longitude = param.getPhoto_longitude();
    }

    @Override
    public int compareTo(@NonNull Photo comparePhoto) {
        Date compareDate = comparePhoto.getPhoto_date();
        //ascending order
        return this.photo_date.compareTo(compareDate);
    }


    @Override
    public String toString() {
        return "Photo [contentDetailId=" + content_detail_id + ", photoFileName=" + photo_file_name + ", photoTopFlag="
                + photo_top_flag + ", photoDate=" + photo_date + ", photoLatitude=" + photo_latitude + ", photoLongitude="
                + photo_longitude + "]";
    }
}