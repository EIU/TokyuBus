package eiu.example.tuann.bus;


import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.w3c.dom.Text;


/**
 * A simple {@link Fragment} subclass.
 */
public class InformationDirectionFragment extends Fragment {

    private static TextView information;

    public InformationDirectionFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_information_direction, container, false);
        information = (TextView) (view.findViewById(R.id.text_view_information_direction));
        return view;
    }

    public static void setTextInformation(String s) {
        information.setText(s);
    }

    public static String getTextInformation() {
        return information.getText().toString();
    }
}
