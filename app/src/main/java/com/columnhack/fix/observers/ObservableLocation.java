package com.columnhack.fix.observers;

import android.location.Location;

import java.util.Observable;

public class ObservableLocation extends Observable {

    private Location mLocation;

    public ObservableLocation() {
    }

    public Location getLocation() {
        return mLocation;
    }

    public void setLocation(Location location) {
        mLocation = location;
        setChanged();
        notifyObservers();
    }
}
