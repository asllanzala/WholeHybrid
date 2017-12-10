package com.honeywell.wholesale.framework.database;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;

import com.honeywell.wholesale.framework.model.Cart;
import com.honeywell.wholesale.framework.model.CartItem;

import java.util.ArrayList;

/**
 * Created by H155935 on 16/6/20.
 * Email: xiaofei.he@honeywell.com
 */
public class CartDAO {

    private static final String TAG = "CartDAO";

    private static final String CART_TABLE = "cart";

    private static final String KEY_ID = "id";

    // cart table column
    private static final String LOGIN_NAME = "login_name";
    private static final String USER_NAME = "user_name";
    private static final String SHOP_ID = "shop_id";
    private static final String SHOP_NAME = "shop_name";

    private static final String CUSTOMER_ID = "customer_id";
    private static final String CUSTOMER_NAME = "customer_name";
    private static final String CONTACT_NAME = "contact_name";
    private static final String CONTACT_PHONE = "contact_phone";
    private static final String CUSTOMER_NOTES = "customer_notes";
    private static final String INVOICE_TITLE = "invoice_name";
    private static final String ADDRESS = "address";

    private static final String PRODUCT_ID = "product_id";
    private static final String PRODUCT_CODE = "product_code";
    private static final String PRODUCT_NAME = "product_name";
    private static final String PRODUCT_UNIT_PRICE = "product_unit_price";
    private static final String PRODUCT_NUM = "total_number";
    private static final String PRODUCT_IMAGE_URL = "image_url";
    private static final String PRODUCT_UNIT = "unit_name";
    private static final String PRODUCT_NUMBER = "product_number";
    private static final String STOCK_QUANTITY = "quantity";


    public static final String CREATE_CART_TABLE = "CREATE TABLE IF NOT EXISTS " + CART_TABLE
            + "("
            + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + LOGIN_NAME + " TEXT,"
            + USER_NAME + " TEXT,"
            + SHOP_ID + " TEXT,"
            + SHOP_NAME + " TEXT,"
            + CUSTOMER_ID + " TEXT,"
            + CUSTOMER_NAME + " TEXT,"
            + CONTACT_NAME + " TEXT,"
            + CONTACT_PHONE + " TEXT,"
            + CUSTOMER_NOTES + " TEXT,"
            + INVOICE_TITLE + " TEXT,"
            + ADDRESS + " TEXT,"
            + PRODUCT_ID + " INTEGER,"
            + PRODUCT_CODE + " TEXT,"
            + PRODUCT_NUMBER + " TEXT,"
            + PRODUCT_NAME + " TEXT,"
            + PRODUCT_UNIT_PRICE + " TEXT,"
            + PRODUCT_IMAGE_URL + " TEXT,"
            + PRODUCT_UNIT + " TEXT,"
            + PRODUCT_NUM + " TEXT,"
            + STOCK_QUANTITY + " TEXT"
            + ")";

    public static final String DROP_CART_TABLE = "DROP TABLE IF EXISTS " + CART_TABLE;

    public static void addCartItem(CartItem cartItem) {
        addCartItem(cartItem.getEmployeeId(), cartItem.getUserName(), cartItem.getShopId(), cartItem.getShopName(), cartItem.getCustomerId(), cartItem.getCustomerName(),
                cartItem.getContactName(), cartItem.getContactPhone(), cartItem.getCustomerNotes(), cartItem.getInvoiceTitle(), cartItem.getAddress(),
                cartItem.getProductId(), cartItem.getProductName(), cartItem.getUnitPrice(),
                cartItem.getTotalNumber(), cartItem.getImageUrl(), cartItem.getUnit(), cartItem.getProductCode(), cartItem.getProductNumber(), cartItem.getStockQuantity());
    }

