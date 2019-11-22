package com.anxell.e5ar;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;

import com.anxell.e5ar.custom.FontEditText;
import com.anxell.e5ar.custom.FontTextView;
import com.anxell.e5ar.custom.My2TextView;
import com.anxell.e5ar.custom.My4TextView;
import com.anxell.e5ar.custom.MySwitch;
import com.anxell.e5ar.transport.APPConfig;
import com.anxell.e5ar.transport.AdminMenu;
import com.anxell.e5ar.transport.BPprotocol;
import com.anxell.e5ar.transport.RBLService;
import com.anxell.e5ar.transport.SettingData;
import com.anxell.e5ar.transport.bpActivity;
import com.anxell.e5ar.util.Util;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import static com.anxell.e5ar.util.Util.showSoftKeyboard;

public class SettingActivity extends bpActivity implements View.OnClickListener {
    private String TAG = SettingActivity.class.getSimpleName().toString();
    private Boolean debugFlag = false;
    private My4TextView mDeviceNameTV;
    private My2TextView mDeviceTimeTV;
    private My4TextView mDoorReLockTimeTV;
    private My2TextView mDoorActionTV;
    private My4TextView mAdminPWDTV;
    private My4TextView mAdminCardTV;
    private My2TextView mExpectLEVELTV;
    private MySwitch mTampSwitch;
    private My2TextView mTampLevelTV;
    private MySwitch mDoorSwitch;
    private ScrollView  settingUI;
    private FontTextView versionTV;
    private RelativeLayout settingMainlayOut;
    private ProgressBar loadDeviceDataBar;
    //private int mProgressStatus = 0;
    //private Handler handler = new Handler();
    private String deviceBDDR = "";
    private String deviceModel = "";
    private int userMax = 0;
    private String mDevice_FW_Version = "";
    private double vr = 0.0;
    private double vrLimit = 1.02;
    private byte currConfig[]=new byte[BPprotocol.len_Device_Config];
    private byte currTime[] = new byte[BPprotocol.len_Device_Time];
    private byte currSensorLevel = BPprotocol.sensor_level1;
    public  static byte tmpConfig[]=new byte[BPprotocol.len_Device_Config];
    public  static byte tmpTime[] = new byte[BPprotocol.len_Device_Time];
    private byte tmpDeviceName[]= new byte[BPprotocol.len_Device_Name];
    private byte tmpPWD[] = new byte[BPprotocol.userPD_maxLen];
    private byte tmpCard[] = new byte[BPprotocol.len_user_card];
    public static byte tmpSensorLevel = BPprotocol.sensor_level1;
    /*backup and restore*/
    private AdminMenu adminMenu;
    private SettingData settingFile = null;

    /*------------------------*/

    public static int updateStatus = 0;
    public static final int up_deviceTime = 1;
    public static final int up_deviceConfig = 2;
    public static final int up_sensor_Level = 4;
    public static final int up_none  = 0;
    public static final int up_rssi_level = 3;
    private Timer ConTimer= null;
    private boolean isReady = false;

    private int setupStatus = 0;
    private final int setupHandle = 0;
    private final int setupBackup = 1;
    private final int setupRestore = 2;
    private final int setupLogin = 3;
    private  int loginProcIndex = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Initial(getLocalClassName());
        setContentView(R.layout.activity_setting);
        findViews();
        Intent intent = getIntent();
        intent.setClass(this,RBLService.class);
        bindService(intent, ServiceConnection, BIND_AUTO_CREATE);
        registerReceiver(mGattUpdateReceiver,  getIntentFilter());
        savedInstanceState = this.getIntent().getExtras();
        String deviceName = savedInstanceState.getString(APPConfig.deviceNameTag);
        deviceBDDR = savedInstanceState.getString(APPConfig.deviceBddrTag);
        deviceModel = savedInstanceState.getString(APPConfig.deviceModelTag);
        mDeviceNameTV.setValue(deviceName);
        int expectLevel = loadDeviceRSSILevel(deviceBDDR);
        mExpectLEVELTV.setValue(""+expectLevel);

        settingUI.setVisibility(View.GONE);

