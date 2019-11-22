package com.anxell.e5ar;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;

import com.anxell.e5ar.custom.FontButton;
import com.anxell.e5ar.custom.My3TextView;
import com.anxell.e5ar.transport.APPConfig;
import com.anxell.e5ar.transport.bpActivity;

public class AboutUsActivity extends bpActivity {
    private FontButton appversion;
    private My3TextView mModel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_us);
        appversion = (FontButton)findViewById(R.id.build);
        mModel = (My3TextView) findViewById(R.id.deviceName);
        mModel.setBackground(Color.TRANSPARENT);

//        String deviceModel = getIntent().getStringExtra(APPConfig.deviceModelTag);
//
//        mModel.setValue(deviceModel);
        String version = "";
        try {
            version = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
        }catch (Exception e){

        }
        appversion.setText(getString(R.string.APP_version)+" "+version);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransitionLeftToRight();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