    public static void addCartItem(String userID, String userName, String shopID, String shopName, String customer_id, String customer_name,
            String contactName, String contactPhone, String customerNotes, String invoiceTitle, String address,
            int product_id, String product_name, String product_unit_price,
            @NonNull String total_number, String imageUrl, String unit, String productCode, String productNumber, String stockQuantity) {
        DataBaseHelper dataBaseHelper = DataBaseHelper.getInstance();
        if (dataBaseHelper == null) {
            return;
        }
        SQLiteDatabase db = dataBaseHelper.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(LOGIN_NAME, userID);
        values.put(USER_NAME, userName);
        values.put(SHOP_ID, shopID);
        values.put(SHOP_NAME, shopName);

        values.put(CUSTOMER_ID, customer_id);
        values.put(CUSTOMER_NAME, customer_name);
        values.put(CONTACT_NAME, contactName);
        values.put(CONTACT_PHONE, contactPhone);
        values.put(CUSTOMER_NOTES, customerNotes);
        values.put(INVOICE_TITLE, invoiceTitle);
        values.put(ADDRESS, address);

        values.put(PRODUCT_ID, product_id);
        values.put(PRODUCT_NAME, product_name);
        values.put(PRODUCT_UNIT_PRICE, product_unit_price);
        values.put(PRODUCT_IMAGE_URL, imageUrl);
        values.put(PRODUCT_NUM, total_number);
        values.put(PRODUCT_UNIT, unit);
        values.put(PRODUCT_NUMBER , productNumber);
        values.put(PRODUCT_CODE, productCode);
        values.put(STOCK_QUANTITY, stockQuantity);
        // Inserting Row
        long i = db.insert(CART_TABLE, null, values);
        db.close();
    }

    public static void updateCartItem(CartItem cartItem) {
        updateCartItem(cartItem.getEmployeeId(), cartItem.getUserName(), cartItem.getShopId(), cartItem.getShopName(), cartItem.getCustomerId(), cartItem.getCustomerName(),
                cartItem.getContactName(), cartItem.getContactPhone(), cartItem.getCustomerNotes(), cartItem.getInvoiceTitle(), cartItem.getAddress(),
                cartItem.getProductId(), cartItem.getProductName(), cartItem.getUnitPrice(),
                cartItem.getTotalNumber(), cartItem.getImageUrl(), cartItem.getUnit(), cartItem.getProductCode(), cartItem.getProductNumber(), cartItem.getStockQuantity());
    }

    public static int updateCartItem(String userID, String userName, String shopID, String shopName, String customer_id, String customer_name,
                                     String contactName, String contactPhone, String customerNotes, String invoiceTitle, String address,
                                     int product_id, String product_name, String product_unit_price,
                                     @NonNull String total_number, String imageUrl, String unit, String productCode, String productNumber, String stockQuantity){
        DataBaseHelper dataBaseHelper = DataBaseHelper.getInstance();
        if (dataBaseHelper == null){
            return -1;
        }
        SQLiteDatabase db = dataBaseHelper.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(CONTACT_NAME, contactName);
        values.put(CONTACT_PHONE, contactPhone);
        values.put(CUSTOMER_NOTES, customerNotes);
        values.put(INVOICE_TITLE, invoiceTitle);
        values.put(ADDRESS, address);

        values.put(PRODUCT_ID, product_id);
        values.put(PRODUCT_NAME, product_name);
        values.put(PRODUCT_UNIT_PRICE, product_unit_price);
        values.put(PRODUCT_IMAGE_URL, imageUrl);
        values.put(PRODUCT_NUM, total_number);
        values.put(PRODUCT_UNIT, unit);
        values.put(PRODUCT_CODE, productCode);
        values.put(PRODUCT_NUMBER, productNumber);
        values.put(STOCK_QUANTITY, stockQuantity);

        return db.update(CART_TABLE, values,
                LOGIN_NAME + "=? and " + SHOP_ID + "=? and " + CUSTOMER_ID + "=?",
                new String[]{userID, shopID, customer_id});
    }

    public static int updateCartItem(String userID, String shopID, String customer_id, int product_id){
        DataBaseHelper dataBaseHelper = DataBaseHelper.getInstance();
        if (dataBaseHelper == null){
            return -1;
        }
        SQLiteDatabase db = dataBaseHelper.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(PRODUCT_ID,0);
        values.putNull(PRODUCT_NAME);
        values.putNull(PRODUCT_UNIT_PRICE);
        values.putNull(PRODUCT_IMAGE_URL);
        values.putNull(PRODUCT_NUM);
        values.putNull(PRODUCT_UNIT);
        values.putNull(STOCK_QUANTITY);

        String productIdStr = String.valueOf(product_id);
        return db.update(CART_TABLE, values,
                LOGIN_NAME + "=? and " + SHOP_ID + "=? and " + CUSTOMER_ID + "=? and " + PRODUCT_ID + "=?",
                new String[]{userID, shopID, customer_id, productIdStr});
    }

