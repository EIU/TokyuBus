package eiu.example.tuann.bus;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by tuann on 4/27/2017.
 */

public class CustomListAdapterNearBy extends ArrayAdapter<String> {

    private final Activity context;
    private final ArrayList<String> distance;
    private final ArrayList<String> address;

    public CustomListAdapterNearBy(Activity context, ArrayList<String> distance, ArrayList<String> address) {
        super(context, R.layout.list_nearby_busstop, distance);
        this.context = context;
        this.distance = distance;
        this.address = address;
    }

    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View rowView = inflater.inflate(R.layout.list_nearby_busstop, null, true);

        TextView distance = (TextView) rowView.findViewById(R.id.distace);
        TextView address = (TextView) rowView.findViewById(R.id.address);

        distance.setText("Khoảng cách: " + this.distance.get(position));
        address.setText(this.address.get(position));
        return rowView;

    }
}
