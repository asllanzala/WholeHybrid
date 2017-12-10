package com.honeywell.wholesale.framework.utils;

import android.content.Context;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.honeywell.wholesale.framework.model.ListItem;
import com.honeywell.wholesale.framework.model.OrderBean;
import com.honeywell.wholesale.framework.model.ProductChildItem;
import com.honeywell.wholesale.framework.model.ProductItem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhujunyu on 16/9/20.
 */
public class FileUtil {


    public static String CONFIG_PATH = "PRINTER-CONFIG";
    public static String CONFIG_SAVE_PATH = "PRINTER-SAVE-CONFIG";
    public static String PRINTER_SCRIPT = "PRINTER_SCRIPT";


    public static String setFolderPath(Context context, String folderName) {
        String folderPath = null;
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {// save in SD card first
            folderPath = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + folderName;
        } else {
            folderPath = context.getFilesDir().getAbsolutePath() + File.separator + folderName + File.separator;
        }
        File folder = new File(folderPath);
        if (!folder.exists()) {
            boolean ism = folder.mkdirs();
            Log.e("&&&&&&1&&&&&&&", "ism:" + ism);
        }
        if (!folder.isDirectory()) {
            Toast.makeText(context, "文件目录错误", Toast.LENGTH_SHORT).show();
            return null;
        }
        return folderPath;
    }
//
//    public static void saveStringToFile(Context context, String source) {
//        String path = setFolderPath(context, CONFIG_PATH);
//        if (TextUtils.isEmpty(path)) {
//            return;
//        }
//        try {
//            File file = new File(path + File.separator + "config.txt");
//            if (file.exists()) {
//                //存在就删除
//                file.delete();
//                file.createNewFile();
//            } else {
//                file.createNewFile();
//            }
//
//            FileOutputStream outStream = new FileOutputStream(file);
//            outStream.write(source.getBytes());
//            outStream.close();
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }

