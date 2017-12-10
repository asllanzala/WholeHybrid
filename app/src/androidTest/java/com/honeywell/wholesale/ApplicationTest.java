package com.honeywell.wholesale;

import android.app.Application;
import android.test.ApplicationTestCase;

import com.honeywell.wholesale.framework.database.AccountDAO;
import com.honeywell.wholesale.framework.http.WebClient;
import com.honeywell.wholesale.ui.IOUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.CountDownLatch;

/**
 * <a href="http://d.android.com/tools/testing/testing_android.html">Testing Fundamentals</a>
 */
public class ApplicationTest extends ApplicationTestCase<Application> {
    CountDownLatch signal = null;
    public ApplicationTest() {
        super(Application.class);
    }

    @Override
    protected void setUp() throws Exception {
        createApplication();
        signal = new CountDownLatch(1);
        super.setUp();
    }

    public void testHttpRegisterRequest() throws JSONException {

        WebClient webClient = new WebClient();
//        String url = "http://115.159.152.188:80/reg/boss/register";
        String url = "http://115.159.152.188:80/shop/add";
//        String url = "http://115.159.152.188:80/shop/query";

//        String url = "http://159.99.93.181:8099/reg/boss/register";
//        String url = "http://159.99.93.181:8099/reg/boss/login";
//        String url = "http://159.99.93.181:8099/shop/query";
        JSONObject jsonObject = new JSONObject();
//        jsonObject.put("phone", "18114456661");
//        jsonObject.put("password", "123456");
//        jsonObject.put("company_account", "18114456666");
        jsonObject.put("shop_name", "18114476197");
        jsonObject.put("shop_address", "1234567");
        jsonObject.put("contact_name", "1234567");
        jsonObject.put("contact_phone", "18111444123");
        jsonObject.put("boss_phone", "18114456197");
//        webClient.httpsJsonRequest(url, jsonObject, "");
//        webClient.httpsJsonRequest(url, jsonObject, "77473e7ef01d4149bf271331b0721958");
        try {
            signal.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void testAccountDB() {

    }



    public void testUploadFile(){
        String path = "/storage/emulated/0/Pictures/Screenshots/Screenshot_2016-05-25-17-01-01.png";
        WebClient webClient = new WebClient();

        ArrayList<String> paths = new ArrayList<>();
        paths.add(path);
//        String url = "http://159.99.93.181:8099/pic/upload";
        String url = "http://159.99.93.181:8099/pic/upload";
        try {
//            webClient.httpUpload(url, paths, "123", "6676");
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            signal.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void testUploadCustomFile() throws Exception{

        final String STRING_PART_PRODUCT_ID = "product_code";
        final String STRING_PART_SHOP_ID = "shop_id";

        String path = "/storage/emulated/0/Pictures/Screenshots/Screenshot_2016-05-25-17-01-01.png";
        ArrayList<String> paths = new ArrayList<>();
        paths.add(path);

        WebClient webClient = new WebClient();
        HashMap<String, String> map = new HashMap<>();

        map.put(STRING_PART_SHOP_ID, "6676");
        map.put(STRING_PART_PRODUCT_ID, "123");

        String url = "http://159.99.93.181:8099/pic/upload";
        webClient.customHttpUpload(url, paths, null, map);

        try {
            signal.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}