package eiu.example.tuann.bus;

import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.AdapterView;
import android.widget.RelativeLayout;

import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.places.AutocompletePrediction;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 * Created by tuann on 5/14/2017.
 */

public class AutoCompleteResult {

    public AdapterView.OnItemClickListener mAutocompleteFindPlaceClickListener
            = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            /*
             Retrieve the place ID of the selected item from the Adapter.
             The adapter stores each Place suggestion in a AutocompletePrediction from which we
             read the place ID and title.
              */
            final AutocompletePrediction item = MainActivity.mAdapterPlaceAutoComplete.getItem(position);
            final String placeId = item.getPlaceId();
            final CharSequence primaryText = item.getPrimaryText(null);

            /*
             Issue a request to the Places Geo Data API to retrieve a Place object with additional
             details about the place.
              */
            PendingResult<PlaceBuffer> placeResult = Places.GeoDataApi.getPlaceById(MainActivity.mGoogleApiClientAutoComplete, placeId);
            placeResult.setResultCallback(mUpdatePlaceDetailsCallback);

//            Toast.makeText(getApplicationContext(), "Clicked: " + primaryText + "&" + item.getPlaceId(),
//                    Toast.LENGTH_SHORT).show();
        }
    };

    private ResultCallback<PlaceBuffer> mUpdatePlaceDetailsCallback = new ResultCallback<PlaceBuffer>() {
        @Override
        public void onResult(PlaceBuffer places) {
            if (!places.getStatus().isSuccess()) {
                places.release();
                return;
            }
            final Place place = places.get(0);
            List<String> list = Arrays.asList(place.getLatLng().toString().split(" "));
            String s = list.get(1).substring(1, list.get(1).length() - 1);
            list = Arrays.asList(s.toString().split(","));
            LatLng latLng = new LatLng(Double.parseDouble(list.get(0)), Double.parseDouble(list.get(1)));
            if (MainActivity.findAddress != null) {
                MainActivity.findAddress.remove();
            }
            MainActivity.findAddress = MainActivity.mMap.addMarker(new MarkerOptions().position(latLng).title(String.valueOf(place.getName())).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
            MainActivity.mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));

            MainActivity.hideKeyboard(MainActivity.appCompatActivity);
        }
    };


    public AdapterView.OnItemClickListener mAutocompleteStartDirectionClickListener
            = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            final AutocompletePrediction item = MainActivity.mAdapterPlaceAutoComplete.getItem(position);
            final String placeId = item.getPlaceId();
            final CharSequence primaryText = item.getPrimaryText(null);
            PendingResult<PlaceBuffer> placeResult = Places.GeoDataApi.getPlaceById(MainActivity.mGoogleApiClientAutoComplete, placeId);
            placeResult.setResultCallback(mUpdatePlaceDetailsStartDirectionCallback);
        }
    };

    public static LatLng latLngStartDirection = null;

    private ResultCallback<PlaceBuffer> mUpdatePlaceDetailsStartDirectionCallback = new ResultCallback<PlaceBuffer>() {
        @Override
        public void onResult(PlaceBuffer places) {
            if (!places.getStatus().isSuccess()) {
                places.release();
                return;
            }
            final Place place = places.get(0);
            List<String> list = Arrays.asList(place.getLatLng().toString().split(" "));
            String s = list.get(1).substring(1, list.get(1).length() - 1);
            list = Arrays.asList(s.toString().split(","));
            LatLng latLng = new LatLng(Double.parseDouble(list.get(0)), Double.parseDouble(list.get(1)));
            if (MainActivity.findAddress != null) {
                MainActivity.findAddress.remove();
            }
            if (MainActivity.startDirection != null && MainActivity.endDirection != null) {
                MainActivity.startDirection.remove();
                MainActivity.endDirection.remove();
            }
            latLngStartDirection = latLng;
            MainActivity.hideKeyboard(MainActivity.appCompatActivity);
        }
    };


    public AdapterView.OnItemClickListener mAutocompleteEndDirectionClickListener
            = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            final AutocompletePrediction item = MainActivity.mAdapterPlaceAutoComplete.getItem(position);
            final String placeId = item.getPlaceId();
            final CharSequence primaryText = item.getPrimaryText(null);
            PendingResult<PlaceBuffer> placeResult = Places.GeoDataApi.getPlaceById(MainActivity.mGoogleApiClientAutoComplete, placeId);
            placeResult.setResultCallback(mUpdatePlaceDetailsEndDirectionCallback);
        }
    };

    private int hight = 0;

    private ResultCallback<PlaceBuffer> mUpdatePlaceDetailsEndDirectionCallback = new ResultCallback<PlaceBuffer>() {
        @Override
        public void onResult(PlaceBuffer places) {
            if (!places.getStatus().isSuccess()) {
                places.release();
                return;
            }
            final Place place = places.get(0);
            List<String> list = Arrays.asList(place.getLatLng().toString().split(" "));
            String s = list.get(1).substring(1, list.get(1).length() - 1);
            list = Arrays.asList(s.toString().split(","));
            LatLng latLng = new LatLng(Double.parseDouble(list.get(0)), Double.parseDouble(list.get(1)));

            if (MainActivity.findAddress != null) {
                MainActivity.findAddress.remove();
            }

            if (MainActivity.polyline != null) {
                MainActivity.polyline.remove();
            }

            if (latLngStartDirection == null && MainActivity.currenLocation != null) {
                latLngStartDirection = MainActivity.currenLocation;
            }

            MainActivity.startDirection = MainActivity.mMap.addMarker(new MarkerOptions().position(latLngStartDirection).title(String.valueOf(place.getName())).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_action_round)));
            MainActivity.endDirection = MainActivity.mMap.addMarker(new MarkerOptions().position(latLng).title(String.valueOf(place.getName())).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));


            if (MainActivity.markerPoints.size() > 1) {
                MainActivity.markerPoints.clear();
            }

            MainActivity.markerPoints.add(latLng);
            if (MainActivity.markerPoints.size() == 1) {
                MainActivity.markerPoints.add(latLngStartDirection);
            } else if (MainActivity.markerPoints.size() == 2) {
                MainActivity.markerPoints.add(latLng);
            }

            List<Address> addresses = null;
            Geocoder geocoder = new Geocoder(MainActivity.appCompatActivity);
            try {
                addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
            } catch (IOException e) {
                e.printStackTrace();
            }

            Address adddress = addresses.get(0);
            MainActivity.addressWalkingDirection = (String.valueOf(adddress.getAddressLine(0) + ", " + adddress.getAddressLine(1) + ", " + adddress.getAddressLine(2) + "\n" + "Tọa độ: " + latLng.latitude + ", " + latLng.longitude));

            final RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) MainActivity.viewMap.getLayoutParams();
            MainActivity.tvDistanceDuration.setVisibility(View.VISIBLE);
            MainActivity.tvDistanceDuration.getViewTreeObserver().

                    addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                        @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
                        @Override
                        public void onGlobalLayout() {
                            MainActivity.tvDistanceDuration.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                            if (hight == 0) {
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

            MainActivity.hideKeyboard(MainActivity.appCompatActivity);
        }
    };
}
