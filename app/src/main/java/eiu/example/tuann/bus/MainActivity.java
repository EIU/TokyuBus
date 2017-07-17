package eiu.example.tuann.bus;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeSet;

import static eiu.example.tuann.bus.MainFragment.fabDirection;
import static eiu.example.tuann.bus.MainFragment.fabWalking;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, OnMapReadyCallback, View.OnClickListener, GoogleApiClient.OnConnectionFailedListener, GoogleMap.OnMarkerClickListener, GoogleMap.InfoWindowAdapter, GoogleApiClient.ConnectionCallbacks, com.google.android.gms.location.LocationListener, GoogleMap.OnMapClickListener, GoogleMap.OnMapLongClickListener {

    private static final String TAG = null;

    public static DrawerLayout drawer;

    public static PlaceAutocompleteAdapter mAdapterPlaceAutoComplete;

    private View header;
    private Button button_login_without;
    private NavigationView navigationView;
    private TextView button_t_register;
    private Button back_to_login;
    private Button register;
    private Button back_to_first;
    private Button login;

    private EditText mEmailViewLogin;
    private EditText mPasswordViewLogin;
    private EditText mNameViewRegister;
    private EditText mEmailViewRegister;
    private EditText mPasswordViewRegister;
    private EditText mConfirmPasswordViewRegister;
    private EditText mPhoneNumberRegister;
    private ImageView mAvatarLoged;
    private TextView mEmailLoged;
    private TextView mNameLoged;
    private ImageView buttonDropDown;
    public static View viewMap;
    public static TextView tvDistanceDuration;
    private static AVLoadingIndicatorView avLoadingIndicatorView;
    private static ImageView animation;

    private static final LatLngBounds BOUNDS_GREATER_SYDNEY = new LatLngBounds(new LatLng(-0, 0), new LatLng(-0, 0));

    private boolean loged = false;

    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;
    public static StorageReference storageReference;
    private Firebase firebasePutLocation;
    public static FirebaseUser user;
    private Firebase firebaseGetLocation;
    private FirebaseStorage firebaseStorage;
    private StorageReference mStorageRef;

    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    public static GoogleApiClient mGoogleApiClientAutoComplete;

    public static Marker findAddress;
    public static Marker startDirection;
    public static Marker endDirection;

    private HashMap<String, Marker> hashMapMarkerBus = new HashMap<String, Marker>();

    private HashMap<String, String> hashMapBus = new HashMap<String, String>();

    public static Location myCurrentLocation;

    private ProgressDialog progressDialog;

    private double oldlat;
    private double oldlong;

    public static LatLng currentLocation;

    public static Polyline polyline = null;

    public static String addressWalkingDirection;

    public static AppCompatActivity appCompatActivity;

    public static String travelMod;

    public static HashMap<Double, Double> hashMapNearByBusStop;

    private FragmentManager manager = getSupportFragmentManager();
    private MainFragment mainFragment = new MainFragment();
    private DirectionFragment directionFragment = new DirectionFragment();
    private PlacePickerFragment placePickerFragment = new PlacePickerFragment();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_navigation_drawer);
        appCompatActivity = MainActivity.this;

        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Đang xử lý...");

        animation = (ImageView) findViewById(R.id.bus_gif);
        Glide.with(this).load(R.drawable.gif_bus).into(animation);
        avLoadingIndicatorView = (AVLoadingIndicatorView) (findViewById(R.id.avi));

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        viewMap = mapFragment.getView();

        buildGoogleApiClient();

        overridePendingTransition(R.animator.start_nothing, R.animator.start_nothing);

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setItemIconTintList(null);

        manager.beginTransaction().replace(R.id.main_layout_maps, mainFragment, mainFragment.getTag()).commit();

        tvDistanceDuration = (TextView) findViewById(R.id.tv_distance_time);

        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        storageReference = FirebaseStorage.getInstance().getReference();
        firebaseStorage = FirebaseStorage.getInstance();
        mStorageRef = firebaseStorage.getReferenceFromUrl("gs://becamex-tokyu-bus.appspot.com");
        firebaseGetLocation = new Firebase("https://becamex-tokyu-bus.firebaseio.com/User/Staff/Driver");
        if (checkGooglePlayServices() == true) {
            firebaseGetLocation.addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    Iterator iteratorName = dataSnapshot.child("Information").getChildren().iterator();
                    Iterator iteratorLocation = dataSnapshot.child("Location").getChildren().iterator();
                    while (iteratorLocation.hasNext()) {
                        double latitude = (((Double) ((DataSnapshot) iteratorLocation.next()).getValue()));
                        double longitude = (((Double) ((DataSnapshot) iteratorLocation.next()).getValue()));
                        iteratorName.next();
                        String name = (((String) ((DataSnapshot) iteratorName.next()).getValue()));
                        hashMapBus.put(name.toLowerCase(), "" + latitude + " " + longitude);
                        iteratorName.next();
                        String phoneNumber = (((String) ((DataSnapshot) iteratorName.next()).getValue()));

                        Geocoder geocoder;
                        List<Address> addresses = null;
                        geocoder = new Geocoder(MainActivity.this, Locale.getDefault());
                        try {
                            addresses = geocoder.getFromLocation(latitude, longitude, 1);
                            //                    arrayListMarkerBus.add(name);
                            Address address = addresses.get(0);
                            String spi = (String.valueOf(address.getAddressLine(0) + ", " + address.getAddressLine(1) + ", " + address.getAddressLine(2) + ", " + address.getAddressLine(3) + "\n" + "Tọa độ: " + latitude + ", " + longitude));
                            LatLng latLng = new LatLng(latitude, longitude);
                            Marker nameMarker = mMap.addMarker(new MarkerOptions().position(latLng).title(name).snippet(spi).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_bus_marker)));
                            hashMapMarkerBus.put(name, nameMarker);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }

                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                    Iterator iteratorName = dataSnapshot.child("Information").getChildren().iterator();
                    Iterator iteratorLocation = dataSnapshot.child("Location").getChildren().iterator();
                    while (iteratorLocation.hasNext()) {
                        double latitude = (((Double) ((DataSnapshot) iteratorLocation.next()).getValue()));
                        double longitude = (((Double) ((DataSnapshot) iteratorLocation.next()).getValue()));
                        iteratorName.next();
                        String name = (((String) ((DataSnapshot) iteratorName.next()).getValue()));
                        if (hashMapMarkerBus.get(name) != null) {
                            hashMapMarkerBus.get(name).remove();
                        }
                        iteratorName.next();
                        String phoneNumber = (((String) ((DataSnapshot) iteratorName.next()).getValue()));

                        Geocoder geocoder;
                        List<Address> addresses = null;
                        geocoder = new Geocoder(MainActivity.this, Locale.getDefault());
                        try {
                            addresses = geocoder.getFromLocation(latitude, longitude, 1);

                            hashMapBus.put(name.toLowerCase(), "" + latitude + " " + longitude);
                            LatLng latLng = new LatLng(latitude - 10, longitude);
                            Address address = addresses.get(0);
                            String spi = (String.valueOf(address.getAddressLine(0) + ", " + address.getAddressLine(1) + ", " + address.getAddressLine(2) + ", " + address.getAddressLine(3) + "\n" + "Tọa độ: " + latitude + ", " + longitude));


//                    Location prevLoc = new Location("service Provider");
//                    prevLoc.setLatitude(oldlat);
//                    prevLoc.setLongitude(oldlong);
//                    Location newLoc = new Location("service Provider");
//                    newLoc.setLatitude(latitude);
//                    newLoc.setLongitude(longit    ude);
//                    float bearing = prevLoc.bearingTo(newLoc);
                            Marker markerName = mMap.addMarker(new MarkerOptions().position(latLng).title(name).snippet(spi).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_bus_marker)));
                            hashMapMarkerBus.put(name, markerName);
                            oldlong = longitude;
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }

                @Override
                public void onChildRemoved(DataSnapshot dataSnapshot) {

                }

                @Override
                public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                }

                @Override
                public void onCancelled(FirebaseError firebaseError) {

                }
            });
        }
        if (loged == true) {
            loged();
        } else {
            without_login();
        }

        mGoogleApiClientAutoComplete = new GoogleApiClient.Builder(this).enableAutoManage(this, 0 /* clientId */, this).addApi(Places.GEO_DATA_API).build();
        AutocompleteFilter autocompleteFilter = new AutocompleteFilter.Builder().setTypeFilter(Place.TYPE_COUNTRY).setCountry("VN").build();
        mAdapterPlaceAutoComplete = new PlaceAutocompleteAdapter(this, mGoogleApiClientAutoComplete, BOUNDS_GREATER_SYDNEY, autocompleteFilter);
    }

    public static int hight = 0;

    @Override
    public void onClick(View v) {
        if (v == button_login_without) {
            hideKeyboard(this);
            login();
        } else if (v == button_t_register) {
            hideKeyboard(this);
            register();
        } else if (v == register) {
            hideKeyboard(this);
            attemptRegister();
        } else if (v == login) {
            hideKeyboard(this);
            attemptLogin();
        } else if (v == mAvatarLoged) {
            hideKeyboard(this);
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
            startActivityForResult(intent, GALLERY_INTENT);
        } else if (v == back_to_login) {
            hideKeyboard(this);
            login();
        } else if (v == back_to_first) {
            hideKeyboard(this);
            without_login();
        } else if (v == mEmailLoged || v == buttonDropDown) {
            new AlertDialog.Builder(this).setCancelable(false).setTitle("Đăng xuất tài khoản này").setMessage("Bạn có muốn đăng xuất?").setPositiveButton("Đồng ý", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    FirebaseAuth.getInstance().signOut();
                    without_login();
                }
            }).setNegativeButton("Hủy bỏ", null).show();
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.navigation_drawer, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_nomal) {
            mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        } else if (id == R.id.nav_hybrid) {
            mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        } else if (id == R.id.nav_stallite) {
            mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
        } else if (id == R.id.nav_terrain) {
            mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
        } else if (id == R.id.nav_none) {
            mMap.setMapType(GoogleMap.MAP_TYPE_NONE);
        } else if (id == R.id.nav_nearby_bustop) {
            showAnimation();
            Location target = new Location("Busstop");
            hashMapNearByBusStop = new HashMap<Double, Double>();
            for (Map.Entry<String, BusStopInfomation> entry : WelcomeScreenActivity.allBusStopInfomation.getAllBus().entrySet()) {
                double latitude = entry.getValue().getLatitude();
                double longitude = entry.getValue().getLongitude();
                target.setLatitude(latitude);
                target.setLongitude(longitude);
                if (myCurrentLocation.distanceTo(target) < 5000) {
                    hashMapNearByBusStop.put(latitude, longitude);
                }
            }
            Thread welcomeThread = new Thread() {
                @Override
                public void run() {
                    try {
                        super.run();
                        sleep(1000);
                    } catch (Exception e) {
                    } finally {
                        Intent i = new Intent(MainActivity.this, NearbyBusStopActivity.class);
                        startActivity(i);
                    }
                }
            };
            welcomeThread.start();

        } else if (id == R.id.nav_bug) {
            animation.setVisibility(View.VISIBLE);
            avLoadingIndicatorView.setVisibility(View.VISIBLE);
            avLoadingIndicatorView.show();
            Thread welcomeThread = new Thread() {
                @Override
                public void run() {
                    try {
                        super.run();
                        sleep(500);
                    } catch (Exception e) {
                    } finally {
                        Intent i = new Intent(MainActivity.this, ReportActivity.class);
                        startActivity(i);
                    }
                }
            };
            welcomeThread.start();
        } else if (id == R.id.busstop) {
            SendBusStopFragment sendBusStopFragment = new SendBusStopFragment();
            manager.beginTransaction().replace(R.id.sendToFireBase, sendBusStopFragment, sendBusStopFragment.getTag()).commit();
        } else if (id == R.id.enable_tracking_location) {
            isTrackingLocationToFireBase = true;
        } else if (id == R.id.disable_tracking_location) {
            isTrackingLocationToFireBase = false;
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private boolean isTrackingLocationToFireBase = false;

    public static GoogleMap mMap;

    public static ArrayList<LatLng> markerPoints = new ArrayList<LatLng>();

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(false);
        mMap.getUiSettings().setMapToolbarEnabled(false);
        mMap.setOnMarkerClickListener(this);
        mMap.setInfoWindowAdapter(this);
        mMap.setOnMapClickListener(this);
        mMap.setOnMapLongClickListener(this);
        setUpBusStop();
    }

    private boolean isMainFragmentClickHide = false;
    private boolean isDirectionFragmentClickHide = false;
    public static boolean isMainFragmentshow = true;
    public static boolean isDirectionFragmentShow = false;

    @Override
    public void onMapClick(LatLng latLng) {
        if (isKeyBoardVisible() == true) {
            hideKeyboard(this);
        } else {
            if (isMainFragmentshow == true && isMainFragmentClickHide == false && isDirectionFragmentClickHide == false) {
                manager.beginTransaction().replace(R.id.main_layout_maps, mainFragment, mainFragment.getTag()).hide(mainFragment).commit();
                isMainFragmentClickHide = true;
                isMainFragmentshow = false;
            } else if (isDirectionFragmentShow == true && isDirectionFragmentClickHide == false && isMainFragmentClickHide == false) {
                manager.beginTransaction().replace(R.id.direction_layout_maps, directionFragment, directionFragment.getTag()).hide(directionFragment).commit();
                manager.beginTransaction().replace(R.id.layout_place_picker, placePickerFragment, placePickerFragment.getTag()).hide(placePickerFragment).commit();
                isDirectionFragmentClickHide = true;
                isDirectionFragmentShow = false;
            } else if (isMainFragmentshow == false && isMainFragmentClickHide == true && isDirectionFragmentClickHide == false) {
                manager.beginTransaction().replace(R.id.main_layout_maps, mainFragment, mainFragment.getTag()).show(mainFragment).commit();
                isMainFragmentClickHide = false;
                isMainFragmentshow = true;
            } else if (isDirectionFragmentShow == false && isDirectionFragmentClickHide == true && isMainFragmentClickHide == false) {
                manager.beginTransaction().replace(R.id.direction_layout_maps, directionFragment, directionFragment.getTag()).show(directionFragment).commit();
                manager.beginTransaction().replace(R.id.layout_place_picker, placePickerFragment, placePickerFragment.getTag()).show(placePickerFragment).commit();
                isDirectionFragmentClickHide = false;
                isDirectionFragmentShow = true;
            }
        }
        fabDirection.setVisibility(View.VISIBLE);
        fabWalking.setVisibility(View.GONE);
    }

    @Override
    public void onMapLongClick(LatLng latLng) {
        hideKeyboard(this);
        if (startDirection != null) {
            startDirection.remove();
        }
        if (endDirection != null) {
            endDirection.remove();
        }
        if (findAddress != null) {
            findAddress.remove();
        }

        if (polyline != null) {
            polyline.remove();
        }
        if (tvDistanceDuration.getVisibility() == View.VISIBLE) {
            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) viewMap.getLayoutParams();
            layoutParams.setMargins(0, 0, 0, 0);
            tvDistanceDuration.setVisibility(View.GONE);
        }

        findAddress = mMap.addMarker(new MarkerOptions().position(latLng).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));

        fabDirection.setVisibility(View.GONE);
        fabWalking.setVisibility(View.VISIBLE);

        if (markerPoints.size() > 1) {
            markerPoints.clear();
        }

        markerPoints.add(latLng);
        if (markerPoints.size() == 1) {
            markerPoints.add(currentLocation);
        }

        List<Address> addresses = null;
        Geocoder geocoder = new Geocoder(this);
        try {
            addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
        } catch (IOException e) {
            e.printStackTrace();
        }

        Address adddress = addresses.get(0);
        addressWalkingDirection = (String.valueOf(adddress.getAddressLine(0) + ", " + adddress.getAddressLine(1) + ", " + adddress.getAddressLine(2) + "\n" + "Tọa độ: " + latLng.latitude + ", " + latLng.longitude));
    }

    private void setUpBusStop() {
        for (Map.Entry<String, BusStopInfomation> entry : WelcomeScreenActivity.allBusStopInfomation.getAllBus().entrySet()) {
            mMap.addMarker(new MarkerOptions().position(entry.getValue().getLatLng()).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_bus_stop)));
        }
    }

    public static LatLng locationMarkerClicked;

    @Override
    public boolean onMarkerClick(Marker marker) {
        String s = marker.getSnippet();
        if (marker.getTitle() == null && !marker.equals(findAddress) && !marker.equals(startDirection) && !marker.equals(endDirection)) {
            locationMarkerClicked = marker.getPosition();
            startActivity(new Intent(MainActivity.this, InformationMarkerActivity.class));
        }
        return false;
    }

    private boolean markerSetup = false;

    @Override
    public void onLocationChanged(Location location) {
        myCurrentLocation = location;
        if (isTrackingLocationToFireBase == true) {
            TrackingLocationToFireBase();
        }
        if (myCurrentLocation != null && markerSetup == false) {
            markerSetup = true;
            currentLocation = new LatLng(myCurrentLocation.getLatitude(), myCurrentLocation.getLongitude());
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 16));
        }
        if (myCurrentLocation != null) {
            currentLocation = new LatLng(myCurrentLocation.getLatitude(), myCurrentLocation.getLongitude());
        }
        if (user != null && user.getEmail().contains("@bustokyu.com")) {
            firebasePutLocation = new Firebase("https://becamex-tokyu-bus.firebaseio.com/User/Staff/Driver/" + user.getUid() + "/Location");
            HashMap<String, Double> locationRealTime = new HashMap<String, Double>();
            locationRealTime.put("latitude", location.getLatitude());
            locationRealTime.put("longitude", location.getLongitude());
            firebasePutLocation.setValue(locationRealTime);
        }
    }

    @Override
    public View getInfoWindow(Marker marker) {
        return null;
    }

    @Override
    public View getInfoContents(Marker marker) {
        return null;
    }

    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    private boolean isKeyBoardVisible() {
        InputMethodManager imm = (InputMethodManager) getApplicationContext()
                .getSystemService(Context.INPUT_METHOD_SERVICE);

        if (imm.isAcceptingText()) {
            return true;
        } else {
            return false;
        }
    }

    private final int GALLERY_INTENT = 2;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GALLERY_INTENT && resultCode == RESULT_OK) {
            progressDialog.setMessage("Đang cập nhật ảnh đại diện...");
            progressDialog.show();
            Uri uir = data.getData();
            StorageReference filePath = mStorageRef.child("Users").child("Client").child(user.getUid()).child("Profile Image");
            filePath.putFile(uir).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                    double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                    Toast.makeText(MainActivity.this, "Upload is " + progress + "% done", Toast.LENGTH_LONG).show();
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
                    mAvatarLoged.setBackground(null);
                    Picasso.with(MainActivity.this).load(downloadUri).fit().centerCrop().into(mAvatarLoged);
                    databaseReference.child("Users").child("Client").child(user.getUid()).child("Information").child("Avatar").setValue(downloadUri.toString());
                    Toast.makeText(MainActivity.this, "Cập nhật ảnh đại diện thành công", Toast.LENGTH_LONG).show();
                    progressDialog.dismiss();
                }
            });
        }
    }

    public void moveLatLgnClickNearby() {
        NearbyBusStopActivity nearbyBusStopActivity = new NearbyBusStopActivity();
        LatLng latLng = nearbyBusStopActivity.getLatLngClickNearBy();
        if (latLng != null) {
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16));
            latLng = null;
            nearbyBusStopActivity.setLatLngClickNearBy(latLng);
        }
    }

    public static void hideAnimation() {
        animation.setVisibility(View.GONE);
        avLoadingIndicatorView.setVisibility(View.GONE);
        avLoadingIndicatorView.hide();
    }

    public void showAnimation() {
        animation.setVisibility(View.VISIBLE);
        avLoadingIndicatorView.setVisibility(View.VISIBLE);
        avLoadingIndicatorView.show();
    }

    private TreeSet<String> treeSet = new TreeSet<String>();

    private void TrackingLocationToFireBase() {
        String key = databaseReference.push().getKey();
        Firebase firebase = new Firebase("https://becamex-tokyu-bus.firebaseio.com/Bus Stop Information/" + key);
        HashMap<String, Double> hashMap = new HashMap<String, Double>();
        if (!treeSet.contains(currentLocation.toString())) {
            treeSet.add(currentLocation.toString());
            hashMap.put("Latitude", currentLocation.latitude);
            hashMap.put("Longitude", currentLocation.longitude);
        }
        firebase.setValue(hashMap);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //Đăng nhập, Đăng ký//
    private boolean isEmailValid(String email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private boolean isPasswordValid(String password) {
        return password.length() > 5;
    }

    private void attemptLogin() {
        // Store values at the time of the login attempt.
        String email = mEmailViewLogin.getText().toString().trim();
        String password = mPasswordViewLogin.getText().toString().trim();

        boolean cancel = false;
        View focusView = null;

        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            mPasswordViewRegister.setError(Html.fromHtml("<font color='red'>Mật khẩu quá ngắn</font>"));
            focusView = mPasswordViewRegister;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            mEmailViewLogin.setError(Html.fromHtml("<font color='red'>Vui lòng nhập email</font>"));
            focusView = mEmailViewLogin;
            cancel = true;
        } else if (!isEmailValid(email)) {
            mEmailViewLogin.setError(Html.fromHtml("<font color='red'>Email không hợp lệ</font>"));
            focusView = mEmailViewLogin;
            cancel = true;
        } else if (TextUtils.isEmpty(password)) {
            mPasswordViewLogin.setError(Html.fromHtml("<font color='red'>Vui lòng nhập mật khẩu</font>"));
            focusView = mPasswordViewLogin;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            progressDialog.show();
            firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        progressDialog.dismiss();
                        user = firebaseAuth.getCurrentUser();
                        loged();
                    } else {
                        progressDialog.dismiss();
                        Toast.makeText(getApplicationContext(), "Đăng nhập thất bại", Toast.LENGTH_LONG).show();
                    }
                }
            });
        }
    }

    private void attemptRegister() {
        final String email = mEmailViewRegister.getText().toString().trim();
        String password = mPasswordViewRegister.getText().toString().trim();
        boolean cancel = false;
        View focusView = null;
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            mPasswordViewRegister.setError(Html.fromHtml("<font color='red'>Mật khẩu quá ngắn</font>"));
            focusView = mPasswordViewRegister;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            mEmailViewRegister.setError(Html.fromHtml("<font color='red'>Vui lòng nhập email</font>"));
            focusView = mEmailViewRegister;
            cancel = true;
        } else if (!isEmailValid(email)) {
            mEmailViewRegister.setError(Html.fromHtml("<font color='red'>Email không hợp lệ</font>"));
            focusView = mEmailViewRegister;
            cancel = true;
        } else if (TextUtils.isEmpty(password)) {
            mPasswordViewRegister.setError(Html.fromHtml("<font color='red'>Vui lòng nhập mật khẩu</font>"));
            focusView = mPasswordViewRegister;
            cancel = true;
        } else if (!mConfirmPasswordViewRegister.getText().toString().equals(mPasswordViewRegister.getText().toString())) {
            mPasswordViewRegister.setError(Html.fromHtml("<font color='red'>Mật khẩu không khớp</font>"));
            mConfirmPasswordViewRegister.setError(Html.fromHtml("<font color='red'>Mật khẩu không khớp</font>"));
            focusView = mPasswordViewRegister;
            cancel = true;
        }

        if (cancel) {
            focusView.requestFocus();
        } else {
            progressDialog.show();
            firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        String name = mNameViewRegister.getText().toString().trim();
                        String password = mPasswordViewRegister.getText().toString().trim();
                        String phoneNumber = mPhoneNumberRegister.getText().toString().trim();
                        user = firebaseAuth.getCurrentUser();
                        UserProfileChangeRequest userProfileChangeRequest = new UserProfileChangeRequest.Builder().setDisplayName(name).build();
                        user.updateProfile(userProfileChangeRequest);
                        Firebase firebaseRegister;
                        if (!email.contains("@bustokyu.com")) {
                            firebaseRegister = new Firebase("https://becamex-tokyu-bus.firebaseio.com/User/Client/" + user.getUid() + "/Information");
                        } else {
                            firebaseRegister = new Firebase("https://becamex-tokyu-bus.firebaseio.com/User/Staff/Driver/" + user.getUid() + "/Information");
                        }
                        HashMap<String, String> register = new HashMap<String, String>();
                        register.put("Name", name);
                        register.put("Email", user.getEmail());
                        register.put("Phone Number", phoneNumber);
                        register.put("Password", password);
                        register.put("Avatar", null);
                        firebaseRegister.setValue(register);
                        hideKeyboard(MainActivity.this);
                        progressDialog.dismiss();
                        Toast.makeText(getApplicationContext(), "Đăng ký thành công", Toast.LENGTH_LONG).show();
                        login();
                    } else {
                        hideKeyboard(MainActivity.this);
                        progressDialog.dismiss();
                        Toast.makeText(getApplicationContext(), "Email đã tồn tại", Toast.LENGTH_LONG).show();
                    }
                }
            });
        }
    }

    private void loged() {
        navigationView.removeHeaderView(header);
        header = LayoutInflater.from(this).inflate(R.layout.nav_header_navigation_drawer_loged, null);
        navigationView.addHeaderView(header);

        mAvatarLoged = (ImageView) (header.findViewById(R.id.loged_avatar));
        mEmailLoged = (TextView) (header.findViewById(R.id.loged_email));
        mNameLoged = (TextView) (header.findViewById(R.id.loged_name));
        buttonDropDown = (ImageView) (header.findViewById(R.id.button_drop_down));

        user = firebaseAuth.getCurrentUser();
        String id1 = user.getUid().toString();
        firebasePutLocation = new Firebase("https://becamex-tokyu-bus.firebaseio.com/User/Staff/Driver/" + user.getUid());
        storageReference = firebaseStorage.getReferenceFromUrl("gs://becamex-tokyu-bus.appspot.com");
        storageReference.child("Users").child("Client").child(user.getUid()).child("Profile Image").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.with(MainActivity.this).load(uri).fit().centerCrop().into(mAvatarLoged);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Picasso.with(MainActivity.this).load("https://firebasestorage.googleapis.com/v0/b/becamex-tokyu-bus.appspot.com/o/ic_bus_tokyu.png?alt=media&token=d64fcc44-3398-430d-a9db-eda8dbe2e015").fit().centerCrop().into(mAvatarLoged);
                Toast.makeText(MainActivity.this, "Nhấn vào iCon Bus Tokyu để đổi ảnh đại diện", Toast.LENGTH_LONG).show();
            }
        });
        mEmailLoged.setText(user.getEmail());
        mNameLoged.setText(user.getDisplayName());
        mAvatarLoged.setOnClickListener(this);
        buttonDropDown.setOnClickListener(this);
        mEmailLoged.setOnClickListener(this);
    }

    private void without_login() {
        navigationView.removeHeaderView(header);
        header = LayoutInflater.from(this).inflate(R.layout.nav_header_navigation_drawer_without_login, null);
        navigationView.addHeaderView(header);
        button_login_without = (Button) (header.findViewById(R.id.button_login_without));
        button_login_without.setOnClickListener(this);
    }

    private void login() {
        navigationView.removeHeaderView(header);
        header = LayoutInflater.from(this).inflate(R.layout.nav_header_navigation_drawer_login, null);
        navigationView.addHeaderView(header);
        button_t_register = (TextView) (header.findViewById(R.id.open_layout_register));
        button_t_register.setOnClickListener(this);
        back_to_first = (Button) (header.findViewById(R.id.login_back_first));
        back_to_first.setOnClickListener(this);
        login = (Button) (header.findViewById(R.id.button_login));
        login.setOnClickListener(this);
        mEmailViewLogin = (EditText) (findViewById(R.id.login_email));
        mPasswordViewLogin = (EditText) (findViewById(R.id.login_password));
    }

    private void register() {
        navigationView.removeHeaderView(header);
        header = LayoutInflater.from(this).inflate(R.layout.nav_header_navigation_drawer_register, null);
        navigationView.addHeaderView(header);
        register = (Button) (header.findViewById(R.id.button_register));
        back_to_login = (Button) (header.findViewById(R.id.register_back_login));
        register.setOnClickListener(this);
        back_to_login.setOnClickListener(this);

        mNameViewRegister = (EditText) (findViewById(R.id.register_name));
        mEmailViewRegister = (EditText) (findViewById(R.id.register_email));
        mPasswordViewRegister = (EditText) (findViewById(R.id.register_password));
        mConfirmPasswordViewRegister = (EditText) (findViewById(R.id.register_confirm_password));
        mPhoneNumberRegister = (EditText) (findViewById(R.id.register_phone_number));
    }
    //Đăng nhập, Đăng ký//
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onPause() {
        super.onPause();
//        stop location updates when Activity is no longer active
        if (mGoogleApiClient != null) {
            if (checkGooglePlayServices()) {
                LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
            }
        }
    }


    /**
     * Called when the Activity could not connect to Google Play services and the auto manager
     * could resolve the error automatically.
     * In this case the API is not available and notify the user.
     *
     * @param connectionResult can be inspected to determine the cause of the failure
     */
    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

        Log.e(TAG, "onConnectionFailed: ConnectionResult.getErrorCode() = "
                + connectionResult.getErrorCode());

        // TODO(Developer): Check error code and notify the user of error state and resolution.
//        Toast.makeText(this,
//                "Could not connect to Google API Client: Error " + connectionResult.getErrorCode(),
//                Toast.LENGTH_SHORT).show();
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private boolean checkGooglePlayServices() {
        final int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (status != ConnectionResult.SUCCESS) {
            Log.e(TAG, GooglePlayServicesUtil.getErrorString(status));
            // ask user to update google play services.
            Dialog dialog = GooglePlayServicesUtil.getErrorDialog(status, this, 1);
            dialog.show();
            return false;
        } else {
            Log.i(TAG, GooglePlayServicesUtil.getErrorString(status));
            // google play services is updated.
            //your code goes here...
            return true;
        }
    }
}
