package com.honeywell.wholesale.framework.http;

/**
 * Created by e887272 on 7/27/16.
 *
 */
public class WebServerConfigManager {
    // Test Url
    private static final String WEB_SERVICE_TEST_IP = "http://159.99.93.113:8099";//测试端口
    private static final String WEB_CHENHUI = "http://10.78.116.122:8888";
    private static final String WEB_CHENHUI_WW = "http://192.168.32.38:8088";
    private static final String WEB_SERVICE_SHANGHAI_IP = "http://192.168.32.63:8080";
    private static final String WEB_SERVICE_TEST_IP_2 ="http://115.159.152.188";//南京测试端口


    // TEST ACCOUNT
    // 13712345670/13712345670/123456
    private static final String WEB_SERVICE_EXTERNAL_WEBSITE_V1_1_0 = "http://acscloud.honeywell.com.cn/00100009/v1.1.0";
    private static final String WEB_SERVICE_EXTERNAL_WEBSITE_V1_1_1 = "https://acscloud.honeywell.com.cn/00100009/v1.1.1";

    //staging test
    private static final String STAGING_WEB_SERVICE_EXTERNAL_WEBSITE_V1_1_1 = "https://dev.acscloud.honeywell.com.cn/00100009/v1.1.1";
    private static final String STAGING_WEB_SERVICE_EXTERNAL_WEBSITE_V1_1_1_Test =
            "https://dev.acscloud.honeywell.com.cn/00100009/v1.1.1/testperformance";

    //QA test
    private static final String QA_STAGING_WEB_SERVICE_EXTERNAL_WEBSITE_V1_1_1 = "https://qa.acscloud.honeywell.com.cn/00100009/v1.1.1";

    public static final String getWebServiceUrl() {
        return getWebServiceIP() + "/";
    }

    public static final String getWebServiceIP() {

//        return STAGING_WEB_SERVICE_EXTERNAL_WEBSITE_V1_1_1_Test;
//        return WEB_SERVICE_NANJING_TEST_IP;
//        return WEB_SERVICE_SHANGHAI_IP;
//        return WEB_SERVICE_TEST_IP_2;
//          return WEB_CHENHUI;
//        return WEB_CHENHUI_WW;

        //QA Staging版本的URL
//        return QA_STAGING_WEB_SERVICE_EXTERNAL_WEBSITE_V1_1_1;

        //Staging版本的URL
        return STAGING_WEB_SERVICE_EXTERNAL_WEBSITE_V1_1_1;

        //Production版本的URL
//        return WEB_SERVICE_EXTERNAL_WEBSITE_V1_1_1;

        //南京测试IP
//        return WEB_CHENHUI;
    }
}
