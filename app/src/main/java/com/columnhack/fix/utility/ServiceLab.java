package com.columnhack.fix.utility;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.location.LocationManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.location.LocationManagerCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.columnhack.fix.adapters.NearbyServicesRecyclerViewAdapter;
import com.columnhack.fix.database.QueryContractClass;
import com.columnhack.fix.database.QueryContractClass.RecentQuery;
import com.columnhack.fix.database.RecentQueryOpenHelper;
import com.columnhack.fix.fragments.RecentSuggestionsFragment;
import com.columnhack.fix.models.Service;
import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryDataEventListener;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ServiceLab {
    private static ServiceLab sServiceLab;
    private static Context sContext;

    private RecentQueryOpenHelper mDbOpenHelper;

    boolean servicesLoaded = false;
    int radius = 10;

    private Location mCurrentLocation = null;

    private List<Service> services = new ArrayList<>();

    private List<Service> nearbyServices = new ArrayList<>();
    private List<Service> myServices = new ArrayList<>();
    private List<Service> searchResult = new ArrayList<>();
    private List<Service> mContactedServices = new ArrayList<>();
    private List<Service> mViewedServices = new ArrayList<>();
    private RecentSuggestionsFragment mRecentFragment;

    public static ServiceLab getInstance(Context context) {
        if (sServiceLab == null)
            sServiceLab = new ServiceLab();
        sContext = context;

        return sServiceLab;
    }

    public boolean isLocationEnabled() {
        LocationManager locationManager = (LocationManager) sContext.getSystemService(Context.LOCATION_SERVICE);
        return LocationManagerCompat.isLocationEnabled(locationManager);
    }

    public Cursor getRecentQueries() {
        mDbOpenHelper = new RecentQueryOpenHelper(sContext);
        SQLiteDatabase db = mDbOpenHelper.getReadableDatabase();
        String[] queryColumn = {RecentQuery.COLUMN_QUERY_STRING};
        return db.query(RecentQuery.TABLE_NAME,
                queryColumn,
                null, null, null, null, null);

    }

    public void closedDB() {
        mDbOpenHelper.close();
    }

    public Location getCurrentLocation() {
        return mCurrentLocation;
    }

    public void setCurrentLocation(Location currentLocation) {
        mCurrentLocation = currentLocation;
    }

    public List<Service> getServices(final RecyclerView.Adapter adapter) {
        services.clear();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        Query query = reference.child("services").orderByKey();
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot idSnapshot : snapshot.getChildren()) {
                        DataSnapshot serviceSnapshot = idSnapshot.child("service");

                        String id = serviceSnapshot.child("id").getValue(String.class);
                        String title = serviceSnapshot.child("title").getValue(String.class);
                        String phone = serviceSnapshot.child("phone").getValue(String.class);
                        String email = serviceSnapshot.child("email").getValue(String.class);
                        String contact_address = serviceSnapshot.child("contact_address").getValue(String.class);
                        String desc = serviceSnapshot.child("description").getValue(String.class);
                        Iterable<DataSnapshot> imgUrls = serviceSnapshot.child("img_urls").getChildren();
                        ServiceLocation location = serviceSnapshot.child("location").getValue(ServiceLocation.class);


                        Service service = new Service();

                        for (DataSnapshot data : imgUrls) {
                            service.getService_img_urls().add(data.getValue(String.class));
                        }

                        service.setId(id);
                        service.setTitle(title);
                        service.setLocation(location);
                        service.setPhone(phone);
                        service.setEmail(email);
                        service.setContact_address(contact_address);
                        service.setDescription(desc);

                        if (!services.contains(service)) {
                            services.add(service);
                        }
                    }
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        return services;
    } // ends getServices


    public List<Service> getNearbyServices(final NearbyServicesRecyclerViewAdapter adapter, String query, final GoogleMap map) {
        nearbyServices.clear();
        /**
         *1.Make the geofire request for the nearby services
         * get a list of first twenty services close by
         *
         * 2.This page will also serve as the search activity/fragment
         * -get the query text from the search input
         * -query the database for nearby services with the given text
         * -in the title and description
         */
        final int maxNearbyServices = 20;
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference()
                .child("services");
        GeoFire geoFire = new GeoFire(dbRef);
        final GeoQuery geoQuery = geoFire.queryAtLocation(new GeoLocation(getCurrentLocation().getLatitude(),
                getCurrentLocation().getLongitude()), radius);
        geoQuery.removeAllListeners();

        geoQuery.addGeoQueryDataEventListener(new GeoQueryDataEventListener() {
            int serviceCounter = 0;

            @Override
            public void onDataEntered(DataSnapshot dataSnapshot, GeoLocation location) {

//                if (nearbyServices.size() < 10 && radius <= 50) {
//                    Toast.makeText(sContext, "Service Found", Toast.LENGTH_SHORT).show();
                Service service = getEachService(dataSnapshot);
                nearbyServices.add(service);


                LatLng latLng = new LatLng(service.getLocation().getLatitude(),
                        service.getLocation().getLongitude());
                if (map != null) {
                    map.addMarker(new MarkerOptions()
                            .position(latLng)
                            .title(service.getTitle())
                            .snippet(service.getPhone())
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
                }
                serviceCounter++;
            }
//            }

            @Override
            public void onDataExited(DataSnapshot dataSnapshot) {
                // ...
            }

            @Override
            public void onDataMoved(DataSnapshot dataSnapshot, GeoLocation location) {
                // ...
            }

            @Override
            public void onDataChanged(DataSnapshot dataSnapshot, GeoLocation location) {
                // ...
            }

            @Override
            public void onGeoQueryReady() {
                servicesLoaded = true;
                adapter.changeServices(nearbyServices);
                geoQuery.removeGeoQueryEventListener(this);
            }

            @Override
            public void onGeoQueryError(DatabaseError error) {
                // ...
            }

        });
        return nearbyServices;
    }

    private Service getEachService(DataSnapshot snapshot) {
        DataSnapshot serviceSnapshot = snapshot.child("service");

        Service service = getSingleService(serviceSnapshot);

        return service;
    }

    private Service getSingleService(DataSnapshot serviceSnapshot) {
        Service service = new Service();
        String id = serviceSnapshot.child("id").getValue(String.class);
        String title = serviceSnapshot.child("title").getValue(String.class);
        String phone = serviceSnapshot.child("phone").getValue(String.class);
        String email = serviceSnapshot.child("email").getValue(String.class);
        String contact_address = serviceSnapshot.child("contact_address").getValue(String.class);
        String desc = serviceSnapshot.child("description").getValue(String.class);
        String tags = serviceSnapshot.child("tags").getValue(String.class);
        ServiceLocation location = serviceSnapshot.child("location").getValue(ServiceLocation.class);
        Iterable<DataSnapshot> imgUrls = serviceSnapshot.child("img_urls").getChildren();


        for (DataSnapshot data : imgUrls) {
            service.getService_img_urls().add(data.getValue(String.class));
        }

        service.setId(id);
        service.setTitle(title);
        service.setPhone(phone);
        service.setLocation(location);
        service.setEmail(email);
        service.setTags(tags);
        service.setContact_address(contact_address);
        service.setDescription(desc);
        return service;
    }

    public File getPhotoFile() {
        File filesDir = sContext.getFilesDir();
        return new File(filesDir, getPhotoFilename());
    }

    public String getPhotoFilename() {
        return "IMG_" + UUID.randomUUID() + ".jpg";
    }

    // Not working properly for now
    public List<Service> getMyServices(final RecyclerView.Adapter adapter) {
        myServices.clear();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        Query query = reference.child("services").orderByChild("service/owner_id")
                .equalTo(FirebaseAuth.getInstance().getCurrentUser().getUid());
        MyServicesListener listener = new MyServicesListener(adapter);
        query.removeEventListener(listener);
        query.addValueEventListener(listener);
        return myServices;
    }

    public void saveQueryString(String queryString) {
        mDbOpenHelper = new RecentQueryOpenHelper(sContext);
        SQLiteDatabase db = mDbOpenHelper.getReadableDatabase();
        String[] queryColumn = {RecentQuery.COLUMN_QUERY_STRING};
        ContentValues value = new ContentValues();
        value.put(RecentQuery.COLUMN_QUERY_STRING, queryString);
        db.insert(RecentQuery.TABLE_NAME, null, value);
    }

    public void search(final String query, final NearbyServicesRecyclerViewAdapter adapter, final GoogleMap map) {
        map.clear();
        searchResult.clear();
        /**
         *1.Make the geofire request for the nearby services
         * get a list of first twenty services close by
         *
         * 2.This page will also serve as the search activity/fragment
         * -get the query text from the search input
         * -query the database for nearby services with the given text
         * -in the title and description
         */
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference()
                .child("services");
        GeoFire geoFire = new GeoFire(dbRef);
        final GeoQuery geoQuery = geoFire.queryAtLocation(new GeoLocation(getCurrentLocation().getLatitude(),
                getCurrentLocation().getLongitude()), radius);
        geoQuery.removeAllListeners();

        geoQuery.addGeoQueryDataEventListener(new GeoQueryDataEventListener() {

            @Override
            public void onDataEntered(DataSnapshot dataSnapshot, GeoLocation location) {

                Service service = getEachService(dataSnapshot);

                if (service.getTags() != null && service.getTags().toLowerCase().contains(query.toLowerCase())) {

                    LatLng latLng = new LatLng(service.getLocation().getLatitude(),
                            service.getLocation().getLongitude());

                    map.addMarker(new MarkerOptions()
                            .position(latLng)
                            .title(service.getTitle())
                            .snippet(service.getPhone())
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
                    searchResult.add(service);
                }
            }

            @Override
            public void onDataExited(DataSnapshot dataSnapshot) {
                // ...
            }

            @Override
            public void onDataMoved(DataSnapshot dataSnapshot, GeoLocation location) {
                // ...
            }

            @Override
            public void onDataChanged(DataSnapshot dataSnapshot, GeoLocation location) {
                // ...
            }

            @Override
            public void onGeoQueryReady() {
                adapter.changeServices(searchResult);
                geoQuery.removeGeoQueryEventListener(this);
            }

            @Override
            public void onGeoQueryError(DatabaseError error) {
                // ...
            }
        });
    }

    public void getContactedSerivces(final NearbyServicesRecyclerViewAdapter adapter) {
        mContactedServices.clear();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();

        Query contactedServicesQuery = reference.child("services_interactions")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child("contacted_services").orderByKey();

        contactedServicesQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Service service = getSingleService(dataSnapshot);
                    mContactedServices.add(service);
                }
                // change the adapter data
                // And notify dataset changed
                adapter.changeServices(mContactedServices);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void getViewedSerivces(final NearbyServicesRecyclerViewAdapter adapter) {
        mViewedServices.clear();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();

        Query viewedServicesQuery = reference.child("services_interactions")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child("viewed_services").orderByKey();

        viewedServicesQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Service service = getSingleService(dataSnapshot);
                    mViewedServices.add(service);
                }
                adapter.changeServices(mViewedServices);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void setRecentSuggestionFragment(RecentSuggestionsFragment recentSuggestionsFragment) {
        mRecentFragment = recentSuggestionsFragment;
    }

    public void changeRecentQueries(List<String> filteredList) {
        mRecentFragment.getAdapter().changeRecentQueries(filteredList);
    }

    class MyServicesListener implements ValueEventListener {

        RecyclerView.Adapter adapter;

        public MyServicesListener(RecyclerView.Adapter adapter) {
            this.adapter = adapter;
        }

        @Override
        public void onDataChange(@NonNull DataSnapshot snapshot) {
            if (snapshot.exists()) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    DataSnapshot serviceSnapshot = dataSnapshot.child("service");

//                    if (serviceSnapshot.child("owner_id").getValue().toString()
//                            .equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {

                    String id = serviceSnapshot.child("id").getValue().toString();
                    String title = serviceSnapshot.child("title").getValue().toString();
                    String phone = serviceSnapshot.child("phone").getValue().toString();
                    String email = serviceSnapshot.child("email").getValue().toString();
                    String contact_address = serviceSnapshot.child("contact_address").getValue().toString();
                    String desc = serviceSnapshot.child("description").getValue().toString();

                    Service service = new Service();
                    service.setId(id);
                    service.setTitle(title);
                    service.setPhone(phone);
                    service.setEmail(email);
                    service.setContact_address(contact_address);
                    service.setDescription(desc);

                    if (!myServices.contains(service)) {
                        myServices.add(service);
                    }
//                    } // ends if statement
                }
                adapter.notifyDataSetChanged();
            }
        }

        @Override
        public void onCancelled(@NonNull DatabaseError error) {
        }
    }
}
