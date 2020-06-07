package skku.edu.elephantory;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;


public class DBHelper extends SQLiteOpenHelper {
    public static String NAME = "job_history.db";
    public static int dbVersion = 1;

    public DBHelper(@Nullable Context context) {
        super(context, NAME, null, dbVersion);
    }

    /*
    public DBHelper(@Nullable Context context, @Nullable String name,
                    @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }
     */

    @Override
    public void onCreate(SQLiteDatabase db) {
        printDebug("///// onCreate(SQLiteDatabase db)");
        db.execSQL("create table if not exists Job(" +
                "_id integer PRIMARY KEY autoincrement, " +
                "job_id text UNIQUE, " +
                "name text, " +
                "user text, " +
                "elapsed_time text)");
    }

    public void onOpen(SQLiteDatabase db) {
        printDebug("///// opOpen()");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        printDebug("///// onUpgrade()");

        if(newVersion > 1) {
            db.execSQL("DROP TABLE IF EXISTS Job");
            // onCreate(db);
        }
    }

    public void printDebug(String data) {
        Log.d("DBHelper", data);
    }
}