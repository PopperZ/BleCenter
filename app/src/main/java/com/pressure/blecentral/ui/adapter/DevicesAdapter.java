package com.pressure.blecentral.ui.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.pressure.blecentral.R;
import com.pressure.blecentral.services.entity.Devices;

import java.util.List;

/**
 * Created by zhangfeng on 2017/12/19.
 */

public class DevicesAdapter extends BaseAdapter{
    private LayoutInflater mInflater = null;
    private List<Devices>list;

    public DevicesAdapter(List<Devices> list, Context context) {
        this.list = list;
        this.mInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        DevicesViewHolder holder;
        if(convertView == null){

            holder = new DevicesViewHolder();

            convertView = mInflater.inflate(R.layout.list_item, null);

            holder.name = (TextView)convertView.findViewById(R.id.name);

            holder.address = (TextView)convertView.findViewById(R.id.address);

            holder.UUID = (TextView)convertView.findViewById(R.id.UUID);
            holder.readUUID = (TextView)convertView.findViewById(R.id.readUUID);
            convertView.setTag(holder);//第二次绘制的时候从Tag中取出

        }else{
            holder = (DevicesViewHolder) convertView.getTag();
        }
        holder.name.setText(list.get(position).getName());
        holder.address.setText(list.get(position).getAddress());
        holder.UUID.setText(list.get(position).getUUID());
        holder.readUUID.setText(list.get(position).getReaduuid());
        return convertView;
    }


    class DevicesViewHolder {
        TextView name;
        TextView address;
        TextView UUID;
        TextView readUUID;
    }

}
