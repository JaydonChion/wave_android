package letswave.co.in.wave.Activities;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.dlazaro66.qrcodereaderview.QRCodeReaderView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Objects;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import letswave.co.in.wave.Models.User;
import letswave.co.in.wave.R;

public class QRCodeReaderActivity extends AppCompatActivity {

    @BindView(R.id.qrCodeReaderActivityQrCodeReaderView)
    QRCodeReaderView qrCodeReaderView;
    @BindString(R.string.domain)
    String baseServerUrl;
    @BindView(R.id.qrCodeReaderActivityLogoImageView)
    ImageView qrCodeReaderActivityLogoImageView;

    private Unbinder unbinder;
    private MaterialDialog materialDialog;
    private RequestQueue requestQueue;
    private User currentUser;
    private String eventId = "5c8c640383fec557167417a9";
    private Vibrator vibrator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_qrcode_reader);

        initializeViews();
        initializeComponents();
    }

    @SuppressLint("MissingPermission")
    private void initializeViews() {
        unbinder = ButterKnife.bind(QRCodeReaderActivity.this);
        currentUser = getIntent().getParcelableExtra("USER");
        vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        requestQueue = Volley.newRequestQueue(getApplicationContext());
        Glide.with(getApplicationContext()).load(R.drawable.logo).into(qrCodeReaderActivityLogoImageView);
        qrCodeReaderView.setQRDecodingEnabled(true);
        qrCodeReaderView.setBackCamera();
        qrCodeReaderView.setOnQRCodeReadListener((text, points) -> {

            if ((materialDialog == null || !materialDialog.isShowing())) {
                if ((text.equals("https://wavenow.wixsite.com/wave"))) {
                    vibrator.vibrate(500);
                    materialDialog = new MaterialDialog.Builder(QRCodeReaderActivity.this)
                            .customView(R.layout.dialog_redeem_goodie_bag, true)
                            .show();
                    Objects.requireNonNull(materialDialog.getCustomView()).findViewById(R.id.goodieBagDialogRedeemButton).setOnClickListener(v -> {
                        materialDialog.dismiss();
                        fetchParticipantObject();
                    });

                } else {
                    vibrator.vibrate(500);
                    materialDialog = new MaterialDialog.Builder(QRCodeReaderActivity.this)
                            .title(R.string.app_name)
                            .content("Invalid QR code, please check with the booth assistants")
                            .titleColorRes(android.R.color.black)
                            .contentColorRes(R.color.colorTextDark)
                            .positiveText("OKAY")
                            .positiveColorRes(R.color.colorPrimary)
                            .onAny((dialog, which) -> finish())
                            .show();
                }
            }
        });
    }

    private void initializeComponents() {

    }

    private void fetchParticipantObject() {
        materialDialog = new MaterialDialog.Builder(QRCodeReaderActivity.this)
                .title(R.string.app_name)
                .content("Performing Magic :)")
                .progress(true, 0)
                .titleColorRes(android.R.color.black)
                .contentColorRes(R.color.colorTextDark)
                .show();
        String requestUrl = baseServerUrl + "/participants/" + eventId + "/" + currentUser.getId();
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, requestUrl, null, response -> {
            try {
                String participantStatusInEvent = response.getString("status");
                if (participantStatusInEvent.equals("AWAIT")) updateUserStatusInEvent();
                else {
                    if (materialDialog != null && materialDialog.isShowing())
                        materialDialog.dismiss();
                    materialDialog = new MaterialDialog.Builder(QRCodeReaderActivity.this)
                            .title(R.string.app_name)
                            .content("You have already redeemed the Goodie Bag!")
                            .titleColorRes(android.R.color.black)
                            .contentColorRes(R.color.colorTextDark)
                            .positiveText("OKAY")
                            .positiveColorRes(R.color.colorPrimary)
                            .onAny((dialog, which) -> finish())
                            .show();
                }
            } catch (JSONException e) {
                notifyMessage(e.getMessage());
                qrCodeReaderView.startCamera();
            }
        }, error -> notifyMessage(error.getMessage()));
        requestQueue.add(jsonObjectRequest);
    }

    private void updateUserStatusInEvent() {
        try {
            String userId = currentUser.getId();
            String requestUrl = baseServerUrl + "/participants?user_id=" + userId + "&event_id=" + eventId;
            JSONObject putRequestBody = new JSONObject();
            putRequestBody.put("status", "COMPLETED");
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.PUT, requestUrl, putRequestBody, response -> {
                try {
                    if (response.getString("status").equals("COMPLETED")) {
                        if (materialDialog != null && materialDialog.isShowing())
                            materialDialog.dismiss();
                        materialDialog = new MaterialDialog.Builder(QRCodeReaderActivity.this)
                                .title(R.string.app_name)
                                .content("Thank you for using this service on our App!\nStay tuned for more features that make your life easier.")
                                .titleColorRes(android.R.color.black)
                                .contentColorRes(R.color.colorTextDark)
                                .positiveText("OKAY")
                                .positiveColorRes(R.color.colorPrimary)
                                .onAny((dialog, which) -> finish())
                                .show();
                    }
                } catch (JSONException e) {
                    notifyMessage(e.getMessage());
                    qrCodeReaderView.startCamera();
                }
            }, error -> notifyMessage(error.getMessage()));
            requestQueue.add(jsonObjectRequest);
        } catch (JSONException e) {
            notifyMessage(e.getMessage());
        } finally {
            qrCodeReaderView.startCamera();
        }
    }

    private void notifyMessage(String message) {
        if (materialDialog != null && materialDialog.isShowing()) materialDialog.dismiss();
        Snackbar.make(qrCodeReaderView, message, Snackbar.LENGTH_LONG).show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        qrCodeReaderView.startCamera();
    }

    @Override
    protected void onPause() {
        super.onPause();
        qrCodeReaderView.stopCamera();
    }

    @Override
    protected void onDestroy() {
        unbinder.unbind();
        super.onDestroy();
    }
}
