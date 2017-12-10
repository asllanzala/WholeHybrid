/**
 * Created by Lee on 16/10/18.
 */
(function(){
    require(['zepto', 'common', 'salesModule', 'template', 'customerModule', 'supplierModule'], function ($, common, salesModule, template, custModule, supplierModule) {
        $(document).on("pageInit", "#orderDetailPage", function () {
             var creditType  = 0,
                 orderNumber = 0,
                 printShopId = 0,
                 orderRequest ={},
                 pay_type_finish =0,
                 current_employee_name = "",
                 webOrderNumber ="",
                 userRole=null;


              //订单作废
             function cancelOrder(orderNum){
                  $(document).on("click","#cancelOrderBtn",function(){
                     if(userRole == 0){
                          $("#orderCancel").css("display","block");
                          $(document).off("click","#cancelConfirm");
                          $("#selectPayMethod,#clearCreditOrder,.needPayTime").css("display","none");
                          $("#cancelText").val("");
                          //$.popup("#orderCancel");
                          $(document).on("click","#iconfont-cancel",function(){
                               $("#orderCancel").css("display","none");
                          });
                          $(document).on("click","#cancelConfirm",function(){
                              $.showIndicator();
                              $("#orderCancel").css("display","none");
                              var cancelContent = $("#cancelText").val();
                                  var cancelRequest ={
                                      "sale_id":orderNum,
                                      "cancel_comment":cancelContent,
                                  };
                                  var cancelData = common.dataTransform('order/cancel','POST',cancelRequest)
                                  common.jsCallNativeEventHandler(
                                      JS_CALL_NATIVE_EVENT_REQUEST_WEB_API
                                      , cancelData
                                      , function(data) {
                                          common.log("取消订单="+JSON.stringify(data));
                                          var respondData =data.response;
                                          respondData =JSON.parse(respondData);
                                          var retbody = respondData.retbody;
                                          if(respondData.retcode ==200){
                                              $(".needPayTitle").text("账单已取消").css("color","#8b888d");
                                              $("#needPayNum").css("color","#8b888d");
                                              $(".btnRow,.creditDiv,.clearDiv").css("display","none");
                                              $(".cancelDiv,.remarkDiv").css("display","block");
                                              $("#cancelOrderBtn,.bottom_bar").css("display","none");
                                              $(".order").css("bottom","0");
                                              $("#orderTime-cancel").text(common.translateTime(retbody.cancel_dt));
                                              $("#cancelTextContent").text(cancelContent);
                                              $("#employeeName-cancel").text(current_employee_name);
                                              $.hideIndicator();
                                          }else if(respondData.retcode ==4021){
                                             common.alertTips("订单不存在");
                                          }else{
                                             common.alertTips("未取消成功");
                                            $.hideIndicator();
                                          }
                                      }
                                  );
                          });
                     }else{
                        $.toast("权限不足，无法取消订单")
                     }
                  });
             }

             //选择赊账
             function selectCredit(creditRequest){
                     $.showIndicator()
                     var creditData = common.dataTransform('order/pay/set_type', 'POST', creditRequest);
                     //call native method
                     common.jsCallNativeEventHandler(
                         JS_CALL_NATIVE_EVENT_REQUEST_WEB_API
                         , creditData
                         , function(data) {
                             common.log("赊账请求数据="+data);
                             var respondData =data.response;
                             respondData =JSON.parse(respondData);
                             var retbody = respondData.retbody;
                             if(respondData.retcode ==200){
                                 $(".needPayTitle").text("已开单，赊账中").css("color","#e2574a");
                                 $("#needPayNum").css("color","#e2574a");
                                 $("#confirmBtn").parent().remove();
//                                 $("#printOrder").parent().removeClass("col-50").addClass("col-100").css("width","100%");
                                 $("#printOrder").parent().removeClass("col-30").addClass("col-50").css("width","48%");
                                 $("#printOrderBlue").parent().removeClass("col-30").addClass("col-50").css("width","48%");
                                 $(".creditDiv").css("display","block");
                                 $(".needPayTime").text(common.creditTime(retbody.set_pay_dt));
                                 $("#orderTime-credit").text(common.translateTime(retbody.set_pay_dt));
                                 $("#orderCancel,#selectPayMethod").css("display","none");
                                 $("#employeeName-credit").text(current_employee_name);
                                 showPayType(creditRequest);
                                 $.hideIndicator();
                             }else{

                             }
                         }
                     );
             };

             //结清赊账详情页重新计算
             function orderDetailReCalculation(data){
                var orderData = data;
                var clearCreditArray =  dealNumber();
                if(clearCreditArray.length > 0){
                    $.each(clearCreditArray,function(key,value){
                          if(value.account_name =='现金'){
                                orderData.crash += value.income;
                                orderData.crash = (orderData.crash).toFixed(2);
                          }else if(value.account_name =='支付宝'){
                                orderData.zhifubao += value.income;
                                orderData.zhifubao = (orderData.zhifubao).toFixed(2);
                          }else if(value.account_name =='微信'){
                                orderData.weixin += value.income;
                                orderData.weixin = (orderData.weixin).toFixed(2);
                          }else if(value.account_name =='银行卡'){
                                orderData.online += value.income;
                                orderData.online = (orderData.online).toFixed(2);
                          }else if(value.account_name =='赊账'){
                                orderData.debt += value.income;
                                orderData.debt = (orderData.debt).toFixed(2);
                          }
                    });
                    setTimeout(function(){
                        showPayType(orderData);
                        if($(".needPayTitle").text() =="已开单，赊账中"){
                             $(".type_credit").css("height","0");
                        }
                    },0)
                }
             };


             //选择现金支付方式
             function selectCash(cashRequest){
                    var requestUrl = 'order/pay/set_type';
                    if(pay_type_finish == 1){
                        requestUrl = 'order/pay/finish';
                    }
                    var cashData = common.dataTransform(requestUrl,'POST',cashRequest)
                    common.jsCallNativeEventHandler(
                        JS_CALL_NATIVE_EVENT_REQUEST_WEB_API
                        , cashData
                        , function(data) {
                            common.log("结清订单="+JSON.stringify(data));
                            var respondData =data.response;
                            respondData =JSON.parse(respondData);
                            var retbody = respondData.retbody;
                            if(respondData.retcode ==200){
                                $(".needPayTime").css("display","none");
                                $(".needPayTitle").text("已结清").css("color","#f5a623");
                                $("#needPayNum").css("color","#f5a623");
                                $("#confirmBtn").text("关闭");
                                $(".clearDiv").css("display","block");
                                $("#clear-orderTime").text(common.translateTime(retbody.finish_dt));
                                $("#clear-employeeName").text(current_employee_name);
                                showPayType(cashRequest);
                                $("#selectPayMethod").css("display","none");
                                $(document).off("click","#confirmBtn")
                                $(document).on("click","#confirmBtn",function(){
                                   var userData = {
                                                       method: NATIVE_PAGE_DASHBOARD_HOME,
                                                       data: ""
                                                   };
                                   common.jsCallNativeEventHandler(JS_CALL_NATIVE_EVENT_REQUEST_SWITCH_PAGE, userData, function (data) {
                                       common.log("跳转到主页面完成！");
                                   });
                                });
                                $.hideIndicator();
                            }else if(respondData.retcode ==4021){
                                $.toast("订单不存在");
                            }else{

                            }
                        }
                    )
             };
             //查看订单详情
             function showTreatDetail(order) {
                 console.log("订单的详情"+JSON.stringify(order));
                 var finalPrice = order.total_price;
                 $("#customerNames").text(order.customer_name);
                 $("#orderPrice").text("¥" + finalPrice);
                 $("#shopNames").text(order.shop_name);
                 $("#employeeNames").text(order.employee_name);
                 // 给客户信息赋值
                 if (order.customer_id != 0) {
                     $(".hiddenBefore").show();
                     $("#contactPhones").text(order.contact_phone);
                 }
                 /* show server pay type */
                 showPayType(order);

                 var productsArray = order.sale_list,
                     tempHtml = "",
                     totalPrice = 0,
                     pratisePrice = 0;
                 if (productsArray != null && productsArray.length > 0) {
                     // 循环显示这个客户选中的所有商品
                     var template1 ="";
                     for (var i = 0; i < productsArray.length; i++) {
                         var oneCartItem = productsArray[i];
                             template1 += $("#proListTemplate").html()
                                         .replace("{{list_productName}}",oneCartItem.product_name)
                     }
                     $("#productLists").html(template1);

                     var template2="";
                         for(var k= 0; k< productsArray.length;k++){
                             var oneCartItem = productsArray[k];
                                 for(var j= 0; j< oneCartItem.sale_sku_list.length;j++){
                                    var sku_object = oneCartItem.sale_sku_list[j];
                                    template2 = $("#listItemTemplate").html()
                                                .replace("{{sku_name}}",sku_object.sku_value_name ? sku_object.sku_value_name : (sku_object.sku_name|| oneCartItem.product_name  ))
                                                .replace("{{sku_info}}",(sku_object.number ? sku_object.number : sku_object.quantity) +"* ￥"+ (sku_object.price >=0 ? sku_object.price : sku_object.standard_price) )
//                                                .replace("{{sale_account}}",sku_object.number ? sku_object.number : sku_object.quantity)
                                    if(sku_object.price >= 0){
                                        totalPrice+=  parseFloat(sku_object.price)*parseFloat(sku_object.number)
                                    }else{
                                        totalPrice+=  parseFloat(sku_object.standard_price)*parseFloat(sku_object.quantity)
                                    }
                                    $(".proList").eq(k).append(template2);
                                 }
                         }
                     totalPrice =  totalPrice.toFixed(2);
                     var adjustPrice = totalPrice - finalPrice;
                     $("#totalPrice").text("¥"+totalPrice);
                     if(adjustPrice >0){
                         $("#adjustPrice").text("¥"+adjustPrice.toFixed(2));
                     }else{
                         $("#adjustPrice").text("").css("color","#666666")
                     }
                 }
             };

             /* change the show pay type */
             function showPayType(data){
                 if(data.debt){
                    $(".type_credit").css("height","1.85rem");
                    $(".credit_value").text(data.debt);
                 }
                 if(data.crash){
                    $(".type_crash").css("height","1.85rem");
                    $(".crash_value").text(data.crash);
                 }
                 if(data.zhifubao){
                    $(".type_alipay").css("height","1.85rem");
                    $(".alipay_value").text(data.zhifubao);
                 }
                 if(data.weixin){
                    $(".type_wechat").css("height","1.85rem");
                    $(".wechat_value").text(data.weixin);
                 }
                 if(data.online){
                    $(".type_bankCard").css("height","1.85rem");
                    $(".bankCard_value").text(data.online);
                 }
             }

             //查询赊账总额
             function totalCredit(shopId,customerId){
                if(customerId =="0"){
                     $("#creditNum").text('');
                }else{
                    var totalCreditRequest ={
                          "shop_id":shopId,
                          "customer_id":customerId
                       };
                       var totalCreditData =common.dataTransform('statistic/debt','POST',totalCreditRequest);
                       common.jsCallNativeEventHandler(
                           JS_CALL_NATIVE_EVENT_REQUEST_WEB_API,
                           totalCreditData,
                           function (data){
                              var respondData =data.response;
                              respondData =JSON.parse(respondData);
                              var retbody = respondData.retbody;
                              if(respondData.retcode ==200){
                                  $("#creditNum").text('（该客户赊账总额¥'+retbody.debting_amount+'）');
                               }else{
                                 common.alertTips('网络异常')
                               }
                           });
                       }
             };

             //展示订单状态详情
             function showOrderDetail(data){
                if(data.length !=0 && data !=null){
                    common.log("查询订单状态详情：" + JSON.stringify(data));
//                     responseCallback(data);
                       //获取传入信息,解析,判断状态,改变内容
                       var order_number = data.order_number ? data.order_number : data.saleOrderNumber
                       var orderDetailId = data.sale_id;
                       var orderShopId   =data.shop_id;
                       var customerId  =data.customer_id;
                       orderNumber = orderDetailId;
                       printShopId = orderShopId;
                       var payTypeValue ={
                           crash: data.crash,
                           debt:data.debt,
                           weixin:data.weixin,
                           zhifubao:data.zhifubao,
                           online:data.online
                       }

                       //查看功能
                       showTreatDetail(data);
                       $(document).on("click","#checkDetailInfo",function(){
                            //原生进入后查看功能
                            $.popup(".popup-orderDetail");
                       });
                       //作废功能
                       cancelOrder(orderDetailId);
                       //查询赊账总额
                       totalCredit(orderShopId,customerId);
                       //结清状态
                       if(data.order_status ==1) {
                           $(".needPayTitle").text("已结清").css("color","#f5a623");
                           $("#needPayNum").css("color","#f5a623");
                           $("#confirmBtn").text("关闭");
                           $(".creditDiv,.cancelDiv,.remarkDiv").css("display","none");
                           $(".needPayTime").css("display","none");
                           $("#clear-orderTime").text(common.translateTime(data.finish_dt));
                           $('.indicator-background,.indicator-div').remove();
                           $(document).off("click","#confirmBtn");
                           $(document).on("click","#confirmBtn",function(){
                               var userData = {
                                                method: NATIVE_PAGE_DASHBOARD_HOME,
                                                data: ""
                                               };
                               common.jsCallNativeEventHandler(JS_CALL_NATIVE_EVENT_REQUEST_SWITCH_PAGE, userData, function (data) {
                                   common.log("跳转到主页面完成！");
                               });
                           });

                           //取消状态
                       }else if(data.order_status ==100){
                           $(".needPayTitle").text("账单已取消").css("color","#8b888d");
                           $("#needPayNum").css("color","#8b888d");
                           $(".btnRow,.creditDiv,.clearDiv,.remarkDiv").css("display","none");
                           $("#cancelOrderBtn").css("display","none");
                           $(".needPayTime").css("display","none");
                           $("#orderTime-cancel").text(common.translateTime(data.cancel_dt));
                           $('.indicator-background,.indicator-div').remove();
                           $("#cancelTextContent").text(data.cancel_comment).parent().show();
                           $(".content").css("bottom","0");
                           $(".bottom_bar").css("display","none");
                           //已开单未付款状态
                       }else if(data.order_status ==0 && data.payment ==0){
                           $(".clearDiv,.creditDiv,.cancelDiv,.remarkDiv").css("display","none");
                           $(".needPayTime").text("订单将在"+common.translateTime(data.gmt_expired)+"后取消");
                           totalCredit(orderShopId,customerId)
                           //订单取消,付清赊账div隐藏
                           $("#orderCancel,#clearCreditOrder").css("display","none");
                           $('.indicator-background,.indicator-div').remove();
                            creditType = 1;
                           //选择付款方式
                            $("#confirmBtn").text("结账收款");
                            $(document).on("click","#confirmBtn",function(){
                                $("#selectPayMethod").css("display","block");
                            });

                            $(document).on("click","#methodConfirm",function(){
                               choicePayType(orderNumber);
                            });

                           //赊账中状态
                       }else if(data.order_status ==0 && data.payment ==2){

                           $(".needPayTitle").text("已开单，赊账中").css("color","#e2574a");
                           $("#needPayNum").css("color","#e2574a");
                           $("#getCreditNum").text(data.debt);
                           $(".cancelDiv,.clearDiv,.remarkDiv").css("display","none");
                           $(".needPayTime").text(common.creditTime(data.set_pay_dt));
                           $("#orderTime-credit").text(common.translateTime(data.set_pay_dt));
                           if( creditType == 2){
                               $("#confirmBtn").parent().remove();
                               $("#printOrder").parent().removeClass("col-30").addClass("col-50").css("width","48%");
                               $("#printOrderBlue").parent().removeClass("col-30").addClass("col-50").css("width","48%");
                            }else{
                               $("#confirmBtn").text("结清该笔赊账");
                           }
                           $('.indicator-background,.indicator-div').remove();

                           $(document).on("click","#confirmBtn",function(){
                               $(".payType_credit_input,.creditType_radio").css("display","none");
                               $("#selectPayMethod").css("display","block");
                               $(".neededPay_title").text("订单赊账金额");
                               $('.neededPay_number').text($(".credit_value").text());
                               pay_type_finish =1;
                           });

                           $(document).on("click","#methodConfirm",function(){
                              choicePayType(orderNumber,orderDetailReCalculation(data));
                           });
                       }
                       $("#customerName").text(data.customer_name);
                       $("#totalMoney").text("¥"+data.total_price);
                       $("#shopName").text(data.shop_name);
                       $("#orderTime").text(data.sale_time);
                       $("#needPayNum").text("￥"+data.total_price);
                       $(".neededPay_number").text(data.total_price);
                       $("#employeeName").text(data.employee_name);
                       $("#employeeName-credit").text( data.set_pay_employee_name ? data.set_pay_employee_name : data.employee_name );
                       $("#clear-employeeName").text( data.finish_employee_name ? data.finish_employee_name : data.employee_name );
                       $("#employeeName-cancel").text( data.cancel_employee_name ? data.cancel_employee_name : data.employee_name )
                       $("#orderNo").text(order_number);
                       $("#warehouse").text(data.sale_list[0].sale_sku_list[0].warehouse_sku_list[0].warehouse_name);
                       webOrderNumber = order_number;
                       if(data.remark){
                            $("#remark").text(data.remark)
                       }else{
                            $(".remarked").hide();
                       }
                }else{
                   common.alertTips("网络异常")
                }
            };

            function sendOrderDetailRequest (transferData) {
               var getOrderDetail ={
                   shop_id:transferData.shop_id,
                   sale_id:transferData.sale_id
               };
               console.log("传给serve的值="+ JSON.stringify(getOrderDetail));
                var getOrderDetailRequest =common.dataTransform('transaction/sale/detail','POST',getOrderDetail);
                     common.jsCallNativeEventHandler(
                        JS_CALL_NATIVE_EVENT_REQUEST_WEB_API,
                        getOrderDetailRequest,
                        function(data){
                            var respondData =data.response;
                            console.log("获取到的data"+respondData);
                            respondData =JSON.parse(respondData);
                            var retbody = respondData.retbody;
                            if(respondData.retcode ==200){
                                showOrderDetail(retbody);
                            }else{
                                 $.toast("网络异常");
                            }
                        }
                     )
            }

             //后退事件
             function backButton(){
                var flag =sessionStorage.getItem("noFromNativeFast");
                if(flag){
                    var userData = {
                        method: NATIVE_PAGE_DASHBOARD_PAY_TRANSACTION,
                        data: ""
                    };
                    common.jsCallNativeEventHandler(JS_CALL_NATIVE_EVENT_REQUEST_SWITCH_PAGE, userData, function (data) {
                        log("打开主页面完成！");
                    });
                }else{
                    common.backHomePage();
                }
             }

             /* choice pay type  */
             function choicePayType(orderAboutInfoOrderId){
                var switch_status = $(".switch_checkbox").prop("checked");
                if(switch_status){
                    if(ifInputMoneyRight()){
                        if(calculationInput()){
                           if(dealType()){
                                var creditRequest = {};
                                    creditRequest.account_flow_list = dealNumber();
                                    creditRequest.sale_id = orderAboutInfoOrderId;
                                    selectCredit(creditRequest)
                           }else{
                           var cashRequest = {};
                               cashRequest.account_flow_list = dealNumber();
                               cashRequest.sale_id = orderAboutInfoOrderId;
                               selectCash(cashRequest)
                           }
                        }
                    }
                }else{
                    if(dealType()){
                         var creditRequest = {};
                             creditRequest.account_flow_list = dealNumber();
                             creditRequest.sale_id = orderAboutInfoOrderId;
                             selectCredit(creditRequest)
                    }else{
                         var cashRequest = {};
                             cashRequest.account_flow_list = dealNumber();
                             cashRequest.sale_id = orderAboutInfoOrderId;
                             selectCash(cashRequest)
                    }
                }
             }


            //打通JS与原生java通道
            common.connectJsTunnel(function(jsTunnel){
              var flagH5 =common.getQueryString("from");
                  jsTunnel.init();
                //原生进入订单状态详情页
                jsTunnel.registerHandler(
                    NATIVE_CALL_JS_EVENT_TRANSFER_DATA,
                    function (data,responseCallback){
                         orderRequest ={
                            shop_id:data.shop_id,
                            sale_id: data.sale_id
                         };
                         sendOrderDetailRequest (data)
                    }
                );

              ~function(){
                 var userData = {
                    method: NATIVE_METHOD_GET_USER_INFO,
                    data: ""
                 };

                 common.jsCallNativeEventHandler(JS_CALL_NATIVE_EVENT_EXECUTE_NATIVE_METHOD, userData, function (data) {
                    var userInfo = JSON.parse(data.response);
                        current_employee_name = userInfo.loginName;
                        userRole = userInfo.role;
                 });
              }()

                //获取用户的支付帐号信息
                getAccountPayInfo()

                /* fast clear */
                var fastCredit = sessionStorage.getItem("fastCredit");
                var fastClear  = sessionStorage.getItem("fastClear");
                ~function(){
                    if(fastCredit){
                        creditType = 2;
                        showOrderDetail(JSON.parse(fastCredit));
                        webOrderNumber = JSON.parse(fastCredit).saleOrderNumber;
                        orderRequest ={
                            shop_id: JSON.parse(fastCredit).shop_id,
                            sale_id: JSON.parse(fastCredit).sale_id
                        }
                    }else if(fastClear){
                        showOrderDetail(JSON.parse(fastClear));
                        webOrderNumber = JSON.parse(fastClear).saleOrderNumber;
                        orderRequest ={
                            shop_id:JSON.parse(fastClear).shop_id,
                            sale_id: JSON.parse(fastClear).sale_id
                        }
                    }
                }();
                var orderAboutInfo = sessionStorage.getItem("orderAboutInfo");
                //H5进入订单状态详情页
                if(orderAboutInfo && !fastCredit && !fastClear){
                    (function(){
                        //跳转至订单详情后,从本地获取订单ID
                        $(".clearDiv,.creditDiv,.cancelDiv,.remarkDiv").css("display","none");
                        $("#orderCancel,#clearCreditOrder").css("display","none");
                        common.log("获取本地数据="+orderAboutInfo);
                        orderAboutInfo =JSON.parse(orderAboutInfo);
                        var orderAboutInfoOrderId =orderAboutInfo.orderId;
                        var orderAboutInfoShopId  =orderAboutInfo.shopID;
                        var orderAboutInfoCustomerId =orderAboutInfo.customerId;
                        printShopId = orderAboutInfoShopId;
                        orderNumber = orderAboutInfoOrderId;
                        orderRequest = {
                            shop_id:printShopId,
                            sale_id:orderNumber
                        };
                        //取消订单
                        cancelOrder(orderAboutInfoOrderId);
                        //查询赊账总额
                        totalCredit(orderAboutInfoShopId,orderAboutInfoCustomerId);
                        //传入ID,获取订单信息
                        var requestInfo ={
                            "shop_id":orderAboutInfo.shopID,
                            "sale_id":orderAboutInfoOrderId
                        };

                        var baseData =common.dataTransform('transaction/sale/detail', 'POST',requestInfo);
                        common.log("发起网络请求="+baseData);
                        common.jsCallNativeEventHandler(
                            JS_CALL_NATIVE_EVENT_REQUEST_WEB_API
                            , baseData
                            , function(data) {
                                var respondData =data.response;
                                console.log("服务器返回的订单"+respondData);
                                respondData =JSON.parse(respondData);
                                var retbody = respondData.retbody;
                                if(respondData.retcode ==200){
                                    $(".needPayTitle").text("已开单,待付款").css("color","#4a90e2");
                                    $("#needPayNum").css("color","#4a90e2");
                                    $("#confirmBtn").text("结账收款");
                                    $("#customerName").text(retbody.customer_name);
                                    $("#totalMoney").text("¥"+retbody.total_price);
                                    $("#shopName").text(orderAboutInfo.shopName);
                                    $("#orderTime").text(retbody.sale_time);
                                    $("#employeeName").text(retbody.employee_name);
                                    $("#orderNo").text(retbody.order_number);
                                    $(".needPayNum").text("¥"+retbody.total_price);
                                    $("#cash").text("客户现金足额:¥"+retbody.total_price);
                                    $("#getCreditNum").text("¥"+retbody.total_price);
                                    $(".neededPay_number").text(retbody.total_price);
                                    $(".needPayTime").text("订单将在"+common.translateTime(retbody.gmt_expired)+"后取消");
                                    $("#warehouse").text(retbody.sale_list[0].sale_sku_list[0].warehouse_sku_list[0].warehouse_name);
                                    totalCredit(orderAboutInfoShopId,orderAboutInfoCustomerId);
                                    showTreatDetail(retbody);
                                    webOrderNumber = retbody.order_number;

                                    if(orderAboutInfo.remark){
                                        $("#remark").text(orderAboutInfo.remark)
                                    }else{
                                        $(".remarked").hide();
                                    }

                                    $('.indicator-background,.indicator-div').remove();

                                    //选择付款方式
                                    $(document).on("click","#confirmBtn",function(){
                                        $("#selectPayMethod").css("display","block");
                                    });

                                    $(document).on("click","#methodConfirm",function(){
                                      choicePayType(orderNumber,orderDetailReCalculation(retbody));
                                    });
                                }else{
                                   $.toast("网络异常");
                                }
                            }
                        );

                    })();
                }

                /* for customer detail page  */
                ~function(){
                    var formCustomer = common.getQueryString("from");
                    var translateOrderInfo =sessionStorage.getItem("translateOrderInfo");
                    console.log("传输给详情页的信息="+translateOrderInfo);
                    if(translateOrderInfo){
                        translateOrderInfo = JSON.parse(translateOrderInfo);
                        orderRequest ={
                            shop_id:translateOrderInfo.shop_id,
                            sale_id: translateOrderInfo.sale_id
                        };
                        sendOrderDetailRequest (translateOrderInfo);
                    }
                }();

                //查看订单详情后退按钮
                $(document).on("click","#backPage",function(){
                    $.closeModal(".popup-orderDetail")
                });

                //打印订单

                $(document).on("click","#printOrder",function(){
                    var requestInfo = {
                        project_id:"wholesale",
                        message: { sale_id : orderNumber,
                                   shop_id : printShopId,
                                   order_number : webOrderNumber
                        }
                    }
                    var requestData = common.dataTransform('push/push', 'POST',requestInfo);
                    common.jsCallNativeEventHandler(
                        JS_CALL_NATIVE_EVENT_REQUEST_WEB_API
                        ,requestData
                        ,function(data) {
                            var respondData =data.response;
                            respondData =JSON.parse(respondData);
                            var retbody = respondData.retbody;
                            common.log("查询订单返回结果="+respondData.retcode);
                            if(respondData.retcode ==200){
                                $.toast("打印信息已发送到PC端，请在PC端打印",1300);
                            }else{
                               $.toast("网络异常");
                            }
                        }
                    );
                });
            });

             //关闭页面按钮
            $(document).on("click",".cancelImages",function () {
               var fromCustomer = common.getQueryString("from");
               if(fromCustomer =="customer"){
                  window.history.back();
               }else{
                  backButton();
               }
            });

            //查看详情页面
            $(document).on("click","#checkDetailInfo",function(){
                 $.popup(".popup-orderDetail")
            });

            $(document).on("click",".cancelChoice",function(){
                $("#selectPayMethod").css("display","none");
            });


            ~function(){
                common.connectJsTunnel(function(jsTunnel){
                    var userData = {
                        method:NATIVE_METHOD_PRODUCT_CLOSE_OLD_PAGE,
                        data: "PRODUCT_CLOSE_OLD_PAGE"
                    };
                    common.jsCallNativeEventHandler(JS_CALL_NATIVE_EVENT_EXECUTE_NATIVE_METHOD, userData, function () {
                    });
                });
            }();

            /* several pay type */
            $(document).on("click",".common_switch",function (e) {
                var checkbox = $(".switch_checkbox");
                var switch_content = $(".switch_content");
                var content_circle = $(".content_circle");
                var choice_payType = $(".choice-payType");
                var choice_radio = $(".choice-radio");
                common.switchResult(checkbox,switch_content,content_circle,resultCallback(choice_payType,choice_radio,checkbox));
            });

            function resultCallback(choice_payType,choice_radio,checkbox) {
                if(checkbox.prop("checked")){
                    choice_radio.show();
                    choice_payType.hide();
                }else{
                    choice_radio.hide();
                    choice_payType.show();
                }
            };

            function dealType(){
                var payType_switch = $(".switch_checkbox").prop("checked");
                var payType_credit = parseFloat($(".payType_credit").val());
                var creditType = $("#creditType").prop("checked");
                var creditDeal = false;

                if(payType_switch){
                     if(payType_credit > 0){
                         creditDeal= true;
                     }else{
                         clearDeal = false;
                     }
                }else{
                    if(creditType){
                       creditDeal= true;
                    }else{
                       clearDeal = false;
                    }
                }
                return creditDeal
            };

            /* post to server several pay type value */
            function dealNumber(){
                var payType_switch = $(".switch_checkbox").prop("checked"),
                    payType_credit = parseFloat($(".payType_credit").val()),
                    payType_crash =  parseFloat($(".payType_crash").val()),
                    payType_alipay = parseFloat($(".payType_alipay").val()),
                    payType_wechat = parseFloat($(".payType_wechat").val()),
                    payType_bankCard = parseFloat($(".payType_bankCard").val());

                var credit_accountId = parseInt($(".payType_credit").data('creditaccountid')),
                    crash_accountId  = parseInt($(".payType_crash").data('crashaccountid')),
                    wechat_accountId  = parseInt($(".payType_wechat").data('wechataccountid')),
                    alipay_accountId  = parseInt($(".payType_alipay").data('alipayaccountid')),
                    bankCard_accountId  = parseInt($(".payType_bankCard").data('bankcardaccountid'));

                var cashType = $("#cashType").prop("checked"),
                    pay_alipay =$("#pay_alipay").prop("checked"),
                    pay_wechat =$("#pay_wechat").prop("checked"),
                    pay_bankCard =$("#pay_bankCard").prop("checked"),
                    creditType =$("#creditType").prop("checked");

                var neededPay_number = parseFloat($(".neededPay_number").text());

                var setPaymentObject = [],
                    debtObject = {},
                    crashObject = {},
                    wechatObject = {},
                    bankCardObject = {},
                    alipayObject = {};

                if(payType_switch){
                     if(payType_credit){
                         debtObject.account_name = "赊账";
                         debtObject.account_id = credit_accountId;
                         debtObject.income = payType_credit;
                         setPaymentObject.push(debtObject)
                     }
                     if(payType_crash){
                         crashObject.account_name = "现金";
                         crashObject.account_id = crash_accountId;
                         crashObject.income = payType_crash;
                         setPaymentObject.push(crashObject)
                     }
                     if(payType_alipay){
                         alipayObject.account_name = "支付宝";
                         alipayObject.account_id = alipay_accountId;
                         alipayObject.income = payType_alipay;
                         setPaymentObject.push(alipayObject)
                     }
                     if(payType_wechat){
                         wechatObject.account_name = "微信";
                         wechatObject.account_id = wechat_accountId;
                         wechatObject.income = payType_wechat;
                         setPaymentObject.push(wechatObject)
                     }
                     if(payType_bankCard){
                         bankCardObject.account_name = "银行卡";
                         bankCardObject.account_id = bankCard_accountId;
                         bankCardObject.income = payType_bankCard;
                         setPaymentObject.push(bankCardObject)
                     }
                }else{
                    if(cashType){
                        crashObject.account_name = "现金";
                        crashObject.account_id = crash_accountId;
                        crashObject.income = neededPay_number;
                        setPaymentObject.push(crashObject)
                    }
                    if(pay_alipay){
                        alipayObject.account_name = "支付宝";
                        alipayObject.account_id = alipay_accountId;
                        alipayObject.income = neededPay_number;
                        setPaymentObject.push(alipayObject)
                    }
                    if(pay_wechat){
                        wechatObject.account_name = "微信";
                        wechatObject.account_id = wechat_accountId;
                        wechatObject.income = neededPay_number;
                        setPaymentObject.push(wechatObject)
                    }
                    if(pay_bankCard){
                        bankCardObject.account_name = "银行卡";
                        bankCardObject.account_id = bankCard_accountId;
                        bankCardObject.income = neededPay_number;
                        setPaymentObject.push(bankCardObject)
                    }
                    if(creditType){
                       debtObject.account_name = "赊账";
                       debtObject.account_id = credit_accountId;
                       debtObject.income = neededPay_number;
                       setPaymentObject.push(debtObject)
                    }
                }
                return setPaymentObject;
            }

            /* when switch close judge if choice one pay type */
            function ifChoiceOneType(){
                if($(".payType_input input:checked").length >0){
                     return true
                }else{
                    $.toast("请至少选择一直支付方式")
                }
            }

            /* when switch open judge if input money right */
            function ifInputMoneyRight(){
                var notEmpty =false;
                var wrongInput =false;
                var payInput = $(".payType_input input")
                for(var i =0;i<payInput.length;i++){
                     if(payInput.eq(i).val()){
                        if(!common.regMoney(payInput.eq(i).val())){
                             wrongInput = true;
                        }
                        notEmpty =true;
                     }
                };
               if(notEmpty){
                    if(wrongInput){
                        $.toast("请输入正确的金额")
                    }else{
                        return true;
                    }
               }else{
                  $.toast("请至少输入一种支付方式")
               }
            }

            /* calculation input money value */
            function calculationInput(){
                var payInput = $(".payType_input input");
                var neededPay = parseFloat(parseFloat($(".neededPay_number").text()).toFixed(2));
                var totalMoney = 0;
                for(var i =0;i<payInput.length;i++){
                     totalMoney +=  ( parseFloat(payInput.eq(i).val()) ? parseFloat(payInput.eq(i).val()): 0 )
                }
                totalMoney = parseFloat(totalMoney.toFixed(2));

                if(neededPay > totalMoney){
                    $.toast("尚有剩余金额未支付,请填写",1000)
                }else if(neededPay < totalMoney ){
                    $.toast("超过需支付金额,请重新输入",1000)
                }else{
                    return true;
                }
            }

            $(document).on("input",".payType_input input",function(){
                 common.inputMoney($(this));
                 var payInput = $(".payType_input input");
                 var neededPay = parseFloat($(".neededPay_number").text());
                 var totalMoney = 0;
                 var leftMoney = 0;
                 for(var i =0;i<payInput.length;i++){
                      totalMoney +=  ( payInput.eq(i).val() ? parseFloat(payInput.eq(i).val()): 0 )
                 };
                 leftMoney = parseFloat((neededPay - totalMoney));
                 if(Math.abs(leftMoney) < 0.009){
                    leftMoney = 0;
                 }
                 $(".havePay_number").text(totalMoney.toFixed(2));
                 $(".residue_number").text(leftMoney.toFixed(2));
            });

            $(document).on("click","#printOrderBlue",function(){
                 var printerData = {
                    method: NATIVE_METHOD_BLUETOOTH_PRINTER,
                    data: orderRequest.sale_id
                };

                common.jsCallNativeEventHandler(JS_CALL_NATIVE_EVENT_EXECUTE_NATIVE_METHOD, printerData, function (data) {

                })

            });

            function totalCredit(shopId,customerId){
                if(customerId =="0"){
                     $(".credit_number").text(0);
                }else{
                    var totalCreditRequest ={
                          "shop_id":shopId,
                          "customer_id":customerId
                       };
                       var totalCreditData =common.dataTransform('statistic/debt','POST',totalCreditRequest);
                       common.jsCallNativeEventHandler(
                           JS_CALL_NATIVE_EVENT_REQUEST_WEB_API,
                           totalCreditData,
                           function (data){
                              var respondData =data.response;
                              respondData =JSON.parse(respondData);
                              var retbody = respondData.retbody;
                              if(respondData.retcode ==200){
                                  $(".credit_number").text(retbody.debting_amount);
                               }else{
                                 common.alertTips('网络异常')
                               }
                           });
                       }
            }

            //获取用户的支付帐号信息
            function getAccountPayInfo(){
                salesModule.getAccountListInfo(function(data){
                   var respondData =data.response;
                   console.log("获取到用户的accountList="+respondData)
                   respondData =JSON.parse(respondData);
                   if(respondData.retcode ==200){
                        var retBody = respondData.retbody;
                        var payChoiceList = $(".choice-payType").html();
                        $.each(retBody,function(key,value){
                            if(value.account_name == "现金"){
                                payChoiceList =  payChoiceList.replace("{{crashAccountId}}",value.account_id)
                            }else if(value.account_name == "微信"){
                                payChoiceList =  payChoiceList.replace("{{wechatAccountId}}",value.account_id)
                            }else if(value.account_name == "支付宝"){
                                payChoiceList =  payChoiceList.replace("{{alipayAccountId}}",value.account_id)
                            }else if(value.account_name == "银行卡"){
                                payChoiceList =  payChoiceList.replace("{{bankCardAccountId}}",value.account_id)
                            }else if(value.account_name == "赊账"){
                                payChoiceList =  payChoiceList.replace("{{creditAccountId}}",value.account_id)
                            }
                        });
                      $(".choice-payType").html(payChoiceList)
                   }else{
                      $.toast("网络异常")
                   }
                });
            }



        });
        $.init();

    })
})();