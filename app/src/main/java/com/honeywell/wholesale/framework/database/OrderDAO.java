package com.honeywell.wholesale.framework.database;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.Nullable;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.honeywell.wholesale.framework.model.Order;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

/**
 * Created by xiaofei on 7/26/16.
 * 销售订单
 */
public class OrderDAO {

    private static final String TAG = "OrderDAO";

    private static final String ORDER_TABLE = "order_table";
    private static final String KEY_ID = "id";

    private static final String ORDER_NUMBER = "order_number";
    private static final String SALE_TIME = "sale_time";
    private static final String EMPLOYEE_NAME = "employee_name";
    private static final String EMPLOYEE_ID = "employee_id";

    private static final String CUSTOMER_ID = "customer_id";
    private static final String CUSTOMER_NAME = "customer_name";
    private static final String ADDRESS = "address";
    private static final String CONTACT_NAME = "contact_name";
    private static final String CONTACT_PHONE = "contact_phone";
    private static final String INVOICE_TITLE = "invoice_title";

    private static final String TOTAL_PRICE = "total_price";
    private static final String PAYMENT = "payment";
    private static final String PAYMENT_STATUS = "payment_status";

    private static final String SHOP_ID = "shop_id";
    private static final String SALE_LIST = "sale_list";

    public static final String CREATE_ORDER_TABLE = "CREATE TABLE IF NOT EXISTS " + ORDER_TABLE + "("
            + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + ORDER_NUMBER + " TEXT,"
            + SALE_TIME + " TEXT,"
            + EMPLOYEE_NAME + " TEXT,"
            + EMPLOYEE_ID + " TEXT,"
            + CUSTOMER_ID + " TEXT,"

            + CUSTOMER_NAME + " TEXT,"
            + ADDRESS + " TEXT,"
            + CONTACT_NAME + " TEXT,"
            + CONTACT_PHONE + " TEXT,"
            + INVOICE_TITLE + " TEXT,"
            + TOTAL_PRICE + " TEXT,"
            + PAYMENT + " TEXT,"
            + PAYMENT_STATUS + " TEXT,"
            + SHOP_ID + " TEXT,"
            + SALE_LIST + " TEXT"
            + ")";

    public static void addOrder(@Nullable String orderNumber, @Nullable String saleTime, @Nullable String employeeName,
                                @Nullable String employeeId, @Nullable String customerId, @Nullable String customerName,
                                @Nullable String address, @Nullable String contactName, @Nullable String contactPhone,
                                @Nullable String invoiceTitle, @Nullable String totalPrice, @Nullable String payment,
                                @Nullable String paymentStatus, @Nullable String shopId, @Nullable String saleList) {
        DataBaseHelper dataBaseHelper = DataBaseHelper.getInstance();
        if (dataBaseHelper == null){
            return;
        }
        SQLiteDatabase db = dataBaseHelper.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(ORDER_NUMBER, orderNumber);
        values.put(SALE_TIME, saleTime);
        values.put(EMPLOYEE_NAME, employeeName);
        values.put(EMPLOYEE_ID, employeeId);
        values.put(CUSTOMER_ID, customerId);

        values.put(CUSTOMER_NAME, customerName);
        values.put(ADDRESS, address);

        values.put(CONTACT_NAME, contactName);

        values.put(CONTACT_PHONE, contactPhone);
        values.put(INVOICE_TITLE, invoiceTitle);
        values.put(TOTAL_PRICE, totalPrice);
        values.put(PAYMENT, payment);

        values.put(PAYMENT_STATUS, paymentStatus);
        values.put(SALE_LIST, saleList);

        values.put(SHOP_ID, shopId);

        db.insert(ORDER_TABLE, null, values);
        db.close();
    }

