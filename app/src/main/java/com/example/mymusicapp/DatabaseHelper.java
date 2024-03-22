package com.example.mymusicapp;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

public class DatabaseHelper {
    // 数据库名称
    private static final String DATABASE_USER = "user";
    private static final String DATABASE_SONG = "song";
    // 数据库版本
    private static final int DATABASE_VERSION = 1;

    // 创建数据库的 SQL 语句
    private static final String CREATE_TABLE_USERS = "CREATE TABLE users (" +
            "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "userName TEXT, " +
            "password TEXT)";
    private static final String CREATE_TABLE_SONG = "CREATE TABLE songs (" +
            "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "title TEXT, " +
            "path TEXT, "+
            "love BOOLEAN, " +
            "collect BOOLEAN)";

    private SQLiteDatabase database;

    public DatabaseHelper(Context context) {
        //SQLiteDatabase.openOrCreateDatabase(context.getDatabasePath(DATABASE_USER), null, null, DATABASE_VERSION);
    }

    // 插入用户信息的方法
    public void insertUser(String userName, String password) {
       // database = getWritableDatabase();
        database.execSQL("INSERT INTO users (userName, password) VALUES ('" + userName + "', '" + password + "')");
        database.close();
    }
}
