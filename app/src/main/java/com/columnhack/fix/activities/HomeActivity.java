package com.columnhack.fix.activities;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.columnhack.fix.R;
import com.columnhack.fix.adapters.ServicePagerAdapter;
import com.columnhack.fix.models.Service;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class HomeActivity extends AppCompatActivity {

    private ViewPager mServiceViewPager;
    private BottomNavigationView mBottomNavigationView;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        mBottomNavigationView = findViewById(R.id.bottom_navigation);

        mServiceViewPager = findViewById(R.id.service_view_pager);
        ServicePagerAdapter servicePagerAdapter =
                new ServicePagerAdapter(getSupportFragmentManager(),
                        FragmentStatePagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        mServiceViewPager.setAdapter(servicePagerAdapter);

        mServiceViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener(){

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                // TODO: change colors of selected views to make them stand out
                switch (position){
                    case Service.ALL_SERVICES:
                        mBottomNavigationView.getMenu().findItem(R.id.action_all_services)
                                .setChecked(true);
                        break;
                    case Service.NEAR_BY_SERVICES:
                        mBottomNavigationView.getMenu().findItem(R.id.action_nearby_services)
                                .setChecked(true);

                        break;
                    case Service.MY_ACCOUNT:
                        mBottomNavigationView.getMenu().findItem(R.id.action_my_account)
                                .setChecked(true);
                        break;
                    case Service.HELP:
                        mBottomNavigationView.getMenu().findItem(R.id.action_help)
                                .setChecked(true);
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        mBottomNavigationView.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        switch (item.getItemId()){
                            case R.id.action_all_services:
                                mServiceViewPager.setCurrentItem(Service.ALL_SERVICES);
                                break;
                            case R.id.action_nearby_services:
                                mServiceViewPager.setCurrentItem(Service.NEAR_BY_SERVICES);
                                break;
                            case R.id.action_my_account:
                                mServiceViewPager.setCurrentItem(Service.MY_ACCOUNT);
                                break;
                            case R.id.action_help:
                                mServiceViewPager.setCurrentItem(Service.HELP);
                        }
                        return true;
                    }
                }
        );
    }

    private void setActiveFragmentColor() {
        mBottomNavigationView.setItemTextColor(ColorStateList.valueOf(Color.WHITE));
        mBottomNavigationView.setItemIconTintList(ColorStateList.valueOf(Color.WHITE));
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.home_menu, menu);
        MenuItem searchItem = menu.findItem(R.id.action_search_service);
        SearchView searchView = (SearchView) searchItem.getActionView();

        return true;
    }
}
