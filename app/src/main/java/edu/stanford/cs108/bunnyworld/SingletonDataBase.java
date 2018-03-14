package edu.stanford.cs108.bunnyworld;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by emohelw on 2/28/2018.
 */

class SingletonDataBase extends SQLiteOpenHelper {

    private static SingletonDataBase ourInstance; // Singleton Instance
    private Context context;

    //Database Info
    private static final String DATABASE_NAME = "GameDB";
    private static final int VERSION = 1;

    // Database Table
    private static final String TABLE_GAMES = "games"; //store games or documents

    public static synchronized SingletonDataBase getInstance(Context context) {
        if(ourInstance == null){
            ourInstance = new SingletonDataBase(context.getApplicationContext());
        }
        return ourInstance;
    }

    private SingletonDataBase(Context context) {
        super(context, DATABASE_NAME, null, VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
