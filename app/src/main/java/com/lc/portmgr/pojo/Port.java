package com.lc.portmgr.pojo;

public class Port {
    public int id; //server's ID
    public int port;
    public String desc;
    public boolean enable;
    public String protocol;

    public Port(int id, int port, String desc, boolean enable, String protocol) {
        this.id = id;
        this.port = port;
        this.desc = desc;
        this.enable = enable;
        this.protocol = protocol;
    }


    @Override
    public String toString() {
        return "Port{" +
                "id=" + id +
                ", port=" + port +
                ", desc='" + desc + '\'' +
                ", enable=" + enable +
                ", protocol='" + protocol + '\'' +
                '}';
    }
}
