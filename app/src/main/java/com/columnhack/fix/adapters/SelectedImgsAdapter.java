package com.columnhack.fix.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.columnhack.fix.R;

import java.util.ArrayList;

public class SelectedImgsAdapter extends RecyclerView.Adapter<SelectedImgsAdapter.SelectedImgHolder> {

    Context mContext;
    ArrayList<Bitmap> mImgBitmaps;

    public SelectedImgsAdapter(Context context, ArrayList<Bitmap> imgBitmaps) {
        mContext = context;
        mImgBitmaps = imgBitmaps;
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
        return mImgBitmaps.size();
    }


    class SelectedImgHolder extends RecyclerView.ViewHolder{
        ImageView selectedImg;

        public SelectedImgHolder(@NonNull View itemView) {
            super(itemView);
            selectedImg = itemView.findViewById(R.id.service_img);
        }

        public void bind(int position){
            selectedImg.setImageBitmap(mImgBitmaps.get(position));
        }
    }
}