    public static int updateCartItemCount(CartItem cartItem){
        DataBaseHelper dataBaseHelper = DataBaseHelper.getInstance();
        if (dataBaseHelper == null){
            return -1;
        }
        SQLiteDatabase db = dataBaseHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(PRODUCT_NUM, cartItem.getTotalNumber());

        return db.update(CART_TABLE, values,
                LOGIN_NAME + "=? and " + SHOP_ID + "=? and " + CUSTOMER_ID + "=? and " + PRODUCT_UNIT_PRICE + "=?",
                new String[]{cartItem.getEmployeeId(), cartItem.getShopId(), cartItem.getCustomerId(), cartItem.getUnitPrice()});
    }

    /**
     *
     * @param oldCustomerID 0表示散客
     */
    public static int updateCustomerInfo(String shopId, String employeeID, String oldCustomerID, String newCustomerId, String customerName,
            String contactName, String contactPhone, String customerNotes, String invoiceTitle, String address) {
            DataBaseHelper dataBaseHelper = DataBaseHelper.getInstance();
            if (dataBaseHelper == null){
                return -1;
            }
            SQLiteDatabase db = dataBaseHelper.getWritableDatabase();

            ContentValues values = new ContentValues();
            values.put(CUSTOMER_ID, newCustomerId);
            values.put(CUSTOMER_NAME, customerName);
            values.put(CONTACT_NAME, contactName);
            values.put(CONTACT_PHONE, contactPhone);
            values.put(CUSTOMER_NOTES, customerNotes);
            values.put(INVOICE_TITLE, invoiceTitle);
            values.put(ADDRESS, address);

            return db.update(CART_TABLE, values, LOGIN_NAME + "=? and " + SHOP_ID + "=? and " + CUSTOMER_ID + "=?",
                    new String[]{employeeID, shopId, oldCustomerID});
        }

