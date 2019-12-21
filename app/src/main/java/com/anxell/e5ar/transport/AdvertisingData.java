package com.anxell.e5ar.transport;

import java.util.HashMap;

/**
 * Created by Sean on 1/19/2018.
 * yusi
 */

public class AdvertisingData {

    public final HashMap CUSTOM_IDs = new HashMap<String, String>();
    public final HashMap dev_Model = new HashMap<String, String>();
    public final HashMap dev_Category = new HashMap<String, String>();
//    public final HashMap dev_Color = new HashMap<String, String>();
    public final HashMap dev_Reserved = new HashMap<String, String>();
    public AdvertisingData(){
        //Custom ID list
        CUSTOM_IDs.put("0000","custom1");
        CUSTOM_IDs.put("0001","custom2");
        CUSTOM_IDs.put("FFFE","GEM");
        CUSTOM_IDs.put("FFFF","ANXELL");

        //Device Model list
        dev_Model.put("0000","E3A2-14");
        dev_Model.put("0001","E3A2-14A");
        dev_Model.put("0002","E3AK1-14");
        dev_Model.put("0003","E3AK1-14A");
        dev_Model.put("0004","E3AK2-14");
        dev_Model.put("0005","E3AK2-14A");
        dev_Model.put("0006","E3AK3-14");
        dev_Model.put("0007","E3AK3-14A");
        dev_Model.put("0008","E3AK4-14");
        dev_Model.put("0009","E3AK4-14A");
        dev_Model.put("000A","E3AK5");
        dev_Model.put("000B","E3AK6");
        dev_Model.put("000C","E3AK6-WI");
        dev_Model.put("000D","Easiprox⁺");
        dev_Model.put("000E","DG-800⁺");
        dev_Model.put("000F","Easiprox⁺ Slim");
        dev_Model.put("0010","DG-160⁺");
        dev_Model.put("0011","E3AK7");
        dev_Model.put("0012","E3AK8");
        dev_Model.put("0013","PBT-1000BT");
        dev_Model.put("0014","BTS-500BT");
        dev_Model.put("0015","BTS-586BT");
        dev_Model.put("0016","DG-700");
        dev_Model.put("0017","DG-750");
        dev_Model.put("0018","DG-760");
        dev_Model.put("0019","DG-360⁺");
        dev_Model.put("001A","DG-365⁺");




        //Device Category list
        dev_Category.put("00","Reader");
        dev_Category.put("01","Keypad");
        dev_Category.put("02","Reader(EM)");
        dev_Category.put("03","Keypad(EM)");
        dev_Category.put("04","Reader(Mifare)");
        dev_Category.put("05","Keypad(Mifare)");
        dev_Category.put("06","TouchPanel");
        dev_Category.put("07","Keypad(EM)+TouchPanel");
        dev_Category.put("08","Reader(Mifare)+TouchPanel");

        //Device Color list
//        dev_Color.put("0000","E3A2-14");
//        dev_Color.put("0001","E3A2-14A");
//        dev_Color.put("0002","E3AK1-14");
//        dev_Color.put("0003","E3AK1-14A");
//        dev_Color.put("0004","E3AK2-14");
//        dev_Color.put("0005","E3AK2-14A");
//        dev_Color.put("0006","E3AK3-14");
//        dev_Color.put("0007","E3AK3-14A");
//        dev_Color.put("0008","E3AK4-14");
//        dev_Color.put("0009","E3AK4-14A");
//        dev_Color.put("000A","E3AK5");
//        dev_Color.put("000B","E3AK6");
//        dev_Color.put("000C","E3AK6-WI");
//        dev_Color.put("000D","E5AR");
//        dev_Color.put("000E","E5AK");

        //device reserved
        dev_Reserved.put("00","XXXX");
        dev_Reserved.put("01","XXXX");
        dev_Reserved.put("FF","XXXX");

    }


}
