package eiu.example.tuann.bus;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.wang.avi.AVLoadingIndicatorView;

public class ReportActivity extends AppCompatActivity implements View.OnClickListener {
    private ImageView send;
    private EditText content;

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
                onBackPressed();
            }
        });

        MainActivity.hideAnimation();

        send = (ImageView) (findViewById(R.id.send));
        content = (EditText) (findViewById(R.id.content));
        send.setOnClickListener(this);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public void onClick(View v) {
        if (v == send) {
            if (content.length() >= 10) {
                hideKeyboard(this);
                GMailSender sender = new GMailSender("tuan.nguyen.set14@eiu.edu.vn", "AdMiN123");
                try {
                    sender.sendMail("Report Tokyu Bus", content.getText().toString(), "tuan.nguyen.set14@eiu.edu.vn", "tuan.nguyen.set14@eiu.edu.vn");
                } catch (Exception e) {
                    e.printStackTrace();
                }
                Toast.makeText(ReportActivity.this, "Cám ơn bạn đã đóng góp", Toast.LENGTH_SHORT).show();
                onBackPressed();
            } else {
                Toast.makeText(ReportActivity.this, "Nội dung quá ngắn!", Toast.LENGTH_SHORT).show();
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
