package letswave.co.in.wave.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatEditText;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import letswave.co.in.wave.Models.User;
import letswave.co.in.wave.R;

public class PhoneNumberActivity extends AppCompatActivity {

    @BindView(R.id.phoneNextTextView)
    TextView phoneNextTextView;
    @BindView(R.id.phoneNumberEditText)
    AppCompatEditText phoneNumberEditText;
    @BindString(R.string.domain)
    String domainUrl;

    private Unbinder unbinder;
    private FirebaseAuth firebaseAuth;
    private RequestQueue requestQueue;
    private MaterialDialog materialDialog;
    private User currentUser;
    private String verificationId;
    private PhoneAuthProvider.ForceResendingToken resendingToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_phone_number);

        initializeViews();
        initializeComponents();
    }

    private void initializeViews() {
        unbinder = ButterKnife.bind(PhoneNumberActivity.this);
    }

    private void initializeComponents() {
        firebaseAuth = FirebaseAuth.getInstance();
        requestQueue = Volley.newRequestQueue(getApplicationContext());
        fetchCurrentUser();
    }

    private void fetchCurrentUser() {
        String requestUrl = domainUrl+"/users/"+ Objects.requireNonNull(firebaseAuth.getCurrentUser()).getUid();
        JsonObjectRequest userObjectRequest = new JsonObjectRequest(Request.Method.GET, requestUrl, null, response -> {
            try {
                String id = response.getString("ID");
                String authorityIssuerName = response.getString("authority_issuer_name");
                String authorityIssuedId = response.getString("authority_issued_id");
                String name = response.getString("name");
                String email = response.getString("email");
                String photo = response.getString("photo");
                String phone = response.getString("phone");
                currentUser = new User(id, authorityIssuerName, authorityIssuedId, name, email, photo, phone);
            } catch (JSONException e) {
                notifyMessage(e.getMessage());
            }
        }, error -> notifyMessage(error.getMessage()));
        requestQueue.add(userObjectRequest);
    }

    private void notifyMessage(String message) {
        if (materialDialog!=null && materialDialog.isShowing()) materialDialog.dismiss();
        Snackbar.make(phoneNextTextView, message, Snackbar.LENGTH_LONG).show();
    }

    @OnClick(R.id.phoneNextTextView)
    public void onNextTextViewPress() {
        String phoneNumber = Objects.requireNonNull(phoneNumberEditText.getText()).toString();
        if (phoneNumber.isEmpty() || phoneNumber.length()<10) notifyMessage("Please enter a valid phone number");
        else {
            PhoneAuthProvider.getInstance().verifyPhoneNumber(phoneNumber, 60, TimeUnit.SECONDS, PhoneNumberActivity.this, new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                @Override
                public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
                    updateCurrentUser(phoneNumber);
                }

                @Override
                public void onVerificationFailed(FirebaseException e) {
                    notifyMessage(e.getMessage());
                }

                @Override
                public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                    super.onCodeSent(s, forceResendingToken);
                    verificationId = s;
                    resendingToken = forceResendingToken;
                }
            });
        }
    }

    private void updateCurrentUser(String phoneNumber) {
        try {
            String requestUrl = domainUrl+"/users/update/phone/"+firebaseAuth.getUid();
            JSONObject updateJsonObject = new JSONObject();
            updateJsonObject.put("phone", phoneNumber);
            JsonObjectRequest updatePhoneNumberRequest = new JsonObjectRequest(Request.Method.PUT, requestUrl, updateJsonObject, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        int affectedRows = response.getInt("affectedRows");
                        if (affectedRows==1) {
                            currentUser.setPhone(phoneNumber);
                            Intent mainActivityIntent = new Intent(PhoneNumberActivity.this, MainActivity.class);
                            mainActivityIntent.putExtra("USER", currentUser);
                            startActivity(mainActivityIntent);
                            finish();
                        }
                    } catch (JSONException e) {
                        notifyMessage(e.getMessage());
                    }
                }
            }, error -> notifyMessage(error.getMessage()));
            requestQueue.add(updatePhoneNumberRequest);
        } catch (JSONException e) {
            notifyMessage(e.getMessage());
        }
    }

    @Override
    protected void onDestroy() {
        unbinder.unbind();
        super.onDestroy();
    }
}
