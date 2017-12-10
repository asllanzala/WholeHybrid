package com.honeywell.wholesale;

import android.app.Application;
import android.test.ApplicationTestCase;
import android.util.Log;


import com.honeywell.wholesale.framework.model.CartItem;

import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;

/**
 * Created by H155935 on 16/6/21.
 * Email: xiaofei.he@honeywell.com
 */
public class CartTest extends ApplicationTestCase<Application> {
    CountDownLatch signal = null;
    public CartTest() {
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

//    public void testCartAdd() throws Exception {
//        CartItem cartItem = new CartItem("userId", "customId", "productId", "productName", "unitPrice", "number", "url");
//        cartItem.addToCart();
//
//        ArrayList arrayList = CartItem.getAllCarts("customId");
//        assertTrue(arrayList.size() == 1);
//        CartItem cartItem1 = (CartItem) arrayList.get(0);
//        assertTrue(cartItem1.getLoginName().equals("userId"));
//    }
//
//    public void testRemoveCartItem() throws Exception {
//        CartItem cartItem = new CartItem("userId", "customId", "productId", "productName", "unitPrice", "number", "url");
//        cartItem.addToCart();
//
//        CartItem.removeCartItem("customId", "productId");
//
//        ArrayList arrayList = CartItem.getAllCarts("customId");
//        assertTrue(arrayList.size() == 0);
//    }

//    public void testRemoveCart() throws Exception {
//        CartItem cartItem = new CartItem("userId", "customId", "productId", "productName", "unitPrice", "number", "url");
//        CartItem cartItem1 = new CartItem("userId", "customId", "productId1", "productName1", "unitPrice1", "number1", "url1");
//        cartItem.addToCart();
//        cartItem1.addToCart();
//
//        Log.e("123123", "123123123");
//
//        ArrayList arrayList1 = CartItem.getAllCarts("customId");
//        assertTrue(arrayList1.size() == 2);
//
//        CartItem.removeCart("customId");
//
//        ArrayList arrayList = CartItem.getAllCarts("customId");
//        assertTrue(arrayList.size() == 0);
//    }
}
