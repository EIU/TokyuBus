package eiu.example.tuann.bus;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

/**
 * Created by tuann on 5/16/2017.
 */

public class AdapterCurrentLocation extends ArrayAdapter<String> {
    private Activity context;
    private String CurrentLocation;

    public AdapterCurrentLocation(Activity context, String currentLocation) {
        super(context, R.layout.list_current_location);
        this.context = context;
        CurrentLocation = currentLocation;
    }

    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View rowView = inflater.inflate(R.layout.list_current_location, null, true);

        TextView location = (TextView) rowView.findViewById(R.id.text_view_list_current_location);
        location.setText(this.CurrentLocation);
        return rowView;

    }
}
