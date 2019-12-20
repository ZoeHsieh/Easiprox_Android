package com.anxell.e5ar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.ScrollView;

import com.anxell.e5ar.custom.FontTextView;
import com.anxell.e5ar.custom.My2TextView;
import com.anxell.e5ar.custom.My4TextView;
import com.anxell.e5ar.custom.MySwitch;
import com.anxell.e5ar.transport.APPConfig;
import com.anxell.e5ar.transport.bpActivity;
import com.anxell.e5ar.util.Util;

/**
 * Created by Sean on 7/30/2017.
 */



public class UserSettingActivity extends bpActivity implements View.OnClickListener {
    private String TAG = UserSettingActivity.class.getSimpleName().toString();
    private Boolean debugFlag = false;
    private My4TextView mDeviceNameTV;
    private My2TextView mDeviceTimeTV;
    private My4TextView mDoorReLockTimeTV;
    private My2TextView mDoorActionTV;
    private My4TextView mAdminPWDTV;
    private My4TextView mAdminCardTV;
    private My2TextView mExpectLEVELTV;
    private My2TextView mTampLevelTV;
    private MySwitch mTampSwitch;
    private MySwitch mDoorSwitch;
    private ScrollView settingUI;
    private FontTextView versionTV;
    private ProgressBar loadDeviceDataBar;
    private String deviceBDDR = "";
    private String deviceModel = "";
    private double vr = 0.0;
    private double vrLimit = 1.02;
    /*------------------------*/
    private int curr_rssi_level = 0;
    public static int updateStatus = 0;
    public static final int up_none  = 0;
    public static final int up_rssi_level = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Initial(getLocalClassName());
        setContentView(R.layout.activity_setting);
        findViews();
        savedInstanceState = this.getIntent().getExtras();
        String deviceName = savedInstanceState.getString(APPConfig.deviceNameTag);
        deviceBDDR = savedInstanceState.getString(APPConfig.deviceBddrTag);
        deviceModel = savedInstanceState.getString(APPConfig.deviceModelTag);
        int rssi = savedInstanceState.getInt(APPConfig.RSSI_LEVEL_Tag);
        curr_rssi_level = APPConfig.Convert_RSSI_to_LEVEL(rssi);
        mDeviceNameTV.setValue(deviceName);
        Util.debugMessage(TAG,"curr_rssi="+rssi,debugFlag);
        int expectLevel = loadDeviceRSSILevel(deviceBDDR);
        Util.debugMessage(TAG,"expectLevel="+expectLevel,debugFlag);
        mExpectLEVELTV.setValue(""+expectLevel);
        setListeners();

    }

    @Override
    protected void onRestart() {
        super.onRestart();

        switch(updateStatus){



            case up_rssi_level:
                int expectLEVEL = loadDeviceRSSILevel(deviceBDDR);
                Util.debugMessage(TAG,"expectLevel="+ expectLEVEL,debugFlag);
                mExpectLEVELTV.setValue(""+expectLEVEL);
                break;

        }
        updateStatus = up_none;
    }

    @Override
    protected void onStop() {
        super.onStop();
        //unRegisterReceiver(this);
    }

    private void findViews() {
        mDeviceNameTV = (My4TextView) findViewById(R.id.deviceName);
        mDeviceTimeTV = (My2TextView) findViewById(R.id.deviceTime);
        mDeviceTimeTV.setVisibility(View.GONE);
        mDoorReLockTimeTV = (My4TextView) findViewById(R.id.doorReLockTime);
        mDoorReLockTimeTV.setVisibility(View.GONE);
        mDoorActionTV = (My2TextView) findViewById(R.id.doorLockAction);
        mDoorActionTV.setVisibility(View.GONE);
        settingUI = (ScrollView) findViewById(R.id.SettingUI);
        mAdminCardTV = (My4TextView) findViewById(R.id.AdminCard);
        mAdminCardTV.setVisibility(View.GONE);
        mTampLevelTV = (My2TextView)findViewById(R.id.sensor_Level);
        mTampLevelTV.setVisibility(View.GONE);
        mTampSwitch = (MySwitch) findViewById(R.id.tamperSensor);
        mTampSwitch.setVisibility(View.GONE);
        mDoorSwitch = (MySwitch) findViewById(R.id.doorSensor);
        mDoorSwitch.setVisibility(View.GONE);
        versionTV = (FontTextView) findViewById(R.id.version);
        versionTV.setVisibility(View.GONE);
        findViewById(R.id.setting_fw_vr_title).setVisibility(View.GONE);
        mAdminPWDTV = (My4TextView) findViewById(R.id.AdminPWD);
        mAdminPWDTV.setVisibility(View.GONE);
        mExpectLEVELTV = (My2TextView) findViewById(R.id.proximityReadRange);
        loadDeviceDataBar = (ProgressBar)findViewById(R.id.setting_loadingBar);
       loadDeviceDataBar.setVisibility(View.GONE);
        findViewById(R.id.user).setVisibility(View.GONE);
        findViewById(R.id.history).setVisibility(View.GONE);
        findViewById(R.id.backup).setVisibility(View.GONE);
        findViewById(R.id.restore).setVisibility(View.GONE);

    }


    private void setListeners() {
        findViewById(R.id.proximityReadRange).setOnClickListener(this);
        findViewById(R.id.aboutUs).setOnClickListener(this);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        overridePendingTransitionLeftToRight();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.proximityReadRange:
                openProximityReadPage();
                break;

            case R.id.aboutUs:
                openAboutUsPage();
                break;
        }
    }


    private void openProximityReadPage() {
        Intent intent = new Intent(this, ProximityReadRangeActivity2.class);

        intent.putExtra(APPConfig.RSSI_LEVEL_Tag,curr_rssi_level);
        intent.putExtra(APPConfig.deviceBddrTag,deviceBDDR);
        Util.debugMessage(TAG,"RSSI="+curr_rssi_level+"deviceBDDR="+deviceBDDR,debugFlag);

        startActivity(intent);
        overridePendingTransitionRightToLeft();

    }


    private void openAboutUsPage() {
        Intent intent = new Intent(this, AboutUsActivity.class);
        intent.putExtra(APPConfig.deviceModelTag,deviceModel);
        startActivity(intent);
        overridePendingTransitionRightToLeft();
    }




    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }



    @Override
    public void update_RSSI(String rssi) {
        Intent intent = new Intent(this, ProximityReadRangeActivity2.class);
        int currLevel = APPConfig.Convert_RSSI_to_LEVEL(Integer.parseInt(rssi));
        intent.putExtra(APPConfig.RSSI_LEVEL_Tag,currLevel);
        intent.putExtra(APPConfig.deviceBddrTag,deviceBDDR);
        Util.debugMessage(TAG,"RSSI="+rssi+"deviceBDDR="+deviceBDDR,debugFlag);

        startActivity(intent);

        overridePendingTransitionRightToLeft();
    }



}

