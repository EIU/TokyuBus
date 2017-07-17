package eiu.example.tuann.bus;


import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;

import static android.app.Activity.RESULT_OK;


/**
 * A simple {@link Fragment} subclass.
 */
public class PlacePickerFragment extends Fragment implements View.OnClickListener {

    public TextView textViewPlacePicker;

    private final int PLACE_PICKER_REQUEST = 1;

    private RelativeLayout relativeLayoutPicker;

    private DirectionFragment directionFragment;

    AutoCompleteResult autoCompleteResult;

    public PlacePickerFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_place_picker, container, false);
        directionFragment = new DirectionFragment();
        autoCompleteResult = new AutoCompleteResult();
        textViewPlacePicker = (TextView) (view.findViewById(R.id.textview_place_picker));
        relativeLayoutPicker = (RelativeLayout) (view.findViewById(R.id.layout_place_picker));
        relativeLayoutPicker.setOnClickListener(this);
        return view;
    }

    @Override
    public void onClick(View v) {
        if (v == relativeLayoutPicker) {
            PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
            try {
                startActivityForResult(builder.build(getActivity()), PLACE_PICKER_REQUEST);
            } catch (GooglePlayServicesRepairableException e) {
                e.printStackTrace();
            } catch (GooglePlayServicesNotAvailableException e) {
                e.printStackTrace();
            }
        }
    }

    private static LatLng latLngStartDirection = null;

    private static LatLng latLngEndDirection = null;

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACE_PICKER_REQUEST) {
            if (resultCode == RESULT_OK) {
                Place place = PlacePicker.getPlace(data, getActivity());
                if (directionFragment.getIsForcusStart()) {
                    directionFragment.setTextStartDirectionAutoComplete(place.getAddress().toString());
                    latLngStartDirection = place.getLatLng();
                    if (latLngEndDirection != null) {
                        autoCompleteResult.direction(latLngStartDirection, latLngEndDirection);
                        latLngStartDirection = null;

                    }
                } else if (directionFragment.getIsForcusEnd()) {
                    directionFragment.setTextEndDirectionAutoComplete(place.getAddress().toString());
                    latLngEndDirection = place.getLatLng();
                    if (latLngStartDirection == null) {
                        AutoCompleteResult.latLngStartDirection = MainActivity.currentLocation;
                        autoCompleteResult.direction(AutoCompleteResult.latLngStartDirection, place.getLatLng());

                    } else {
                        autoCompleteResult.direction(latLngStartDirection, latLngEndDirection);
                        latLngStartDirection = null;
                    }
                } else {
                    Toast.makeText(getActivity(), "Bạn chưa chọn nơi cần nhập.", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
}