package com.example.wifisetting.model;

import com.example.wifisetting.bean.WifiConfig;

//单例模式
public class WifiControlUtil {
    private volatile static WifiControlUtil wifiControlUtil;
    private WifiControlUtilCallback wifiControlUtilCallback;
    private WifiControlUtil(){}

    public static WifiControlUtil getInstance(){
        if(wifiControlUtil==null){
            synchronized (WifiControlUtil.class){
                if(wifiControlUtil==null){
                    wifiControlUtil=new WifiControlUtil();
                }
            }
        }
        return wifiControlUtil;
    }

    //开启WiFi
    public void openWifi(){

    }
    //关闭WiFi
    public void closeWifi(){

    }
    //获取WiFi列表
    public void searchWifi(WifiConfig  wifiConfig){

    }
    //连接WiFi
    public void connWifi(){

    }
    //断开当前连接WiFi
    public void disConnWifi(){

    }
    //设置监听
    public void setCallBack(WifiControlUtilCallback callBack){
        wifiControlUtilCallback=callBack;
    }

    interface WifiControlUtilCallback{
        void findWifi();
        void connect();
        void disConnect();
        Exception error();
        void opened();
        void closed();
        void searchStart();
        void searchEnd();
    }


}
