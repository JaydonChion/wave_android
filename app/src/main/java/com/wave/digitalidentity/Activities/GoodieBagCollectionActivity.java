package com.wave.digitalidentity.Activities;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.ImageView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.wave.digitalidentity.Models.User;

import org.json.JSONException;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
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
    private boolean participantCollected = false;

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
        fetchParticipant(currentUser.getId());
    }

    private void notifyMessage(String message) {
        if (materialDialog != null && materialDialog.isShowing()) materialDialog.dismiss();
        Snackbar.make(goodieBagCollectionToolbar, message, Snackbar.LENGTH_LONG).show();
    }

    @OnClick(R.id.goodieBagCameraImageView)
    public void onCameraImageViewPress() {
        if (participantCollected) {
            if (materialDialog != null && materialDialog.isShowing()) materialDialog.dismiss();
            materialDialog = new MaterialDialog.Builder(GoodieBagCollectionActivity.this)
                    .title(R.string.app_name)
                    .content("You have already redeemed the Goodie Bag!")
                    .titleColorRes(android.R.color.black)
                    .contentColorRes(R.color.colorTextDark)
                    .positiveText("OKAY")
                    .positiveColorRes(R.color.colorPrimary)
                    .onAny((dialog, which) -> finish())
                    .show();

        } else {
            Intent qrCodeActivityIntent = new Intent(GoodieBagCollectionActivity.this, QRCodeReaderActivity.class);
            qrCodeActivityIntent.putExtra("USER", currentUser);
            startActivity(qrCodeActivityIntent);
        }
    }

    private void fetchParticipant(String userId) {
        String requestUrl = baseServerUrl + "/participants/" + eventId + "/" + userId;
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, requestUrl, null, response -> {
            try {
                String participantStatusInEvent = response.getString("status");
                if (participantStatusInEvent.equals("COMPLETED")) participantCollected=true;
            } catch (JSONException e) {
                notifyMessage(e.getMessage());
            }
        }, error -> notifyMessage(error.getMessage()));
        requestQueue.add(jsonObjectRequest);
    }

    @Override
    protected void onDestroy() {
        unbinder.unbind();
        super.onDestroy();
    }
}
