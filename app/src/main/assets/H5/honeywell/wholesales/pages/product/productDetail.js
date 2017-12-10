/**
 * (c) Copyright 2016 Administrator. All Rights Reserved.
 * 2016-05-19
 */
function getQueryString(name) {
    var reg = new RegExp("(^|\\?|&)" + name + "=([^&]*)(\\s|&|$)", "i");
    if (reg.test(location.href)){
//        console.log("------:" + RegExp.$2.replace(/\+/g, " "));
        return decodeURI(RegExp.$2.replace(/\%20/g, " "));
    }
//        return unescape(RegExp.$2.replace(/\+/g, " "));
    return null;
}

var product_code = getQueryString("barcode"),
    product_number = getQueryString("productNumber") ? getQueryString("productNumber") : "",
    warehouse_id = getQueryString("warehouseId") ? getQueryString("warehouseId") : "";

if(sessionStorage.getItem("changedProductNumber")){
    product_number = sessionStorage.getItem("changedProductNumber");
//    sessionStorage.removeItem("changedProductNumber");
}

if(sessionStorage.getItem("changedProductCode")){
    product_code = sessionStorage.getItem("changedProductCode");
//    sessionStorage.removeItem("changedProductCode");
}

var shopId = getQueryString("shopid"),product_id = getQueryString("productId")? getQueryString("productId") : "";

    sessionStorage.setItem("shopId", shopId);
    sessionStorage.setItem("mData", product_code);
    sessionStorage.setItem("productNumber",product_number);

var scanProduct = "", counter = 1,changedNumber =0,changedUnit="";

