package com.lc.portmgr;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.lc.portmgr.activities.PortActivity;
import com.lc.portmgr.adapter.ServerListAdapter;
import com.lc.portmgr.dao.port.PortDAO;
import com.lc.portmgr.dao.server.ServerDAO;
import com.lc.portmgr.dao.DBHelper;
import com.lc.portmgr.pojo.Server;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private ServerListAdapter serverListAdapter;
    public ArrayList<Server> servers = new ArrayList<>();
    public ImageButton btnAddServer;
    public ImageButton btnRefreshServer;

    private ServerDAO serverDAO;
    private PortDAO portDAO;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final ActionBar actionBar = getActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }

        DBHelper dbHelper = new DBHelper(this);

        serverDAO = new ServerDAO(dbHelper);
        portDAO = new PortDAO(dbHelper);

        ListView listView = findViewById(R.id.server_list);
        serverListAdapter = new ServerListAdapter(this, servers);
        listView.setAdapter(serverListAdapter);
        listView.setOnItemClickListener(new listViewOnClickListener());
        listView.setOnItemLongClickListener(new listViewOnLongClickListener());

        btnAddServer = findViewById(R.id.btn_add_server);
        btnRefreshServer = findViewById(R.id.btn_refresh_server);
        bottomBtnListener bottomBtnListener = new bottomBtnListener();
        btnAddServer.setOnTouchListener(bottomBtnListener);
        btnRefreshServer.setOnTouchListener(bottomBtnListener);


        this.refreshServers();
    }


    //ip列表的item点击事件监听器
     class listViewOnClickListener implements AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Intent intent = new Intent();
            ArrayList<Server> list = new ArrayList<>();
            list.add(servers.get(position));
            intent.putParcelableArrayListExtra(Const.PASS_EXTRA_SERVER_NAME, list);
            intent.setClass(MainActivity.this, PortActivity.class);
            startActivity(intent);
        }
    }
    //ip列表的item长点击事件监听器
     class listViewOnLongClickListener implements AdapterView.OnItemLongClickListener {
        @Override
        public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            builder.setTitle("想执行的操作").setIcon(R.drawable.tip);
            final AlertDialog dialog = builder.create();
            View dialogView = View.inflate(MainActivity.this, R.layout.on_click_actions_dialog, null);
            dialog.setView(dialogView);
            ImageButton editBtn =  dialogView.findViewById(R.id.btn_action_edit);
            ImageButton delBtn =  dialogView.findViewById(R.id.btn_action_delete);
            editBtn.setOnClickListener(v -> showEditAddServerDialog(servers.get(position)));
            delBtn.setOnClickListener(v -> {
                new AlertDialog.Builder(MainActivity.this)
                        .setTitle("删除确认")
                        .setMessage("确定要删除 " + servers.get(position).name + " 吗？")
                        .setIcon(R.drawable.tip)
                        .setPositiveButton("确定", (dialog1, which) -> {
                            serverDAO.delete(servers.get(position).id);
                            portDAO.delete(servers.get(position).id);
                            dialog1.dismiss();
                            dialog.dismiss();
                            refreshServers();
                        })
                        .setNegativeButton("取消", (dialog1, which) -> {dialog1.dismiss();dialog.dismiss();})
                        .create()
                        .show();
            });
            dialog.show();
            return true;
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
                    if (v.getId() == btnAddServer.getId()){
                        showEditAddServerDialog(null);
                    }else if (v.getId() == btnRefreshServer.getId()){
                        refreshServers();
                        toast("刷新成功~");
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


    public void refreshServers(){
        this.servers = serverDAO.query();
        serverListAdapter.dataChange(this.servers);
    }



    public void showEditAddServerDialog(final Server server){
        String title = server == null ? "添加Server": "编辑Server";
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title).setIcon(R.drawable.server);

        final AlertDialog dialog = builder.create();
        View dialogView = View.inflate(this, R.layout.add_server_dialog, null);
        dialog.setView(dialogView);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, Const.sysType);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        final Spinner sysSpinner = dialogView.findViewById(R.id.sysSpinner); sysSpinner.setAdapter(adapter);
        sysSpinner.setSelection(server == null ? 0 : Const.getSysIndex(server.sys));

        final EditText ipEditText = dialogView.findViewById(R.id.ipEditText); ipEditText.setText(server == null ? "" : server.ip);
        final EditText portEditText = dialogView.findViewById(R.id.portEditText); portEditText.setText(server == null ? "" : (server.port + ""));
        final EditText nameEditText = dialogView.findViewById(R.id.nameEditText); nameEditText.setText(server == null ? "" : server.name);
        final EditText userNameEditText = dialogView.findViewById(R.id.usernameEditText); userNameEditText.setText(server == null ? "" : server.user);
        final EditText passwordEditText = dialogView.findViewById(R.id.passwordEditText); passwordEditText.setText(server == null ? "" : server.pwd);
        final Button cancelBtn = dialogView.findViewById(R.id.btnCancel);
        final Button confirmBtn = dialogView.findViewById(R.id.btnConfirm);
        cancelBtn.setOnClickListener(v -> { dialog.dismiss(); });
        confirmBtn.setOnClickListener(v -> {
            int port = 0;
            try {
                port = Integer.parseInt(portEditText.getText().toString().trim());
            }catch (Exception e){ toast("端口号无效"); return; }

            Server s = new Server(0, nameEditText.getText().toString().trim(), ipEditText.getText().toString(), port, userNameEditText.getText().toString().trim(), passwordEditText.getText().toString().trim(), Const.sysType[sysSpinner.getSelectedItemPosition()]);
            if (server == null){ // insert
                serverDAO.insert(s);
            }else{ // update
                s.id = server.id;
                serverDAO.update(s);
            }
            dialog.dismiss();
            refreshServers();
            toast(title + "成功~");
        });
        dialog.show();
    }





    public void toast(String text){
        Context context  = this;
        runOnUiThread(() -> Toast.makeText(context,text,Toast.LENGTH_SHORT).show());
    }

}