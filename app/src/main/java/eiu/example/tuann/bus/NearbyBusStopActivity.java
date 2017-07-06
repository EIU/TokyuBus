package eiu.example.tuann.bus;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.maps.model.LatLng;
import com.wang.avi.AVLoadingIndicatorView;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;

public class NearbyBusStopActivity extends AppCompatActivity {

    private ListView listNearByBusStop;
    private TextView textView;
    private AVLoadingIndicatorView avLoadingIndicatorView;
    private ImageView animation;

    public static LatLng latLngClickNearBy = null;

    public static ArrayList<String> listDistance = new ArrayList<String>();
    public static ArrayList<String> listAddress = new ArrayList<String>();
    private TreeMap<Integer, String> hashMapClickNearBy = new TreeMap<Integer, String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_nearby_bus_stop);

        overridePendingTransition(R.animator.start_nothing, R.animator.start_nothing);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.animation.setVisibility(View.GONE);
                MainActivity.avLoadingIndicatorView.setVisibility(View.GONE);
                MainActivity.avLoadingIndicatorView.hide();
                onBackPressed();
            }
        });

        animation = (ImageView) findViewById(R.id.bus_gif);
        Glide.with(this).load(R.drawable.gif_bus).into(animation);
        avLoadingIndicatorView = (AVLoadingIndicatorView) (findViewById(R.id.avi));
        listNearByBusStop = (ListView) (findViewById(R.id.list_nearby_busstop));
        textView = (TextView) (findViewById(R.id.clean_listview));
        avLoadingIndicatorView.show();


        int i = 0;
        for (Map.Entry<Float, String> entry : MainActivity.TreeClickNearBy.entrySet()) {
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
                Thread welcomeThread = new Thread() {
                    @Override
                    public void run() {
                        try {
                            super.run();
                            sleep(1000);
                        } catch (Exception e) {

                        } finally {
                            Intent i = new Intent(NearbyBusStopActivity.this,
                                    MainActivity.class);
                            startActivity(i);
                            finish();
                        }
                    }
                };
                welcomeThread.start();
            }
        });
    }

    @Override
    public void onBackPressed() {
        MainActivity.animation.setVisibility(View.GONE);
        MainActivity.avLoadingIndicatorView.setVisibility(View.GONE);
        MainActivity.avLoadingIndicatorView.hide();
        super.onBackPressed();
    }
}
