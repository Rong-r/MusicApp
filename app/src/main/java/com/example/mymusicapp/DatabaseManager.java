package com.example.mymusicapp;

import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class DatabaseManager {
    private static final String MUSIC_LIST_ALL="MUSIC_LIST_ALL";
    private static final String MUSIC_LIST_LOVED="MUSIC_LIST_LOVED";
    private static final String MUSIC_LIST_COLLECTED="MUSIC_LIST_COLLECTED";
    private static volatile DatabaseManager databaseManager=new DatabaseManager();
    private ConcurrentHashMap<String, List<String>> cacheMusicLists=new ConcurrentHashMap<>();
    private final DatabaseHelper databaseHelper=new DatabaseHelper(MainApplication.getContext(),"SongsApp.db",null,1);

    public static DatabaseManager getDatabaseManager(){
        return databaseManager;
    }
    public SQLiteDatabase getSQLiteDatabase(){
        return databaseHelper.getWritableDatabase();
    }
    public void initDatabase(){
        DatabaseHelper.initSongsDB(MainApplication.getContext());
    }
    public void initMusicListAll(){
        List<String> tempList=databaseHelper.getStoredMusicPath();
        cacheMusicLists.put(MUSIC_LIST_ALL,tempList);
    }
    public List<String> getMusicListAll(){
        if(cacheMusicLists.get(MUSIC_LIST_ALL)==null){
            initMusicListAll();
        }
        return cacheMusicLists.get(MUSIC_LIST_ALL);
    }
    public void initMusicListCollectd(){
        List<String> tempList=databaseHelper.getCollectedMusicPath();
        cacheMusicLists.put(MUSIC_LIST_COLLECTED,tempList);
    }
    public List<String> getMusicListCollected(){
        if(cacheMusicLists.get(MUSIC_LIST_COLLECTED)==null){
            initMusicListCollectd();
        }
        return cacheMusicLists.get(MUSIC_LIST_COLLECTED);
    }
    public void initMusicListLoved(){
        List<String> tempList=databaseHelper.getLovedMusicPath();
        cacheMusicLists.put(MUSIC_LIST_LOVED,tempList);
    }
    public List<String> getMusicListLoved(){
        if(cacheMusicLists.get(MUSIC_LIST_LOVED)==null){
            initMusicListLoved();
        }
        return cacheMusicLists.get(MUSIC_LIST_LOVED);
    }
    public String getTitle(String path){
        return databaseHelper.getTitle(path);
    }
    public String getSinger(String path){
        return databaseHelper.getSinger(path);
    }
    public Bitmap getCover(String path){
        return databaseHelper.getCover(path);
    }
    public String getLastFilePath(String filePath,String fileName){
        return databaseHelper.getLastFilePath(filePath,fileName);
    }
    public String getNextFilePath(String filePath,String fileName){
        return databaseHelper.getNextFilePath(filePath,fileName);
    }
    public int isLoved(String filePath){
        return databaseHelper.isLoved(filePath);
    }
    public void setLoved(String filePath){
        databaseHelper.setLoved(filePath);
    }
    public void setLove(String filePath){
        databaseHelper.setLove(filePath);
    }
    public int isCollected(String filePath){
        return databaseHelper.isCollected(filePath);
    }
    public void setCollected(String filePath){
        databaseHelper.setCollected(filePath);
    }
    public void setCollect(String filePath){
        databaseHelper.setCollect(filePath);
    }
    public void insertMusicFilesInfo(List<String> filePaths){
        databaseHelper.insertMusicFilesInfo(filePaths);
    }
}
