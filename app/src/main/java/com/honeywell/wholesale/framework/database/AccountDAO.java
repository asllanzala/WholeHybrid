package com.honeywell.wholesale.framework.database;

import com.honeywell.wholesale.framework.model.Account;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.ArrayList;

/**
 * Created by H155935 on 16/5/17.
 * Email: xiaofei.he@honeywell.com
 */
public class AccountDAO {
    private static final String TAG = "AccountDAO";

    private static final String ACCOUNT_TABLE = "account";

    private static final String KEY_ID = "id";

    // CurrentUser table column
    private static final String COMPANY_ACCOUNT = "company_account";
    private static final String EMPLOYEE_ID = "employee_id";
    private static final String LOGIN_NAME = "login_name";
    private static final String ACCOUNT_PASSWORD = "user_password";
    private static final String ACCOUNT_ROLE = "account_role";
    private static final String ACCOUNT_TOKEN = "token";

    private static final String SHOP_ID = "shop_id";
    private static final String SHOP_NAME = "shop_name";

    private static final String ALL_SHOPS = "all_shops";
    private static final String USER_NAME = "user_name";
    private static final String UPDATE_TIME = "update_time";

    public static final String CREATE_ACCOUNT_TABLE =  "CREATE TABLE IF NOT EXISTS " + ACCOUNT_TABLE + "("
            + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + COMPANY_ACCOUNT + " TEXT,"
            + EMPLOYEE_ID + " TEXT unique,"
            + LOGIN_NAME + " TEXT,"
            + ACCOUNT_PASSWORD + " TEXT,"
            + ACCOUNT_ROLE + " TEXT,"
            + ACCOUNT_TOKEN + " TEXT,"

            + SHOP_ID + " TEXT,"
            + SHOP_NAME + " TEXT,"
            + ALL_SHOPS + " TEXT,"
            + USER_NAME + " TEXT,"
            + UPDATE_TIME + " TEXT"
            + ")";

    public static final String DROP_ACCOUNT_TABLE = "DROP TABLE IF EXISTS " + ACCOUNT_TABLE;
    // add account
    public static void addAccount(
            String companyAccount, String employeeId, String loginName, String password,
            String role, String token, @NonNull String shopId,
            @NonNull String shopName, @Nullable String allShops, @Nullable String username) {
        DataBaseHelper dataBaseHelper = DataBaseHelper.getInstance();
        if (dataBaseHelper == null){
            return;
        }
        SQLiteDatabase db = dataBaseHelper.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(COMPANY_ACCOUNT, companyAccount);
        values.put(EMPLOYEE_ID, employeeId);
        values.put(LOGIN_NAME, loginName);
        values.put(ACCOUNT_PASSWORD, password);
        values.put(ACCOUNT_ROLE, role);
        values.put(ACCOUNT_TOKEN, token);

        values.put(SHOP_ID, shopId);
        values.put(SHOP_NAME, shopName);
        values.put(ALL_SHOPS, allShops);

        values.put(USER_NAME, username);

        long timeSeconds = System.currentTimeMillis();
        String time = String.valueOf(timeSeconds);
        values.put(UPDATE_TIME, time);
        // Inserting Row
        db.insert(ACCOUNT_TABLE, null, values);
        db.close();
    }