    /**
     * 返回结果需要根据customer ID拍好序。
     * @return
     */
    public static int getCartItemsCount(String shopId, String customer_id, String employeeID) {
        DataBaseHelper dataBaseHelper = DataBaseHelper.getInstance();
        if (dataBaseHelper == null) {
            return 0;
        }

        Cursor cursor = null;
        //得到操作数据库的实例
        try {
            SQLiteDatabase db = dataBaseHelper.getReadableDatabase();
            // 调用查找书库代码并返回数据源
            cursor = db.rawQuery("select count(*) from " +
                    CART_TABLE + " where " + LOGIN_NAME + " = '" +
                    employeeID + "' AND " + SHOP_ID + " = '" + shopId + "' AND " + CUSTOMER_ID + " = '" + customer_id + "'", null);
            //游标移到第一条记录准备获取数据
            cursor.moveToFirst();
            // 获取数据中的LONG类型数据
            return cursor.getInt(0);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        return 0;
    }

    /**
     * 返回结果需要根据customer ID拍好序。
     * @return
     */
    public static Long getCartItemsCount(String shopId, String employeeID) {
        DataBaseHelper dataBaseHelper = DataBaseHelper.getInstance();
        if (dataBaseHelper == null) {
            return 0L;
        }

        Cursor cursor = null;
        //得到操作数据库的实例
        try {
            SQLiteDatabase db = dataBaseHelper.getReadableDatabase();
            // 调用查找书库代码并返回数据源
            cursor = db.rawQuery("select count(*) from " +
                    CART_TABLE + " where " + LOGIN_NAME + " = '" + employeeID + "' AND " + SHOP_ID + " = " + shopId, null);
            //游标移到第一条记录准备获取数据
            cursor.moveToFirst();
            // 获取数据中的LONG类型数据
            return cursor.getLong(0);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        return 0L;
    }

    /**
     * 查询以存在的购物车记录
     *
     */
    public static ArrayList<CartItem> getCartItems(String customerID, String employeeID, int productId){
        ArrayList<CartItem> arrayList = new ArrayList<>();
        DataBaseHelper dataBaseHelper = DataBaseHelper.getInstance();
        if (dataBaseHelper == null) {
            return arrayList;
        }

        String productIdStr = String.valueOf(productId);

        SQLiteDatabase db = dataBaseHelper.getReadableDatabase();
        Cursor cursor = db.query(CART_TABLE, new String[]{KEY_ID, LOGIN_NAME, USER_NAME, SHOP_ID, SHOP_NAME, CUSTOMER_ID, CUSTOMER_NAME,
                        CONTACT_NAME, CONTACT_PHONE, CUSTOMER_NOTES, INVOICE_TITLE, ADDRESS,
                        PRODUCT_ID, PRODUCT_NAME, PRODUCT_UNIT_PRICE, PRODUCT_NUM,
                        PRODUCT_IMAGE_URL, PRODUCT_UNIT, PRODUCT_CODE, PRODUCT_NUMBER, STOCK_QUANTITY}, LOGIN_NAME + "=? and " + CUSTOMER_ID + "=? and " + PRODUCT_ID + "=?",
                new String[]{employeeID, customerID, productIdStr}, null, null, CUSTOMER_ID, null);

        if (cursor != null && cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                CartItem cartItem = new CartItem(
                        cursor.getString(cursor.getColumnIndex(LOGIN_NAME)),
                        cursor.getString(cursor.getColumnIndex(USER_NAME)),
                        cursor.getString(cursor.getColumnIndex(SHOP_ID)),
                        cursor.getString(cursor.getColumnIndex(SHOP_NAME)),

                        cursor.getString(cursor.getColumnIndex(CUSTOMER_ID)),
                        cursor.getString(cursor.getColumnIndex(CUSTOMER_NAME)),
                        cursor.getString(cursor.getColumnIndex(CONTACT_NAME)),
                        cursor.getString(cursor.getColumnIndex(CONTACT_PHONE)),
                        cursor.getString(cursor.getColumnIndex(CUSTOMER_NOTES)),
                        cursor.getString(cursor.getColumnIndex(INVOICE_TITLE)),
                        cursor.getString(cursor.getColumnIndex(ADDRESS)),

                        cursor.getInt(cursor.getColumnIndex(PRODUCT_ID)),
                        cursor.getString(cursor.getColumnIndex(PRODUCT_NAME)),
                        cursor.getString(cursor.getColumnIndex(PRODUCT_UNIT_PRICE)),
                        cursor.getString(cursor.getColumnIndex(PRODUCT_NUM)),
                        cursor.getString(cursor.getColumnIndex(PRODUCT_IMAGE_URL)),
                        cursor.getString(cursor.getColumnIndex(PRODUCT_UNIT)),
                        cursor.getString(cursor.getColumnIndex(PRODUCT_CODE)),
                        cursor.getString(cursor.getColumnIndex(PRODUCT_NUMBER)),
                        cursor.getString(cursor.getColumnIndex(STOCK_QUANTITY)));
                arrayList.add(cartItem);
            }
        }
        return arrayList;
    }

    public static ArrayList<CartItem> getAllCartItemsByCustomer(String employeeId, String shopId, String customerId){
        DataBaseHelper dataBaseHelper = DataBaseHelper.getInstance();
        if (dataBaseHelper == null) {
            return null;
        }
        SQLiteDatabase db = dataBaseHelper.getReadableDatabase();

        Cursor cursor = db.query(CART_TABLE, new String[]{KEY_ID, LOGIN_NAME, USER_NAME, SHOP_ID, SHOP_NAME, CUSTOMER_ID, CUSTOMER_NAME,
                        CONTACT_NAME, CONTACT_PHONE, CUSTOMER_NOTES, INVOICE_TITLE, ADDRESS,
                        PRODUCT_ID, PRODUCT_NAME, PRODUCT_UNIT_PRICE, PRODUCT_NUM,
                        PRODUCT_IMAGE_URL, PRODUCT_UNIT, PRODUCT_CODE, PRODUCT_NUMBER, STOCK_QUANTITY}, LOGIN_NAME + "=? AND " + SHOP_ID + "=? AND " + CUSTOMER_ID + "=?",
                new String[]{employeeId, shopId, customerId}, null, null, null, null);

        ArrayList<CartItem> arrayList = new ArrayList<>();
        if (cursor != null && cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                CartItem cartItem = new CartItem(
                        cursor.getString(cursor.getColumnIndex(LOGIN_NAME)),
                        cursor.getString(cursor.getColumnIndex(USER_NAME)),
                        cursor.getString(cursor.getColumnIndex(SHOP_ID)),
                        cursor.getString(cursor.getColumnIndex(SHOP_NAME)),

                        cursor.getString(cursor.getColumnIndex(CUSTOMER_ID)),
                        cursor.getString(cursor.getColumnIndex(CUSTOMER_NAME)),
                        cursor.getString(cursor.getColumnIndex(CONTACT_NAME)),
                        cursor.getString(cursor.getColumnIndex(CONTACT_PHONE)),
                        cursor.getString(cursor.getColumnIndex(CUSTOMER_NOTES)),
                        cursor.getString(cursor.getColumnIndex(INVOICE_TITLE)),
                        cursor.getString(cursor.getColumnIndex(ADDRESS)),

                        cursor.getInt(cursor.getColumnIndex(PRODUCT_ID)),
                        cursor.getString(cursor.getColumnIndex(PRODUCT_NAME)),
                        cursor.getString(cursor.getColumnIndex(PRODUCT_UNIT_PRICE)),
                        cursor.getString(cursor.getColumnIndex(PRODUCT_NUM)),
                        cursor.getString(cursor.getColumnIndex(PRODUCT_IMAGE_URL)),
                        cursor.getString(cursor.getColumnIndex(PRODUCT_UNIT)),
                        cursor.getString(cursor.getColumnIndex(PRODUCT_CODE)),
                        cursor.getString(cursor.getColumnIndex(PRODUCT_NUMBER)),
                        cursor.getString(cursor.getColumnIndex(STOCK_QUANTITY)));
                arrayList.add(cartItem);
            }
        }

        if (cursor != null) {
            cursor.close();
        }
        return arrayList;
    }