    public static void addOrder(Order order) {
        DataBaseHelper dataBaseHelper = DataBaseHelper.getInstance();
        if (dataBaseHelper == null){
            return;
        }
        SQLiteDatabase db = dataBaseHelper.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(ORDER_NUMBER, order.getOrderNumber());
        values.put(SALE_TIME, order.getSaleTime());
        values.put(EMPLOYEE_NAME, order.getEmployeeName());
        values.put(EMPLOYEE_ID, order.getEmployeeID());
        values.put(CUSTOMER_ID, order.getCustomerID());

        values.put(CUSTOMER_NAME, order.getCustomerName());
        values.put(ADDRESS, order.getAddress());

        values.put(CONTACT_NAME, order.getContactName());

        values.put(CONTACT_PHONE, order.getContactPhone());
        values.put(INVOICE_TITLE, order.getInvoiceTitle());
        values.put(TOTAL_PRICE, order.getTotalPrice());
        values.put(PAYMENT, order.getPayment());

        values.put(PAYMENT_STATUS, order.getOrderStatus());

        Gson gson = new Gson();
        String jsonString = gson.toJson(order.getSaleList());

        values.put(SALE_LIST, jsonString);

        values.put(SHOP_ID, order.getShopId());

        db.insert(ORDER_TABLE, null, values);
        db.close();
    }

    // get all order item
    public static ArrayList<Order> getAllOrderItems(String shopId) throws JSONException {

        DataBaseHelper dataBaseHelper = DataBaseHelper.getInstance();
        if (dataBaseHelper == null) {
            return null;
        }
        SQLiteDatabase db = dataBaseHelper.getReadableDatabase();

        Cursor cursor = db.query(ORDER_TABLE, new String[]{KEY_ID, ORDER_NUMBER, SALE_TIME, EMPLOYEE_NAME, EMPLOYEE_ID,
                        CUSTOMER_ID, CUSTOMER_NAME, ADDRESS,
                        CONTACT_NAME, CONTACT_PHONE, INVOICE_TITLE, TOTAL_PRICE,
                        PAYMENT, PAYMENT_STATUS, SALE_LIST}, SHOP_ID + "=?",
                new String[]{String.valueOf(shopId)}, null, null, SALE_TIME, null);

        ArrayList<Order> arrayList = new ArrayList<>();
        if (cursor != null && cursor.getCount() > 0) {
            Gson gson = new Gson();
            while (cursor.moveToNext()) {
                String productString = cursor.getString(cursor.getColumnIndex(SALE_LIST));
                JSONArray productJsonArray = new JSONArray(productString);
                Order.Product[] products = new Order.Product[productJsonArray.length()];

                for (int i = 0 ; i < productJsonArray.length(); i++){
                    String jsonString = productJsonArray.optString(i);
                    JsonElement mJson =  new JsonParser().parse(jsonString);
                    Order.Product product = gson.fromJson(mJson, Order.Product.class);
                    products[i] = product;
                }

                Order order = new Order(
                        cursor.getString(cursor.getColumnIndex(TOTAL_PRICE)),
                        cursor.getString(cursor.getColumnIndex(ADDRESS)),
                        cursor.getString(cursor.getColumnIndex(CONTACT_NAME)),
                        cursor.getString(cursor.getColumnIndex(CONTACT_PHONE)),
                        cursor.getString(cursor.getColumnIndex(CUSTOMER_ID)),
                        cursor.getString(cursor.getColumnIndex(CUSTOMER_NAME)),
                        cursor.getString(cursor.getColumnIndex(EMPLOYEE_ID)),
                        cursor.getString(cursor.getColumnIndex(EMPLOYEE_NAME)),
                        cursor.getString(cursor.getColumnIndex(INVOICE_TITLE)),
                        cursor.getString(cursor.getColumnIndex(ORDER_NUMBER)),
                        cursor.getString(cursor.getColumnIndex(PAYMENT)),
                        cursor.getString(cursor.getColumnIndex(PAYMENT_STATUS)),
                        products,
                        cursor.getString(cursor.getColumnIndex(SALE_TIME)),
                        shopId);

                arrayList.add(order);
            }
        }
        return arrayList;
    }

