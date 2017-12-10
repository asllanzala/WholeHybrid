package com.honeywell.wholesale.framework.database;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.Nullable;

import com.honeywell.hybridapp.framework.log.LogHelper;
import com.honeywell.wholesale.framework.model.Customer;
import com.honeywell.wholesale.framework.utils.WSLinkedJSONObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xiaofei on 8/25/16.
 * 客户本地db
 */
public class CustomerDAO {
    private static final String TAG = "CustomerDAO";

    private static final String CUSTOMER_TABLE = "customer_table";
    private static final String KEY_ID = "id";

    private static final String CUSTOMER_ID = "customer_id";
    private static final String CUSTOMER_CONTACT_NAME = "contact_name";
    private static final String CUSTOMER_SHOP_ID = "shop_id";
    private static final String CUSTOMER_CONTACT_PHONE = "contact_phone";
    private static final String CUSTOMER_NAME = "customer_name";

    //备注信息
    private static final String CUSTOMER_MEMO = "memo";
    private static final String CUSTOMER_ADDRESS = "address";
    private static final String CUSTOMER_INVOICE_TITLE = "invoice_title";

    //数据分组
    private static final String CUSTOMER_GROUP = "cus_group";

    //pinyin
    private static final String CUSTOMER_PINYIN = "pinyin";
    private static final String CUSTOMER_INITIALS = "initials";

    public static final String CREATE_CUSTOMER_TABLE = "CREATE TABLE IF NOT EXISTS " + CUSTOMER_TABLE + "("
            + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + CUSTOMER_ID + " TEXT,"
            + CUSTOMER_CONTACT_NAME + " TEXT,"
            + CUSTOMER_SHOP_ID + " TEXT,"
            + CUSTOMER_CONTACT_PHONE + " TEXT,"
            + CUSTOMER_NAME + " TEXT,"

            + CUSTOMER_MEMO + " TEXT,"
            + CUSTOMER_ADDRESS + " TEXT,"
            + CUSTOMER_INVOICE_TITLE + " TEXT,"
            + CUSTOMER_GROUP + " TEXT,"
            + CUSTOMER_PINYIN + " TEXT,"
            + CUSTOMER_INITIALS + " TEXT"
            + ")";

    public static final String DROP_CUSTOMER_TABLE = "DROP TABLE IF EXISTS " + CUSTOMER_TABLE;


    public static void addCustomer(Customer customer, String group) {
        DataBaseHelper dataBaseHelper = DataBaseHelper.getInstance();
        if (dataBaseHelper == null){
            return;
        }
        SQLiteDatabase db = dataBaseHelper.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(CUSTOMER_ID, customer.getCustomeId());
        values.put(CUSTOMER_CONTACT_NAME, customer.getContactName());
        values.put(CUSTOMER_SHOP_ID, customer.getShopId());
        values.put(CUSTOMER_CONTACT_PHONE, customer.getContactPhone());
        values.put(CUSTOMER_NAME, customer.getCustomerName());
        values.put(CUSTOMER_MEMO, customer.getMemo());

        values.put(CUSTOMER_ADDRESS, customer.getAddress());
        values.put(CUSTOMER_INVOICE_TITLE, customer.getInvoiceTitle());

        values.put(CUSTOMER_GROUP, group);

        values.put(CUSTOMER_PINYIN, customer.getPinyin());

        values.put(CUSTOMER_INITIALS, customer.getInitials());

        db.insert(CUSTOMER_TABLE, null, values);
        db.close();
    }

    public static ArrayList getAllCustomer(String shopId){
        DataBaseHelper dataBaseHelper = DataBaseHelper.getInstance();
        if (dataBaseHelper == null) {
            return null;
        }
        SQLiteDatabase db = dataBaseHelper.getReadableDatabase();

        Cursor cursor = db.query(
                CUSTOMER_TABLE,
                new String[]{KEY_ID, CUSTOMER_ID,
                        CUSTOMER_CONTACT_NAME, CUSTOMER_SHOP_ID, CUSTOMER_CONTACT_PHONE, CUSTOMER_NAME,
                        CUSTOMER_MEMO, CUSTOMER_ADDRESS, CUSTOMER_INVOICE_TITLE, CUSTOMER_PINYIN,
                        CUSTOMER_INITIALS, CUSTOMER_GROUP},
                CUSTOMER_SHOP_ID + "=?",
                new String[]{String.valueOf(shopId)},
                null, null, null, null);

        ArrayList<Customer> arrayList = new ArrayList<>();
        if (cursor != null && cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                Customer customer = new Customer(
                        cursor.getString(cursor.getColumnIndex(CUSTOMER_ID)),
                        cursor.getString(cursor.getColumnIndex(CUSTOMER_CONTACT_NAME)),
                        cursor.getString(cursor.getColumnIndex(CUSTOMER_SHOP_ID)),
                        cursor.getString(cursor.getColumnIndex(CUSTOMER_CONTACT_PHONE)),
                        cursor.getString(cursor.getColumnIndex(CUSTOMER_ADDRESS)),
                        cursor.getString(cursor.getColumnIndex(CUSTOMER_NAME)),
                        cursor.getString(cursor.getColumnIndex(CUSTOMER_INVOICE_TITLE)),
                        cursor.getString(cursor.getColumnIndex(CUSTOMER_MEMO)),
                        cursor.getString(cursor.getColumnIndex(CUSTOMER_PINYIN)),
                        cursor.getString(cursor.getColumnIndex(CUSTOMER_INITIALS)));

                customer.setGroup(cursor.getString(cursor.getColumnIndex(CUSTOMER_GROUP)));
                arrayList.add(customer);
            }
        }

