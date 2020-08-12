package com.columnhack.fix.activities;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.core.location.LocationManagerCompat;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.columnhack.fix.R;
import com.columnhack.fix.adapters.SelectedImgsAdapter;
import com.columnhack.fix.dialogs.ServiceLoadingDialog;
import com.columnhack.fix.models.Service;
import com.columnhack.fix.services.UploadService;
import com.columnhack.fix.utility.ServiceLab;
import com.columnhack.fix.utility.ServiceLocation;
import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.nabinbhandari.android.permissions.PermissionHandler;
import com.nabinbhandari.android.permissions.Permissions;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class AddServiceActivity extends AppCompatActivity implements PopupMenu.OnMenuItemClickListener {
    private static final int SELECT_PICTURE = 1;
    private static final String TAG = AddServiceActivity.class.getSimpleName();
    public static final int GALLERY_IMG_REQUEST_CODE = 1001;
    public static final int LOCATION_REQUEST_CODE = 1002;
    public static final String LOADING_DIALOG = "loading_dialog";
    public static final int CAPTURE_IMAGE = 1003;

    // widgets
    TextView mNoImagesTextView;
    RecyclerView mServiceImgRecyclerView;
    ImageView addServiceImageView;
    PopupMenu chooseLocationPopup;
    PopupMenu selectImgPopup;
    ImageView chooseServiceLocationImg;
    EditText mServiceTitleEditText;
    EditText mServiceAddressEditText;
    EditText mServiceEmailEditText;
    EditText mServicePhoneEditText;
    EditText mServiceDescEditText;
    EditText mServiceTagsEditText;
    CardView mServiceImgCard;
    CardView mServiceLocationCard;

    // vars
    private boolean addedImages = false;
    private ArrayList<Bitmap> mImgBitmaps = new ArrayList<>();
    private ArrayList<Uri> mImageUris = new ArrayList<>();
    private Intent mIntent;
    private Handler mHandler = new Handler();

    private Location mServiceLocation;
    private FusedLocationProviderClient mFusedLocationClient;
    private LocationRequest mLocationRequest;
    private ArrayList<String> mServiceImages;
    private AddLocationCallback mLocationCallback;
    private ServiceLoadingDialog mAddServiceDialog;
    private File mPhotoFile;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_service);

        mServiceTitleEditText = findViewById(R.id.service_title_edit_text);
        mServiceAddressEditText = findViewById(R.id.service_address_edit_text);
        mServiceEmailEditText = findViewById(R.id.service_email_edit_text);
        mServicePhoneEditText = findViewById(R.id.service_phone_edit_text);
        mServiceDescEditText = findViewById(R.id.service_description_edit_text);
        mServiceTagsEditText = findViewById(R.id.service_tags_edit_text);

        mNoImagesTextView = findViewById(R.id.no_images_text_view);
        mServiceImgRecyclerView = findViewById(R.id.service_img_recycler_view);
        mServiceLocationCard = findViewById(R.id.service_location_card);
        mServiceLocationCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chooseLocationPopup = new PopupMenu(AddServiceActivity.this, mServiceLocationCard);
                chooseLocationPopup.setOnMenuItemClickListener(AddServiceActivity.this);
                chooseLocationPopup.inflate(R.menu.service_location_menu);
                chooseLocationPopup.show();
            }
        });

        mServiceImgCard = findViewById(R.id.service_images_card);
        mServiceImgCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImgPopup = new PopupMenu(AddServiceActivity.this, mServiceImgCard);
                selectImgPopup.setOnMenuItemClickListener(AddServiceActivity.this);
                selectImgPopup.inflate(R.menu.popup);
                selectImgPopup.show();
            }
        });

        updateUI();
    }

    public void addService(View view) {

        if (mServiceLocation == null) {
            Toast.makeText(this, "Please set service location",
                    Toast.LENGTH_SHORT).show();
            return;
        }
        if (mImageUris.isEmpty() || mImageUris.size() < 3) {
            Toast.makeText(this, "Please select three or more images",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        mAddServiceDialog = new ServiceLoadingDialog(this);
        FragmentManager manager = getSupportFragmentManager();
        mAddServiceDialog.show(manager, LOADING_DIALOG);
        // get all the values from the user
        // all the service details including images,
        // and location
        // start a background process to send the values
        // to the firebase database and storage

        String serviceTitle = mServiceTitleEditText.getText().toString();
        String serviceEmail = mServiceEmailEditText.getText().toString();
        String serviceAddress = mServiceAddressEditText.getText().toString();
        String serviceDesc = mServiceDescEditText.getText().toString();
        String servicePhone = mServicePhoneEditText.getText().toString();
        String serviceTags = mServiceTagsEditText.getText().toString();

        // combine title, description and tags for the purpose of search
        serviceTags += serviceTitle + " " + serviceDesc;
//        Location location = mServiceLocation;
        mServiceImages = new ArrayList<>();


        /**
         * Upload the images,
         * get their urls as an array
         * store the array of urls and other service detail
         * in the firebase realtime database
         */

        final Service service = new Service();
        service.setId();
        service.setOwner_id(FirebaseAuth.getInstance().getCurrentUser().getUid());
        service.setTitle(serviceTitle);
        service.setContact_address(serviceAddress);
        service.setLocation(new ServiceLocation(mServiceLocation.getLatitude(), mServiceLocation.getLongitude()));
        service.setDescription(serviceDesc);
        service.setPhone(servicePhone);
        service.setEmail(serviceEmail);
        service.setTags(serviceTags);

        // Set the location first before saving the service
        // in firebase
        // or the service will not be saved
        // because they're being saved in the same location
        setLocation(service.getLocation(), service.getId());

        FirebaseDatabase.getInstance().getReference()
                .child("services")
                .child(service.getId())
                .child("service")
                .setValue(service).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
            }
        });
        startUploadService(service.getId());
    }

    private void setLocation(ServiceLocation location, String id) {
        if (location == null) {
            Toast.makeText(this, "Please choose location for this service", Toast.LENGTH_SHORT)
                    .show();
            return;
        } else {
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("services");
            GeoFire geoFire = new GeoFire(ref);
            geoFire.setLocation(id, new GeoLocation(location.getLatitude(), location.getLongitude()),
                    new GeoFire.CompletionListener() {
                        @Override
                        public void onComplete(String key, DatabaseError error) {
                            if (error != null) {
                                Log.e(TAG, "onComplete: There was an error saving the location to Geofire" + error);
                            } else {
                                Log.d(TAG, "onComplete: Service location saved on server successfully");
                            }
                        }
                    });
        }
    }

    private void startUploadService(String serviceId) {
        ResultReceiver uploadReceiver = new UploadReceiver(null);

        Intent uploadIntent = new Intent(this, UploadService.class);
        uploadIntent.putExtra(UploadService.RECEIVER, uploadReceiver);
        uploadIntent.putExtra(Service.SERVICE_ID, serviceId);
        uploadIntent.putExtra(Service.IMAGE_URIS, mImageUris);
        uploadIntent.putExtra("service_location", mServiceLocation);
        startService(uploadIntent);
    }


    @Override
    protected void onPause() {
        super.onPause();
    }

    private void requestLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            if (mServiceLocation == null) {
                mFusedLocationClient = new FusedLocationProviderClient(this);
                mLocationRequest = new LocationRequest();
                mLocationCallback = new AddLocationCallback();
                mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
                mLocationRequest.setFastestInterval(4000);
                mLocationRequest.setInterval(8000);
                mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, getMainLooper());
                if (mServiceLocation != null) {
                    mFusedLocationClient.removeLocationUpdates(mLocationCallback);
                }
            }

            return;
        } else callPermissions();
    }

    private void callPermissions() {
        String[] permissions = {Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION};
        Permissions.check(this, permissions, "Location permission are required to get user location",
                new Permissions.Options()
                        .setRationaleDialogTitle("Warning")
                        .setRationaleDialogTitle("Location Permission"),
                new PermissionHandler() {
                    @Override
                    public void onGranted() {
                        // do your task.
                        requestLocationUpdates();
                    }

                    @Override
                    public void onDenied(Context context, ArrayList<String> deniedPermissions) {
                        super.onDenied(context, deniedPermissions);
                        callPermissions();
                    }
                });
    }

    @Override
    protected void onStart() {
        super.onStart();
        callPermissions();
    }

    private void configureRecyclerView() {
        mServiceImgRecyclerView.setLayoutManager(new LinearLayoutManager(this,
                RecyclerView.HORIZONTAL, false));
//        SelectedImgsAdapter imgsAdapter = new SelectedImgsAdapter(this, mImgBitmaps);
        SelectedImgsAdapter imgsAdapter = new SelectedImgsAdapter(this, mImageUris);
        mServiceImgRecyclerView.setAdapter(imgsAdapter);
    }

    private void updateUI() {
        if (addedImages) {
            mNoImagesTextView.setVisibility(View.INVISIBLE);
            mServiceImgRecyclerView.setVisibility(View.VISIBLE);
        } else {
            mNoImagesTextView.setVisibility(View.VISIBLE);
            mServiceImgRecyclerView.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CAPTURE_IMAGE) {
            if (resultCode == Activity.RESULT_OK) {
                Uri capturedImgUri = FileProvider.getUriForFile(this,
                        "com.columnhack.fix.fileprovider", mPhotoFile);
                try {
                    Bitmap capturedImgBitmap = MediaStore.Images.Media
                            .getBitmap(this.getContentResolver(), capturedImgUri);
                    mImageUris.add(capturedImgUri);
                    mImgBitmaps.add(capturedImgBitmap);

                    changeUI();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        if (requestCode == SELECT_PICTURE) {
            if (resultCode == Activity.RESULT_OK) {
                if (data.getClipData() != null) {
                    Uri imageUri = null;
                    Bitmap selectedImgBitmap = null;
                    //evaluate the count before the for loop
                    // --- otherwise, the count is evaluated every loop.
                    int count = data.getClipData().getItemCount();
                    for (int i = 0; i < count; i++) {
                        imageUri = data.getClipData().getItemAt(i).getUri();
                        //do something with the image (save it to some directory or whatever you need to do with it here)
                        try {
                            selectedImgBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
                            mImageUris.add(imageUri);
                            mImgBitmaps.add(selectedImgBitmap);

                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    changeUI();
                } else if (data.getData() != null) {
                    Uri selectedImgUri = data.getData();
                    //do something with the image (save it to some directory or whatever you need to do with it here)
                    try {
                        mImageUris.add(selectedImgUri);
                        Bitmap selectedImgBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImgUri);
                        mImgBitmaps.add(selectedImgBitmap);

                        changeUI();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    private void changeUI() {
        configureRecyclerView();
        addedImages = true;
        updateUI();
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        // so users won't be shown annoying permission dialogs
        switch (item.getItemId()) {
            case R.id.popup_camera:
                getImageFromCamera();
                return true;
            case R.id.popup_gallery:
                // Request for permission here
                askPermissionAndGetImgs();
                return true;
            case R.id.use_current_location:
                getCurrentLocation();
                return true;
            case R.id.location_from_map:
                return true;
            default:
                return false;
        }
    }

    private void getImageFromCamera() {
        mPhotoFile = ServiceLab.getInstance(this).getPhotoFile();
        Uri uri = FileProvider.getUriForFile(this,
                "com.columnhack.fix.fileprovider",
                mPhotoFile);
        final Intent captureImage = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        captureImage.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        boolean canTakePhoto = captureImage.resolveActivity(getPackageManager()) != null;
        if (!canTakePhoto) {
            Toast.makeText(this, "There is no camera app on your device",
                    Toast.LENGTH_LONG).show();
            return;
        }

        // get all the activities that can respond to this intent and grant them write permission
        List<ResolveInfo> cameraActivities = getPackageManager().queryIntentActivities(captureImage,
                PackageManager.MATCH_DEFAULT_ONLY);

        for (ResolveInfo activity : cameraActivities) {
            grantUriPermission(activity.activityInfo.packageName, uri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        }
        startActivityForResult(captureImage, CAPTURE_IMAGE);
    }

    private void getCurrentLocation() {
        // check if location is enabled
        boolean locationEnabled = ServiceLab.getInstance(this).isLocationEnabled();
        if (!locationEnabled) {
            // display location settings dialog and return
            new AlertDialog.Builder(this)
                    .setTitle(R.string.gps_not_found_title)  // GPS not found
                    .setMessage(R.string.gps_not_found_message) // Want to enable?
                    .setPositiveButton(R.string.go_to_settings, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialogInterface, int i) {
                            startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                        }
                    })
                    .setNegativeButton(R.string.cancel, null)
                    .show();
            return;
        }
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mFusedLocationClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    if (location != null) mServiceLocation = location;
                    Toast.makeText(AddServiceActivity.this, "lat: " + location.getLatitude() + "\n long: "
                            + location.getLongitude(), Toast.LENGTH_SHORT)
                            .show();
                }
            });
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

        if (requestCode == LOCATION_REQUEST_CODE) {

        }
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
                    new AlertDialog.Builder(AddServiceActivity.this)
                            .setMessage("FixFind needs access to your storage to get service photos")
                            .setCancelable(true)
                            .setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    // User declined but I can still ask for more and show rationale
                                    requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                                            GALLERY_IMG_REQUEST_CODE);
                                }
                            })
                            .setNegativeButton("Cancel", null)
                            .show();

                } else {
                    // User declined and I can't ask more
                    Toast.makeText(this, "You need file access permissions to upload service images",
                            Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mFusedLocationClient.removeLocationUpdates(mLocationCallback);
    }

    private void pickServiceImgFromGallery() {
        mIntent = new Intent();
        mIntent.setType("image/*");
        mIntent.setAction(Intent.ACTION_GET_CONTENT);
        mIntent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        startActivityForResult(Intent.createChooser(mIntent,
                "Select Picture"), SELECT_PICTURE);
    }

    private class AddLocationCallback extends LocationCallback {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            super.onLocationResult(locationResult);
            mServiceLocation = locationResult.getLastLocation();
        }
    }

    private class UploadReceiver extends ResultReceiver {

        /**
         * Create a new ResultReceive to receive results.  Your
         * {@link #onReceiveResult} method will be called from the thread running
         * <var>handler</var> if given, or from an arbitrary thread if null.
         *
         * @param handler
         */
        public UploadReceiver(Handler handler) {
            super(handler);
        }

        // This onReceive also runs in the worker thread
        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {
            super.onReceiveResult(resultCode, resultData);
            Log.d(TAG, "onReceiveResult: " + Thread.currentThread().getName());
            if (resultCode == UploadService.UPLOAD_COMPLETE && resultData != null) {
                boolean done = resultData.getBoolean(UploadService.DONE);
                if (done) {
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            mAddServiceDialog.dismiss();
                            Toast.makeText(getApplicationContext(), "Service upload complete", Toast.LENGTH_LONG)
                                    .show();
                        }
                    });
//                     stop the upload dialog and finish the activity
                    finish();
                }
            }
        }
    }
}
