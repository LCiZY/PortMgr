package com.lc.portmgr.dao.port;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.lc.portmgr.dao.DBHelper;
import com.lc.portmgr.pojo.Port;
import java.util.ArrayList;

public class PortDAO {
    private final DBHelper helper;

    public PortDAO(DBHelper helper) {
        this.helper = helper;
    }

    public long insert(Port port){
        // Gets the data repository in write mode
        SQLiteDatabase db = helper.getWritableDatabase();
        // Create a new map of values, where column names are the keys
        ContentValues values = new ContentValues();
        values.put(DBHelper.PortTableColumns.COLUMN_ID, port.id);
        values.put(DBHelper.PortTableColumns.COLUMN_PORT, port.port);
        values.put(DBHelper.PortTableColumns.COLUMN_DESC, port.desc);
        values.put(DBHelper.PortTableColumns.COLUMN_ENABLE, port.enable);
        values.put(DBHelper.PortTableColumns.COLUMN_PROTOCOL, port.protocol);

        // Insert the new row, returning the primary key value of the new row
        return db.insert(DBHelper.PORT_TABLE_NAME, null, values);
    }

    public void delete(int serverId, Port port){
        // Gets the data repository in write mode
        SQLiteDatabase db = helper.getWritableDatabase();
        // Define 'where' part of query.
        String selection = String.format("%s = ? and %s = ? and %s = ?", DBHelper.PortTableColumns.COLUMN_ID, DBHelper.PortTableColumns.COLUMN_PORT, DBHelper.PortTableColumns.COLUMN_PROTOCOL);
        String[] selectionArgs = { serverId + "", port.port + "" , port.protocol};
        db.delete(DBHelper.PORT_TABLE_NAME, selection, selectionArgs);
    }

    public void  delete(Integer serverId){
        // Gets the data repository in write mode
        SQLiteDatabase db = helper.getWritableDatabase();
        // Define 'where' part of query.
        String selection = String.format("%s = ?", DBHelper.PortTableColumns.COLUMN_ID);
        String[] selectionArgs = { serverId + ""};
        db.delete(DBHelper.PORT_TABLE_NAME, selection, selectionArgs);
    }

    public void update(Port port){
        SQLiteDatabase db = helper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(DBHelper.PortTableColumns.COLUMN_DESC, port.desc);
        values.put(DBHelper.PortTableColumns.COLUMN_ENABLE, port.enable);
        values.put(DBHelper.PortTableColumns.COLUMN_PROTOCOL, port.protocol);

        String selection = String.format("%s = ? and %s = ? and %s = ?", DBHelper.PortTableColumns.COLUMN_ID, DBHelper.PortTableColumns.COLUMN_PORT, DBHelper.PortTableColumns.COLUMN_PROTOCOL);
        String[] selectionArgs = { port.id + "", port.port + "" , port.protocol};

        db.update(DBHelper.PORT_TABLE_NAME, values, selection, selectionArgs);
    }

    public ArrayList<Port> query(Integer serverId){
        SQLiteDatabase db = helper.getWritableDatabase();
        String[] projection = {
                DBHelper.PortTableColumns.COLUMN_ID,
                DBHelper.PortTableColumns.COLUMN_PORT,
                DBHelper.PortTableColumns.COLUMN_DESC,
                DBHelper.PortTableColumns.COLUMN_ENABLE,
                DBHelper.PortTableColumns.COLUMN_PROTOCOL,
        };

        String selection = DBHelper.PortTableColumns.COLUMN_ID + " = ?";
        String[] selectionArgs = { serverId + "" };

        String sortOrder = DBHelper.PortTableColumns.COLUMN_PORT + " ASC";

        Cursor cursor = db.query(
                DBHelper.PORT_TABLE_NAME,   // The table to query
                projection,             // The array of columns to return (pass null to get all)
                selection,              // The columns for the WHERE clause
                selectionArgs,          // The values for the WHERE clause
                null,                   // don't group the rows
                null,                   // don't filter by row groups
                sortOrder               // The sort order
        );

        ArrayList<Port> ports = new ArrayList<>();
        while(cursor.moveToNext()) {
            int port = cursor.getInt(cursor.getColumnIndexOrThrow(DBHelper.PortTableColumns.COLUMN_PORT));
            String desc = cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.PortTableColumns.COLUMN_DESC));
            int enable = cursor.getInt(cursor.getColumnIndexOrThrow(DBHelper.PortTableColumns.COLUMN_ENABLE));
            String protocol = cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.PortTableColumns.COLUMN_PROTOCOL));
            ports.add(new Port(serverId,port,desc, enable == 1, protocol));
        }
        cursor.close();
        return ports;
    }
}
