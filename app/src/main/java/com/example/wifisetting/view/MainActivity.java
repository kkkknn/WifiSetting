package com.example.wifisetting.view;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TableRow;

import com.example.wifisetting.R;

public class MainActivity extends AppCompatActivity {
    private RadioGroup radioGroup_pwd,radioGroup_conn;
    private RadioButton radio_pwdType_0,radio_pwdType_1,radio_pwdType_2;
    private RadioButton wifi_connType_dhcp,wifi_connType_static;
    private LinearLayout staticLayout;
    private TableRow pwdRow;

    private RadioGroup.OnCheckedChangeListener pwdRadioGroupListener=new RadioGroup.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(RadioGroup radioGroup, int i) {
            if(radio_pwdType_0.getId()==i){
                //隐藏密码设置
                pwdRow.setVisibility(View.GONE);
            }else{
                //全部显示
                pwdRow.setVisibility(View.VISIBLE);
            }
        }
    };

    private RadioGroup.OnCheckedChangeListener connRadioGroupListener=new RadioGroup.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(RadioGroup radioGroup, int i) {
            if(wifi_connType_dhcp.getId()==i){
                //隐藏静态IP设置
                staticLayout.setVisibility(View.GONE);
            }else{
                //全部显示
                staticLayout.setVisibility(View.VISIBLE);
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();
    }

    private void initView(){
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
    }

}