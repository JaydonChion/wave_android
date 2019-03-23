package com.wave.identity.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatEditText;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.wave.identity.Models.User;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import letswave.co.in.wave.R;

public class PhoneNumberActivity extends AppCompatActivity {

    @BindView(R.id.phoneNextTextView)
    TextView phoneNextTextView;
    @BindView(R.id.phoneNumberEditText)
    AppCompatEditText phoneNumberEditText;
    @BindString(R.string.domain)
    String domainUrl;
    @BindView(R.id.phoneNumberConfirmationCodeEditText)
    AppCompatEditText phoneNumberConfirmationCodeEditText;
    @BindView(R.id.phoneNumberEnterConfirmationCodeTextView)
    TextView phoneNumberEnterConfirmationCodeTextView;
    @BindView(R.id.phoneNUmberResendCodeTextView)
    TextView phoneNUmberResendCodeTextView;
    @BindView(R.id.phoneNumberChangeItTextView)
    TextView phoneNumberChangeItTextView;
    @BindView(R.id.phoneNumberConfirmationCodeLinearLayout)
    LinearLayout phoneNumberConfirmationCodeLinearLayout;
    @BindView(R.id.phoneNumberEditTextLinearLayout)
    LinearLayout phoneNumberEditTextLinearLayout;

    private Unbinder unbinder;
    private FirebaseAuth firebaseAuth;
    private RequestQueue requestQueue;
    private MaterialDialog materialDialog;
    private User currentUser;
    private String verificationId;
    private PhoneAuthProvider.ForceResendingToken resendingToken;
    private String phoneNumber;

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
        currentUser = getIntent().getParcelableExtra("USER");
    }

    private void notifyMessage(String message) {
        if (materialDialog != null && materialDialog.isShowing()) materialDialog.dismiss();
        Snackbar.make(phoneNextTextView, message, Snackbar.LENGTH_LONG).show();
    }

    @OnClick(R.id.phoneNextTextView)
    public void onNextTextViewPress() {
        if (phoneNumberEditTextLinearLayout.getVisibility()==View.VISIBLE) {
            phoneNumber = Objects.requireNonNull(phoneNumberEditText.getText()).toString();
            if (phoneNumber.isEmpty() || phoneNumber.length() < 10)
                notifyMessage("Please enter a valid phone number");
            else {
                materialDialog = new MaterialDialog.Builder(PhoneNumberActivity.this)
                        .title(getString(R.string.app_name))
                        .content("Verifying Phone Number")
                        .progress(true, 0)
                        .show();
                phoneNumberEnterConfirmationCodeTextView.setText("Enter the confirmation code we sent to "+phoneNumber+". If you didn't get it, we can");
                PhoneAuthProvider.getInstance().verifyPhoneNumber("+91" + phoneNumber, 60, TimeUnit.SECONDS, PhoneNumberActivity.this, new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                    @Override
                    public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
                        firebaseAuth.signInWithCredential(phoneAuthCredential).addOnCompleteListener(task -> {
                            if (task.isSuccessful()) updateCurrentUser(phoneNumber);
                            else notifyMessage(Objects.requireNonNull(task.getException()).getMessage());
                        });
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
                        phoneNumberEditTextLinearLayout.setVisibility(View.GONE);
                        phoneNumberConfirmationCodeLinearLayout.setVisibility(View.VISIBLE);
                        if (materialDialog!=null && materialDialog.isShowing()) materialDialog.dismiss();
                    }
                });
            }
        } else if (phoneNumberConfirmationCodeLinearLayout.getVisibility()==View.VISIBLE) {
            String enteredOtp = Objects.requireNonNull(phoneNumberConfirmationCodeEditText.getText()).toString();
            verifyVerificationCode(enteredOtp, phoneNumber);
        }
    }

    private void verifyVerificationCode(String codeSentViaSms, String phoneNumber) {
        PhoneAuthCredential phoneAuthCredential = PhoneAuthProvider.getCredential(verificationId, codeSentViaSms);
        firebaseAuth.signInWithCredential(phoneAuthCredential).addOnCompleteListener(task -> {
            if (task.isSuccessful()) updateCurrentUser(phoneNumber);
            else notifyMessage(Objects.requireNonNull(task.getException()).getMessage());
        });
    }

    private void updateCurrentUser(String phoneNumber) {
        try {
            String requestUrl = domainUrl + "/users/update/phone/" + currentUser.getId();
            JSONObject updateJsonObject = new JSONObject();
            updateJsonObject.put("phone", phoneNumber);
            JsonObjectRequest updatePhoneNumberRequest = new JsonObjectRequest(Request.Method.PUT, requestUrl, updateJsonObject, response -> {
                try {
                    int affectedRows = response.getInt("affectedRows");
                    if (affectedRows == 1) {
                        currentUser.setPhone(phoneNumber);
                        firebaseAuth.signOut();
                        Intent mainActivityIntent = new Intent(PhoneNumberActivity.this, SignInActivity.class);
                        startActivity(mainActivityIntent);
                        finish();
                    }
                } catch (JSONException e) {
                    notifyMessage(e.getMessage());
                }
            }, error -> notifyMessage(error.getMessage()));
            requestQueue.add(updatePhoneNumberRequest);
        } catch (JSONException e) {
            notifyMessage(e.getMessage());
        }
    }

    @OnClick(R.id.phoneNumberChangeItTextView)
    public void onChangePhoneNumberTextViewPress() {
        phoneNumberConfirmationCodeLinearLayout.setVisibility(View.GONE);
        phoneNumberEditTextLinearLayout.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onDestroy() {
        unbinder.unbind();
        super.onDestroy();
    }
}
