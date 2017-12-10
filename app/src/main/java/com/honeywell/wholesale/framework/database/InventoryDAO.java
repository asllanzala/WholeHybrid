package com.honeywell.wholesale.framework.database;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.Nullable;
import android.util.Log;

import com.honeywell.wholesale.framework.model.Inventory;

import java.util.ArrayList;

import static android.transition.Fade.IN;
import static com.honeywell.wholesale.framework.search.SearchResultItem.ResultType.INVENTORY;
import static com.honeywell.wholesale.lib.util.StringUtil.getString;

/**
 * Created by xiaofei on 7/14/16.
 *
 */
public class InventoryDAO {

    private static final String INVENTORY_TABLE = "inventory";

    private static final String KEY_ID = "id";

    private static final String INVENTORY_NUMBER = "product_number";
    private static final String INVENTORY_CODE = "product_code";
    private static final String INVENTORY_ID = "product_id";
    private static final String INVENTORY_CATEGORY = "category";
    private static final String INVENTORY_SALE_NUMBER = "sale_number";
    private static final String INVENTORY_NAME = "name";
    private static final String INVENTORY_STANDARD_PRICE = "standard_price";
    private static final String INVENTORY_STOCK_PRICE = "stock_price";
    private static final String INVENTORY_VIP_PRICE = "vip_price";
    private static final String INVENTORY_AVG_PRICE = "avg_price";
    private static final String INVENTORY_LAST_TIME = "last_sale_time";
    private static final String INVENTORY_PIC_SRC = "pic_src";
    private static final String INVENTORY_QUANTITY = "quantity";

    private static final String INVENTORY_PINYIN = "pinyin";
    private static final String INVENTORY_INITIALS = "initials";

    private static final String INVENTORY_UNIT = "unit_name";

    // shop_id if a person has two same shop
    private static final String INVENTORYT_SHOP_ID = "shop_id";

    public static final String CREATE_INVENTORY_TABLE = "CREATE TABLE IF NOT EXISTS " + INVENTORY_TABLE
            + "("
            + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + INVENTORY_CODE + " TEXT,"
            + INVENTORY_NUMBER + " TEXT,"
            + INVENTORY_CATEGORY + " TEXT,"

            + INVENTORY_SALE_NUMBER + " TEXT,"
            + INVENTORY_NAME + " TEXT,"
            + INVENTORY_STANDARD_PRICE + " TEXT,"
            + INVENTORY_STOCK_PRICE + " TEXT,"
            + INVENTORY_VIP_PRICE + " TEXT,"
            + INVENTORY_AVG_PRICE + " TEXT,"
            + INVENTORY_LAST_TIME + " TEXT,"
            + INVENTORY_PIC_SRC + " TEXT,"
            + INVENTORY_QUANTITY + " TEXT,"
            + INVENTORYT_SHOP_ID + " TEXT,"
            + INVENTORY_PINYIN + " TEXT,"
            + INVENTORY_INITIALS + " TEXT,"
            + INVENTORY_UNIT + " TEXT,"
            + INVENTORY_ID + " INTEGER"
            + ")";

    public static final String DROP_INVENTORY_TABLE = "DROP TABLE IF EXISTS " + INVENTORY_TABLE;

    public static void addInventoryItem(String productCode, String category, String saleNumber,
                                        String name, String standardPrice, String stockPrice, String vipPrice,
                                        String avgPrice, String lastSaleTime, String picSrc, String quantity,
                                        String shopId, int productId, String productNumber, String unit) {
        DataBaseHelper dataBaseHelper = DataBaseHelper.getInstance();
        if (dataBaseHelper == null) {
            return;
        }
        SQLiteDatabase db = dataBaseHelper.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(INVENTORY_CODE, productCode);
        values.put(INVENTORY_CATEGORY, category);
        values.put(INVENTORY_SALE_NUMBER, saleNumber);
        values.put(INVENTORY_NAME, name);
        values.put(INVENTORY_STANDARD_PRICE, standardPrice);
        values.put(INVENTORY_STOCK_PRICE, stockPrice);
        values.put(INVENTORY_VIP_PRICE, vipPrice);
        values.put(INVENTORY_AVG_PRICE, avgPrice);
        values.put(INVENTORY_LAST_TIME, lastSaleTime);
        values.put(INVENTORY_PIC_SRC, picSrc);
        values.put(INVENTORY_QUANTITY, quantity);
        values.put(INVENTORYT_SHOP_ID, shopId);
        values.put(INVENTORY_ID, productId);
        values.put(INVENTORY_NUMBER, productNumber);
        values.put(INVENTORY_UNIT, unit);
        // Inserting Row
        long i = db.insert(INVENTORY_TABLE, null, values);
        db.close();
    }

