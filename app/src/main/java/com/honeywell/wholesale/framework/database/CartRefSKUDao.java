package com.honeywell.wholesale.framework.database;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.honeywell.wholesale.framework.model.CartRefSKU;

import java.util.ArrayList;


/**
 * Created by xiaofei on 3/20/17.
 *
 */

public class CartRefSKUDao {
    private static final String TAG = "CartRefSKUDao";

    private static final String CART_REF_SKU_TABLE = "cart_ref_sku";

    private static final String KEY_ID = "id";
    private static final String CART_UUID = "uuid";

    // Product
    private static final String PRODUCT_ID = "product_id";
    private static final String PRODUCT_CODE = "product_code";
    private static final String PRODUCT_NUMBER = "product_number";

    private static final String PRODUCT_NAME = "product_name";
    private static final String PRODUCT_UNIT_PRICE = "product_unit_price";
    private static final String PRODUCT_NUM = "total_number";
    private static final String PRODUCT_IMAGE_URL = "image_url";
    private static final String PRODUCT_UNIT = "unit_name";
    private static final String QUANTITY = "quantity";

    // SKU
    private static final String SKU_JSON_STR = "sku_json";

    public static final String CREATE_CART_REF_CUSTOMER_TABLE = "CREATE TABLE IF NOT EXISTS " + CART_REF_SKU_TABLE
            + "("
            + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + CART_UUID + " TEXT,"
            + PRODUCT_ID + " TEXT,"
            + PRODUCT_CODE + " TEXT,"
            + PRODUCT_NUMBER + " TEXT,"
            + PRODUCT_NAME + " TEXT,"
            + PRODUCT_UNIT_PRICE + " TEXT,"
            + PRODUCT_NUM + " TEXT,"
            + PRODUCT_IMAGE_URL + " TEXT,"
            + PRODUCT_UNIT + " TEXT,"
            + QUANTITY + " TEXT,"
            + SKU_JSON_STR + " TEXT"
            + ")";

    public static final String DROP_CART_TABLE = "DROP TABLE IF EXISTS " + CART_REF_SKU_TABLE;

    public static void addCartRefSKU(CartRefSKU cartRefSKU){
        addCartRefCustomerItem(cartRefSKU.getCartUuid(), cartRefSKU.getProductId(), cartRefSKU.getProductCode(),
                cartRefSKU.getProductNumber(), cartRefSKU.getProductName(), cartRefSKU.getProductUnitPrice(),
                cartRefSKU.getTotalNumber(), cartRefSKU.getImageUrl(), cartRefSKU.getUnitName(),
                cartRefSKU.getQuantity(), cartRefSKU.getSkuJson());
    }

    public static void addCartRefCustomerItem(String uuid, String productId, String productCode, String productNumber,
                                              String productName, String productUnitPrice, String totalNumber,
                                              String imageUrl, String unitName, String quantity, String skuJson){

        DataBaseHelper dataBaseHelper = DataBaseHelper.getInstance();
        if (dataBaseHelper == null) {
            return;
        }
        SQLiteDatabase db = dataBaseHelper.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(CART_UUID, uuid);
        values.put(PRODUCT_ID, productId);
        values.put(PRODUCT_CODE, productCode);
        values.put(PRODUCT_NUMBER, productNumber);
        values.put(PRODUCT_NAME, productName);

        values.put(PRODUCT_UNIT_PRICE, productUnitPrice);
        values.put(PRODUCT_NUM, totalNumber);
        values.put(PRODUCT_IMAGE_URL, imageUrl);
        values.put(PRODUCT_UNIT, unitName);
        values.put(QUANTITY, quantity);
        values.put(SKU_JSON_STR, skuJson);

        // Inserting Row
        long i = db.insert(CART_REF_SKU_TABLE, null, values);
        db.close();
    }

