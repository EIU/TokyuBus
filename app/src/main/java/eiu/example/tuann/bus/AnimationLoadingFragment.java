package eiu.example.tuann.bus;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.wang.avi.AVLoadingIndicatorView;


/**
 * A simple {@link Fragment} subclass.
 */
public class AnimationLoadingFragment extends Fragment {


    private AVLoadingIndicatorView avLoadingIndicatorView;
    private ImageView animation;

    public AnimationLoadingFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_animation_loading, container, false);
        animation = (ImageView) view.findViewById(R.id.bus_gif);
        Glide.with(this).load(R.drawable.gif_bus).into(animation);
        avLoadingIndicatorView = (AVLoadingIndicatorView) (view.findViewById(R.id.avi));
        avLoadingIndicatorView.show();
        return view;
    }
}
