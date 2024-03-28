package com.example.mymusicapp;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class DatabaseManager {
    private ConcurrentHashMap<String, List<String>> cacheMusicLists=new ConcurrentHashMap<>();
    private  DatabaseHelper databaseHelper=new DatabaseHelper(MainApplication.getContext(),"SongsApp.db",null,1);

}
