package com.anxell.e5ar;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.anxell.e5ar.custom.DebouncedOnClickListener;
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
//        mModel = (My3TextView) findViewById(R.id.deviceName);
//        mModel.setBackground(Color.TRANSPARENT);

//        String deviceModel = getIntent().getStringExtra(APPConfig.deviceModelTag);
//
//        mModel.setValue(deviceModel);
        String version = "";
        try {
            version = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
        }catch (Exception e){

        }
        appversion.setText(getString(R.string.APP_version)+" "+version);

        findViewById(R.id.web).setOnClickListener(buttonLister);
        findViewById(R.id.email).setOnClickListener(buttonLister);
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

    private DebouncedOnClickListener buttonLister = new DebouncedOnClickListener(Config.BUTTON_DEBOUNCE) {
        @Override
        public void onDebouncedClick(View v) {

            switch(v.getId())
            {


                case R.id.web:
                    SettingActivity.openWebLink(AboutUsActivity.this,"http://www.gianni.com.tw/");
                    break;

                case R.id.email:
                    try{
                        isACTIVE_SEND = true;
                        Intent emailIntent=new Intent(Intent.ACTION_SEND);
                        emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{"inquiry1@gianni.com.tw"});
                        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Sent from Easiprox APP");
                        emailIntent.putExtra(Intent.EXTRA_TEXT, "");
                        emailIntent.setType("text/plain");
                        startActivity(emailIntent);
                    }catch(android.content.ActivityNotFoundException ex){
                        //Theres no email client installed on users device.
                    }
                    break;
            }
        }
    };
}
