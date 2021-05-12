package com.example.wifisetting.model;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.wifisetting.R;
import com.example.wifisetting.view.SpinnerViewHolder;

import java.util.ArrayList;

public class SpinnerAdapter extends BaseAdapter {
    private Context mContext;
    private ArrayList<String[]> mObjects;

    public SpinnerAdapter(Context mContext, ArrayList<String[]> mObjects) {
        this.mContext = mContext;
        this.mObjects = mObjects;
    }

    @Override
    public int getCount() {
        return mObjects.size();
    }

    @Override
    public Object getItem(int i) {
        return mObjects.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        SpinnerViewHolder viewHolder;

        if (view == null) {
            view = LayoutInflater.from(mContext).inflate(R.layout.wifi_item, null);
            viewHolder = new SpinnerViewHolder();
            viewHolder.wifiName = (TextView) view.findViewById(R.id.item_wifi_name);
            viewHolder.wifiPower = (TextView) view.findViewById(R.id.item_wifi_power);
            view.setTag(viewHolder);
        } else {
            viewHolder = (SpinnerViewHolder) view.getTag();
        }

        //Object item =  getItem(pos);
        viewHolder.wifiName.setText(mObjects.get(i)[0]);
        viewHolder.wifiPower.setText(mObjects.get(i)[1]);
        switch (mObjects.get(i)[1]){
            case "强":
                viewHolder.wifiPower.setTextColor(Color.GREEN);
                break;
            case "中":
                viewHolder.wifiPower.setTextColor(Color.GRAY);
                break;
            case "弱":
                viewHolder.wifiPower.setTextColor(Color.RED);
                break;
            default:
                viewHolder.wifiPower.setTextColor(Color.WHITE);
                break;
        }
        return view;
    }

}