    public static Order queryOrderItem(String shopId, @Nullable String orderNumber) throws JSONException {
        DataBaseHelper dataBaseHelper = DataBaseHelper.getInstance();
        if (dataBaseHelper == null) {
            return null;
        }
        SQLiteDatabase db = dataBaseHelper.getWritableDatabase();

        Cursor cursor = db.query(ORDER_TABLE, new String[]{KEY_ID, ORDER_NUMBER, SALE_TIME, EMPLOYEE_NAME, EMPLOYEE_ID,
                        CUSTOMER_ID, CUSTOMER_NAME, ADDRESS,
                        CONTACT_NAME, CONTACT_PHONE, INVOICE_TITLE, TOTAL_PRICE,
                        PAYMENT, PAYMENT_STATUS, SALE_LIST}, SHOP_ID + "=? and " + ORDER_NUMBER + "=?",
                new String[]{shopId, orderNumber}, null, null, SALE_TIME, null);
        Order order = null;
        if (cursor != null && cursor.getCount() > 0) {
            Gson gson = new Gson();
            cursor.moveToFirst();

            String productString = cursor.getString(cursor.getColumnIndex(SALE_LIST));
            JSONArray productJsonArray = new JSONArray(productString);
            Order.Product[] products = new Order.Product[productJsonArray.length()];

            for (int i = 0 ; i < productJsonArray.length(); i++){
                String jsonString = productJsonArray.optString(i);
                JsonElement mJson =  new JsonParser().parse(jsonString);
                Order.Product product = gson.fromJson(mJson, Order.Product.class);
                products[i] = product;
            }

            order = new Order(
                    cursor.getString(cursor.getColumnIndex(TOTAL_PRICE)),
                    cursor.getString(cursor.getColumnIndex(ADDRESS)),
                    cursor.getString(cursor.getColumnIndex(CONTACT_NAME)),
                    cursor.getString(cursor.getColumnIndex(CONTACT_PHONE)),
                    cursor.getString(cursor.getColumnIndex(CUSTOMER_ID)),
                    cursor.getString(cursor.getColumnIndex(CUSTOMER_NAME)),
                    cursor.getString(cursor.getColumnIndex(EMPLOYEE_ID)),
                    cursor.getString(cursor.getColumnIndex(EMPLOYEE_NAME)),
                    cursor.getString(cursor.getColumnIndex(INVOICE_TITLE)),
                    cursor.getString(cursor.getColumnIndex(ORDER_NUMBER)),
                    cursor.getString(cursor.getColumnIndex(PAYMENT)),
                    cursor.getString(cursor.getColumnIndex(PAYMENT_STATUS)),
                    products,
                    cursor.getString(cursor.getColumnIndex(SALE_TIME)),
                    shopId);

        }
        return order;
    }

    public static int updateOrder(Order order){
        DataBaseHelper dataBaseHelper = DataBaseHelper.getInstance();
        if (dataBaseHelper == null){
            return -1;
        }
        SQLiteDatabase db = dataBaseHelper.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(ORDER_NUMBER, order.getOrderNumber());
        values.put(SALE_TIME, order.getSaleTime());
        values.put(EMPLOYEE_NAME, order.getEmployeeName());
        values.put(EMPLOYEE_ID, order.getEmployeeID());
        values.put(CUSTOMER_ID, order.getCustomerID());

        values.put(CUSTOMER_NAME, order.getCustomerName());
        values.put(ADDRESS, order.getAddress());

        values.put(CONTACT_NAME, order.getContactName());

        values.put(CONTACT_PHONE, order.getContactPhone());
        values.put(INVOICE_TITLE, order.getInvoiceTitle());
        values.put(TOTAL_PRICE, order.getTotalPrice());
        values.put(PAYMENT, order.getPayment());

        values.put(PAYMENT_STATUS, order.getOrderStatus());

        Gson gson = new Gson();
        String jsonString = gson.toJson(order.getSaleList());

        values.put(SALE_LIST, jsonString);

        values.put(SHOP_ID, order.getShopId());

        return db.update(ORDER_TABLE, values, ORDER_NUMBER + "=? and " + SHOP_ID + "=?",
                new String[]{order.getOrderNumber(), order.getShopId()});
    }

