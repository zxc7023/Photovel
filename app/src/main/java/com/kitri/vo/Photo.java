package com.kitri.vo;

import android.graphics.Bitmap;
import android.support.annotation.NonNull;

import java.util.Date;

public class Photo implements Comparable<Photo> {

    private int contentDetailId;
    private String photoFileName;
    private int photoTopFlag;
    private Date photoDate;
    private double photoLatitude;
    private double photoLongitude;
    private Bitmap bitmap;
    private String address;

    public Photo() {
    }

    public Photo(Bitmap bitmap, Date photoDate, String address) {
        this.bitmap = bitmap;
        this.photoDate = photoDate;
        this.address = address;
    }

    public int getContentDetailId() {
        return contentDetailId;
    }
    public void setContentDetailId(int contentDetailId) {
        this.contentDetailId = contentDetailId;
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

    public String getPhotoFileName() {
        return photoFileName;
    }
    public void setPhotoFileName(String photoFileName) {
        this.photoFileName = photoFileName;
    }

    public int getPhotoTopFlag() {
        return photoTopFlag;
    }
    public void setPhotoTopFlag(int photoTopFlag) {
        this.photoTopFlag = photoTopFlag;
    }

    public Date getPhotoDate() {
        return photoDate;
    }
    public void setPhotoDate(Date photoDate) {
        this.photoDate = photoDate;
    }

    public double getPhotoLatitude() {
        return photoLatitude;
    }
    public void setPhotoLatitude(double photoLatitude) {
        this.photoLatitude = photoLatitude;
    }

    public double getPhotoLongitude() {
        return photoLongitude;
    }
    public void setPhotoLongitude(double photoLongitude) {
        this.photoLongitude = photoLongitude;
    }

    // Photo 모델 복사
    public void CopyData(Photo param)
    {
        this.contentDetailId = param.getContentDetailId();
        this.photoFileName = param.getPhotoFileName();
        this.photoTopFlag = param.getPhotoTopFlag();
        this.photoDate = param.getPhotoDate();
        this.photoLatitude = param.getPhotoLatitude();
        this.photoLongitude = param.getPhotoLongitude();
    }

    @Override
    public int compareTo(@NonNull Photo comparePhoto) {
        Date compareDate = comparePhoto.getPhotoDate();
        //ascending order
        return this.photoDate.compareTo(compareDate);
    }

    @Override
    public String toString() {
        return "Photo [contentDetailId=" + contentDetailId + ", photoFileName=" + photoFileName + ", photoTopFlag="
                + photoTopFlag + ", photoDate=" + photoDate + ", photoLatitude=" + photoLatitude + ", photoLongitude="
                + photoLongitude + "]";
    }
}