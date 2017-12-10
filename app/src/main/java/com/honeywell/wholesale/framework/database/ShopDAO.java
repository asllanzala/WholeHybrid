package com.honeywell.wholesale.framework.database;

import android.database.sqlite.SQLiteDatabase;

/**
 * Created by H155935 on 16/5/17.
 * Email: xiaofei.he@honeywell.com
 */
public class ShopDAO {
    private static final String SHOP_TABLE = "shop";

    private static final String KEY_ID = "id";
    //shop table column
    private static final String SHOP_ID = "shop_id";
    private static final String SHOP_ACCOUNT_ID = "account_id";
    private static final String SHOP_NAME = "shop_name";

    public static final String CREATE_SHOP_TABLE =  "CREATE TABLE IF NOT EXISTS " + SHOP_TABLE + "("
            + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + SHOP_ID + " INTEGER,"
            + SHOP_ACCOUNT_ID + " INTEGER," + SHOP_NAME + " TEXT" + ")";

    private SQLiteDatabase db;
}
