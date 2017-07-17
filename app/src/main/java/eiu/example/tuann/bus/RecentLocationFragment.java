package eiu.example.tuann.bus;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class RecentLocationFragment extends Fragment {

    private ListView listView;

    private ReadWriteFileActivity readWriteFileActivity = new ReadWriteFileActivity();

    public RecentLocationFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_recent_location, container, false);
        listView = (ListView) (view.findViewById(R.id.list_recent_location));
        List<String> listLocation = new ArrayList<String>();
        if (readWriteFileActivity.fileExists(getActivity(), "historyLocation.txt")) {
            readWriteFileActivity.saveFile();
            listLocation.add(readWriteFileActivity.getValueFile(getActivity()));
        }
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, listLocation);
        listView.setAdapter(arrayAdapter);
        return view;
    }

}
