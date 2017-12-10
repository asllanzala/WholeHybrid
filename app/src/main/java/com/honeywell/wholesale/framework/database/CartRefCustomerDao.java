package com.honeywell.wholesale.framework.database;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.honeywell.wholesale.framework.model.CartItem;
import com.honeywell.wholesale.framework.model.CartRefCustomer;

import java.util.ArrayList;
import java.util.UUID;

/**
 * Created by xiaofei on 3/20/17.
 *
 */

public class CartRefCustomerDao {

    private static final String TAG = "CartRefCustomerDao";

    private static final String CART_REF_CUSTOMER_TABLE = "cart_ref_customer";

    private static final String KEY_ID = "id";

    private static final String CART_UUID = "uuid";

    // cart table column
    private static final String EMPLOYEE_ID = "employee_id";
    private static final String USER_NAME = "user_name";
    private static final String SHOP_ID = "shop_id";
    private static final String SHOP_NAME = "shop_name";

    private static final String CUSTOMER_ID = "customer_id";
    private static final String CUSTOMER_NAME = "customer_name";
    private static final String CONTACT_NAME = "contact_name";
    private static final String CONTACT_PHONE = "contact_phone";
    private static final String CUSTOMER_NOTES = "customer_notes";
    private static final String INVOICE_NAME = "invoice_name";
    private static final String ADDRESS = "address";
    private static final String WAREHOUSE_ID = "warehouse_id";
    private static final String WAREHOUSE_NAME = "warehouse_name";

    public static final String CREATE_CART_TABLE = "CREATE TABLE IF NOT EXISTS " + CART_REF_CUSTOMER_TABLE
            + "("
            + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + CART_UUID + " TEXT,"
            + EMPLOYEE_ID + " TEXT,"
            + USER_NAME + " TEXT,"
            + SHOP_ID + " TEXT,"
            + SHOP_NAME + " TEXT,"
            + CUSTOMER_ID + " TEXT,"
            + CUSTOMER_NAME + " TEXT,"
            + CONTACT_NAME + " TEXT,"
            + CONTACT_PHONE + " TEXT,"
            + CUSTOMER_NOTES + " TEXT,"
            + INVOICE_NAME + " TEXT,"
            + ADDRESS + " TEXT,"
            + WAREHOUSE_ID + " TEXT,"
            + WAREHOUSE_NAME + " TEXT"
            + ")";

    public static final String DROP_CART_TABLE = "DROP TABLE IF EXISTS " + CART_REF_CUSTOMER_TABLE;

    public static String addCartRefCustomerItem(CartRefCustomer cartRefCustomer) {
        return addCartRefCustomerItem(cartRefCustomer.getEmployeeId(), cartRefCustomer.getUserName(), cartRefCustomer.getShopId(),
                cartRefCustomer.getShopName(), cartRefCustomer.getCustomerId(), cartRefCustomer.getCustomerName(),
                cartRefCustomer.getContactName(), cartRefCustomer.getContactPhone(), cartRefCustomer.getCustomerNotes(),
                cartRefCustomer.getInvoiceName(), cartRefCustomer.getAddress(), cartRefCustomer.getWarehouseId(), cartRefCustomer.getWarehouseName());
    }

    public static String addCartRefCustomerItem(String employeeId, String userName, String shopId, String shopName,
                                              String customerId, String customerName, String contactName, String contactPhone,
                                              String customerNotes, String invoiceName, String address, String warehouseId, String warehouseName){
        DataBaseHelper dataBaseHelper = DataBaseHelper.getInstance();
        if (dataBaseHelper == null) {
            return "";
        }
        SQLiteDatabase db = dataBaseHelper.getWritableDatabase();
        ContentValues values = new ContentValues();

        String uuid = UUID.randomUUID().toString();

        values.put(CART_UUID, uuid);
        values.put(EMPLOYEE_ID, employeeId);
        values.put(USER_NAME, userName);
        values.put(SHOP_ID, shopId);
        values.put(SHOP_NAME, shopName);

        values.put(CUSTOMER_ID, customerId);
        values.put(CUSTOMER_NAME, customerName);
        values.put(CONTACT_NAME, contactName);
        values.put(CONTACT_PHONE, contactPhone);
        values.put(CUSTOMER_NOTES, customerNotes);
        values.put(INVOICE_NAME, invoiceName);
        values.put(ADDRESS, address);
        values.put(WAREHOUSE_ID, warehouseId);
        values.put(WAREHOUSE_NAME, warehouseName);

        // Inserting Row
        long i = db.insert(CART_REF_CUSTOMER_TABLE, null, values);
        db.close();

        return uuid;
    }

