package com.lc.portmgr.command;

import com.lc.portmgr.pojo.Port;

import java.util.ArrayList;

public interface PortCmdGenerator {
    String getEnablePortCmd(Port port);
    String getDisablePortCmd(Port port);
    String getChangePortSuffixCmd();
    String getListPortCmd();

    ArrayList<Port> parsePortFromServer(String s);
}
