//package com.columnhack.fix.tasks;
//
//import android.content.Context;
//import android.os.AsyncTask;
//
//import androidx.recyclerview.widget.RecyclerView;
//
//import com.columnhack.fix.R;
//import com.columnhack.fix.adapters.NearbyServicesRecyclerViewAdapter;
//import com.columnhack.fix.models.Service;
//import com.columnhack.fix.utility.ServiceLab;
//import com.google.android.gms.maps.GoogleMap;
//import com.google.android.gms.maps.model.BitmapDescriptorFactory;
//import com.google.android.gms.maps.model.LatLng;
//import com.google.android.gms.maps.model.MarkerOptions;
//
//import java.util.List;
//
//public class AddMarkersAsync extends AsyncTask<Void, Void, List<Service>> {
//    Context mContext;
//    GoogleMap mGoogleMap;
//    NearbyServicesRecyclerViewAdapter mAdapter;
//
//    public AddMarkersAsync(Context context, GoogleMap map, NearbyServicesRecyclerViewAdapter adapter) {
//        mContext = context;
//        mGoogleMap = map;
//        mAdapter = adapter;
//    }
//
//    @Override
//    protected List<Service> doInBackground(Void... voids) {
//        /**
//         * Get the current list of nearby services
//         * add markers to the map for each service in the list
//         */
//
//        List<Service> loadedNearbyServices = ServiceLab.getInstance(mContext)
//                .getLoadedNearbyServices(mAdapter, mContext.getString(R.string.google_map_markers));
//        return loadedNearbyServices;
//    }
//
//    @Override
//    protected void onPostExecute(List<Service> services) {
//        super.onPostExecute(services);
//        for (Service service : services) {
//
//            LatLng ltln = new LatLng(service.getLocation().getLatitude(), service.getLocation().getLongitude());
//
//            mGoogleMap.addMarker(new MarkerOptions()
//                    .position(ltln)
//                    .title(service.getTitle())
//                    .snippet(service.getPhone())
//                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
//        }
//    }
//}
