package com.columnhack.fix;

import android.location.Location;
import android.media.Image;

import java.util.UUID;

public class Service {

    public static final int ALL_SERVICES = 0;
    public static final int NEAR_BY_SERVICES = 1;
    public static final int MY_SERVICES = 2;

    private UUID id;
    private String mTitle;
    private String mDescription;
    private String mContactAddress;
    private Location mLocation;
    private String[] mServiceImageUrls;
    private ServicePerson mServicePerson;

    public Service(){
        this.id = UUID.randomUUID();
    }

    public UUID getId() {
        return id;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public String getDescription() {
        return mDescription;
    }

    public void setDescription(String description) {
        mDescription = description;
    }

    public String getContactAddress() {
        return mContactAddress;
    }

    public void setContactAddress(String contactAddress) {
        mContactAddress = contactAddress;
    }

    public Location getLocation() {
        return mLocation;
    }

    public String[] getServiceImageUrls() {
        return mServiceImageUrls;
    }

    public void setServiceImageUrls(String[] serviceImageUrls) {
        mServiceImageUrls = serviceImageUrls;
    }

    public void setLocation(Location location) {
        mLocation = location;
    }

    public ServicePerson getServicePerson() {
        return mServicePerson;
    }

    public void setServicePerson(ServicePerson servicePerson) {
        mServicePerson = servicePerson;
    }
}
