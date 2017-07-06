package eiu.example.tuann.bus;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.wang.avi.AVLoadingIndicatorView;

public class ReportActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText receiver;
    private ImageView send;
    private EditText content;

    private AVLoadingIndicatorView avLoadingIndicatorView;
    private ImageView animation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_report);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        setTitle("Đóng góp hoặc báo lỗi");
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
        receiver = (EditText) (findViewById(R.id.receiver));
        send = (ImageView) (findViewById(R.id.send));
        content = (EditText) (findViewById(R.id.content));
        send.setOnClickListener(this);
    }

    @Override
    public void onBackPressed() {
        MainActivity.animation.setVisibility(View.GONE);
        MainActivity.avLoadingIndicatorView.setVisibility(View.GONE);
        MainActivity.avLoadingIndicatorView.hide();
        super.onBackPressed();
    }

    @Override
    public void onClick(View v) {
        View focusView = null;
        if (v == send) {
            if (content.length() >= 10) {
                hideKeyboard(this);
                animation.setVisibility(View.VISIBLE);
                avLoadingIndicatorView.setVisibility(View.VISIBLE);
                avLoadingIndicatorView.show();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            GMailSender sender = new GMailSender("tuan.nguyen.set14@eiu.edu.vn", "AdMiN123");
                            sender.sendMail("Report Tokyu Bus", content.getText().toString(), "tuan.nguyen.set14@eiu.edu.vn", "tuan.nguyen.set14@eiu.edu.vn");
                        } catch (Exception e) {
                        }
                    }
                }).start();

                MainActivity.animation.setVisibility(View.GONE);
                MainActivity.avLoadingIndicatorView.setVisibility(View.GONE);
                MainActivity.avLoadingIndicatorView.hide();
                Thread welcomeThread = new Thread() {

                    @Override
                    public void run() {
                        try {
                            super.run();
                            sleep(1500);
                        } catch (Exception e) {

                        } finally {
                            startActivity(new Intent(ReportActivity.this, MainActivity.class));
                            finish();
                        }
                    }
                };
                welcomeThread.start();
            } else {
                content.setError("Nội dung quá ngắn!");
                focusView = content;
                focusView.requestFocus();
            }

        }
    }

    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
}
