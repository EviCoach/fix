package com.columnhack.fix.models;

import android.location.Location;
import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.Nullable;

import com.columnhack.fix.utility.ServiceLocation;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Service implements Parcelable {

    public static final int ALL_SERVICES = 0;
    public static final int NEAR_BY_SERVICES = 1;
    public static final int MY_ACCOUNT = 2;
    public static final int HELP = 3;
    public static final String SERVICE_ID = "service_id";
    public static final String IMAGE_URIS = "image_uris";

    private String id = "";
    private String owner_id = "";
    private String title = "";
    private String description = "";
    private String email = "";
    private String phone = "";
    private String contact_address = "";
    private ServiceLocation location = null;
    private List<String> service_img_urls = new ArrayList<>();

    public Service(String title, String description, String email, String phone, String contact_address,
                   ServiceLocation location, List<String> serviceImageUrls) {
        this.id = UUID.randomUUID().toString();
        this.title = title;
        this.description = description;
        this.email = email;
        this.phone = phone;
        this.contact_address = contact_address;
        this.location = location;
        this.service_img_urls = serviceImageUrls;
        this.owner_id = FirebaseAuth.getInstance().getCurrentUser().getUid();
    }

    public Service() {
        this.owner_id = FirebaseAuth.getInstance().getCurrentUser().getUid();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(title);
        dest.writeString(description);
        dest.writeString(email);
        dest.writeString(phone);
        dest.writeString(contact_address);
        dest.writeParcelable(location, flags);
        dest.writeStringList(service_img_urls);
        dest.writeString(owner_id);
    }

    protected Service(Parcel in) {
        id = in.readString();
        title = in.readString();
        description = in.readString();
        email = in.readString();
        phone = in.readString();
        contact_address = in.readString();
        location = in.readParcelable(ServiceLocation.class.getClassLoader());
        service_img_urls = in.createStringArrayList();
        owner_id = in.readString();
    }

    public void setId() {
        this.id = UUID.randomUUID().toString();
    }

    public void setId(String id){
        this.id = id;
    }

    public String getContact_address() {
        return contact_address;
    }

    public void setContact_address(String contact_address) {
        this.contact_address = contact_address;
    }

    public List<String> getService_img_urls() {
        return service_img_urls;
    }

    public void setService_img_urls(List<String> service_img_urls) {
        this.service_img_urls = service_img_urls;
    }

    public static final Creator<Service> CREATOR = new Creator<Service>() {
        @Override
        public Service createFromParcel(Parcel in) {
            return new Service(in);
        }

        @Override
        public Service[] newArray(int size) {
            return new Service[size];
        }
    };

    public String getId() {
        return id;
    }

    public static int getAllServices() {
        return ALL_SERVICES;
    }

    public String getOwner_id() {
        return owner_id;
    }

    public void setOwner_id(String owner_id) {
        this.owner_id = owner_id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public ServiceLocation getLocation() {
        return location;
    }

    public void setLocation(ServiceLocation location) {
        this.location = location;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        boolean isEqual = false;
        if(obj instanceof Service){
            Service thisObject = (Service) obj;
            isEqual = thisObject.id == this.id;
        }

        return isEqual;
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }
}
