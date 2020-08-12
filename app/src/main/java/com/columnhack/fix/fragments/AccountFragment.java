package com.columnhack.fix.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.columnhack.fix.R;
import com.columnhack.fix.activities.AuthActivity;
import com.columnhack.fix.activities.MyServicesActivity;
import com.columnhack.fix.interfaces.ChangeFragment;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;

public class AccountFragment extends Fragment {

    String userEmail;
    String userDisplayName;
    private FirebaseAuth mFirebaseAuth;
    private CardView mMyServicesCardView;
    private CardView mViewedServicesCardView;
    private CardView mContactedServicesCardView;
    private CardView mRecentSearch;
    private TextView mUserDisplayName;
    private TextView mUserEmail;
    private List<String> mRecentQueries = new ArrayList<>();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mFirebaseAuth = FirebaseAuth.getInstance();
        userEmail = mFirebaseAuth.getCurrentUser().getEmail();
        userDisplayName = mFirebaseAuth.getCurrentUser().getDisplayName();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_account, container, false);
        mUserDisplayName = view.findViewById(R.id.user_display_name);
        mUserEmail = view.findViewById(R.id.user_email_view);

        mUserEmail.setText(userEmail);
        mUserDisplayName.setText(getDisplayName());
        mMyServicesCardView = view.findViewById(R.id.my_services_cardview);
        mViewedServicesCardView = view.findViewById(R.id.viewed_services_cardview);
        mContactedServicesCardView = view.findViewById(R.id.contacted_services_cardview);
        mRecentSearch = view.findViewById(R.id.recent_search_cardview);
        mContactedServicesCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(getActivity() instanceof ChangeFragment){
                    ((ChangeFragment) getActivity()).showContacted();
                }
            }
        });
        mRecentSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(getActivity() instanceof ChangeFragment){
                    ((ChangeFragment) getActivity()).showRecent();
                }
            }
        });
        mViewedServicesCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(getActivity() instanceof ChangeFragment){
                    ((ChangeFragment) getActivity()).showViewed();
                }
            }
        });
        mMyServicesCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), MyServicesActivity.class));
            }
        });
        final Button logoutBtn = view.findViewById(R.id.logout_btn);
        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logout();
                return;
            }
        });
        return view;
    }



    private String getDisplayName() {
        String diplayName =  "";
        if(userDisplayName == null || userDisplayName.isEmpty()){
            String firstLetter = userEmail.substring(0,1);
            String otherPart = userEmail.substring(1, userEmail.indexOf("@"));
            diplayName = firstLetter.toUpperCase() + otherPart;
        } else {
            diplayName = userEmail;
        }
        return diplayName;
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    private void logout() {
        mFirebaseAuth.signOut();
        Intent logoutIntent = new Intent(getActivity(), AuthActivity.class);
        logoutIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(logoutIntent);
        getActivity().finish();
        return;
    }
}
