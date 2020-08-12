package com.columnhack.fix.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.columnhack.fix.R;
import com.columnhack.fix.utility.ServiceLab;
import com.columnhack.fix.adapters.MyServicesRecyclerViewAdapter;
import com.columnhack.fix.models.Service;
import com.columnhack.fix.touchhelpers.MyServicesItemTouchHelperCallback;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class MyServicesActivity extends AppCompatActivity {

    /*widgets*/
    SwipeRefreshLayout mSwipeRefreshLayout;
    RecyclerView mRecyclerView;
    FloatingActionButton mAddNewServiceFab;

    /*vars*/
    private MyServicesRecyclerViewAdapter mAdapter;
    private List<Service> mMyServices = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_services);

        forceRefreshUI();

        mAddNewServiceFab = findViewById(R.id.add_new_service);
        mAddNewServiceFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MyServicesActivity.this, AddServiceActivity.class);
                startActivity(intent);
            }
        });
        mSwipeRefreshLayout = findViewById(R.id.swipe_refresh_my_services);
        mRecyclerView = findViewById(R.id.my_services_recyclerview);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));



        MyServicesItemTouchHelperCallback callback = new MyServicesItemTouchHelperCallback(mAdapter);
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(callback);
        mAdapter.setTouchHelper(itemTouchHelper);
        itemTouchHelper.attachToRecyclerView(mRecyclerView);

        mRecyclerView.setAdapter(mAdapter);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mSwipeRefreshLayout.setRefreshing(true);
                mAdapter.refreshServices();
                mSwipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    private void forceRefreshUI() {
        mAdapter = new MyServicesRecyclerViewAdapter(this, mMyServices);
        mMyServices = ServiceLab.getInstance(this).getMyServices(mAdapter);
        mAdapter.refreshServices();
    }
}