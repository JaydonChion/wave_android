package com.wave.digitalidentity.Activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.nfc.NfcAdapter;
import android.nfc.NfcManager;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.FirebaseAuth;
import com.wave.digitalidentity.Adapters.MainViewPagerAdapter;
import com.wave.digitalidentity.Models.User;

import org.json.JSONException;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import letswave.co.in.wave.R;

import static com.google.firebase.analytics.FirebaseAnalytics.Event.SELECT_CONTENT;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.mainToolbarLogoImageView)
    ImageView mainToolbarLogoImageView;
    @BindView(R.id.mainTabLayout)
    TabLayout mainTabLayout;
    @BindView(R.id.mainViewPager)
    ViewPager mainViewPager;
    @BindString(R.string.domain)
    String domain;

    private FirebaseAuth firebaseAuth;
    private Unbinder unbinder;
    private MainViewPagerAdapter mainViewPagerAdapter;
    private User currentUser;
    private NfcManager nfcManager;
    private NfcAdapter nfcAdapter;
    private SharedPreferences.Editor editor;
    private FirebaseAnalytics firebaseAnalytics;
    private SharedPreferences sharedPreferences;
    private RequestQueue requestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        unbinder = ButterKnife.bind(MainActivity.this);
        requestQueue = Volley.newRequestQueue(getApplicationContext());
        Glide.with(getApplicationContext()).load(R.drawable.logo).into(mainToolbarLogoImageView);

        sharedPreferences = getSharedPreferences("SP", MODE_PRIVATE);
        editor = sharedPreferences.edit();
        String email = sharedPreferences.getString("email", null);
        currentUser = getIntent().getParcelableExtra("USER");
        if (currentUser==null) {
            if(email==null) {
                startActivity(new Intent(MainActivity.this, SignInActivity.class));
                finish();
            }
            else fetchCurrentUser(email);
        } else {
            if (email==null) {
                editor = sharedPreferences.edit();
                editor.putString("email", currentUser.getEmail());
                editor.apply();
            }
            initializeViews();
            initializeComponents();
        }
    }

    private void fetchCurrentUser(String email) {
        String requestUrl = domain+"/users/"+email;
        JsonObjectRequest userObjectRequest = new JsonObjectRequest(Request.Method.GET, requestUrl, null, response -> {
            try {
                String userId = response.getString("_id");
                String name = response.getString("name");
                String authorityName = response.getString("authority_name");
                String authorityIssuedId = response.getString("authority_id");
                String photo = response.getString("photo");
                String phone = response.getString("phone");
                User user = new User(userId, authorityName, authorityIssuedId, name, email, photo, phone);
                setCurrentUser(user);
            } catch (JSONException e) {
                Snackbar.make(mainToolbarLogoImageView, e.getMessage(), Snackbar.LENGTH_INDEFINITE)
                        .setAction("RETRY", v -> fetchCurrentUser(email))
                        .setActionTextColor(Color.YELLOW)
                        .show();
            }
        }, error -> {
            if (error.networkResponse.statusCode==400) {
                startActivity(new Intent(MainActivity.this, SignInActivity.class));
                finish();
            }
            else Snackbar.make(mainToolbarLogoImageView, error.getMessage(), Snackbar.LENGTH_INDEFINITE)
                        .setAction("RETRY", v -> fetchCurrentUser(email))
                        .setActionTextColor(Color.YELLOW)
                        .show();
        });
        requestQueue.add(userObjectRequest);
    }

    private void initializeViews() {
        mainViewPagerAdapter = new MainViewPagerAdapter(getSupportFragmentManager());
        mainViewPager.setAdapter(mainViewPagerAdapter);
        mainTabLayout.setupWithViewPager(mainViewPager);
    }

    private void initializeComponents() {
        firebaseAuth = FirebaseAuth.getInstance();
        nfcManager = (NfcManager) getSystemService(NFC_SERVICE);
        nfcAdapter = nfcManager.getDefaultAdapter();
        firebaseAnalytics = FirebaseAnalytics.getInstance(MainActivity.this);
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, currentUser.getId());
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, currentUser.getName());
        firebaseAnalytics.logEvent(SELECT_CONTENT, bundle);
    }

    public SharedPreferences.Editor getEditor() {
        return editor;
    }

    @Override
    protected void onDestroy() {
        unbinder.unbind();
        super.onDestroy();
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public void setCurrentUser(User currentUser) {
        this.currentUser = currentUser;
        editor.putString("email", currentUser.getEmail());
        editor.apply();
        mainViewPagerAdapter = new MainViewPagerAdapter(getSupportFragmentManager());
        mainViewPager.setAdapter(mainViewPagerAdapter);
        mainTabLayout.setupWithViewPager(mainViewPager);
    }
}
