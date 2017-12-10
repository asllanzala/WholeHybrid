package com.honeywell.wholesale.framework.scanner;

import android.util.Log;

/**
 * Created by xiaofei on 2/24/17.
 *
 */

public class ScanerRespManager {
    private static ScanerRespManager instance = null;

    private ScanerRespType type;

    private ScanerRespManager() {
        type = ScanerRespType.SCANNER_RESP_DEFAULT;
    }

    public static ScanerRespManager getInstance() {
        if (instance == null){
            instance = new ScanerRespManager();
        }
        return instance;
    }


    public ScanerRespType getType() {
        return type;
    }

    public void setType(ScanerRespType type) {
        Log.e("settype", "settype" + "   "  + type.toString());
        this.type = type;
    }

    public enum ScanerRespType {
        // initial
        SCANNER_RESP_DEFAULT,
        SCANER_RESP_SEARCH,
        SCANER_RESP_INVENTORY,
        SCANER_NULL_TYPE
    }
}
