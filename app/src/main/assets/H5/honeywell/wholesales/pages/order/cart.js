/**
 * (c) Copyright 2016 Administrator. All Rights Reserved.
 * 2016-05-19
 */
(function () {
    require(['zepto', 'salesModule', 'common'], function ($, salesModule, common) {

        $(document).on("pageInit", "#cartListPage", function (e, pageId, $page) {
            var totalPrice = 0;
            var selectedProductArray = [];
            var preSelectedCustomerId = "";

            $('#cartBar').hide();
            sessionStorage.removeItem("orderUserInfo");

            //购物车商品全选
            $(document).on("click", ".cart-product-json", function (event) {
                var currentGroupRadio = $(this).find("input[name='cart-customer-group-radio']")[0];
                var isGroupChecked = $(currentGroupRadio).prop("checked");

                $("input[type='radio']").prop("checked",false);
                $("input[type='checkbox']").prop("checked",false);

                if (isGroupChecked) {
                    common.log("进入取消全选逻辑");
                    // 取消选中的组
                    $(currentGroupRadio).prop("checked",false);
                    // 把所有的选中的商品取消选中
                    var productArrays = $(this).parents("li").find(".cart-proDetail-info");
                    $.each(productArrays, function (i, item) {
                        var cbox = $(item).find("input[type='checkbox']");
                        $(cbox).prop("checked",false);
                    })

                    // 清空所有选中的商品
                    selectedProductArray = [];
                    totalPrice = 0;
                    $('.addCustomerName').text('')

                } else {
                    common.log("进入开始全选逻辑");
                    // 选中当前组
                    $(currentGroupRadio).prop("checked", true);
                    totalPrice = 0;
                    // 选中的组中的所有商品
                    var pdInfoArrays = $(this).parents("li").find(".cart-proDetail-info");
                     selectedProductArray = [];
                    $.each(pdInfoArrays, function (i, item) {
                        var cbox = $(item).find("input[type='checkbox']");
                        $(cbox).prop("checked", true);
                        var productJson = JSON.parse($(item).attr("data-product-json"));

                        selectedProductArray.push(productJson);

                        var price = productJson.unitPrice, num = productJson.totalNumber;
                        common.log("price=" + price, "num=" + num);
                        totalPrice += price * num;
                    })
                    $('.addCustomerName').text($(this).find('.item-title-row .cust-name-radio').text())

                }

                $(".product-selMoney").text("￥" + totalPrice);
                //添加客户名

                event.stopPropagation();
                event.preventDefault();
            })

            //判断是否有商品选中,获取所属商品的下单客户ID标识 ？
            function isProductSelected(currentCid) {
                var currentCBoxs = $("#box").find("input[name='product_cartInfo']");
                common.log("选中了值: currentCBoxs.length＝" + currentCBoxs.length);

                if (currentCBoxs && currentCBoxs.length > 0) {
                    for (var k = 0; k < currentCBoxs.length; k++) {
                        // 当前checkbox是选中状态
                        if ($(currentCBoxs[k]).prop("checked")) {
                            preSelectedCustomerId = $(currentCBoxs[k]).val();
                            common.log("选中了值:" + preSelectedCustomerId);
                            return true;
                        }
                    }
                }
                return false;
            }

            /**
             * 单个商品的选择
             * 1.选中的时候  移除其它下单客户所选中的商品
             * 2.计算价格
             * 3.选中当前商品,选中当前客户,计算价格
             */
            $(document).on("click", ".cart-proDetail-info", function (event) {
                event.stopPropagation();
                event.preventDefault();

                var customerId =$(this).attr("data-customerId");
                var productJsonData = JSON.parse($(this).attr("data-product-json"));
                var currentCheckBox = $(this).find("input[name='product_cartInfo']")[0];

                common.log("当前选中商品所属开单客户的ID值preSelectedCustomerId=" + preSelectedCustomerId + ",customerId = " + customerId);

                // 换了个用户，需要去掉之前选的商品，把价格推领
                if (preSelectedCustomerId != customerId) {
                    common.log("新选的不是同一个客户");
                    selectedProductArray = [];
                    totalPrice = 0;
                    //移除掉上一次选择的用户的所有商品，下面方法直接把所有选择框都去掉
                    $("#cart-customer-group-" + preSelectedCustomerId).find("input").prop("checked",false);
                }
                preSelectedCustomerId = customerId;

                selectProduct(customerId, currentCheckBox, productJsonData);

                //添加的判断是否全选功能
                  var groupCheckBox = $("#cart-customer-group-" + customerId).find("input[name='cart-customer-group-radio']");
                  var allSelected = $(".cart-proDetail-info").find("input[name='product_cartInfo']:checked");
                  var allcheckbox =$(".cart-proDetail-info").find("input[name='product_cartInfo']");

                    if(allSelected.length == allcheckbox.length){
                        $(groupCheckBox).prop("checked",true);
                    }else{
                         $(groupCheckBox).prop("checked",false);
                    }

                //添加选中的客户名
                if($(this).find("input[name='product_cartInfo']").prop('checked')){
                      $('.addCustomerName').text($(this).parent('li').find('.customerNameInfo').text());
                }else{
                    $('.addCustomerName').text('')
                }

            })

            /**
             * 控制单个商品的checkbox选中与否
             * @param customerId
             * @param currentCheckBox
             * @param productJsonData
             */
            function selectProduct(customerId, currentCheckBox, productJsonData) {
                // 把group也选中
                var groupCheckBox = $("#cart-customer-group-" + customerId).find("input[name='cart-customer-group-radio']");

                if ($(currentCheckBox).prop("checked")) {
                    common.log("这个checkBox是选中状态");
                    $(currentCheckBox).prop("checked",false);

                    $.each(selectedProductArray, function (i, product) {
                        common.log("数组中的对象product=" + JSON.stringify(product));
                        if (product.productId == productJsonData.productId) {
                            // 从数组中删除
                            selectedProductArray.splice(i, 1);
                        }
                    })


                     //没有商品被选中的时候，把group的checkbox勾选去掉
                    if(selectedProductArray.length == 0){
                        $(groupCheckBox).prop("checked",false);
                    }

                    // 计算价格
                    var price = productJsonData.unitPrice, num = productJsonData.totalNumber;
                    totalPrice -= price * num;

                } else {
                    common.log("这个checkBox是没有选中");
                    $(currentCheckBox).prop("checked", true);
                    selectedProductArray.push(productJsonData);
                    // 计算价格
                    var price = productJsonData.unitPrice, num = productJsonData.totalNumber;
                    totalPrice += price * num;

                }
                $(".product-selMoney").text("￥" + totalPrice);

            }

            function showAllCarts(allCustomerCartJsonArray) {
                var tempHtml = '';
                totalPrice = 0;
                selectedProductArray = [];
                preSelectedCustomerId = "";
                $("#box").empty();

                if (allCustomerCartJsonArray != null && allCustomerCartJsonArray.length > 0) {

                    $("#cartBar").show();
                    $("#box").show();
                    $("#hasNoProInCart").hide();
                    common.log("showAllCarts（）总共获取到 " + allCustomerCartJsonArray.length + " 个客户的数据。");

                    // 这里循环所有的客户
                    for (var i = 0; i < allCustomerCartJsonArray.length; i++) {
                        common.log("获取购物车第" + i + "组数据Array = " + JSON.stringify(allCustomerCartJsonArray[i]));
                        //var oneCustomerCartJsonArray = allCustomerCartJsonArray[i];
                        var oneCustomerCartJsonArray = eval(allCustomerCartJsonArray[i]);
                        // 这里循环第i个客户的所有的商品
                        for (var j = 0; j < oneCustomerCartJsonArray.length; j++) {
                            // 从第i个客户的所有商品中的第一个产品中，取出客户名字
                            var customerId = oneCustomerCartJsonArray[j].customerId;
                            if (i == 0 && j == 0) {
                                var firstCartItem = oneCustomerCartJsonArray[0];
                                //第一个客户的信息调用第一个模板
                                var customerNameUI = $("#cart-customer-group-title").html()
                                    .replace("{{custName}}", firstCartItem.customerName)
                                    .replace("{{customerId}}", firstCartItem.customerId)
                                    .replace("{{customerId}}", firstCartItem.customerId);
                                $("#box").append(customerNameUI);
                                common.log("i&j==0, customerNameUI=" + customerNameUI);
                            } else if (i != 0 && j == 0) {
                                //第二个或者以上的客户调用第二个模板
                                var firstCartItem = oneCustomerCartJsonArray[0];
                                var customerNameUI = $("#cart-customer-group-title-notTop").html()
                                    .replace("{{custName}}", firstCartItem.customerName)
                                    .replace("{{customerId}}", firstCartItem.customerId)
                                    .replace("{{customerId}}", firstCartItem.customerId);
                                $("#box").append(customerNameUI);
                                common.log("i != 0 && j == 0, customerNameUI=" + customerNameUI);
                            }

                            // 这里循环显示第i个客户的每一件商品
                            var oneCartItem = oneCustomerCartJsonArray[j];
                            common.log("客户" + i + "的第" + j + "件商品oneCartItem=：" + JSON.stringify(oneCartItem));
                            var firstImage = "../../images/icons/icon-product.png";   // 默认图片
                            if(common.isNotnull(oneCartItem.imageUrl)) {
                                try {
                                    var picImgArray = eval('(' + oneCartItem.imageUrl + ')');
                                    if (picImgArray != null && picImgArray.length > 0) {
                                        firstImage = picImgArray[0];
                                    }
                                } catch (e) {
                                    common.log("商品图片加载出错: " + oneCartItem.imageUrl);
                                }

                            }
                            common.log("图片地址firstImage = " + firstImage);

                            var productListHtml = $("#cart-product-list").html();
                            productListHtml = productListHtml.replace("{{productName}}", oneCartItem.productName)
                                .replace("{{productImage}}", firstImage)
                                .replace("{{unitPrice}}", oneCartItem.unitPrice)
                                .replace("{{unit}}", oneCartItem.unit)
                                .replace("{{totalNumber}}", oneCartItem.totalNumber)
                                .replace("{{customerId}}", oneCartItem.customerId)
                                .replace("{{customerJson}}", JSON.stringify(oneCartItem));
                            $("#cart-customer-group-" + customerId).append(productListHtml);
                            common.log(productListHtml);
                        }
                    }

                    common.log("totalPrice = " + totalPrice);
                    $(".product-selMoney").text("￥" + totalPrice);
                } else {
                    $("#hasNoProInCart").show();
                    $("#box").hide();
                    $("#cartBar").hide();
                    $.hideIndicator();
                }
            };

            common.connectJsTunnel(function (jsTunnel) {
                jsTunnel.init();
                // 注册页面数据监听事件，可以收到底层传来的更新数据
                jsTunnel.registerHandler(
                    NATIVE_CALL_JS_EVENT_TRANSFER_DATA,
                    function (data, responseCallback) {
                        common.log("获取购物车的所有商品信息集合" + JSON.stringify(data));
                        var allCustomerCartJsonArray = eval(data)
                        showAllCarts(allCustomerCartJsonArray);
                    }
                );
            });

            //清空
            $(document).on("click", ".removeCartByCustomerID", function (event) {
                //TODO 这里模拟了删除散客的动作，需要把customerId替换成真实的customerId
                var customerId =$(this).attr("data-customerId");
                common.log("获取到的用户ID = " + customerId);
                var customerIdJson = {
                    customerId: customerId,
                };
                var removeCartEvent = {
                    method: NATIVE_METHOD_CART_REMOVE_BY_CUSTOMER_ID,
                    data: JSON.stringify(customerIdJson)
                };
                common.jsCallNativeEventHandler(JS_CALL_NATIVE_EVENT_EXECUTE_NATIVE_METHOD, removeCartEvent, function (data) {
                    common.log("清除客户完成。");
                });

                // 删除完以后再重新刷新页面
                var getAllCartEvent = {
                    method: NATIVE_METHOD_CART_GET_ALL,
                    data: ""
                };
                common.jsCallNativeEventHandler(JS_CALL_NATIVE_EVENT_EXECUTE_NATIVE_METHOD, getAllCartEvent, function (data) {
                    var jsonArray = eval(data.response);
                    selectedProductArray = [];
//                    $.showIndicator();
                    showAllCarts(jsonArray);
                });
                event.stopPropagation();
                event.preventDefault();
            })

            //结算
            $(document).on("click", "#checkout", function () {
                if (selectedProductArray && selectedProductArray.length > 0) {
                    common.log("selectedProductArray=" + JSON.stringify(selectedProductArray), selectedProductArray.length);
                        var userData = {
                            method: NATIVE_PAGE_CHECKOUT,
                            data: JSON.stringify(selectedProductArray)
                        };
                        common.jsCallNativeEventHandler(JS_CALL_NATIVE_EVENT_REQUEST_SWITCH_PAGE, userData, function (data) {
                            common.log("传输过去的数据="+data);
                            common.log("跳转到确认订单页面！");
                            selectedProductArray = [];
                        });
                    }
                else{
                    $.toast("您还没有选择商品");
                }
            })

        })
        $.init();
        ///end
    });
})();