//    public static String getSourceFromFile(Context context, String fileName) {
//        StringBuffer sb = new StringBuffer();
//        String path = setFolderPath(context, CONFIG_SAVE_PATH);
//        if (TextUtils.isEmpty(path)) {
//            Toast.makeText(context, "暂无配置信息", Toast.LENGTH_SHORT).show();
//            return "";
//        }
//        try {
//            File file = new File(path + File.separator + fileName);
//            if (!file.exists()) {
//                //存在就删除
//                Toast.makeText(context, "暂无配置信息", Toast.LENGTH_SHORT).show();
//                return "";
//            } else {
//                BufferedReader br = new BufferedReader(new FileReader(file));
//                String line = "";
//                while ((line = br.readLine()) != null) {
//                    sb.append(line);
//                }
//                Log.e("获取config.txt", "" + sb.toString());
//                br.close();
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//
//        return sb.toString();
//    }


    public static List<ListItem> getListItem(JSONObject jsonObject) throws JSONException {

//        private String
        OrderBean orderBean = new OrderBean();
        String zhifubao = jsonObject.optString("zhifubao");
        String weixin = jsonObject.optString("weixin");
        String cash = jsonObject.optString("crash");
        String online = jsonObject.optString("online");

        String customerName = jsonObject.optString("customer_name");
        String shopName = jsonObject.optString("shop_name");
        String emloyName = jsonObject.optString("employee_name");
        String totalPrice = jsonObject.optString("total_price");
        String debt = jsonObject.optString("debt");
        String time = jsonObject.optString("sale_time");
        JSONArray jsonArray = jsonObject.optJSONArray("sale_list");

        List<ProductItem> productItems = new ArrayList<ProductItem>();

        float numberPrice;

        float totalNumberPrice = 0f;

        for (int i = 0; i < jsonArray.length(); i++) {

            ProductItem productItem = new ProductItem();
            List<ProductChildItem> productChildItems = new ArrayList<ProductChildItem>();
            JSONObject jsonObject1 = jsonArray.getJSONObject(i);
            String prductName = jsonObject1.optString("product_name");
            JSONArray jsonArray1 = jsonObject1.optJSONArray("sale_sku_list");
            for (int j = 0; j < jsonArray1.length(); j++) {

                JSONObject jsonObject2 = jsonArray1.getJSONObject(j);
                String productChildName = jsonObject2.optString("sku_value_name");
                String productChildprice = jsonObject2.optString("price");
                String productChildNumber = jsonObject2.optString("number");
                ProductChildItem productChildItem = new ProductChildItem();
                if (TextUtils.isEmpty(productChildName)) {
                    productChildName = prductName;
                }
                productChildItem.setProductChildName(productChildName);
                productChildItem.setProductChildPrice(productChildprice);
                productChildItem.setProductChildNumber(productChildNumber);
                numberPrice = Float.parseFloat(productChildprice) * Float.parseFloat(productChildNumber);
                totalNumberPrice = numberPrice + totalNumberPrice;
                productChildItems.add(productChildItem);

            }
            productItem.setProductChildItems(productChildItems);
            productItem.setProductName(prductName);
            productItems.add(productItem);
        }

        float actualPrice = Float.parseFloat(totalPrice);
        Log.e("--------", "actualPrice" + actualPrice + "totalNumberPrice" + totalNumberPrice);

        orderBean.setDiscountPrice(String.format("%.2f", (totalNumberPrice - actualPrice)) + "");
        orderBean.setProductTotalPrice(String.format("%.2f", (totalNumberPrice)) + "");

        orderBean.setCustomerName(customerName);
        orderBean.setShopName(shopName);
        orderBean.setEmloyName(emloyName);
        orderBean.setTotalPrice(totalPrice);
        orderBean.setOrderTime(time);
        orderBean.setDebt(debt);
        orderBean.setProductItemList(productItems);
        orderBean.setCash(cash);
        orderBean.setWeixin(weixin);
        orderBean.setZhifubao(zhifubao);
        orderBean.setBankCard(online);

        List<ListItem> listItems = setDataToListItem(orderBean);
        return listItems;

    }


    public static List<ListItem> setDataToListItem(OrderBean orderBean) {

        List<ListItem> listItems = new ArrayList<ListItem>();
        //组织ListItem 数据颠倒着
        if (!TextUtils.isEmpty(orderBean.getDebt())) {
            listItems = createItem(listItems, "赊账", orderBean.getDebt(), false);
        }
        if (!TextUtils.isEmpty(orderBean.getBankCard())) {
            listItems = createItem(listItems, "银行卡", orderBean.getBankCard(), false);
        }
        if (!TextUtils.isEmpty(orderBean.getWeixin())) {
            listItems = createItem(listItems, "微信", orderBean.getWeixin(), false);
        }

        if (!TextUtils.isEmpty(orderBean.getZhifubao())) {
            listItems = createItem(listItems, "支付宝", orderBean.getZhifubao(), false);
        }
        if (!TextUtils.isEmpty(orderBean.getCash())) {
            listItems = createItem(listItems, "现金", orderBean.getCash(), false);
        }

        listItems = createItem(listItems, "    ", "", false);
        String actualPrice = String.valueOf((Float.parseFloat(orderBean.getTotalPrice())));
        listItems = createItem(listItems, "实付价格", orderBean.getTotalPrice(), false);
        listItems = createItem(listItems, "    ", "", false);
        listItems = createItem(listItems, "调整", orderBean.getDiscountPrice(), false);
        listItems = createItem(listItems, "总价", orderBean.getProductTotalPrice(), false);
//        listItems = createItem(listItems, "订单金额", "", true);
        listItems = createItem(listItems, "*************************************************************************", "", false);
        listItems = createItem(listItems, "    ", "", false);
        for (ProductItem productItem : orderBean.getProductItemList()) {

            for (ProductChildItem productChildItem : productItem.getProductChildItems()) {
                float siglePrice = Float.parseFloat(productChildItem.getProductChildPrice());
                float number = Float.parseFloat(productChildItem.getProductChildNumber());
                float tPrice = siglePrice * number;

                listItems = createItem(listItems, "" + productChildItem.getProductChildNumber() + "X" + productChildItem.getProductChildPrice(), "" + tPrice, false);
                listItems = createItem(listItems, productItem.getProductName() + " " + productChildItem.getProductChildName(), "", false);
                listItems = createItem(listItems, "    ", "", false);
            }
//            listItems = createItem(listItems, productItem.getProductName(), "", true);
        }

        listItems = createItem(listItems, "**************************************************************************", "", false);
//        listItems = createItem(listItems, "货品清单", "", true);
        listItems = createItem(listItems, "开单员:" + orderBean.getEmloyName(), "", false);
        listItems = createItem(listItems, orderBean.getOrderTime(), "", false);
        listItems = createItem(listItems, "    ", "", false);
        String string = orderBean.getCustomerName();
        if (string.length() > 12) {
            listItems = createItem(listItems, "顾客", string.substring(0, 12), false);
        } else {
            listItems = createItem(listItems, "顾客", string, false);
        }

        listItems = createItem(listItems, "    ", "", false);
        listItems = createItem(listItems, "    ", "", false);
        listItems = createItem(listItems, orderBean.getShopName(), "", true);
        return listItems;

    }


    public static List<ListItem> createItem(List<ListItem> listItems, String key, String value, boolean isTitle) {
        ListItem listItem = new ListItem();
        listItem.setKey(key);
        listItem.setValue(value);
        listItem.setFlag(isTitle);
        listItems.add(listItem);
        return listItems;
    }


    public static File getSourceFile2(Context context, List<ListItem> listItems) throws Exception {
        File file = null;

        StringBuffer stringBuffer = new StringBuffer();

        String key[] = new String[20];
        String value[] = new String[20];

        for (int i = 0; i < 20; i++) {
            if (listItems.size() <= i) {
                key[i] = "";
                value[i] = "";
            } else {
                key[i] = listItems.get(i).getKey();
                value[i] = listItems.get(i).getValue();
            }
        }

        String[] strs = {
                "\u0002L\n",
                "D11\n",
                "1911S000480001000250025" + "Customer" + "                    " + "guest" + "\n",
                "1911S000480012500250025" + key[18] + "\n",
                "1911S000440001000250025" + "Merchandiser: Jin" + "\n",
                "1911S000420001000250025" + "****************************" + "\n",
                "1911S000380001000250025" + "Product" + "\n",
                "1911S000360001000250025" + key[12] + "\n",
                "1911S000360016500250025" + value[12] + "\n",
                "1911S000320001000250025" + "****************************" + "\n",
                "1911S000300001000250025" + "Total"+ "\n",
                "1911S000300016500250025" + value[9] + "\n",
                "1911S000280001000250025" + "Adjustment"+ "\n",
                "1911S000280016500250025" + value[8] + "\n",
                "1911S000240001000250025" + "Paid price"+ "\n",
                "1911S000240016500250025" + value[6] + "\n",
                "1911S000200001000250025" + "Cash"+ "\n",
                "1911S000200016500250025" + value[4] + "\n",
                "1911S000180001000250025" + "AliPay"+ "\n",
                "1911S000180016500250025" + value[3] + "\n",
                "1911S000160001000250025" + "WeChat"+ "\n",
                "1911S000160016500250025" + value[2] + "\n",
                "1911S000140001000250025" + "Bank"+ "\n",
                "1911S000140016500250025" + value[1] + "\n",
                "1911S000120001000250025" + "On credit"+ "\n",
                "1911S000120016500250025" + value[0] + "\n",
                "Q0001\n",
                "E\n"};

        for (String str : strs)
            stringBuffer.append(str);

        String fileName = "script.txt";
        String path = setFolderPath(context, PRINTER_SCRIPT);
        if (TextUtils.isEmpty(path)) {
            return null;
        }
        file = new File(path + File.separator + fileName);
        if (file.exists()) {
            Log.e("=====", "文件存在");
            //存在就删除
            file.delete();
            file.createNewFile();
        } else {
            Log.e("=====", "文件不存在");
            file.createNewFile();
        }

        FileOutputStream outStream = new FileOutputStream(file);
        OutputStreamWriter osw = new OutputStreamWriter(outStream, "UTF-8");
        osw.write(stringBuffer.toString());
        osw.flush();
//            outStream.write(stringBuffer.toString().getBytes());
//            outStream.close();


        return file;
    }


}
