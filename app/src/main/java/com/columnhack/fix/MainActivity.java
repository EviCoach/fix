package com.columnhack.fix;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.os.Bundle;
import android.view.Gravity;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        MyServicesFragment.onFragmentBtnSelected{

    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mActionBarToggle;
    private Toolbar mToolbar;
    private NavigationView mNavigationView;
    private FragmentManager mFragmentManager;
    private int mValuePassedDown;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        configureDrawerLayout();
        loadFragment(new ServicesFragment(), R.id.all_services_menu);
    }

    private void configureDrawerLayout() {
        mToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

        mDrawerLayout = findViewById(R.id.drawer_layout);
        mNavigationView = findViewById(R.id.navigation_view);

        mActionBarToggle = new ActionBarDrawerToggle(this, mDrawerLayout, mToolbar,
                R.string.open, R.string.close);
        mDrawerLayout.addDrawerListener(mActionBarToggle);
        mNavigationView.setNavigationItemSelectedListener(this);
        mActionBarToggle.setDrawerIndicatorEnabled(true);
        mActionBarToggle.setDrawerSlideAnimationEnabled(false);
        mActionBarToggle.syncState();

    }

    public void loadFragment(Fragment fragment, int menuItem){
        mFragmentManager = getSupportFragmentManager();
        mFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();

        mNavigationView.getMenu().findItem(menuItem).setChecked(true);

        // Close the drawer
        mDrawerLayout.closeDrawer(GravityCompat.START);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.all_services_menu:
                loadFragment(new ServicesFragment(), R.id.all_services_menu);
                return true;
            case R.id.my_services_menu:
                loadFragment(new MyServicesFragment(), R.id.my_services_menu);
                return true;
        }
        return true;
    }

    @Override
    public void onButtonSelected() {
        loadFragment(new ServicesFragment(), R.id.all_services_menu);
    }
}
