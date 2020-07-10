package com.columnhack.fix.activities;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.columnhack.fix.R;
import com.columnhack.fix.adapters.SelectedImgsAdapter;

import java.io.IOException;
import java.util.ArrayList;

public class AddServiceActivity extends AppCompatActivity implements PopupMenu.OnMenuItemClickListener {
    private static final int SELECT_PICTURE = 1;
    private static final int REQUEST_ID_MULTIPLE_PERMISSIONS = 2;
    private static final String TAG = AddServiceActivity.class.getSimpleName();
    public static final int GALLERY_IMG_REQUEST_CODE = 1001;

    // widgets
    TextView mNoImagesTextView;
    RecyclerView mServiceImgsRecyclerView;
    ImageView addServiceImageView;
    PopupMenu selectImgPopup;

    // vars
    private boolean addedImages = false;
    ArrayList<Bitmap> mImgBitmaps;
    private Intent mIntent;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_service);

        mNoImagesTextView = findViewById(R.id.no_images_text_view);
        mServiceImgsRecyclerView = findViewById(R.id.service_img_recycler_view);

        addServiceImageView = findViewById(R.id.add_img_view);
        addServiceImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImgPopup = new PopupMenu(AddServiceActivity.this, addServiceImageView);
                selectImgPopup.setOnMenuItemClickListener(AddServiceActivity.this);
                selectImgPopup.inflate(R.menu.popup);
                selectImgPopup.show();
            }
        });

        updateUI();
    }

    private void configureRecyclerView() {
        mServiceImgsRecyclerView.setLayoutManager(new LinearLayoutManager(this,
                RecyclerView.HORIZONTAL, false));
        SelectedImgsAdapter imgsAdapter = new SelectedImgsAdapter(this, mImgBitmaps);
        mServiceImgsRecyclerView.setAdapter(imgsAdapter);
    }

    private void updateUI() {
        if (addedImages) {
            mNoImagesTextView.setVisibility(View.INVISIBLE);
            mServiceImgsRecyclerView.setVisibility(View.VISIBLE);
        } else {
            mNoImagesTextView.setVisibility(View.VISIBLE);
            mServiceImgsRecyclerView.setVisibility(View.INVISIBLE);
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == SELECT_PICTURE) {
            if (resultCode == Activity.RESULT_OK) {
                if (data.getClipData() != null) {
                    Uri imageUri = null;
                    Bitmap selectedImgBitmap = null;
                    int count = data.getClipData().getItemCount(); //evaluate the count before the for loop --- otherwise, the count is evaluated every loop.
                    for (int i = 0; i < count; i++) {
                        imageUri = data.getClipData().getItemAt(i).getUri();
                        //do something with the image (save it to some directory or whatever you need to do with it here)
                        try {
                            selectedImgBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(),
                                    Uri.parse(imageUri.toString()));
                            mImgBitmaps.add(selectedImgBitmap);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    configureRecyclerView();
                    addedImages = true;
                    updateUI();
                } else if (data.getData() != null) {
                    String imagePath = data.getData().getPath();
                    //do something with the image (save it to some directory or whatever you need to do with it here)
                    try {
                        Bitmap selectedImgBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(),
                                Uri.parse(imagePath));
                        mImgBitmaps.add(selectedImgBitmap);
                        configureRecyclerView();
                        addedImages = true;
                        updateUI();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

//        if (resultCode == RESULT_OK) {
//            if (requestCode == SELECT_PICTURE) {
//                Uri selectedImageUri = data.getData();
//                selectedImagePath = getPath(selectedImageUri);
//                configureRecyclerView();
//                        updateUI();
//                try {
//                    if (Uri.parse(selectedImagePath) != null) {
//                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(),
//                                Uri.parse(selectedImagePath));
//                        mImgBitmaps.add(bitmap);
//                        addedImages = true;
//                    }
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//        }
    }

    /**
     * helper to retrieve the path of an image URI
     */

    /**
        public String getPath(Uri uri) {
        // just some safety built in
        if (uri == null) {
            // TODO perform some logging or show user feedback
            return null;
        }
        // try to retrieve the image from the media store first
        // this will only work for images selected from gallery
        String[] projection = {MediaStore.Images.Media.DATA};
        Cursor cursor = managedQuery(uri, projection, null, null, null);
        if (cursor != null) {
            int column_index = cursor
                    .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            String path = cursor.getString(column_index);
            cursor.close();
            return path;
        }
        // this is our fallback here
        return uri.getPath();
    }*/


    @Override
    public boolean onMenuItemClick(MenuItem item) {
        // TODO: implement the request for multiple permissions here
        // so users won't be shown annoying permission dialogs
        switch (item.getItemId()) {
            case R.id.popup_camera:
                // TODO: yet to be implemented
                return true;
            case R.id.popup_gallery:
                // Request for permission here
                askPermissionAndGetImgs();
                return true;
            default:
                return false;
        }
    }

    private void askPermissionAndGetImgs() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED) {
            // Permission already granted
            pickServiceImgFromGallery();
        } else {
            // User declined but I can still ask for more and show rationale
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    GALLERY_IMG_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == GALLERY_IMG_REQUEST_CODE) {
            if (permissions.length < 1) {
                throw new RuntimeException("no permissions on request result");
            }
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // permission granted
                pickServiceImgFromGallery();
            } else {
                if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                        Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    // User declined but I can still ask for more and show rationale
                    requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                            GALLERY_IMG_REQUEST_CODE);
                } else {
                    // User declined and I can't ask more
                    Toast.makeText(this, "You need file access permissions to upload service images",
                            Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private void pickServiceImgFromGallery() {
        mIntent = new Intent();
        mIntent.setType("image/*");
        mIntent.setAction(Intent.ACTION_GET_CONTENT);
        mIntent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        startActivityForResult(Intent.createChooser(mIntent,
                "Select Picture"), SELECT_PICTURE);
    }
}
