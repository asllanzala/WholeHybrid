package com.honeywell.wholesale.framework.database;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.honeywell.wholesale.framework.model.StockItem;
import org.json.JSONException;

import java.util.ArrayList;

/**
 * Created by xiaofei on 7/27/16.
 * 库存记录
 */
public class StockDAO {
    private static final String TAG = "StockDAO";

    private static final String STOCK_TABLE = "stock";
    private static final String KEY_ID = "id";

    // stock table column

    private static final String NAME = "name";
    private static final String COUNT = "count";
    private static final String UNIT = "unit";
    private static final String UNIT_PRICE = "unit_price";

    private static final String STOCK_IN_NAME = "stock_in_employee_name";
    private static final String STOCK_IN_TIME = "stock_in_time";

    private static final String STOCK_FINISH_TIME = "stock_finish_time";
    private static final String STOCK_FINISH_NAME = "stock_finish_employee_name";

    private static final String SHOP_ID = "shop_id";
    private static final String PRODUCT_CODE = "product_code";
    private static final String PRODUCT_NUMBER = "product_number";
    private static final String STOCK_RECORD_ID = "stock_record_id";

    public static final String DROP_STOCK_TABLE = "DROP TABLE IF EXISTS " + STOCK_TABLE;

    // stock status
    // 1 have in stock
    // 100 wait for in stock
    // 200 stock update
    private static final String STOCK_STATUS = "status";

    public static final String STOCK_IN_STATUS = "1";
    public static final String STOCK_UNCONFIRM_STATUS = "100";
    public static final String STOCK_UPDATE_STATUS = "200";

    public static final String CREATE_STOCK_TABLE =  "CREATE TABLE IF NOT EXISTS " + STOCK_TABLE + "("
            + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + NAME + " TEXT,"
            + COUNT + " TEXT,"
            + UNIT + " TEXT,"
            + UNIT_PRICE + " TEXT,"
            + STOCK_IN_NAME + " TEXT,"

            + STOCK_IN_TIME + " TEXT,"
            + STOCK_FINISH_TIME + " TEXT,"
            + STOCK_FINISH_NAME + " TEXT,"
            + SHOP_ID + " TEXT,"
            + STOCK_STATUS + " TEXT,"
            + PRODUCT_CODE + " TEXT,"
            + PRODUCT_NUMBER + " TEXT,"
            + STOCK_RECORD_ID + " TEXT"
            + ")";


    // add stock
    public static void addStock(
            @NonNull String name, @NonNull String count, String unit,
            String unitPrice, String stockInEmployeeName,
            @NonNull String stockInTime, @NonNull String stockFinishTime,
            @Nullable String stockFinishEmployeeName, @Nullable String shopId,
            @Nullable String stockStatus, @NonNull String productCode, @NonNull String stockRecordId, String productNumber) {

        DataBaseHelper dataBaseHelper = DataBaseHelper.getInstance();
        if (dataBaseHelper == null){
            return;
        }
        SQLiteDatabase db = dataBaseHelper.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(NAME, name);
        values.put(COUNT, count);
        values.put(UNIT, unit);
        values.put(UNIT_PRICE, unitPrice);
        values.put(STOCK_IN_NAME, stockInEmployeeName);

        values.put(STOCK_IN_TIME, stockInTime);
        values.put(STOCK_FINISH_TIME, stockFinishTime);
        values.put(STOCK_FINISH_NAME, stockFinishEmployeeName);

        values.put(SHOP_ID, shopId);
        values.put(STOCK_STATUS, stockStatus);
        values.put(PRODUCT_CODE, productCode);
        values.put(STOCK_RECORD_ID, stockRecordId);
        values.put(PRODUCT_NUMBER, productNumber);
        // Inserting Row
        db.insert(STOCK_TABLE, null, values);
        db.close();
    }

