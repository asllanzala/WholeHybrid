package com.honeywell.wholesale.framework.database;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;


/**
 * Created by xiaofei on 7/15/16.
 * Stored time for Incremental update
 */

public class IncrementalDAO {
    private static final String TAG = "IncrementalDAO";
    private static final String INCREMENTAL_TABLE = "incremental";

    private static final String KEY_ID = "id";
    private static final String INCREMENTAL_KEY = "incremental_key";
    private static final String INCREMENTAL_TIME = "time";
    private static final String INCREMENTAL_SHOP_ID = "shop_id";


    public static final String CREATE_INCREMENTAL_TABLE = "CREATE TABLE IF NOT EXISTS " + INCREMENTAL_TABLE
            + "("
            + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + INCREMENTAL_KEY + " TEXT,"
            + INCREMENTAL_SHOP_ID + " TEXT,"
            + INCREMENTAL_TIME + " TEXT"
            + ")";

    public static final String DROP_INCREMENTAL_TABLE = "DROP TABLE IF EXISTS " + INCREMENTAL_TABLE;


    public static void addIncrementalItem(Class cls, String time, String shopId) {
        DataBaseHelper dataBaseHelper = DataBaseHelper.getInstance();
        if (dataBaseHelper == null) {
            return;
        }
        SQLiteDatabase db = dataBaseHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(INCREMENTAL_KEY, cls.getName());
        values.put(INCREMENTAL_TIME, time);
        values.put(INCREMENTAL_SHOP_ID, shopId);

        long i = db.insert(INCREMENTAL_TABLE, null, values);
        db.close();
    }

    public static boolean isExistItem(Class cls, String shopId){
        DataBaseHelper dataBaseHelper = DataBaseHelper.getInstance();
        if (dataBaseHelper == null) {
            throw new RuntimeException("can not open db");
        }

        SQLiteDatabase db = dataBaseHelper.getReadableDatabase();
        Cursor cursor = db.query(INCREMENTAL_TABLE, new String[] {KEY_ID, INCREMENTAL_TIME}
                , INCREMENTAL_KEY + "=? and " + INCREMENTAL_SHOP_ID + "=?",
                new String[] {cls.getName(), shopId}, null, null, null, null);

        if (cursor.getCount() == 0){
            cursor.close();
            return false;
        }

        cursor.close();
        return true;
    }

    public static int updateIncrementalItem(Class cls, String time, String shopId) {
        DataBaseHelper dataBaseHelper = DataBaseHelper.getInstance();

        ContentValues args = new ContentValues();
        String clsName = cls.getName();
        args.put(INCREMENTAL_TIME, time);

        SQLiteDatabase db = dataBaseHelper.getWritableDatabase();
        return db.update(INCREMENTAL_TABLE, args, INCREMENTAL_KEY + "=? and " + INCREMENTAL_SHOP_ID + "=?", new String[]{cls.getName(), shopId});
    }

    public static String queryIncrementalItem(Class cls, String shopId){
        DataBaseHelper dataBaseHelper = DataBaseHelper.getInstance();
        if (dataBaseHelper == null) {
            throw new RuntimeException("can not open db");
        }

        SQLiteDatabase db = dataBaseHelper.getReadableDatabase();

        Cursor cursor = db.query(INCREMENTAL_TABLE, new String[] {KEY_ID, INCREMENTAL_TIME}
                , INCREMENTAL_KEY + "=? and " + INCREMENTAL_SHOP_ID + "=?",
                new String[] {cls.getName(), shopId}, null, null, null, null);

        if (cursor.getCount() == 0){
            cursor.close();
            return "";
        }

        cursor.moveToFirst();

        return cursor.getString(1);
    }
}
