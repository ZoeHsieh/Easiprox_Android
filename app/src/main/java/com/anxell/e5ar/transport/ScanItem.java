package com.anxell.e5ar.transport;

/**
 * Created by Sean on 7/25/2017.
 */

public class ScanItem {
    public String dev_name;
    public String dev_addr;
    public String Custom;
    public String Model;
    public String Category;
//    public String Color;
    public String reserved;
    public int rssi;
    public int alive_cnt;


    public ScanItem() {
        clear();
    }

    public ScanItem(String dev_name, String dev_addr,String custom,String model,String category,String reserved,int rssi, int alive_cnt) {
        this.dev_name = dev_name;
        this.dev_addr = dev_addr;
        this.Custom = custom;
        this.Model = model;
        this.Category = category;
//        this.Color = color;
        this.reserved = reserved;
        this.rssi = rssi;
        this.alive_cnt = alive_cnt;

    }

    /*public ScanItem(String dev_name, String dev_addr) {
        this.dev_name = dev_name;
        this.dev_addr = dev_addr;
        this.rssi = -255;
        this.alive_cnt = 0;
    }

    public ScanItem(String dev_name, String dev_addr, int alive_cnt) {
        this.dev_name = dev_name;
        this.dev_addr = dev_addr;
        this.alive_cnt = alive_cnt;
    }

    public ScanItem(String dev_addr) {
        this.dev_name = "";
        this.dev_addr = dev_addr;
        this.rssi = -255;
        this.alive_cnt = 0;
    }*/

    public void setAlive_cnt(int alive_cnt) {
        this.alive_cnt = alive_cnt;
    }

    public void clear() {
        this.dev_name = "";
        this.dev_addr = "";
        this.rssi = -255;
        this.alive_cnt = 0;
    }
}
