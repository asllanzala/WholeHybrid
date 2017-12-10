package com.honeywell.wholesale.ui;

import android.text.TextUtils;

import java.io.IOException;

/**
 * Created by allanhwmac on 16/5/24.
 */
public class ReadJsonFileUtil {

    /**
     * Get the demo response
     *
     * @param jsonFileName demo response json file name
     * @return
     */
    public static String getJsonString(String jsonFileName) throws IOException {
        try {
            if (!TextUtils.isEmpty(jsonFileName)) {
                return IOUtils.getStringByAssetFileName(jsonFileName);
            } else {
                // Response mock result.
                return "";
            }
        } catch (Exception e) {
            throw new IOException("Get json data error, " + jsonFileName + ", " + e.getMessage());
        }
    }

}
