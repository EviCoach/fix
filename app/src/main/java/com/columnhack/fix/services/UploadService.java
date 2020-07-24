package com.columnhack.fix.services;

import android.app.Service;
import android.content.Intent;
import android.net.Uri;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;

public class UploadService extends Service {
    public static final String TAG = UploadService.class.getSimpleName();

    private ArrayList<Uri> mImageUri = new ArrayList<>();
    private ArrayList<String> mServiceImagesUrls;


    private void executeUpload(Uri uri, final int currentIndex, final String serviceId) {
        //specify where the photo will be stored
        final StorageReference storageReference = FirebaseStorage.getInstance().getReference()
                .child("images")
                .child("service_images")
                .child(serviceId)
                .child("img_" + serviceId + "_" + currentIndex);

        // Create file metadata including the content type
        StorageMetadata metadata = new StorageMetadata.Builder()
                .setContentType("image/jpg")
                .setContentLanguage("en") //see nodes below
                .build();

        UploadTask uploadTask = null;
        uploadTask = storageReference.putFile(uri, metadata);
        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                storageReference.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        if (task.isSuccessful()) {
                            String imageUrl = task.getResult().toString();
                            FirebaseDatabase.getInstance().getReference()
                                    .child("services")
                                    .child(serviceId)
                                    .child("service")
                                    .child("img_urls")
                                    .push()
                                    .setValue(imageUrl);
                        }
                    }
                });
                // Cause a recursion here
                if (currentIndex < mImageUri.size()) {
                    int nextIndex = currentIndex + 1;
                    if (nextIndex < mImageUri.size()) {
                        executeUpload(mImageUri.get(nextIndex), nextIndex, serviceId);
                    }
                } else return;
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Toast.makeText(UploadService.this, "could not upload photo", Toast.LENGTH_SHORT).show();
            }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
            }
        });
    } // end of executeUpload method

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy: called. Thread name " + Thread.currentThread().getName());
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        mServiceImagesUrls = new ArrayList<>();

        String serviceId = intent.getStringExtra(com.columnhack.fix.models.Service.SERVICE_ID);


        mImageUri = (ArrayList<Uri>) intent.getSerializableExtra(com.columnhack.fix.models.Service.IMAGE_URIS);
        int i = 0;
        executeUpload(mImageUri.get(i), i, serviceId);

        if (i == mImageUri.size()) {
            stopSelf();
        }

        return Service.START_REDELIVER_INTENT;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
