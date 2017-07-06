package eiu.example.tuann.bus;

import android.content.Intent;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
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
import com.squareup.picasso.Picasso;
import com.wang.avi.AVLoadingIndicatorView;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class InformationMarkerActivity extends AppCompatActivity implements View.OnClickListener {

    private ImageView imageMarker;
    private TextView buttonDirection;
    private TextView textViewAddress;

    private AVLoadingIndicatorView avLoadingIndicatorView;

    private LatLng latLng;

    private double latitude;

    private double longitude;

    private Address address;

    public static String fullAddressMarker;

    private boolean isImageFitToScreen;

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
        latitude = MainActivity.locationMarkerClicked.latitude;
        longitude = MainActivity.locationMarkerClicked.longitude;
        latLng = new LatLng(latitude, longitude);
        avLoadingIndicatorView = (AVLoadingIndicatorView) (findViewById(R.id.avi));
        avLoadingIndicatorView.show();
        setupToolbarLayout();

        MainActivity.travelMod = "walking";

        imageMarker = (ImageView) (findViewById(R.id.image_information_marker));
        imageMarker.setOnClickListener(this);
        buttonDirection = (TextView) (findViewById(R.id.button_direction_information_marker));
        textViewAddress = (TextView) (findViewById(R.id.address_information_marker));
        buttonDirection.setOnClickListener(this);
        getImageMarker();
        fullAddressMarker = getAddressMarker();
        textViewAddress.setText(fullAddressMarker);
    }

    private int hight = 0;

    private String getAddressMarker() {
        List<Address> addresses = null;
        Geocoder geocoder = new Geocoder(InformationMarkerActivity.this, Locale.getDefault());
        try {
            addresses = geocoder.getFromLocation(latitude, longitude, 1);
        } catch (IOException e) {
            e.printStackTrace();
        }
        address = addresses.get(0);
        return (String.valueOf(address.getAddressLine(0) + ", " + address.getAddressLine(1) + ", " + address.getAddressLine(2) + ", " + address.getAddressLine(3) + "\n" + "Tọa độ: " + latitude + " " + longitude));
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
            if (MainActivity.startDirection != null) {
                MainActivity.startDirection.remove();
            }
            if (MainActivity.endDirection != null) {
                MainActivity.endDirection.remove();
            }
            if (MainActivity.findAddress != null) {
                MainActivity.findAddress.remove();
            }
            if (MainActivity.polyline != null) {
                MainActivity.polyline.remove();
            }

            if (MainActivity.markerPoints.size() > 1) {
                MainActivity.markerPoints.clear();
            }

            MainActivity.markerPoints.add(latLng);
            if (MainActivity.markerPoints.size() == 1) {
                MainActivity.markerPoints.add(MainActivity.currenLocation);
            }

            final RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) MainActivity.viewMap.getLayoutParams();
            MainActivity.tvDistanceDuration.setVisibility(View.VISIBLE);
            MainActivity.tvDistanceDuration.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
                @Override
                public void onGlobalLayout() {
                    MainActivity.tvDistanceDuration.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    if (hight == 0 && MainActivity.tvDistanceDuration.getHeight() == 0) {
                        hight = MainActivity.tvDistanceDuration.getHeight() * 3 - 16;
                    }
                    layoutParams.setMargins(0, 0, 0, hight);
                }
            });
            LatLng origin = MainActivity.markerPoints.get(0);
            LatLng dest = MainActivity.markerPoints.get(1);

            // Getting URL to the Google Directions API
            String url = MainActivity.getUrl(origin, dest);
            FetchUrl FetchUrl = new FetchUrl();

            // Start downloading json data from Google Directions API
            FetchUrl.execute(url);
            onBackPressed();
        } else if (v == imageMarker) {
            if (isImageFitToScreen) {
                isImageFitToScreen = false;
                imageMarker.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                imageMarker.setAdjustViewBounds(true);
            } else {
                isImageFitToScreen = true;
                imageMarker.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
                imageMarker.setScaleType(ImageView.ScaleType.FIT_XY);
            }
        }
    }

    private void setupToolbarLayout() {
        final AHBottomNavigation bottomNavigation = (AHBottomNavigation) findViewById(R.id.bottom_navigation);

// Create items
        AHBottomNavigationItem item1 = new AHBottomNavigationItem("Walking", R.drawable.ic_travel_walking, R.color.colorAccent);
        AHBottomNavigationItem item2 = new AHBottomNavigationItem("Bus", R.drawable.ic_travel_bus, R.color.colorPrimary);
        AHBottomNavigationItem item3 = new AHBottomNavigationItem("Bus", R.drawable.ic_travel_car, R.color.colorWhite);

// Add items
        bottomNavigation.addItem(item1);
        bottomNavigation.addItem(item2);
        bottomNavigation.addItem(item3);
        bottomNavigation.setDefaultBackgroundColor(Color.parseColor("#7CB342"));

// Disable the translation inside the CoordinatorLayout
        bottomNavigation.setBehaviorTranslationEnabled(false);

// Change colors
        bottomNavigation.setAccentColor(Color.parseColor("#FFFFFF"));
        bottomNavigation.setInactiveColor(Color.parseColor("#747474"));

// Force to tint the drawable (useful for font with icon for example)
        bottomNavigation.setForceTint(true);

// Display color under navigation bar (API 21+)
// Don't forget these lines in your style-v21
// <item name="android:windowTranslucentNavigation">true</item>
// <item name="android:fitsSystemWindows">true</item>
        bottomNavigation.setTranslucentNavigationEnabled(true);

// Manage titles
//        bottomNavigation.setTitleState(AHBottomNavigation.TitleState.SHOW_WHEN_ACTIVE);
//        bottomNavigation.setTitleState(AHBottomNavigation.TitleState.ALWAYS_SHOW);
        bottomNavigation.setTitleState(AHBottomNavigation.TitleState.ALWAYS_HIDE);

// Use colored navigation with circle reveal effect
        bottomNavigation.setColored(false);

// Set current item programmatically
        bottomNavigation.setCurrentItem(0);

// Customize notification (title, background, typeface)
        bottomNavigation.setNotificationBackgroundColor(Color.parseColor("#F63D2B"));

// Set listeners
        bottomNavigation.setOnTabSelectedListener(new AHBottomNavigation.OnTabSelectedListener() {
            @Override
            public boolean onTabSelected(int position, boolean wasSelected) {
                if (position == 0) {
                    MainActivity.travelMod = "walking";
                } else if (position == 1) {
                    MainActivity.travelMod = "transit";
                } else {
                    MainActivity.travelMod = "driving";
                }
                return true;
            }
        });
        bottomNavigation.setOnNavigationPositionListener(new AHBottomNavigation.OnNavigationPositionListener() {
            @Override
            public void onPositionChange(int y) {
                // Manage the new y position
            }
        });
    }
}
