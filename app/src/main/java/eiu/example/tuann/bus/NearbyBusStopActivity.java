package eiu.example.tuann.bus;

import android.location.Location;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.maps.model.LatLng;
import com.wang.avi.AVLoadingIndicatorView;

import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;

public class NearbyBusStopActivity extends AppCompatActivity {

    private ListView listNearByBusStop;
    private TextView textView;
    private AVLoadingIndicatorView avLoadingIndicatorView;
    private ImageView animation;

    public static LatLng latLngClickNearBy = null;

    private ArrayList<String> listDistance = new ArrayList<String>();

    private ArrayList<String> listAddress = new ArrayList<String>();

    private TreeMap<Integer, String> hashMapClickNearBy = new TreeMap<Integer, String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_nearby_bus_stop);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        animation = (ImageView) findViewById(R.id.bus_gif);
        Glide.with(this).load(R.drawable.gif_bus).into(animation);
        avLoadingIndicatorView = (AVLoadingIndicatorView) (findViewById(R.id.avi));
        listNearByBusStop = (ListView) (findViewById(R.id.list_nearby_busstop));
        textView = (TextView) (findViewById(R.id.clean_listview));

        MainActivity.hideAnimation();

        TreeMap<Float, String> treeClickNearBy = new TreeMap<Float, String>();

        TreeMap<Float, String> treeMap = new TreeMap<Float, String>();
        LatLng StartP = MainActivity.currentLocation;

        for (Map.Entry<Double, Double> entry : MainActivity.hashMapNearByBusStop.entrySet()) {
            double latitude = entry.getKey();
            double longitude = entry.getValue();
            float[] result = new float[1];
            Location.distanceBetween(StartP.latitude, StartP.longitude, latitude, longitude, result);
            LatLng latLng = new LatLng(latitude, longitude);
            treeClickNearBy.put(result[0], entry.getKey() + " " + entry.getValue());
            treeMap.put(result[0], WelcomeScreenActivity.allBusStopInfomation.getAllBus().get(latLng.toString()).getAddress());
        }
        for (Map.Entry<Float, String> entry : treeMap.entrySet()) {
            Float distanceDouble = entry.getKey();
            distanceDouble /= 1000;
            String distance = Float.toString(distanceDouble);
            if (distanceDouble >= 1) {
                distance = Double.toString(distanceDouble);
                listDistance.add("" + distance.substring(0, distance.indexOf('.') + 2) + "km");
            } else {
                distance = Double.toString(distanceDouble * 1000);
                listDistance.add("" + distance.substring(0, distance.indexOf('.') + 2) + "m");
            }
            listAddress.add(entry.getValue());
        }

        int i = 0;
        for (Map.Entry<Float, String> entry : treeClickNearBy.entrySet()) {
            hashMapClickNearBy.put(i, entry.getValue());
            i++;
        }
        if (listDistance.isEmpty() || listAddress.isEmpty()) {
            listNearByBusStop.setVisibility(View.GONE);
            textView.setVisibility(View.VISIBLE);
        }
        CustomListAdapterNearBy adapter = new CustomListAdapterNearBy(this, listDistance, listAddress);
        listNearByBusStop.setAdapter(adapter);
        listNearByBusStop.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                animation.setVisibility(View.VISIBLE);
                avLoadingIndicatorView.setVisibility(View.VISIBLE);
                avLoadingIndicatorView.show();
                String lat = hashMapClickNearBy.get(position).substring(0, hashMapClickNearBy.get(position).indexOf(' '));
                String lon = hashMapClickNearBy.get(position).substring(hashMapClickNearBy.get(position).indexOf(' ') + 1, hashMapClickNearBy.get(position).length());
                latLngClickNearBy = new LatLng(Double.parseDouble(lat), Double.parseDouble(lon));
                MainActivity mainActivity = new MainActivity();
                mainActivity.moveLatLgnClickNearby();
                onBackPressed();
            }
        });
    }

    public LatLng getLatLngClickNearBy() {
        return latLngClickNearBy;
    }

    public void setLatLngClickNearBy(LatLng latLngClickNearBy) {
        this.latLngClickNearBy = latLngClickNearBy;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
