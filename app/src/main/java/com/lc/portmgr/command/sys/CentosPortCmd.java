package com.lc.portmgr.command.sys;

import android.annotation.SuppressLint;

import com.lc.portmgr.command.PortCmdGenerator;
import com.lc.portmgr.pojo.Port;

import java.util.ArrayList;

@SuppressLint("DefaultLocale")
public class CentosPortCmd implements PortCmdGenerator {

    private static final  String ENABLE_PORT_FORMAT = "firewall-cmd --permanent --zone=public --add-port=%d/%s"; // port/protocol
    private static final  String DISABLE_PORT_FORMAT = "firewall-cmd --permanent --zone=public --remove-port=%d/%s"; // port/protocol
    private static final  String LIST_PORT_COMMAND = "firewall-cmd --list-ports";
    private static final  String CHANGE_PORT_SUFFIX_COMMAND = "firewall-cmd --reload";

    @Override
    public String getEnablePortCmd(Port port) {
        return String.format(ENABLE_PORT_FORMAT, port.port, port.protocol.toLowerCase());
    }

    @Override
    public String getDisablePortCmd(Port port) {
        return String.format(DISABLE_PORT_FORMAT, port.port, port.protocol.toLowerCase());
    }

    @Override
    public String getChangePortSuffixCmd() {
        return CHANGE_PORT_SUFFIX_COMMAND;
    }

    @Override
    public String getListPortCmd() {
        return LIST_PORT_COMMAND;
    }

    @Override
    public ArrayList<Port> parsePortFromServer(String s) {
       ArrayList<Port> ports = new ArrayList<>();
        try {
            String[] ps = s.split(" ");
            for (int i = 0; i < ps.length; i++){
                String[] portAndProtocol = ps[i].split("/");
                if (portAndProtocol.length != 2)
                    continue;
                ports.add(new Port(0, Integer.parseInt(portAndProtocol[0]), "", true, portAndProtocol[1].toUpperCase()));
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return ports;
    }
}
