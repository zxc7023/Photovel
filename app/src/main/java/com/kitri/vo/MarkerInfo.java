package com.kitri.vo;

import android.graphics.Bitmap;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;

/**
 * Created by Junki on 2017-05-31.
 */

public class MarkerInfo implements ClusterItem {
    public final String name;
    public final Bitmap photo;
    private final LatLng mPosition;
    public final int rank;

    public MarkerInfo(LatLng position, String name, Bitmap pictureResource, int rank) {
        mPosition = position;
        this.name = name;
        photo = pictureResource;
        this.rank=rank;
    }

    public int getRank() {
        return rank;
    }

    @Override
    public LatLng getPosition() {
        return mPosition;
    }

    public String getName() {
        return name;
    }

    public Bitmap getPhoto() {
        return photo;
    }

    @Override
    public String toString() {
        return getName()+getPhoto()+getPosition()+getRank()+"";
    }
}
