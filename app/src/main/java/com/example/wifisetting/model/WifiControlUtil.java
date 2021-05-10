package com.example.wifisetting.model;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;

import androidx.core.app.ActivityCompat;

import com.example.wifisetting.bean.WifiConfig;

import java.util.ArrayList;
import java.util.List;

//单例模式
public class WifiControlUtil {
    private volatile static WifiControlUtil wifiControlUtil;
    private WifiControlUtilCallback wifiControlUtilCallback;
    private Context mContext;
    private WifiManager mWifiManager;

    private WifiControlUtil() {
    }

    public static WifiControlUtil getInstance() {
        if (wifiControlUtil == null) {
            synchronized (WifiControlUtil.class) {
                if (wifiControlUtil == null) {
                    wifiControlUtil = new WifiControlUtil();
                }
            }
        }
        return wifiControlUtil;
    }

    public WifiControlUtil setContext(Context context) {
        this.mContext = context;
        mWifiManager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        return wifiControlUtil;
    }

    //开启WiFi
    public void openWifi() {
        if (mWifiManager != null && !isWifiOpen()) {
            mWifiManager.setWifiEnabled(true);
        }
    }

    //是否已开启wifi
    public boolean isWifiOpen() {
        if (mWifiManager != null) {
            return mWifiManager.isWifiEnabled();
        }
        return false;
    }

    //关闭WiFi
    public void closeWifi() {
        if (mWifiManager != null && isWifiOpen()) {
            mWifiManager.setWifiEnabled(false);
        }
    }

    //获取WiFi列表
    public ArrayList<ScanResult> searchWifi() {
        ArrayList<ScanResult> resultList = new ArrayList<>();
        if (mWifiManager != null && isWifiOpen()) {
            resultList.addAll(mWifiManager.getScanResults());
        }
        return resultList;
    }

    //连接WiFi
    public void connWifi(WifiConfig wifiConfig) {
        if(wifiConfig==null){
            return;
        }
        if(wifiConfig.pwdType==0){
            mWifiManager.disableNetwork(mWifiManager.getConnectionInfo().getNetworkId());
            int netId = mWifiManager.addNetwork(getWifiConfig(wifiConfig.name, "", false));
            mWifiManager.enableNetwork(netId, true);
        }else{
            mWifiManager.disableNetwork(mWifiManager.getConnectionInfo().getNetworkId());
            int netId = mWifiManager.addNetwork(getWifiConfig(wifiConfig.name, wifiConfig.pwd, true));
            mWifiManager.enableNetwork(netId, true);
        }
    }

    //断开当前连接WiFi
    public void disConnWifi() {

    }


    private WifiConfiguration getWifiConfig(String ssid, String pws, boolean isHasPws){
        WifiConfiguration config = new WifiConfiguration();
        config.allowedAuthAlgorithms.clear();
        config.allowedGroupCiphers.clear();
        config.allowedKeyManagement.clear();
        config.allowedPairwiseCiphers.clear();
        config.allowedProtocols.clear();
        config.SSID = "\"" + ssid + "\"";

        WifiConfiguration tempConfig = isExist(ssid);
        if(tempConfig != null) {
            mWifiManager.removeNetwork(tempConfig.networkId);
        }
        if (isHasPws){
            config.preSharedKey = "\""+pws+"\"";
            config.hiddenSSID = true;
            config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
            config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
            config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
            config.status = WifiConfiguration.Status.ENABLED;
        }else {
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
        }
        return config;
    }


    private WifiConfiguration isExist(String ssid) {
        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return null;
        }
        List<WifiConfiguration> configs = mWifiManager.getConfiguredNetworks();
        for (WifiConfiguration config : configs) {
            if (config.SSID.equals("\""+ssid+"\"")) {
                return config;
            }
        }
        return null;
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
