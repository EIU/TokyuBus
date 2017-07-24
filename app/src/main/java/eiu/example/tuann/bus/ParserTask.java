package eiu.example.tuann.bus;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by tuann on 5/13/2017.
 */

public class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {

    // Parsing the data in non-ui thread


    private String distance;
    private String duration;
    private String detail;

    public static String addressStart;

    public static String addressEnd;

    @Override
    protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {

        JSONObject jObject;
        List<List<HashMap<String, String>>> routes = null;

        try {
            jObject = new JSONObject(jsonData[0]);
            DirectionsJSONParser parser = new DirectionsJSONParser();

            // Starts parsing data
            routes = parser.parse(jObject);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return routes;
    }

    // Executes in UI thread, after the parsing process
    @Override
    protected void onPostExecute(List<List<HashMap<String, String>>> result) {
        ArrayList<LatLng> points = null;
        PolylineOptions lineOptions = null;
        String distance = "";
        String duration = "";


        // Traversing through all the routes
        for (int i = 0; i < result.size(); i++) {
            points = new ArrayList<LatLng>();
            lineOptions = new PolylineOptions();

            // Fetching i-th route
            List<HashMap<String, String>> path = result.get(i);

            // Fetching all the points in i-th route
            for (int j = 0; j < path.size(); j++) {
                HashMap<String, String> point = path.get(j);

                if (j == 0) {    // Get distance from the list
                    distance = (String) point.get("distance");
                    continue;
                } else if (j == 1) { // Get duration from the list
                    duration = (String) point.get("duration");
                    continue;
                }

                double lat = Double.parseDouble(point.get("lat"));
                double lng = Double.parseDouble(point.get("lng"));
                LatLng position = new LatLng(lat, lng);

                points.add(position);
            }

            lineOptions.addAll(points);
            lineOptions.width(10);
            lineOptions.color(Color.GREEN);
        }
        if (MainActivity.polyline != null) {
            MainActivity.polyline.remove();
        }
        String detail = "";
        zoom(points);
        detail = addressStart + " ---> " + addressEnd;
        MainActivity.polyline = MainActivity.mMap.addPolyline(new PolylineOptions().addAll(points).width(7).color(Color.parseColor("#4CAF50")).geodesic(true));
        setDistance(distance);
        setDuration(duration);
        setDetail(detail);
        InformationDirectionFragment.setTextInformation(getDistanceAnDuration());
        MainActivity.hideAnimation();
    }

    public static int padding = 0;

    public void zoom(ArrayList<LatLng> directionPoints) {
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        for (LatLng marker : directionPoints) {
            builder.include(marker);
        }
        LatLngBounds bounds = builder.build();
        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);
        MainActivity.mMap.animateCamera(cu);
    }

    private void setDistance(String distance) {
        this.distance = distance;
    }

    private void setDuration(String duration) {
        this.duration = duration;
    }

    private void setDetail(String detail) {
        this.detail = detail;
    }

    private String getDistance() {
        return distance;
    }

    private String getDuration() {
        return duration;
    }

    private String getDetail() {
        return detail;
    }

    public String getDistanceAnDuration() {
        return "Khoảng cách: " + getDistance() + ", Thời gian: " + getDuration() + "\n" + getDetail();
    }
}