/**
 * (c) Copyright 2016 Administrator. All Rights Reserved.
 * 2016-05-19
 *
 */
(function () {
    require(['zepto', 'common'], function ($, common) {

        $(document).on("pageInit","#setting_auto_productNumber",function(){

            /* init js tunnel */
            common.connectJsTunnel(function (jsTunnel) {
                jsTunnel.init();
                 var userData = {
                    method: NATIVE_METHOD_GET_USER_INFO,
                    data: ""
                 };
                 common.jsCallNativeEventHandler(JS_CALL_NATIVE_EVENT_EXECUTE_NATIVE_METHOD, userData, function (data) {
                    var userInfo = JSON.parse(data.response);
                    if(userInfo.role == 1){
                        $(".warehouse_content").hide();
                    }
                    $('.indicator-background,.indicator-div').remove();

                    var prefixData ={
                        shop_id:userInfo.shopID
                    };
                    var prefixRequest =common.dataTransform('product/rule/query','POST',prefixData);
                        common.jsCallNativeEventHandler(
                            JS_CALL_NATIVE_EVENT_REQUEST_WEB_API,
                            prefixRequest,
                            function(data){
                               var respondData =data.response;
                               respondData =JSON.parse(respondData);
                               if(respondData.retcode ==200){
                                  if(respondData.retbody){
                                    $(".detail_prefixInput").val(respondData.retbody.prefix ? respondData.retbody.prefix:"");
                                    $(".productNumberComponent_value").text(respondData.retbody.length ? respondData.retbody.length:6);
                                    if(respondData.retbody.enabled){
                                          $(".switch_checkbox").prop("checked",true);
                                          $(".content_circle").animate({
                                              left: "1.85rem"
                                          },150,"ease-in");
                                          $(".switch_content").animate({
                                              backgroundColor:"#ffc700"
                                          },150,"ease-out");
                                          $(".auto_content").animate({
                                             height:"5.1rem"
                                          },150,"ease-out");
                                    }
                                  }
                                }else {
                                    $.toast('网络异常');
                                }
                            }
                        )
                 });
            });

            /* minus and add */
            $(document).on("click",".productNumberComponent_minus",function(){
                 var productNumberComponent_value = parseInt($(".productNumberComponent_value").text());
                 if(productNumberComponent_value >1){
                       productNumberComponent_value -=1;
                       $(".productNumberComponent_value").text(productNumberComponent_value)
                 }
            });

            $(document).on("click",".productNumberComponent_plus",function(){
                 var productNumberComponent_value = parseInt($(".productNumberComponent_value").text());
                 if(productNumberComponent_value <12){
                       productNumberComponent_value ++;
                       $(".productNumberComponent_value").text(productNumberComponent_value)
                 }
            });

            /* open auto setting product number */
            function resultCallback(some_content,checkbox) {
                if(checkbox.prop("checked")){
                    some_content.animate({
                        height:"0"
                    },150,"ease-out")
                }else{
                    some_content.animate({
                        height:"5.1rem"
                    },150,"ease-out")
                }
            }

            $(document).on("click",".common_switch",function (e) {
                var checkbox = $(".switch_checkbox");
                var switch_content = $(".switch_content");
                var content_circle = $(".content_circle");
                var some_content = $(".auto_content");
                common.switchResult(checkbox,switch_content,content_circle,resultCallback(some_content,checkbox));
            });

            /* submit prefix to server */
            $(document).on("click",".bar_confirm",function(){
                if(regValue($(".detail_prefixInput").val())){
                    var prefixData ={
                        prefix: $(".detail_prefixInput").val().toUpperCase(),
                        length: parseInt($(".productNumberComponent_value").text()),
                        enabled: $(".switch_checkbox").prop("checked")? true :false,
                    };
                    var prefixRequest =common.dataTransform('product/rule/update','POST',prefixData);
                        common.jsCallNativeEventHandler(
                            JS_CALL_NATIVE_EVENT_REQUEST_WEB_API,
                            prefixRequest,
                            function(data){
                               var respondData =data.response;
                               respondData =JSON.parse(respondData);
                               if(respondData.retcode ==200){
                                    $.toast("设置成功",1000);
                                    common.backHomePage();
                                }else {
                                    $.toast('网络异常');
                                }
                            }
                        )
                }else{
                    $.toast("仅可输入字母和- . /",1000)
                }
            });

            function regValue(data){
               var reg =new RegExp('^[-a-zA-Z\./]+$');
               if(reg.test(data) || data ==""){
                   return true
               }else{
                   return false
               }
            }

            /* back to native */
            $(document).on("click",".bar_backButton",function(){
                common.backHomePage();
            });

            /* change page to warehouse manege page */
            $(document).on("click",".warehouse_manege",function(){
                var moveToWarehouseEvent = {
                     method:NATIVE_PAGE_WAREHOUSE_MANAGER,
                     data:""
                };
                 common.jsCallNativeEventHandler(
                    JS_CALL_NATIVE_EVENT_REQUEST_SWITCH_PAGE,
                    moveToWarehouseEvent,
                    function (data) {

                 });
            });



        })
        $.init();
    });
})();