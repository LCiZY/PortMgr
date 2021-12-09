package com.lc.portmgr.command.sys;

import com.lc.portmgr.command.PortCmdGenerator;
import com.lc.portmgr.pojo.Port;

import java.util.ArrayList;

public class UbuntuPortCmd implements PortCmdGenerator {

    private static final  String ENABLE_PORT_FORMAT = "ufw allow %d/%s"; // port/protocol
    private static final  String DISABLE_PORT_FORMAT = "ufw deny %d/%s"; // port/protocol
    private static final  String LIST_PORT_COMMAND = "";
    private static final  String CHANGE_PORT_SUFFIX_COMMAND = "";

    @Override
    public String getEnablePortCmd(Port port) {
        return null;
    }

    @Override
    public String getDisablePortCmd(Port port) {
        return null;
    }

    @Override
    public String getChangePortSuffixCmd() {
        return "";
    }

    @Override
    public String getListPortCmd( ) {
        return null;
    }

    @Override
    public ArrayList<Port> parsePortFromServer(String s) {
        return new ArrayList<>();
    }
}
