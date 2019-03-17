package letswave.co.in.wave.Activities;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.ImageView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import letswave.co.in.wave.Models.User;
import letswave.co.in.wave.R;

public class GoodieBagCollectionActivity extends AppCompatActivity {

    @BindView(R.id.goodieBagCollectionToolbar)
    Toolbar goodieBagCollectionToolbar;
    @BindString(R.string.domain)
    String baseServerUrl;
    @BindView(R.id.goodieBagCameraImageView)
    ImageView goodieBagCameraImageView;

    private Unbinder unbinder;
    private MaterialDialog materialDialog;
    private RequestQueue requestQueue;
    private User currentUser;
    private String eventId = "5c8c640383fec557167417a9";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_goodie_bag_collection);


        initializeViews();
        initializeComponents();
    }

    private void initializeViews() {
        unbinder = ButterKnife.bind(GoodieBagCollectionActivity.this);
        setSupportActionBar(goodieBagCollectionToolbar);
        goodieBagCollectionToolbar.setTitleTextColor(Color.WHITE);
    }

    private void initializeComponents() {
        currentUser = getIntent().getParcelableExtra("USER");
        requestQueue = Volley.newRequestQueue(getApplicationContext());
    }

    private void notifyMessage(String message) {
        if (materialDialog != null && materialDialog.isShowing()) materialDialog.dismiss();
        Snackbar.make(goodieBagCollectionToolbar, message, Snackbar.LENGTH_LONG).show();
    }

    @OnClick(R.id.goodieBagCameraImageView)
    public void onCameraImageViewPress() {
        Intent qrCodeActivityIntent = new Intent(GoodieBagCollectionActivity.this, QRCodeReaderActivity.class);
        qrCodeActivityIntent.putExtra("USER", currentUser);
        startActivity(qrCodeActivityIntent);
    }

    @Override
    protected void onDestroy() {
        unbinder.unbind();
        super.onDestroy();
    }
}
