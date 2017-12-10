package com.honeywell.wholesale.framework.utils;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;


import com.honeywell.wholesale.framework.service.btservice.BluetoothService;
import com.honeywell.wholesale.ui.menu.customer.CustomerDetailActivity;
import com.honeywell.wholesale.ui.order.OrderConfirmActivity;
import com.honeywell.wholesale.ui.order.OrderDetailActivity;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;

/**
 * Created by Junyu.zhu@Honeywell.com on 16/8/24.
 */
public class ParseXml {
    private static String TAG = "ParseXml";

    private BluetoothService mService = null;
    private Context mContext;
    private int mRequestType;
    private String message;
    private File mFile;
    private byte[] bytes;


    public ParseXml(Context context, File file) {
        this.mContext = context;
        this.mService = OrderDetailActivity.getBluetoothService();

        if (mService == null) {
            this.mService = OrderConfirmActivity.getBluetoothService();
        }
        if (mService == null) {
            this.mService = CustomerDetailActivity.getBluetoothService();
        }
        this.mFile = file;
    }


    public interface XmlResponse {
        void getXmlData(String stringResult);
    }


    public void sendFileMessage() throws Exception {

        if (mService == null) {
            Toast.makeText(mContext, "蓝牙没有连接", Toast.LENGTH_SHORT).show();
            return;
        }

        if (mService.getState() != BluetoothService.STATE_CONNECTED) {
            Toast.makeText(mContext, "蓝牙没有连接", Toast.LENGTH_SHORT).show();
            return;
        }
        byte[] bytes = null;

        int length = (int) mFile.length();
        Log.e(TAG, "脚本文件的长度:" + length);
        if (length > Integer.MAX_VALUE)   //当文件的长度超过了int的最大值
        {
            System.out.println("this file is max ");
            return;
        }
        bytes = new byte[length];
        BufferedInputStream buf = new BufferedInputStream(new FileInputStream(mFile));
        Log.e(TAG, "查看长度是否相同:" + (buf.available() == mFile.length()));
        buf.read(bytes, 0, bytes.length);
        buf.close();

        Log.e("脚本byte", "" + bytes.length);
        mService.write(bytes);


    }


    public static class RequestType {

        public static final int REQUEST_CLC = 1;
        public static final int REQUEST_CONN = 2;
        public static final int REQUEST_SETLOCALIZATION = 3;
        public static final int REQUEST_SYSTEMINFO = 4;
        public static final int REQUEST_PRINTQUALITYWIZARD = 5;
        public static final int REQUEST_TEMPLATEFILE = 6;
        public static final int REQUEST_GETCONFIGSCHEMA = 7;
        public static final int REQUEST_GETCONFIG = 8;
        public static final int REQUEST_SETCONFIG = 9;
        public static final int REQUEST_GETFILES = 10;


    }


}
