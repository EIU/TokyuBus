package eiu.example.tuann.bus;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;

import static android.app.Activity.RESULT_OK;
import static eiu.example.tuann.bus.MainActivity.isDirectionFragmentShow;
import static eiu.example.tuann.bus.MainActivity.isMainFragmentshow;


/**
 * A simple {@link Fragment} subclass.
 */
public class DirectionFragment extends Fragment implements View.OnClickListener, View.OnFocusChangeListener {

    public static AutoCompleteTextView startDirectionAutoComplete;
    public static AutoCompleteTextView endDirectionAutoComplete;

    private ImageView backDirection;
    private ImageView swapTextDirection;

    private FragmentManager fragmentManager;
    private MainFragment mainFragment;
    private RecentLocationFragment recentLocationFragment;
    private DirectionFragment directionFragment;

    private PlacePickerFragment placePickerFragment;

    public static boolean isForcusStart = false;

    public static boolean isForcusEnd = false;

    public DirectionFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_direction, container, false);
        placePickerFragment = new PlacePickerFragment();
        fragmentManager = getFragmentManager();
        directionFragment = new DirectionFragment();
        recentLocationFragment = new RecentLocationFragment();
        placePickerFragment = new PlacePickerFragment();
        mainFragment = new MainFragment();
        startDirectionAutoComplete = (AutoCompleteTextView) (view.findViewById(R.id.start_location));
        endDirectionAutoComplete = (AutoCompleteTextView) (view.findViewById(R.id.end_location));
        backDirection = (ImageView) (view.findViewById(R.id.back_direction));
        swapTextDirection = (ImageView) (view.findViewById(R.id.swap_location));
        swapTextDirection.setOnClickListener(this);
        backDirection.setOnClickListener(this);
        AutoCompleteResult autoCompleteResult = new AutoCompleteResult();
        startDirectionAutoComplete.setOnItemClickListener(autoCompleteResult.mAutocompleteStartDirectionClickListener);
        endDirectionAutoComplete.setOnItemClickListener(autoCompleteResult.mAutocompleteEndDirectionClickListener);
        startDirectionAutoComplete.setAdapter(MainActivity.mAdapterPlaceAutoComplete);
        endDirectionAutoComplete.setAdapter(MainActivity.mAdapterPlaceAutoComplete);
        startDirectionAutoComplete.setOnFocusChangeListener(this);
        endDirectionAutoComplete.setOnFocusChangeListener(this);
        return view;
    }

    @Override
    public void onClick(View v) {
        if (v == backDirection) {
            isMainFragmentshow = true;
            isDirectionFragmentShow = false;
            fragmentManager.beginTransaction().replace(R.id.main_layout_maps, mainFragment, mainFragment.getTag()).commit();
            fragmentManager.beginTransaction().replace(R.id.direction_layout_maps, directionFragment, directionFragment.getTag()).hide(directionFragment).commit();
            fragmentManager.beginTransaction().replace(R.id.layout_place_picker, placePickerFragment, placePickerFragment.getTag()).hide(placePickerFragment).commit();
            fragmentManager.beginTransaction().replace(R.id.layout_recent_location, recentLocationFragment, recentLocationFragment.getTag()).hide(recentLocationFragment).commit();
        } else if (v == swapTextDirection) {
            String oldStart = startDirectionAutoComplete.getText().toString();
            startDirectionAutoComplete.setText(endDirectionAutoComplete.getText().toString());
            endDirectionAutoComplete.setText(oldStart);
        }
    }

    @Override
    public void onFocusChange(View view, boolean hasFocus) {
        if (hasFocus) {
            if (view == startDirectionAutoComplete) {
                setForcusStart(true);
            } else if (view == endDirectionAutoComplete) {
                setForcusEnd(true);
            } else if (view != startDirectionAutoComplete && view != endDirectionAutoComplete) {
                Toast.makeText(getActivity(), "Bạn chưa chọn nơi cần nhập.", Toast.LENGTH_SHORT).show();
            }
        } else {
            if (view != startDirectionAutoComplete && view != endDirectionAutoComplete) {
                Toast.makeText(getActivity(), "Bạn chưa chọn nơi cần nhập.", Toast.LENGTH_SHORT).show();
            }
            setForcusStart(false);
            setForcusEnd(false);
        }
    }

    public void setTextStartDirectionAutoComplete(String text) {
        startDirectionAutoComplete.setText(text);
    }

    public void setTextEndDirectionAutoComplete(String text) {
        endDirectionAutoComplete.setText(text);
    }

    public boolean getIsForcusStart() {
        return isForcusStart;
    }

    public boolean getIsForcusEnd() {
        return isForcusEnd;
    }

    public void setForcusStart(boolean forcusStart) {
        isForcusStart = forcusStart;
    }

    public void setForcusEnd(boolean forcusEnd) {
        isForcusEnd = forcusEnd;
    }
}
