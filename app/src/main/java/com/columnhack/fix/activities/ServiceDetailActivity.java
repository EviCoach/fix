package com.columnhack.fix.activities;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import com.columnhack.fix.R;
import com.columnhack.fix.fragments.ServiceDetailFragment;
import com.columnhack.fix.models.Service;

public class ServiceDetailActivity extends AppCompatActivity {
    public static final String SELECTED_SERVICE = "selected_service";
    Service mService;


    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_container);
        mService = getIntent().getParcelableExtra(SELECTED_SERVICE);

        // Host the service detail fragment
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.single_fragment_container, ServiceDetailFragment.getInstance(mService))
                .commit();
    }
}
