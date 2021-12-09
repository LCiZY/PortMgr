package com.lc.portmgr.dao.server;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.lc.portmgr.dao.DBHelper;
import com.lc.portmgr.pojo.Server;

import java.util.ArrayList;

public class ServerDAO {
    private final DBHelper helper;
    public ServerDAO(DBHelper helper){
        this.helper = helper;
    }

    public long insert(Server server){
        // Gets the data repository in write mode
        SQLiteDatabase db = helper.getWritableDatabase();
        // Create a new map of values, where column names are the keys
        ContentValues values = new ContentValues();
        values.put(DBHelper.ServerTableColumns.COLUMN_NAME, server.name);
        values.put(DBHelper.ServerTableColumns.COLUMN_IP, server.ip);
        values.put(DBHelper.ServerTableColumns.COLUMN_PORT, server.port);
        values.put(DBHelper.ServerTableColumns.COLUMN_USER, server.user);
        values.put(DBHelper.ServerTableColumns.COLUMN_PWD, server.pwd);
        values.put(DBHelper.ServerTableColumns.COLUMN_SYS, server.sys);


        // Insert the new row, returning the primary key value of the new row
        return db.insert(DBHelper.SERVER_TABLE_NAME, null, values);
    }

    public void delete(Integer serverId){
        // Gets the data repository in write mode
        SQLiteDatabase db = helper.getWritableDatabase();
        // Define 'where' part of query.
        String selection = String.format("%s = ?", DBHelper.ServerTableColumns.COLUMN_ID);
        String[] selectionArgs = { serverId + ""};
        db.delete(DBHelper.SERVER_TABLE_NAME, selection, selectionArgs);
    }

    public void update(Server server){
        SQLiteDatabase db = helper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(DBHelper.ServerTableColumns.COLUMN_NAME, server.name);
        values.put(DBHelper.ServerTableColumns.COLUMN_IP, server.ip);
        values.put(DBHelper.ServerTableColumns.COLUMN_PORT, server.port);
        values.put(DBHelper.ServerTableColumns.COLUMN_USER, server.user);
        values.put(DBHelper.ServerTableColumns.COLUMN_PWD, server.pwd);
        values.put(DBHelper.ServerTableColumns.COLUMN_SYS, server.sys);

        String selection = String.format("%s = ?", DBHelper.ServerTableColumns.COLUMN_ID);
        String[] selectionArgs = { server.id + ""};

        db.update(DBHelper.SERVER_TABLE_NAME, values, selection, selectionArgs);
    }

    public ArrayList<Server> query(){
        SQLiteDatabase db = helper.getWritableDatabase();
        String[] projection = {
                DBHelper.ServerTableColumns.COLUMN_ID,
                DBHelper.ServerTableColumns.COLUMN_NAME,
                DBHelper.ServerTableColumns.COLUMN_IP,
                DBHelper.ServerTableColumns.COLUMN_PORT,
                DBHelper.ServerTableColumns.COLUMN_USER,
                DBHelper.ServerTableColumns.COLUMN_PWD,
                DBHelper.ServerTableColumns.COLUMN_SYS,
        };

        String sortOrder = DBHelper.ServerTableColumns.COLUMN_ID + " ASC";

        Cursor cursor = db.query(
                DBHelper.SERVER_TABLE_NAME,   // The table to query
                projection,             // The array of columns to return (pass null to get all)
                null,              // The columns for the WHERE clause
                null,          // The values for the WHERE clause
                null,                   // don't group the rows
                null,                   // don't filter by row groups
                sortOrder               // The sort order
        );

        ArrayList<Server> servers = new ArrayList<>();
        while(cursor.moveToNext()) {
            int id = cursor.getInt(cursor.getColumnIndexOrThrow(DBHelper.ServerTableColumns.COLUMN_ID));
            String name = cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.ServerTableColumns.COLUMN_NAME));
            String ip = cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.ServerTableColumns.COLUMN_IP));
            int port = cursor.getInt(cursor.getColumnIndexOrThrow(DBHelper.ServerTableColumns.COLUMN_PORT));
            String user = cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.ServerTableColumns.COLUMN_USER));
            String pwd = cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.ServerTableColumns.COLUMN_PWD));
            String sys = cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.ServerTableColumns.COLUMN_SYS));
            servers.add(new Server(id,name, ip, port, user, pwd, sys));
        }
        cursor.close();
        return servers;
    }
    
}
