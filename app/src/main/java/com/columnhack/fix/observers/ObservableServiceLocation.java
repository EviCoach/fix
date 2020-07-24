package com.columnhack.fix.observers;

import android.location.Location;

import java.util.Observable;

public class ObservableServiceLocation extends Observable {
    /*private double latitude;
    private double longitude;

    public void setLatitude(double latitude) {
        this.latitude = latitude;
        this.setChanged();
        this.notifyObservers(latitude);
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
        this.setChanged();
        this.notifyObservers();
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }*/
    private Location mLocation;

    public ObservableServiceLocation() {
    }

    public Location getLocation() {
        return mLocation;
    }

    public void setLocation(Location location) {
        mLocation = location;
        this.setChanged();
        this.notifyObservers(location);
    }
}