        if (cursor != null) {
            cursor.close();
        }
        return arrayList;
    }

    /**
     *
     * @param shopId
     * @return 所有客户信息,并且按照拼音首字母分成了小组
     */
    public static String getAllCustomerWithGroup(String shopId) {
        List<Customer> allCustomerList = getAllCustomer(shopId);
        WSLinkedJSONObject jsonObject = new WSLinkedJSONObject();
        String[] groupNameArray = new String[]{"#","a","b","c","d","e","f","g","h","j","k","l","m","n","o","p","q","r","s","t","u","v","w","x","y","z"};;

        for(int i = 0; i< groupNameArray.length; i++) {
            String groupName = groupNameArray[i];
            JSONArray groupJson = filterGroup(groupName, allCustomerList);
            if(groupJson.length() > 0){
                try {
                    jsonObject.put(groupName, groupJson);
                } catch (JSONException e) {
                    LogHelper.getInstance().e(TAG, "获取客户分组出错: " + e.getLocalizedMessage());
                }
            }
        }
        return  jsonObject.toString();
    }

    /**
     *
     * @param shopId
     * @return 所有客户信息,并且按照拼音首字母分成了小组
     */
    public static WSLinkedJSONObject getAllCustomerObjectWithGroup(String shopId) {
        List<Customer> allCustomerList = getAllCustomer(shopId);
        WSLinkedJSONObject jsonObject = new WSLinkedJSONObject();
        String[] groupNameArray = new String[]{"#","a","b","c","d","e","f","g","h","j","k","l","m","n","o","p","q","r","s","t","u","v","w","x","y","z"};;

        for(int i = 0; i< groupNameArray.length; i++) {
            String groupName = groupNameArray[i];
            JSONArray groupJson = filterGroup(groupName, allCustomerList);
            if(groupJson.length() > 0){
                try {
                    jsonObject.put(groupName, groupJson);
                } catch (JSONException e) {
                    LogHelper.getInstance().e(TAG, "获取客户分组出错: " + e.getLocalizedMessage());
                }
            }
        }
        return  jsonObject;
    }

    /**
     * 获取给定组名的所有客户信息,并封装成JSON类型。
     * @param groupName
     * @param allCustomerList
     * @return 所有给定组的客户的JSON数组字符串。
     */
    public static JSONArray filterGroup(String groupName, List<Customer> allCustomerList) {
        JSONArray jsonArray = new JSONArray();
        for (int i = allCustomerList.size() - 1; i >=0; i--) {
            Customer customer = allCustomerList.get(i);
            if (groupName.equals(customer.getGroup())) {
                allCustomerList.remove(i);
                try {
                    WSLinkedJSONObject jsonObject = new WSLinkedJSONObject(customer.getJsonString());
                    jsonArray.put(jsonObject);
                } catch (JSONException e) {
                    LogHelper.getInstance().e(TAG, "客户信息转JSON报错: " + customer.getJsonString());
                    LogHelper.getInstance().e(TAG, "获取客户分组出错: " + e.getLocalizedMessage());
                }
            }
        }
        return jsonArray;
    }

    public static Customer queryByCustomer(String customerId){
        DataBaseHelper dataBaseHelper = DataBaseHelper.getInstance();
        if (dataBaseHelper == null) {
            return null;
        }
        SQLiteDatabase db = dataBaseHelper.getReadableDatabase();

        Cursor cursor = db.query(
                CUSTOMER_TABLE,
                new String[]{KEY_ID, CUSTOMER_ID,
                        CUSTOMER_CONTACT_NAME, CUSTOMER_SHOP_ID, CUSTOMER_CONTACT_PHONE, CUSTOMER_NAME,
                        CUSTOMER_MEMO, CUSTOMER_ADDRESS, CUSTOMER_INVOICE_TITLE, CUSTOMER_PINYIN,
                        CUSTOMER_INITIALS, CUSTOMER_GROUP},
                CUSTOMER_ID + "=?",
                new String[]{String.valueOf(customerId)},
                null, null, null, null);

        Customer customer = null;
        if (cursor != null && cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                customer = new Customer(
                        cursor.getString(cursor.getColumnIndex(CUSTOMER_ID)),
                        cursor.getString(cursor.getColumnIndex(CUSTOMER_CONTACT_NAME)),
                        cursor.getString(cursor.getColumnIndex(CUSTOMER_SHOP_ID)),
                        cursor.getString(cursor.getColumnIndex(CUSTOMER_CONTACT_PHONE)),
                        cursor.getString(cursor.getColumnIndex(CUSTOMER_ADDRESS)),
                        cursor.getString(cursor.getColumnIndex(CUSTOMER_NAME)),
                        cursor.getString(cursor.getColumnIndex(CUSTOMER_INVOICE_TITLE)),
                        cursor.getString(cursor.getColumnIndex(CUSTOMER_MEMO)),
                        cursor.getString(cursor.getColumnIndex(CUSTOMER_PINYIN)),
                        cursor.getString(cursor.getColumnIndex(CUSTOMER_INITIALS)));

                customer.setGroup(cursor.getString(cursor.getColumnIndex(CUSTOMER_GROUP)));
            }
        }
        return customer;
    }

    public static int updateCustomer(Customer customer, String shopId){
        DataBaseHelper dataBaseHelper = DataBaseHelper.getInstance();

        SQLiteDatabase db = dataBaseHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(CUSTOMER_ID, customer.getCustomeId());
        values.put(CUSTOMER_CONTACT_NAME, customer.getContactName());
        values.put(CUSTOMER_SHOP_ID, customer.getShopId());
        values.put(CUSTOMER_CONTACT_PHONE, customer.getContactPhone());
        values.put(CUSTOMER_NAME, customer.getCustomerName());
        values.put(CUSTOMER_MEMO, customer.getMemo());

        values.put(CUSTOMER_ADDRESS, customer.getAddress());
        values.put(CUSTOMER_INVOICE_TITLE, customer.getInvoiceTitle());

        values.put(CUSTOMER_PINYIN, customer.getPinyin());

        values.put(CUSTOMER_INITIALS, customer.getInitials());

        values.put(CUSTOMER_GROUP, customer.getGroup());


        return db.update(CUSTOMER_TABLE, values, CUSTOMER_ID + "=? and " + CUSTOMER_SHOP_ID + "=?",
                new String[]{customer.getCustomeId(), shopId});
    }


    public static int removeCustomer(String shopId, @Nullable String customerId) {
        DataBaseHelper dataBaseHelper = DataBaseHelper.getInstance();
        if (dataBaseHelper == null) {
            return 0;
        }
        SQLiteDatabase db = dataBaseHelper.getWritableDatabase();
        return db.delete(CUSTOMER_TABLE, CUSTOMER_SHOP_ID + "=? and " + CUSTOMER_ID + "=?",
                new String[]{shopId, customerId});
    }

    public static ArrayList queryCustomerBySearch(String queryString, String shopId){
        DataBaseHelper dataBaseHelper = DataBaseHelper.getInstance();
        if (dataBaseHelper == null) {
            return null;
        }
        SQLiteDatabase db = dataBaseHelper.getReadableDatabase();

        Cursor cursor = db.query(
                CUSTOMER_TABLE,
                new String[]{KEY_ID, CUSTOMER_ID,
                        CUSTOMER_CONTACT_NAME, CUSTOMER_SHOP_ID, CUSTOMER_CONTACT_PHONE, CUSTOMER_NAME,
                        CUSTOMER_MEMO, CUSTOMER_ADDRESS, CUSTOMER_INVOICE_TITLE, CUSTOMER_PINYIN,
                        CUSTOMER_INITIALS, CUSTOMER_GROUP},
                CUSTOMER_SHOP_ID + "=? and (" + CUSTOMER_CONTACT_NAME + " LIKE ? or " +
                        CUSTOMER_NAME + " LIKE ? or " + CUSTOMER_PINYIN + " LIKE ? or " +
                        CUSTOMER_INITIALS + " LIKE ?)",
                new String[]{shopId, "%" + queryString + "%", "%" + queryString + "%",
                        "%" + queryString + "%", "%" + queryString + "%"},
                null, null, null, null);

        ArrayList<Customer> arrayList = new ArrayList<>();
        if (cursor != null && cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                Customer customer = new Customer(
                        cursor.getString(cursor.getColumnIndex(CUSTOMER_ID)),
                        cursor.getString(cursor.getColumnIndex(CUSTOMER_CONTACT_NAME)),
                        cursor.getString(cursor.getColumnIndex(CUSTOMER_SHOP_ID)),
                        cursor.getString(cursor.getColumnIndex(CUSTOMER_CONTACT_PHONE)),
                        cursor.getString(cursor.getColumnIndex(CUSTOMER_ADDRESS)),
                        cursor.getString(cursor.getColumnIndex(CUSTOMER_NAME)),
                        cursor.getString(cursor.getColumnIndex(CUSTOMER_INVOICE_TITLE)),
                        cursor.getString(cursor.getColumnIndex(CUSTOMER_MEMO)),
                        cursor.getString(cursor.getColumnIndex(CUSTOMER_PINYIN)),
                        cursor.getString(cursor.getColumnIndex(CUSTOMER_INITIALS)));

                customer.setGroup(cursor.getString(cursor.getColumnIndex(CUSTOMER_GROUP)));
                arrayList.add(customer);
            }
        }

        if (cursor != null) {
            cursor.close();
        }
        return arrayList;
    }
}