    /**
     * 返回结果需要根据customer ID拍好序。
     * @return
     */
    public static ArrayList<CartItem> getAllCartItems(String shopId, String employeeID) {
        DataBaseHelper dataBaseHelper = DataBaseHelper.getInstance();
        if (dataBaseHelper == null) {
            return null;
        }
        SQLiteDatabase db = dataBaseHelper.getReadableDatabase();

        Cursor cursor = db.query(CART_TABLE, new String[]{KEY_ID, LOGIN_NAME, USER_NAME, SHOP_ID, SHOP_NAME, CUSTOMER_ID, CUSTOMER_NAME,
                        CONTACT_NAME, CONTACT_PHONE, CUSTOMER_NOTES, INVOICE_TITLE, ADDRESS,
                        PRODUCT_ID, PRODUCT_NAME, PRODUCT_UNIT_PRICE, PRODUCT_NUM,
                        PRODUCT_IMAGE_URL, PRODUCT_UNIT, PRODUCT_CODE, PRODUCT_NUMBER, STOCK_QUANTITY}, LOGIN_NAME + "=? AND " + SHOP_ID + "=?",
                new String[]{employeeID, shopId}, null, null, CUSTOMER_ID, null);

        ArrayList<CartItem> arrayList = new ArrayList<>();
        if (cursor != null && cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                CartItem cartItem = new CartItem(
                        cursor.getString(cursor.getColumnIndex(LOGIN_NAME)),
                        cursor.getString(cursor.getColumnIndex(USER_NAME)),
                        cursor.getString(cursor.getColumnIndex(SHOP_ID)),
                        cursor.getString(cursor.getColumnIndex(SHOP_NAME)),

                        cursor.getString(cursor.getColumnIndex(CUSTOMER_ID)),
                        cursor.getString(cursor.getColumnIndex(CUSTOMER_NAME)),
                        cursor.getString(cursor.getColumnIndex(CONTACT_NAME)),
                        cursor.getString(cursor.getColumnIndex(CONTACT_PHONE)),
                        cursor.getString(cursor.getColumnIndex(CUSTOMER_NOTES)),
                        cursor.getString(cursor.getColumnIndex(INVOICE_TITLE)),
                        cursor.getString(cursor.getColumnIndex(ADDRESS)),

                        cursor.getInt(cursor.getColumnIndex(PRODUCT_ID)),
                        cursor.getString(cursor.getColumnIndex(PRODUCT_NAME)),
                        cursor.getString(cursor.getColumnIndex(PRODUCT_UNIT_PRICE)),
                        cursor.getString(cursor.getColumnIndex(PRODUCT_NUM)),
                        cursor.getString(cursor.getColumnIndex(PRODUCT_IMAGE_URL)),
                        cursor.getString(cursor.getColumnIndex(PRODUCT_UNIT)),
                        cursor.getString(cursor.getColumnIndex(PRODUCT_CODE)),
                        cursor.getString(cursor.getColumnIndex(PRODUCT_NUMBER)),
                        cursor.getString(cursor.getColumnIndex(STOCK_QUANTITY)));
                arrayList.add(cartItem);
            }
        }

