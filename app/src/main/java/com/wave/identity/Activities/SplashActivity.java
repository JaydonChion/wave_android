package com.wave.identity.Activities;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.wave.identity.Models.User;

import org.json.JSONException;

import java.util.List;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import letswave.co.in.wave.R;

public class SplashActivity extends AppCompatActivity {

    @BindView(R.id.splashLogoImageView)
    ImageView splashLogoImageView;
    @BindString(R.string.domain)
    String domain;

    private RequestQueue requestQueue;
    private FirebaseAuth firebaseAuth;
    private Unbinder unbinder;
    private static final int SPLASH_DELAY_LENGTH = 1000;
    private String email;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().setStatusBarColor(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimary));
        getWindow().setNavigationBarColor(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimary));
        setContentView(R.layout.activity_splash);

        initializeViews();
        initializeComponents();
    }

    private void initializeViews() {
        unbinder = ButterKnife.bind(SplashActivity.this);
        Glide.with(getApplicationContext()).load(R.drawable.logo).into(splashLogoImageView);
    }

    private void initializeComponents() {
        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        requestQueue = Volley.newRequestQueue(getApplicationContext());
        SharedPreferences prefs = getSharedPreferences("SP", MODE_PRIVATE);
        email = prefs.getString("email", null);
        Dexter.withActivity(SplashActivity.this)
                .withPermissions(
                        Manifest.permission.CAMERA,
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.NFC,
                        Manifest.permission.VIBRATE,
                        Manifest.permission.INTERNET
                ).withListener(new MultiplePermissionsListener() {
            @Override
            public void onPermissionsChecked(MultiplePermissionsReport report) {
                if (report.areAllPermissionsGranted()) {
                    if (email==null) {
                        new Handler().postDelayed(() -> {
                            startActivity(new Intent(SplashActivity.this, SignInActivity.class));
                            finish();
                        }, SPLASH_DELAY_LENGTH);
                    } else fetchCurrentUser();
                } else if (report.isAnyPermissionPermanentlyDenied()) {
                    Snackbar.make(splashLogoImageView, "Please provide all the required permissions to continue", Snackbar.LENGTH_INDEFINITE)
                            .setAction("RETRY", v -> initializeComponents()).setActionTextColor(Color.YELLOW)
                            .show();
                }
            }

            @Override
            public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                token.continuePermissionRequest();
            }
        }).withErrorListener(error -> Snackbar.make(splashLogoImageView, error.toString(), Snackbar.LENGTH_INDEFINITE).setAction("RETRY", v -> initializeComponents()).setActionTextColor(Color.YELLOW).show()).check();
    }

    private void fetchCurrentUser() {
        String requestUrl = domain+"/users/"+email;
        JsonObjectRequest userObjectRequest = new JsonObjectRequest(Request.Method.GET, requestUrl, null, response -> {
            try {
                String userId = response.getString("_id");
                String name = response.getString("name");
                String authorityName = response.getString("authority_name");
                String authorityIssuedId = response.getString("authority_id");
                email = response.getString("email");
                String photo = response.getString("photo");
                String phone = response.getString("phone");
                User currentUser = new User(userId, authorityName, authorityIssuedId, name, email, photo, phone);
                if (response.has("phone")) currentUser.setPhone(response.getString("phone"));
                Intent mainActivityIntent = new Intent(SplashActivity.this, MainActivity.class);
                mainActivityIntent.putExtra("USER", currentUser);
                startActivity(mainActivityIntent);
                finish();
            } catch (JSONException e) {
                Snackbar.make(splashLogoImageView, e.getMessage(), Snackbar.LENGTH_INDEFINITE)
                        .setAction("RETRY", v -> fetchCurrentUser())
                        .setActionTextColor(Color.YELLOW)
                        .show();
            }
        }, error -> Snackbar.make(splashLogoImageView, error.getMessage(), Snackbar.LENGTH_INDEFINITE)
                .setAction("RETRY", v -> fetchCurrentUser())
                .setActionTextColor(Color.YELLOW)
                .show());
        requestQueue.add(userObjectRequest);
    }

    @Override
    protected void onDestroy() {
        unbinder.unbind();
        super.onDestroy();
    }
}