    public static int updateCartRefSKUItem(CartRefSKU cartRefSKU){
        DataBaseHelper dataBaseHelper = DataBaseHelper.getInstance();
        if (dataBaseHelper == null) {
            return -1;
        }
        SQLiteDatabase db = dataBaseHelper.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(PRODUCT_ID, cartRefSKU.getProductId());
        values.put(PRODUCT_CODE, cartRefSKU.getProductCode());
        values.put(PRODUCT_NUMBER, cartRefSKU.getProductNumber());

        values.put(PRODUCT_UNIT_PRICE, cartRefSKU.getProductUnitPrice());
        values.put(PRODUCT_NUM, cartRefSKU.getProductNumber());
        values.put(PRODUCT_IMAGE_URL, cartRefSKU.getImageUrl());
        values.put(PRODUCT_UNIT, cartRefSKU.getUnitName());
        values.put(QUANTITY, cartRefSKU.getQuantity());
        values.put(SKU_JSON_STR, cartRefSKU.getSkuJson());

        return db.update(CART_REF_SKU_TABLE, values,
                CART_UUID + "=? and " + PRODUCT_ID + "=?",
                new String[]{cartRefSKU.getCartUuid(), cartRefSKU.getProductId()});
    }

    public static ArrayList<CartRefSKU> queryCartRefSKU(String cartUuid){
        ArrayList<CartRefSKU> arrayList = new ArrayList<>();
        DataBaseHelper dataBaseHelper = DataBaseHelper.getInstance();
        if (dataBaseHelper == null) {
            return arrayList;
        }

        SQLiteDatabase db = dataBaseHelper.getReadableDatabase();
        Cursor cursor = db.query(CART_REF_SKU_TABLE, new String[]{KEY_ID, CART_UUID, PRODUCT_ID, PRODUCT_CODE, PRODUCT_NUMBER,
                        PRODUCT_NAME, PRODUCT_UNIT_PRICE, PRODUCT_NUM, PRODUCT_IMAGE_URL, PRODUCT_UNIT, QUANTITY,
                        SKU_JSON_STR}, CART_UUID + "=?",
                new String[]{cartUuid}, null, null, null, null);


        if (cursor != null && cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                CartRefSKU cartRefSKU = new CartRefSKU(
                        cursor.getString(cursor.getColumnIndex(CART_UUID)),
                        cursor.getString(cursor.getColumnIndex(PRODUCT_ID)),
                        cursor.getString(cursor.getColumnIndex(PRODUCT_CODE)),
                        cursor.getString(cursor.getColumnIndex(PRODUCT_NUMBER)),

                        cursor.getString(cursor.getColumnIndex(PRODUCT_NAME)),
                        cursor.getString(cursor.getColumnIndex(PRODUCT_UNIT_PRICE)),
                        cursor.getString(cursor.getColumnIndex(PRODUCT_NUM)),
                        cursor.getString(cursor.getColumnIndex(PRODUCT_IMAGE_URL)),
                        cursor.getString(cursor.getColumnIndex(PRODUCT_UNIT)),
                        cursor.getString(cursor.getColumnIndex(QUANTITY)),
                        cursor.getString(cursor.getColumnIndex(SKU_JSON_STR)));
                arrayList.add(cartRefSKU);
            }
        }
        return arrayList;
    }


    public static ArrayList<CartRefSKU> queryCartRefSKUByProductId(String productId){
        ArrayList<CartRefSKU> arrayList = new ArrayList<>();
        DataBaseHelper dataBaseHelper = DataBaseHelper.getInstance();
        if (dataBaseHelper == null) {
            return arrayList;
        }

        SQLiteDatabase db = dataBaseHelper.getReadableDatabase();
        Cursor cursor = db.query(CART_REF_SKU_TABLE, new String[]{KEY_ID, CART_UUID, PRODUCT_ID, PRODUCT_CODE, PRODUCT_NUMBER,
                        PRODUCT_NAME, PRODUCT_UNIT_PRICE, PRODUCT_NUM, PRODUCT_IMAGE_URL, PRODUCT_UNIT, QUANTITY,
                        SKU_JSON_STR}, PRODUCT_ID + "=?",
                new String[]{productId}, null, null, productId, null);


        if (cursor != null && cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                CartRefSKU cartRefSKU = new CartRefSKU(
                        cursor.getString(cursor.getColumnIndex(CART_UUID)),
                        cursor.getString(cursor.getColumnIndex(PRODUCT_ID)),
                        cursor.getString(cursor.getColumnIndex(PRODUCT_CODE)),
                        cursor.getString(cursor.getColumnIndex(PRODUCT_NUMBER)),

                        cursor.getString(cursor.getColumnIndex(PRODUCT_NAME)),
                        cursor.getString(cursor.getColumnIndex(PRODUCT_UNIT_PRICE)),
                        cursor.getString(cursor.getColumnIndex(PRODUCT_NUM)),
                        cursor.getString(cursor.getColumnIndex(PRODUCT_IMAGE_URL)),
                        cursor.getString(cursor.getColumnIndex(PRODUCT_UNIT)),
                        cursor.getString(cursor.getColumnIndex(QUANTITY)),
                        cursor.getString(cursor.getColumnIndex(SKU_JSON_STR)));
                arrayList.add(cartRefSKU);
            }
        }
        return arrayList;
    }

    public static ArrayList<CartRefSKU> queryCartRefSKU(String uuid, String productId){
        ArrayList<CartRefSKU> arrayList = new ArrayList<>();
        DataBaseHelper dataBaseHelper = DataBaseHelper.getInstance();
        if (dataBaseHelper == null) {
            return arrayList;
        }

        SQLiteDatabase db = dataBaseHelper.getReadableDatabase();
        Cursor cursor = db.query(CART_REF_SKU_TABLE, new String[]{KEY_ID, CART_UUID, PRODUCT_ID, PRODUCT_CODE, PRODUCT_NUMBER,
                        PRODUCT_NAME, PRODUCT_UNIT_PRICE, PRODUCT_NUM, PRODUCT_IMAGE_URL, PRODUCT_UNIT, QUANTITY,
                        SKU_JSON_STR}, PRODUCT_ID + "=? and " + CART_UUID + "=?",
                new String[]{productId, uuid}, null, null, productId, null);


        if (cursor != null && cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                CartRefSKU cartRefSKU = new CartRefSKU(
                        cursor.getString(cursor.getColumnIndex(CART_UUID)),
                        cursor.getString(cursor.getColumnIndex(PRODUCT_ID)),
                        cursor.getString(cursor.getColumnIndex(PRODUCT_CODE)),
                        cursor.getString(cursor.getColumnIndex(PRODUCT_NUMBER)),

                        cursor.getString(cursor.getColumnIndex(PRODUCT_NAME)),
                        cursor.getString(cursor.getColumnIndex(PRODUCT_UNIT_PRICE)),
                        cursor.getString(cursor.getColumnIndex(PRODUCT_NUM)),
                        cursor.getString(cursor.getColumnIndex(PRODUCT_IMAGE_URL)),
                        cursor.getString(cursor.getColumnIndex(PRODUCT_UNIT)),
                        cursor.getString(cursor.getColumnIndex(QUANTITY)),
                        cursor.getString(cursor.getColumnIndex(SKU_JSON_STR)));
                arrayList.add(cartRefSKU);
            }
        }
        return arrayList;
    }

    public static int removeCartRefSKUItem(String cartUuid){
        DataBaseHelper dataBaseHelper = DataBaseHelper.getInstance();
        if (dataBaseHelper == null) {
            return 0;
        }
        SQLiteDatabase db = dataBaseHelper.getWritableDatabase();

        return db.delete(CART_REF_SKU_TABLE, CART_UUID + "=?", new String[]{cartUuid});
    }
}
