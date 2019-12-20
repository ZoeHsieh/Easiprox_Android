package com.anxell.e5ar.transport;

import android.os.Process;
import android.support.compat.BuildConfig;

/**
 * Created by Sean on 7/25/2017.
 */

public class APPConfig {
    public static final String SETTINGS_FILE_NAME   = "Easiprox_Setting";
    public static final int	E3K_DEVICES_BLE_RSSI_MIN = (-115);
    public static final int	E3K_DEVICES_BLE_RSSI_MAX = (-35);
    public static final int	E3K_DEVICES_BLE_RSSI_SCALE = E3K_DEVICES_BLE_RSSI_MAX - E3K_DEVICES_BLE_RSSI_MIN;
    public static final int	E3K_DEVICES_BLE_RSSI_LEVEL_MAX = 20;
    public static final int	E3K_DEVICES_BLE_RSSI_LEVEL_DEFAULT = 20;//10;
    public static final int	E3K_DEVICES_BLE_RSSI_LEVEL_MIN = 0;
    public static final int	E3K_DEVICES_BLE_RSSI_LEVEL_SCALE = E3K_DEVICES_BLE_RSSI_LEVEL_MAX - E3K_DEVICES_BLE_RSSI_LEVEL_MIN;
    public static final int	E3K_DEVICES_BLE_RSSI_LEVEL_CONVERT_BASE = E3K_DEVICES_BLE_RSSI_SCALE / E3K_DEVICES_BLE_RSSI_LEVEL_SCALE;
    public static final int	E3K_DEVICES_BLE_RSSI_DETECT_DEFAULT = Convert_LEVEL_to_RSSI(E3K_DEVICES_BLE_RSSI_LEVEL_DEFAULT);
    public static final AdvertisingData advertisingData = new AdvertisingData();
    public static final String RSSI_LEVEL_Tag = "RSSI";
    public static final String RSSI_DB_EXIST = "RSSI_EXIST";
    public static final int PROCESS_THREAD_PRIORITY = Process.THREAD_PRIORITY_DEFAULT;
    public static final int QUEUE_PROCESS_PERIOD = 100;
    public static final String ADMIN_ID = "ADMIN";
    public static final String ADMIN_ENROLL = "ADMIN";
    public static final int  disTimeOut = 2500;
    public static final int  conTimeOut = 15000;
    public static final String deviceNameTag = "deviceNameTag";
    public static final String deviceBddrTag = "bdAddrTag";
    public static final String deviceModelTag = "modelTag";
    public static  String deviceCategory = "";
    public static final String isAutoTag = "isAutoTag";
    public static final String ADMINPWD_Tag = ADMIN_ID+"PWD";
    public static final String ADMINCARD_Tag = ADMIN_ID+"CARD";
    /*E5AK
    * deviceType = APPConfig.deviceType_Keypad_Card
    *
    * E5AR
    * deviceType = APPConfig.ddeviceType_Card
    * */
    public static String deviceType = APPConfig.deviceType_Keypad_Card;
    public static final String deviceType_Keypad = "Keypad";
    public static final String deviceType_Card = "Card";
    public static final String deviceType_Keypad_Card = "Keypad_Card";

    public static final int loginCMDCntMax = 9;
    public static final int backupCMDCntMax = 4; //(not include user data)
    public static final int restoreCMDCntMax = 6;
    public static final String deviceSeries = "Easiprox⁺,Easiprox⁺ Slim,DG-800⁺,DG-160⁺,DG-360⁺,DG-365⁺";  //1122 新增型號365
    public static final String CustomID = (String)APPConfig.advertisingData.CUSTOM_IDs.get("FFFE");
    public static int Convert_RSSI_to_LEVEL(int rssi)
    {
//        return ((rssi - E3K_DEVICES_BLE_RSSI_MIN) / E3K_DEVICES_BLE_RSSI_LEVEL_CONVERT_BASE);
        int value = (E3K_DEVICES_BLE_RSSI_MAX - rssi) / E3K_DEVICES_BLE_RSSI_LEVEL_CONVERT_BASE;

        if (value > E3K_DEVICES_BLE_RSSI_LEVEL_MAX)
        {
            value = E3K_DEVICES_BLE_RSSI_LEVEL_MAX;
        }

        if (value < E3K_DEVICES_BLE_RSSI_LEVEL_MIN)
        {
            value = E3K_DEVICES_BLE_RSSI_LEVEL_MIN;
        }
        return value;
    }

    public static int Convert_LEVEL_to_RSSI(int level)
    {
        return ((level * (-E3K_DEVICES_BLE_RSSI_LEVEL_CONVERT_BASE)) + (E3K_DEVICES_BLE_RSSI_MAX));
    }

}
