package com.anxell.e5ar;

import android.os.Bundle;
import android.support.annotation.IdRes;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.anxell.e5ar.transport.BPprotocol;
import com.anxell.e5ar.transport.bpActivity;

public class SensorLevelActivity extends bpActivity {
    private RadioGroup sensorLevelGroup;
    private RadioButton btnLevel1;
    private RadioButton btnLevel2;
    private RadioButton btnLevel3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sensor_level);
        sensorLevelGroup = (RadioGroup) findViewById(R.id.rgroup);
        btnLevel1 = (RadioButton) findViewById(R.id.btn_level1);
        btnLevel2 = (RadioButton) findViewById(R.id.btn_level2);
        btnLevel3 = (RadioButton) findViewById(R.id.btn_level3);
        sensorLevelGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                switch (checkedId) {

                    case R.id.btn_level1:
                        btnLevel1.setChecked(true);
                        btnLevel2.setChecked(false);
                        btnLevel3.setChecked(false);
                        SettingActivity.tmpSensorLevel = BPprotocol.sensor_level1;
                        break;

                    case R.id.btn_level2:
                        btnLevel1.setChecked(false);
                        btnLevel2.setChecked(true);
                        btnLevel3.setChecked(false);
                        SettingActivity.tmpSensorLevel =BPprotocol.sensor_level2;
                        break;
                    case R.id.btn_level3:
                        btnLevel1.setChecked(false);
                        btnLevel2.setChecked(false);
                        btnLevel3.setChecked(true);
                        SettingActivity.tmpSensorLevel = BPprotocol.sensor_level3;
                        break;
                }
            }
        });
        if (SettingActivity.tmpSensorLevel == BPprotocol.sensor_level1) {
            btnLevel1.setChecked(true);
            btnLevel2.setChecked(false);
            btnLevel3.setChecked(false);
        } else if (SettingActivity.tmpSensorLevel == BPprotocol.sensor_level2) {
            btnLevel1.setChecked(false);
            btnLevel2.setChecked(true);
            btnLevel3.setChecked(false);
        } else if (SettingActivity.tmpSensorLevel == BPprotocol.sensor_level3) {
            btnLevel1.setChecked(false);
            btnLevel2.setChecked(false);
            btnLevel3.setChecked(true);
    }

}
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        SettingActivity.updateStatus = SettingActivity.up_sensor_Level;
        overridePendingTransitionLeftToRight();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
