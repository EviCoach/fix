package com.columnhack.fix.activities;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import com.columnhack.fix.R;
import com.columnhack.fix.fragments.ServiceDetailFragment;

public class ServiceDetailActivity extends AppCompatActivity {


    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_container);

        // Host the service detail fragment
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.single_fragment_container, new ServiceDetailFragment())
                .commit();
    }
}
