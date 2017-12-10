package com.honeywell.wholesale.framework.database;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.honeywell.wholesale.framework.model.CartItem;
import com.honeywell.wholesale.framework.model.Category;

import java.util.ArrayList;

/**
 * Created by xiaofei on 7/14/16.
 *
 */
public class CategoryDAO {
    private static final String TAG = "CategoryDAO";
    private static final String CATEGORY_TABLE = "category";

    private static final String KEY_ID = "id";
    private static final String USER_ID = "user_id";

    private static final String CATEGORY_ID = "category_id";
    private static final String CATEGORY = "category";
    private static final String REQUEST_TIME = "request_time";

    public static final String CREATE_CATEGORY_TABLE = "CREATE TABLE IF NOT EXISTS " + CATEGORY_TABLE
            + "("
            + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + USER_ID + " TEXT,"
            + CATEGORY_ID + " TEXT,"
            + CATEGORY + " TEXT"
            + ")";

    public static final String DROP_CATEGORY_TABLE = "DROP TABLE IF EXISTS " + CATEGORY_TABLE;

    public static void addCatyegoryItem(Category category, String userId) {
        DataBaseHelper dataBaseHelper = DataBaseHelper.getInstance();
        if (dataBaseHelper == null) {
            return;
        }
        SQLiteDatabase db = dataBaseHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(USER_ID, userId);
        values.put(CATEGORY_ID, category.getCategoryId());
        values.put(CATEGORY, category.getName());

        long i = db.insert(CATEGORY_TABLE, null, values);
        db.close();
    }

    public static void addCatyegoryItem(String userId, String categoryId, String categoryName) {
        DataBaseHelper dataBaseHelper = DataBaseHelper.getInstance();
        if (dataBaseHelper == null) {
            return;
        }
        SQLiteDatabase db = dataBaseHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(USER_ID, userId);
        values.put(CATEGORY_ID, categoryId);
        values.put(CATEGORY, categoryName);

        long i = db.insert(CATEGORY_TABLE, null, values);
        db.close();
    }


    public static ArrayList<Category> queryCategory(String userId){
        DataBaseHelper dataBaseHelper = DataBaseHelper.getInstance();
        if (dataBaseHelper == null) {
            return null;
        }
        SQLiteDatabase db = dataBaseHelper.getReadableDatabase();

        Cursor cursor = db.query(CATEGORY_TABLE, new String[]{KEY_ID,
                        CATEGORY_ID, CATEGORY, }, USER_ID + "=?",
                new String[]{userId}, null, null, null, null);

        ArrayList<Category> arrayList = new ArrayList<>();

        if (cursor != null && cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                Category category = new Category(
                        cursor.getString(1),
                        cursor.getString(2));

                arrayList.add(category);
            }
        }

        if (cursor != null) {
            cursor.close();
        }
        return arrayList;
    }

    public static void clearCategory(){
        DataBaseHelper dataBaseHelper = DataBaseHelper.getInstance();
        if (dataBaseHelper == null) {
            return ;
        }
        SQLiteDatabase db = dataBaseHelper.getReadableDatabase();

        db.execSQL("delete from "+ CATEGORY_TABLE);
    }
}
