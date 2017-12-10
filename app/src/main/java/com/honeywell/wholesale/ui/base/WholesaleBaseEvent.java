package com.honeywell.wholesale.ui.base;

import android.support.annotation.Nullable;

import java.util.Map;


public class WholesaleBaseEvent {
    public enum CarBaseEventType {
        NORMAL_MSG_EVENT,//普通推送消息，用于Toast一下即可，处理只放在MainActivity中。
        WEBSOCKET_EVENT,
        PROGRESS_STATUS,//成功，或者失败, 并携带错误原因
        UPGRADE,//升级事件，包含升级日期，版本号，描述文字
        TIME_OUT,//超时处理
        CONNECTING_LOST,//失去连接，跳转到登录界面
        RE_LOGIN,//重新登录
    }

    public CarBaseEventType type;
    public String message;
    public boolean isSuccessed;

    /**
     * 普通消息
     *
     * @param type
     * @param isSuccessed
     * @param message
     */
    public WholesaleBaseEvent(CarBaseEventType type, @Nullable Boolean isSuccessed, @Nullable String message) {
        this.type = type;
        this.isSuccessed = isSuccessed;
        this.message = message;
    }


    /**
     * 升级使用
     */
    private Map<String, String> info;

    public WholesaleBaseEvent(CarBaseEventType type, Map<String, String> updateInfo) {
        this.type = type;
        this.info = updateInfo;
    }


    public Boolean getIsSuccessed() {
        return isSuccessed;
    }

    public String getMessage() {
        return message;
    }

    public CarBaseEventType getType() {
        return type;
    }

    public Map<String, String> getInfo() {
        return info;
    }

}
