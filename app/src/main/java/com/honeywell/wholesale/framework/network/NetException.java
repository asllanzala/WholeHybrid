package com.honeywell.wholesale.framework.network;

/**
 * Created by xiaofei on 3/27/17.
 *
 */

public class NetException extends RuntimeException{

    public NetException(int resultCode) {
        this(getApiExceptionMessage(resultCode));
    }

    public NetException(String detailMessage) {
        super(detailMessage);
    }

    private static String getApiExceptionMessage(int code){
        String message = "";

        return message;
    }
}
