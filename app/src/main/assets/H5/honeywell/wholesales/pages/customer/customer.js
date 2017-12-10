/**
 * (c) Copyright 2016 Administrator. All Rights Reserved.
 * 2016-05-19
 *
 */
(function () {
    require(['zepto', 'common', 'customerModule', 'supplierModule', 'template'], function ($, common, custModule, supplier, template) {

//        $(document).on("pageInit", "#customersListPage", function (e, pageId, $page) {
//            var shopId = "";
//            var customerShopId ='';
//            common.connectJsTunnel(function (jsTunnel) {
//                jsTunnel.init();
//                jsTunnel.registerHandler(
//                    NATIVE_CALL_JS_EVENT_PAGE_GO_BACK,
//                    function (data, responseCallback) {
//                        common.log("JS Registered Handler received data from Java: = " + data);
//
//                    }
//                );
//
//                jsTunnel.registerHandler(
//                    NATIVE_CALL_JS_EVENT_TRANSFER_DATA,
//                    function (data, responseCallback) {
//                        common.log("NATIVE_CALL_JS_EVENT_TRANSFER_DATA");
//                    }
//                );
//
//                var getCustomerList = {
//                    method: NATIVE_METHOD_CUSTOMER_GET_ALL,
//                    data: ""
//                };
//                //从底层拿到的数据更新
//                common.jsCallNativeEventHandler(JS_CALL_NATIVE_EVENT_EXECUTE_NATIVE_METHOD, getCustomerList, function (responseData) {
//                    if (responseData.ack == 1 && common.isNotnull(responseData.response)) {
//                        $(".noDataView").hide();
//                        var customersGroupArray = JSON.parse(responseData.response);
//                        common.log("客户清单="+responseData.response);
//                        $.each(customersGroupArray, function (key, customerList) {
//                            common.log("key=" + key + ", customerList=" + JSON.stringify(customerList));
//                            if (customerList && customerList.length > 0) {
//                                var templateText = '<li class="list-group-title grouptitle" style="color:#000;">' + key + '</li>';
//                                $.each(customerList, function (k, customer) {
//                                    //在dom 元素上存储客户信息
//                                    templateText += '<li  class="showCustomerDetail"   data-name="' + customer.contact_name + '" ' +
//                                        'data-cid="' + customer.customer_id + '" data-phone="' + customer.contact_phone + '" data-address="' + customer.address +
//                                        '" data-memo="' + customer.memo + '" data-shopid="' + customer.shop_id+'">' +
////                                        '<input type="hidden" class="invoice_title" value="' + customer.invoice_title + '"/>' +
////                                        '<input type="hidden" class="customerInfo"  data-debtdealsnumber="'+customer.debt_deals_number +'" data-debtdealsprice="'+customer.debt_deals_price +
////                                        '" data-totaldealsnumber="'+customer.total_deals_number + '"  data-totaldealsprice="'+customer.total_deals_price +
////                                        '" data-lastsaletime="'+ customer.last_sale_time +'"/>' +
//                                        '<a href="javascript:void(0)" class="item-link item-content"><div class="item-inner"><div class="item-title-row">' +
//                                        '<div class="item-title customer-name">' + customer.customer_name + '</div></div>'+
//                                        '<div class="item-subtitle customer-concatInfo">' + customer.contact_phone + '</div></div></a></li>';
//                                })
//                                $("#customerList").append(templateText);
//                            }else{
//                                 $(".noDataView").show();
//                            }
//                        });
//                        $('.indicator-background,.indicator-div').remove();
//                    }else {
//                        $('.indicator-background,.indicator-div').remove();
//                        common.alertTips('网络异常')
//                    }
//                });
//
//            });
//
//            //查询客户
//            $(document).on("click", "#searchBtn", function () {
//                var userData = {
//                    method: NATIVE_PAGE_CUSTOMER_SEARCH,
//                    data: ""
//                };
//                common.jsCallNativeEventHandler(JS_CALL_NATIVE_EVENT_REQUEST_SWITCH_PAGE, userData, function (data) {
//                    common.log("打开搜索客户页面！");
//                });
//            })
//
//            //添加客户
//            $(document).on("click", "#addCustBtn", function () {
//                location.href = "addCustomer.html?flag=0";
//            })
//
//            //跳转客户详情页面,从dom元素上将信息取出后,然后缓存起来,在详情页展示
//            $(document).on("click", ".showCustomerDetail", function () {
//                common.log("获取客户详情信息并存储:" + $(this).find(".invoice_title").val());
//                var customerJson = {
//                    "contact_name": $(this).data("name"),
//                    "customer_id": $(this).data("cid"),
//                    "contact_phone": $(this).find('.customer-concatInfo').text(),
//                    "customer_name": $(this).find(".customer-name").text(),
//                    "memo": $(this).data("memo"),
//                    "shop_id":$(this).data('shopid'),
////                    "debt_deals_number":$(this).find(".customerInfo").data('debtdealsnumber'),
////                    "debt_deals_price":$(this).find(".customerInfo").data('debtdealsprice'),
////                    "total_deals_number":$(this).find(".customerInfo").data('totaldealsnumber'),
////                    "total_deals_price":$(this).find(".customerInfo").data('totaldealsprice'),
////                    "last_sale_time":$(this).find(".customerInfo").data('lastsaletime'),
//                }
//                sessionStorage.setItem("updateCustInfo", JSON.stringify(customerJson));
//                common.log('点击存储客户信息='+JSON.stringify(customerJson))
//                location.href = "customerDetail.html";
//            })
//            $(document).on("click", "#backHomeBtn", function () {
//                common.backHomePage();
//            })
//        })

        //客户详情
        $(document).on("pageInit", "#customerDetailPage", function (e, pageId, $page){
            var customerId_deal ='';
            common.log("current pageId is : " + pageId);
            var topHeight = parseFloat($(".shopInfo").height() + $("#cust_header").height());
            $("#custDetail-block").css("top", parseFloat($(".shopInfo").height()));
            var from = common.getQueryString("from");
            common.log('截取的form='+from)
            var data = sessionStorage.getItem("updateCustInfo");
            showCustomerDetailData(JSON.parse(data));

            common.connectJsTunnel(function (jsTunnel) {
                var flagH5 =common.getQueryString("from");

                    jsTunnel.init();

                jsTunnel.registerHandler(
                    NATIVE_CALL_JS_EVENT_TRANSFER_DATA,
                    function (data, responseCallback) {
                        sessionStorage.setItem("updateCustInfo", JSON.stringify(data));
                        showCustomerDetailData(data);
                        //判断由原生进入的详情页面,标识退出位置;
                        sessionStorage.setItem('mark','native')
                    }
                );

                $(document).on('click','.newOrder',function(){
                     var data = sessionStorage.getItem("updateCustInfo");
                     common.log("客户customer_id" + JSON.parse(data).customer_id);
                     var newOrderEvent = {
                         method:NATIVE_PAGE_CREATE_NEW_ORDERS,
//                         data:JSON.parse(data).customer_id
                         data:JSON.stringify(data)
                     };
                     common.jsCallNativeEventHandler(
                        JS_CALL_NATIVE_EVENT_REQUEST_SWITCH_PAGE,
                        newOrderEvent,
                        function (data) {
                            common.log("打开创建新预售单界面！");
                     });

                });
            });

            function showCustomerDetailData(data) {
                if (common.isNotnull(data)) {
                    common.log("客户详情: " + JSON.stringify(data));
                    customerId_deal = data.customer_id;
                    $(".customer_name").text(data.customer_name);
                    $("#contact_name").text(common.isNotnull(data.contact_name) ? data.contact_name : "没有提供联系人姓名");
                    $("#contact_phone").text(common.isNotnull(data.contact_phone) ? data.contact_phone : "没有提供联系人联系方式");
//                    $("#currentCredit").text(common.isNotnull(data.debt_deals_price) ? "￥"+ data.debt_deals_price : "￥");
//                    $("#creditNumber").text(common.isNotnull(data.debt_deals_number) ? data.debt_deals_number+"笔" : "0笔");
//                    $("#totalPrice").text(common.isNotnull(data.total_deals_price) ? "￥"+data.total_deals_price : "￥");
//                    $("#totalDealNum").text(common.isNotnull(data.total_deals_number) ? data.total_deals_number+"笔" : "0笔");
//                    $("#lastDealTime").text(common.isNotnull(data.last_sale_time) ? common.translateTime(data.last_sale_time) : "");

                    /* show customer information*/
                    var customerSaleInfo ={
                          "shop_id":parseInt(data.shop_id),
                          "customer_id":parseInt(data.customer_id)
                      };
                    var customerSaleInfoRequest =common.dataTransform('customer/detail','POST',customerSaleInfo);
                 common.connectJsTunnel(function (jsTunnel) {
                    common.jsCallNativeEventHandler(
                       JS_CALL_NATIVE_EVENT_REQUEST_WEB_API,
                       customerSaleInfoRequest,
                       function(data){
                          var respondData =data.response;
                          console.log('respondBody='+respondData);
                          respondData =JSON.parse(respondData);
                          var retBody = respondData.retbody,
                              template ='';
                          if(respondData.retcode ==200){
                              $("#currentCredit").text(common.isNotnull(retBody.debt_deals_price) ? "￥"+ retBody.debt_deals_price : "￥");
                              $("#creditNumber").text(common.isNotnull(retBody.debt_deals_number) ? retBody.debt_deals_number+"笔" : "0笔");
                              $("#totalPrice").text(common.isNotnull(retBody.total_deals_price) ? "￥"+retBody.total_deals_price : "￥");
                              $("#totalDealNum").text(common.isNotnull(retBody.total_deals_number) ? retBody.total_deals_number+"笔" : "0笔");
                              $("#lastDealTime").text(common.isNotnull(retBody.last_sale_time) ? retBody.last_sale_time : "");
                          }else if(respondData.retcode ==4026){
                            common.alertTips("权限不足，获取客户信息失败！");
                          }else{
                            common.alertTips("网络异常");
                          }
                          $('.indicator-background,.indicator-div').remove();

                       }
                    )
                 });
                    if(data.memo){
                         $("#desc_memo").text(data.memo);
                    }else{
                         $('.customerNode').hide();
                    }
                }
            };

            //回退按钮
            $(document).on("click", "#backBtn", function () {
              var backMark =sessionStorage.getItem('mark')
                if(common.isNotnull(backMark) && backMark == "native") {
                    var userData = {
                        method: NATIVE_PAGE_CLOSE,
                        data: ""
                    };
                    common.jsCallNativeEventHandler(JS_CALL_NATIVE_EVENT_REQUEST_SWITCH_PAGE, userData, function (data) {
                        common.log("关闭当前页面！");
                    });
                } else {
                    window.history.go(-1);
                }
            });

            //修改客户 并赋予标识符
            $(document).on("click", "#updateCust", function () {
                $(document).off("click", "#updateCust");
                location.href="addCustomer.html?flag=1";
            })

            //删除客户
            $(document).on("click", ".deleteBtn", function (){
                var data = sessionStorage.getItem("updateCustInfo");
                    $(document).off("click", ".deleteBtn");
                    custModule.deleteCustomer(JSON.parse(data).customer_id, function (responseData) {
                        var httpResponse = JSON.parse(responseData.response);
                        if (responseData.ack == 1 && httpResponse.retcode == 200) {
                                if(common.isNotnull(from) && from == "native") {
                                    var userData = {
                                        method: NATIVE_PAGE_CLOSE,
                                        data: ""
                                    };
                                    common.jsCallNativeEventHandler(JS_CALL_NATIVE_EVENT_REQUEST_SWITCH_PAGE, userData, function (data) {
                                        common.log("关闭当前页面！");
                                    });
                                } else {
                                    window.history.go(-1);
                                }
                        }else if(responseData.ack == 0 && httpResponse.retcode == 4026){
                            $.toast('权限不足，无法操作')
                        }else {
                            $.toast('网络异常', 2000, 'error');
                        }
                        $.hideIndicator();
                    })
            });

            !function(){
                $(".moreBtn").on("click",function(e){
                       $('.more').toggleClass("toggleCss");
                       e.cancelBubble = true;
                });

                $("#customerDetailPage").not($(".moreBtn")).click(function(){
                       $('.more').removeClass("toggleCss")
                });
                common.deletePopup();
            }()

             /* to dealRecord.html */
            $(document).on('click','#dealRecord',function(){
//                    $.router.load('#dealRecordPage');
             location.href="customerOrderList.html";
            });
        });

        //添加或者修改客户信息
        $(document).on("pageInit", "#addCustomerPage", function (e, pageId, $page) {
            common.log("current pageId is : " + pageId);
            var customerId ='';
            var flag = common.getQueryString("flag"), type = common.getQueryString("type"), custInfo = "";
            common.log("判断是增加客户还是修改客户的标识flag" + flag + "进入页面的标识type,如果type是order,则最后返回到sureOrder页面,type=" + type);//flag=1修改客户,flag=0增加用户

            var custInfo = JSON.parse(sessionStorage.getItem("updateCustInfo"));

            if (flag == 1) {
                common.log("修改客户信息,获取session中的客户信息:" + sessionStorage.getItem("updateCustInfo"));
                $(".name").val(common.isNotnull(custInfo.customer_name) ? custInfo.customer_name : "");
                $(".concat").val(custInfo.contact_name);
                $(".telephone").val(custInfo.contact_phone);
                $(".address").val(custInfo.address);
                customerId = custInfo.customer_id
                $(".note").val(common.isNotnull(custInfo.memo) ? custInfo.memo : "");
                $(".receipt").val(common.isNotnull(custInfo.invoice_title) ? custInfo.invoice_title : "");
                if( validateCustomer()){
                     $('#menu').addClass('isRightInput')
                }else{
                     $('#menu').removeClass('isRightInput')
                }

                $(document).on('input','.name,.concat,.note,.telephone',function(){
                    if( validateCustomer()){
                         $('#menu').addClass('isRightInput')
                    }else{
                         $('#menu').removeClass('isRightInput')
                    }
                })

            }
            /* close loading animation */
            $('.indicator-background,.indicator-div').remove();

            var userData = {
                method: NATIVE_METHOD_GET_USER_INFO,
                data: ""
            };

            var userInfo = {};
            common.connectJsTunnel(function (jsTunnel) {
                jsTunnel.init();
                common.jsCallNativeEventHandler(JS_CALL_NATIVE_EVENT_EXECUTE_NATIVE_METHOD, userData, function (data) {
                    userInfo = JSON.parse(data.response);
                    common.log('获取的用户信息='+data.response)
                })
            });


            $(document).on('input','.login_input input',function(){
                validateCustomer()
            })

            //添加客户 或者修改客户
            $(".confirmBtn").on("click", function() {
               if($('#menu').hasClass('isRightInput')){
                 if(!($(".note").parent(".login_input").hasClass("errorAdd"))){
                      $(this).attr("disabled","disabled");
                      if (flag == 1) {  
                        common.log("修改客户,获取客户的缓存信息=1" + JSON.stringify(userInfo));

                            var customer = {
                                customer_name: $(".name").val(),
                                customer_id:customerId,
                                address: $(".address").val(),
                                contact_name: $(".concat").val(),
                                contact_phone: $(".telephone").val(),
                                memo: $(".note").val(),
                                invoice_title: $(".receipt").val(),
                                shop_id :userInfo.shopID
//                              shop_id :custInfo.shop_id
                            }
                               common.log("修改的用户信息customer=" + JSON.stringify(customer));
                               custModule.updateCustomer(customer.shop_id, customer, function (responseData) {
                                   var httpResponse = JSON.parse(responseData.response);
                                   if (responseData.ack == 1 && httpResponse.retcode == 200) {
                                          if(type && type == "order") {
                                              sessionStorage.setItem("orderCustomerInfo", JSON.stringify(customer));
                                              common.log("缓存需要携带到order页面的用户信息:" + JSON.stringify(customer));
                                              location.replace("../order/sureOrder.html");
                                          }else {
                                              //判断为客户详情进入的页面后,修改后讲本地缓存的信息更新,然后返回客户详情页
                                              sessionStorage.setItem("updateCustInfo",JSON.stringify(customer));
                                              window.history.go(-1);
                                          }
                                   }else if(responseData.ack == 0 && httpResponse.retcode == 4026){
                                       $(".confirmBtn").removeAttr("disabled");
                                       $.toast('权限不足，无法操作')
                                   }else if(responseData.ack == 0 && httpResponse.retcode == 4015){
                                       $(".confirmBtn").removeAttr("disabled");
                                       $.toast('很抱歉！客户已存在')
                                   }else {
                                       $(".confirmBtn").removeAttr("disabled");
                                       $.toast('网络异常', 2000, 'error');
                                   }
                                   $.hideIndicator();
                               })
                    }else{
                            var customer = {
                                customer_name: $(".name").val(),
                                address: $(".address").val(),
                                contact_name: $(".concat").val(),
                                contact_phone: $(".telephone").val(),
                                memo: $(".note").val(),
                                invoice_title: $(".receipt").val(),
                                shop_id :userInfo.shopID
                            }
                            common.log("当前登陆用户的信息" + JSON.stringify(userInfo) + "添加的用户信息customer=" + JSON.stringify(customer));
                            custModule.addCustomer(customer.shop_id, customer, function (responseData){
                                var httpResponse = JSON.parse(responseData.response);
                                if (responseData.ack == 1 && httpResponse.retcode == 200) {
                                        if (type && type == "order") {
                                            customer.customer_id = httpResponse.retbody.customer_id;
                                            sessionStorage.setItem("orderCustomerInfo", JSON.stringify(customer));
                                            common.log("缓存需要携带到order页面的用户信息:" + JSON.stringify(customer));
                                            location.replace("../order/sureOrder.html");
                                        } else {
                                            //后退后本地数据库已刷新
                                            common.backHomePage();
                                        }
                                }else if(responseData.ack == 0 && httpResponse.retcode == 4026){
                                    $(".confirmBtn").removeAttr("disabled");
                                    $.toast('权限不足，无法操作')
                                }else if(responseData.ack == 0 && httpResponse.retcode == 4015){
                                     $(".confirmBtn").removeAttr("disabled");
                                     $.toast('很抱歉！客户已存在')
                                }else {
                                    $(".confirmBtn").removeAttr("disabled");
                                    $.toast('网络异常', 2000, 'error');
                                }
                                $.hideIndicator();
                            });
                    }
                 }else{
                     common.alertTips("请输入正确的信息")
                 }
               }
            });

            $(document).on("click", "#cancel", function () {
                if (flag == 1) {
                    var customer = {
                        customer_name: $(".name").val(),
                        customer_id: custInfo.customer_id + "",
                        address: $(".address").val(),
                        contact_name: $(".concat").val(),
                        contact_phone: $(".telephone").val(),
                        memo: $(".note").val(),
                        invoice_title: $(".receipt").val()
                    }
                    $(document).off("click", "#cancel");
                    sessionStorage.setItem("orderCustomerInfo", JSON.stringify(customer));
                    if (type && type == "order") {
                        common.log("缓存需要携带到order页面的用户信息:" + JSON.stringify(customer));
                        location.replace("../order/sureOrder.html");
                    } else {
                        window.history.go(-1);
                    }
                } else {
                    if (type && type == "order") {
                        location.replace("../order/sureOrder.html");
                    } else {
                        common.backHomePage();
                    }
                }
            })

             /* customer page validate */

            $(document).on('input','.name,.concat,.note,.telephone',function(){
                if($(this)[0] == document.querySelector('.telephone')){
                    if (!common.regTel($(this).val())) {
                        $(this).parent(".login_input").addClass("errorAdd");
                    } else {
                        $(".telephone").parent(".login_input").removeClass("errorAdd");
                    }
                }else if($(this)[0] == document.querySelector('.note')){
                     if (!common.regNote($(this).val())) {
                         $(this).parent(".login_input").addClass("errorAdd");
                     } else {
                         $(this).parent(".login_input").removeClass("errorAdd");
                     }
                }else{
                    if (!common.regName($(this).val())) {
                        $(this).parent(".login_input").addClass("errorAdd");
                    } else {
                        $(this).parent(".login_input").removeClass("errorAdd");
                    }
                }

                if(validateCustomer()){
                    $('#menu').addClass('isRightInput')
                }else{
                    $('#menu').removeClass('isRightInput')
                }

            });

            $(document).on('blur','.name,.concat,.note,.telephone',function(){
                if($(this)[0] == document.querySelector('.telephone')){
                    if (!common.regTel($(this).val())) {
                        $(this).parent(".login_input").addClass("errorAdd");
                         $.toast("电话请输入数字",1300,'toastInfo');
                    } else {
                        $(".telephone").parent(".login_input").removeClass("errorAdd");
                    }
                }else if($(this)[0] == document.querySelector('.note')){
                     if (!common.regNote($(this).val())) {
                         $(this).parent(".login_input").addClass("errorAdd");
                         $.toast("备注请输入中英文数字下划线",1300,'toastInfo')
                     } else {
                         $(this).parent(".login_input").removeClass("errorAdd");
                     }
                }else{
                    if (!common.regName($(this).val())) {
                        $(this).parent(".login_input").addClass("errorAdd");
                        $.toast("客户名请输入中英文数字或下划线",1300,'toastInfo')
                    } else {
                        $(this).parent(".login_input").removeClass("errorAdd");
                    }
                }

            });



             function validateCustomer() {
                var customerName = $(".name").val();
                var customerContact = $(".concat").val();
                var customerTel = $(".telephone").val();

                var valiteFlag = true;

                if (!common.regName(customerName)) {
                    valiteFlag = false;
                }

                if (!common.regAddress(customerContact)) {
                    valiteFlag = false;
                }

                if (!common.regTel(customerTel)) {
                    valiteFlag = false;
                }

                if (valiteFlag) {
                    return true;
                } else {
                    return false;
                }
             }

        });

        /* deal record page */
        $(document).on('pageInit','#dealRecordPage',function(){
            var data = sessionStorage.getItem("updateCustInfo"),
                customerId ='',
                orderState = '',
                orderType = '',
                orderTime ='',
                translatedata = [],
                shopName ="";

            console.log("获取到了缓存的信息="+data);

            if(data){
                customerId = (JSON.parse(data)).customer_id;
            }

            /* click to order detail page */
            $(document).off("click",".dealInfo");
            $(document).on("click",".dealInfo",function(){
                var orderIndex = parseInt($(this).data("index")),
                    orderInfo = translatedata[orderIndex];
                    orderInfo.shop_name =shopName;
                sessionStorage.setItem("translateOrderInfo",JSON.stringify(orderInfo));
                window.location.href="../order/orderStateDetail.html?from=customer";
            })


            common.connectJsTunnel(function (jsTunnel) {
                 jsTunnel.init();

            var userData = {
                method: NATIVE_METHOD_GET_USER_INFO,
                data: ""
            };
            common.jsCallNativeEventHandler(JS_CALL_NATIVE_EVENT_EXECUTE_NATIVE_METHOD, userData, function (data) {
                console.log("获取到用户的所有信息="+data.response);
                var userInfo = JSON.parse(data.response);
                var customerSaleRecord ={
                      "shop_id":userInfo.shopID,
                      "customer_id":customerId,
                       "sale_type":0,
                      "page_length":1000
                    };
                shopName = userInfo.shopName;
                var customerSaleRecordData =common.dataTransform('transaction/sale/search','POST',customerSaleRecord);
                 /*web request for customer deal record*/
                 $.showIndicator();
                 common.jsCallNativeEventHandler(
                    JS_CALL_NATIVE_EVENT_REQUEST_WEB_API,
                    customerSaleRecordData,
                    function(data){
                       var respondData =data.response;
                       respondData =JSON.parse(respondData);
                       var retbody = respondData.retbody.total_order_list,
                           template ='';
                       if(respondData.retcode ==200){
                            if(retbody && retbody.length> 0){
                               $.each(retbody,function(key,data){
                                  var productName ="";
                                  translatedata.push(data);
                                  if(data.order_status == 1){
                                        orderState ='已结清';
                                        orderType = '结清处理';
                                        orderTime = common.translateTime(data.finish_dt);
                                  }else if(data.order_status ==100){
                                        orderState ='已取消';
                                        orderType = '取消处理';
                                        orderTime = common.translateTime(data.cancel_dt);
                                  }else if(data.order_status == 0 && data.payment ==0){
                                        orderState ='未支付'
                                        orderType = '待支付';
                                        orderTime = data.sale_time;
                                  }else if(data.order_status == 0 && data.payment ==2){
                                        orderState ='赊账中';
                                        orderType = '赊账处理';
                                        orderTime = common.translateTime(data.set_pay_dt);
                                  }
                                  $.each(data.sale_list,function(i,info){
                                     if(i < ((data.sale_list).length-1)){
                                        productName += info.name + ","
                                     }else{
                                        productName += info.name;
                                     }
                                  });

                                  template +=$('#dealInfomation').html()
                                     .replace('{{customerName}}',data.customer_name)
                                     .replace('{{productPrice}}','￥'+data.total_price)
                                     .replace('{{productName}}',productName)
                                     .replace('{{productProperty}}','')
                                     .replace('{{orderState}}',orderState)
                                     .replace('{{dealTime}}',orderTime)
                                     .replace('{{dealMan}}',data.employee_name)
                                     .replace('{{dealType}}',orderType)
                                     .replace('{{index}}',key)
                               });
                               $('.recordInfo').html(template);
                            }else{
                                $('.noInfo').show();
                            }
                            /* close loading animation */
                            $.hideIndicator();
                       }else{
                         /* close loading animation */
                         $.hideIndicator();
                         common.alertTips("网络异常");
                       }
                    }
                 )
               });
            });
        })
        $.init();
    });
})();