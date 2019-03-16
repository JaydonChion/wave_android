package letswave.co.in.wave.Activities;

import android.content.SharedPreferences;
import android.nfc.NfcAdapter;
import android.nfc.NfcManager;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import letswave.co.in.wave.Adapters.MainViewPagerAdapter;
import letswave.co.in.wave.Models.User;
import letswave.co.in.wave.R;

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
