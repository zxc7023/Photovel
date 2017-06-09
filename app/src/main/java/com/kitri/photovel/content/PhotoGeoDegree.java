package com.kitri.photovel.content;

import android.media.ExifInterface;

/**
 * Created by EunD on 2017-05-29.
 */

public class PhotoGeoDegree {
    private boolean valid = false;
    private Double latitude, longitude;

    public PhotoGeoDegree(ExifInterface exif) {
        String attrLATITUDE = exif.getAttribute(ExifInterface.TAG_GPS_LATITUDE);
        String attrLATITUDE_REF = exif.getAttribute(ExifInterface.TAG_GPS_LATITUDE_REF);
        String attrLONGITUDE = exif.getAttribute(ExifInterface.TAG_GPS_LONGITUDE);
        String attrLONGITUDE_REF = exif.getAttribute(ExifInterface.TAG_GPS_LONGITUDE_REF);

        if ((attrLATITUDE != null) && (attrLATITUDE_REF != null) && (attrLONGITUDE != null)
                && (attrLONGITUDE_REF != null)) {
            valid = true;

            if (attrLATITUDE_REF.equals("N")) {
                latitude = convertToDegree(attrLATITUDE);
            } else {
                latitude = 0 - convertToDegree(attrLATITUDE);
            }

            if (attrLONGITUDE_REF.equals("E")) {
                longitude = convertToDegree(attrLONGITUDE);
            } else {
                longitude = 0 - convertToDegree(attrLONGITUDE);
            }
        }
    };

    private Double convertToDegree(String stringDMS) {
        Double result = null;
        String[] DMS = stringDMS.split(",", 3);

        String[] stringD = DMS[0].split("/", 2);
        Double D0 = new Double(stringD[0]);
        Double D1 = new Double(stringD[1]);
        Double FloatD = D0 / D1;

        String[] stringM = DMS[1].split("/", 2);
        Double M0 = new Double(stringM[0]);
        Double M1 = new Double(stringM[1]);
        Double FloatM = M0 / M1;

        String[] stringS = DMS[2].split("/", 2);
        Double S0 = new Double(stringS[0]);
        Double S1 = new Double(stringS[1]);
        Double FloatS = S0 / S1;

        result = new Double(FloatD + (FloatM / 60) + (FloatS / 3600));

        return result;

    };

    public boolean isValid() {
        return valid;
    }

    @Override
    public String toString() {
        return (String.valueOf(latitude) + ", " + String.valueOf(longitude));
    }

    public Double getLatitude() {
        return latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public int getLatitudeE6() {
        return (int) (latitude * 1000000);
    }

    public int getLongitudeE6() {
        return (int) (longitude * 1000000);
    }
}
