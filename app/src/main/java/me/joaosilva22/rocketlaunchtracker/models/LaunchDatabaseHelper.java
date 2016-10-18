package me.joaosilva22.rocketlaunchtracker.models;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class LaunchDatabaseHelper extends SQLiteOpenHelper{

    public static final int DATABASE_VERSION = 3;
    public static final String DATABASE_NAME = "LaunchDatabase.db";

    private static final String SQL_CREATE_LAUNCHES =
            "CREATE TABLE " + LaunchDatabaseContract.LaunchEntry.TABLE_NAME + " (" +
            LaunchDatabaseContract.LaunchEntry.COLUMN_LAUNCH_ID + " INTEGER PRIMARY KEY," +
            LaunchDatabaseContract.LaunchEntry.COLUMN_NAME + " TEXT," +
            LaunchDatabaseContract.LaunchEntry.COLUMN_NET + " TEXT )";

    private static final String SQL_CREATE_DETAILS =
            "CREATE TABLE " + LaunchDatabaseContract.DetailsEntry.TABLE_NAME + " (" +
            LaunchDatabaseContract.DetailsEntry.COLUMN_LAUNCH_ID + " INTEGER PRIMARY KEY," +
            LaunchDatabaseContract.DetailsEntry.COLUMN_MISSION_NAME + " TEXT," +
            LaunchDatabaseContract.DetailsEntry.COLUMN_MISSION_DESCRIPTION + " TEXT," +
            LaunchDatabaseContract.DetailsEntry.COLUMN_PAD_NAME + " TEXT," +
            LaunchDatabaseContract.DetailsEntry.COLUMN_ROCKET_NAME + " TEXT," +
            LaunchDatabaseContract.DetailsEntry.COLUMN_WINDOW_START + " TEXT," +
            LaunchDatabaseContract.DetailsEntry.COLUMN_WINDOW_END + " TEXT," +
            LaunchDatabaseContract.DetailsEntry.COLUMN_NOTIFY + " INTEGER DEFAULT 0 )";

    private static final String SQL_DELETE_LAUNCHES =
            "DROP TABLE IF EXISTS " + LaunchDatabaseContract.LaunchEntry.TABLE_NAME;

    private static final String SQL_DELETE_DETAILS =
            "DROP TABLE IF EXISTS " + LaunchDatabaseContract.DetailsEntry.TABLE_NAME;

    public LaunchDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(SQL_CREATE_LAUNCHES);
        sqLiteDatabase.execSQL(SQL_CREATE_DETAILS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL(SQL_DELETE_LAUNCHES);
        sqLiteDatabase.execSQL(SQL_DELETE_DETAILS);
        onCreate(sqLiteDatabase);
    }
}
