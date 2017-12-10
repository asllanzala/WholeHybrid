package com.honeywell.hybridapp.framework.event;

import com.honeywell.hybridapp.framework.IHybridCallback;

public interface EventHandler {

    void handle(Event event, IHybridCallback callBack);

}
