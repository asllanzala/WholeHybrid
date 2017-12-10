package com.honeywell.wholesale;

import android.app.Application;
import android.test.ApplicationTestCase;
import android.util.Log;

import com.honeywell.wholesale.framework.model.Account;
import com.honeywell.wholesale.framework.model.AccountManager;

import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * Created by H155935 on 16/6/22.
 * Email: xiaofei.he@honeywell.com
 */
public class AccountTest extends ApplicationTestCase<Application> {
    CountDownLatch signal = null;
    public AccountTest() {
        super(Application.class);
    }

    @Override
    public void setUp() throws Exception {
        createApplication();
        signal = new CountDownLatch(1);
        super.setUp();
    }

    @Override
    public void tearDown() throws Exception {
        super.tearDown();
    }

    public void testAccountAdd() throws Exception{
//        AccountManager accountManager = AccountManager.getInstance();
//        accountManager.updateAccount("companyId", "userId", "pawword", "1", "token", "shopid", "shopname", "allshops", "44444");
//
//        accountManager.updateAccount("companyId", "userId2", "pawword", "1", "token", "shopid", "shopname", "allshops123", "321");
//        assertTrue(AccountManager.isExist("userId"));
//        assertTrue(AccountManager.isExist("userId2"));
    }

    public void testAccountUpdate() throws Exception{
//        AccountManager accountManager = AccountManager.getInstance();
//        accountManager.updateAccount("company2", "userId", "pawword1", "1", "token", "shopid1", "shopname", "allshops321", "123");
//
//        Account account = AccountManager.getAccountWithUserId("userId");
//        assertTrue(account != null);
//        assertTrue(account.getCompanyAccount().equals("company2"));
//        assertTrue(account.getLoginName().equals("userId"));
//        assertTrue(account.getPassword().equals("pawword1"));
//        assertTrue(account.getCurrentShopId().equals("shopid1"));
//        assertTrue(account.getShopName().equals("shopname"));
//        assertTrue(account.getAllShops().equals("allshops321"));
//        assertTrue(account.getUserName().equals("123"));
    }

    public void testAccountList() throws Exception{
        AccountManager accountManager = AccountManager.getInstance();
        accountManager.clearAllAccounts();

        accountManager.updateAccount("company2", "employeeID1", "userId2", "pawword1", "1", "token", "shopid1", "shopname", "allshops1", "username1");
        accountManager.updateAccount("company3", "employeeID2", "userId3", "pawword1", "1", "token", "shopid1", "shopname", "allshops2", "username2");
        accountManager.updateAccount("company4", "employeeID3", "userId4", "pawword1", "1", "token", "shopid1", "shopname", "allshops3", "username3");
        accountManager.updateAccount("company5", "employeeID4", "userId5", "pawword1", "1", "token", "shopid1", "shopname", "allshops4", "username4");
        accountManager.updateAccount("company6", "employeeID5", "userId6", "pawword1", "1", "token", "shopid1", "shopname", "allshops5", "username5");
        accountManager.updateAccount("companyAccount", "employeeID6", "userId", "pawword", "1", "token", "shopid", "shopname", "allshops6", "username6");
        accountManager.updateAccount("company2", "employeeID7", "userId2", "pawword1", "1", "token", "shopid1", "shopname", "allshops7", "username7");

        List<Account> accountArrayList = accountManager.getAllAccount();

        assertEquals(7, accountArrayList.size());

        for (Account account: accountArrayList){
            String userId = account.getLoginName();
            String allShop = account.getAllShops();
            Log.v("MZ", account.getJsonString());
        }
    }
}