        if (cursor != null) {
            cursor.close();
        }
        return arrayList;
    }

    /**
     * 返回结果需要根据customer ID拍好序。
     * @return
     */
    public static ArrayList<CartItem> getAllCartItems() {
        DataBaseHelper dataBaseHelper = DataBaseHelper.getInstance();
        if (dataBaseHelper == null) {
            return null;
        }
        SQLiteDatabase db = dataBaseHelper.getReadableDatabase();

        Cursor cursor = db.query(CART_TABLE, new String[]{KEY_ID, LOGIN_NAME, USER_NAME, SHOP_ID, SHOP_NAME, CUSTOMER_ID, CUSTOMER_NAME,
                        CONTACT_NAME, CONTACT_PHONE, CUSTOMER_NOTES, INVOICE_TITLE, ADDRESS,
                        PRODUCT_ID, PRODUCT_NAME, PRODUCT_UNIT_PRICE, PRODUCT_NUM,
                        PRODUCT_IMAGE_URL, PRODUCT_UNIT, PRODUCT_CODE, PRODUCT_NUMBER, STOCK_QUANTITY}, null,
                null, null, null, CUSTOMER_ID, null);

        ArrayList<CartItem> arrayList = new ArrayList<>();
        if (cursor != null && cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                CartItem cartItem = new CartItem(
                        cursor.getString(cursor.getColumnIndex(LOGIN_NAME)),
                        cursor.getString(cursor.getColumnIndex(USER_NAME)),
                        cursor.getString(cursor.getColumnIndex(SHOP_ID)),
                        cursor.getString(cursor.getColumnIndex(SHOP_NAME)),

                        cursor.getString(cursor.getColumnIndex(CUSTOMER_ID)),
                        cursor.getString(cursor.getColumnIndex(CUSTOMER_NAME)),
                        cursor.getString(cursor.getColumnIndex(CONTACT_NAME)),
                        cursor.getString(cursor.getColumnIndex(CONTACT_PHONE)),
                        cursor.getString(cursor.getColumnIndex(CUSTOMER_NOTES)),
                        cursor.getString(cursor.getColumnIndex(INVOICE_TITLE)),
                        cursor.getString(cursor.getColumnIndex(ADDRESS)),

                        cursor.getInt(cursor.getColumnIndex(PRODUCT_ID)),
                        cursor.getString(cursor.getColumnIndex(PRODUCT_NAME)),
                        cursor.getString(cursor.getColumnIndex(PRODUCT_UNIT_PRICE)),
                        cursor.getString(cursor.getColumnIndex(PRODUCT_NUM)),
                        cursor.getString(cursor.getColumnIndex(PRODUCT_IMAGE_URL)),
                        cursor.getString(cursor.getColumnIndex(PRODUCT_UNIT)),
                        cursor.getString(cursor.getColumnIndex(PRODUCT_CODE)),
                        cursor.getString(cursor.getColumnIndex(PRODUCT_NUMBER)),
                        cursor.getString(cursor.getColumnIndex(STOCK_QUANTITY)));
                arrayList.add(cartItem);
            }
        }

        if (cursor != null) {
            cursor.close();
        }
        return arrayList;
    }

    /**
     * @return the number of rows affected if a whereClause is passed in, 0
     * otherwise. To remove all rows and get a count pass "1" as the
     * whereClause.
     */
    public static int removeCartItem(String employeeId, String customId, int productId) {
        DataBaseHelper dataBaseHelper = DataBaseHelper.getInstance();
        if (dataBaseHelper == null) {
            return 0;
        }
        String productIdStr = String.valueOf(productId);
        SQLiteDatabase db = dataBaseHelper.getWritableDatabase();
        return db.delete(CART_TABLE, LOGIN_NAME + "=? and " + CUSTOMER_ID + "=? and " + PRODUCT_ID + "=?",
                new String[]{employeeId, customId, productIdStr});
    }

    /**
     * @return the number of rows affected if a whereClause is passed in, 0
     * otherwise. To remove all rows and get a count pass "1" as the
     * whereClause.
     */
    public static int removeCart(String employeeId, String customId) {
        DataBaseHelper dataBaseHelper = DataBaseHelper.getInstance();
        if (dataBaseHelper == null) {
            return 0;
        }
        SQLiteDatabase db = dataBaseHelper.getWritableDatabase();
        return db.delete(CART_TABLE, CUSTOMER_ID + "=? and " + LOGIN_NAME + "=?", new String[]{customId, employeeId});
    }

    /**
     *
     * @return the number of rows affected if a whereClause is passed in, 0
     *         otherwise. To remove all rows and get a count pass "1" as the
     *         whereClause.
     */
    public static int removeAllCart(){
        DataBaseHelper dataBaseHelper = DataBaseHelper.getInstance();
        if (dataBaseHelper == null){
            return 0;
        }
        SQLiteDatabase db = dataBaseHelper.getWritableDatabase();
        return db.delete(CART_TABLE, null, null);
    }
}
