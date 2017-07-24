package eiu.example.tuann.bus;

import android.location.Address;
import android.location.Geocoder;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.app.FragmentManager;
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
import java.util.Locale;

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
            DirectionFragment.fragmentManager.beginTransaction().replace(R.id.information_direction_layout_maps, DirectionFragment.informationDirectionFragment, DirectionFragment.informationDirectionFragment.getTag()).show(DirectionFragment.informationDirectionFragment).commit();
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
            MainActivity.showAnimation();
            final AutocompletePrediction item = MainActivity.mAdapterPlaceAutoComplete.getItem(position);
            final String placeId = item.getPlaceId();
            final CharSequence primaryText = item.getPrimaryText(null);
            PendingResult<PlaceBuffer> placeResult = Places.GeoDataApi.getPlaceById(MainActivity.mGoogleApiClientAutoComplete, placeId);
            placeResult.setResultCallback(mUpdatePlaceDetailsEndDirectionCallback);
        }
    };

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
            LatLng latLngEndDirection = new LatLng(Double.parseDouble(list.get(0)), Double.parseDouble(list.get(1)));
            if (latLngStartDirection == null && MainActivity.currentLocation != null) {
                latLngStartDirection = MainActivity.currentLocation;
            }
            latLngEndDirection = place.getLatLng();
            direction(latLngStartDirection, latLngEndDirection);
        }
    };

    public void direction(LatLng start, LatLng end) {
        ParserTask.padding = 250;
        DirectionFragment.fragmentManager.beginTransaction().replace(R.id.information_direction_layout_maps, DirectionFragment.informationDirectionFragment, DirectionFragment.informationDirectionFragment.getTag()).show(DirectionFragment.informationDirectionFragment).commit();
        ParserTask.addressStart = getAddress(start.latitude, start.longitude);
        ParserTask.addressEnd = getAddress(end.latitude, end.longitude);
        MainActivity.checkRemovePoint();
        if (start != MainActivity.currentLocation) {
            MainActivity.startDirection = MainActivity.mMap.addMarker(new MarkerOptions().position(start).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_action_start)));
        }
        MainActivity.endDirection = MainActivity.mMap.addMarker(new MarkerOptions().position(end).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_action_end)));

        LatLng origin = start;
        LatLng dest = end;

        // Getting URL to the Google Directions API
        String url = getUrl(origin, dest);
        FetchUrl FetchUrl = new FetchUrl();

        // Start downloading json data from Google Directions API
        FetchUrl.execute(url);

        MainActivity.hideKeyboard(MainActivity.appCompatActivity);
    }

    public String getUrl(LatLng origin, LatLng dest) {

        // Origin of route
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;

        // Destination of route
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;


        // Sensor enabled
        String sensor = "sensor=true";

        // Building the parameters to the web service
        String parameters = str_origin + "&" + str_dest + "&" + sensor + "&" + "mode=" + MainActivity.travelMod;

        // Output format
        String output = "json";

        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters;

        return url;
    }

    private String getAddress(Double latitude, Double longitude) {
        List<Address> addresses = null;
        String addressS = null;
        Geocoder geocoder = new Geocoder(MainActivity.appCompatActivity, Locale.getDefault());
        try {
            addresses = geocoder.getFromLocation(latitude, longitude, 1);
            Address address = addresses.get(0);
            addressS = address.getAddressLine(0) + ", " + address.getAddressLine(1) + ", " + address.getAddressLine(2) + ", " + address.getAddressLine(3);

        } catch (IOException e) {
            e.printStackTrace();
        }
        return addressS;
    }
}
