package eiu.example.tuann.bus;


import android.app.Activity;
import android.location.Address;
import android.location.Geocoder;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AutoCompleteTextView;
import android.widget.RelativeLayout;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;

import static eiu.example.tuann.bus.MainActivity.isDirectionFragmentShow;
import static eiu.example.tuann.bus.MainActivity.isMainFragmentshow;


/**
 * A simple {@link Fragment} subclass.
 */
public class MainFragment extends Fragment implements View.OnClickListener, View.OnTouchListener {

    private AutoCompleteTextView mAutocompleteFindAddess;

    public static FloatingActionButton fabDirection;
    public static FloatingActionButton fabWalking;
    private FloatingActionButton fabCurrentLocation;

    public static Marker findAddress;

    private RecentLocationFragment recentLocationFragment = new RecentLocationFragment();

    private FragmentManager manager;
    private DirectionFragment directionFragment;
    private MainFragment mainFragment;
    private PlacePickerFragment placePickerFragment;

    public MainFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_main, container, false);
        manager = getFragmentManager();
        directionFragment = new DirectionFragment();
        mainFragment = new MainFragment();
        placePickerFragment = new PlacePickerFragment();
        mAutocompleteFindAddess = (AutoCompleteTextView) (view.findViewById(R.id.edittextFindAddress));
        mAutocompleteFindAddess.setImeOptions(EditorInfo.IME_ACTION_SEARCH);
        mAutocompleteFindAddess.setOnClickListener(this);
        mAutocompleteFindAddess.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
                        (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    findAddress();
                    return true;
                }
                return false;
            }
        });

        mAutocompleteFindAddess.setOnTouchListener(this);
        AutoCompleteResult autoCompleteResult = new AutoCompleteResult();

        mAutocompleteFindAddess.setOnItemClickListener(autoCompleteResult.mAutocompleteFindPlaceClickListener);
        mAutocompleteFindAddess.setAdapter(MainActivity.mAdapterPlaceAutoComplete);

        fabDirection = (FloatingActionButton) (view.findViewById(R.id.fab_direction));
        fabWalking = (FloatingActionButton) (view.findViewById(R.id.fab_walking));
        fabCurrentLocation = (FloatingActionButton) (view.findViewById(R.id.fab_current_location));
        fabDirection.setOnClickListener(this);
        fabWalking.setOnClickListener(this);
        fabCurrentLocation.setOnClickListener(this);
        return view;
    }

    @Override
    public void onClick(View v) {
        if (v == fabDirection) {
            isMainFragmentshow = false;
            isDirectionFragmentShow = true;
            manager.beginTransaction().replace(R.id.main_layout_maps, mainFragment, mainFragment.getTag()).hide(mainFragment).commit();
            manager.beginTransaction().replace(R.id.direction_layout_maps, directionFragment, directionFragment.getTag()).commit();
            manager.beginTransaction().replace(R.id.layout_place_picker, placePickerFragment, placePickerFragment.getTag()).commit();
            manager.beginTransaction().replace(R.id.layout_recent_location, recentLocationFragment, recentLocationFragment.getTag()).commit();
        } else if (v == fabWalking) {
            if (MainActivity.markerPoints.size() >= 2) {
                final RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) MainActivity.viewMap.getLayoutParams();
                MainActivity.tvDistanceDuration.setVisibility(View.VISIBLE);
                MainActivity.tvDistanceDuration.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
                    @Override
                    public void onGlobalLayout() {
                        MainActivity.tvDistanceDuration.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                        if (MainActivity.hight == 0) {
                            MainActivity.hight = MainActivity.tvDistanceDuration.getHeight() * 3 - 16;
                        }
                        layoutParams.setMargins(0, 0, 0, MainActivity.hight);
                    }
                });
                LatLng origin = MainActivity.markerPoints.get(0);
                LatLng dest = MainActivity.markerPoints.get(1);

                // Getting URL to the Google Directions API
                AutoCompleteResult autoCompleteResult = new AutoCompleteResult();
                String url = autoCompleteResult.getUrl(origin, dest);
                FetchUrl FetchUrl = new FetchUrl();

                // Start downloading json data from Google Directions API
                FetchUrl.execute(url);
            }
        } else if (v == fabCurrentLocation) {
            if (MainActivity.myCurrentLocation != null) {
                MainActivity.currentLocation = new LatLng((MainActivity.myCurrentLocation.getLatitude()), MainActivity.myCurrentLocation.getLongitude());
                MainActivity.mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(MainActivity.currentLocation, 16));
            }
        }
    }

    public void findAddress() {
        String location = mAutocompleteFindAddess.getText().toString().toLowerCase();
//        if (!hashMapBus.containsKey(location)) {
        List<Address> addresses = null;
        if (location.length() > 0) {
            Geocoder geocoder = new Geocoder(getActivity());
            try {
                addresses = geocoder.getFromLocationName(location, 1);
            } catch (IOException e) {
                e.printStackTrace();
            }
            Address address = addresses.get(0);
            LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());
            if (findAddress != null) {
                findAddress.remove();
            }
            findAddress = MainActivity.mMap.addMarker(new MarkerOptions().position(latLng).title(address.getFeatureName()).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
            MainActivity.mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16));
        }
//        } else {
//            List<String> list = Arrays.asList(hashMapBus.get(location).toString().split(" "));
//            LatLng latLng = new LatLng(Double.parseDouble(list.get(0)), Double.parseDouble(list.get(1)));
//            if (findAddress != null) {
//                findAddress.remove();
//            }
//            findAddress = MainActivity.mMap.addMarker(new MarkerOptions().position(latLng).title(String.valueOf(location)).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
//            if (findAddress != null) {
//                findAddress.remove();
//            }
//            MainActivity.mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16));
//        }

        mAutocompleteFindAddess.dismissDropDown();

        MainActivity.hideKeyboard(getActivity());
    }

    @Override
    public boolean onTouch(View view, MotionEvent event) {
        final int DRAWABLE_LEFT = 0;
        final int DRAWABLE_TOP = 1;
        final int DRAWABLE_RIGHT = 2;
        final int DRAWABLE_BOTTOM = 3;

        if (event.getAction() == MotionEvent.ACTION_UP) {
            if (event.getRawX() >= (mAutocompleteFindAddess.getRight() - mAutocompleteFindAddess.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                mAutocompleteFindAddess.setText("");
                return true;
            } else if (event.getRawX() <= (mAutocompleteFindAddess.getCompoundDrawables()[DRAWABLE_LEFT].getBounds().width()) + 110) {
                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Activity.INPUT_METHOD_SERVICE);
                //Find the currently focused view, so we can grab the correct window token from it.
                View view1 = getActivity().getCurrentFocus();
                //If no view currently has focus, create a new one, just so we can grab a window token from it
                if (view1 == null) {
                    view1 = new View(getActivity());
                }
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                MainActivity.drawer.openDrawer(Gravity.START);
                return true;
            }
        }
        return false;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }
}
