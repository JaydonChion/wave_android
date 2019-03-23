package com.wave.identity.Activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.WindowManager;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.wave.identity.Models.User;

import java.util.Arrays;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import letswave.co.in.wave.R;

public class LeeWeeNamActivity extends AppCompatActivity {

    @BindView(R.id.leeWeeNamToolbar)
    Toolbar leeWeeNamToolbar;
    @BindView(R.id.leeWeeNamBarCodeImageView)
    ImageView leeWeeNamBarCodeImageView;
    @BindView(R.id.leeWeeNamContentImageView)
    ImageView leeWeeNamContentImageView;

    private static final int RC_WRITE_PERM = 511;
    private Unbinder unbinder;
    private User currentUser;
    static int currentBrightnessValue = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        setContentView(R.layout.activity_lee_wee_nam);

        initializeViews();
        initializeComponents();
    }

    private void initializeViews() {
        unbinder = ButterKnife.bind(LeeWeeNamActivity.this);
        setSupportActionBar(leeWeeNamToolbar);
        leeWeeNamToolbar.setTitleTextColor(Color.WHITE);
        Glide.with(getApplicationContext()).load(R.drawable.access1).into(leeWeeNamContentImageView);
    }

    private void initializeComponents() {
        currentUser = getIntent().getParcelableExtra("USER");
        generateBarCode(currentUser.getAuthorityIssuedId());
        youDesirePermissionCode(LeeWeeNamActivity.this);
    }

    private void generateBarCode(String content) {
        try {
            Glide.with(getApplicationContext()).load(createBarcodeBitmap(content,370,93)).into(leeWeeNamBarCodeImageView);
        } catch (WriterException e) {
            Log.v("GEN_BAR", e.getMessage());
        }
    }

    private Bitmap createBarcodeBitmap(String data, int width, int height) throws WriterException {
        MultiFormatWriter writer = new MultiFormatWriter();
        String finalData = Uri.encode(data);
        BitMatrix bm = writer.encode(finalData, BarcodeFormat.CODE_128, width, 1);
        int bmWidth = bm.getWidth();
        Bitmap imageBitmap = Bitmap.createBitmap(bmWidth, height, Bitmap.Config.ARGB_8888);
        for (int i = 0; i < bmWidth; i++) {
            int[] column = new int[height];
            Arrays.fill(column, bm.get(i, 0) ? Color.BLACK : Color.WHITE);
            imageBitmap.setPixels(column, 0, 1, i, 0, 1, height);
        }
        return imageBitmap;
    }

    private void setScreenBrightness() {
        //get current brightness
        try {
            currentBrightnessValue =Settings.System.getInt(
                    getApplicationContext().getContentResolver(),
                    Settings.System.SCREEN_BRIGHTNESS);
        } catch (Settings.SettingNotFoundException e) {
            currentBrightnessValue = 100;
        }


        Settings.System.putInt(getContentResolver(), Settings.System.SCREEN_BRIGHTNESS, 255);
        WindowManager.LayoutParams layoutpars = getWindow().getAttributes();
        layoutpars.screenBrightness = 1f;
        getWindow().setAttributes(layoutpars);
    }

    public void youDesirePermissionCode(Activity context){
        boolean permission;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            permission = Settings.System.canWrite(context);
        } else {
            permission = ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_SETTINGS) == PackageManager.PERMISSION_GRANTED;
        }
        if (permission) {
            setScreenBrightness();
        }  else {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS);
                intent.setData(Uri.parse("package:" + context.getPackageName()));
                context.startActivityForResult(intent, RC_WRITE_PERM);
            } else {
                ActivityCompat.requestPermissions(context, new String[]{Manifest.permission.WRITE_SETTINGS}, RC_WRITE_PERM);
            }
        }
    }

    @SuppressLint("NewApi")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_WRITE_PERM && Settings.System.canWrite(LeeWeeNamActivity.this)){
            Log.v("TAG", "LeeWeeNamActivity.CODE_WRITE_SETTINGS_PERMISSION success");
            setScreenBrightness();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == RC_WRITE_PERM && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            setScreenBrightness();
        }
    }

    @Override
    protected void onDestroy() {
        unbinder.unbind();
        super.onDestroy();
        restorebrightness();
    }


    @Override
    public void onPause() {
        super.onPause();
        restorebrightness();
    }

    @Override
    public void onResume() {
        super.onResume();
        restorebrightness();
    }



    private void restorebrightness(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if(Settings.System.canWrite(getApplicationContext().getApplicationContext())) {
                //increase brightness programmatically
                Settings.System.putInt(
                        getApplicationContext().getApplicationContext().getContentResolver(),
                        Settings.System.SCREEN_BRIGHTNESS,
                        currentBrightnessValue
                );
            }
        }
    }


}