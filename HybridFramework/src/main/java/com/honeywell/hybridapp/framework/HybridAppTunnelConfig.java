package com.honeywell.hybridapp.framework;

import android.content.Context;
import android.webkit.WebView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
public class HybridAppTunnelConfig {

    public final static String URL_START_TAG = "hybridapp://";

    //Example:   hybrid://response/{function}/returncontent
    public final static String URL_RESPONSE_TAG = URL_START_TAG + "response/";

    public final static String URL_RESPONSE_DATA_NEW_EVENT_ALERT = URL_START_TAG + "newEventAlert/";

    public final static String URL_RESPONSE_FETCH_EVENTS = URL_RESPONSE_TAG + "fetchEvents/";

    public final static String JS_TUNNEL_CALL_JS_API = "callJs('%s')";

    public final static String JS_TUNNEL_API_FETCH_EVENTS = "fetchEvents()";

    public final static String EMPTY_STR = "";

    public final static String UNDERLINE_STR = "_";

    public final static String SPLIT_MARK = "/";

    public final static String EVENT_CALLBACK_ID_FORMAT = "JAVA_CALLBACK_%s";

    public final static String URL_JAVASCRIPT_START_TAG = "javascript:";

    public final static String JS_TUNNEL_NAME = "JsTunnel";


    public static String parseFunctionName(String jsUrl) {
        // replace javascript:JsTunnel.
        return jsUrl.replace(URL_JAVASCRIPT_START_TAG + JS_TUNNEL_NAME + ".", "")
                .replaceAll("\\(.*\\);", "");
    }

    /**
     * Generate the final URL can be executed in the WebView.(finalURL).
     * javascript:JsTunnel.handleRequest('%s')
     *
     * @param apiName the api method name provided by JsTunnel.js
     */
    public static String getFinalURL(String apiName) {
        return URL_JAVASCRIPT_START_TAG + JS_TUNNEL_NAME + "." + apiName + ";";

    }

    public static String getDataFromReturnUrl(String url) {
        if (url.startsWith(URL_RESPONSE_FETCH_EVENTS)) {
            return url.replace(URL_RESPONSE_FETCH_EVENTS, EMPTY_STR);
        }

        String temp = url.replace(URL_RESPONSE_TAG, EMPTY_STR);
        String[] functionAndData = temp.split(SPLIT_MARK);

        if (functionAndData.length >= 2) {
            StringBuilder sb = new StringBuilder();
            for (int i = 1; i < functionAndData.length; i++) {
                sb.append(functionAndData[i]);
            }
            return sb.toString();
        }
        return null;
    }

    public static String getFunctionFromReturnUrl(String url) {
        String temp = url.replace(URL_RESPONSE_TAG, EMPTY_STR);
        String[] functionAndData = temp.split(SPLIT_MARK);
        if (functionAndData.length >= 1) {
            return functionAndData[0];
        }
        return null;
    }

    public static void webViewLoadLocalJs(WebView view, String path) {
        String jsContent = assetFile2Str(view.getContext(), path);
        view.loadUrl(URL_JAVASCRIPT_START_TAG + jsContent);
    }

    public static String assetFile2Str(Context context, String filePath) {
        InputStream inputStream = null;
        try {
            inputStream = context.getAssets().open(filePath);
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

            String line = null;
            StringBuilder sb = new StringBuilder();
            do {
                line = bufferedReader.readLine();
                if (line != null && !line.matches("^\\s*\\/\\/.*")) {
                    sb.append(line);
                }
            } while (line != null);

            bufferedReader.close();
            inputStream.close();

            return sb.toString();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                }
            }
        }
        return null;
    }
}