    // get account by id
    public static Account getAccount(String companyAccount, String loginName) {
        DataBaseHelper dataBaseHelper = DataBaseHelper.getInstance();
        if (dataBaseHelper == null){
            return null;
        }
        SQLiteDatabase db = dataBaseHelper.getReadableDatabase();

        Cursor cursor = db.query(ACCOUNT_TABLE, new String[] { KEY_ID, COMPANY_ACCOUNT, EMPLOYEE_ID,
                        LOGIN_NAME, ACCOUNT_PASSWORD, ACCOUNT_ROLE, ACCOUNT_TOKEN, SHOP_ID, SHOP_NAME, USER_NAME, UPDATE_TIME, ALL_SHOPS}, COMPANY_ACCOUNT + "=? and " + LOGIN_NAME + "=?",
                new String[] {String.valueOf(companyAccount), String.valueOf(loginName) }, null, null, null, null);

        Account account = null;
        if (cursor != null && cursor.getCount()>0){
            cursor.moveToFirst();
            account = new Account(
                    cursor.getString(cursor.getColumnIndex(COMPANY_ACCOUNT)),
                    cursor.getString(cursor.getColumnIndex(EMPLOYEE_ID)),
                    cursor.getString(cursor.getColumnIndex(LOGIN_NAME)),
                    cursor.getString(cursor.getColumnIndex(ACCOUNT_PASSWORD)),
                    cursor.getString(cursor.getColumnIndex(ACCOUNT_ROLE)),
                    cursor.getString(cursor.getColumnIndex(ACCOUNT_TOKEN)),
                    cursor.getString(cursor.getColumnIndex(SHOP_ID)),
                    cursor.getString(cursor.getColumnIndex(SHOP_NAME)),
                    cursor.getString(cursor.getColumnIndex(ALL_SHOPS)),
                    cursor.getString(cursor.getColumnIndex(USER_NAME)));

            account.setUpdateTime(cursor.getString(cursor.getColumnIndex(UPDATE_TIME)));
        }

        if (cursor != null){
            cursor.close();
        }
        return account;
    }

    // 获取之前登录的用户,由于App异常退出,没有登出
    public static Account getAccountForNonTokeClean(){
        DataBaseHelper dataBaseHelper = DataBaseHelper.getInstance();
        if (dataBaseHelper == null){
            return null;
        }
        SQLiteDatabase db = dataBaseHelper.getReadableDatabase();
        Cursor cursor = db.query(ACCOUNT_TABLE, new String[] { KEY_ID, COMPANY_ACCOUNT,
                        EMPLOYEE_ID, LOGIN_NAME, ACCOUNT_PASSWORD, ACCOUNT_ROLE,
                ACCOUNT_TOKEN, SHOP_ID, SHOP_NAME, USER_NAME, UPDATE_TIME, ALL_SHOPS}, ACCOUNT_TOKEN + "!=?",
                new String[] {""}, null, null, null, null);

        Account account = null;

        if (cursor != null && cursor.getCount()>0){
            cursor.moveToFirst();
            account = new Account(
                    cursor.getString(cursor.getColumnIndex(COMPANY_ACCOUNT)),
                    cursor.getString(cursor.getColumnIndex(EMPLOYEE_ID)),
                    cursor.getString(cursor.getColumnIndex(LOGIN_NAME)),
                    cursor.getString(cursor.getColumnIndex(ACCOUNT_PASSWORD)),
                    cursor.getString(cursor.getColumnIndex(ACCOUNT_ROLE)),
                    cursor.getString(cursor.getColumnIndex(ACCOUNT_TOKEN)),
                    cursor.getString(cursor.getColumnIndex(SHOP_ID)),
                    cursor.getString(cursor.getColumnIndex(SHOP_NAME)),
                    cursor.getString(cursor.getColumnIndex(ALL_SHOPS)),
                    cursor.getString(cursor.getColumnIndex(USER_NAME)));

            account.setUpdateTime(cursor.getString(cursor.getColumnIndex(UPDATE_TIME)));
        }

        if (cursor != null){
            cursor.close();
        }
        return account;
    }

    // update account token
    public static int updateAccount(
            String companyAccount, String employeeId, String loginName, String password,
            String role, String token, String shopId, String shopName, String allShops, String username){
        DataBaseHelper dataBaseHelper = DataBaseHelper.getInstance();

        ContentValues args = new ContentValues();

        args.put(COMPANY_ACCOUNT, companyAccount);
        args.put(ACCOUNT_PASSWORD, password);
        args.put(ACCOUNT_ROLE, role);
        args.put(ACCOUNT_TOKEN, token);

        args.put(SHOP_ID, shopId);
        args.put(SHOP_NAME, shopName);
        args.put(USER_NAME, username);

        long timeSeconds = System.currentTimeMillis();
        String time = String.valueOf(timeSeconds);
        args.put(UPDATE_TIME, time);

        args.put(ALL_SHOPS, allShops);

        SQLiteDatabase db = dataBaseHelper.getWritableDatabase();

        return db.update(ACCOUNT_TABLE, args, EMPLOYEE_ID + "= ?", new String[]{employeeId});
    }



