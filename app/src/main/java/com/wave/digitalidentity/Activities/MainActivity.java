package com.wave.digitalidentity.Activities;

import android.content.SharedPreferences;
import android.nfc.NfcAdapter;
import android.nfc.NfcManager;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.FirebaseAuth;
import com.wave.digitalidentity.Adapters.MainViewPagerAdapter;
import com.wave.digitalidentity.Models.User;

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

    private FirebaseAuth firebaseAuth;
    private Unbinder unbinder;
    private MainViewPagerAdapter mainViewPagerAdapter;
    private User currentUser;
    private NfcManager nfcManager;
    private NfcAdapter nfcAdapter;
    private SharedPreferences.Editor editor;
    private FirebaseAnalytics firebaseAnalytics;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        currentUser = getIntent().getParcelableExtra("USER");
        initializeViews();
        initializeComponents();
    }

    private void initializeViews() {
        unbinder = ButterKnife.bind(MainActivity.this);
        Glide.with(getApplicationContext()).load(R.drawable.logo).into(mainToolbarLogoImageView);
        mainViewPagerAdapter = new MainViewPagerAdapter(getSupportFragmentManager());
        mainViewPager.setAdapter(mainViewPagerAdapter);
        mainTabLayout.setupWithViewPager(mainViewPager);
    }

    private void initializeComponents() {
        firebaseAuth = FirebaseAuth.getInstance();
        editor = getSharedPreferences("SP", MODE_PRIVATE).edit();
        editor.putString("email", currentUser.getEmail());
        editor.apply();
        nfcManager = (NfcManager) getSystemService(NFC_SERVICE);
        nfcAdapter = nfcManager.getDefaultAdapter();
        if (nfcAdapter==null || !nfcAdapter.isEnabled()) {
            //Open NFC settings
        }
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
        mainViewPagerAdapter = new MainViewPagerAdapter(getSupportFragmentManager());
        mainViewPager.setAdapter(mainViewPagerAdapter);
        mainTabLayout.setupWithViewPager(mainViewPager);
    }
}
