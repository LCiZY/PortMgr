package com.lc.portmgr.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {
    private static final int DB_VERSION = 1;
    private static final String DB_NAME = "portMgr.db";
    public static final String SERVER_TABLE_NAME = "Servers";
    public static final String PORT_TABLE_NAME = "Ports";


    public static class ServerTableColumns{
        public static String COLUMN_ID = "id";
        public static String COLUMN_NAME = "name";
        public static String COLUMN_IP = "ip";
        public static String COLUMN_PORT = "port";
        public static String COLUMN_USER = "user";
        public static String COLUMN_PWD = "pwd";
        public static String COLUMN_SYS = "sys";
    }

    public static class PortTableColumns{
        public static String COLUMN_ID = "id";
        public static String COLUMN_PORT = "port";
        public static String COLUMN_DESC = "description";
        public static String COLUMN_ENABLE = "enable";
        public static String COLUMN_PROTOCOL = "protocol";
    }

    public DBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        System.out.println("创建表============");
        // create table Orders(Id integer primary key, CustomName text, OrderPrice integer, Country text);
        String sql = "create table if not exists " + SERVER_TABLE_NAME +  String.format(" (%s integer primary key AUTOINCREMENT, %s text, %s text, %s integer, %s text, %s text, %s text)", ServerTableColumns.COLUMN_ID, ServerTableColumns.COLUMN_NAME, ServerTableColumns.COLUMN_IP, ServerTableColumns.COLUMN_PORT,ServerTableColumns.COLUMN_USER ,ServerTableColumns.COLUMN_PWD,ServerTableColumns.COLUMN_SYS);
        sqLiteDatabase.execSQL(sql);

        String sql2 = "create table if not exists " + PORT_TABLE_NAME + String.format(" (%s integer, %s integer, `%s` text, %s integer, %s text)", DBHelper.PortTableColumns.COLUMN_ID, DBHelper.PortTableColumns.COLUMN_PORT, DBHelper.PortTableColumns.COLUMN_DESC, DBHelper.PortTableColumns.COLUMN_ENABLE, PortTableColumns.COLUMN_PROTOCOL);
        sqLiteDatabase.execSQL(sql2);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        String sql = "DROP TABLE IF EXISTS " + SERVER_TABLE_NAME;
        sqLiteDatabase.execSQL(sql);
        String sql2 = "DROP TABLE IF EXISTS " + PORT_TABLE_NAME;
        sqLiteDatabase.execSQL(sql2);
        onCreate(sqLiteDatabase);
    }
}