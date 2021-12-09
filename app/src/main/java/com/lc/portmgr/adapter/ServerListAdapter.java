package com.lc.portmgr.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.lc.portmgr.R;
import com.lc.portmgr.pojo.Server;

import java.util.ArrayList;
import java.util.List;

public class ServerListAdapter extends BaseAdapter {

    private LayoutInflater layoutInflater;
    private List<Server> servers;

    public ServerListAdapter(Context context, List<Server> servers){
        if (servers == null){
            servers = new ArrayList<>();
        }
        this.servers = servers;
        this.layoutInflater= LayoutInflater.from(context);

    }

    public void dataChange(List<Server> servers){
        this.servers = servers;
        this.notifyDataSetChanged();
    }


    @Override
    public int getCount() {
        return servers.size();
    }

    @Override
    public Object getItem(int position) {
        if(position<servers.size())
        return servers.get(position);
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    static class ViewHolder{
        public TextView l_id, l_name, l_ip;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder=null;
        if(convertView==null){
            convertView=layoutInflater.inflate(R.layout.server_list_item,null);
            viewHolder=new ViewHolder();
            viewHolder.l_id=convertView.findViewById(R.id.item_id);
            viewHolder.l_name=convertView.findViewById(R.id.item_content);
            viewHolder.l_ip=convertView.findViewById(R.id.item_send_date);
            convertView.setTag(viewHolder);
        }else{
            viewHolder=(ViewHolder) convertView.getTag();
        }

        //System.out.println(data.size());
        if(servers.size()>0&&servers.size()>position){
            Server server = servers.get(position);
            viewHolder.l_id.setText(position + 1 + "");
            viewHolder.l_name.setText(server.name);
            viewHolder.l_ip.setText(server.ip);
        }

        return convertView;
    }
}