    // update account token
    public static int updateAccount(Account account){
        DataBaseHelper dataBaseHelper = DataBaseHelper.getInstance();

        ContentValues args = new ContentValues();

        args.put(COMPANY_ACCOUNT, account.getCompanyAccount());
        args.put(ACCOUNT_PASSWORD, account.getPassword());
        args.put(ACCOUNT_ROLE, account.getRole());
        args.put(ACCOUNT_TOKEN, account.getToken());

        args.put(SHOP_ID, account.getCurrentShopId());
        args.put(SHOP_NAME, account.getShopName());
        args.put(USER_NAME, account.getUserName());

        long timeSeconds = System.currentTimeMillis();
        String time = String.valueOf(timeSeconds);
        args.put(UPDATE_TIME, time);

        args.put(ALL_SHOPS, account.getAllShops());

        SQLiteDatabase db = dataBaseHelper.getWritableDatabase();

        return db.update(ACCOUNT_TABLE, args, EMPLOYEE_ID + "= ?", new String[]{account.getEmployeeId()});
    }

    public static void deleteAccount(String companyAccount, String loginName) {

        DataBaseHelper dataBaseHelper = DataBaseHelper.getInstance();
        if (dataBaseHelper == null){
            return;
        }
        SQLiteDatabase db = dataBaseHelper.getWritableDatabase();
        db.delete(ACCOUNT_TABLE, LOGIN_NAME + "=? AND " + COMPANY_ACCOUNT + "=?", new String[]{loginName, companyAccount});
        db.close();
    }

    public static void clearAllAccounts() {
        DataBaseHelper dataBaseHelper = DataBaseHelper.getInstance();
        if (dataBaseHelper == null){
            return;
        }
        SQLiteDatabase db = dataBaseHelper.getWritableDatabase();

        db.delete(ACCOUNT_TABLE, null, null);
        db.close();
    }

    public static ArrayList<Account> getAll() {
        DataBaseHelper dataBaseHelper = DataBaseHelper.getInstance();
        if (dataBaseHelper == null){
            return null;
        }

        SQLiteDatabase db = dataBaseHelper.getReadableDatabase();
        ArrayList<Account> arrayList = new ArrayList<>();

        Cursor cursor = db.query(ACCOUNT_TABLE, new String[] {KEY_ID, COMPANY_ACCOUNT, EMPLOYEE_ID,
                LOGIN_NAME, ACCOUNT_PASSWORD, ACCOUNT_ROLE, ACCOUNT_TOKEN, SHOP_ID, SHOP_NAME, USER_NAME, UPDATE_TIME, ALL_SHOPS}, null, null, null, null, null);
        while (cursor.moveToNext()) {
            Account  account = new Account(
                    cursor.getString(cursor.getColumnIndex(COMPANY_ACCOUNT)),
                    cursor.getString(cursor.getColumnIndex(EMPLOYEE_ID)),
                    cursor.getString(cursor.getColumnIndex(LOGIN_NAME)),
                    cursor.getString(cursor.getColumnIndex(ACCOUNT_PASSWORD)),
                    cursor.getString(cursor.getColumnIndex(ACCOUNT_ROLE)),
                    cursor.getString(cursor.getColumnIndex(ACCOUNT_TOKEN)),
                    cursor.getString(cursor.getColumnIndex(SHOP_ID)),
                    cursor.getString(cursor.getColumnIndex(SHOP_NAME)),
                    cursor.getString(cursor.getColumnIndex(ALL_SHOPS)),
                    cursor.getString(cursor.getColumnIndex(USER_NAME)));

            account.setUpdateTime(cursor.getString(cursor.getColumnIndex(UPDATE_TIME)));
            arrayList.add(account);
        }
        cursor.close();

        return arrayList;
    }

}
