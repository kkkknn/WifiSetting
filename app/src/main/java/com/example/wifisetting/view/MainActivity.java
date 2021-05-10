package com.example.wifisetting.view;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.net.wifi.ScanResult;
import android.os.Build;
import android.os.Bundle;
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
    private RadioButton radio_pwdType_0,radio_pwdType_1;
    private RadioButton wifi_connType_dhcp,wifi_connType_static;
    private EditText wifiPwd,staticIp,staticDns,staticNetmask,staticGateway;
    private LinearLayout staticLayout;
    private TableRow pwdRow;
    private Button submitBtn;
    private ArrayList<String[]> list=new ArrayList<>();
    private Spinner spinner_wifi;
    private final static int PERMISSON_REQUESTCODE=22;
    private static WifiConfig wifiConfig=new WifiConfig();
    private boolean isDhcp=true;
    private boolean isPwd=true;

    private RadioGroup.OnCheckedChangeListener pwdRadioGroupListener=new RadioGroup.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(RadioGroup radioGroup, int i) {
            if(radio_pwdType_0.getId()==i){
                //隐藏密码设置
                pwdRow.setVisibility(View.GONE);
                isPwd=false;
            }else{
                //全部显示
                pwdRow.setVisibility(View.VISIBLE);
                isPwd=true;
            }
        }
    };

    private RadioGroup.OnCheckedChangeListener connRadioGroupListener=new RadioGroup.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(RadioGroup radioGroup, int i) {
            if(wifi_connType_dhcp.getId()==i){
                //隐藏静态IP设置
                staticLayout.setVisibility(View.GONE);
                isDhcp=false;
            }else{
                //全部显示
                staticLayout.setVisibility(View.VISIBLE);
                isDhcp=true;
            }
        }
    };
    private View.OnClickListener onClickListener=new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if(view.getId()==submitBtn.getId()){
                Log.i(TAG, "onClick: 获取配置信息并连接WiFi");
                //验证当前所输入的所有信息 是否有误
                if(isPwd){
                    wifiConfig.pwdType=1;
                    wifiConfig.pwd=wifiPwd.getText().toString();
                }else {
                    wifiConfig.pwdType=0;
                }
                if(isDhcp){
                    wifiConfig.connType=1;
                }else{
                    wifiConfig.connType=0;
                    wifiConfig.staticConnConfig=new StaticConnConfig();
                    wifiConfig.staticConnConfig.this_ip=staticIp.getText().toString();
                    wifiConfig.staticConnConfig.dns_ip=staticDns.getText().toString();
                    wifiConfig.staticConnConfig.gateWay_ip=staticGateway.getText().toString();
                    wifiConfig.staticConnConfig.net_mask=staticNetmask.getText().toString();
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
            if(!CheckUtil.checkIpStr(wifiConfig.staticConnConfig.net_mask)){
                str="子网掩码 错误";
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
        wifiPwd=findViewById(R.id.wifiPwd);
        staticIp=findViewById(R.id.wifiStaticIp);
        staticDns=findViewById(R.id.wifiStaticDns);
        staticNetmask=findViewById(R.id.wifiStaticNetmask);
        staticGateway=findViewById(R.id.wifiStaticGateway);

        radio_pwdType_0=findViewById(R.id.radio_pwdType_0);
        radio_pwdType_1=findViewById(R.id.radio_pwdType_1);
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
        SpinnerAdapter spinnerAdapter=new SpinnerAdapter(getApplicationContext(),list);
        spinner_wifi.setAdapter(spinnerAdapter);
        spinner_wifi.setOnItemSelectedListener(onItemSelectedListener);

        //todo 设置重复任务，每隔10S重新获取wifi列表并显示
        

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
                syncWifiList();
            }
        }

        //注册静态广播 监听wifi连接状态

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode==PERMISSON_REQUESTCODE){
            int grantResult = grantResults[0];
            boolean granted = grantResult == PackageManager.PERMISSION_GRANTED; // 请求权限的结果  true代表用户同意了
            if(granted){
                syncWifiList();
            }
        }
    }

    //获取并同步显示 搜索到的wifi列表
    private void syncWifiList(){
        WifiControlUtil wifiControlUtil=WifiControlUtil.getInstance().setContext(getApplicationContext());
        wifiControlUtil.openWifi();
        ArrayList<ScanResult> arrayList=wifiControlUtil.searchWifi();
        list.clear();
        for (ScanResult scanResult:arrayList) {
            String[] arrs=new String[2];
            arrs[0]=scanResult.SSID;
            arrs[1]=Integer.toString(scanResult.level);
            list.add(arrs);
            Log.i(TAG, "搜索到的wifi名字: "+ scanResult.SSID);
        }
        ((BaseAdapter)spinner_wifi.getAdapter()).notifyDataSetChanged();
    }


}