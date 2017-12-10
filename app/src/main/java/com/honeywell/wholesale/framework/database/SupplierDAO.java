package com.honeywell.wholesale.framework.database;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.Nullable;

import com.honeywell.hybridapp.framework.log.LogHelper;
import com.honeywell.wholesale.framework.model.Supplier;
import com.honeywell.wholesale.framework.utils.WSLinkedJSONObject;
import com.honeywell.wholesale.lib.util.StringUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xiaofei on 8/25/16.
 * 供应商 本地db
 */
public class SupplierDAO {

    private static final String TAG = "SupplierDAO";

    private static final String SUPPLIER_TABLE = "supplier_table";
    private static final String KEY_ID = "id";

    private static final String SUPPLIER_ID = "supplier_id";
    private static final String SUPPLIER_CONTACT_NAME = "contact_name";
    private static final String SUPPLIER_SHOP_ID = "shop_id";
    private static final String SUPPLIER_CONTACT_PHONE = "contact_phone";
    private static final String SUPPLIER_SUPPLIER_NAME = "supplier_name";

    //备注信息
    private static final String SUPPLIER_DESC_MEMO = "desc_memo";
    private static final String SUPPLIER_BANK_INFO = "bank_info";
    private static final String SUPPLIER_ADDRESS = "address";

    //pinyin
    private static final String SUPPLIER_PINYIN = "pinyin";
    private static final String SUPPLIER_INITIALS = "initials";

    public static final String CREATE_SUPPLIER_TABLE = "CREATE TABLE IF NOT EXISTS " + SUPPLIER_TABLE + "("
            + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + SUPPLIER_ID + " TEXT,"
            + SUPPLIER_CONTACT_NAME + " TEXT,"
            + SUPPLIER_SHOP_ID + " TEXT,"
            + SUPPLIER_CONTACT_PHONE + " TEXT,"
            + SUPPLIER_SUPPLIER_NAME + " TEXT,"

            + SUPPLIER_DESC_MEMO + " TEXT,"
            + SUPPLIER_BANK_INFO + " TEXT,"
            + SUPPLIER_ADDRESS + " TEXT,"
            + SUPPLIER_PINYIN + " TEXT,"
            + SUPPLIER_INITIALS + " TEXT"
            + ")";

    public static final String DROP_SUPPLIER_TABLE = "DROP TABLE IF EXISTS " + SUPPLIER_TABLE;


    public static void addVendor(Supplier supplier) {
        DataBaseHelper dataBaseHelper = DataBaseHelper.getInstance();
        if (dataBaseHelper == null){
            return;
        }
        SQLiteDatabase db = dataBaseHelper.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(SUPPLIER_ID, supplier.getSupplierId());
        values.put(SUPPLIER_CONTACT_NAME, supplier.getContactName());
        values.put(SUPPLIER_SHOP_ID, supplier.getShopId());
        values.put(SUPPLIER_CONTACT_PHONE, supplier.getContactPhone());
        values.put(SUPPLIER_SUPPLIER_NAME, supplier.getSupplierName());
        values.put(SUPPLIER_DESC_MEMO, supplier.getDescMemo());

        values.put(SUPPLIER_BANK_INFO, supplier.getBankInfo());
        values.put(SUPPLIER_ADDRESS, supplier.getAddress());

        values.put(SUPPLIER_PINYIN, supplier.getPinyin());

        values.put(SUPPLIER_INITIALS, supplier.getInitials());


        db.insert(SUPPLIER_TABLE, null, values);
        db.close();
    }

    public static Supplier queryVendor(String shopId, String supplierId){
        DataBaseHelper dataBaseHelper = DataBaseHelper.getInstance();
        if (dataBaseHelper == null) {
            return null;
        }
        SQLiteDatabase db = dataBaseHelper.getReadableDatabase();

        Cursor cursor = db.query(
                SUPPLIER_TABLE,
                new String[]{KEY_ID, SUPPLIER_ID,
                        SUPPLIER_CONTACT_NAME, SUPPLIER_SHOP_ID, SUPPLIER_CONTACT_PHONE, SUPPLIER_SUPPLIER_NAME,
                        SUPPLIER_DESC_MEMO, SUPPLIER_BANK_INFO, SUPPLIER_ADDRESS, SUPPLIER_PINYIN,
                        SUPPLIER_INITIALS},
                SUPPLIER_SHOP_ID + "=? and " + SUPPLIER_ID + "=?",
                new String[]{shopId, supplierId},
                null, null, null, null);

        Supplier supplier = null;
        if (cursor != null && cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                supplier = new Supplier(
                        cursor.getString(cursor.getColumnIndex(SUPPLIER_ID)),
                        cursor.getString(cursor.getColumnIndex(SUPPLIER_CONTACT_NAME)),
                        cursor.getString(cursor.getColumnIndex(SUPPLIER_SHOP_ID)),
                        cursor.getString(cursor.getColumnIndex(SUPPLIER_CONTACT_PHONE)),
                        cursor.getString(cursor.getColumnIndex(SUPPLIER_SUPPLIER_NAME)),
                        cursor.getString(cursor.getColumnIndex(SUPPLIER_DESC_MEMO)),
                        cursor.getString(cursor.getColumnIndex(SUPPLIER_BANK_INFO)),
                        cursor.getString(cursor.getColumnIndex(SUPPLIER_ADDRESS)),
                        cursor.getString(cursor.getColumnIndex(SUPPLIER_PINYIN)),
                        cursor.getString(cursor.getColumnIndex(SUPPLIER_INITIALS)));

            }
        }