        setListeners();
        // getDeviceTime();
        currentClassName = getLocalClassName();

    }

    @Override
    protected void onRestart() {
        super.onRestart();
        //RegisterReceiver(this);
        currentClassName = getLocalClassName();

           // registerReceiver(mGattUpdateReceiver, getIntentFilter());

            switch(updateStatus){

            case up_deviceConfig:
                Util.debugMessage(TAG,"tmpConfig="+encode.BytetoHexString(tmpConfig)+"len="+tmpConfig.length,true);
                bpProtocol.setDeviceConfig(tmpConfig);
                break;

            case up_deviceTime:
                bpProtocol.setDeviceTime(tmpTime);
                break;

            case up_rssi_level:
                int expectLEVEL = loadDeviceRSSILevel(deviceBDDR);
                Util.debugMessage(TAG,"expectLevel="+ expectLEVEL,debugFlag);
                mExpectLEVELTV.setValue(""+expectLEVEL);
                break;

            case up_sensor_Level:
                bpProtocol.setSensorDegree(tmpSensorLevel);
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
        mAdminPWDTV = (My4TextView) findViewById(R.id.AdminPWD);
        mAdminCardTV = (My4TextView) findViewById(R.id.AdminCard);
        mDeviceTimeTV = (My2TextView) findViewById(R.id.deviceTime);
        mDoorReLockTimeTV = (My4TextView) findViewById(R.id.doorReLockTime);

        mDoorActionTV = (My2TextView) findViewById(R.id.doorLockAction);
        settingUI = (ScrollView) findViewById(R.id.SettingUI);
        mTampSwitch = (MySwitch) findViewById(R.id.tamperSensor);
        mTampLevelTV= (My2TextView)findViewById(R.id.sensor_Level);
        mDoorSwitch = (MySwitch) findViewById(R.id.doorSensor);
        mTampSwitch.setSwitchClickable(false);
        mDoorSwitch.setSwitchClickable(false);
        versionTV = (FontTextView) findViewById(R.id.version);


        mExpectLEVELTV = (My2TextView) findViewById(R.id.proximityReadRange);
        settingMainlayOut = (RelativeLayout) findViewById(R.id.SettingRelativeLayout);
        loadDeviceDataBar = (ProgressBar)findViewById(R.id.setting_loadingBar);
        adminMenu = new AdminMenu(this, settingMainlayOut,bpProtocol);

    }

    @Override
    public void getERROREvent(String bdAddress){
        connect(bdAddress);
        StartConnectTimer();
    }

    @Override
    public void update_service_connect() {
        super.update_service_connect();
        connect(deviceBDDR);
        bpProtocol.queueClear();
        StartConnectTimer();
        isReady = false;
    }

    private void setListeners() {
        findViewById(R.id.user).setOnClickListener(this);
        findViewById(R.id.history).setOnClickListener(this);
        findViewById(R.id.backup).setOnClickListener(this);
        findViewById(R.id.restore).setOnClickListener(this);
        findViewById(R.id.deviceName).setOnClickListener(this);
        findViewById(R.id.doorLockAction).setOnClickListener(this);
        findViewById(R.id.proximityReadRange).setOnClickListener(this);
        mDoorReLockTimeTV.setOnClickListener(this);
//        mDeviceTimeTV.setOnClickListener(this);
        mAdminCardTV.setOnClickListener(this);
        mAdminPWDTV.setOnClickListener(this);
        mTampLevelTV.setOnClickListener(this);
        mTampSwitch.setOnClickListener(this);
        mDoorSwitch.setOnClickListener(this);
        findViewById(R.id.aboutUs).setOnClickListener(this);


        settingUI.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {

                }
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    setcurrentdate();
                }

                return false;
            }
        });


    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        forceDisconnect();
        unbindService(ServiceConnection);
        unregisterReceiver(mGattUpdateReceiver);
        overridePendingTransitionLeftToRight();
        finish();

    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.user:


                openUsersPage();
                break;

            case R.id.history:

                openHistoryPage();
                break;

            case R.id.backup:



                    settingFile = new SettingData(APPConfig.SETTINGS_FILE_NAME+".bp",getString(R.string.app_name));

                settingFile.checkOldBackupFileExist();
                    executeBackup();




                break;

            case R.id.restore:

                 executeRestore();

                break;

            case R.id.deviceName:
                String currentDeviceName = mDeviceNameTV.getValue();
                showDeviceNameDialog(currentDeviceName);
                break;
            case R.id.AdminPWD:
                String currentPWD = mAdminPWDTV.getValue();
                showAdminPWDDialog(currentPWD);
                break;
            case R.id.AdminCard:

                String currentCard = mAdminCardTV.getValue();

                showAdminCardDialog(currentCard);
                break;
            case R.id.doorSensor:
                tmpConfig = Arrays.copyOf(currConfig,currConfig.length);
                if(!mDoorSwitch.isSwitchCheck()){
                    tmpConfig[0] = 0x01;
                    bpProtocol.setDeviceConfig(tmpConfig);
                    Util.debugMessage(TAG,"door on",debugFlag);
                }else{

                    tmpConfig[0] = 0x00;
                    bpProtocol.setDeviceConfig(tmpConfig);
                    Util.debugMessage(TAG,"door off",debugFlag);
                }


                break;
            case R.id.tamperSensor:
                tmpConfig = Arrays.copyOf(currConfig,currConfig.length);
                if(!mTampSwitch.isSwitchCheck()){
                    tmpConfig[4] = 0x01;
                    bpProtocol.setDeviceConfig(tmpConfig);
                    Util.debugMessage(TAG,"tamp on",true);
                }else{

                    tmpConfig[4] = 0x00;
                    bpProtocol.setDeviceConfig(tmpConfig);
                    Util.debugMessage(TAG,"tamp off",false);
                }
                break;

            case R.id.doorLockAction:
                tmpConfig = Arrays.copyOf(currConfig,currConfig.length);
                openDoorLockActionPage();
                break;
            case R.id.sensor_Level:
                tmpSensorLevel = currSensorLevel;
                openSensorLevelPage();
                break;
            case R.id.proximityReadRange:
                openProximityReadPage();
                break;

            case R.id.doorReLockTime:
                tmpConfig = Arrays.copyOf(currConfig,currConfig.length);
                String ReLockTime= mDoorReLockTimeTV.getValue();
                showReLockTimeDialog(ReLockTime);
                //openDoorReLockTimePage();
                break;

            case R.id.deviceTime:
                tmpTime = Arrays.copyOf(currTime,currTime.length);
                openDeviceTimePage();
                break;

            case R.id.aboutUs:
                openAboutUsPage();
                break;
        }
    }

    private void openUsersPage() {
        Intent intent = new Intent(this,UsersListActivity.class);
        intent.putExtra(APPConfig.deviceBddrTag,deviceBDDR);

        startActivity(intent);

        overridePendingTransitionRightToLeft();

       // unregisterReceiver(mGattUpdateReceiver);

    }

    private void openHistoryPage() {
        Intent intent = new Intent(this, HistoryActivity.class);
        intent.putExtra(APPConfig.deviceNameTag,getDeviceName());
        startActivity(intent);

        overridePendingTransitionRightToLeft();

        //unregisterReceiver(mGattUpdateReceiver);

    }

    private void openSensorLevelPage() {
        Intent intent = new Intent(this, SensorLevelActivity.class);
        startActivity(intent);
        overridePendingTransitionRightToLeft();
    }
    private void openDoorLockActionPage() {
        Intent intent = new Intent(this, DoorLockActionActivity.class);
        startActivity(intent);
        overridePendingTransitionRightToLeft();
    }

    private void openProximityReadPage() {
//        readRSSI();
        Intent intent = new Intent(this, ProximityReadRangeActivity1.class);

//        intent.putExtra(APPConfig.RSSI_LEVEL_Tag,curr_rssi_level);
        intent.putExtra(APPConfig.deviceBddrTag,deviceBDDR);
//        Util.debugMessage(TAG,"RSSI="+curr_rssi_level+"deviceBDDR="+deviceBDDR,debugFlag);

        startActivity(intent);
        overridePendingTransitionRightToLeft();
    }

    private void openDoorReLockTimePage() {
        Intent intent = new Intent(this, DoorReLockTimeActivity.class);
        startActivity(intent);
        overridePendingTransitionRightToLeft();
    }

    private void openDeviceTimePage() {
        tmpTime = currTime;
        Intent intent = new Intent(this, DeviceTimeActivity.class);
        startActivity(intent);
        overridePendingTransitionRightToLeft();
    }

    private void openAboutUsPage() {
        Intent intent = new Intent(this, AboutUsActivity.class);

        intent.putExtra(APPConfig.deviceModelTag,deviceModel);
        startActivity(intent);
        overridePendingTransitionRightToLeft();
    }

    private void showDeviceNameDialog(String currentDeviceName) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        dialogBuilder.setTitle(R.string.edit_device_name);
        //dialogBuilder.setMessage(getString(R.string.up_to_16_characters));


        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.my_alert_editor, null);

        dialogBuilder.setView(dialogView);

        final FontEditText editText = (FontEditText) dialogView.findViewById(R.id.editText);
        editText.setText(currentDeviceName);
        editText.setTextColor(Color.BLACK);
        editText.setHint(R.string.up_to_16_characters);
        if (editText.getText().length()>0 ) {
            editText.setSelection(editText.getText().length());
        }
        dialogBuilder.setPositiveButton(R.string.Confirm, new DialogInterface.OnClickListener() {


            @Override
            public void onClick(DialogInterface dialog, int which) {
                String newDeviceName = editText.getText().toString();
                if (newDeviceName.isEmpty()) {
                    SettingActivity.this.showMsg(R.string.msg_please_enter_device_name);
                } else {

                    byte[] device_name_tmp = newDeviceName.getBytes(Charset.forName("UTF-8"));


                    bpProtocol.setDeviceName(device_name_tmp, device_name_tmp.length);
                    dialog.dismiss();
                }


            }
        });
        dialogBuilder.setNeutralButton(R.string.cancel, null);
        final AlertDialog alertDialog = dialogBuilder.create();

        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                int bytes_len = s.toString().getBytes().length;
                int pos = s.length();

                Util.debugMessage(TAG, "String Len= " + s.length(),debugFlag);
                Util.debugMessage(TAG, "Bytes Len= " + bytes_len,debugFlag);

                if (bytes_len > BPprotocol.userID_maxLen) {
                    s.delete(pos - 1, pos);
                }

            }
        });
        dialogBuilder.show();
    }

    private void showAdminPWDDialog(String currentPWD) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        dialogBuilder.setTitle(R.string.settings_Admin_pwd_Edit);
        //dialogBuilder.setMessage(getString(R.string._4_8_digits));

        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.admin_edit_pwd_dialog, null);
        dialogBuilder.setView(dialogView);

        final FontEditText editText = (FontEditText) dialogView.findViewById(R.id.editText);

        editText.setText(currentPWD);
        editText.setTextColor(Color.BLACK);
        editText.setHint(R.string._4_8_digits);
        editText.setInputType(InputType.TYPE_CLASS_NUMBER);
        editText.setRawInputType(this.getResources().getConfiguration().KEYBOARD_12KEY);
        if (editText.getText().length()>0 ) {
            editText.setSelection(editText.getText().length());
        }
        dialogBuilder.setPositiveButton(R.string.Confirm, null);
        dialogBuilder.setNeutralButton(R.string.cancel, null);
        final AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {

            @Override
            public void onShow(final DialogInterface dialog) {

                Button b = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
                b.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {
                        String newPassword = editText.getText().toString();
                        if (TextUtils.isEmpty(newPassword)) {
                            SettingActivity.this.showMsg(R.string.msg_please_enter_device_name);
                        } else {
                            byte[] password_buf = Util.convertStringToByteBuffer(newPassword, BPprotocol.userPD_maxLen);


                            boolean isDuplicated_Password = Util.checkUserDuplicateByPassword(newPassword, mUserDataList);

                            if (isDuplicated_Password) {
                                show_toast_msg(getResources().getString(R.string.users_manage_edit_status_duplication_password));

                            } else {


                                bpProtocol.setAdminPWD(password_buf);

                            }
                            dialog.dismiss();
                        }
                    }
                });
            }
        });
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().length() < 4)
                    alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
                else
                    alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(true);


                try {

                    int isNumeric = Integer.parseInt(editText.getText().toString());

                }catch(NumberFormatException e){
                    alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
                }

                int bytes_len = s.toString().getBytes().length;
                int pos = s.length();


                if (bytes_len > BPprotocol.userPD_maxLen) {
                    s.delete(pos - 1, pos);
                }
            }
        });

        alertDialog.show();
    }

    private void showAdminCardDialog(String currentCard) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        dialogBuilder.setTitle(R.string.settings_Admin_card_Edit);

        LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.admin_edit_card_dialog, null);
        dialogBuilder.setView(dialogView);
        final EditText ArrayCard[] = new EditText[10];
        final int uiCardEditID []={R.id.editText_Admin_Edit_Dialog_Card1,R.id.editText_Admin_Edit_Dialog_Card2,
                R.id.editText_Admin_Edit_Dialog_Card3,R.id.editText_Admin_Edit_Dialog_Card4,
                R.id.editText_Admin_Edit_Dialog_Card5,R.id.editText_Admin_Edit_Dialog_Card6,
                R.id.editText_Admin_Edit_Dialog_Card7,R.id.editText_Admin_Edit_Dialog_Card8,
                R.id.editText_Admin_Edit_Dialog_Card9,R.id.editText_Admin_Edit_Dialog_Card10
        };
        dialogBuilder.setPositiveButton(R.string.Confirm, null);
        dialogBuilder.setNeutralButton(R.string.cancel, null);

        final AlertDialog alertDialog = dialogBuilder.create();

        alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {

            @Override
            public void onShow(final DialogInterface dialog) {

                Button b = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
                b.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {

                        String newCard = "";
                        for(int i=0;i<uiCardEditID.length;i++)
                            newCard += ArrayCard[i].getText().toString();

                        Util.debugMessage(TAG,"newCard ="+newCard ,true);
                        if (TextUtils.isEmpty(newCard)) {

                            ByteBuffer b = ByteBuffer.allocate(BPprotocol.len_Admin_card);

                            b.order(ByteOrder.LITTLE_ENDIAN); // optional, the initial order of a byte buffer is always BIG_ENDIAN.
                            for(int i =0;i<BPprotocol.len_Admin_card;i++)
                                b.put((byte)BPprotocol.nullData);
                            bpProtocol.setAdminCard(b.array());
                            dialog.dismiss();
                        } else {
                            Long data = Long.parseLong(newCard);
                            Util.debugMessage(TAG,"data="+data,true);

                            if( data < Long.parseLong(BPprotocol.INVALID_CARD)){



                                boolean isDuplicated_Card= Util.checkUserDuplicateByCard(newCard, mUserDataList);

                                if (isDuplicated_Card) {
                                    show_toast_msg(getResources().getString(R.string.users_manage_edit_status_duplication_card));

                                } else {
                                    byte[] card = Util.hexStringToByteArray(Util.StringDecToUINT8(data));
                                    bpProtocol.setAdminCard(card);
                                }

                            }else
                                show_toast_msg(getResources().getString(R.string.users_manage_edit_status_Admin_card));

                            dialog.dismiss();
                        }
                    }
                });
            }
        });
        for(int i=0;i<uiCardEditID.length;i++) {
            ArrayCard[i] = (EditText) dialogView.findViewById(uiCardEditID[i]);
            if(currentCard.equals(BPprotocol.spaceCardStr))
                ArrayCard[i].setText("");
            else
                ArrayCard[i].setText(currentCard.substring(i,i+1));
            ArrayCard[i].setTextColor(Color.BLACK);
            ArrayCard[i].setRawInputType(getResources().getConfiguration().KEYBOARD_12KEY);
        }
        for(int i=0;i<uiCardEditID.length;i++) {
            ArrayCard[i].addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    int cardNum = 0;

                    for (int i = 0; i < uiCardEditID.length; i++)
                        cardNum += ArrayCard[i].length();
                    for (int i = 0; i < uiCardEditID.length; i++) {
                        if (ArrayCard[i].length() == 1 && ArrayCard[i].isFocused() && (i < (uiCardEditID.length - 1))) {
                            ArrayCard[i + 1].requestFocus();
                                   i = uiCardEditID.length + 1;
                        }
                    }
                    if (cardNum != 0 && cardNum != 10)
                        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
                    else
                        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(true);

                }


            });
            ArrayCard[i].setOnKeyListener(new View.OnKeyListener() {
                @Override
                public boolean onKey(View v, int keyCode, KeyEvent event) {

                    if (keyCode == KeyEvent.KEYCODE_DEL) {
                        for (int i = 0; i < uiCardEditID.length; i++) {
                            if (ArrayCard[i].isFocused() && i != 0 && ArrayCard[i].length() == 0) {
                                ArrayCard[i - 1].requestFocus();


                                i = uiCardEditID.length + 1;
                            }
                        }
                    }
                    return false;
                }
            });
        }


        new Handler().postDelayed(new Runnable(){
            public void run(){
                //處理少量資訊或UI
//                alertDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
                showSoftKeyboard((EditText)dialogView.findViewById(R.id.editText_Admin_Edit_Dialog_Card1),SettingActivity.this);
            }
        }, 200);

        alertDialog.show();





    }
    private void showReLockTimeDialog(String currentTime) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        dialogBuilder.setTitle(R.string.edit_door_re_lock_time);
        //dialogBuilder.setMessage(getString(R.string._4_8_digits));

        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.my_alert_editor, null);
        dialogBuilder.setView(dialogView);

        final FontEditText editText = (FontEditText) dialogView.findViewById(R.id.editText);

        editText.setText(currentTime);
        editText.setTextColor(Color.BLACK);
        editText.setHint("1~1800");
        editText.setInputType(InputType.TYPE_CLASS_NUMBER);
        editText.setRawInputType(this.getResources().getConfiguration().KEYBOARD_12KEY);
        if (editText.getText().length()>0 ) {
            editText.setSelection(editText.getText().length());
        }
        dialogBuilder.setPositiveButton(R.string.Confirm, null);
        dialogBuilder.setNeutralButton(R.string.cancel, null);
        final AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {

            @Override
            public void onShow(final DialogInterface dialog) {

                Button b = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
                b.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {
                        String time = editText.getText().toString();
                        try{
                        final int delayTime =  Integer.parseInt(time);
                        if (delayTime >0 && delayTime <=1800) {
                            tmpConfig[2] = (byte) (delayTime >> 8);
                            tmpConfig[3] = (byte) (delayTime & 0xFF);
                            bpProtocol.setDeviceConfig(tmpConfig);
                        }
                        }catch(NumberFormatException e){

                        }
                            dialog.dismiss();
                        }
                    }
                );
            }
        });
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().length() < 4 && s.toString().length() > 0)
                    alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
                else
                    alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(true);

                if (!editText.getText().toString().isEmpty())
                {     try {

                    int time = Integer.parseInt(editText.getText().toString());

                    if (time > 0 && time <= 1800)
                        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(true);
                    else
                        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
                    }catch(NumberFormatException e){
                      alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
                    }
                }else
                    alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);

                int bytes_len = s.toString().getBytes().length;
                int pos = s.length();


                if (bytes_len > 4) {
                    s.delete(pos - 1, pos);
                }
            }
        });

        alertDialog.show();
    }
    private void showDoneDialog(final int messageResId) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(SettingActivity.this);
                dialogBuilder.setTitle(messageResId);

                dialogBuilder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                    }
                });

                dialogBuilder.show();
            }
        });
    }



    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void connectUpdate(){

    }

    @Override
    public void BLEReady() {
        super.BLEReady();
        update_system_ble_mac_addrss();
        isReady = true;
        executeLogin();

    }

    @Override
    public void update_RSSI(String rssi) {
        Intent intent = new Intent(this, ProximityReadRangeActivity1.class);
        int currLevel = APPConfig.Convert_RSSI_to_LEVEL(Integer.parseInt(rssi));
        intent.putExtra(APPConfig.RSSI_LEVEL_Tag,currLevel);
        intent.putExtra(APPConfig.deviceBddrTag,deviceBDDR);

        Util.debugMessage(TAG,"RSSI="+rssi+"deviceBDDR="+deviceBDDR,debugFlag);

        startActivity(intent);

        overridePendingTransitionRightToLeft();
    }

    @Override
    public void cmdDataCacheEvent(byte cmd, byte cmdType,byte data[], int datalen){
        Util.debugMessage(TAG,"setting cache",debugFlag);

        switch ((char) cmd) {

          /*  case BPprotocol.cmd_device_config:
                if (cmdType == (byte) BPprotocol.type_write) {
                    tmpConfig = Arrays.copyOf(data,data.length);
                    for(byte tmp:data)
                        Util.debugMessage(TAG,String.format("%02x",tmp),debugFlag);
                    for(byte tmp:tmpConfig)
                        Util.debugMessage(TAG,"data="+String.format("%02x",tmp),debugFlag);
                }

                break;*/
            case BPprotocol.cmd_device_time:
                if (cmdType == (byte) BPprotocol.type_write) {
                    tmpTime = Arrays.copyOf(data,BPprotocol.len_Device_Time);
                    for(byte tmp:tmpTime)
                        Util.debugMessage(TAG,String.format("%02x",tmp),debugFlag);
                }
                break;

            case BPprotocol.cmd_device_name:
                Util.debugMessage(TAG,"data="+encode.BytetoHexString(data),debugFlag);
                if (cmdType == (byte) BPprotocol.type_write)
                    tmpDeviceName =  Arrays.copyOf(data,datalen);

                break;


            case BPprotocol.cmd_set_admin_pwd:
                if (cmdType == (byte) BPprotocol.type_write)
                    tmpPWD = Arrays.copyOf(data,BPprotocol.len_set_Admin_PWD);
                break;

            case BPprotocol.cmd_set_admin_card:
                if (cmdType == (byte) BPprotocol.type_write)
                    tmpCard = Arrays.copyOf(data,BPprotocol.len_user_card);
                break;
            case BPprotocol.cmd_sensor_degree:
                if (cmdType == (byte) BPprotocol.type_write)
                    tmpSensorLevel = data[0];
                break;
            default:


        }

    }


    @Override
    public void cmdAnalysis(byte cmd, byte cmdType, byte data[], int datalen){

            if (setupStatus == setupHandle){

                AdminSettingHandle(cmd,cmdType,data,datalen);

            }else if (setupStatus == setupBackup){
                AdminBackupProc(cmd,cmdType,data,datalen);

            }else if( setupStatus == setupRestore){
                AdminRestoreProc(cmd,cmdType,data,datalen);

            }else if (setupStatus == setupLogin){
                AdminLoginProc(cmd,cmdType,data,datalen);

            }



    }

    private void executeLogin(){

        bpProtocol.adminLogin(mSYS_BLE_MAC_Address_RAW);



        setupStatus = setupLogin;
    }


   private void AdminLoginProc(byte cmd, byte cmdType, byte data[], int datalen){

              if(loginProcIndex == 0)
                bpProtocol.getUsersCount();
              else if(loginProcIndex == 1)
                bpProtocol.getAdminPWD();
              else if(loginProcIndex == 2)
                bpProtocol.getAdminCard();
              else if(loginProcIndex == 3)
                bpProtocol.getFW_version();
              else if(loginProcIndex == 4)
                bpProtocol.getDeviceTime();
              else if(loginProcIndex == 5)
                bpProtocol.readSensorDegree();
              else if(loginProcIndex == 6)
                bpProtocol.getDeviceName();
              else if(loginProcIndex == 7)
                bpProtocol.getDeviceBDAddr();
              else if(loginProcIndex == 8)
                bpProtocol.getDeviceConfig();




        switch(cmd){

            case BPprotocol.cmd_admin_login:

                if (data[0] == BPprotocol.result_success){
                mUserDataList.clear();
                mHistoryDatas.clear();
                HistoryActivity.isHistoryDownloadOK = false;
                    UsersListActivity.isLoadUserListCompleted = false;

            }else{
                onBackPressed();
                sharedPreferences.edit().putBoolean(getBluetoothDeviceAddress(), false).apply();
                Util.debugMessage(TAG,"admin login fail",debugFlag);
            }

            break;
            case BPprotocol.cmd_device_bd_addr:

                //APPConfig.deviceType = APPConfig.deviceType_Card;



                break;
            case BPprotocol.cmd_device_config:

                update_Device_Config(data);


            break;

            case BPprotocol.cmd_device_name:

                if (cmdType == (byte) BPprotocol.type_read){

                    update_DeviceName(data);
                }

                break;
            case BPprotocol.cmd_sensor_degree:

                update_Device_Sensor_Degree(data[0]);

                break;

            case BPprotocol.cmd_user_counter:

                userMax = encode.getUnsignedTwoByte(data);
                Util.debugMessage(TAG,"user Max =%d"+userMax,debugFlag);
                if (userMax == 0)
                    UsersListActivity.isLoadUserListCompleted = true;


            break;

            case BPprotocol.cmd_set_admin_pwd:

                update_Admin_password(data);

            break;

            case BPprotocol.cmd_set_admin_card:
                update_Admin_card(data);

            break;

            case BPprotocol.cmd_fw_version:
                update_FW_Version(data);

                vr = Double.parseDouble(mDevice_FW_Version.substring(1));

                break;

            case BPprotocol.cmd_device_time:
                updata_device_time(data);
                break;

            default:
                Util.debugMessage(TAG,"loginProc ERROR\r\n",debugFlag);

        }

        if (loginProcIndex < APPConfig.loginCMDCntMax)
            loginProcIndex++;

        else
            setupStatus = setupHandle;
    }

    private void executeBackup(){



        bpProtocol.getUsersCount();

        setupStatus = setupBackup;

    }



    /*
     Backup data item
     1.Admin password
     2.Admin card
     3.Device Config
     4.Tamper Sensor Level
     5.Device User Data
     */
    private void AdminBackupProc(byte cmd, byte cmdType, byte data[], int datalen){
        if (cmdType == BPprotocol.type_read) {

            switch (cmd) {

                case BPprotocol.cmd_user_counter:

                    userMax = encode.getUnsignedTwoByte(data);
                    settingFile.writeUserSize(userMax);
                    adminMenu.Setup_Dialog_Backup(userMax);
                    break;

                case BPprotocol.cmd_set_admin_pwd:

                    settingFile.writeData(data);
                    adminMenu.backupRecevice();

                   if(userMax <= 0)
                    setupStatus = setupHandle;

                    break;


                case BPprotocol.cmd_sensor_degree:

                    settingFile.writeData(data);
                    adminMenu.backupRecevice();
                    break;

                case BPprotocol.cmd_set_admin_card:
                    settingFile.writeData(data);
                    adminMenu.backupRecevice();
                    break;

                case BPprotocol.cmd_device_config:

                    settingFile.writeData(data);
                    adminMenu.backupRecevice();

                    break;

                case BPprotocol.cmd_user_data:

                    int getUserCount = adminMenu.getBackupCount() - APPConfig.backupCMDCntMax + 2;
                    Util.debugMessage(TAG, "backup count =" + getUserCount, debugFlag);
                    adminMenu.backupRecevice();
                    settingFile.writeData(data);

                    Util.debugMessage(TAG, "count backupUsers end ", debugFlag);

                    if (getUserCount <= userMax) {
                        bpProtocol.getUserData(getUserCount);
                        Util.debugMessage(TAG, "count send data start ", debugFlag);

                    } else
                        setupStatus = setupHandle;


                    break;


            }
        }



    }

    private void AdminSettingHandle(byte cmd, byte cmdType, byte data[],int datalen){

        if (data[0] == BPprotocol.result_success) {
            switch (cmd) {


                case BPprotocol.cmd_device_config:

                    update_Device_Config(tmpConfig);

                    break;

                case BPprotocol.cmd_device_name:


                    HomeActivity.befSettingBDAddr = deviceBDDR;
                    HomeActivity.isEditDeviceName = true;
                    update_DeviceName(tmpDeviceName);

                break;


                case BPprotocol.cmd_set_admin_pwd:


                    update_Admin_password(tmpPWD);

                    break;

                case BPprotocol.cmd_sensor_degree:


                    update_Device_Sensor_Degree(tmpSensorLevel);

                    break;

                case BPprotocol.cmd_set_admin_card:

                    update_Admin_card(tmpCard);

                break;

                case BPprotocol.cmd_device_time:

                    updata_device_time(tmpTime);

                break;

            }
        }else {
            switch (cmd) {
                case BPprotocol.cmd_set_admin_pwd:


                    show_toast_msg(getResources().getString(R.string.users_manage_edit_status_duplication_password));


                    break;
                case BPprotocol.cmd_set_admin_card:
                    show_toast_msg(getResources().getString(R.string.users_manage_edit_status_duplication_card));


                    break;
            }

        }
    }


    private void executeRestore(){

        bpProtocol.getUsersCount();

        setupStatus = setupRestore;
    }


    /*
     Restore data item
     1.Admin password
     2.Admin card
     3.Device Config
     4.Device name
     5.Tamper Sensor Level
     6.Device User Data
     */
    private void AdminRestoreProc(byte cmd, byte cmdType, byte data[],int datalen){


             switch(cmd) {

                 case BPprotocol.cmd_user_counter:

                     adminMenu.Setup_Dialog_Restore(userMax, getBluetoothDeviceAddress().toString());
                     break;

                 case BPprotocol.cmd_device_name:

                     if(data[0] == BPprotocol.result_success)
                     adminMenu.restoreSend();

                     break;

                 case BPprotocol.cmd_device_config:

                     if(data[0] == BPprotocol.result_success)
                     adminMenu.restoreSend();

                     break;

                 case BPprotocol.cmd_sensor_degree:

                     if(data[0] == BPprotocol.result_success)
                     adminMenu.restoreSend();

                     break;

                 case BPprotocol.cmd_set_admin_card:

                     if(data[0] == BPprotocol.result_success)
                     adminMenu.restoreSend();

                     break;

                 case BPprotocol.cmd_user_data:

                     if(data[0] == BPprotocol.result_success)
                     adminMenu.restoreSend();

                    break;

                 case BPprotocol.cmd_set_admin_pwd:

                     if(data[0] == BPprotocol.result_success)
                     adminMenu.restoreSend();

                    break;


                 case BPprotocol.cmd_erase_users:

                     if(data[0] == BPprotocol.result_success)
                     adminMenu.restoreSend();

                     break;


             }


    }



    private void updata_device_time(byte data[]){

        Calendar deviceDataTime  = Calendar.getInstance();
        Util.debugMessage(TAG,"R time data="+encode.BytetoHexString(data),true);
        Util.debugMessage(TAG,String.format("curr_Y =%04d\r\n, d_Y= %04x", deviceDataTime.get(Calendar.YEAR),(((data[0] << 8) & 0x0000ff00) | (data[1] & 0x000000ff))),true);
        int currYear = deviceDataTime.get(Calendar.YEAR);
        if( currYear <= (((data[0] << 8) & 0x0000ff00) | (data[1] & 0x000000ff)))
        deviceDataTime.set(Calendar.YEAR,((data[0] << 8) & 0x0000ff00) | (data[1] & 0x000000ff));
        else{
            Util.debugMessage(TAG,"C year="+currYear,true);
            data[0] = (byte) ((currYear >> 8 )& 0xFF);
            data[1] = (byte) (currYear & 0xFF );
        }
        deviceDataTime.set(Calendar.MONTH,data[2]-1);
        deviceDataTime.set(Calendar.DAY_OF_MONTH,data[3]);
        deviceDataTime.set(Calendar.HOUR_OF_DAY,data[4]);
        deviceDataTime.set(Calendar.MINUTE,data[5]);
        deviceDataTime.set(Calendar.SECOND,data[6]);
        //deviceTimeStr = Util.Convert_Date_Time_Str(deviceDataTime);
        mDeviceTimeTV.setValue(dateTimeFormat(deviceDataTime.getTime()));
        currTime = data;
        Util.debugMessage(TAG,"c time data="+encode.BytetoHexString(currTime),true);
        Util.debugMessage(TAG,"t time data="+encode.BytetoHexString(currTime),true);
    }
    private void update_DeviceName(byte[] data) {

        String str_DeviceName;



        Util.debugMessage(TAG,"deviceName="+encode.BytetoHexString(data),debugFlag);

        Util.debugMessage(TAG,"deviceName2="+encode.BytetoHexString(data),debugFlag);

        str_DeviceName = new String(data, Charset.forName("UTF-8"));

        mDeviceNameTV.setValue(str_DeviceName);


    }
    private void update_Device_Config(byte[] data) {
        int delay_secs;
        byte lock_type; //0: By Delay, 1: Always Open, 2: Always Closed
        boolean door_sensor_opt;
        boolean tamper_opt;
        //sendProcessMessage(MSG_PROGRESS_BAR_SETUP_INVISIBLE);

        //mLinearLayout_Setup_Items.setVisibility(View.VISIBLE);
        settingUI.setVisibility(View.VISIBLE);
        loadDeviceDataBar.setVisibility(View.GONE);
        //Get Data
        door_sensor_opt = (data[0] != 0);
        lock_type = data[1];
        delay_secs = ((data[2] << 8) & 0x0000ff00) | (data[3] & 0x000000ff);
        tamper_opt = (data[4] != 0);
        String tmp_str = "";
        if (lock_type == BPprotocol.door_statis_delayTime)
            tmp_str = getResources().getString(R.string.use_re_lock_time);
        else if (lock_type == BPprotocol.door_statis_KeepOpen)
            tmp_str = getResources().getString(R.string.always_unlocked);
        else if (lock_type == BPprotocol.door_statis_KeepLock)
            tmp_str = getResources().getString(R.string.always_locked);
        mDoorActionTV.setValue(tmp_str);
        mDoorReLockTimeTV.setValue(String.format(Locale.US, "%d", delay_secs));
        mDoorSwitch.setSwitchCheck(door_sensor_opt);
        mTampSwitch.setSwitchCheck(tamper_opt);
        currConfig = data;

        setcurrentdate();
    }
    private void update_Device_Sensor_Degree(byte sensor_Level){

        switch (sensor_Level){

            case BPprotocol.sensor_level1:
                mTampLevelTV.setValue(getString(R.string.Level_1));
                currSensorLevel = BPprotocol.sensor_level1;
                break;

            case BPprotocol.sensor_level2:
                mTampLevelTV.setValue(getString(R.string.Level_2));
                currSensorLevel = BPprotocol.sensor_level2;
                break;

            case BPprotocol.sensor_level3:
                mTampLevelTV.setValue(getString(R.string.Level_3));
                currSensorLevel = BPprotocol.sensor_level3;
                break;

            default:

                break;
        }
    }
    private void update_FW_Version(byte[] data) {
        int major, minor;

        //Get Data
        major = data[0];
        minor = data[1];

        mDevice_FW_Version = String.format(Locale.US, "V%d.%02d", major, minor);
        versionTV.setText(mDevice_FW_Version);

    }
    private void update_Admin_password(byte[] data) {

        String str_password="";

        //Get Data

//        for(int i=0; i<len; i++)
//            str_DeviceName = String.format("%s%c", str_DeviceName.toString(), data[i+1]);
        //Log.v(TAG, "len = " + len);
        Util.debugMessage(TAG,"password="+encode.BytetoHexString(data),debugFlag);

        for(int i=0; i <data.length;i++) {
            if(data[i]!=(byte)0xFF) {
                Util.debugMessage(TAG,"data["+i+"]="+String.format(Locale.US, "%x", data[i]),debugFlag);
                str_password += encode.convert_ASCII(String.format(Locale.US, "%02x", data[i]));
            }
        }
        mAdminPWDTV.setValue(str_password);

        sharedPreferences.edit().putString(APPConfig.ADMINPWD_Tag+deviceBDDR,str_password).commit();
    }
    private void update_Admin_card(byte[] data) {

        String str_card = BPprotocol.spaceCardStr;
        int checkCnt = 0;
        for(byte raw:data){
             if(raw == 0xff)
                 checkCnt++;
        }

          if(checkCnt < 4)
          {   str_card = Util.UINT8toStringDecForCard(data,BPprotocol.len_Admin_card);
          }
        Util.debugMessage(TAG,"card="+encode.BytetoHexString(data),debugFlag);


        mAdminCardTV.setValue( str_card);
        sharedPreferences.edit().putString(APPConfig.ADMINCARD_Tag+deviceBDDR, str_card).commit();
    }

    public String getDeviceName(){

        return mDeviceNameTV.getValue();
    }

    public void forceDisconnect(){

        bpProtocol.executeForceDisconnect();
    }
    private void StartConnectTimer(){
        if(ConTimer != null){
            ConTimer.cancel();
            ConTimer.purge();
        }
        if(ConTimer  == null){
            ConTimer = new Timer();
            ConTimer.schedule(new TimerTask() {
                @Override
                public void run() {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if(!isReady)
                            onBackPressed();
                            Util.debugMessage(TAG,"Connect Time out",debugFlag);
                            ConTimer = null;
                        }
                    });


                }
            },APPConfig.conTimeOut);

        }
    }



    public static void setcurrentdate(){
        Calendar calendar = Calendar.getInstance();
        SettingActivity.tmpTime[0] = (byte) (calendar.get(Calendar.YEAR) >> 8);
        SettingActivity.tmpTime[1] = (byte) (calendar.get(Calendar.YEAR) & 0xFF);
        SettingActivity.tmpTime[2] = (byte)((calendar.get(Calendar.MONTH) &0xFF)+1);
        SettingActivity.tmpTime[3] = (byte)(calendar.get(Calendar.DAY_OF_MONTH) &0xFF);
        SettingActivity.tmpTime[4] = (byte)(calendar.get(Calendar.HOUR_OF_DAY) &0xFF);
        SettingActivity.tmpTime[5] = (byte)(calendar.get(Calendar.MINUTE) &0xFF);
        SettingActivity.tmpTime[6] = (byte)(calendar.get(Calendar.SECOND) &0xFF);
        bpProtocol.setDeviceTime(tmpTime);

    }








}
