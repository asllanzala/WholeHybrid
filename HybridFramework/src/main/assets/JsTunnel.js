// This JS will be added to every html page in the WebViewClient.onPageFinished().
(function() {
    if (window.JsTunnel) {
        return;
    }

    var URL_START_TAG = 'hybridapp://';

    var URL_RESPONSE_TAG = URL_START_TAG + "response/";
    var URL_RESPONSE_NEW_EVENT_ALERT = 'newEventAlert/';
    var URL_RESPONSE_FETCH_EVENTS = "fetchEvents/";

    var JS_CALL_NATIVE_EVENT_CALLBACK_ID = "JS_CALLBACK_";


    var eventsIframe;
    var responseEventsQueue = [];
    var callJsQueue = [];
    var nativeCallJsEventsHandlers = {};

    var responseCallbacks = {};

    // Create a none-display iframe, if we reset the src, the native code will catch the src URL to fetch data.
    function _createIframe(doc) {
        eventsIframe = doc.createElement('iframe');
        eventsIframe.style.display = 'none';
        doc.documentElement.appendChild(eventsIframe);
    }

    //set default messageHandler
    function init(eventHandler) {
        if (JsTunnel.eventHandler) {
            throw new Error('JsTunnel.init called twice');
        }
        JsTunnel.eventHandler = eventHandler;
        var receivedRequests = callJsQueue;
        callJsQueue = null;

        for (var i = 0; i < receivedRequests.length; i++) {
            _processNativeCallJsEvent(receivedRequests[i]);
        }
    }

    function registerHandler(handlerName, handler) {
        nativeCallJsEventsHandlers[handlerName] = handler;
    }

    function callNativeEventHandler(eventName, data, responseCallback) {
        console.log("callNative(), eventName=" + eventName + ", data=" + data + "responseCallback = " +responseCallback);

        _callNative({
            name: eventName,
            data: JSON.stringify(data)
        }, responseCallback);
    }

    function callNativeNoHandler(data, responseCallback) {
        _callNative({
            data: JSON.stringify(data)
        }, responseCallback);
    }

    function _callNative(event, responseCallback) {
        if (responseCallback) {
            var callbackId = JS_CALL_NATIVE_EVENT_CALLBACK_ID + '_' + new Date().getTime();
            responseCallbacks[callbackId] = responseCallback;
            event.callbackId = callbackId;
        }

//        console.log("event.data=" + event.data);
//        console.log("event.name=" + event.name);
//        console.log("event.callbackId=" + event.callbackId);

        responseEventsQueue.push(event);
        eventsIframe.src = URL_START_TAG + URL_RESPONSE_NEW_EVENT_ALERT;
    }

    // API for native codes to get all events need to be handled. Js reset the iframe.src,
    // and then Android can catch the src url from the WebViewClient.shouldOverrideUrlLoading() method.
    function fetchEvents() {
        var messageQueueString = JSON.stringify(responseEventsQueue);
        console.log("messageQueueString="+messageQueueString);
        responseEventsQueue = [];
        // Android can't read the response data directly, reset the eventsIframe.src value can reload iframe src to transfer data to Android.
        eventsIframe.src = URL_RESPONSE_TAG + URL_RESPONSE_FETCH_EVENTS + encodeURIComponent(messageQueueString);
    }

    // API for native codes to call javascript.
    function callJs(jsonData) {
        var jsonDataString = JSON.stringify(jsonData);
        if (callJsQueue) {
            console.log("callJs() 页面没加载完，缓存起来。jsonDataString="+jsonDataString);
            callJsQueue.push(jsonData);
        } else {
            console.log("callJs() 页面加载完，直接使用。jsonDataString="+jsonDataString);
            _processNativeCallJsEvent(jsonData);

        }
    }

    // Process native call js event from native codes.
    function _processNativeCallJsEvent(jsonData) {
        setTimeout(function() {
            var callJsEventJson = JSON.parse(jsonData);
            var responseCallback;

            //java call finished, now need to call js callback function
            console.log("java call finished, now need to call js callback function"+callJsEventJson.responseId);
            if (callJsEventJson.responseId) {
                responseCallback = responseCallbacks[callJsEventJson.responseId];
                if (!responseCallback) {
                    return;
                }
                responseCallback(callJsEventJson.responseData);
                delete responseCallbacks[callJsEventJson.responseId];
            } else {
                //发送回调给native
                console.log("发送回调给native");
                if (callJsEventJson.callbackId) {
                    var callbackResponseId = callJsEventJson.callbackId;
                    responseCallback = function(responseData) {
                        _callNative({
                            responseId: callbackResponseId,
                            responseData: JSON.stringify(responseData)
                        });
                    };
                }

                var handler = JsTunnel.eventHandler;
                if (callJsEventJson.name) {
                    //查找指定handler
                    handler = nativeCallJsEventsHandlers[callJsEventJson.name];
                    console.log("handler=" + handler);

                    if(handler == null) {
                        console.log("No JS Handler for this Native Call:" + JSON.stringify(callJsEventJson));
                    } else {
                        //查找指定handler
                        try {
                            handler(JSON.parse(callJsEventJson.data), responseCallback);
                        } catch (exception) {
                            console.log("Native call JS exception:" + JSON.stringify(callJsEventJson));
                            console.log("Native call JS exception:" + exception.name + ":" + exception.message);
                        }
                    }

                } else {
                    console.log("Native call JS, Error! event name is null!");

                }
            }
        });
    }

    var JsTunnel = window.JsTunnel = {
        init: init,
        callNativeNoHandler: callNativeNoHandler,
        registerHandler: registerHandler,
        callNativeEventHandler: callNativeEventHandler,
        fetchEvents: fetchEvents,
        callJs: callJs,
    };

    // Create a hidden Iframe for every html page.
    _createIframe(document);

    var jsTunnelReady = document.createEvent('Events');
    jsTunnelReady.initEvent('JsTunnelReady');
    jsTunnelReady.bridge = JsTunnel;
    document.dispatchEvent(jsTunnelReady);

})();