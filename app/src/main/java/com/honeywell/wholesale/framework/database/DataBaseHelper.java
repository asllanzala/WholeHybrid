package com.honeywell.wholesale.framework.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


/**
 * Created by H155935 on 16/5/11.
 * Email: xiaofei.he@honeywell.com
 */
public class DataBaseHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "WholeSale";
    public static final int DATABASE_VERSION = 8;

    private Context context;
    private static DataBaseHelper instance = null;
    private static SQLiteDatabase database;

    private DataBaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    public static DataBaseHelper getInstance(Context context) {
        if (instance == null) {
            instance = new DataBaseHelper(context.getApplicationContext());
        }
        return instance;
    }

    public static DataBaseHelper getInstance(){
        return instance;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL(AccountDAO.CREATE_ACCOUNT_TABLE);
        db.execSQL(CartDAO.CREATE_CART_TABLE);
        db.execSQL(CategoryDAO.CREATE_CATEGORY_TABLE);
        db.execSQL(ShopDAO.CREATE_SHOP_TABLE);
        db.execSQL(IncrementalDAO.CREATE_INCREMENTAL_TABLE);
        db.execSQL(InventoryDAO.CREATE_INVENTORY_TABLE);
        db.execSQL(OrderDAO.CREATE_ORDER_TABLE);
        db.execSQL(StockDAO.CREATE_STOCK_TABLE);
        db.execSQL(SupplierDAO.CREATE_SUPPLIER_TABLE);
        db.execSQL(CustomerDAO.CREATE_CUSTOMER_TABLE);
        db.execSQL(CartRefSKUDao.CREATE_CART_REF_CUSTOMER_TABLE);
        db.execSQL(CartRefCustomerDao.CREATE_CART_TABLE);


    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 8){
            // upgrade db version 1-2
            db.execSQL(InventoryDAO.DROP_INVENTORY_TABLE);
            db.execSQL(CustomerDAO.DROP_CUSTOMER_TABLE);
            db.execSQL(SupplierDAO.DROP_SUPPLIER_TABLE);
            db.execSQL(IncrementalDAO.DROP_INCREMENTAL_TABLE);
            db.execSQL(CartDAO.DROP_CART_TABLE);
            db.execSQL(CategoryDAO.DROP_CATEGORY_TABLE);
            db.execSQL(StockDAO.DROP_STOCK_TABLE);
            db.execSQL(CartRefCustomerDao.DROP_CART_TABLE);
            db.execSQL(CartRefSKUDao.DROP_CART_TABLE);


            db.execSQL(InventoryDAO.CREATE_INVENTORY_TABLE);
            db.execSQL(CustomerDAO.CREATE_CUSTOMER_TABLE);
            db.execSQL(SupplierDAO.CREATE_SUPPLIER_TABLE);
            db.execSQL(IncrementalDAO.CREATE_INCREMENTAL_TABLE);
            db.execSQL(CartDAO.CREATE_CART_TABLE);
            db.execSQL(CategoryDAO.CREATE_CATEGORY_TABLE);
            db.execSQL(StockDAO.CREATE_STOCK_TABLE);
            db.execSQL(CartRefSKUDao.CREATE_CART_REF_CUSTOMER_TABLE);
            db.execSQL(CartRefCustomerDao.CREATE_CART_TABLE);
        }
    }
}
