package com.kitri.vo;

import android.graphics.Bitmap;
import android.support.annotation.NonNull;

import java.util.Date;

public class Photo implements Comparable<Photo> {

    private Integer contentDetailId;

    private String photoFileName;

    private Integer photoTopFlag;

    private Date photoDate;

    private Double photoLatitude;

    private Double photoLongitude;

    private Bitmap bitmap;

    private String address;

    public Photo() {
    }

    public Photo(Bitmap bitmap, Date photoDate, String address) {
        this.bitmap = bitmap;
        this.photoDate = photoDate;
        this.address = address;
    }

    public Integer getContentDetailId() {
        return contentDetailId;
    }

    public void setContentDetailId(Integer contentDetailId) {
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

    public Integer getPhotoTopFlag() {
        return photoTopFlag;
    }

    public void setPhotoTopFlag(Integer photoTopFlag) {
        this.photoTopFlag = photoTopFlag;
    }

    public Date getPhotoDate() {
        return photoDate;
    }

    public void setPhotoDate(Date photoDate) {
        this.photoDate = photoDate;
    }

    public Double getPhotoLatitude() {
        return photoLatitude;
    }

    public void setPhotoLatitude(Double photoLatitude) {
        this.photoLatitude = photoLatitude;
    }

    public Double getPhotoLongitude() {
        return photoLongitude;
    }

    public void setPhotoLongitude(Double photoLongitude) {
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