    public static void removeDataAfter30days(){
        DataBaseHelper dataBaseHelper = DataBaseHelper.getInstance();
        if (dataBaseHelper == null){
            return ;
        }
        SQLiteDatabase db = dataBaseHelper.getWritableDatabase();

        String delSql = "DELETE FROM " + ORDER_TABLE + " WHERE " + SALE_TIME + " <= date('now','-30 day')";

        db.execSQL(delSql);
    }

    public static ArrayList<Order> queryOrderBySearch(String queryString, String shopId) throws JSONException{
        DataBaseHelper dataBaseHelper = DataBaseHelper.getInstance();
        if (dataBaseHelper == null){
            return null;
        }
        SQLiteDatabase db = dataBaseHelper.getWritableDatabase();

        Cursor cursor = db.query(
                ORDER_TABLE,
                new String[]{KEY_ID, ORDER_NUMBER, SALE_TIME, EMPLOYEE_NAME, EMPLOYEE_ID,
                        CUSTOMER_ID, CUSTOMER_NAME, ADDRESS,
                        CONTACT_NAME, CONTACT_PHONE, INVOICE_TITLE, TOTAL_PRICE,
                        PAYMENT, PAYMENT_STATUS, SALE_LIST},
                SHOP_ID + "=? and (" + ORDER_NUMBER + " LIKE ? or " +
                CUSTOMER_NAME + " LIKE ? or " + SALE_TIME + " LIKE ?)",
                new String[]{shopId, "%" + queryString + "%", "%" + queryString + "%", "%" + queryString + "%"},
                null, null, SALE_TIME, null);

        ArrayList<Order> arrayList = new ArrayList<>();

        if (cursor != null && cursor.getCount() > 0) {
            Gson gson = new Gson();
            while (cursor.moveToNext()) {
                String productString = cursor.getString(cursor.getColumnIndex(SALE_LIST));
                JSONArray productJsonArray = new JSONArray(productString);
                Order.Product[] products = new Order.Product[productJsonArray.length()];

                for (int i = 0; i < productJsonArray.length(); i++) {
                    String jsonString = productJsonArray.optString(i);
                    JsonElement mJson = new JsonParser().parse(jsonString);
                    Order.Product product = gson.fromJson(mJson, Order.Product.class);
                    products[i] = product;
                }

                Order order = new Order(
                        cursor.getString(cursor.getColumnIndex(TOTAL_PRICE)),
                        cursor.getString(cursor.getColumnIndex(ADDRESS)),
                        cursor.getString(cursor.getColumnIndex(CONTACT_NAME)),
                        cursor.getString(cursor.getColumnIndex(CONTACT_PHONE)),
                        cursor.getString(cursor.getColumnIndex(CUSTOMER_ID)),
                        cursor.getString(cursor.getColumnIndex(CUSTOMER_NAME)),
                        cursor.getString(cursor.getColumnIndex(EMPLOYEE_ID)),
                        cursor.getString(cursor.getColumnIndex(EMPLOYEE_NAME)),
                        cursor.getString(cursor.getColumnIndex(INVOICE_TITLE)),
                        cursor.getString(cursor.getColumnIndex(ORDER_NUMBER)),
                        cursor.getString(cursor.getColumnIndex(PAYMENT)),
                        cursor.getString(cursor.getColumnIndex(PAYMENT_STATUS)),
                        products,
                        cursor.getString(cursor.getColumnIndex(SALE_TIME)),
                        shopId);

                arrayList.add(order);
            }
        }

        return arrayList;
    }

}