    public static void addInventoryItem(Inventory inventory, String shopId) {
        DataBaseHelper dataBaseHelper = DataBaseHelper.getInstance();
        if (dataBaseHelper == null) {
            return;
        }
        SQLiteDatabase db = dataBaseHelper.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(INVENTORY_CODE, inventory.getProductCode());
        values.put(INVENTORY_CATEGORY, inventory.getCategory());
        values.put(INVENTORY_SALE_NUMBER, inventory.getSaleNumber());
        values.put(INVENTORY_NAME, inventory.getProductName());
        values.put(INVENTORY_STANDARD_PRICE, inventory.getStandardPrice());
        values.put(INVENTORY_STOCK_PRICE, inventory.getStockPrice());
        values.put(INVENTORY_VIP_PRICE, inventory.getVipPrice());
        values.put(INVENTORY_AVG_PRICE, inventory.getAvgPrice());
        values.put(INVENTORY_LAST_TIME, inventory.getLastSaleTime());
        values.put(INVENTORY_PIC_SRC, inventory.getPicSrc());
        values.put(INVENTORY_QUANTITY, inventory.getQuantity());
        values.put(INVENTORYT_SHOP_ID, shopId);
        values.put(INVENTORY_PINYIN, inventory.getPinyin());
        values.put(INVENTORY_INITIALS, inventory.getPyInitials());
        values.put(INVENTORY_ID, inventory.getProductId());
        values.put(INVENTORY_NUMBER, inventory.getProductNumber());
        values.put(INVENTORY_UNIT, inventory.getUnit());
        // Inserting Row
        long i = db.insert(INVENTORY_TABLE, null, values);
        db.close();
    }

    public static ArrayList<Inventory> getInventoryItems(String shopId, String quantity) {
        DataBaseHelper dataBaseHelper = DataBaseHelper.getInstance();
        if (dataBaseHelper == null) {
            return null;
        }
        SQLiteDatabase db = dataBaseHelper.getReadableDatabase();

        Cursor cursor = db.query(INVENTORY_TABLE, new String[]{KEY_ID, INVENTORY_CODE,
                        INVENTORY_CATEGORY, INVENTORY_SALE_NUMBER, INVENTORY_NAME, INVENTORY_STANDARD_PRICE,
                        INVENTORY_STOCK_PRICE, INVENTORY_VIP_PRICE, INVENTORY_AVG_PRICE, INVENTORY_LAST_TIME,
                        INVENTORY_PIC_SRC, INVENTORY_QUANTITY, INVENTORY_ID, INVENTORY_NUMBER, INVENTORY_UNIT}, INVENTORYT_SHOP_ID + "=? and " + INVENTORY_QUANTITY + ">?",
                new String[]{String.valueOf(shopId), String.valueOf(quantity)}, null, null, null, null);

        ArrayList<Inventory> arrayList = new ArrayList<>();
        if (cursor != null && cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                Inventory inventoryItem = new Inventory(
                        cursor.getString(cursor.getColumnIndex(INVENTORY_CODE)),
                        cursor.getString(cursor.getColumnIndex(INVENTORY_CATEGORY)),
                        cursor.getString(cursor.getColumnIndex(INVENTORY_SALE_NUMBER)),
                        cursor.getString(cursor.getColumnIndex(INVENTORY_NAME)),
                        cursor.getString(cursor.getColumnIndex(INVENTORY_STANDARD_PRICE)),
                        cursor.getString(cursor.getColumnIndex(INVENTORY_STOCK_PRICE)),
                        cursor.getString(cursor.getColumnIndex(INVENTORY_VIP_PRICE)),
                        cursor.getString(cursor.getColumnIndex(INVENTORY_AVG_PRICE)),
                        cursor.getString(cursor.getColumnIndex(INVENTORY_LAST_TIME)),
                        cursor.getString(cursor.getColumnIndex(INVENTORY_PIC_SRC)),
                        cursor.getString(cursor.getColumnIndex(INVENTORY_QUANTITY)),
                        cursor.getInt(cursor.getColumnIndex(INVENTORY_ID)),
                        cursor.getString(cursor.getColumnIndex(INVENTORY_NUMBER)),
                        cursor.getString(cursor.getColumnIndex(INVENTORY_UNIT)));
                arrayList.add(inventoryItem);
            }
        }

