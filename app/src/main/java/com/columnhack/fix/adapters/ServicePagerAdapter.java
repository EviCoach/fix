package com.columnhack.fix.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.columnhack.fix.fragments.MyServicesFragment;
import com.columnhack.fix.fragments.NearByServicesFragment;
import com.columnhack.fix.models.Service;
import com.columnhack.fix.fragments.ServicesFragment;

public class ServicePagerAdapter extends FragmentStatePagerAdapter {



    public ServicePagerAdapter(@NonNull FragmentManager fm, int behavior) {
        super(fm, behavior);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch(position){
            case Service.ALL_SERVICES:
             return new ServicesFragment();
            case Service.NEAR_BY_SERVICES:
                return new NearByServicesFragment();
            case Service.MY_SERVICES:
                return new MyServicesFragment();
        }
        return null;
    }

    @Override
    public int getCount() {
        return 3;
    }
}