    public static int updateCartRefCustomerItem(CartRefCustomer cartRefCustomer){
        DataBaseHelper dataBaseHelper = DataBaseHelper.getInstance();
        if (dataBaseHelper == null) {
            return -1;
        }
        SQLiteDatabase db = dataBaseHelper.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(CUSTOMER_NAME, cartRefCustomer.getCustomerName());
        values.put(CONTACT_NAME, cartRefCustomer.getContactName());
        values.put(SHOP_NAME, cartRefCustomer.getShopName());
        values.put(USER_NAME, cartRefCustomer.getUserName());
        values.put(CONTACT_PHONE, cartRefCustomer.getContactPhone());
        values.put(CUSTOMER_NOTES, cartRefCustomer.getCustomerNotes());
        values.put(INVOICE_NAME, cartRefCustomer.getInvoiceName());
        values.put(ADDRESS, cartRefCustomer.getAddress());
        values.put(WAREHOUSE_ID, cartRefCustomer.getWarehouseId());
        values.put(WAREHOUSE_NAME, cartRefCustomer.getWarehouseName());

        return db.update(CART_REF_CUSTOMER_TABLE, values,
                EMPLOYEE_ID + "=? and " + SHOP_ID + "=? and " + CUSTOMER_ID + "=?",
                new String[]{cartRefCustomer.getEmployeeId(), cartRefCustomer.getShopId(), cartRefCustomer.getCustomerId()
                });
    }

    public static int updateCartRefCustomerItem(String employeeId, String employeeName, String shopID, String shopName,
                                     String customer_id, String customer_name, String contactName,
                                     String contactPhone, String customerNotes, String invoiceName, String address, String warehouseId, String warehouseName) {
        DataBaseHelper dataBaseHelper = DataBaseHelper.getInstance();
        if (dataBaseHelper == null) {
            return -1;
        }
        SQLiteDatabase db = dataBaseHelper.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(CUSTOMER_NAME, customer_name);
        values.put(CONTACT_NAME, contactName);
        values.put(SHOP_NAME, shopName);
        values.put(USER_NAME, employeeName);
        values.put(CONTACT_PHONE, contactPhone);
        values.put(CUSTOMER_NOTES, customerNotes);
        values.put(INVOICE_NAME, invoiceName);
        values.put(ADDRESS, address);
        values.put(WAREHOUSE_ID, warehouseId);
        values.put(WAREHOUSE_NAME, warehouseName);


        return db.update(CART_REF_CUSTOMER_TABLE, values,
                EMPLOYEE_ID + "=? and " + SHOP_ID + "=? and " + CUSTOMER_ID + "=?",
                new String[]{employeeId, shopID, customer_id});
    }


