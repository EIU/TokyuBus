package eiu.example.tuann.bus;

import android.app.Application;

import com.firebase.client.Firebase;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.OkHttpDownloader;
import com.squareup.picasso.Picasso;

/**
 * Created by tuann on 10/17/2016.
 */

public class FireApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Firebase.setAndroidContext(this);

        FirebaseDatabase.getInstance().setPersistenceEnabled(true);

        Picasso.Builder builder = new Picasso.Builder(this);
        Picasso buildPicasso = builder.build();
        buildPicasso.setIndicatorsEnabled(false);
        buildPicasso.setLoggingEnabled(true);
        Picasso.setSingletonInstance(buildPicasso);
    }
}
