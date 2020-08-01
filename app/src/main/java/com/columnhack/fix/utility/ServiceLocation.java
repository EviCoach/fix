package com.columnhack.fix.utility;

import android.os.Parcel;
import android.os.Parcelable;

public class ServiceLocation implements Parcelable {
    private double latitude = 0.0;
    private double longitude = 0.0;

    public ServiceLocation(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public ServiceLocation() {
    }

    protected ServiceLocation(Parcel in) {
        latitude = in.readDouble();
        longitude = in.readDouble();
    }

    public static final Creator<ServiceLocation> CREATOR = new Creator<ServiceLocation>() {
        @Override
        public ServiceLocation createFromParcel(Parcel in) {
            return new ServiceLocation(in);
        }

        @Override
        public ServiceLocation[] newArray(int size) {
            return new ServiceLocation[size];
        }
    };

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeDouble(latitude);
        dest.writeDouble(longitude);
    }
}