    public static void addStock(StockItem stockItem, String status) {

        DataBaseHelper dataBaseHelper = DataBaseHelper.getInstance();
        if (dataBaseHelper == null){
            return;
        }
        SQLiteDatabase db = dataBaseHelper.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(NAME, stockItem.getmName());
        values.put(COUNT, stockItem.getmCount());
        values.put(UNIT, stockItem.getmUnit());
        values.put(UNIT_PRICE, stockItem.getmUnitPrice());
        values.put(STOCK_IN_NAME, stockItem.getmStockInEmployeeName());

        values.put(STOCK_IN_TIME, stockItem.getmStockInTime());
        values.put(STOCK_FINISH_TIME, stockItem.getmStockFinishTime());
        values.put(STOCK_FINISH_NAME, stockItem.getmStockFinishEmployeeName());

        values.put(SHOP_ID, stockItem.mShopId);
        values.put(PRODUCT_CODE, stockItem.getmProductCode());
        values.put(PRODUCT_NUMBER, stockItem.getmProductNumber());
        values.put(STOCK_STATUS, status);
        values.put(STOCK_RECORD_ID, stockItem.mStockRecordId);

        // Inserting Row
        db.insert(STOCK_TABLE, null, values);
        db.close();
    }


    // get all stock
    public static ArrayList<StockItem> getStockByShopId(String shopId, String status){

        DataBaseHelper dataBaseHelper = DataBaseHelper.getInstance();
        if (dataBaseHelper == null){
            return null;
        }
        SQLiteDatabase db = dataBaseHelper.getReadableDatabase();

        Cursor cursor = db.query(STOCK_TABLE, new String[] { KEY_ID, NAME,
                        COUNT, UNIT, UNIT_PRICE, STOCK_IN_NAME, STOCK_IN_TIME, STOCK_FINISH_TIME,
                        STOCK_FINISH_NAME, PRODUCT_CODE, STOCK_RECORD_ID, SHOP_ID, PRODUCT_NUMBER},
                SHOP_ID + "=? and " + STOCK_STATUS + "=?",
                new String[] {shopId, status}, null, null, null, null);

        ArrayList<StockItem> arrayList = new ArrayList<>();

        if (cursor != null && cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                StockItem stockItem = new StockItem(
                        cursor.getString(cursor.getColumnIndex(COUNT)),
                        cursor.getString(cursor.getColumnIndex(NAME)),
                        cursor.getString(cursor.getColumnIndex(STOCK_FINISH_NAME)),
                        cursor.getString(cursor.getColumnIndex(STOCK_FINISH_TIME)),
                        cursor.getString(cursor.getColumnIndex(STOCK_IN_NAME)),
                        cursor.getString(cursor.getColumnIndex(STOCK_IN_TIME)),
                        cursor.getString(cursor.getColumnIndex(UNIT)),
                        cursor.getString(cursor.getColumnIndex(UNIT_PRICE)),
                        cursor.getString(cursor.getColumnIndex(PRODUCT_CODE)),
                        cursor.getString(cursor.getColumnIndex(STOCK_RECORD_ID)),
                        cursor.getString(cursor.getColumnIndex(SHOP_ID)),
                        cursor.getString(cursor.getColumnIndex(PRODUCT_NUMBER))
                );
                arrayList.add(stockItem);
            }
        }

