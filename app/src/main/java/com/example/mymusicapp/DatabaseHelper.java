package com.example.mymusicapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.media.MediaPlayer;
import android.os.Environment;
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

    //获取存储在数据库中的音乐文件
    public List<File> getStoredMusicFiles() {
        SQLiteDatabase db = this.getReadableDatabase();
        List<File> storedMusicFiles = new ArrayList<File>();
        // 查询数据库中的音乐文件路径
        String query = "SELECT * FROM Songs";
        Cursor cursor = db.rawQuery(query, null);
        if (cursor.moveToFirst()) {
            do {
                String filePath = cursor.getString(0);
                File musicFile = new File(filePath);
                storedMusicFiles.add(musicFile);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return storedMusicFiles;
    }

    public static List<File> getStoredMusicFiles(Context context) {
        DatabaseHelper helper = new DatabaseHelper(context,"SongsApp.db",null,1);
        return helper.getStoredMusicFiles();
    }

    //获取本地音乐文件并存储到数据库中
    public static void initSongsDB(Context context) {
        DatabaseHelper helper = new DatabaseHelper(context,"SongsApp.db",null,1);
        // 获取外部存储的音乐目录
        File musicDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC);
        // 获取音乐文件列表
        List<File> musicFiles = getMusicFiles(musicDirectory);
        // 存储音乐文件路径到数据库
        helper.insertMusicFiles(musicFiles);
    }
    //获取指定目录下的音乐文件
    private static List<File> getMusicFiles(File directory) {
        List<File> musicFiles = new ArrayList<File>();
        // 遍历目录和子目录
        File[] files = directory.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isFile() && isMusicFile(file.getName())) {
                    musicFiles.add(file);
                } else if (file.isDirectory()) {
                    musicFiles.addAll(getMusicFiles(file));
                }
            }
        }
        return musicFiles;
    }
    //判断文件是否为.mp3 或 .wav 格式
    private static boolean isMusicFile(String fileName) {
        return fileName.endsWith(".mp3") || fileName.endsWith(".wav");
    }
    public void insertMusicFiles(List<File> musicFiles) {
        SQLiteDatabase db = this.getWritableDatabase();
        // 遍历音乐文件并插入到数据库中
        for (File musicFile : musicFiles) {
            String filePath = musicFile.getAbsolutePath();
            // 提取歌名和歌手信息
            String title = getTitle(filePath);
            String singer = getSinger(filePath);
            // 创建 ContentValues 对象，用于存储要插入的数据
            ContentValues values = new ContentValues();
            values.put("filePath", filePath);
            values.put("title", title);
            values.put("singer", singer);
            // 插入数据
            db.insert("Songs", null, values);
        }
        db.close();
    }
    // 获取文件的歌名
    private static String getTitle(String filePath) {
        String title = "unknown";
        // 获取 MediaPlayer 实例
        MediaPlayer mediaPlayer = new MediaPlayer();
        try {
            mediaPlayer.setDataSource(filePath);
            // 等待媒体播放器准备好
            mediaPlayer.prepare();
            /**
             * 获取歌名
             * title = mediaPlayer.getTitle();
             */
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // 释放资源
            if (mediaPlayer != null) {
                mediaPlayer.release();
            }
        }
        return title;
    }
    // 获取文件的歌手
    private static String getSinger(String filePath) {
        String artist = "unknown";
        // 获取 MediaPlayer 实例
        MediaPlayer mediaPlayer = new MediaPlayer();
        try {
            mediaPlayer.setDataSource(filePath);
            // 等待媒体播放器准备好
            mediaPlayer.prepare();
            /**
             * 获取歌手
             * artist = mediaPlayer.getSinger();
             */

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // 释放资源
            if (mediaPlayer != null) {
                mediaPlayer.release();
            }
        }

        return artist;
    }
}
