package com.wave.identity.Activities;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.wave.identity.Models.User;

import org.json.JSONException;

import java.util.Objects;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import letswave.co.in.wave.R;

public class SignInActivity extends AppCompatActivity {

    @BindView(R.id.signInImageView)
    ImageView signInImageView;
    @BindView(R.id.signInEmailAddressEditText)
    TextInputEditText signInEmailAddressEditText;
    @BindView(R.id.signInPasswordEditText)
    TextInputEditText signInPasswordEditText;
    @BindString(R.string.domain)
    String domain;

    private FirebaseAuth firebaseAuth;
    private Unbinder unbinder;
    private RequestQueue requestQueue;
    private MaterialDialog materialDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_sign_in);

        initializeViews();
        initializeComponents();
    }

    private void initializeViews() {
        unbinder = ButterKnife.bind(SignInActivity.this);
        requestQueue = Volley.newRequestQueue(getApplicationContext());
        Glide.with(getApplicationContext()).load(R.drawable.smartphone_two).into(signInImageView);
    }

    private void initializeComponents() {
        firebaseAuth = FirebaseAuth.getInstance();
    }

    /*
    @OnClick(R.id.signInSignUpTextView)
    public void onSignInSignUpTextViewPress() {
        startActivity(new Intent(SignInActivity.this, SignUpActivity.class));
    }
    */

    @OnClick(R.id.signInButton)
    public void onSignInButtonPress() {
        String email = Objects.requireNonNull(signInEmailAddressEditText.getText()).toString();
        String password = Objects.requireNonNull(signInPasswordEditText.getText()).toString();
        if (TextUtils.isEmpty(email)) notifyMessage("Email Address is empty");
        else if (TextUtils.isEmpty(password)) notifyMessage("Password field is empty");
        else {
            materialDialog = new MaterialDialog.Builder(SignInActivity.this)
                    .title(R.string.app_name)
                    .content("Signing In..")
                    .progress(true, 0)
                    .titleColorRes(android.R.color.black)
                    .contentColorRes(R.color.colorTextDark)
                    .show();
            firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
                if (task.isSuccessful()) fetchCurrentUser(Objects.requireNonNull(firebaseAuth.getCurrentUser()).getUid());
                else notifyMessage(Objects.requireNonNull(task.getException()).getMessage());
            });
        }
    }

    private void fetchCurrentUser(String userId) {
        String requestUrl = domain+"/users/"+userId;
        JsonObjectRequest userObjectRequest = new JsonObjectRequest(Request.Method.GET, requestUrl, null, response -> {
            try {
                String name = response.getString("name");
                String authorityName = response.getString("authority_name");
                String authorityIssuedId = response.getString("authority_id");
                String email = response.getString("email");
                String photo = response.getString("photo");
                String phone = response.getString("phone");
                User currentUser = new User(userId, authorityName, authorityIssuedId, name, email, photo, phone);
                Intent mainActivityIntent = new Intent(SignInActivity.this, MainActivity.class);
                mainActivityIntent.putExtra("USER", currentUser);
                if (materialDialog!=null && materialDialog.isShowing()) materialDialog.dismiss();
                startActivity(mainActivityIntent);
                finish();
            } catch (JSONException e) {
                Snackbar.make(signInImageView, e.getMessage(), Snackbar.LENGTH_INDEFINITE)
                        .setAction("RETRY", v -> fetchCurrentUser(userId.toLowerCase()))
                        .setActionTextColor(Color.YELLOW)
                        .show();
            }
        }, error -> {
            if (error.networkResponse.statusCode==400) notifyMessage("Account with email "+userId+" does not exist!");
            else
                Snackbar.make(signInImageView, error.getMessage(), Snackbar.LENGTH_INDEFINITE)
                        .setAction("RETRY", v -> fetchCurrentUser(userId.toLowerCase()))
                        .setActionTextColor(Color.YELLOW)
                        .show();
        });
        requestQueue.add(userObjectRequest);
    }


    private void notifyMessage(String message) {
        if (materialDialog!=null && materialDialog.isShowing()) materialDialog.dismiss();
        Snackbar.make(signInImageView, message, Snackbar.LENGTH_LONG).show();
    }

    @Override
    protected void onDestroy() {
        unbinder.unbind();
        super.onDestroy();
    }
}
