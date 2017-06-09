package com.kitri.vo;

import android.graphics.Bitmap;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;

/**
 * Created by Junki on 2017-05-31.
 */
public class Photo implements ClusterItem {
    private String name;
    private Bitmap photo;
    private LatLng position;
    private int rank;


    public Photo(LatLng position, String name, Bitmap pictureResource, int rank) {
        this.position = position;
        this.name = name;
        photo = pictureResource;
        this.rank=rank;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Bitmap getPhoto() {
        return photo;
    }

    public void setPhoto(Bitmap photo) {
        this.photo = photo;
    }

    @Override
    public LatLng getPosition() {
        return position;
    }


    public void setRank(int rank) {
        this.rank = rank;
    }

    public int getRank() {
        return rank;
    }

    @Override
    public String toString() {
        return getName()+getPhoto()+getPosition()+getRank()+"";
    }
}