        if (cursor != null){
            cursor.close();
        }
        return arrayList;
    }

    public static ArrayList<StockItem> queryStockItem(String stockRecordId, String status) throws JSONException {
        DataBaseHelper dataBaseHelper = DataBaseHelper.getInstance();
        if (dataBaseHelper == null) {
            return null;
        }
        SQLiteDatabase db = dataBaseHelper.getWritableDatabase();

        Cursor cursor = db.query(STOCK_TABLE, new String[] { KEY_ID, NAME,
                        COUNT, UNIT, UNIT_PRICE, STOCK_IN_NAME, STOCK_IN_TIME, STOCK_FINISH_TIME,
                        STOCK_FINISH_NAME, PRODUCT_CODE, STOCK_RECORD_ID, SHOP_ID, PRODUCT_NUMBER},
                STOCK_RECORD_ID + "=? and " + STOCK_STATUS + "=?",
                new String[] {stockRecordId, status,}, null, null, null, null);
        ArrayList<StockItem> arrayList = new ArrayList<>();

        if (cursor != null && cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                StockItem stockItem = new StockItem(
                        cursor.getString(cursor.getColumnIndex(COUNT)),
                        cursor.getString(cursor.getColumnIndex(NAME)),
                        cursor.getString(cursor.getColumnIndex(STOCK_FINISH_NAME)),
                        cursor.getString(cursor.getColumnIndex(STOCK_FINISH_TIME)),
                        cursor.getString(cursor.getColumnIndex(STOCK_IN_NAME)),
                        cursor.getString(cursor.getColumnIndex(STOCK_IN_TIME)),
                        cursor.getString(cursor.getColumnIndex(UNIT)),
                        cursor.getString(cursor.getColumnIndex(UNIT_PRICE)),
                        cursor.getString(cursor.getColumnIndex(PRODUCT_CODE)),
                        cursor.getString(cursor.getColumnIndex(STOCK_RECORD_ID)),
                        cursor.getString(cursor.getColumnIndex(SHOP_ID)),
                        cursor.getString(cursor.getColumnIndex(PRODUCT_NUMBER))
                );
                arrayList.add(stockItem);
            }
        }
        return arrayList;
    }


    public static int updateStockItem(String mStockRecordId, String status){
        DataBaseHelper dataBaseHelper = DataBaseHelper.getInstance();
        SQLiteDatabase db = dataBaseHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(STOCK_STATUS, status);
        return db.update(STOCK_TABLE, values, STOCK_RECORD_ID + "=?", new String[]{mStockRecordId});
    }


    public static int updateStockItem(StockItem stockItem, String status){
        DataBaseHelper dataBaseHelper = DataBaseHelper.getInstance();

        SQLiteDatabase db = dataBaseHelper.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(NAME, stockItem.getmName());
        values.put(COUNT, stockItem.getmCount());
        values.put(UNIT, stockItem.getmUnit());
        values.put(UNIT_PRICE, stockItem.getmUnitPrice());
        values.put(STOCK_IN_NAME, stockItem.getmStockInEmployeeName());

        values.put(STOCK_IN_TIME, stockItem.getmStockInTime());
        values.put(STOCK_FINISH_TIME, stockItem.getmStockFinishTime());
        values.put(STOCK_FINISH_NAME, stockItem.getmStockFinishEmployeeName());

        values.put(SHOP_ID, stockItem.mShopId);
        values.put(STOCK_STATUS, status);
        values.put(PRODUCT_CODE, stockItem.getmProductCode());
        values.put(STOCK_RECORD_ID, stockItem.mStockRecordId);
        values.put(PRODUCT_NUMBER,stockItem.getmProductNumber());

        return db.update(STOCK_TABLE, values, STOCK_RECORD_ID + "=?", new String[]{stockItem.mStockRecordId});
    }


    public static int removeStockItem(String recordId) {
        DataBaseHelper dataBaseHelper = DataBaseHelper.getInstance();
        if (dataBaseHelper == null) {
            return 0;
        }
        SQLiteDatabase db = dataBaseHelper.getWritableDatabase();

        return db.delete(STOCK_TABLE, STOCK_RECORD_ID + "=?", new String[]{recordId});
    }

    public static void removeDataAfter30days(){
        DataBaseHelper dataBaseHelper = DataBaseHelper.getInstance();
        if (dataBaseHelper == null){
            return ;
        }
        SQLiteDatabase db = dataBaseHelper.getWritableDatabase();

        String delSql = "DELETE FROM " + STOCK_TABLE + " WHERE " + STOCK_FINISH_TIME + " <= date('now','-30 day')";

        db.execSQL(delSql);
    }
}