(function () {
    require(['zepto', 'common', 'salesModule', 'supplierModule'], function ($, common, salesModule, supplierModule) {
        //scan page
        $(document).on("pageInit", "#scanProPage", function (e, pageId, $page) {
            console.log("获取的Url="+location.href);
            var scanUrl = location.href;
            var urlCustomerId = getQueryString("customerId");
            var urlCustomerName =  getQueryString("customerName");
            if(urlCustomerId){
                sessionStorage.setItem("urlCustomerId",urlCustomerId);
                sessionStorage.setItem("urlCustomerName",urlCustomerName);
            }
            
//            counter++;
//            if (counter % 2 == 0) {
//                 $.showIndicator();
//            }

            common.connectJsTunnel(function (jsTunnel) {
                jsTunnel.init();

                var productData ={
                    "shop_id":parseInt(shopId),
                }

                if(product_id > -1){
                   productData.product_id  = product_id;
                }

                if(product_code){
                   productData.product_code = product_code;
                }

                if(product_number){
                   productData.product_number = product_number;
                }

                if(warehouse_id !=0 ){
                    productData.warehouse_id = warehouse_id ;
                }

                console.log("传入的数据="+JSON.stringify(productData));
                queryProductDetail(productData);
            });

            /**
             * 查询商品详细信息
             * @param shopId
             * @param product_code
             */
            function queryProductDetail(productData) {
                salesModule.querySaleDetail(productData,function (httpResponse) {
                    common.log("查询的产品数据:" + JSON.stringify(httpResponse));
                    var data = JSON.parse(httpResponse.response);
                    if (httpResponse.ack == 1) {
                        if (data.retcode == 200) {
                            var contentHtml = $("#pd-content").html();
                            $("#prodetail-swiper").append(contentHtml);
                            $("#scanBottom").css("display", "block");
                            scanProduct = data.retbody;
                            console.log("获取到的SKU="+JSON.stringify(scanProduct));
                            changedNumber = scanProduct.total_quantity;
                            changedUnit = scanProduct.unit;
                            product_id = scanProduct.product_id;
                            sessionStorage.setItem("product_id",product_id);
                            scanProduct.shop_id = shopId;

                            sessionStorage.setItem("scanProduct", JSON.stringify(scanProduct));

                            if(scanProduct.inventory_list[0].sku_value_id1){
                                $(".ws_code").text(scanProduct.product_number? "货号"+scanProduct.product_number:"货号");
                                $(".ws_number").text(scanProduct.product_code ? scanProduct.product_code:"");
                                $(".ws_product_name").html(scanProduct.product_name);
                                $(".standard_price").html(scanProduct.standard_price_range ? "￥"+ scanProduct.standard_price_range :"");
                                $(".scan_price").text(scanProduct.standard_price_range ? "¥" + scanProduct.standard_price_range :"");
                                $(".number").html(scanProduct.total_quantity ? scanProduct.total_quantity +(scanProduct.unit_name ? scanProduct.unit_name:"")  : "0");
                                $("#pd_avgPrice").html(scanProduct.stock_price_range ? "￥"+scanProduct.stock_price_range:"");
                                $(".info_stock").text(scanProduct.total_quantity ? scanProduct.total_quantity +(scanProduct.unit_name ? scanProduct.unit_name:""): 0);
                                $(".info_priceQuery").text(scanProduct.standard_price_range ? "￥"+ scanProduct.standard_price_range :"")
                            }else{
                               $(".ws_code").text(scanProduct.inventory_list[0].product_number ? "货号"+scanProduct.inventory_list[0].product_number :"货号");
                               $(".ws_number").text(scanProduct.inventory_list[0].product_code ? scanProduct.inventory_list[0].product_code :"");
                               $(".ws_product_name").html(scanProduct.product_name);
                               $(".standard_price").html(scanProduct.inventory_list[0].standard_price ? "￥"+ scanProduct.inventory_list[0].standard_price :"");
                               $(".scan_price").text(scanProduct.inventory_list[0].standard_price ? "￥"+ scanProduct.inventory_list[0].standard_price :"");
                               $(".number").html(scanProduct.total_quantity ? scanProduct.total_quantity+(scanProduct.unit_name ? scanProduct.unit_name:"") : "0");
                               $("#pd_avgPrice").html(scanProduct.inventory_list[0].stock_price ? "￥"+scanProduct.inventory_list[0].stock_price:"");
                               $(".info_stock").text(scanProduct.total_quantity ? scanProduct.total_quantity+(scanProduct.unit_name ? scanProduct.unit_name:"") :0);
                               $(".info_priceQuery").text(scanProduct.inventory_list[0].standard_price ? "￥"+ scanProduct.inventory_list[0].standard_price :"")
                            }

                            var supplierNames ="";
                            if(scanProduct.suppliers && (scanProduct.suppliers).length > 0){
                               $.each(scanProduct.suppliers,function(key,supplierN){
                                    if(key < (scanProduct.suppliers).length -1){
                                       supplierNames += supplierN.supplier_name +",";
                                    }else{
                                       supplierNames += supplierN.supplier_name;
                                    }
                               });
                            }
                            $("#pd_unit").html(scanProduct.unit_name ? scanProduct.unit_name:"");
                            $("#pd_supplier").html(supplierNames ? supplierNames:"");
                            $("#pd_type").html((scanProduct.categorys).length > 0 ? (scanProduct.categorys)[0].category_name: "");
                            $("#pd_zxl").html(scanProduct.sale_number ? scanProduct.sale_number +(scanProduct.unit_name ? scanProduct.unit_name:"") : "");
                            $(".describe_text").text(scanProduct.describe ? scanProduct.describe:"");
                            var picImgs = scanProduct.pic_hd_src, imgHtml = "";
                            if (picImgs != null && picImgs.length > 0) {
                                $.each(picImgs, function (index, item) {
                                    imgHtml += '<div class="swiper-slide"><img src="' + item + '" width="100%" height="240px"  style="object-fit:cover;"></div>'
                                });
                            } else {
                                imgHtml = '<div class="swiper-slide"><img src="../../images/newUi/defaultImg-icon.png" width="100%" height="240px"  style="object-fit:cover;"></div>';
                            }
                            $(".swiper-wrapper").empty().append(imgHtml);
                            $(".swiper-container").swiper({
                                speed: 200,
                                autoplay: 5000,
                                pagination : '.swiper-pagination',
                            });

                            $("#scanProPage").addClass("page-current");
                        }
                    } else if (httpResponse.ack == 0) {
                        if (data.retcode == 4006 || data.retcode == 4007) {
                            $.confirm("货品号" + product_code + "的商品信息尚不存在数据库中.是否需要创建新商品.", "未知商品", function () {
                                window.location.href = "addProduct.html";
                            }, function () {
                                $.closeModal();
                                common.backHomePage();
                            }, "放弃", "新建");
                        }else if(data.retcode == 180000021){
                               $.toast("货品未在当前仓库入库");
                        } else {
                            $.confirm("因网络原因暂时未能获取商品信息,请稍候重试", "网络问题", function () {
                                queryProductDetail(shopId, product_code);
                            }, function () {
                                common.backHomePage();
                            }, "放弃", "重试");
                        }
                    }
                       //关闭加载动画
                    $('.indicator-background,.indicator-div').remove();
                });
            }


            $(".open-supplier").on('click', function () {
                $.popup('.popup-supplier');
            });

            // 点页面其他地方让弹出框消失
            $(document).on("click", "#menu", function (event) {
                var isVisible = $("#secMenu").attr("data-visible");
                if(isVisible == "true"){
                  $("#secMenu").hide();
                  $("#secMenu").attr("data-visible", false);
                } else {
                  $("#secMenu").show();
                  $("#secMenu").attr("data-visible", true);
                }
            });

            $(document).on("click","#openDeal",function(){
                sessionStorage.setItem("fromH5ToDeal","openDeal");
                var scanProduct = sessionStorage.getItem("scanProduct");
                    scanProduct =JSON.parse(scanProduct);
                $.popup(".popup-choice_product_sku");
                /* transform data to native*/
                    $(".info_name").text(scanProduct.product_name);
                     $(".header_img img").attr("src",scanProduct.pic_hd_src[0] ? scanProduct.pic_hd_src[0] :"../../images/default.jpeg")
                    $(".info_stock").text(scanProduct.total_quantity ? "库存"+ scanProduct.total_quantity +"件" :"0件");
                    var list_item_template ="";
                    if(scanProduct.inventory_list[0].sku_value_id1){
                        $.each(scanProduct.inventory_list,function(key,value){
                              list_item_template += $("#list_item").html()
                                                    .replace("{{data-inventory_id}}",value.inventory_id)
                                                    .replace("{{sku_value_name}}",value.sku_name)
                                                    .replace("{{quantity}}",value.quantity ?value.quantity:0)
                                                    .replace("{{standard_price}}",value.standard_price)
                                                    .replace("{{data_standard_price}}",value.standard_price)
                        })
                        $(".content_lists").html(list_item_template);
                        for(var i =0;i<$(".list_item").length;i++){
                            if(parseFloat($(".left_stock").eq(i).text()) > 0){
                                 $(".list_item").eq(i).addClass("hasStock");
                            }
                        }
                    }else{
                         $.each(scanProduct.inventory_list,function(key,value){
                             list_item_template +=  $("#list_item").html()
                                                    .replace("{{data-inventory_id}}",value.inventory_id)
                                                    .replace("{{sku_value_name}}",value.sku_name)
                                                    .replace("{{quantity}}",value.quantity ?value.quantity:0)
                                                    .replace("{{standard_price}}",value.standard_price)
                                                    .replace("{{data_standard_price}}",value.standard_price)
                         });
                        $(".content_lists").html(list_item_template);
                        if(parseFloat($(".left_stock").text()) > 0){
                            $(".list_item").addClass("hasStock");
                        }
                    }
                 });

                 $(document).on("input",".right_value",function(){
                    common.inputMoney($(this));
                    if(parseFloat($(this).parent().siblings(".detail_left").find(".left_stock").text()) >= (parseFloat($(this).val())? parseFloat($(this).val()):0)) {
                          if($(this).val() !=""  ){
                            if(common.regMoney($(this).val()) || $(this).val() == 0){
                                $(this).removeClass("borderRed");
                            }else{
                                 $(this).addClass("borderRed");
                            }
                          }else{
                              $(this).removeClass("borderRed");
                          }
                    }else{
                         $.toast("销售数量不能大于库存");
                         $(this).val(0);
                         $(this).val(parseFloat($(this).parent().siblings(".detail_left").find(".left_stock").text()));
                    }

                 });

                 /* plus and minus */
                 $(document).on("click",".right_minus",function(){
                     var stock = $(this).siblings("input");
                     var stockNumber = parseFloat(stock.val())? parseFloat(stock.val()):0;
                     if(stock.hasClass("borderRed")){
                         stock.val(0).removeClass("borderRed");
                     }else{
                        if(stockNumber >=1){
                          var calculateValue =  stockNumber - 1;
                          stock.val(calculateValue.toFixed(2));
                        }
                     }
                 });

                 $(document).on("click",".right_plus",function(){
                     if( parseFloat($(this).parent().siblings(".detail_left").find(".left_stock").text()) > parseFloat($(this).siblings("input").val()) ){
                         var stock = $(this).siblings("input");
                         var stockNumber = parseFloat(stock.val())? parseFloat(stock.val()):0;
                         if(stock.hasClass("borderRed")){
                             stock.val(0).removeClass("borderRed");
                         }else{
                            if(stockNumber >=0){
                              var calculateValue =  stockNumber + 1;
                              stock.val(calculateValue.toFixed(2));
                            }
                         }
                     }else{
                        $.toast("销售数量不能大于库存");
                        stock.val(parseFloat($(this).parent().siblings(".detail_left").find(".left_stock").text()));
                     }
                 });

                 $(document).on("click",".confirm_cancel",function(){
                     var contentHtml = $("#pd-content").html();
                     $("#prodetail-swiper").append(contentHtml);
                     $.closeModal(".popup-choice_product_sku");
                 });

                 $(document).off("click",".confirm_can");
                 $(document).on("click",".confirm_can",function(){
                     var noStock =false;
                     if($(".borderRed").length < 1){
                         if($(".hasStock").length >0){

                            for(var i =0;i<$(".hasStock").length;i++){
                                if(parseFloat($(".hasStock").eq(i).find(".left_stock").text()) < parseFloat($(".hasStock").eq(i).find(".right_value").val())){
                                      $(".hasStock").eq(i).addClass("noEnoughStock");
                                }else{
                                  if($(".hasStock").eq(i).find(".right_value").val() > 0 && (parseFloat($(".hasStock").eq(i).find(".left_stock").text()) >= parseFloat($(".hasStock").eq(i).find(".right_value").val()))){
                                        $(".hasStock").eq(i).addClass("hasToSale");
                                   }else {
                                        $(".hasStock").eq(i).removeClass("hasToSale");
                                   }
                                }
                            }

                            if($(".hasToSale").length > 0){
                                var productInfo =  sessionStorage.getItem("scanProduct");
                                    productInfo = JSON.parse(productInfo);

                                var inventory_list_toNative =[];
                                for(var k =0;k< productInfo.inventory_list.length;k++){
                                  for(var j =0 ;j<$(".hasToSale").length;j++){
                                       if(productInfo.inventory_list[k].inventory_id == parseInt($(".hasToSale").eq(j).find(".content_name").data("inventory_id"))){
                                            productInfo.inventory_list[k].sale_amount = $(".hasToSale").eq(j).find(".right_value").val();
                                            inventory_list_toNative.push(productInfo.inventory_list[k]);
                                       }
                                  }
                                }
                                productInfo.inventory_list = inventory_list_toNative;
                                 var productEvent = {
                                     method:NATIVE_METHOD_PRODUCT_DETAIL_NEW_ORDERS,
                                     data:JSON.stringify(productInfo)
                                 };
                                common.jsCallNativeEventHandler(
                                     JS_CALL_NATIVE_EVENT_REQUEST_SWITCH_PAGE,
                                     productEvent,
                                     function (data) {
                                 });
                             }else if($(".noEnoughStock").length >0){
                                $.toast("货品无充足库存");
                             }else{
                                $.toast("请选择商品，输入数量")
                             }

                         }else{
                            $.toast("货品无库存，请先入库")
                         }
                       }else{
                          $.toast("请输入正确的信息");
                       }
                 });

            // 调整库存
            $(document).on("click", "#pd-update-numsInfo", function () {
                $("#secMenu").hide();
                location.href = "";
            });

            // 退回主页面
            $(document).on("click", "#backHomeBtn", function() {
                common.backHomePage();
            });

            //编辑商品
            $(document).on("click", "#pd-edit-info", function () {
                $("#secMenu").hide();
                location.href = "addProduct.html?flag=1";
            });

            //check stock in list
            $(document).on("click","#checkStockList",function(){
                location.href="stockRecord.html";
            });

            //删除商品
            $(document).on("click", "#delelte-product", function () {
                $("#secMenu").hide();
                // 删除商品
                common.log("开始删除商品");
                $.confirm("删除该商品？", "温馨提示", function () {
                    //$.closeModal();
                    //从服务器端删除这件商品
                    salesModule.deleteProduct(shopId,product_id, function (responseData) {
                        common.log("删除商品完成:" + JSON.stringify(responseData));
                        var jsonData = JSON.parse(responseData.response);
                        if (jsonData.retcode == 200) {
                            //从本地数据库里面去删除这件商品
                            var productInfoJson = {
                                shopId: shopId,
                                productId: product_id
                            }
                            var deleteProductEvent = {
                                method: NATIVE_METHOD_PRODUCT_DELETE,
                                data: JSON.stringify(productInfoJson)
                            };
                            common.jsCallNativeEventHandler(JS_CALL_NATIVE_EVENT_EXECUTE_NATIVE_METHOD, deleteProductEvent, function (data) {
                                var backHomeData = {
                                    method: NATIVE_PAGE_DASHBOARD_HOME,
                                    data: ""
                                };
                                common.jsCallNativeEventHandler(JS_CALL_NATIVE_EVENT_REQUEST_SWITCH_PAGE, backHomeData, function (data) {
                                    common.log("打开主页面完成！");
                                });
                                common.log("从本地删除商品数据完成。");
                            });
                        } else if (jsonData.retcode == 180000016) {
                            $.toast("删除失败，请先删除该货品的SKU库存");
                        }else if(jsonData.retcode == 180000019){
                            $.toast("删除失败，请先清空库存再删除商品");
                        }else if(jsonData.retcode == 180000020){
                             $.toast("货品已出售，无法删除");
                        } else {
                            $.toast(jsonData.message);
                        }
                    });
                }, function () {
                    //$.closeModal();
                }, "取消", "确认");
            });
        });

       /*purchase record*/
       $.init();
    });
})();



