package eiu.example.tuann.bus;

import android.content.Intent;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.aurelhubert.ahbottomnavigation.AHBottomNavigation;
import com.aurelhubert.ahbottomnavigation.AHBottomNavigationItem;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnPausedListener;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.wang.avi.AVLoadingIndicatorView;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class InformationMarkerActivity extends AppCompatActivity implements View.OnClickListener {

    private ImageView imageMarker;
    private TextView buttonDirection;
    private TextView textViewAddress;

    private AVLoadingIndicatorView avLoadingIndicatorView;

    private LatLng latLng;

    private double latitude;

    private double longitude;

    private Address address;

    private FragmentManager fragmentManager;
    private InformationDirectionFragment informationDirectionFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_information_marker);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        setTitle("Thông tin trạm xe");
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        fragmentManager = getSupportFragmentManager();
        informationDirectionFragment = new InformationDirectionFragment();
        latitude = MainActivity.locationMarkerClicked.latitude;
        longitude = MainActivity.locationMarkerClicked.longitude;
        latLng = new LatLng(latitude, longitude);
        avLoadingIndicatorView = (AVLoadingIndicatorView) (findViewById(R.id.avi));
        avLoadingIndicatorView.show();

        MainActivity.travelMod = "walking";

        imageMarker = (ImageView) (findViewById(R.id.image_information_marker));
        imageMarker.setOnClickListener(this);
        buttonDirection = (TextView) (findViewById(R.id.button_direction_information_marker));
        textViewAddress = (TextView) (findViewById(R.id.address_information_marker));
        buttonDirection.setOnClickListener(this);
        getImageMarker();
        textViewAddress.setText(getAddressMarker());
        firebaseStorage = FirebaseStorage.getInstance();
        mStorageRef = firebaseStorage.getReferenceFromUrl("gs://becamex-tokyu-bus.appspot.com");
        sendImageB = (Button) (findViewById(R.id.send_image));
        databaseReference = FirebaseDatabase.getInstance().getReference();
        sendImageB.setOnClickListener(this);
    }

    private String getAddressMarker() {
        List<Address> addresses = null;
        Geocoder geocoder = new Geocoder(InformationMarkerActivity.this, Locale.getDefault());
        try {
            addresses = geocoder.getFromLocation(latitude, longitude, 1);
        } catch (IOException e) {
            e.printStackTrace();
        }
        address = addresses.get(0);
        return (String.valueOf(address.getAddressLine(0) + ", " + address.getAddressLine(1) + ", " + address.getAddressLine(2) + ", " + address.getAddressLine(3)));
    }

    private void getImageMarker() {
        MainActivity.storageReference.child("BusStop").child(latitude + " " + longitude).child("Image.png").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(final Uri uri) {
                Picasso.with(InformationMarkerActivity.this).load(uri).fit().centerCrop().into(imageMarker);
                imageMarker.setVisibility(View.VISIBLE);
                avLoadingIndicatorView.setVisibility(View.GONE);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                avLoadingIndicatorView.setVisibility(View.GONE);
                imageMarker.setVisibility(View.VISIBLE);
                Toast.makeText(InformationMarkerActivity.this, "Không có kết nối mạng!", Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public void onClick(View v) {
        if (v == buttonDirection) {
            MainActivity.checkRemovePoint();
            fragmentManager.beginTransaction().replace(R.id.information_direction_layout_maps, informationDirectionFragment, informationDirectionFragment.getTag()).commit();
            MainActivity.markerPoints.add(latLng);
            if (MainActivity.markerPoints.size() == 1) {
                MainActivity.markerPoints.add(MainActivity.currentLocation);
            }
            LatLng origin = MainActivity.markerPoints.get(0);
            LatLng dest = MainActivity.markerPoints.get(1);

            // Getting URL to the Google Directions API
            AutoCompleteResult autoCompleteResult = new AutoCompleteResult();
            String url = autoCompleteResult.getUrl(origin, dest);
            FetchUrl FetchUrl = new FetchUrl();

            // Start downloading json data from Google Directions API
            FetchUrl.execute(url);
            onBackPressed();
        } else if (v == sendImageB) {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
            startActivityForResult(intent, GALLERY_INTENT);
        }
    }

    private StorageReference mStorageRef;
    private DatabaseReference databaseReference;
    private Button sendImageB;
    private FirebaseStorage firebaseStorage;

    private final int GALLERY_INTENT = 2;

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GALLERY_INTENT && resultCode == RESULT_OK) {
            Uri uir = data.getData();
            StorageReference filePath = mStorageRef.child("Bus Stop Information");
            filePath.putFile(uir).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                    double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                    Toast.makeText(InformationMarkerActivity.this, "Upload is " + progress + "% done", Toast.LENGTH_LONG).show();
                }
            }).addOnPausedListener(new OnPausedListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onPaused(UploadTask.TaskSnapshot taskSnapshot) {
                    System.out.println("Upload is paused");
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Uri downloadUri = taskSnapshot.getDownloadUrl();
                    databaseReference.child("Bus Stop Information").child(MainActivity.currentLocation.latitude + ", " + MainActivity.currentLocation.longitude).setValue(downloadUri.toString());
                    Toast.makeText(InformationMarkerActivity.this, "Gửi ảnh thành công", Toast.LENGTH_LONG).show();
                }
            });
        }
    }
}
