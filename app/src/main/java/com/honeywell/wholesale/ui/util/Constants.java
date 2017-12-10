package com.honeywell.wholesale.ui.util;

import android.os.Environment;

public class Constants {
    public static final boolean IS_BETA = false;
    public static final boolean IS_TESTING_PID03 = false;
    public static final String PID_03_CONTENT = "0343 01 03 03";
    public static final String APP_NAME = "CarDiagnose";
//    public static final boolean IS_PROFESSIONAL = BuildConfig.IS_PROFESSIONAL;
//    public static final String IP_ALL = BuildConfig.NET_DEBUG ? "qa.acscloud.honeywell.com.cn" : "acscloud.honeywell.com.cn";


    public static final String IP_ALL = "http://115.159.225.195";
    public static final String IP_PORT = ":8088/";
    public static final String IP_PORT_SMS = ":8086/";
    public static final String IP_BASE = IP_ALL + IP_PORT;
    public static final String IP_BASE_SMS = IP_ALL + IP_PORT_SMS;

    public static final String IP_LOGIN = IP_BASE + "user/login";
    public static final String IP_REGISTER = IP_BASE + "user/register";
    public static final String IP_VERIFY_SMS = IP_BASE_SMS + "sms/vcode/id";
    public static final String IP_CHANGE_PASSWORD = IP_BASE + "/user/modifypassword";
    public static final String IP_UPLOAD_TESTING_REPORT = IP_BASE + "user/history";

    public static final String IP_UPLOAD_REPORT_BATTERY = IP_BASE + "history/battery";
    public static final String IP_UPLOAD_REPORT_BATTERY2 = IP_BASE + "history/battery2";
    public static final String IP_UPLOAD_REPORT_MISFIRE = IP_BASE + "history/missfire";
    public static final String IP_UPLOAD_REPORT_COOLANT = IP_BASE + "history/coolant";
    public static final String IP_UPLOAD_ALL_PARAMETERS = IP_BASE + "history/alldata";
    public static final String IP_GET_LATEST_REPORT = IP_BASE + "user/getnewesthistorydata";
    public static final String IP_GET_ALL_REPORT = IP_BASE + "user/gethistorydata";
    public static final String IP_GET_CAR_REPORT = IP_BASE + "user/getvin";
    public static final String IP_GET_CAR_DETAIL_REPORT = IP_BASE + "user/gethistorybyvin";
    public static final String IP_GET_DETAIL_REPORT = IP_BASE + "user/getdetaildata";


//    public static final String IP_LOGIN=IP_BASE+"user/login/";

    //总地址
    public static final String URI_ALL = "https://" + IP_ALL + "/v1/00100001";

    public static final int SDK_INT = android.os.Build.VERSION.SDK_INT;


    public static final String FILE_PATH = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + APP_NAME + "/";
    public static final String FINISH_ALL = "finish_all";
    public static final String TITLE = "title11";
    public static final String CONTENT = "content11";
    public static final String TYPE = "type111";
    public static final String OPERATION_TYPE = "type";
    public static final String OPERATION_ADD = "add";
    public static final String OPERATION_EDIT = "edit";
    public static final String RESULT = "result";
    public static final String SUCCESS = "success";
    public static final String TOKEN = "token";
    public static final String REGISTER = "register";
    public static final String TIME = "time";

    public static final String OBD_MAC = "obd_mac";
    public static final String VIN = "car_vin";
    public static final String UNKNOW = "unknow";
    public static final String NEED_INPUT_VIN = "input vin";

    public static final String NET_OPERATION_SUCCESS = "success";
    public static final String NET_OPERATION_FAILED = "failed";
    public static final String NET_OPERATION_STATUS = "Status";
    public static final String NET_OPERATION_DETAIL = "Detail";
    public static final String NET_OPERATION_DATA = "Data";
    public static final String NET_OPERATION_ID = "Id";
    public static final String NET_OPERATION_NO_DATA = "[]";
    public static final String NET_OPERATION_TIME = "time";


}
