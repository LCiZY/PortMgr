package com.lc.portmgr.command;

import com.lc.portmgr.Const;
import com.lc.portmgr.command.sys.CentosPortCmd;
import com.lc.portmgr.command.sys.UbuntuPortCmd;
import com.lc.portmgr.pojo.Server;

public class PortCmdGeneratorFactory {

    public static PortCmdGenerator getPortCmdGenerator(Server server){
        switch (server.sys){
            case Const.SysType.CENTOS:
                return new CentosPortCmd();
            case Const.SysType.UBUNTU:
                return new UbuntuPortCmd();
            default:
                break;
        }
        return new CentosPortCmd();
    }


}
