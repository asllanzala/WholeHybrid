(function () {
    require(['zepto', 'common', 'salesModule'], function ($, common, salesModule) {
        //stockInPage
        $(document).on("pageInit", "#stockInConfirmPage", function (e, pageId, $page) {
            common.log("进入补登页面");
            var stockRecordId;
            common.connectJsTunnel(function (jsTunnel) {
                jsTunnel.init();
                jsTunnel.registerHandler(
                    NATIVE_CALL_JS_EVENT_TRANSFER_DATA,
                    function (data, responseCallback) {
                        common.log("补登页面：" + JSON.stringify(data));
                        stockRecordId = data.stockRecordId;
                        $(".quantity").val(data.number);
                        $("#vendor").val(data.vendor);
                        $(".product_name").html(data.productName);
                        $(".product_code").html("货号 " + data.productCode);
                        $("#stockPrice").val('');
                        $(".totalPrice").val('');
                    }
                );
            });

            $(document).on("click", "#confirm_stockInfo_btn", function () {
                var stockPrice = $(".stockPrice").val();
                common.log("入库商品名: 单价＝" + stockPrice + ", stockRecordId=" + stockRecordId);
                if (common.isNotnull(stockPrice)) {
                    $.showIndicator();
                    var stockInfo = {
                        stock_record_id: parseInt(stockRecordId),
                        price: stockPrice
                    }
                    common.log("补登商品=" + JSON.stringify(stockInfo));

                    salesModule.stockInConfirm(stockInfo, function (data) {
                        common.log("补登服务器返回结果：data=" + JSON.stringify(data));
                        var responseJson = JSON.parse(data.response);
                        // 4023 已经确认入库了
                        if (data.ack == 1 || responseJson.retcode == 4023) {
                            //从本地数据库里面去把这条记录设置为已经确认入库
                            var stockJson = {
                                stockRecordId: stockRecordId
                            }
                            common.log("补登成功：stockRecordId=" + stockRecordId);
                            var stockEvent = {
                                method: NATIVE_METHOD_PRODUCT_STOCK_CONFIRM,
                                data: JSON.stringify(stockJson)
                            };
                            common.jsCallNativeEventHandler(JS_CALL_NATIVE_EVENT_EXECUTE_NATIVE_METHOD, stockEvent, function (data) {
                                var backHomeData = {
                                    method: NATIVE_PAGE_DASHBOARD_HOME,
                                    data: ""
                                };
                                common.jsCallNativeEventHandler(JS_CALL_NATIVE_EVENT_REQUEST_SWITCH_PAGE, backHomeData, function (data) {
                                    common.log("打开主页面完成！");
                                });
                                common.log("补登价格成功");
                            });
                        } else {
                            $.toast(data.msg);
                        }
                        // 完成
                        $.hideIndicator();
                    });
                } else {
                    $.alert("请输入商品单位进价或总进价");
                }
                return false;
            })

            //单价和数量自动计算总进价
            $(document).on("input propertychange", ".stockPrice", function () {
                var sprice = $(".stockPrice").val(), num = $(".num").val();
                if (isNaN(sprice)) {
                    var pinyin = sprice.substr(sprice.length - 1, 1);
                    $(".stockPrice").val(sprice.replace(pinyin, ""))
                    common.log($(".stockPrice").val());
                }
                common.log(common.isNotnull(num) && !(isNaN(sprice)&& isNaN(num)));
                if (common.isNotnull(num) && !(isNaN(sprice)&& isNaN(num))) {
                    var totalPrice = (parseFloat($(".stockPrice").val()) * parseFloat(num)).toFixed(2);
                    $(".totalPrice").val(totalPrice);
                }
            })
            $(document).on("input propertychange", ".totalPrice", function () {
                var totalPrice = $(".totalPrice").val(), num = $(".num").val();
                if (isNaN(totalPrice)) {
                    var pinyin = totalPrice.substr(totalPrice.length - 1, 1);
                    $(".totalPrice").val(totalPrice.replace(pinyin, ""))
                }
                common.log(totalPrice,num);
                common.log(!(isNaN(totalPrice) && isNaN(num)));
                if (common.isNotnull(num)&& !(isNaN(totalPrice) && isNaN(num))) {
                    $(".stockPrice").val((parseFloat($(".totalPrice").val()) / parseInt(num).toFixed(2)).toFixed(2));
                }
            })
            $(document).on("input propertychange", ".num", function () {
                var sprice = $(".stockPrice").val(), num = $(".num").val(),totalPrice = $(".totalPrice").val();
                if (isNaN(num)) {
                    var pinyin = num.substr(num.length - 1, 1);
                    $(".num").val(num.replace(pinyin, ""))
                }
                common.log(common.isNotnull(sprice) && !(isNaN(sprice)&& isNaN(num)));
                if (common.isNotnull(sprice) && !(isNaN(sprice)&& isNaN(num))) {
                    var totalPrice = (parseFloat($(".stockPrice").val()) * parseFloat(num)).toFixed(2);
                    $(".totalPrice").val(totalPrice);
                }
                if(common.isNotnull(totalPrice) && !isNaN(totalPrice)){
                    $(".stockPrice").val((parseFloat(totalPrice) / parseInt(num).toFixed(2)).toFixed(2));
                }
            })
        })
        $.init();
    });
})();



