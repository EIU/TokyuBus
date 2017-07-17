package eiu.example.tuann.bus;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.firebase.client.Firebase;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnPausedListener;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;

import static android.app.Activity.RESULT_OK;


/**
 * A simple {@link Fragment} subclass.
 */
public class SendBusStopFragment extends Fragment implements View.OnClickListener {

    private Button sendLocationB;
    private Firebase firebase;
    private FirebaseDatabase database;
    private DatabaseReference myRef;


    public SendBusStopFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_send_bus_stop, container, false);
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference();
        sendLocationB = (Button) (view.findViewById(R.id.send_location));
        sendLocationB.setOnClickListener(this);
        return view;
    }

    @Override
    public void onClick(View view) {
        if (view == sendLocationB) {
            HashMap<String, Double> hashMap = new HashMap<String, Double>();
            String key = myRef.push().getKey();
            firebase = new Firebase("https://becamex-tokyu-bus.firebaseio.com/Bus Stop Information/" + key);
            hashMap.put("Latitude", MainActivity.currentLocation.latitude);
            hashMap.put("Longitude", MainActivity.currentLocation.longitude);
            firebase.setValue(hashMap);
//            Toast.makeText(getActivity(), "n", Toast.LENGTH_SHORT).show();
        }
    }
}
