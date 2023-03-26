package com.lc.portmgr.activities;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.lc.portmgr.Const;
import com.lc.portmgr.R;
import com.lc.portmgr.adapter.PortListAdapter;
import com.lc.portmgr.command.PortCmdGenerator;
import com.lc.portmgr.command.PortCmdGeneratorFactory;
import com.lc.portmgr.dao.DBHelper;
import com.lc.portmgr.dao.port.PortDAO;
import com.lc.portmgr.pojo.Port;
import com.lc.portmgr.pojo.Server;
import com.lc.portmgr.service.Jssh;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import pl.droidsonroids.gif.GifImageView;

public class PortActivity extends AppCompatActivity {

   private PortDAO portDAO;
   private PortListAdapter portListAdapter;

   public ArrayList<Port> ports;
   public Server server;
   
    public ImageButton btnAddPort;
    public ImageButton btnRefreshPort;
    public ImageButton btnSavePort;

    public ListView portList;
    public LinearLayout loadingView;
    public GifImageView loadingGif;
    public TextView loadingText;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_port);


        DBHelper dbHelper = new DBHelper(this);
        portDAO = new PortDAO(dbHelper);

        List<Server> servers = getIntent().getParcelableArrayListExtra(Const.PASS_EXTRA_SERVER_NAME);
        if (servers.size() == 0){
          toast("server不存在");
          this.finish();
          return;
        }
        server = servers.get(0);

        this.setTitle(String.format("%s - %s(%s)", server.sys.toUpperCase(), server.name, server.ip));

        portListAdapter = new PortListAdapter(this, ports);

        portList = findViewById(R.id.port_list);
        portList.setAdapter(portListAdapter);
        portList.setOnItemClickListener(new listViewOnClickListener());

        loadingView = findViewById(R.id.port_list_loading);
        loadingView.setVisibility(View.INVISIBLE);
        loadingGif = findViewById(R.id.port_list_loading_gif);
        loadingText = findViewById(R.id.port_list_loading_text);

        bottomBtnListener listener = new bottomBtnListener();
        btnAddPort = findViewById(R.id.btn_add_port); btnAddPort.setOnTouchListener(listener);
        btnRefreshPort = findViewById(R.id.btn_refresh_port); btnRefreshPort.setOnTouchListener(listener);
        btnSavePort = findViewById(R.id.btn_save_port); btnSavePort.setOnTouchListener(listener);


        refreshPorts();
    }


    //ip列表的item长点击事件监听器
    class listViewOnClickListener implements AdapterView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            AlertDialog.Builder builder = new AlertDialog.Builder(PortActivity.this);
            builder.setTitle("想执行的操作").setIcon(R.drawable.tip);
            final AlertDialog dialog = builder.create();
            View dialogView = View.inflate(PortActivity.this, R.layout.on_click_actions_dialog, null);
            dialog.setView(dialogView);
            ImageButton editBtn =  dialogView.findViewById(R.id.btn_action_edit);
            ImageButton delBtn =  dialogView.findViewById(R.id.btn_action_delete);
            editBtn.setOnClickListener(v -> {
                dialog.dismiss();
                showEditAddPortDialog(ports.get(position));
            });
            delBtn.setOnClickListener(v -> {
                new AlertDialog.Builder(PortActivity.this)
                        .setTitle("删除确认")
                        .setMessage("确定要删除 " + ports.get(position).port + " 吗？")
                        .setIcon(R.drawable.tip)
                        .setPositiveButton("确定", (dialog1, which) -> {
                            portDAO.delete(server.id ,ports.get(position));
                            dialog1.dismiss();
                            dialog.dismiss();
                            refreshPorts();
                        })
                        .setNegativeButton("取消", (dialog1, which) -> {dialog1.dismiss();dialog.dismiss();})
                        .create()
                        .show();
            });
            dialog.show();
        }
    }

    //页面底部的两个按钮图标点击事件监听器
    class bottomBtnListener implements View.OnTouchListener {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    changeViewSize(v, -1);
                    break;
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                    //按钮图标变换,正常图标
                    changeViewSize(v, 1);
                    break;
                default:
                    break;
            }

            if (event.getAction() == MotionEvent.ACTION_UP){
                if (v.getId() == btnAddPort.getId()){
                    showEditAddPortDialog(null);
                }else if (v.getId() == btnRefreshPort.getId()){
                    refreshPorts();toast("刷新成功~");
                }else if (v.getId() == btnSavePort.getId()){
                    syncPortsToRemote();
                }
            }
            return true;
        }

        //flag为1表示增大图标尺寸，-1反之
        private void changeViewSize(View view,int flag) {
            ViewGroup.LayoutParams sizeParamHome = view.getLayoutParams();
            sizeParamHome.height = sizeParamHome.height + 10 * flag;
            sizeParamHome.width = sizeParamHome.width + 10 * flag;
            view.setLayoutParams(sizeParamHome);
        }
    }

    public void loading(int imgId){
        portList.setVisibility(View.INVISIBLE);
        loadingView.setVisibility(View.VISIBLE);
        loadingGif.setImageResource(imgId);
        if (R.drawable.wait == imgId){
            loadingText.setText("加载中。。。");
        }
        if (R.drawable.eat == imgId){
            loadingText.setText("别急！等我吃完！");
        }

    }
    public void cancelLoading(){
        portList.setVisibility(View.VISIBLE);
        loadingView.setVisibility(View.INVISIBLE);
    }

    public void refreshPorts(){
        loading(R.drawable.wait);
        getDBPorts();
        getRemotePorts();
    }

    public void getDBPorts(){
        ports = portDAO.query(server.id);
    }

    public void getRemotePorts(){
        new Thread(() -> {
            Jssh shell = new Jssh(server.ip, server.port, server.user, server.pwd);
            PortCmdGenerator cmdGenerator = PortCmdGeneratorFactory.getPortCmdGenerator(server);
            try {
                shell.execute(cmdGenerator.getListPortCmd());
            } catch (Exception e) {
                e.printStackTrace();
                toastLong("拉取远程端口失败: " + e.getMessage());
                runOnUiThread(() -> {portListAdapter.dataChange(ports); cancelLoading(); });
                return;
            }
            ArrayList<String> stdout = shell.getStandardOutput();
            StringBuilder builder = new StringBuilder();
            for (String str : stdout) {
                builder.append(str);
            }

            ArrayList<Port> portsFromServer = cmdGenerator.parsePortFromServer(builder.toString());

            HashMap<String, Port> serverPortSet = new HashMap<>();
            for (Port p: portsFromServer ) {
                serverPortSet.put(getPortKey(p.port,p.protocol), p);
            }
            HashMap<String, Port> localSet = new HashMap<>();
            for (Port p: ports ) {
                localSet.put(getPortKey(p.port,p.protocol), p);
            }


            ArrayList<Port> toUpdate = new ArrayList<>();
            ArrayList<Port> toInsert = new ArrayList<>();
            for (Port p: portsFromServer ) {
                Port localPort;
                if((localPort=localSet.get(getPortKey(p.port,p.protocol))) != null){ //如果本地有远程有，更新本地的信息为enable=true
                    localPort.enable = true;
                    toUpdate.add(localPort);
                }else{ //如果本地无远程有，添加到本地
                    p.id = server.id;
                    System.out.println("如果本地无远程有，添加到本地:" + p);
                    toInsert.add(p);
                }
            }

            for (Port p : ports) {
                if (serverPortSet.get(getPortKey(p.port,p.protocol)) == null) { // 如果本地有远程无，更新本地的信息为enable=false
                    p.enable = false;
                    toUpdate.add(p);
                }
            }

            for (Port p: toUpdate) {
                portDAO.update(p);
            }
            for (Port p: toInsert) {
                portDAO.insert(p);
            }
            getDBPorts();

            runOnUiThread(() -> {portListAdapter.dataChange(ports); cancelLoading(); });

            toast("拉取远程端口成功");
        }).start();
    }

    public void syncPortsToRemote(){
        loading(R.drawable.eat);
        new Thread(() -> {
            Jssh shell = new Jssh(server.ip, server.port, server.user, server.pwd);
            PortCmdGenerator cmdGenerator = PortCmdGeneratorFactory.getPortCmdGenerator(server);

            StringBuilder cmdBuilder = new StringBuilder();
            for (Port p: ports) {
                if (p.enable)
                    cmdBuilder.append(cmdGenerator.getEnablePortCmd(p)).append(";");
                else
                    cmdBuilder.append(cmdGenerator.getDisablePortCmd(p)).append(";");
            }
            cmdBuilder.append(cmdGenerator.getChangePortSuffixCmd());

            try {
//                System.out.println("==============================要执行的同步命令：");
//                System.out.println(cmdBuilder.toString());
                shell.execute(cmdBuilder.toString());
            } catch (Exception e) {
                e.printStackTrace();
                toastLong("同步命令 [" + cmdBuilder.toString() + "] 执行失败: " + e.getMessage());
                runOnUiThread(this::cancelLoading);
                return;
            }
            toast("同步成功~");
            runOnUiThread(this::cancelLoading);
        }).start();

    }

    public void toast(String text){
        Context context  = this;
        runOnUiThread(() -> Toast.makeText(context,text,Toast.LENGTH_SHORT).show());
    }
    public void toastLong(String text){
        Context context  = this;
        runOnUiThread(() -> Toast.makeText(context,text,Toast.LENGTH_LONG).show());
    }
    private String getPortKey(int port, String protocol){
        return port + protocol;
    }

    public void showEditAddPortDialog(final Port port){
        boolean isAdd = port == null;
        String title = isAdd ? "添加端口": "编辑端口";
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title).setIcon(R.drawable.port);

        final AlertDialog dialog = builder.create();
        View dialogView = View.inflate(this, R.layout.add_port_dialog, null);
        dialog.setView(dialogView);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, Const.protocolType);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        final Spinner protocolSpinner = dialogView.findViewById(R.id.protocolSpinner); protocolSpinner.setSelection(port == null ? 0 : Const.getProtocolIndex(port.protocol)); protocolSpinner.setAdapter(adapter);
        final EditText descEditText = dialogView.findViewById(R.id.edit_add_port_desc); descEditText.setText(port == null ? "" : port.desc);
        final EditText portEditText = dialogView.findViewById(R.id.edit_add_port_port); portEditText.setText(port == null ? "" : (port.port + "")); if (port != null) portEditText.setEnabled(false);
        final Switch portEnableSwitch = dialogView.findViewById(R.id.switch_add_port_enable); portEnableSwitch.setChecked(port != null && port.enable);
        final Button cancelBtn = dialogView.findViewById(R.id.btnCancel);
        final Button confirmBtn = dialogView.findViewById(R.id.btnConfirm);
        cancelBtn.setOnClickListener(v -> { dialog.dismiss(); });
        confirmBtn.setOnClickListener(v -> {
            int port_temp = 0;
            String protocol = "";
            try {
                port_temp = Integer.parseInt(portEditText.getText().toString().trim());
                protocol = Const.protocolType[protocolSpinner.getSelectedItemPosition()];
            }catch (Exception e){ toast("端口号无效"); return; }
            if (isAdd && isPortExist(port_temp, protocol)){
                toast("端口已存在");
                return;
            }

            Port p = new Port(server.id, port_temp, descEditText.getText().toString().trim(), portEnableSwitch.isChecked(), protocol);
            if (isAdd){ // insert
                portDAO.insert(p);
            }else{ // update
                portDAO.update(p);
            }
            dialog.dismiss();
            refreshPorts();
            toast(title + "成功~");
        });
        dialog.show();
    }


    public boolean isPortExist(int port, String protocol){
        for (Port p: ports) {
            if (port == p.port && protocol.equals(p.protocol)) return true;
        }
        return false;
    }

}
