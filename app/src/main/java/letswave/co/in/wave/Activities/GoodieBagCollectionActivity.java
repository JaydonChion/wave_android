package letswave.co.in.wave.Activities;

import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.afollestad.materialdialogs.MaterialDialog;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Objects;

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

    private Unbinder unbinder;
    private MaterialDialog materialDialog;
    private RequestQueue requestQueue;
    private User currentUser;

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

    private void updateUserStatusInEvent() {
        try {
            materialDialog = new MaterialDialog.Builder(GoodieBagCollectionActivity.this)
                    .title(R.string.app_name)
                    .content("Performing Magic")
                    .progress(true, 0)
                    .titleColorRes(android.R.color.black)
                    .contentColorRes(R.color.colorTextDark)
                    .show();
            String userId = currentUser.getId();
            String eventId = "5c8c640383fec557167417a9";
            String requestUrl = baseServerUrl+"/participants?user_id="+userId+"&event_id="+eventId;
            JSONObject putRequestBody = new JSONObject();
            putRequestBody.put("status", "COMPLETED");
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.PUT, requestUrl, putRequestBody, response -> {
                try {
                    if (response.getString("status").equals("COMPLETED")) notifyMessage("Thank you for using this service on our App!\nStay tuned for more features that make your life easier.");
                } catch (JSONException e) {
                    notifyMessage(e.getMessage());
                }
            }, error -> notifyMessage(error.getMessage()));
            requestQueue.add(jsonObjectRequest);
        } catch (JSONException e) {
            notifyMessage(e.getMessage());
        }
    }

    private void notifyMessage(String message) {
        if (materialDialog!=null && materialDialog.isShowing()) materialDialog.dismiss();
        Snackbar.make(goodieBagCollectionToolbar, message, Snackbar.LENGTH_LONG).show();
    }

    @OnClick(R.id.goodieBagCameraImageView)
    public void onCameraImageViewPress() {
        materialDialog = new MaterialDialog.Builder(GoodieBagCollectionActivity.this)
                .customView(R.layout.dialog_redeem_goodie_bag, true)
                .show();
        Objects.requireNonNull(materialDialog.getCustomView()).findViewById(R.id.goodieBagDialogRedeemButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                materialDialog.dismiss();
                updateUserStatusInEvent();
            }
        });
    }

    @Override
    protected void onDestroy() {
        unbinder.unbind();
        super.onDestroy();
    }
}
