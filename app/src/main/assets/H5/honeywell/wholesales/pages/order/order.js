/**
 * Created by caihong on 16/7/20.
 */
(function () {
    require(['zepto', 'common', 'salesModule', 'template', 'customerModule', 'supplierModule'], function ($, common, salesModule, template, custModule, supplierModule) {
        //sureOrderPage
        var cartJsonArray = [], custInfo = {};//用户信息
        var totalPrice = 0;
        var visitorId, shopNameInfo="", employeeNameInfo ="",getShopId ="",warehouseName="",uuid="";
        var cashNumber =null, wechatNumber =null, alipayNumber =null, bankcardNumber =null, creditNumber =null;

        $(document).on("pageInit", "#sureOrderPage", function (e, pageId, $page) {
            common.connectJsTunnel(function (jsTunnel) {
                jsTunnel.init();
                //获取原生传入的数据
                jsTunnel.registerHandler(
                    NATIVE_CALL_JS_EVENT_TRANSFER_DATA,
                    function (data, responseCallback) {
                      if(data){
                           common.log("确认订单页面：" + JSON.stringify(data));
                           var status = localStorage.getItem("switchKey");
                           if(status =="true"){
                                $(".switch").prop("checked",true)
                           }else{
                                $(".switch").prop("checked",false)
                           }

                            getShopId = data.nameValuePairs.shopId;
                            visitorId = data.nameValuePairs.customerId;

                            console.log("货品的店铺ID="+getShopId);
                            var productInfoData = data.nameValuePairs.productList;
                                shopNameInfo = data.nameValuePairs.shopName;
                                employeeNameInfo = data.nameValuePairs.employeeName;
                                warehouseName = data.nameValuePairs.wareHouseName;
                           //购物车的信息存入缓存中
                           sessionStorage.setItem("inCartAllProduct",productInfoData);
                           //接收底层的数据并展示页面
                           showSelectedCarts(eval(productInfoData));

                           //获取用户的支付帐号信息
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
                            //关闭加载动画
                           $('.indicator-background,.indicator-div').remove();
                      }
                    }
                );

                function showSelectedCarts(allCartData) {
                    var tempHtml = '';
                    $("#productList").text('');
                    console.log("获取的店铺ID="+allCartData.shopId);
                    if (allCartData != null && allCartData.length > 0) {
                        var firstCartItem = allCartData[0];
                        custInfo = firstCartItem;
                        custInfo.shopId = getShopId;
                        custInfo.visitorId = visitorId;
                        custInfo.shopName = shopNameInfo;
                        common.log('赋值给custInfo的信息='+JSON.stringify(custInfo));
                        // 给客户信息赋值
                        if (firstCartItem.customerId != 0) {
                            $(".hiddenBefore").show();
                            $("#contactName").text(firstCartItem.contactName);
                            $("#contactPhone").text(firstCartItem.contactPhone);
                            $("#invoiceTitle").text(firstCartItem.invoiceTitle);
                        }
                        $("#customerName").text(firstCartItem.customerName);
                        $("#shopName").text(shopNameInfo);
                        $("#employeeName").text(employeeNameInfo);
                        uuid = allCartData[0].uuid;
                        console.log("订单的详细信息="+JSON.stringify(allCartData));

                        // 循环显示这个客户选中的所有商品
                        var template1 ="";
                        for(var i = 0; i < allCartData.length; i++){
                            var oneCartItem = allCartData[i];
                            template1 += $("#proListTemplate").html()
                                        .replace("{{list_productName}}",oneCartItem.productName)
                        }
                        $("#productList").html(template1);

                        var template2="";
                        for(var k= 0; k< allCartData.length;k++){
                            var productInfo ={product_id:"",product_name:"",sale_sku_list:[]};
                                productInfo.product_id = allCartData[k].productId;
                                productInfo.product_name =allCartData[k].productName;
//                                productInfo.unit = allCartData[k].unit;
                            var oneCartItem = allCartData[k];
                                for(var j= 0; j<JSON.parse(oneCartItem.skuListString).length;j++){
                                   var sku_object = JSON.parse(oneCartItem.skuListString)[j];
                                   var sale_object ={sku_name:"",quantity:"",inventory_id:"",standard_price:"",warehouse_sku_list:[{warehouse_name:"",warehouse_quantity:"",warehouse_inventory_id:""}]};

                                   template2 = $("#listItemTemplate").html()
                                               .replace("{{sku_name}}",sku_object.sku_name ? sku_object.sku_name: oneCartItem.productName )
                                               .replace("{{sku_info}}",sku_object.sale_amount +"* ￥"+ sku_object.standard_price)
//                                               .replace("{{sale_account}}",sku_object.sale_amount)
                                   totalPrice+=  parseFloat(sku_object.standard_price)* parseFloat(sku_object.sale_amount);

                                   sale_object.warehouse_sku_list[0].warehouse_quantity = (parseFloat(sku_object.sale_amount)).toFixed(2);
                                   sale_object.warehouse_sku_list[0].warehouse_inventory_id = sku_object.warehouse_inventory_id;
                                   sale_object.warehouse_sku_list[0].warehouse_name = warehouseName;

                                   sale_object.quantity = (parseFloat(sku_object.sale_amount)).toFixed(2);
                                   sale_object.sku_name = sku_object.sku_name;
                                   sale_object.inventory_id =sku_object.inventory_id;
                                   sale_object.standard_price =sku_object.standard_price;
                                   productInfo.sale_sku_list.push(sale_object)
                                   $(".proList").eq(k).append(template2);
                                }
                            cartJsonArray.push(productInfo);
                        }
                        console.log("出售货品="+JSON.stringify(cartJsonArray));
                        totalPrice = parseFloat(totalPrice).toFixed(2);
                        // 显示货品的总货款
                        $("#total_money").text(totalPrice);
                        $("#setTotalPrice").val(totalPrice);
                        $(".neededPay_number").text(totalPrice);
                        totalCredit(getShopId,visitorId);
                    } else {
                        common.log("Error! 没有选中的商品！！！");
                    }
                };
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


            function judgeFast(){
                   var customerIdJson = {
                       customerId: visitorId,
                       sale_list: cartJsonArray,
                       uuid : uuid
                   };
                    var removeCartEvent = {
                        method: NATIVE_METHOD_CART_REMOVE_BY_CUSTOMER_ID,
                        data: JSON.stringify(customerIdJson)
                    };

                     common.jsCallNativeEventHandler(JS_CALL_NATIVE_EVENT_EXECUTE_NATIVE_METHOD, removeCartEvent, function (data) {
                        location.href ="orderStateDetail.html?flag=1";
                     });
            }

            $(document).on("click","#methodConfirm",function(){
                var switch_status = $(".switch_checkbox").prop("checked");

                if(switch_status){
                   if(($('#setTotalPrice').css('color') !='red') && ($('#setTotalPrice').val() !="")){
                           if(ifInputMoneyRight()){
                               if(calculationInput()){
//                                   $(document).off("click","#methodConfirm");
                                     $(this).attr("disabled","disabled");
                                   if(dealType()){
                                        ~function(){
                                          common.log("获取custinfo = " + JSON.stringify(custInfo));
                                          var discount = $("#discount").val();
                                          if(common.isNullString(discount)) {
                                              discount = "0";
                                          }
                                          var finalPrice = $("#setTotalPrice").val().replace("¥", "");
                                          common.log("最终价格:"+finalPrice);
                                          visitorId = custInfo.customerId;

                                          if(typeof(custInfo.customerId) !== "number" && isNaN(parseInt((custInfo.customerId)[0]))){
                                              custInfo.customerId = 0;
                                          }
                                          var orderDetail = {
                                              shop_id: custInfo.shopId,
                                              employee_id: custInfo.employeeId,
                                              employee_name: employeeNameInfo,
                                              customer_id: visitorId,
                                              customer_name: custInfo.customerName ? custInfo.customerName: "" ,
                                              contact_name: custInfo.contactName ? custInfo.contactName: "",
                                              contact_phone: custInfo.contactPhone ? custInfo.contactPhone: "",
                                              invoice_title: " ",
                                              address:custInfo.address?custInfo.address: " " ,
                                              discount: discount,
                                              total_price: finalPrice + "",
                                              payment: '0',//0-未知，1-现款，2-赊账，3-电子支付
                                              sale_list: cartJsonArray,
                                              remark:$(".area").val(),
                                          }
                                          common.log("开单传入数据：" + JSON.stringify(orderDetail));

                                          salesModule.shipSales(orderDetail,function (data){
                                              common.log("开单服务器返回数据：" + JSON.stringify(data));
                                              var responseBody = data.response;
                                              var responseJson = JSON.parse(responseBody);
                                              if (data.ack == 1 && responseJson.retcode == 200) {
                                                  //缓存信息
                                                  var orderAboutInfo = {
                                                      shopID: custInfo.shopId,
                                                      customerName: custInfo.customerName,
                                                      //totalPrice: parseFloat(custInfo.unitPrice) * parseInt(custInfo.totalNumber) + "",
                                                      totalPrice:finalPrice,
                                                      discount: custInfo.discount,
                                                      shopName: custInfo.shopName,
                                                      employeeName: custInfo.userName,
                                                      createTime: getNowFormatDate(),
                                                      orderId: responseJson.retbody.sale_id,
                                                      saleOrderNumber:responseJson.retbody.order_number,
                                                      customerId :custInfo.customerId,
                                                      remark:$(".area").val(),
                                                  }
                                                  sessionStorage.setItem("orderAboutInfo",JSON.stringify(orderAboutInfo));
                                                  /* judge if it is fast deal  */

                                                 var creditRequest = {};
                                                     creditRequest.account_flow_list = dealNumber();
                                                     creditRequest.sale_id = responseJson.retbody.sale_id;

                                                   var creditData = common.dataTransform('order/pay/set_type', 'POST', creditRequest);
                                                   //call native method
                                                   common.jsCallNativeEventHandler(
                                                       JS_CALL_NATIVE_EVENT_REQUEST_WEB_API
                                                       , creditData
                                                       , function(data) {
                                                           common.log("赊账请求数据="+JSON.stringify(data));
                                                           var respondData =data.response;
                                                           respondData =JSON.parse(respondData);
                                                           var retbody = respondData.retbody;
                                                           if(respondData.retcode ==200){
                                                                var credit = orderDetail;
                                                                credit.shop_name = custInfo.shopName;
                                                                credit.sale_id = orderAboutInfo.orderId;
                                                                credit.sale_time = orderAboutInfo.createTime;
                                                                credit.finish_dt ="";
                                                                credit.gmt_expired =responseJson.retbody.gmt_expired
                                                                credit.order_status = 0;
                                                                credit.payment ="2";
                                                                credit.set_pay_dt = retbody.set_pay_dt;
                                                                credit.set_pay_employee_name = employeeNameInfo;
                                                                credit.crash = cashNumber;
                                                                credit.online = bankcardNumber;
                                                                credit.weixin = wechatNumber;
                                                                credit.zhifubao = alipayNumber;
                                                                credit.debt = creditNumber;
                                                                credit.saleOrderNumber =  orderAboutInfo.saleOrderNumber;

                                                                sessionStorage.setItem("fastCredit",JSON.stringify(credit));
                                                                console.log("快速赊账账单="+JSON.stringify(credit));
                                                                judgeFast();
                                                           }else if(respondData.retcode ==4021){
                                                                $("#methodConfirm").removeAttr("disabled");
                                                                $.toast("订单不存在");
                                                           }else{
                                                               $("#methodConfirm").removeAttr("disabled");
                                                               $.toast("网络异常");
                                                           }
                                                       }
                                                   );
                                              } else {
                                                  $("#methodConfirm").removeAttr("disabled");
                                                  if (responseJson.retcode == 4016) {
                                                      $.toast("商品库存不足");
                                                  } else {
                                                      $.toast("网络异常");
                                                  }
                                              }
                                            });
                                          }()
                                   }else{
                                        ~function(){
                                            common.log("获取custinfo = " + JSON.stringify(custInfo));
                                            var discount = $("#discount").val();
                                            if(common.isNullString(discount)) {
                                                discount = "0";
                                            }
                                            var finalPrice = $("#setTotalPrice").val().replace("¥", "");
                                            common.log("最终价格:"+finalPrice);
                                            visitorId = custInfo.customerId;

                                            if(typeof(custInfo.customerId) !== "number" && isNaN(parseInt((custInfo.customerId)[0]))){
                                                custInfo.customerId = 0;
                                            }
                                            var orderDetail = {
                                                shop_id: custInfo.shopId,
                                                employee_id: custInfo.employeeId,
                                                employee_name: employeeNameInfo,
                                                customer_id: visitorId,
                                                customer_name: custInfo.customerName ? custInfo.customerName: "" ,
                                                contact_name: custInfo.contactName ? custInfo.contactName: "",
                                                contact_phone: custInfo.contactPhone ? custInfo.contactPhone: "",
                                                invoice_title: " ",
                                                address:custInfo.address?custInfo.address: " " ,
                                                discount: discount,
                                                total_price: finalPrice + "",
                                                payment: '0',//0-未知，1-现款，2-赊账，3-电子支付
                                                sale_list: cartJsonArray,
                                                remark:$(".area").val(),
                                            }
                                            common.log("开单传入数据：" + JSON.stringify(orderDetail));

                                            salesModule.shipSales(orderDetail,function (data){
                                                common.log("开单服务器返回数据：" + JSON.stringify(data));
                                                var responseBody = data.response;
                                                var responseJson = JSON.parse(responseBody);
                                                if (data.ack == 1 && responseJson.retcode == 200) {
                                                    //缓存信息
                                                    var orderAboutInfo = {
                                                        shopID: custInfo.shopId,
                                                        customerName: custInfo.customerName,
                                                        //totalPrice: parseFloat(custInfo.unitPrice) * parseInt(custInfo.totalNumber) + "",
                                                        totalPrice:finalPrice,
                                                        discount: custInfo.discount,
                                                        shopName: custInfo.shopName,
                                                        employeeName: custInfo.userName,
                                                        createTime: getNowFormatDate(),
                                                        orderId: responseJson.retbody.sale_id,
                                                        saleOrderNumber:responseJson.retbody.order_number,
                                                        customerId :custInfo.customerId,
                                                        remark:$(".area").val(),
                                                    }
                                                    sessionStorage.setItem("orderAboutInfo",JSON.stringify(orderAboutInfo));

                                                     var cashRequest = {};
                                                         cashRequest.account_flow_list = dealNumber();
                                                         cashRequest.sale_id = responseJson.retbody.sale_id;
                                                    var cashData = common.dataTransform('order/pay/set_type','POST',cashRequest)
                                                    common.jsCallNativeEventHandler(
                                                        JS_CALL_NATIVE_EVENT_REQUEST_WEB_API
                                                        , cashData
                                                        , function(data) {
                                                            var respondData =data.response;
                                                            console.log("结清状态的结果="+respondData);
                                                            respondData =JSON.parse(respondData);
                                                            var retbody = respondData.retbody;
                                                            if(respondData.retcode ==200){
                                                               var fastClear = orderDetail;
                                                               fastClear.shop_name = custInfo.shopName;
                                                               fastClear.sale_id = orderAboutInfo.orderId;
                                                               fastClear.sale_time = orderAboutInfo.createTime;
                                                               fastClear.finish_dt = retbody.finish_dt;
                                                               fastClear.gmt_expired = responseJson.retbody.gmt_expired;
                                                               fastClear.order_status = 1;
                                                               fastClear.set_pay_dt = retbody.finish_dt;
                                                               fastClear.set_pay_employee_name = employeeNameInfo;
                                                               fastClear.crash = cashNumber;
                                                               fastClear.online = bankcardNumber;
                                                               fastClear.weixin = wechatNumber;
                                                               fastClear.zhifubao = alipayNumber;
                                                               fastClear.debt = creditNumber;
                                                               fastClear.saleOrderNumber =  responseJson.retbody.order_number;

                                                               sessionStorage.setItem("fastClear",JSON.stringify(fastClear));
                                                               console.log("快速结清账单="+JSON.stringify(fastClear));
                                                               judgeFast()
                                                            }else if(respondData.retcode ==4021){
                                                                $("#methodConfirm").removeAttr("disabled");
                                                                $.toast("订单不存在");
                                                            }else{
                                                                $("#methodConfirm").removeAttr("disabled");
                                                                $.toast("网络异常");
                                                            }
                                                        }
                                                    )
                                                } else {
                                                    $("#methodConfirm").removeAttr("disabled");
                                                    if (responseJson.retcode == 4016) {
                                                        $.toast("商品库存不足");
                                                    } else {
                                                        $.toast("网络异常");
                                                    }
                                                }
                                            });
                                        }()
                                   }
                               }
                           }
                   }else{
                     common.alertTips('请输入正确的总价')
                   }
                }else{
                   if(($('#setTotalPrice').css('color') !='red') && ($('#setTotalPrice').val() !="")){
//                            $(document).off("click","#methodConfirm");
                            $(this).attr("disabled","disabled");
                            if(dealType()){
                                ~function(){
                                      common.log("获取custinfo = " + JSON.stringify(custInfo));
                                      var discount = $("#discount").val();
                                      if(common.isNullString(discount)) {
                                          discount = "0";
                                      }
                                      var finalPrice = $("#setTotalPrice").val().replace("¥", "");
                                      common.log("最终价格:"+finalPrice);
                                      visitorId = custInfo.customerId;

                                      if(typeof(custInfo.customerId) !== "number" && isNaN(parseInt((custInfo.customerId)[0]))){
                                          custInfo.customerId = 0;
                                      }
                                      var orderDetail = {
                                          shop_id: custInfo.shopId,
                                          employee_id: custInfo.employeeId,
                                          employee_name: employeeNameInfo,
                                          customer_id: visitorId,
                                          customer_name: custInfo.customerName ? custInfo.customerName: "" ,
                                          contact_name: custInfo.contactName ? custInfo.contactName: "",
                                          contact_phone: custInfo.contactPhone ? custInfo.contactPhone: "",
                                          invoice_title: " ",
                                          address:custInfo.address?custInfo.address: " " ,
                                          discount: discount,
                                          total_price: finalPrice + "",
                                          payment: '0',//0-未知，1-现款，2-赊账，3-电子支付
                                          sale_list: cartJsonArray,
                                          remark:$(".area").val(),
                                      }
                                      common.log("开单传入数据：" + JSON.stringify(orderDetail));

                                      salesModule.shipSales(orderDetail,function (data){
                                          common.log("开单服务器返回数据：" + JSON.stringify(data));
                                          var responseBody = data.response;
                                          var responseJson = JSON.parse(responseBody);
                                          if (data.ack == 1 && responseJson.retcode == 200) {
                                              //缓存信息
                                              var orderAboutInfo = {
                                                  shopID: custInfo.shopId,
                                                  customerName: custInfo.customerName,
                                                  //totalPrice: parseFloat(custInfo.unitPrice) * parseInt(custInfo.totalNumber) + "",
                                                  totalPrice:finalPrice,
                                                  discount: custInfo.discount,
                                                  shopName: custInfo.shopName,
                                                  employeeName: custInfo.userName,
                                                  createTime: getNowFormatDate(),
                                                  orderId: responseJson.retbody.sale_id,
                                                  saleOrderNumber:responseJson.retbody.order_number,
                                                  customerId :custInfo.customerId,
                                                  remark:$(".area").val(),
                                              }
                                              sessionStorage.setItem("orderAboutInfo",JSON.stringify(orderAboutInfo));
                                              /* judge if it is fast deal  */

                                              var creditRequest = {};
                                                  creditRequest.account_flow_list = dealNumber();
                                                  creditRequest.sale_id = responseJson.retbody.sale_id;

                                               var creditData = common.dataTransform('order/pay/set_type', 'POST', creditRequest);
                                               //call native method
                                               common.jsCallNativeEventHandler(
                                                   JS_CALL_NATIVE_EVENT_REQUEST_WEB_API
                                                   , creditData
                                                   , function(data) {
                                                       common.log("赊账请求数据="+JSON.stringify(data));
                                                       var respondData =data.response;
                                                       respondData =JSON.parse(respondData);
                                                       var retbody = respondData.retbody;
                                                       if(respondData.retcode ==200){
                                                            var credit = orderDetail;
                                                            credit.shop_name = custInfo.shopName;
                                                            credit.sale_id = orderAboutInfo.orderId;
                                                            credit.sale_time = orderAboutInfo.createTime;
                                                            credit.finish_dt ="";
                                                            credit.gmt_expired =responseJson.retbody.gmt_expired
                                                            credit.order_status = 0;
                                                            credit.payment ="2";
                                                            credit.set_pay_dt = retbody.set_pay_dt;
                                                            credit.set_pay_employee_name = employeeNameInfo;
                                                            credit.saleOrderNumber = orderAboutInfo.saleOrderNumber;
                                                            credit.crash = cashNumber;
                                                            credit.online = bankcardNumber;
                                                            credit.weixin = wechatNumber;
                                                            credit.zhifubao = alipayNumber;
                                                            credit.debt = creditNumber;


                                                            sessionStorage.setItem("fastCredit",JSON.stringify(credit));
                                                            console.log("快速赊账账单="+JSON.stringify(credit));
                                                            judgeFast();
                                                       }else if(respondData.retcode ==4021){
                                                            $("#methodConfirm").removeAttr("disabled");
                                                            $.toast("订单不存在");
                                                       }else{
                                                           $("#methodConfirm").removeAttr("disabled");
                                                           $.toast("网络异常");
                                                       }
                                                   }
                                               );
                                          } else {
                                              $("#methodConfirm").removeAttr("disabled");
                                              if (responseJson.retcode == 4016) {
                                                  $.toast("商品库存不足");
                                              } else {
                                                  $.toast("网络异常");
                                              }
                                          }
                                        });
                                }();
                            }else{
                                ~function(){
                                    common.log("获取custinfo = " + JSON.stringify(custInfo));
                                    var discount = $("#discount").val();
                                    if(common.isNullString(discount)) {
                                        discount = "0";
                                    }
                                    var finalPrice = $("#setTotalPrice").val().replace("¥", "");
                                    common.log("最终价格:"+finalPrice);
                                    visitorId = custInfo.customerId;

                                    if(typeof(custInfo.customerId) !== "number"){
                                        custInfo.customerId = 0;
                                    }
                                    var orderDetail = {
                                        shop_id: custInfo.shopId,
                                        employee_id: custInfo.employeeId,
                                        employee_name: employeeNameInfo,
                                        customer_id: visitorId,
                                        customer_name: custInfo.customerName ? custInfo.customerName: "" ,
                                        contact_name: custInfo.contactName ? custInfo.contactName: "",
                                        contact_phone: custInfo.contactPhone ? custInfo.contactPhone: "",
                                        invoice_title: " ",
                                        address:custInfo.address?custInfo.address: " " ,
                                        discount: discount,
                                        total_price: finalPrice + "",
                                        payment: '0',//0-未知，1-现款，2-赊账，3-电子支付
                                        sale_list: cartJsonArray,
                                        remark:$(".area").val(),
                                    }
                                    common.log("开单传入数据：" + JSON.stringify(orderDetail));

                                    salesModule.shipSales(orderDetail,function (data){
                                        common.log("开单服务器返回数据：" + JSON.stringify(data));
                                        var responseBody = data.response;
                                        var responseJson = JSON.parse(responseBody);
                                        if (data.ack == 1 && responseJson.retcode == 200) {
                                            //缓存信息
                                            var orderAboutInfo = {
                                                shopID: custInfo.shopId,
                                                customerName: custInfo.customerName,
                                                //totalPrice: parseFloat(custInfo.unitPrice) * parseInt(custInfo.totalNumber) + "",
                                                totalPrice:finalPrice,
                                                discount: custInfo.discount,
                                                shopName: custInfo.shopName,
                                                employeeName: custInfo.userName,
                                                createTime: getNowFormatDate(),
                                                orderId: responseJson.retbody.sale_id,
                                                saleOrderNumber:responseJson.retbody.order_number,
                                                customerId :custInfo.customerId,
                                                remark:$(".area").val(),
                                            }
                                            sessionStorage.setItem("orderAboutInfo",JSON.stringify(orderAboutInfo));

                                             var cashRequest = {};
                                             cashRequest.account_flow_list = dealNumber();
                                             cashRequest.sale_id = responseJson.retbody.sale_id;

                                            var cashData = common.dataTransform('order/pay/set_type','POST',cashRequest)
                                            common.jsCallNativeEventHandler(
                                                JS_CALL_NATIVE_EVENT_REQUEST_WEB_API
                                                , cashData
                                                , function(data) {
                                                    var respondData =data.response;
                                                    console.log("结清状态的结果="+respondData);
                                                    respondData =JSON.parse(respondData);
                                                    var retbody = respondData.retbody;
                                                    if(respondData.retcode ==200){
                                                       var fastClear = orderDetail;
                                                       fastClear.shop_name = custInfo.shopName;
                                                       fastClear.sale_id = orderAboutInfo.orderId;
                                                       fastClear.sale_time = orderAboutInfo.createTime;
                                                       fastClear.finish_dt = retbody.finish_dt;
                                                       fastClear.gmt_expired = responseJson.retbody.gmt_expired;
                                                       fastClear.order_status = 1;
                                                       fastClear.set_pay_dt = retbody.finish_dt;
                                                       fastClear.saleOrderNumber =  responseJson.retbody.order_number;
                                                       fastClear.set_pay_employee_name = employeeNameInfo;
                                                       fastClear.crash = cashNumber;
                                                       fastClear.online = bankcardNumber;
                                                       fastClear.weixin = wechatNumber;
                                                       fastClear.zhifubao = alipayNumber;
                                                       fastClear.debt = creditNumber;

                                                       sessionStorage.setItem("fastClear",JSON.stringify(fastClear));
                                                       console.log("快速结清账单="+JSON.stringify(fastClear));
                                                       judgeFast()
                                                    }else if(respondData.retcode ==4021){
                                                        $("#methodConfirm").removeAttr("disabled");
                                                        $.toast("订单不存在");
                                                    }else{
                                                        $("#methodConfirm").removeAttr("disabled");
                                                        $.toast("网络异常");
                                                    }
                                                }
                                            )
                                        } else {
                                            if (responseJson.retcode == 4016) {
                                                $("#methodConfirm").removeAttr("disabled");
                                                $.toast("商品库存不足");
                                            } else {
                                                $("#methodConfirm").removeAttr("disabled");
                                                $.toast("网络异常");
                                            }
                                        }
                                    });
                                }()
                            }
                   }else{
                        common.alertTips('请输入正确的总价')
                   }
                }
            });

            //确认开单
            $("#sureOrderBtn").on("click",function () {
               if($(".switch").prop("checked")){
                  if(($('#setTotalPrice').css('color') !='red') && ($('#setTotalPrice').val() !="")){
                      $(this).attr("disabled","disabled");
                      var discount = $("#discount").val();
                      if(common.isNullString(discount)) {
                          discount = "0";
                      }
                      var finalPrice = $("#setTotalPrice").val().replace("¥", "");
                      common.log("最终价格:"+finalPrice);
                      visitorId = custInfo.customerId;

                      if(typeof(custInfo.customerId) !== "number" && isNaN(parseInt((custInfo.customerId)[0]))){
                          custInfo.customerId = 0;
                      }
                      var orderDetail = {
                          shop_id: custInfo.shopId,
                          employee_id: custInfo.employeeId,
                          employee_name: employeeNameInfo,
                          customer_id: visitorId,
                          customer_name: custInfo.customerName ? custInfo.customerName: "" ,
                          contact_name: custInfo.contactName ? custInfo.contactName: "",
                          contact_phone: custInfo.contactPhone ? custInfo.contactPhone: "",
                          invoice_title: " ",
                          address:custInfo.address?custInfo.address: " " ,
                          discount: discount,
                          total_price: finalPrice + "",
                          payment: '0',//0-未知，1-现款，2-赊账，3-电子支付
                          sale_list: cartJsonArray,
                          remark:$(".area").val(),
                      }

                      salesModule.shipSales(orderDetail,function (data){
                          common.log("开单服务器返回数据：" + JSON.stringify(data));
                          var responseBody = data.response;
                          var responseJson = JSON.parse(responseBody);
                          if (data.ack == 1 && responseJson.retcode == 200) {
                              //缓存信息
                              var orderAboutInfo = {
                                  shopID: custInfo.shopId,
                                  customerName: custInfo.customerName,
                                  //totalPrice: parseFloat(custInfo.unitPrice) * parseInt(custInfo.totalNumber) + "",
                                  totalPrice:finalPrice,
                                  discount: custInfo.discount,
                                  shopName: custInfo.shopName,
                                  employeeName: custInfo.userName,
                                  createTime: getNowFormatDate(),
                                  orderId: responseJson.retbody.sale_id,
                                  saleOrderNumber :responseJson.retbody.order_number,
                                  customerId :custInfo.customerId,
                                  remark:$(".area").val(),
                              }
                              sessionStorage.setItem("orderAboutInfo",JSON.stringify(orderAboutInfo));
                              judgeFast()
                          } else {
                              $("#sureOrderBtn").removeAttr("disabled");
                              if (responseJson.retcode == 4016) {
                                  $.toast("商品库存不足");
                              } else {
                                  $.toast("网络异常");
                              }
                          }
                          $.hideIndicator();
                      });
                  }else{
                     common.alertTips('请输入正确的总价')
                  }
               }else{
                  $.popup(".popup-choicePayType")
               }
            });

            custInfo.finalPrice =parseFloat($("#total_money").text().replace("¥", ""));

            $(document).on("click","#setTotalPrice",function(){
                  $(this)[0].select();
//                    $(this).val("");
            })
            //调整价格
            $(document).on("input", "#setTotalPrice", function () {
                var inputTotal = $(this).val();
                var tprice = parseFloat($("#total_money").text());
                var adjustPrice = (tprice - parseFloat(inputTotal)).toFixed(2);
                if(adjustPrice > 0 ){
                    $("#discount").val("减去"+adjustPrice);
                }else if(adjustPrice ==0 ){
                     $("#discount").val("");
                }else{
                     $("#discount").val("增加"+Math.abs(adjustPrice));
                }

                if(inputTotal ==""){
                     $("#discount").val("");
                     inputTotal = 0;
                }

                if(common.regMoney(inputTotal)){
                    $(this).css('color','orange');
                    if(inputTotal.indexOf(".") < 0 && inputTotal[0] == 0){
                         $(this).css('color','red');
                    }
                }else{
                    $(this).css('color','red');
                }

                $(".neededPay_number").text($(this).val())
                custInfo.finalPrice = parseFloat($(this).val());
                custInfo.discount =  adjustPrice;
            });

            $(document).on("click", ".customer_list .shiment-cust", function (event) {
                $(".radio-icon").addClass("icon-form-checkbox").removeClass("icon-check-checked");
                var chname = $(this).find(".customer-name").text();
                common.log("选择的客户姓名是：" + chname);
                var cid = $(this).data("cid");
                var custInfo = {
                    contact_name: $(this).data("name"),
                    contact_phone: $(this).data("phone"),
                    customer_id: cid,
                    address: $(this).data("address")
                }
                $("#contactName").text($(this).data("name"));
                $("#contactPhone").text($(this).data("phone"));
                $("#invoiceTitle").text("测试信息");
                $("#skInfo").css("display", "none");
                $("#concatLayout").css("display", "block");
                $("#invoiceLayout").css("display", "block");
                $.closeModal(".popup-customer");
                $.hideIndicator();
                event.stopPropagation();
                event.preventDefault();
            });

            $(document).on("click", ".popup-sign", function () {
                $.router.load("#payWayPage");
                $.closeModal();
            });

           $(document).on("click",".backHomeBtn",function(){
                $.confirm("确认退出当前开单流程？", "温馨提示", function () {
                    common.backHomePage()
                }, function () {
                }, "取消", "确认");
           });

            /* 缓存开关状态 */
            $(document).on("click",".label-switch",function(){
                var status = $(".switch").prop("checked");
                localStorage.setItem("switchKey",status.toString());
            })

            /* 添加收银员模式开关 */
            $(document).on("click",".cancelImage",function(){
                $.closeModal(".popup-choicePayType");
                $(".payType_input input").val("");
                $(".havePay_number, .residue_number").text("");
            });

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
                         creditNumber = payType_credit;
                         debtObject.account_name = "赊账";
                         debtObject.account_id = credit_accountId;
                         debtObject.income = payType_credit;
                         setPaymentObject.push(debtObject)
                     }
                     if(payType_crash){
                         cashNumber = payType_crash;
                         crashObject.account_name = "现金";
                         crashObject.account_id = crash_accountId;
                         crashObject.income = payType_crash;
                         setPaymentObject.push(crashObject)
                     }
                     if(payType_alipay){
                         alipayNumber = payType_alipay;
                         alipayObject.account_name = "支付宝";
                         alipayObject.account_id = alipay_accountId;
                         alipayObject.income = payType_alipay;
                         setPaymentObject.push(alipayObject)
                     }
                     if(payType_wechat){
                         wechatNumber = payType_wechat;
                         wechatObject.account_name = "微信";
                         wechatObject.account_id = wechat_accountId;
                         wechatObject.income = payType_wechat;
                         setPaymentObject.push(wechatObject)
                     }
                     if(payType_bankCard){
                         bankcardNumber = payType_bankCard;
                         bankCardObject.account_name = "银行卡";
                         bankCardObject.account_id = bankCard_accountId;
                         bankCardObject.income = payType_bankCard;
                         setPaymentObject.push(bankCardObject)
                     }
                }else{
                    if(cashType){
                        cashNumber = neededPay_number;
                        crashObject.account_name = "现金";
                        crashObject.account_id = crash_accountId;
                        crashObject.income = neededPay_number;
                        setPaymentObject.push(crashObject)
                    }
                    if(pay_alipay){
                        alipayNumber = neededPay_number;
                        alipayObject.account_name = "支付宝";
                        alipayObject.account_id = alipay_accountId;
                        alipayObject.income = neededPay_number;
                        setPaymentObject.push(alipayObject)
                    }
                    if(pay_wechat){
                        wechatNumber = neededPay_number;
                        wechatObject.account_name = "微信";
                        wechatObject.account_id = wechat_accountId;
                        wechatObject.income = neededPay_number;
                        setPaymentObject.push(wechatObject)
                    }
                    if(pay_bankCard){
                        bankcardNumber = neededPay_number;
                        bankCardObject.account_name = "银行卡";
                        bankCardObject.account_id = bankCard_accountId;
                        bankCardObject.income = neededPay_number;
                        setPaymentObject.push(bankCardObject)
                    }
                    if(creditType){
                       creditNumber = neededPay_number;
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
                    $.toast("请至少选择一直支付方式");
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
        });

        //可以作为全局的方法commom.js
        function getNowFormatDate() {
            var date = new Date();
            var seperator1 = "-";
            var seperator2 = ":";
            var year = date.getFullYear();
            var month = date.getMonth() + 1;
            var strDate = date.getDate();
            if (month >= 1 && month <= 9) {
                month = "0" + month;
            }
            if (strDate >= 0 && strDate <= 9) {
                strDate = "0" + strDate;
            }
            var currentdate = year + seperator1 + month + seperator1 + strDate
                + " " + date.getHours() + seperator2 + date.getMinutes()
                + seperator2 + date.getSeconds();
            return currentdate;
        }

        //回退到主页面
        $.init();
    })
})()