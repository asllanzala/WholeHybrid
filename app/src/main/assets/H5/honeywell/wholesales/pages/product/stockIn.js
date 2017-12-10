/**
 * (c) Copyright 2016 Administrator. All Rights Reserved.
 * 2016-05-19
 */

(function () {
    require(['zepto', 'common', 'salesModule', 'supplierModule'], function ($, common, salesModule, supplierModule) {

        $(document).on("pageInit", "#stockInPage", function (e, pageId, $page) {
          var chId = '';
          var totalSetIndex = 0;

          common.connectJsTunnel(function (jsTunnel) {
              jsTunnel.init();
          });

        /* get cache product detail data */
        var scanProduct= sessionStorage.getItem("scanProduct");
            scanProduct = JSON.parse(scanProduct);

        /* 获取sku后，重绘dom */
        var inventoryList = scanProduct.inventory_list;
        var domTemplate1 ="";
        $(".warehouse_choice").text(scanProduct.warehouse_name).attr("data-warehouseid",scanProduct.warehouse_id)

        if(inventoryList[0].sku_value_id1){
            $.each(inventoryList,function(key,value){
                domTemplate1 += $("#set_list").html()
                               .replace("{{sku_value_name}}",value.sku_name)
                               .replace("{{inventory_id}}",value.inventory_id)
            });
            $(".set_content").html(domTemplate1);
        }else{
            domTemplate1 += $("#set_list").html()
                           .replace("{{sku_value_name}}",scanProduct.product_name)
                           .replace("{{inventory_id}}",inventoryList[0].inventory_id);
            $(".set_content").html(domTemplate1);
        }
        $('.indicator-background,.indicator-div').remove();

        /* switch for total set price cost and stock  */
        $(document).off("click",".totalStock,.totalCost,.totalPrice");
        $(document).on("click",".totalStock,.totalCost,.totalPrice",function(){
            $(".totalStock,.totalCost,.totalPrice").removeClass("active_border");
            $(this).addClass("active_border");
            $(".tab_input").animate({
                height:"2.65rem"
            },200,"ease-out");

            if($(this)[0] == document.querySelector('.totalStock')){
                totalSetIndex = 0
            }else if($(this)[0] == document.querySelector('.totalCost')){
                totalSetIndex = 1
            }else if($(this)[0] == document.querySelector('.totalPrice')){
                totalSetIndex = 2
            }
        });

            $(document).off("click",".total_cancel");
            $(document).on("click",".total_cancel",function(){
                 $(".tab_input").animate({
                    height:"0rem"
                 },200,"ease-out",function(){
                        $(".totalStock,.totalCost,.totalPrice").removeClass("active_border");
                        $(".input_value").val("");
                        totalSetIndex = 0;
                 });
            });

            $(document).off("click",".total_confirm");
            $(document).on("click",".total_confirm",function(){
                if(validate_inputValue()){
                     if(totalSetIndex ==0){
                         $(".number_value").val($(".input_value").val()).removeClass("borderRed");
                     }else if(totalSetIndex ==1){
                         $(".detail_cost").val($(".input_value").val()).removeClass("borderRed");
                     }
                     $(".input_value").val("");
                }else{
                    $.toast("请输入正确的信息",1300);
                    $(".input_value").val("");
                }
            });

            function validate_inputValue(){
                var inputValue = $(".input_value").val();
                if(inputValue != ""){
                    if(common.regMoney(inputValue)){
                        return true
                    }else {
                        return false;
                    }
                }else{
                   return true;
                }
            }

            $(document).on("input",".input_value",function(){
                common.inputMoney($(this));

            });

            $(document).on("input",'.number_value',function(){
                    common.inputMoney($(this))
                    if($(this).val() ==""){
                        $(this).val(0)
                    }
            });

            $(document).on("input",".detail_cost",function(){
                common.inputMoney($(this));

                if($(this).val() !=""){
                   if(common.regMoney($(this).val())){
                      if($(this).val().indexOf(".") < 0 && ($(this).val())[0] == 0){
                          $(this).addClass("borderRed");
                      }else{
                          $(this).removeClass("borderRed");
                      }
                   }else{
                         $(this).addClass("borderRed");
                   }
                }else{
                   $(this).removeClass("borderRed");
                }
            });

            $(document).on("click",".number_value",function(){
                var txtFocus = $(this)[0];
                var userAgent= window.navigator.userAgent.toLowerCase();
                var isWwebkit = /webkit/.test(userAgent);//谷歌
                if(isWwebkit){
                    txtFocus.setSelectionRange((txtFocus.value).length,(txtFocus.value).length);
                }
            });


            $(document).on("input",".number_value",function(){
                 if($(this).val() !=""){
                   if(common.regMoney($(this).val())){
                       $(this).removeClass("borderRed");
                   }else {
                        $(this).addClass("borderRed");
                   }
                 }else{
                       $(this).removeClass("borderRed");
                 }
            });

            /* plus and minus */
            $(document).off("click",".number_minus")
            $(document).on("click",".number_minus",function(){
                var stock = $(this).siblings("input");
                if(stock.hasClass("borderRed")){
                    $(this).siblings("input").val("").removeClass("borderRed");
                }else{
                    if(stock.val() ==""){
                       stock.val(0);
                    }
                    if(parseFloat(stock.val()) >= 1){
                     var calculateValue =  parseFloat(stock.val())-1;
                     $(this).siblings("input").val(calculateValue.toFixed(2));
                    }
                }
            });

            $(document).off("click",".number_plus");
            $(document).on("click",".number_plus",function(){
                var stock = $(this).siblings("input");
                if(stock.hasClass("borderRed")){
                    $(this).siblings("input").val("").removeClass("borderRed");
                }else{
                    if(stock.val() ==""){
                       stock.val(0);
                    }
                    if(parseFloat(stock.val()) >=0){
                     var calculateValue =  parseFloat(stock.val())+1;
                     $(this).siblings("input").val(calculateValue.toFixed(2));
                    }
                }
            });

            /* back page */
            $(document).off("click",".backPageBtn")
            $(document).on("click",".backPageBtn",function(){
                  window.history.back();
            });

            $(document).on("click", ".tab_choiceSupplier", function () {
                supplierModule.initSupplierList(scanProduct.shop_id,function (responseData) {
                    common.log("vendors list :" + JSON.stringify(responseData.response));
                    if (responseData.ack == 1 && common.isNotnull(responseData.response)) {
                        var customersGroupArray = JSON.parse(responseData.response);
                        var templateText ="";
                        $.each(customersGroupArray.retbody.supplier_list, function (key, supplierList) {
//                          templateText += '<li class="list-group-title">' + key + '</li>';
                            if (supplierList && supplierList.length > 0) {
                                $.each(supplierList, function (k, customer) {
                                    templateText += '<li class="shiment-cust" data-name="' + customer.supplier_name + '" ' +
                                        'data-cid="' + customer.supplier_id + '"  data-phone="' + customer.contact_phone + '" data-address="' + customer.address + '">' +
                                        '<div class="item-content"><div>' +
                                        '<input type="hidden" class="invoiceTitle" value="'+customer.invoice_title+'"/>' +
                                        '<input  type="hidden" class="memo" value="'+customer.desc_memo+'"/><div class="customer-name">' + customer.supplier_name + '</div>' +
                                        '<div class="customer-concatInfo">' + customer.contact_name + "," + customer.contact_phone + '</div></div></div></li>'
                                });
                            }
                        });
                        if(JSON.stringify(customersGroupArray.retbody.supplier_list) == "{}"){
                             templateText = '<li><div class="content-block" id="clickToAdd">暂无供应商，请先添加供应商</div></li>';
                        }
                        $("#supplierList").html(templateText);
                    } else {
                        $.toast("获取客户列表失败", 2000, 'error');
                    }
                    $.popup('.popup-supplier');
                })
            });

            //关闭popup
            $(document).on('click','.closePopup',function(){
                $.closeModal('.popup-supplier');
            });

            $(document).on("click", "#supplierList .shiment-cust", function (event) {
                chId = $(this).attr("data-cid");
                common.log("选择的客户姓名是：" + chId);
                $(".supplierName").val($(this).attr("data-name"));
                $.closeModal(".popup-supplier");
            });

            /* save stock in data */
            $(document).on("click", ".confirmButton", function (event) {
                if($(".detail_content").hasClass("borderRed") || $(".number_value").hasClass("borderRed")){
                    $.toast("请输入正确的信息");
                }else if ($(".supplierName").val()==""){
                    $.toast("请选择供应商");
                }else{

                        /* 选中正确的入库条目 */
                        for(var j = 0; j< $(".set-item").length;j++){
                             if( ($(".set-item").eq(j).find(".number_value").val() !="") && ($(".set-item").eq(j).find(".detail_cost").val() !="") ){
                                    $(".set-item").eq(j).addClass("hasChoice")
                             }
                        }
                        setTimeout(function(){
                            if($(".hasChoice").length >0){
                                    var productBuyInfo = {};
                                    productBuyInfo.buy_list = [];
                                    for(var i=0;i< $(".hasChoice").length;i++){
                                        var inventory_item ={};
                                        inventory_item.number =parseFloat($(".hasChoice").eq(i).find(".number_value").val());
                                        inventory_item.inventory_id = parseInt($(".hasChoice").eq(i).data("inventory_id"));
                                        inventory_item.price = $(".hasChoice").eq(i).find(".detail_cost").val();
                                        inventory_item.product_name = $(".hasChoice").eq(i).find(".title_name").text();
                                        inventory_item.supplier_id =parseInt(chId);
                                        inventory_item.warehouse_id = parseInt($(".warehouse_choice").data("warehouseid"));
                                        productBuyInfo.buy_list.push(inventory_item);
                                    }

                                    var userData = {
                                        method: NATIVE_METHOD_GET_USER_INFO,
                                        data: ""
                                    };
                                    common.jsCallNativeEventHandler(JS_CALL_NATIVE_EVENT_EXECUTE_NATIVE_METHOD, userData, function (data) {
                                        var userInfo = JSON.parse(data.response);
                                        common.log(JSON.stringify(userInfo));
                                        productBuyInfo.shop_id= parseInt(userInfo.shopID);

                                        common.log("入库商品对象productBuyInfo = " + JSON.stringify(productBuyInfo));
                                        salesModule.buyProduct(productBuyInfo, function (responseData) {
                                             var httpResponse = JSON.parse(responseData.response);
                                             if (responseData.ack == 1 && httpResponse.retcode == 200) {
                                                 window.history.go(-1);
                                             }else if(responseData.ack == 0 && httpResponse.retcode == 4026){
                                                 $.toast('权限不足，无法操作')
                                             } else {
                                                 $.toast('网络异常');
                                             }
                                        });
                                    })
                            }else{
                              $.toast('请填写正确的价格和数量')
                            }
                        },0)
                }
            });

        })
        $.init();
    });
})();



