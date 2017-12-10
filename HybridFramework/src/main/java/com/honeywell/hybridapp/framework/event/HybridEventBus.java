package com.honeywell.hybridapp.framework.event;

import android.text.TextUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by allanhwmac on 16/5/17.
 */
public class HybridEventBus {

    private Map<String, EventHandler> mEventHandlers = new HashMap<String, EventHandler>();

    private EventHandler mDefaultHandler = new DefaultEventHandler();

    public EventHandler getEventHandler(Event event) {
        EventHandler handler;
        if (event != null && !TextUtils.isEmpty(event.getEventName())) {
            handler = mEventHandlers.get(event.getEventName());
        } else {
            handler = mDefaultHandler;
        }

        return handler;
    }

    /**
     * @param handler default handle,handle messages callJS by js without assigned handle name,
     *                if js message has handle name, it will be handled by named handlers
     *                registered by native
     */
    public void setDefaultEventHandler(EventHandler handler) {
        this.mDefaultHandler = handler;
    }

    /**
     * register handle,so that javascript can call it
     */
    public void registerEventHandler(String handlerName, EventHandler handler) {
        if (handler != null) {
            mEventHandlers.put(handlerName, handler);
        }
    }


}
