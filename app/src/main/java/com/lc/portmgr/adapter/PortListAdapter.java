package com.lc.portmgr.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import com.lc.portmgr.R;
import com.lc.portmgr.pojo.Port;

import java.util.ArrayList;
import java.util.List;

public class PortListAdapter extends BaseAdapter {

    private LayoutInflater layoutInflater;
    private List<Port> ports;

    public PortListAdapter(Context context, List<Port> ports){
        if (ports == null){
            ports = new ArrayList<>();
        }
        this.ports = ports;
        this.layoutInflater= LayoutInflater.from(context);

    }

    public void dataChange(List<Port> ports){
        this.ports = ports;
        this.notifyDataSetChanged();
    }


    @Override
    public int getCount() {
        return ports.size();
    }

    @Override
    public Object getItem(int position) {
        if(position<ports.size())
        return ports.get(position);
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    static class ViewHolder{
        public TextView l_port, l_desc, l_protocol;
        public Switch  l_switch;
    }

    @SuppressLint({"SetTextI18n", "ClickableViewAccessibility"})
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder=null;
        if(convertView==null){
            convertView=layoutInflater.inflate(R.layout.port_list_item,null);
            viewHolder=new ViewHolder();
            viewHolder.l_port=convertView.findViewById(R.id.item_port);
            viewHolder.l_desc=convertView.findViewById(R.id.item_desc);
            viewHolder.l_protocol=convertView.findViewById(R.id.item_protocol);
            viewHolder.l_switch=convertView.findViewById(R.id.port_list_item_switch);
            convertView.setTag(viewHolder);
        }else{
            viewHolder=(ViewHolder) convertView.getTag();
        }

        //System.out.println(data.size());
        if(ports.size()>0&&ports.size()>position){
            Port port = ports.get(position);
            viewHolder.l_port.setText(port.port + "");
            viewHolder.l_desc.setText(port.desc);
            viewHolder.l_protocol.setText(port.protocol);
            viewHolder.l_switch.setChecked(port.enable);
        }
        if (viewHolder != null && viewHolder.l_switch != null){
            viewHolder.l_switch.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if(ports.size()>0&&ports.size()>position) {
                    ports.get(position).enable = isChecked;
                }
            });
        }

        return convertView;
    }
}
