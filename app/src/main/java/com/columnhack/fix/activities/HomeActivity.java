package com.columnhack.fix.activities;

import android.app.Activity;
import android.content.Context;
import android.content.res.ColorStateList;
import android.database.Cursor;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;
import androidx.viewpager.widget.ViewPager;

import com.columnhack.fix.R;
import com.columnhack.fix.adapters.NearbyServicesRecyclerViewAdapter;
import com.columnhack.fix.adapters.ServicePagerAdapter;
import com.columnhack.fix.database.QueryContractClass;
import com.columnhack.fix.fragments.ContactedAndViewedFragment;
import com.columnhack.fix.fragments.RecentSuggestionsFragment;
import com.columnhack.fix.interfaces.ChangeFragment;
import com.columnhack.fix.interfaces.QueryTextSetters;
import com.columnhack.fix.models.Service;
import com.columnhack.fix.utility.ServiceLab;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<Cursor>, QueryTextSetters, ChangeFragment {

    public static final int LOADER_GET_QUERY_STRING = 210;
    public static final int LOADER_SAVE_QUERY_STRING = 220;
    public static final String QUERY_STRING = "query_string";
    private ViewPager mServiceViewPager;
    private BottomNavigationView mBottomNavigationView;
    private FragmentManager mFm;
    private RecentSuggestionsFragment mRecentSuggestionsFragment;
    private SearchView mSearchView;
    private MenuItem mBackBtn;
    private List<String> mRecentQueries = new ArrayList<>();
    private Cursor mQueryCursor;
    private List<String> mFilteredList = new ArrayList<>();
    private ServicePagerAdapter mServicePagerAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        mBottomNavigationView = findViewById(R.id.bottom_navigation);

        mServiceViewPager = findViewById(R.id.service_view_pager);
        mServicePagerAdapter = new ServicePagerAdapter(getSupportFragmentManager(),
                FragmentStatePagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        mServiceViewPager.setAdapter(mServicePagerAdapter);
        mFm = getSupportFragmentManager();

        mServiceViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                switch (position) {
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
                        switch (item.getItemId()) {
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

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private void setActiveFragmentColor() {
        mBottomNavigationView.setItemTextColor(ColorStateList.valueOf(Color.WHITE));
        mBottomNavigationView.setItemIconTintList(ColorStateList.valueOf(Color.WHITE));
    }

    public void hideKeyboardFrom(Context context, View view) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        View view = activity.getCurrentFocus();
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.home_search_menu, menu);
        final MenuItem searchItem = menu.findItem(R.id.action_search_service);
        mSearchView = (SearchView) searchItem.getActionView();
//        mSearchView.setQuery("Put here by code", false);
        mBackBtn = menu.findItem(R.id.action_back);
        mBackBtn.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                mBackBtn.setVisible(false);
                mSearchView.clearFocus();
                mSearchView.setIconified(true); // This clears the text
                searchItem.collapseActionView();
                mSearchView.setIconified(true); // This iconify the view
                closeSuggestionFragment();
                return true;
            }
        });

        mSearchView.setOnSearchClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBackBtn.setVisible(true);
                LoaderManager.getInstance(HomeActivity.this)
                        .restartLoader(LOADER_GET_QUERY_STRING, null, HomeActivity.this);
            }
        });

        mSearchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                searchItem.collapseActionView();
                mBackBtn.setVisible(false);
                closeSuggestionFragment();
                return false;
            }
        });

        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Bundle bundle = new Bundle();
                bundle.putString(QUERY_STRING, query);
                // Persist the query string
                AsyncTask<String, Void, Void> task = new AsyncTask<String, Void, Void>() {

                    @Override
                    protected Void doInBackground(String... strings) {
                        ServiceLab.getInstance(HomeActivity.this).saveQueryString(strings[0]);
                        return null;
                    }
                };
                task.execute(query);
                // close the suggestion fragment
                closeSuggestionFragment();
                hideKeyboard(HomeActivity.this);
                displayNearbyServicesFragment();


                /**
                 * Query fire base for the nearby services with the query string
                 * 1. query geofire for nearby services
                 * 2. search for services that contains the query string
                 * in their title or description
                 * 3. return those services
                 */
                NearbyServicesRecyclerViewAdapter nearbyServicesRecyclerViewAdapter =
                        mServicePagerAdapter.getNearbyFragment().getAdapter();
                GoogleMap googleMap = mServicePagerAdapter.getNearbyFragment().getGoogleMap();
                ServiceLab.getInstance(HomeActivity.this).search(query, nearbyServicesRecyclerViewAdapter, googleMap);

                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // Filter the recent queries as the user type
                mFilteredList.clear();

                for (int i = 0; i < mRecentQueries.size(); i++) {
                    if (mRecentQueries.get(i).toLowerCase().contains(newText.toLowerCase())) {
                        // put this recent query in the new List
                        mFilteredList.add(mRecentQueries.get(i));
                    }
                    ServiceLab.getInstance(HomeActivity.this).changeRecentQueries(mFilteredList);
                }
                return false;
            }
        });

        return true;
    }

    private void openRecentQueriesFragment() {
        // open the recent queries fragment
        mRecentSuggestionsFragment = RecentSuggestionsFragment.newInstance(mRecentQueries);
        ServiceLab.getInstance(this).setRecentSuggestionFragment(mRecentSuggestionsFragment);
        mFm.beginTransaction()
                .add(R.id.search_suggestion_frg_container, mRecentSuggestionsFragment)
                .addToBackStack("recent_fragment")
                .commit();
    }

    private void displayNearbyServicesFragment() {
        mServiceViewPager.setCurrentItem(Service.NEAR_BY_SERVICES);
    }

    private void closeSuggestionFragment() {
        Fragment fragment = mFm.findFragmentById(R.id.search_suggestion_frg_container);
        if (fragment != null) {
            mFm.beginTransaction().remove(fragment)
                    .commit();
        }
    }

    @Override
    public void onBackPressed() {
        if (!mSearchView.isIconified()) {
            mSearchView.setIconified(true);
            closeSuggestionFragment();
        } else {
            super.onBackPressed();
        }
    }


    // LoaderMangar callbacks

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle args) {
        CursorLoader loader = null;
        if (id == LOADER_GET_QUERY_STRING) {
            loader = createLoaderQuery();
        }
        return loader;
    }

    private CursorLoader createLoaderQuery() {
        return new CursorLoader(this) {
            @Override
            public Cursor loadInBackground() {
                // returns a cursor
                return ServiceLab.getInstance(HomeActivity.this).getRecentQueries();
            }
        };
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor data) {
        if (loader.getId() == LOADER_GET_QUERY_STRING) {
            loadFinishedQueries(data);
        }
    }

    private void loadFinishedQueries(Cursor data) {
        mRecentQueries.clear();
        mQueryCursor = data;
        while (data.moveToNext()) {
            int columnIndex = data.getColumnIndex(QueryContractClass.RecentQuery.COLUMN_QUERY_STRING);
            String queryString = data.getString(columnIndex);
            mRecentQueries.add(queryString);
        }
        openRecentQueriesFragment();
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
        if (loader.getId() == LOADER_GET_QUERY_STRING) {
            if (mQueryCursor != null) {
                mQueryCursor.close();
            }
        }
    }


    @Override
    public void setQueryText(String text) {
        mSearchView.setIconified(false);
        mSearchView.setQuery(text, false);

    }

    @Override
    public void setAndQueryText(String text) {
        mSearchView.setQuery(text, true);
    }

    private void startInteractedServicesFragment(String whichServices) {
        ContactedAndViewedFragment fragment = ContactedAndViewedFragment.newInstance(whichServices);
        FragmentManager fm = getSupportFragmentManager();
        fm.popBackStack("contacted_recent_viewed", FragmentManager.POP_BACK_STACK_INCLUSIVE);
        fm.beginTransaction().replace(R.id.search_suggestion_frg_container, fragment)
                .addToBackStack("contacted_recent_viewed")
                .commit();
    }

    @Override
    public void showContacted() {
        startInteractedServicesFragment(Service.CONTACTED);
    }

    @Override
    public void showViewed() {
        startInteractedServicesFragment(Service.VIEWED);
    }

    @Override
    public void showRecent() {
        mRecentQueries.clear();
        Cursor recentQueries = ServiceLab.getInstance(this).getRecentQueries();
        while (recentQueries.moveToNext()) {
            int columnIndex = recentQueries.getColumnIndex(QueryContractClass.RecentQuery.COLUMN_QUERY_STRING);
            String queryString = recentQueries.getString(columnIndex);
            mRecentQueries.add(queryString);
        }
        RecentSuggestionsFragment recentSuggestionsFragment = RecentSuggestionsFragment
                .newInstance(mRecentQueries);
        ServiceLab.getInstance(this).setRecentSuggestionFragment(recentSuggestionsFragment);
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.search_suggestion_frg_container, recentSuggestionsFragment)
                .addToBackStack("contacted_recent_viewed")
                .commit();

    }
}
