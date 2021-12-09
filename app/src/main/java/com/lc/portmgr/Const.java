package com.lc.portmgr;

public class Const {
    public static final String PASS_EXTRA_SERVER_NAME = "server";

    public static class SysType{
        public static final String CENTOS = "centos";
        public static final String UBUNTU = "ubuntu";
        public static final String WINDOWS = "windows";
    }
    public static final String [] sysType ={SysType.CENTOS, SysType.UBUNTU, SysType.WINDOWS};
    public static int getSysIndex(String sys){
        for (int i = 0; i < sysType.length; i++)
            if (sysType[i].equals(sys))
                return i;
        return 0;
    }

    public static class Protocol{
        public static final String TCP = "TCP";
        public static final String UDP = "UDP";
    }

    public static final String [] protocolType ={Protocol.TCP, Protocol.UDP};
    public static int getProtocolIndex(String protocol){
        for (int i = 0; i < protocolType.length; i++)
            if (protocolType[i].equals(protocol))
                return i;
        return 0;
    }

}
