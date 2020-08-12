package com.columnhack.fix.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.columnhack.fix.R;
import com.columnhack.fix.utility.PictureUtils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class SelectedImgsAdapter extends RecyclerView.Adapter<SelectedImgsAdapter.SelectedImgHolder> {

    Context mContext;
    List<Uri> mImgUris;

    public SelectedImgsAdapter(Context context, ArrayList<Uri> imgUris) {
        mContext = context;
        mImgUris = imgUris;
    }

    @NonNull
    @Override
    public SelectedImgHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(mContext).inflate(R.layout.service_img_items,
                parent, false);
        return new SelectedImgHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull SelectedImgHolder holder, int position) {
        holder.bind(position);
    }

    @Override
    public int getItemCount() {
        return mImgUris.size();
    }


    class SelectedImgHolder extends RecyclerView.ViewHolder {
        ImageView selectedImg;

        public SelectedImgHolder(@NonNull View itemView) {
            super(itemView);
            selectedImg = itemView.findViewById(R.id.service_img);
        }

        public void bind(final int position) {

            selectedImg.setImageBitmap(PictureUtils.getScaledBitmap(mContext, mImgUris.get(position)));
        }
    }
}
