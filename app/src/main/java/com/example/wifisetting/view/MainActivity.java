package com.example.wifisetting.view;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.Parcelable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.example.wifisetting.R;
import com.example.wifisetting.bean.StaticConnConfig;
import com.example.wifisetting.bean.WifiConfig;
import com.example.wifisetting.model.SpinnerAdapter;
import com.example.wifisetting.model.WifiControlUtil;
import com.example.wifisetting.util.CheckUtil;


import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private final static String TAG="MainActivity";
    private RadioGroup radioGroup_pwd,radioGroup_conn;
    private RadioButton radio_pwdType_0,radio_pwdType_1,radio_pwdType_2;
    private RadioButton wifi_connType_dhcp,wifi_connType_static;
    private EditText wifiPwd,staticIp,staticDns,staticGateway;
    private TextView wifi_state_name,wifi_state_ip,wifi_state_show;
    private LinearLayout staticLayout;
    private TableRow pwdRow;
    private Button submitBtn;
    private ArrayList<String[]> list=new ArrayList<>();
    private Spinner spinner_wifi;
    private final static int PERMISSON_REQUESTCODE=22;
    private static WifiConfig wifiConfig=new WifiConfig();
    private boolean isDhcp=true;
    private boolean isPermission=false;
    private boolean threadFlag=false;
    private SpinnerAdapter spinnerAdapter;

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (WifiManager.NETWORK_STATE_CHANGED_ACTION.equals(intent.getAction())) {
                Parcelable parcelableExtra = intent
                        .getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
                if (null != parcelableExtra) {
                    NetworkInfo networkInfo = (NetworkInfo) parcelableExtra;
                    NetworkInfo.State state = networkInfo.getState();
                    boolean isConnected = state == NetworkInfo.State.CONNECTED;// 当然，这边可以更精确的确定状态
                    Log.i("H3c", "isConnected" + isConnected);
                    if (isConnected) {
                        WifiManager wifiManager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
                        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
                        if(wifiInfo!=null){
                            wifi_state_ip.setVisibility(View.VISIBLE);
                            wifi_state_name.setVisibility(View.VISIBLE);
                            wifi_state_show.setText("已连接WIFI");
                            wifi_state_ip.setText(ip2str(wifiInfo.getIpAddress()));
                            wifi_state_name.setText("SSID："+wifiInfo.getSSID());
                        }
                        Log.i(TAG, "onReceive: "+wifiInfo.getSSID());
                        Log.i(TAG, "onReceive: "+ip2str(wifiInfo.getIpAddress()));
                    } else {
                        wifi_state_show.setText("未连接WIFI");
                    }
                }
            }
        }
    };

    private Handler handler=new Handler(Looper.getMainLooper(), new Handler.Callback() {
        @Override
        public boolean handleMessage(@NonNull Message message) {
            switch (message.what){
                case 11:
                    spinnerAdapter.notifyDataSetChanged();
                    break;
            }
            return false;
        }
    });

    private RadioGroup.OnCheckedChangeListener pwdRadioGroupListener=new RadioGroup.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(RadioGroup radioGroup, int i) {
            if(radio_pwdType_0.getId()==i){
                //隐藏密码设置
                pwdRow.setVisibility(View.GONE);
                wifiConfig.pwdType=0;
            }else if(radio_pwdType_1.getId()==i){
                //全部显示
                pwdRow.setVisibility(View.VISIBLE);
                wifiConfig.pwdType=1;
            }else if(radio_pwdType_2.getId()==i){
                pwdRow.setVisibility(View.VISIBLE);
                wifiConfig.pwdType=2;
            }
        }
    };

    private RadioGroup.OnCheckedChangeListener connRadioGroupListener=new RadioGroup.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(RadioGroup radioGroup, int i) {
            if(wifi_connType_dhcp.getId()==i){
                //隐藏静态IP设置
                staticLayout.setVisibility(View.GONE);
                isDhcp=true;
            }else if(wifi_connType_static.getId()==i){
                //全部显示
                staticLayout.setVisibility(View.VISIBLE);
                isDhcp=false;
            }
        }
    };
    private View.OnClickListener onClickListener=new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if(view.getId()==submitBtn.getId()){
                Log.i(TAG, "onClick: 获取配置信息并连接WiFi");
                //验证当前所输入的所有信息 是否有误
                if(wifiConfig.pwdType==1||wifiConfig.pwdType==2){
                    wifiConfig.pwd=wifiPwd.getText().toString();
                }
                if(isDhcp){
                    Log.i(TAG, "onClick: 213123123123");
                    wifiConfig.connType=1;
                }else{
                    wifiConfig.connType=0;
                    wifiConfig.staticConnConfig=new StaticConnConfig();
                    wifiConfig.staticConnConfig.this_ip=staticIp.getText().toString();
                    wifiConfig.staticConnConfig.dns_ip=staticDns.getText().toString();
                    wifiConfig.staticConnConfig.gateWay_ip=staticGateway.getText().toString();

                    //写入当前输入缓存
                    SharedPreferences sharedPreferences=  (SharedPreferences)getSharedPreferences("wifi_info", Context.MODE_PRIVATE);
                    if(sharedPreferences!=null){
                        SharedPreferences.Editor editor=sharedPreferences.edit();
                        editor.putString("staticIp",staticIp.getText().toString());
                        editor.putString("staticDns",staticDns.getText().toString());
                        editor.putString("staticGateway",staticGateway.getText().toString());
                        editor.apply();
                        editor.commit();
                    }
                    Log.i(TAG, "onClick: 啦啦啦");
                }
                if(checkInfo(wifiConfig)){
                    WifiControlUtil.getInstance().setContext(getApplicationContext()).connWifi(wifiConfig);
                }
            }

        }
    };

    private AdapterView.OnItemSelectedListener onItemSelectedListener=new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
            wifiConfig.name=list.get(i)[0];
        }

        @Override
        public void onNothingSelected(AdapterView<?> adapterView) {
            wifiConfig.name=null;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();
    }

    private boolean checkInfo(WifiConfig wifiConfig){
        String str=null;
        if(wifiConfig.name==null||wifiConfig.name.isEmpty()){
            str="SSID 错误";
        }
        if(wifiConfig.connType==0){
            if(!CheckUtil.checkIpStr(wifiConfig.staticConnConfig.this_ip)){
                str="静态IP 错误";
            }
            if(!CheckUtil.checkIpStr(wifiConfig.staticConnConfig.gateWay_ip)){
                str="网关 错误";
            }
            if(!CheckUtil.checkIpStr(wifiConfig.staticConnConfig.dns_ip)){
                str="dns 错误";
            }
        }
        if(str!=null){
            Toast.makeText(getApplication(),str,Toast.LENGTH_SHORT).show();
            return false;
        }else {
            return true;
        }
    }

    private void initView(){
        wifi_state_name=findViewById(R.id.wifi_state_connName);
        wifi_state_ip=findViewById(R.id.wifi_state_connIp);
        wifi_state_show=findViewById(R.id.wifi_state_show);

        wifiPwd=findViewById(R.id.wifiPwd);


        staticIp=findViewById(R.id.wifiStaticIp);
        staticDns=findViewById(R.id.wifiStaticDns);
        staticGateway=findViewById(R.id.wifiStaticGateway);
        staticIp.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(CheckUtil.checkStr(charSequence.toString())){
                    staticGateway.setText(charSequence+"1");
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        //取出缓存并填充
        SharedPreferences sharedPreferences=  getSharedPreferences("wifi_info", Context.MODE_PRIVATE);
        if(sharedPreferences!=null){
            String ip=sharedPreferences.getString("staticIp","");
            String dns=sharedPreferences.getString("staticDns","");
            String gateWay=sharedPreferences.getString("staticGateway","");

            if(!ip.equals(""))staticIp.setText(ip);
            if(!dns.equals(""))staticDns.setText(dns);
            if(!gateWay.equals(""))staticGateway.setText(gateWay);
        }

        radio_pwdType_0=findViewById(R.id.radio_pwdType_0);
        radio_pwdType_1=findViewById(R.id.radio_pwdType_1);
        radio_pwdType_2=findViewById(R.id.radio_pwdType_2);
        wifi_connType_dhcp=findViewById(R.id.radio_dhcp);
        wifi_connType_static=findViewById(R.id.radio_static);
        staticLayout=findViewById(R.id.wifi_static_show);
        pwdRow=findViewById(R.id.wifi_pwdType_show);
        radioGroup_pwd=findViewById(R.id.radioGroup_pwdType);
        radioGroup_conn=findViewById(R.id.radioGroup_connType);
        radioGroup_pwd.setOnCheckedChangeListener(pwdRadioGroupListener);
        radioGroup_conn.setOnCheckedChangeListener(connRadioGroupListener);

        submitBtn=findViewById(R.id.btn_connect);
        submitBtn.setOnClickListener(onClickListener);
        spinner_wifi=findViewById(R.id.wifi_spinner);
        spinnerAdapter=new SpinnerAdapter(getApplicationContext(),list);
        spinner_wifi.setAdapter(spinnerAdapter);
        spinner_wifi.setOnItemSelectedListener(onItemSelectedListener);


        //动态权限申请
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            ArrayList<String> permissions = new ArrayList<String>();
            // 定位精确位置
            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                permissions.add(Manifest.permission.ACCESS_FINE_LOCATION);
            }
            if (checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                permissions.add(Manifest.permission.ACCESS_COARSE_LOCATION);
            }
            if (permissions.size() > 0) {
                requestPermissions(permissions.toArray(new String[permissions.size()]), PERMISSON_REQUESTCODE);
            } else {
                isPermission=true;
            }
        }
        //重复任务，每隔10S重新获取wifi列表并显示
        threadFlag=true;
        new Thread(new SyncThread()).start();

        //注册静态广播 监听wifi连接状态
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);//添加监听网络改变的动作

        registerReceiver(broadcastReceiver, intentFilter);//注册广播
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode==PERMISSON_REQUESTCODE){
            int grantResult = grantResults[0];
            boolean granted = grantResult == PackageManager.PERMISSION_GRANTED; // 请求权限的结果  true代表用户同意了
            if(granted){
                isPermission=true;
            }
        }
    }



    class SyncThread implements Runnable{
        @Override
        public void run() {
            WifiControlUtil wifiControlUtil=WifiControlUtil.getInstance().setContext(getApplicationContext());
            wifiControlUtil.openWifi();
            ArrayList<ScanResult> arrayList;
            while(threadFlag){
                if(isPermission){
                    try {
                        arrayList=wifiControlUtil.searchWifi();
                        list.clear();
                        for (ScanResult scanResult:arrayList) {
                            String[] arrs=new String[2];
                            arrs[0]=scanResult.SSID;
                            int nSigLevel = WifiManager.calculateSignalLevel(
                                    scanResult.level, 100);
                            if(nSigLevel>=70){
                                arrs[1]="强";
                            }else if(nSigLevel>=40){
                                arrs[1]="中";
                            }else {
                                arrs[1]="弱";
                            }

                            list.add(arrs);
                        }
                        Log.i(TAG, "搜索到的wifi数量: "+ list.size());
                        //发送刷新命令
                        handler.sendEmptyMessage(11);
                        Thread.sleep(5000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        threadFlag=false;
        unregisterReceiver(broadcastReceiver);//取消注册广播
    }

    //IP地址转换
    private String ip2str(int ip){
        StringBuilder sb = new StringBuilder();
        sb.append(ip & 0xFF).append(".");
        sb.append((ip >> 8) & 0xFF).append(".");
        sb.append((ip >> 16) & 0xFF).append(".");
        sb.append((ip >> 24) & 0xFF);
        return "IP地址："+sb.toString();
    }

}