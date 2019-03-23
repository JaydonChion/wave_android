package com.wave.identity.Activities;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.NfcEvent;
import android.nfc.NfcManager;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import letswave.co.in.wave.R;

import static android.nfc.NdefRecord.createMime;

public class HiveActivity extends AppCompatActivity implements NfcAdapter.CreateNdefMessageCallback {

    @BindView(R.id.hiveToolbar)
    Toolbar hiveToolbar;

    private Unbinder unbinder;
    private NfcAdapter adapter ;
    private NfcManager manager;
    private static int  MY_PERMISSIONS_REQUEST_READ_CONTACTS =119;
    static String message ="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hive);

        initializeViews();
        initializeComponents();
        manager =  (NfcManager) getApplicationContext().getSystemService(Context.NFC_SERVICE);
        adapter= manager.getDefaultAdapter();
        if (checkNFC()) startNfcSettingsActivity();
    }

    private void initializeViews() {
        unbinder = ButterKnife.bind(HiveActivity.this);
        setSupportActionBar(hiveToolbar);
        hiveToolbar.setTitleTextColor(Color.WHITE);
    }

    private void initializeComponents() {

    }


    @Override
    public NdefMessage createNdefMessage(NfcEvent event) {
        NdefMessage msg;
        msg = new NdefMessage(new NdefRecord[] { createMime("application/vnd.com.example.android.beam", message.getBytes())});
        return msg;
    }

    public boolean checkNFC(){
        if (ContextCompat.checkSelfPermission(HiveActivity.this, Manifest.permission.NFC) != PackageManager.PERMISSION_GRANTED ) {
            if (!ActivityCompat.shouldShowRequestPermissionRationale(HiveActivity.this, Manifest.permission.NFC)) {
                ActivityCompat.requestPermissions(HiveActivity.this,
                        new String[]{Manifest.permission.NFC},
                        MY_PERMISSIONS_REQUEST_READ_CONTACTS);
            }
        } else if(adapter!=null) {
            return !adapter.isEnabled();
        }
        return false;
    }

    protected void startNfcSettingsActivity() {
        Snackbar.make(hiveToolbar, "Please enable NFC", Snackbar.LENGTH_INDEFINITE)
                .setAction("SETTINGS", v -> startActivity(new Intent(android.provider.Settings.ACTION_NFC_SETTINGS))).setActionTextColor(Color.YELLOW)
                .show();
    }

    @Override

    public void onResume() {
        super.onResume();
        if(checkNFC()) startNfcSettingsActivity();
        else if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(getIntent().getAction())) processIntent(getIntent());
    }

    @Override
    public void onNewIntent(Intent intent) {
        setIntent(intent);
    }
    void processIntent(Intent intent) {
        Parcelable[] rawMsgs = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
        NdefMessage msg = (NdefMessage) rawMsgs[0];
    }

    @Override
    protected void onDestroy() {
        unbinder.unbind();
        super.onDestroy();
    }

    @Override
    protected void onStart() {
        super.onStart();
        checkNFC();
    }
}
