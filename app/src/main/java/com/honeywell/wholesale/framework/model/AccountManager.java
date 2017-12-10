package com.honeywell.wholesale.framework.model;

import com.honeywell.wholesale.framework.database.AccountDAO;
import com.honeywell.wholesale.framework.http.WebClient;
import com.honeywell.wholesale.lib.util.StringUtil;

import org.json.JSONArray;

import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by H155935 on 16/6/22.
 * Email: xiaofei.he@honeywell.com
 */
public class AccountManager {
    private static final String TAG = AccountManager.class.getSimpleName();

    private static AccountManager mAccountManager;
    private Account mCurrentAccount;

    private AccountManager(){
        mCurrentAccount = getRecentlyAccountWithAppCrash();
    }

    public static synchronized AccountManager getInstance() {
        if(mAccountManager == null) {
            mAccountManager = new AccountManager();
        }
        return mAccountManager;
    }

    public static boolean isExist(String companyAccount, String loginName){
        Account account = AccountDAO.getAccount(companyAccount, loginName);
        if (account == null){
            return false;
        }
        return true;
    }

    public static Account getAccountWithloginName(String companyAccount, String loginName){
        Account account = AccountDAO.getAccount(companyAccount, loginName);
        if (account == null){
            return null;
        }
        return account;
    }

    public String getAllAccountJson(){
        JSONArray jsonArray = new JSONArray();
        List<Account> accountList = getAllAccount();
        for(int i = 0; i < accountList.size(); i++) {
            jsonArray.put(accountList.get(i).getJsonStringWithPassword());
        }
        return jsonArray.toString();
    }

    public List<Account> getAllAccount(){
        ArrayList<Account> arrayList = AccountDAO.getAll();
        if (arrayList != null){
            Collections.sort(arrayList, new AccountComparator());
        }
        return arrayList;
    }

    /**
     * 更新的到数据库里。
     *
     * @param account
     */
    public void updateAccount(Account account){
        updateAccount(account.getCompanyAccount(), account.getEmployeeId(), account.getLoginName(), account.getPassword(), account.getRole(),
                account.getToken(), account.getCurrentShopId(), account.getShopName(), account.getAllShops(), account.getUserName());
    }

    /**
     * 更新的到数据库里。
     *
     * @param companyAccount
     * @param loginName
     * @param password
     * @param role
     * @param token
     * @param shopId
     * @param shopName
     */
    public void updateAccount(
            String companyAccount, String employeeID, String loginName, String password,
            String role, String token, String shopId,
            String shopName, String allShops, String username){

        if (isExist(companyAccount, loginName)){
            AccountDAO.updateAccount(companyAccount, employeeID, loginName, password, role, token, shopId, shopName, allShops, username);
        }else{
            AccountDAO.addAccount(companyAccount, employeeID, loginName, password, role, token, shopId, shopName, allShops, username);
        }

        mCurrentAccount = new Account(companyAccount, employeeID, loginName, password, role, token, shopId, shopName, allShops, username);
    }

    public void logout(){
        if (mCurrentAccount != null){
            String companyAccount = mCurrentAccount.getCompanyAccount();
            String employeeID = mCurrentAccount.getEmployeeId();
            String loginName = mCurrentAccount.getLoginName();
            String password = mCurrentAccount.getPassword();
            String role = mCurrentAccount.getRole();
            String token = "";
            String shopId = mCurrentAccount.getCurrentShopId();
            String shopName = mCurrentAccount.getShopName();
            String allShops = mCurrentAccount.getAllShops();
            String username = mCurrentAccount.getUserName();
            updateAccount(companyAccount, employeeID, loginName, password, role, token, shopId, shopName,allShops, username);
        }

        WebClient webClient = new WebClient();
        webClient.logout();
        mCurrentAccount = null;
    }

    public boolean isLogin() {
        return (getCurrentAccount() != null && !StringUtil.isEmpty(mCurrentAccount.getToken()));
    }

    // 获取之前登录的用户,由于App异常退出,没有登出
    public Account getRecentlyAccountWithAppCrash(){
        Account account = AccountDAO.getAccountForNonTokeClean();

        if (account != null && mCurrentAccount == null){
            mCurrentAccount = account;
        }
        return mCurrentAccount;
    }

    public Account getCurrentAccount() {
        if(mCurrentAccount != null) {
            return mCurrentAccount;
        } else {
            return getRecentlyAccountWithAppCrash();
        }
    }

    public Account getAccount(String companyAccount, String loginName) {
        return AccountDAO.getAccount(companyAccount, loginName);
    }

    public void clearAllAccounts() {
        AccountDAO.clearAllAccounts();
    }

    class AccountComparator implements Comparator<Account> {
        @Override
        public int compare(Account account1, Account account2) {
            if (account1.getUpdateTime().compareTo(account2.getUpdateTime()) > 0){
                return -1;
            }

            if (account1.getUpdateTime().compareTo(account2.getUpdateTime()) < 0){
                return 1;
            }

            return account1.getUpdateTime().compareTo(account2.getUpdateTime());
        }
    }

    public String getCurrentShopId() {
        return mCurrentAccount.getCurrentShopId();
    }

    public void setCurrentShopId(String currentShopId) {
        mCurrentAccount.setCurrentShopId(currentShopId);
    }
}
