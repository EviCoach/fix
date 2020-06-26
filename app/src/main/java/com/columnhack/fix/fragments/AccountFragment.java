package com.columnhack.fix.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.columnhack.fix.R;
import com.columnhack.fix.activities.AuthActivity;
import com.columnhack.fix.activities.MyServices;
import com.google.firebase.auth.FirebaseAuth;

public class AccountFragment extends Fragment {

    FirebaseAuth mFirebaseAuth;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mFirebaseAuth = FirebaseAuth.getInstance();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_account, container, false);
        CardView myServicesCardView = view.findViewById(R.id.my_services_cardview);
        myServicesCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), MyServices.class));
            }
        });
        final Button logoutBtn = view.findViewById(R.id.logout_btn);
        logoutBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                logout();
                return;
            }
        });
        return view;
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
