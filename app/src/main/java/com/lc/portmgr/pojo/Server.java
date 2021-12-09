package com.lc.portmgr.pojo;

import android.os.Parcel;
import android.os.Parcelable;

public class Server implements Parcelable {
    public int id;
    public String name;
    public String ip;
    public int port;
    public String user;
    public String pwd;
    public String sys;

    public Server(int id, String name, String ip, int port, String user, String pwd, String sys) {
        this.id = id;
        this.name = name;
        this.ip = ip;
        this.port = port;
        this.user = user;
        this.pwd = pwd;
        this.sys = sys;
    }

    protected Server(Parcel in) {
        id = in.readInt();
        name = in.readString();
        ip = in.readString();
        port = in.readInt();
        user = in.readString();
        pwd = in.readString();
        sys = in.readString();
    }

    public static final Creator<Server> CREATOR = new Creator<Server>() {
        @Override
        public Server createFromParcel(Parcel in) {
            return new Server(in);
        }

        @Override
        public Server[] newArray(int size) {
            return new Server[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(name);
        dest.writeString(ip);
        dest.writeInt(port);
        dest.writeString(user);
        dest.writeString(pwd);
        dest.writeString(sys);
    }

    @Override
    public String toString() {
        return "Server{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", ip='" + ip + '\'' +
                ", port=" + port +
                ", user='" + user + '\'' +
                ", pwd='" + pwd + '\'' +
                ", sys='" + sys + '\'' +
                '}';
    }
}
