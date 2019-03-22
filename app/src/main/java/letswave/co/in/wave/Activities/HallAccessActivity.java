package letswave.co.in.wave.Activities;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.customtabs.CustomTabsIntent;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import letswave.co.in.wave.R;

public class HallAccessActivity extends AppCompatActivity {
    private static final String SERVICE_ACTION = "android.support.customtabs.action.CustomTabsService";
    private static final String CHROME_PACKAGE = "com.android.chrome";
    @BindView(R.id.hallAccessToolbar)
    Toolbar hallAccessToolbar;

    private Unbinder unbinder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hall_access);
        ButterKnife.bind(this);


        initializeViews();
        initializeComponents();
    }

    private void initializeViews() {
        unbinder = ButterKnife.bind(HallAccessActivity.this);
        setSupportActionBar(hallAccessToolbar);
        hallAccessToolbar.setTitleTextColor(Color.WHITE);
    }

    private void initializeComponents() {

    }

    @Override
    protected void onDestroy() {
        unbinder.unbind();
        super.onDestroy();
    }


    public void openWaveWebsite(View view){
        if(isChromeCustomTabsSupported(getApplicationContext())){
            CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
            CustomTabsIntent customTabsIntent = builder.build();
            customTabsIntent.launchUrl(this, Uri.parse("https://wavenow.wixsite.com/wave"));
        }else{

            Intent viewIntent = new Intent("android.intent.action.VIEW", Uri.parse("https://wavenow.wixsite.com/wave"));
            startActivity(viewIntent);
        }
    }



    private static boolean isChromeCustomTabsSupported(@NonNull final Context context) {
        Intent serviceIntent = new Intent(SERVICE_ACTION);
        serviceIntent.setPackage(CHROME_PACKAGE);
        List<ResolveInfo> resolveInfos = context.getPackageManager().queryIntentServices(serviceIntent, 0);
        return !(resolveInfos == null || resolveInfos.isEmpty());
    }
}