    public static ArrayList<CartRefCustomer> queryCartRefCustomerItems(String customerID, String employeeID){
        ArrayList<CartRefCustomer> arrayList = new ArrayList<>();
        DataBaseHelper dataBaseHelper = DataBaseHelper.getInstance();
        if (dataBaseHelper == null) {
            return arrayList;
        }


        SQLiteDatabase db = dataBaseHelper.getReadableDatabase();
        Cursor cursor = db.query(CART_REF_CUSTOMER_TABLE, new String[]{KEY_ID, CART_UUID, EMPLOYEE_ID, USER_NAME, SHOP_ID, SHOP_NAME, CUSTOMER_ID, CUSTOMER_NAME,
                        CONTACT_NAME, CONTACT_PHONE, CUSTOMER_NOTES, INVOICE_NAME, ADDRESS, WAREHOUSE_ID, WAREHOUSE_NAME}, EMPLOYEE_ID + "=? and " +  CUSTOMER_ID + "=?",
                new String[]{employeeID, customerID}, null, null, CUSTOMER_ID, null);

        if (cursor != null && cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                CartRefCustomer cartRefCustomer = new CartRefCustomer(
                        cursor.getString(cursor.getColumnIndex(EMPLOYEE_ID)),
                        cursor.getString(cursor.getColumnIndex(USER_NAME)),
                        cursor.getString(cursor.getColumnIndex(SHOP_ID)),
                        cursor.getString(cursor.getColumnIndex(SHOP_NAME)),

                        cursor.getString(cursor.getColumnIndex(CUSTOMER_ID)),
                        cursor.getString(cursor.getColumnIndex(CUSTOMER_NAME)),
                        cursor.getString(cursor.getColumnIndex(CONTACT_NAME)),
                        cursor.getString(cursor.getColumnIndex(CONTACT_PHONE)),
                        cursor.getString(cursor.getColumnIndex(CUSTOMER_NOTES)),
                        cursor.getString(cursor.getColumnIndex(INVOICE_NAME)),
                        cursor.getString(cursor.getColumnIndex(ADDRESS)),
                        cursor.getString(cursor.getColumnIndex(WAREHOUSE_ID)),
                        cursor.getString(cursor.getColumnIndex(WAREHOUSE_NAME)));


                cartRefCustomer.setUuid(cursor.getString(cursor.getColumnIndex(CART_UUID)));
                arrayList.add(cartRefCustomer);
            }
        }
        return arrayList;
    }

    public static ArrayList<CartRefCustomer> queryCartRefCustomerItems(String cartUuid){
        ArrayList<CartRefCustomer> arrayList = new ArrayList<>();
        DataBaseHelper dataBaseHelper = DataBaseHelper.getInstance();
        if (dataBaseHelper == null) {
            return arrayList;
        }


        SQLiteDatabase db = dataBaseHelper.getReadableDatabase();
        Cursor cursor = db.query(CART_REF_CUSTOMER_TABLE, new String[]{KEY_ID, CART_UUID, EMPLOYEE_ID, USER_NAME, SHOP_ID, SHOP_NAME, CUSTOMER_ID, CUSTOMER_NAME,
                        CONTACT_NAME, CONTACT_PHONE, CUSTOMER_NOTES, INVOICE_NAME, ADDRESS, WAREHOUSE_ID, WAREHOUSE_NAME}, CART_UUID + "=?",
                new String[]{cartUuid}, null, null, CART_UUID, null);

        if (cursor != null && cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                CartRefCustomer cartRefCustomer = new CartRefCustomer(
                        cursor.getString(cursor.getColumnIndex(EMPLOYEE_ID)),
                        cursor.getString(cursor.getColumnIndex(USER_NAME)),
                        cursor.getString(cursor.getColumnIndex(SHOP_ID)),
                        cursor.getString(cursor.getColumnIndex(SHOP_NAME)),

                        cursor.getString(cursor.getColumnIndex(CUSTOMER_ID)),
                        cursor.getString(cursor.getColumnIndex(CUSTOMER_NAME)),
                        cursor.getString(cursor.getColumnIndex(CONTACT_NAME)),
                        cursor.getString(cursor.getColumnIndex(CONTACT_PHONE)),
                        cursor.getString(cursor.getColumnIndex(CUSTOMER_NOTES)),
                        cursor.getString(cursor.getColumnIndex(INVOICE_NAME)),
                        cursor.getString(cursor.getColumnIndex(ADDRESS)),
                        cursor.getString(cursor.getColumnIndex(WAREHOUSE_ID)),
                        cursor.getString(cursor.getColumnIndex(WAREHOUSE_NAME)));

                cartRefCustomer.setUuid(cursor.getString(cursor.getColumnIndex(CART_UUID)));
                arrayList.add(cartRefCustomer);
            }
        }
        return arrayList;
    }

    public static ArrayList<CartRefCustomer> getAllCartRefCustomerItems(String shopId){
        DataBaseHelper dataBaseHelper = DataBaseHelper.getInstance();
        if (dataBaseHelper == null) {
            return null;
        }
        SQLiteDatabase db = dataBaseHelper.getReadableDatabase();
        Cursor cursor = db.query(CART_REF_CUSTOMER_TABLE, new String[]{KEY_ID, CART_UUID, EMPLOYEE_ID, USER_NAME,
                        SHOP_ID, SHOP_NAME, CUSTOMER_ID, CUSTOMER_NAME,
                        CONTACT_NAME, CONTACT_PHONE, CUSTOMER_NOTES, INVOICE_NAME, ADDRESS, WAREHOUSE_ID, WAREHOUSE_NAME}, SHOP_ID + "=?",
                new String[]{shopId}, null, null, SHOP_ID, null);

        ArrayList<CartRefCustomer> arrayList = new ArrayList<>();

        if (cursor != null && cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                CartRefCustomer cartRefCustomer = new CartRefCustomer(
                        cursor.getString(cursor.getColumnIndex(EMPLOYEE_ID)),
                        cursor.getString(cursor.getColumnIndex(USER_NAME)),
                        cursor.getString(cursor.getColumnIndex(SHOP_ID)),
                        cursor.getString(cursor.getColumnIndex(SHOP_NAME)),

                        cursor.getString(cursor.getColumnIndex(CUSTOMER_ID)),
                        cursor.getString(cursor.getColumnIndex(CUSTOMER_NAME)),
                        cursor.getString(cursor.getColumnIndex(CONTACT_NAME)),
                        cursor.getString(cursor.getColumnIndex(CONTACT_PHONE)),
                        cursor.getString(cursor.getColumnIndex(CUSTOMER_NOTES)),
                        cursor.getString(cursor.getColumnIndex(INVOICE_NAME)),
                        cursor.getString(cursor.getColumnIndex(ADDRESS)),
                        cursor.getString(cursor.getColumnIndex(WAREHOUSE_ID)),
                        cursor.getString(cursor.getColumnIndex(WAREHOUSE_NAME)));

                cartRefCustomer.setUuid(cursor.getString(cursor.getColumnIndex(CART_UUID)));
                arrayList.add(cartRefCustomer);
            }
        }

        if (cursor != null) {
            cursor.close();
        }
        return arrayList;
    }

    public static ArrayList<CartRefCustomer> getAllCartRefCustomerItems(){
        DataBaseHelper dataBaseHelper = DataBaseHelper.getInstance();
        if (dataBaseHelper == null) {
            return null;
        }
        SQLiteDatabase db = dataBaseHelper.getReadableDatabase();
        Cursor cursor = db.query(CART_REF_CUSTOMER_TABLE, new String[]{KEY_ID, CART_UUID, EMPLOYEE_ID, USER_NAME,
                SHOP_ID, SHOP_NAME, CUSTOMER_ID, CUSTOMER_NAME,
                CONTACT_NAME, CONTACT_PHONE, CUSTOMER_NOTES, INVOICE_NAME, ADDRESS, WAREHOUSE_ID, WAREHOUSE_NAME}, null,
                null, null, null, CUSTOMER_ID, null);

        ArrayList<CartRefCustomer> arrayList = new ArrayList<>();

        if (cursor != null && cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                CartRefCustomer cartRefCustomer = new CartRefCustomer(
                        cursor.getString(cursor.getColumnIndex(EMPLOYEE_ID)),
                        cursor.getString(cursor.getColumnIndex(USER_NAME)),
                        cursor.getString(cursor.getColumnIndex(SHOP_ID)),
                        cursor.getString(cursor.getColumnIndex(SHOP_NAME)),

                        cursor.getString(cursor.getColumnIndex(CUSTOMER_ID)),
                        cursor.getString(cursor.getColumnIndex(CUSTOMER_NAME)),
                        cursor.getString(cursor.getColumnIndex(CONTACT_NAME)),
                        cursor.getString(cursor.getColumnIndex(CONTACT_PHONE)),
                        cursor.getString(cursor.getColumnIndex(CUSTOMER_NOTES)),
                        cursor.getString(cursor.getColumnIndex(INVOICE_NAME)),
                        cursor.getString(cursor.getColumnIndex(ADDRESS)),
                        cursor.getString(cursor.getColumnIndex(WAREHOUSE_ID)),
                        cursor.getString(cursor.getColumnIndex(WAREHOUSE_NAME)));

                cartRefCustomer.setUuid(cursor.getString(cursor.getColumnIndex(CART_UUID)));
                arrayList.add(cartRefCustomer);
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
    public static int removeCartRefCustomer(String employeeId, String customId) {
        DataBaseHelper dataBaseHelper = DataBaseHelper.getInstance();
        if (dataBaseHelper == null) {
            return 0;
        }
        SQLiteDatabase db = dataBaseHelper.getWritableDatabase();
        return db.delete(CART_REF_CUSTOMER_TABLE, CUSTOMER_ID + "=? and " + EMPLOYEE_ID + "=?", new String[]{customId, employeeId});
    }

    public static int removeCartRefCustomer(String cartUuid) {
        DataBaseHelper dataBaseHelper = DataBaseHelper.getInstance();
        if (dataBaseHelper == null) {
            return 0;
        }
        SQLiteDatabase db = dataBaseHelper.getWritableDatabase();
        return db.delete(CART_REF_CUSTOMER_TABLE, CART_UUID + "=?", new String[]{cartUuid});
    }
}