        if (cursor != null) {
            cursor.close();
        }
        return arrayList;
    }

    public static ArrayList<Inventory> getInventoryItems(String shopId) {
        DataBaseHelper dataBaseHelper = DataBaseHelper.getInstance();
        if (dataBaseHelper == null) {
            return null;
        }
        SQLiteDatabase db = dataBaseHelper.getReadableDatabase();

        Cursor cursor = db.query(INVENTORY_TABLE, new String[]{KEY_ID, INVENTORY_CODE,
                        INVENTORY_CATEGORY, INVENTORY_SALE_NUMBER, INVENTORY_NAME, INVENTORY_STANDARD_PRICE,
                        INVENTORY_STOCK_PRICE, INVENTORY_VIP_PRICE, INVENTORY_AVG_PRICE, INVENTORY_LAST_TIME,
                        INVENTORY_PIC_SRC, INVENTORY_QUANTITY, INVENTORY_ID, INVENTORY_NUMBER, INVENTORY_UNIT}, INVENTORYT_SHOP_ID + "=?",
                new String[]{String.valueOf(shopId)}, null, null, null, null);

        ArrayList<Inventory> arrayList = new ArrayList<>();
        if (cursor != null && cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                Inventory inventoryItem = new Inventory(
                        cursor.getString(cursor.getColumnIndex(INVENTORY_CODE)),
                        cursor.getString(cursor.getColumnIndex(INVENTORY_CATEGORY)),
                        cursor.getString(cursor.getColumnIndex(INVENTORY_SALE_NUMBER)),
                        cursor.getString(cursor.getColumnIndex(INVENTORY_NAME)),
                        cursor.getString(cursor.getColumnIndex(INVENTORY_STANDARD_PRICE)),
                        cursor.getString(cursor.getColumnIndex(INVENTORY_STOCK_PRICE)),
                        cursor.getString(cursor.getColumnIndex(INVENTORY_VIP_PRICE)),
                        cursor.getString(cursor.getColumnIndex(INVENTORY_AVG_PRICE)),
                        cursor.getString(cursor.getColumnIndex(INVENTORY_LAST_TIME)),
                        cursor.getString(cursor.getColumnIndex(INVENTORY_PIC_SRC)),
                        cursor.getString(cursor.getColumnIndex(INVENTORY_QUANTITY)),
                        cursor.getInt(cursor.getColumnIndex(INVENTORY_ID)),
                        cursor.getString(cursor.getColumnIndex(INVENTORY_NUMBER)),
                        cursor.getString(cursor.getColumnIndex(INVENTORY_UNIT)));
                arrayList.add(inventoryItem);
            }
        }

        if (cursor != null) {
            cursor.close();
        }
        return arrayList;
    }

    public static Inventory queryInventoryItem(String shopId, String productCode){
        DataBaseHelper dataBaseHelper = DataBaseHelper.getInstance();
        if (dataBaseHelper == null) {
            return null;
        }
        SQLiteDatabase db = dataBaseHelper.getReadableDatabase();

        Cursor cursor = db.query(INVENTORY_TABLE, new String[]{KEY_ID, INVENTORY_CODE,
                        INVENTORY_CATEGORY, INVENTORY_SALE_NUMBER, INVENTORY_NAME, INVENTORY_STANDARD_PRICE,
                        INVENTORY_STOCK_PRICE, INVENTORY_VIP_PRICE, INVENTORY_AVG_PRICE, INVENTORY_LAST_TIME,
                        INVENTORY_PIC_SRC, INVENTORY_QUANTITY, INVENTORY_ID, INVENTORY_NUMBER, INVENTORY_UNIT}, INVENTORYT_SHOP_ID + "=? and " + INVENTORY_CODE + "=?",
                new String[]{shopId, productCode}, null, null, null, null);

        Inventory inventoryItem = null;
        if (cursor != null && cursor.getCount() > 0){
            while (cursor.moveToNext()) {
                inventoryItem = new Inventory(
                        cursor.getString(cursor.getColumnIndex(INVENTORY_CODE)),
                        cursor.getString(cursor.getColumnIndex(INVENTORY_CATEGORY)),
                        cursor.getString(cursor.getColumnIndex(INVENTORY_SALE_NUMBER)),
                        cursor.getString(cursor.getColumnIndex(INVENTORY_NAME)),
                        cursor.getString(cursor.getColumnIndex(INVENTORY_STANDARD_PRICE)),
                        cursor.getString(cursor.getColumnIndex(INVENTORY_STOCK_PRICE)),
                        cursor.getString(cursor.getColumnIndex(INVENTORY_VIP_PRICE)),
                        cursor.getString(cursor.getColumnIndex(INVENTORY_AVG_PRICE)),
                        cursor.getString(cursor.getColumnIndex(INVENTORY_LAST_TIME)),
                        cursor.getString(cursor.getColumnIndex(INVENTORY_PIC_SRC)),
                        cursor.getString(cursor.getColumnIndex(INVENTORY_QUANTITY)),
                        cursor.getInt(cursor.getColumnIndex(INVENTORY_ID)),
                        cursor.getString(cursor.getColumnIndex(INVENTORY_NUMBER)),
                        cursor.getString(cursor.getColumnIndex(INVENTORY_UNIT)));
            }
        }

        return inventoryItem;
    }

    public static Inventory queryInventoryItem(String shopId, @Nullable int productId){
        DataBaseHelper dataBaseHelper = DataBaseHelper.getInstance();
        if (dataBaseHelper == null) {
            return null;
        }
        SQLiteDatabase db = dataBaseHelper.getReadableDatabase();

        Cursor cursor = db.query(INVENTORY_TABLE, new String[]{KEY_ID, INVENTORY_CODE,
                        INVENTORY_CATEGORY, INVENTORY_SALE_NUMBER, INVENTORY_NAME, INVENTORY_STANDARD_PRICE,
                        INVENTORY_STOCK_PRICE, INVENTORY_VIP_PRICE, INVENTORY_AVG_PRICE, INVENTORY_LAST_TIME,
                        INVENTORY_PIC_SRC, INVENTORY_QUANTITY, INVENTORY_ID, INVENTORY_NUMBER, INVENTORY_UNIT}, INVENTORYT_SHOP_ID + "=? and " + INVENTORY_ID + "=?",
                new String[]{shopId, String.valueOf(productId)}, null, null, null, null);

        Inventory inventoryItem = null;
        if (cursor != null && cursor.getCount() > 0){
            while (cursor.moveToNext()) {
                inventoryItem = new Inventory(
                        cursor.getString(cursor.getColumnIndex(INVENTORY_CODE)),
                        cursor.getString(cursor.getColumnIndex(INVENTORY_CATEGORY)),
                        cursor.getString(cursor.getColumnIndex(INVENTORY_SALE_NUMBER)),
                        cursor.getString(cursor.getColumnIndex(INVENTORY_NAME)),
                        cursor.getString(cursor.getColumnIndex(INVENTORY_STANDARD_PRICE)),
                        cursor.getString(cursor.getColumnIndex(INVENTORY_STOCK_PRICE)),
                        cursor.getString(cursor.getColumnIndex(INVENTORY_VIP_PRICE)),
                        cursor.getString(cursor.getColumnIndex(INVENTORY_AVG_PRICE)),
                        cursor.getString(cursor.getColumnIndex(INVENTORY_LAST_TIME)),
                        cursor.getString(cursor.getColumnIndex(INVENTORY_PIC_SRC)),
                        cursor.getString(cursor.getColumnIndex(INVENTORY_QUANTITY)),
                        cursor.getInt(cursor.getColumnIndex(INVENTORY_ID)),
                        cursor.getString(cursor.getColumnIndex(INVENTORY_NUMBER)),
                        cursor.getString(cursor.getColumnIndex(INVENTORY_UNIT)));
            }
        }
        
        return inventoryItem;
    }

    public static ArrayList<Inventory> queryInventoryItems(String shopId, @Nullable String category){
        DataBaseHelper dataBaseHelper = DataBaseHelper.getInstance();
        if (dataBaseHelper == null) {
            return null;
        }
        SQLiteDatabase db = dataBaseHelper.getWritableDatabase();

        ArrayList<Inventory> arrayList = new ArrayList<>();
        Cursor cursor = db.query(INVENTORY_TABLE, new String[]{KEY_ID, INVENTORY_CODE,
                        INVENTORY_CATEGORY, INVENTORY_SALE_NUMBER, INVENTORY_NAME, INVENTORY_STANDARD_PRICE,
                        INVENTORY_STOCK_PRICE, INVENTORY_VIP_PRICE, INVENTORY_AVG_PRICE, INVENTORY_LAST_TIME,
                        INVENTORY_PIC_SRC, INVENTORY_QUANTITY, INVENTORY_ID, INVENTORY_NUMBER, INVENTORY_UNIT}, INVENTORYT_SHOP_ID + "=? and " + INVENTORY_CATEGORY + "=?",
                new String[]{shopId, category}, null, null, null, null);

        if (cursor != null && cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                Inventory inventoryItem = new Inventory(
                        cursor.getString(cursor.getColumnIndex(INVENTORY_CODE)),
                        cursor.getString(cursor.getColumnIndex(INVENTORY_CATEGORY)),
                        cursor.getString(cursor.getColumnIndex(INVENTORY_SALE_NUMBER)),
                        cursor.getString(cursor.getColumnIndex(INVENTORY_NAME)),
                        cursor.getString(cursor.getColumnIndex(INVENTORY_STANDARD_PRICE)),
                        cursor.getString(cursor.getColumnIndex(INVENTORY_STOCK_PRICE)),
                        cursor.getString(cursor.getColumnIndex(INVENTORY_VIP_PRICE)),
                        cursor.getString(cursor.getColumnIndex(INVENTORY_AVG_PRICE)),
                        cursor.getString(cursor.getColumnIndex(INVENTORY_LAST_TIME)),
                        cursor.getString(cursor.getColumnIndex(INVENTORY_PIC_SRC)),
                        cursor.getString(cursor.getColumnIndex(INVENTORY_QUANTITY)),
                        cursor.getInt(cursor.getColumnIndex(INVENTORY_ID)),
                        cursor.getString(cursor.getColumnIndex(INVENTORY_NUMBER)),
                        cursor.getString(cursor.getColumnIndex(INVENTORY_UNIT)));
                arrayList.add(inventoryItem);
            }
        }

        if (cursor != null) {
            cursor.close();
        }
        return arrayList;
    }

    public static ArrayList<Inventory> queryInventoryBySearch(String queryString, String shopId){
        DataBaseHelper dataBaseHelper = DataBaseHelper.getInstance();
        if (dataBaseHelper == null) {
            return null;
        }
        SQLiteDatabase db = dataBaseHelper.getReadableDatabase();

        Cursor cursor = db.query(INVENTORY_TABLE,
                new String[]{KEY_ID, INVENTORY_CODE,
                        INVENTORY_CATEGORY, INVENTORY_SALE_NUMBER, INVENTORY_NAME, INVENTORY_STANDARD_PRICE,
                        INVENTORY_STOCK_PRICE, INVENTORY_VIP_PRICE, INVENTORY_AVG_PRICE, INVENTORY_LAST_TIME,
                        INVENTORY_PIC_SRC, INVENTORY_QUANTITY, INVENTORYT_SHOP_ID, INVENTORY_INITIALS, INVENTORY_PINYIN,
                        INVENTORY_ID, INVENTORY_NUMBER, INVENTORY_UNIT},
                        INVENTORYT_SHOP_ID + "=? and (" + INVENTORY_NAME + " LIKE ? or " +  INVENTORY_PINYIN + " LIKE ? or " +
                        INVENTORY_INITIALS + " LIKE ? or " + INVENTORY_NUMBER + " LIKE ? )",
                new String[]{shopId, "%"+ queryString+ "%", "%"+ queryString+ "%", "%"+ queryString+ "%", "%"+ queryString+ "%"},
                null, null, null, null);

        ArrayList<Inventory> arrayList = new ArrayList<>();
        if (cursor != null && cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                String ppp = cursor.getString(cursor.getColumnIndex(INVENTORY_INITIALS));
                String pppiy = cursor.getString(cursor.getColumnIndex(INVENTORY_PINYIN));
                String nanme = cursor.getString(cursor.getColumnIndex(INVENTORY_NAME));

                if (ppp != null){
                }

                if (pppiy != null){
                }

                Inventory inventoryItem = new Inventory(
                        cursor.getString(cursor.getColumnIndex(INVENTORY_CODE)),
                        cursor.getString(cursor.getColumnIndex(INVENTORY_CATEGORY)),
                        cursor.getString(cursor.getColumnIndex(INVENTORY_SALE_NUMBER)),
                        cursor.getString(cursor.getColumnIndex(INVENTORY_NAME)),
                        cursor.getString(cursor.getColumnIndex(INVENTORY_STANDARD_PRICE)),
                        cursor.getString(cursor.getColumnIndex(INVENTORY_STOCK_PRICE)),
                        cursor.getString(cursor.getColumnIndex(INVENTORY_VIP_PRICE)),
                        cursor.getString(cursor.getColumnIndex(INVENTORY_AVG_PRICE)),
                        cursor.getString(cursor.getColumnIndex(INVENTORY_LAST_TIME)),
                        cursor.getString(cursor.getColumnIndex(INVENTORY_PIC_SRC)),
                        cursor.getString(cursor.getColumnIndex(INVENTORY_QUANTITY)),
                        cursor.getInt(cursor.getColumnIndex(INVENTORY_ID)),
                        cursor.getString(cursor.getColumnIndex(INVENTORY_NUMBER)),
                        cursor.getString(cursor.getColumnIndex(INVENTORY_UNIT)));
                int d = cursor.getColumnIndex(INVENTORYT_SHOP_ID);
                String initial = cursor.getString(cursor.getColumnIndex(INVENTORY_INITIALS));
                String pinyin = cursor.getString(cursor.getColumnIndex(INVENTORY_PINYIN));

                String shopiddd = cursor.getString(cursor.getColumnIndex(INVENTORYT_SHOP_ID));
                arrayList.add(inventoryItem);
            }
        }

        if (cursor != null) {
            cursor.close();
        }
        return arrayList;
    }


    // update account token
    public static int updateInventory(Inventory inventory, String shopId){
        DataBaseHelper dataBaseHelper = DataBaseHelper.getInstance();

        SQLiteDatabase db = dataBaseHelper.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(INVENTORY_CODE, inventory.getProductCode());
        values.put(INVENTORY_CATEGORY, inventory.getCategory());
        values.put(INVENTORY_SALE_NUMBER, inventory.getSaleNumber());
        values.put(INVENTORY_NAME, inventory.getProductName());
        values.put(INVENTORY_STANDARD_PRICE, inventory.getStandardPrice());
        values.put(INVENTORY_STOCK_PRICE, inventory.getStockPrice());
        values.put(INVENTORY_VIP_PRICE, inventory.getVipPrice());
        values.put(INVENTORY_AVG_PRICE, inventory.getAvgPrice());
        values.put(INVENTORY_LAST_TIME, inventory.getLastSaleTime());
        values.put(INVENTORY_PIC_SRC, inventory.getPicSrc());
        values.put(INVENTORY_QUANTITY, inventory.getQuantity());
        values.put(INVENTORYT_SHOP_ID, shopId);
        values.put(INVENTORY_ID, inventory.getProductId());
        values.put(INVENTORY_NUMBER, inventory.getProductNumber());
        values.put(INVENTORY_UNIT, inventory.getUnit());

        return db.update(INVENTORY_TABLE, values, INVENTORY_ID + "=? and " + INVENTORYT_SHOP_ID + "=?",
                new String[]{String.valueOf(inventory.getProductId()), shopId});
    }

    public static int removeInventoryItem(String shopId, @Nullable int productId) {
        DataBaseHelper dataBaseHelper = DataBaseHelper.getInstance();
        if (dataBaseHelper == null) {
            return 0;
        }
        SQLiteDatabase db = dataBaseHelper.getWritableDatabase();
        return db.delete(INVENTORY_TABLE, INVENTORYT_SHOP_ID + "=? and " + INVENTORY_ID + "=?",
                new String[]{shopId, String.valueOf(productId)});
    }
}
