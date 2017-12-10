/**
 * (c) Copyright 2016 Administrator. All Rights Reserved.
 * 2016-05-19
 */

var scanProduct = "", couter = 1;

(function () {
    require(['zepto', 'common', 'salesModule'], function ($, common, salesModule) {

        $(document).on("pageInit", "#adjustInventory", function (e, pageId, $page) {

            /* init js tunnel */
            common.connectJsTunnel(function (jsTunnel) {
                jsTunnel.init();
            });

            /* get cache product detail */
             var scanProduct= sessionStorage.getItem("scanProduct"),
                 product_id = "",
                 shopId ="",
                 inventory_list =[];

                scanProduct = JSON.parse(scanProduct);
                product_id = scanProduct.product_id;
                inventory_list = scanProduct.inventory_list;

                $(".header_img img").attr("src",scanProduct.pic_hd_src[0] ? scanProduct.pic_hd_src[0] :"../../images/default.jpeg");
                $(".info_name").text(scanProduct.product_name);
                $(".warehouse_choice").text(scanProduct.warehouse_name);

                if(inventory_list[0].sku_value_id1){
                    $(".info_stock").text(scanProduct.total_quantity ? "库存 "+scanProduct.total_quantity +"件":"库存0件");
                    $(".info_priceQuery").text(scanProduct.standard_price_range ? "￥"+scanProduct.standard_price_range :"￥0");
                }else{
                    $(".info_stock").text(inventory_list[0].quantity ? "库存 "+ inventory_list[0].quantity +"件" :"库存0件");
                    $(".info_priceQuery").text(inventory_list[0].standard_price ? "￥"+inventory_list[0].standard_price:"￥0");

                }

             var list_item_template ="";
             if(inventory_list[0].sku_value_id1){
                  $.each(inventory_list,function(key,value){
                      list_item_template +=  $("#list_item").html()
                                             .replace("{{inventory_id}}",value.inventory_id)
                                             .replace("{{warehouse_inventory_id}}",value.warehouse_inventory_id)
                                             .replace("{{sku_value_name}}",value.sku_name)
                                             .replace("{{quantity}}",value.quantity ? "库存 "+value.quantity+"件" :"库存0件")
                                             .replace("{{standard_price}}",value.standard_price ? "￥"+value.standard_price :"￥0")
                  });
                  $(".content_list").html(list_item_template);
             }else{
                 list_item_template +=  $("#list_item").html()
                                      .replace("{{inventory_id}}",inventory_list[0].inventory_id)
                                      .replace("{{warehouse_inventory_id}}",inventory_list[0].warehouse_inventory_id)
                                      .replace("{{sku_value_name}}",scanProduct.product_name)
                                      .replace("{{quantity}}","库存 "+inventory_list[0].quantity ? inventory_list[0].quantity+"件":"库存0件")
                                      .replace("{{standard_price}}",inventory_list[0].standard_price ? "￥"+inventory_list[0].standard_price:"￥0")
                 $(".content_list").html(list_item_template);
             }
             $('.indicator-background,.indicator-div').remove();

             var userData = {
                 method: NATIVE_METHOD_GET_USER_INFO,
                 data: ""
             };
             common.jsCallNativeEventHandler(JS_CALL_NATIVE_EVENT_EXECUTE_NATIVE_METHOD, userData, function (data) {
                 var userInfo = JSON.parse(data.response);
                 shopId = userInfo.shopID;
             });

            $(document).on("input",".right_value",function(){
//                 common.inputMoney($(this));
                 if($(this).val() !=""){
                    if(($(this).val())[0] == "-"){
                           if(common.regMoney_special($(this).val())){
                               $(this).removeClass("borderRed");
                           }else {
                                $(this).addClass("borderRed");
                           }
                           common.inputMoney($(this));
                           $(this).val("-"+$(this).val().substring(1));
                    }else{
                       common.inputMoney($(this));
                       if(common.regMoney_special($(this).val())){
                           $(this).removeClass("borderRed");
                       }else {
                            $(this).addClass("borderRed");
                       }
                    }
                 }else{
                       $(this).removeClass("borderRed");
                 }
            });

            /* plus and minus */
            $(document).on("click",".right_minus",function(){
                var stock = $(this).siblings("input");
                if(stock.hasClass("borderRed")){
                    $(this).siblings("input").val(0).removeClass("borderRed");
                }else{
                    if(stock.val() ==""){
                       stock.val(0);
                    }
                     var calculateValue =  parseFloat(stock.val())-1;
                     $(this).siblings("input").val(calculateValue.toFixed(2));
                }
            });

            $(document).on("click",".right_plus",function(){
                var stock = $(this).siblings("input");
                if(stock.hasClass("borderRed")){
                    $(this).siblings("input").val(0).removeClass("borderRed");
                }else{
                   if(stock.val() ==""){
                       stock.val(0);
                   }
                     var calculateValue =  parseFloat(stock.val())+1;
                     $(this).siblings("input").val(calculateValue.toFixed(2));
                }
            });

            $(document).on("click",".confirmButton",function(){
                if($(".right_value").hasClass("borderRed")){
                    $.toast("请输入正确的信息");
                }else{
                    for(var j = 0; j< $(".list_item").length;j++){
                         if($(".list_item").eq(j).find(".right_value").val()){
                                $(".list_item").eq(j).addClass("hasChoice")
                         }
                    }
                   var inventories = [];
                   for(var i=0;i<$(".hasChoice").length;i++){
                        var inventory = {};
                        inventory.quantity = parseFloat($(".hasChoice").eq(i).find(".right_value").val());
                        inventory.warehouse_inventory_id = parseInt($(".hasChoice").eq(i).find(".content_name").data("warehouse_inventory_id"));
                        if(inventory.warehouse_inventory_id.toString() == 'NaN'){
                            $.toast("货品未在本仓库入库，请先入库再调整");
                            return true;
                            break;
                        }
                        inventories.push(inventory);
                   }
                   var  adjustInventoryData ={
                        shop_id:parseInt(shopId),
//                        product_id:parseInt(product_id),
                        inventories:inventories
                   }
                   console.log("库存调整传入的数据="+JSON.stringify(adjustInventoryData));
                    var adjustInventoryRequest =common.dataTransform('inventory/take','POST',adjustInventoryData);
                    common.jsCallNativeEventHandler(
                        JS_CALL_NATIVE_EVENT_REQUEST_WEB_API,
                        adjustInventoryRequest,
                        function(responseData){
                           var httpResponse = JSON.parse(responseData.response);
                            if (responseData.ack == 1 && httpResponse.retcode == 200) {
                                window.history.go(-1);
                            }else if(responseData.ack == 0 && httpResponse.retcode == 4016){
                                $.toast('库存不足')
                            }else if(responseData.ack == 0 && httpResponse.retcode == 4026){
                                $.toast('权限不足，无法操作')
                            } else {
                                $.toast('网络异常');
                            }
                        }
                    )
                }
            });

            /* back page */
            $(document).on("click",".backPageBtn",function(){
                window.history.back();
            });
        });

        $.init();
    });
})();



