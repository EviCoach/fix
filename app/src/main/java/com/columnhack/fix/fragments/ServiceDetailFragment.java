package com.columnhack.fix.fragments;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.columnhack.fix.R;
import com.columnhack.fix.utility.ServiceLab;
import com.columnhack.fix.adapters.ServiceImagesPagerAdapter;
import com.columnhack.fix.adapters.ServiceRecyclerViewAdapter;
import com.columnhack.fix.models.Service;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;
import static androidx.core.content.ContextCompat.checkSelfPermission;
import static androidx.fragment.app.FragmentStatePagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT;

public class ServiceDetailFragment extends Fragment {
    public static final String THIS_SERVICE = "this_service";
    public static final int PHONE_CALL_REQUEST_CODE = 110;
    public static final String SERVICE_VIEWED = "service_viewed";
    // vars
    private List<Service> mSimilarServices;
    private ArrayList<Fragment> imgFragments = new ArrayList<>();
    ServiceRecyclerViewAdapter mAdapter;
    private Service mService;
    private SharedPreferences mPrefs;

    // widgets
    private ViewPager servicesImageViewPager;
    private TabLayout serviceImgIndicatorTabs;
    private TextView serviceTitleView;
    private TextView serviceDesc;
    private ImageButton callServiceBtn;
    private ImageButton dmServiceBtn;
    private boolean mViewed;
    private boolean mContacted;
    private boolean mCallServiceCalled = false;
    private boolean executed = false;

    public static ServiceDetailFragment getInstance(Service service) {
        ServiceDetailFragment fragment = new ServiceDetailFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable(THIS_SERVICE, service);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        mPrefs = getActivity().getSharedPreferences("shared_prefs", MODE_PRIVATE);
        mService = getArguments().getParcelable(THIS_SERVICE);
        viewedService();
        // TODO: change to real similar and related services later
        mAdapter = new ServiceRecyclerViewAdapter(getActivity(), mSimilarServices);

        mSimilarServices = ServiceLab.getInstance(getActivity()).getServices(mAdapter);
        mAdapter.refreshServices();


        /**
         * Load  the service image urls into different fragments
         * that will be used in the serviesImageViewPager
         */
        for (String imgUrl : mService.getService_img_urls()) {
            Fragment imgFragment = new ImgFragment();
            Bundle args = new Bundle();
            args.putString(ImgFragment.SERVICE_IMG, imgUrl);
            imgFragment.setArguments(args);
            imgFragments.add(imgFragment);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    private void viewedService() {
        mViewed = mPrefs.getBoolean(mService.getId() + "viewed", false);

        if (!mViewed) {
            SharedPreferences.Editor edit = mPrefs.edit();
            edit.putBoolean(mService.getId() + "viewed", true)
                    .apply();

            String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
            DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference()
                    .child("services_interactions").child(uid).child("viewed_services");
            dbRef.push().setValue(mService);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_service_detail, container, false);

        RecyclerView similarServicesView = view.findViewById(R.id.similar_services_recycler_view);
        servicesImageViewPager = view.findViewById(R.id.service_images_view_pager);
        serviceImgIndicatorTabs = view.findViewById(R.id.service_image_indicator_tabs);
        serviceTitleView = view.findViewById(R.id.service_detail_title);
        serviceDesc = view.findViewById(R.id.service_desc_text);
        serviceTitleView.setText(mService.getTitle());
        callServiceBtn = view.findViewById(R.id.call_service);
        dmServiceBtn = view.findViewById(R.id.dm_service);
        dmServiceBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dmService();
            }
        });
        callServiceBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Check for permissions before making the call
                if (checkSelfPermission(getActivity(), Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
                    // Call the service
                    callService();
                } else {
                    // Request for permission
                    requestPermissions(new String[]{Manifest.permission.CALL_PHONE}, PHONE_CALL_REQUEST_CODE);
                }
            }
        });
        serviceDesc.setText(mService.getDescription());
        servicesImageViewPager.setAdapter(new ServiceImagesPagerAdapter(getActivity().getSupportFragmentManager(),
                BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT, imgFragments));
        serviceImgIndicatorTabs.setupWithViewPager(servicesImageViewPager, true);
        similarServicesView.setAdapter(mAdapter);
        similarServicesView.setLayoutManager(new LinearLayoutManager(getActivity(), RecyclerView.HORIZONTAL, false));

        return view;
    } // end of onCreateView

    private void dmService() {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra("address", "sms:" + mService.getPhone());
        sendIntent.putExtra(Intent.EXTRA_TEXT, "Contacting you  from  FixFind");
        sendIntent.setType("text/*");

        if (sendIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            startActivity(sendIntent);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PHONE_CALL_REQUEST_CODE) {
            if (permissions.length < 1) {
                throw new RuntimeException("no permissions on request result");
            }
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                callService();
            } else {
                if (shouldShowRequestPermissionRationale(Manifest.permission.CALL_PHONE)) {
                    // request for permission
                    showPermissionDialog();
                } else {
                    // user declined and can't ask any longer
                    Toast.makeText(getActivity(), "Call permissions not granted", Toast.LENGTH_SHORT)
                            .show();
                }
            }
        }
    }

    private void showPermissionDialog() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
        dialog.setTitle("Call Permission Needed");
        dialog.setCancelable(false);
        dialog.setMessage("FixFind requires permission to call this service provider");
        dialog.setPositiveButton("Okay", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                requestPermissions(new String[]{Manifest.permission.CALL_PHONE}, PHONE_CALL_REQUEST_CODE);
                dialog.dismiss();
            }
        });
        dialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        dialog.show();
    }

    private void callService() {
        addToContactedServices();

        Uri serviceNumber = Uri.parse("tel:" + mService.getPhone());
        Intent intent = new Intent(Intent.ACTION_CALL);
        intent.setData(serviceNumber);
        startActivity(intent);
    }

    private void addToContactedServices() {
        //check if callService() has been called
        mContacted = mPrefs.getBoolean(mService.getId() + "contacted", false);

        if (!mContacted) {

            SharedPreferences.Editor edit = mPrefs.edit();
            edit.putBoolean(mService.getId() + "contacted", true)
                    .apply();

            DatabaseReference ref = FirebaseDatabase.getInstance().getReference()
                    .child("services_interactions").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                    .child("contacted_services");
            ref.push().setValue(mService);
        }
    }
}
