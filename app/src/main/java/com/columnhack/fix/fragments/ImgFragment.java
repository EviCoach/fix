package com.columnhack.fix.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.columnhack.fix.R;

public class ImgFragment extends Fragment {
    public static final String SERVICE_IMG = "service_img";

    // var
    private int service_img;

    // widget
    private ImageView serviceImgView;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        service_img = getArguments().getInt(SERVICE_IMG);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.service_img_framgment, container, false);

        serviceImgView = view.findViewById(R.id.service_img_view);
        serviceImgView.setImageResource(service_img);
        return view;
    }
}
