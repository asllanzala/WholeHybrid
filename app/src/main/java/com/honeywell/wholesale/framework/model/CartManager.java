package com.honeywell.wholesale.framework.model;

import com.honeywell.hybridapp.framework.log.LogHelper;
import com.honeywell.wholesale.framework.database.CartDAO;

import org.json.JSONArray;

import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by e887272 on 6/29/16.
 */
public class CartManager {

    public static final String TAG = CartManager.class.getSimpleName();

    private static CartManager mCartManager = null;

    private CartManager() {
    }

    public static synchronized CartManager getInstance() {
        if (mCartManager == null) {
            mCartManager = new CartManager();
        }
        return mCartManager;
    }

    public void addToCart(ArrayList<CartItem> cartList) {
        if (cartList == null || cartList.size() == 0) {
            return;
        }

        for (int i = 0; i < cartList.size(); i++) {
            addToCart(cartList.get(i));
        }
    }

    public void addToCart(CartItem cartItem) {
        // TODO may be need combine the same cartItem
        String customerId = cartItem.getCustomerId();
        String employId = cartItem.getEmployeeId();
        int productId = cartItem.getProductId();

        ArrayList<CartItem> arrayList = CartDAO.getCartItems(customerId, employId, productId);

        for (CartItem item: arrayList) {
            String price = item.getUnitPrice();
            if (price.equals(cartItem.getUnitPrice())){
                // update cart item
                int count = 0;
                int newCardCount = 0;
                try{
                    count = Integer.valueOf(item.getTotalNumber());
                    newCardCount = Integer.valueOf(cartItem.getTotalNumber());
                }catch (Exception e){
                    LogHelper.getInstance().e(TAG, "add to cart string to float error");
                    e.printStackTrace();
                    return;
                }

                String s = String.valueOf(count + newCardCount);
                item.setTotalNumber(s);
                CartDAO.updateCartItemCount(item);
                return;
            }
        }

        CartDAO.addCartItem(cartItem);
    }

    /**
     * 更新购物篮CartItmen
     * @param cartItem
     */
    public void updateCart(CartItem cartItem) {
        // TODO may be need combine the same cartItem
        String customerId = cartItem.getCustomerId();
        String employId = cartItem.getEmployeeId();
        int productId = cartItem.getProductId();

        ArrayList<CartItem> arrayList = CartDAO.getCartItems(customerId, employId, productId);

        for (CartItem item: arrayList) {
            String price = item.getUnitPrice();
            if (price.equals(cartItem.getUnitPrice())){
                // update cart item
                int count = 0;
                int newCardCount = 0;
                try{
                    count = Integer.valueOf(item.getTotalNumber());
                    newCardCount = Integer.valueOf(cartItem.getTotalNumber());
                }catch (Exception e){
                    LogHelper.getInstance().e(TAG, "add to cart string to float error");
                    e.printStackTrace();
                    return;
                }

                String s = String.valueOf(count + newCardCount);
                item.setTotalNumber(s);
                CartDAO.updateCartItemCount(item);
                return;
            }
        }

        CartDAO.updateCartItem(cartItem);
    }

    /**
     *
     * @param oldCustomerID 0表示散客
     */
    public void updateCustomerInfo(String shopId, String employeeID, String oldCustomerID, String newCustomerId, String customerName,
            String contactName, String contactPhone, String customerNotes, String invoiceTitle, String address) {
        CartDAO.updateCustomerInfo(shopId, employeeID, oldCustomerID, newCustomerId, customerName,
                contactName, contactPhone, customerNotes, invoiceTitle, address);
    }

    public Long getAllCartsCount(String shopId, String employeeID) {
        return CartDAO.getCartItemsCount(shopId, employeeID);
    }

    public ArrayList<CartItem> getAllCarts(String shopId, String employeeID) {
        ArrayList arrayList = CartDAO.getAllCartItems(shopId, employeeID);
        return arrayList;
    }

    /**
     * 把所有数据转换成json
     * @return
     */
    public String getCartItemsJsonString(String shopId, String employeeID) {
        return listToJsonString(getAllCarts(shopId, employeeID));
    }

    private String listToJsonString(ArrayList<CartItem> cartList) {
        if (cartList == null || cartList.size() == 0) {
            return "[]";
        }

        // 先把数据按照customer ID归类
        CartItem oneProduct = null;
        String customerID = "";
        List<CartItem> oneCustomerCartList = null;
        Map<String, List<CartItem>> customersCartMap = new HashMap<String, List<CartItem>>();

        for (int i = 0; i < cartList.size(); i++) {
            oneProduct = cartList.get(i);
            customerID = oneProduct.getCustomerId();

            oneCustomerCartList = customersCartMap.get(customerID);
            if(oneCustomerCartList == null) {
                oneCustomerCartList = new ArrayList<CartItem>();
                customersCartMap.put(customerID, oneCustomerCartList);
            }

            oneCustomerCartList.add(oneProduct);
        }

        // 讲归类好的购物车数据转换成json
        JSONArray allCustomersCartJsonArray = new JSONArray();

        //遍历map中的键
        for (String customerIdKey : customersCartMap.keySet()) {
            JSONArray oneCustomerCartJsonArray = new JSONArray();
            // 获取到客户的所有购物车列表
            oneCustomerCartList = customersCartMap.get(customerIdKey);
            if(oneCustomerCartList != null) {
                // 转Json
                for (int i = 0; i < oneCustomerCartList.size(); i++) {
                    oneProduct = oneCustomerCartList.get(i);
                    oneCustomerCartJsonArray.put(oneProduct.toJsonObject());
                }
            }
            allCustomersCartJsonArray.put(oneCustomerCartJsonArray);
        }
        // TODO need redo;
        return allCustomersCartJsonArray.toString();
    }

    public boolean removeCartItem(String employeeId, String customId, int productId) {
        int rows = CartDAO.removeCartItem(employeeId, customId, productId);
        if (rows > 1) {
            Log.e(TAG, "remove cart item more than 1 row");
            return false;
        }

        if (rows == 0) {
            Log.e(TAG, "remove cart item with some error");
            return false;
        }
        return true;
    }

    public boolean removeCart(String employeeId, String customId) {
        int rows = CartDAO.removeCart(employeeId, customId);
        if (rows > 0) {
            return true;
        }
        return false;
    }

    public boolean removeAllCarts() {
        int rows = CartDAO.removeAllCart();
        if (rows > 0) {
            return true;
        }
        return false;
    }


}
