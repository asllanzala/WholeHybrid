package com.honeywell.wholesale.framework.database;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;

import com.honeywell.wholesale.framework.model.Employee;

/**
 * Created by H155935 on 16/5/17.
 * Email: xiaofei.he@honeywell.com
 */
public class EmployeeDAO {
    private static final String TAG = "AccountDAO";

    private static final String EMPLOYEE_TABLE = "employee";

    private static final String KEY_ID = "id";
    //employee table column
    private static final String EMPLOYEE_ID = "employee_id";
    private static final String EMPLOYEE_USERNAME = "username";
    private static final String EMPLOYEE_PASSWORD = "password";
    private static final String EMPLOYEE_NAME = "employee_name";
    private static final String EMPLOYEE_SHOP_ID = "shop_id";
    private static final String EMPLOYEE_ENABLED = "enabled";

    // create employee table string
    public static final String CREATE_EMPLOYEE_TABLE =  "CREATE TABLE IF NOT EXISTS " + EMPLOYEE_TABLE + "("
            + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + EMPLOYEE_ID + " INTEGER,"
            + EMPLOYEE_USERNAME + " TEXT,"
            + EMPLOYEE_PASSWORD + " TEXT,"
            + EMPLOYEE_NAME + " TEXT,"
            + EMPLOYEE_SHOP_ID + " INTEGER,"
            + EMPLOYEE_ENABLED + " INTEGER"
            + ")";

    private SQLiteDatabase db;

    // add account
    public static void addEmployee(Employee employee) {
        DataBaseHelper dataBaseHelper = DataBaseHelper.getInstance();
        if (dataBaseHelper == null){
            return;
        }
        SQLiteDatabase db = dataBaseHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(EMPLOYEE_ID, employee.getEmployeeId());
        // Inserting Row
        db.insert(EMPLOYEE_TABLE, null, values);
        db.close();
    }
}