        if (cursor != null) {
            cursor.close();
        }

        return supplier;
    }

    public static ArrayList getAllVendor(String shopId){
        DataBaseHelper dataBaseHelper = DataBaseHelper.getInstance();
        if (dataBaseHelper == null) {
            return null;
        }
        SQLiteDatabase db = dataBaseHelper.getReadableDatabase();

        Cursor cursor = db.query(
                SUPPLIER_TABLE,
                new String[]{KEY_ID, SUPPLIER_ID,
                        SUPPLIER_CONTACT_NAME, SUPPLIER_SHOP_ID, SUPPLIER_CONTACT_PHONE, SUPPLIER_SUPPLIER_NAME,
                        SUPPLIER_DESC_MEMO, SUPPLIER_BANK_INFO, SUPPLIER_ADDRESS, SUPPLIER_PINYIN,
                        SUPPLIER_INITIALS},
                SUPPLIER_SHOP_ID + "=?",
                new String[]{String.valueOf(shopId)},
                null, null, null, null);

        ArrayList<Supplier> arrayList = new ArrayList<>();
        if (cursor != null && cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                Supplier supplier = new Supplier(
                        cursor.getString(cursor.getColumnIndex(SUPPLIER_ID)),
                        cursor.getString(cursor.getColumnIndex(SUPPLIER_CONTACT_NAME)),
                        cursor.getString(cursor.getColumnIndex(SUPPLIER_SHOP_ID)),
                        cursor.getString(cursor.getColumnIndex(SUPPLIER_CONTACT_PHONE)),
                        cursor.getString(cursor.getColumnIndex(SUPPLIER_SUPPLIER_NAME)),
                        cursor.getString(cursor.getColumnIndex(SUPPLIER_DESC_MEMO)),
                        cursor.getString(cursor.getColumnIndex(SUPPLIER_BANK_INFO)),
                        cursor.getString(cursor.getColumnIndex(SUPPLIER_ADDRESS)),
                        cursor.getString(cursor.getColumnIndex(SUPPLIER_PINYIN)),
                        cursor.getString(cursor.getColumnIndex(SUPPLIER_INITIALS)));
                arrayList.add(supplier);
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
     * @return 所有供应商信息,并且按照拼音首字母分成了小组
     */
    public static String getAllSupplierWithGroup(String shopId) {
        List<Supplier> allSupplierList = getAllVendor(shopId);
        WSLinkedJSONObject jsonObject = new WSLinkedJSONObject();
        String[] groupNameArray = new String[]{"#","a","b","c","d","e","f","g","h","j","k","l","m","n","o","p","q","r","s","t","u","v","w","x","y","z"};;

        for(int i = 0; i < groupNameArray.length; i++) {
            String groupName = groupNameArray[i];
            JSONArray groupJson = filterGroup(groupName, allSupplierList);
            if(groupJson.length() > 0){
                try {
                    jsonObject.put(groupName, groupJson);
                } catch (JSONException e) {
                    LogHelper.getInstance().e(TAG, "获取供应商分组出错: " + e.getLocalizedMessage());
                }
            }
        }

        // 把所有不是字母开头的元素全放进"#"里
        if (allSupplierList != null && !allSupplierList.isEmpty()){
            JSONArray jsonArray = new JSONArray();
            for (Supplier supplier:allSupplierList){
                WSLinkedJSONObject sharpJsonObject;
                try {
                    sharpJsonObject = new WSLinkedJSONObject(supplier.getJsonString());
                    jsonArray.put(sharpJsonObject);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            if(jsonArray.length() > 0){
                try {
                    jsonObject.put("#", jsonArray);
                } catch (JSONException e) {
                    LogHelper.getInstance().e(TAG, "获取供应商分组出错: " + e.getLocalizedMessage());
                }
            }
        }

        return  jsonObject.toString();
    }

    /**
     * 获取给定组名的所有供应商信息,并封装成JSON类型。
     * @param groupName
     * @param allSupplierList
     * @return 所有给定组的供应商的JSON数组字符串。
     */
    public static JSONArray filterGroup(String groupName, List<Supplier> allSupplierList) {
        JSONArray jsonArray = new JSONArray();
        for (int i = allSupplierList.size() - 1; i >=0; i--) {
            Supplier supplier = allSupplierList.get(i);
            String pinyin = supplier.getPinyin();
            if (!StringUtil.isEmpty(pinyin) && groupName.equals(pinyin.substring(0, 1))) {
                try {
                    WSLinkedJSONObject jsonObject = new WSLinkedJSONObject(supplier.getJsonString());
                    jsonArray.put(jsonObject);
                    allSupplierList.remove(i);
                } catch (JSONException e) {
                    LogHelper.getInstance().e(TAG, "供应商信息转JSON报错: " + supplier.getJsonString());
                    LogHelper.getInstance().e(TAG, "获取供应商分组出错: " + e.getLocalizedMessage());
                }
            }
        }
        return jsonArray;
    }
    
    public static int updateVendor(Supplier supplier, String shopId){
        DataBaseHelper dataBaseHelper = DataBaseHelper.getInstance();

        SQLiteDatabase db = dataBaseHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(SUPPLIER_ID, supplier.getSupplierId());
        values.put(SUPPLIER_CONTACT_NAME, supplier.getContactName());
        values.put(SUPPLIER_SHOP_ID, supplier.getShopId());
        values.put(SUPPLIER_CONTACT_PHONE, supplier.getContactPhone());
        values.put(SUPPLIER_SUPPLIER_NAME, supplier.getSupplierName());
        values.put(SUPPLIER_DESC_MEMO, supplier.getDescMemo());

        values.put(SUPPLIER_BANK_INFO, supplier.getBankInfo());
        values.put(SUPPLIER_ADDRESS, supplier.getAddress());

        values.put(SUPPLIER_PINYIN, supplier.getPinyin());

        values.put(SUPPLIER_INITIALS, supplier.getInitials());


        return db.update(SUPPLIER_TABLE, values, SUPPLIER_ID + "=? and " + SUPPLIER_SHOP_ID + "=?",
                new String[]{supplier.getSupplierId(), shopId});
    }

    public static int removeVendor(String shopId, @Nullable String supplierId) {
        DataBaseHelper dataBaseHelper = DataBaseHelper.getInstance();
        if (dataBaseHelper == null) {
            return 0;
        }
        SQLiteDatabase db = dataBaseHelper.getWritableDatabase();
        return db.delete(SUPPLIER_TABLE, SUPPLIER_ID + "=? and " + SUPPLIER_SHOP_ID + "=?",
                new String[]{supplierId, shopId});
    }

    public static ArrayList querySupplierBySearch(String queryString, String shopId){
        DataBaseHelper dataBaseHelper = DataBaseHelper.getInstance();
        if (dataBaseHelper == null) {
            return null;
        }
        SQLiteDatabase db = dataBaseHelper.getReadableDatabase();

        Cursor cursor = db.query(
                SUPPLIER_TABLE,
                new String[]{KEY_ID, SUPPLIER_ID,
                        SUPPLIER_CONTACT_NAME, SUPPLIER_SHOP_ID, SUPPLIER_CONTACT_PHONE, SUPPLIER_SUPPLIER_NAME,
                        SUPPLIER_DESC_MEMO, SUPPLIER_BANK_INFO, SUPPLIER_ADDRESS, SUPPLIER_PINYIN,
                        SUPPLIER_INITIALS},
                SUPPLIER_SHOP_ID + "=? and (" + SUPPLIER_CONTACT_NAME + " LIKE ? or " +
                        SUPPLIER_SUPPLIER_NAME + " LIKE ? or " + SUPPLIER_PINYIN + " LIKE ? or " +
                        SUPPLIER_INITIALS + " LIKE ?)",
                new String[]{shopId, "%" + queryString + "%", "%" + queryString + "%",
                        "%" + queryString + "%", "%" + queryString + "%"},
                null, null, null, null);


        ArrayList<Supplier> arrayList = new ArrayList<>();
        if (cursor != null && cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                Supplier supplier = new Supplier(
                        cursor.getString(cursor.getColumnIndex(SUPPLIER_ID)),
                        cursor.getString(cursor.getColumnIndex(SUPPLIER_CONTACT_NAME)),
                        cursor.getString(cursor.getColumnIndex(SUPPLIER_SHOP_ID)),
                        cursor.getString(cursor.getColumnIndex(SUPPLIER_CONTACT_PHONE)),
                        cursor.getString(cursor.getColumnIndex(SUPPLIER_SUPPLIER_NAME)),
                        cursor.getString(cursor.getColumnIndex(SUPPLIER_DESC_MEMO)),
                        cursor.getString(cursor.getColumnIndex(SUPPLIER_BANK_INFO)),
                        cursor.getString(cursor.getColumnIndex(SUPPLIER_ADDRESS)),
                        cursor.getString(cursor.getColumnIndex(SUPPLIER_PINYIN)),
                        cursor.getString(cursor.getColumnIndex(SUPPLIER_INITIALS)));
                arrayList.add(supplier);
            }
        }

        if (cursor != null) {
            cursor.close();
        }
        return arrayList;
    }

}
