package com.example.mymusicapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {
    // 数据库版本
    private static final int DATABASE_VERSION = 1;
    // 创建数据库的 SQL 语句
    private static final String CREATE_TABLE_USERS = "CREATE TABLE Users (" +
            "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "userName TEXT, " +
            "password TEXT)";
    private static final String CREATE_TABLE_SONGS = "CREATE TABLE Songs (" +
            "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "title TEXT, " +
            "singer TEXT, " +
            "filePath TEXT, "+
            "love BOOLEAN DEFAULT 0, " +
            "collect BOOLEAN DEFAULT 0)";
    private Context mContext;

    public DatabaseHelper(Context context,String name,SQLiteDatabase.CursorFactory factory,int version) {
        super(context,name,factory,version);
        mContext=context;
    }
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(CREATE_TABLE_USERS);
        sqLiteDatabase.execSQL(CREATE_TABLE_SONGS);
        Toast.makeText(mContext,"表格创建成功",Toast.LENGTH_SHORT).show();
    }
    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS Users");
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS Songs");
        onCreate(sqLiteDatabase);
    }

    //获取存储在数据库中的音乐文件路径集合
    public List<String> getStoredMusicPath() {
        SQLiteDatabase db = this.getReadableDatabase();
        List<String> storedMusicFiles = new ArrayList<String>();
        // 查询数据库中的音乐文件路径
        String query = "SELECT * FROM Songs";
        Cursor cursor = db.rawQuery(query, null);
        if (cursor.moveToFirst()) {
            do {
                String filePath = cursor.getString(cursor.getColumnIndexOrThrow("filePath"));
                storedMusicFiles.add(filePath);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return storedMusicFiles;
    }

    //获取本地音乐文件并存储到数据库中
    public static void initSongsDB(Context context) {
        DatabaseHelper helper = new DatabaseHelper(context,"SongsApp.db",null,1);
        // 获取外部存储的音乐目录
        File musicDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC);
        Log.d("TAG","musicDirectory: "+musicDirectory.toString());
        // 获取本地音乐文件路径
        List<String> musicPaths = getMusicPaths(musicDirectory);
        Log.d("TAG","musicPaths: "+musicPaths);
        SQLiteDatabase sqLiteDatabase=helper.getWritableDatabase();
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS Songs");
        sqLiteDatabase.execSQL(CREATE_TABLE_SONGS);
        // 存储音乐文件路径到数据库
        helper.insertMusicFilesInfo(musicPaths);
    }
    //获取指定目录下的音乐文件的路径
    private static List<String> getMusicPaths(File directory) {
        List<String> musicPaths = new ArrayList<String>();
        // 遍历目录和子目录
        File[] files = directory.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isFile() && isMusicFile(file.getName())) {
                    musicPaths.add(file.getAbsolutePath());
                } else if (file.isDirectory()) {
                    musicPaths.addAll(getMusicPaths(file));
                }
            }
        }
        return musicPaths;
    }
    //判断文件是否为.mp3 或 .wav 格式
    private static boolean isMusicFile(String fileName) {
        return fileName.endsWith(".mp3") || fileName.endsWith(".wav");
    }
    public void insertMusicFilesInfo(List<String> musicPaths) {
        SQLiteDatabase db = this.getWritableDatabase();
        // 遍历音乐文件并插入到数据库中
        for (String musicPath : musicPaths) {
            // 提取歌名和歌手信息
            String title = getTitle(musicPath);
            String singer = getSinger(musicPath);
            // 创建 ContentValues 对象，用于存储要插入的数据
            ContentValues values = new ContentValues();
            values.put("filePath", musicPath);
            values.put("title", title);
            values.put("singer", singer);
            //Log.d("TAG","filePath: "+musicPath+" title: "+title+" singer: "+singer);
            // 插入数据
            db.insert("Songs", null, values);
            values.clear();
        }
        db.close();
    }
    // 获取文件的歌名
    public static String getTitle(String filePath) {
        String title = "unknown";
        MediaMetadataRetriever mmr=new MediaMetadataRetriever();
        try {
            mmr.setDataSource(filePath);
            title = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return title;
    }
    // 获取文件的歌手
    public static String getSinger(String filePath) {
        String artist = "unknown";
        MediaMetadataRetriever mmr=new MediaMetadataRetriever();
        try {
            mmr.setDataSource(filePath);
            artist = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return artist;
    }
    // 获取文件的封面
    public static Bitmap getCover(String filePath) {
        MediaMetadataRetriever mmr=new MediaMetadataRetriever();
        mmr.setDataSource(filePath);
        byte[] cover = mmr.getEmbeddedPicture();
        Bitmap bitmap = BitmapFactory.decodeByteArray(cover, 0, cover.length);
        return bitmap;
    }
}
