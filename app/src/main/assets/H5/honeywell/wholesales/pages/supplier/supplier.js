/**
 * (c) Copyright 2016 Administrator. All Rights Reserved.
 * 2016-05-19
 *
 */
(function () {
    require(['zepto', 'common', 'supplierModule', 'template'], function ($, common, supplierModule, template) {

//        $(document).on("pageInit", "#supplierListPage", function (e, pageId, $page) {
//            common.connectJsTunnel(function (jsTunnel) {
//                common.log("jsTunnel 初始化完成");
//                jsTunnel.init();
//
//                var getCustomerList = {
//                    method: NATIVE_METHOD_SUPPLIER_GET_ALL,
//                    data: ""
//                };
//                common.jsCallNativeEventHandler(JS_CALL_NATIVE_EVENT_EXECUTE_NATIVE_METHOD, getCustomerList, function (responseData) {
//                    if (responseData.ack == 1 && common.isNotnull(responseData.response)) {
//                        $(".noDataView").hide();
//                        var supplierGroupArray = JSON.parse(responseData.response);
//                        common.log("从底层获取的供应商的数据="+responseData.response);
//                        $.each(supplierGroupArray, function (key, supplierList) {
//                            common.log("key=" + key + ", supplierList=" + JSON.stringify(supplierList));
//                            if (supplierList && supplierList.length > 0) {
//                                var templateText = '<li class="list-group-title grouptitle" style="color:#000;">' + key + '</li>';
//                                $.each(supplierList, function (k, supplier) {
//                                    templateText += $('#supplierListTemplate').html()
//                                    .replace("{{supplier_name}}", supplier.supplier_name)
//                                    .replace("{{contact_phone}}", supplier.contact_phone)
//                                    .replace("{{contact_name}}", supplier.contact_name)
//                                    .replace("{{desc_memo}}", supplier.desc_memo)
//                                    .replace("{{supplier_id}}", supplier.supplier_id);
//                                })
//                                $("#supplierList").append(templateText);
//                            }else{
//                                 $(".noDataView").show();
//                            }
//
//                        })
//                        $('.indicator-background,.indicator-div').remove();
//                    } else {
//                        $('.indicator-background,.indicator-div').remove();
//                        common.alertTips('网络异常')
//                    }
//                });
//                jsTunnel.registerHandler(
//                    NATIVE_CALL_JS_EVENT_PAGE_GO_BACK,
//                    function (data, responseCallback) {
//                        common.log("JS Registered Handler received data from Java: = " + data);
//                    }
//                );
//            });
//
//            $(document).on("click", ".showSupplierDetail", function () {
//                var supplierDetail = {
//                    contact_phone: $(this).find("#cphone").text(),
//                    contact_name: $(this).find("#cname").text(),
//                    desc_memo: $(this).find("#descMemo").val(),
//                    supplier_id: $(this).find("#spid").val(),
//                    supplier_name: $(this).find("#supplierName").text()
//                }
//                common.log("缓存需要修改和显示详情的供应商信息 supplierDetail= " + JSON.stringify(supplierDetail));
//                sessionStorage.setItem("supplierData", JSON.stringify(supplierDetail));
//                $.hideIndicator();
//                location.href = "supplierDetail.html";
//            });
//            $(document).on("click", "#addSupplierBtn", function () {
//                location.href = "addSupplier.html?flag=0";
//            });
//            $(document).on("click", "#backHomeBtn", function () {
//                common.backHomePage();
//            });
//        });


        // 供应商详情页面
        $(document).on("pageInit", "#supplierDetailPage", function (e, pageId, $page) {
            common.log("current pageId is : " + pageId);
            var topHeight = $(".shopInfo").height() + $("#cust_header").height();
            $("#custDetail-block").css("top", parseFloat($(".shopInfo").height()));
            var from = common.getQueryString("from");
            var supplierDetail = sessionStorage.getItem("supplierData");
            showSupplierDetailData(JSON.parse(supplierDetail));

            common.connectJsTunnel(function (jsTunnel) {
                jsTunnel.init();

                jsTunnel.registerHandler(
                    NATIVE_CALL_JS_EVENT_TRANSFER_DATA,
                    function (data, responseCallback) {
                        sessionStorage.setItem("supplierData", JSON.stringify(data));
                        showSupplierDetailData(data);

                    }
                );
            });

            function showSupplierDetailData(data) {
                //显示供应商详情信息
                if (common.isNotnull(data)) {
                    $("#supplier_name").text(data.supplier_name);
                    $("#contact_name").text(data.contact_name);
                    $("#contact_phone").text(data.contact_phone);
                    if(data.desc_memo){
                         $("#desc_memo").text(data.desc_memo);
                    }else{
                         $('.supplierNote').hide();
                    }
                    $('.indicator-background,.indicator-div').remove();
                }
            };

            //删除供应商
            $(document).on("click",".deleteBtn",function(){
                       var supplierData =sessionStorage.getItem('supplierData');
                       supplierModule.deleteSupplier(JSON.parse(supplierData).supplier_id, function (responseData){
                           var httpResponse = JSON.parse(responseData.response);
                           if (responseData.ack == 1 && httpResponse.retcode == 200) {
                                 if(common.isNotnull(from) && from == "native") {
                                    var userData = {
                                        method: NATIVE_PAGE_CLOSE,
                                        data: ""
                                    };
                                    common.jsCallNativeEventHandler(JS_CALL_NATIVE_EVENT_REQUEST_SWITCH_PAGE, userData, function (data) {

                                    });
                                 }else{
                                     common.goBackPage()

                                 }
                           }else if(responseData.ack == 0 && httpResponse.retcode ==4026){
                               $.toast('权限不足，无法操作')
                           }else{
                               common.log("返回错误, httpResponse.retcode=" + httpResponse.retcode);
                               $.toast('网络异常', 2000, 'error');
                           }

                      });
            });

            //修改供应商
            $(document).on("click", "#upSupplierBtn", function () {
                location.href = "addSupplier.html?flag=1";
            })

            //回退按钮
            $(document).on("click", "#backBtn", function () {

                    var userData = {
                        method: NATIVE_PAGE_CLOSE,
                        data: ""
                    };
                    common.jsCallNativeEventHandler(JS_CALL_NATIVE_EVENT_REQUEST_SWITCH_PAGE, userData, function (data) {
                        common.log("关闭当前页面！");
                    });
            });

             /* "more",hidden and show */
             !function(){
               $(".moreBtn").on("click",function(e){
                     $('.more').toggleClass("toggleCss");
                     e.cancelBubble = true;
                     });

                 $("#supplierDetailPage").not($(".moreBtn")).click(function(){
                     $('.more').removeClass("toggleCss")
                 });
                 common.deletePopup();
             }()

             /* to purchasesRecord.html */
             $(document).on('click','#productRecord',function(){
                $.router.load('#purchaseRecordPage');
             })
        })

        //查询客户
        $(document).on("click", "#searchBtn", function () {
             var userData = {
                method: NATIVE_PAGE_SUPPLIER_SEARCH,
                data: ""
            };
            common.jsCallNativeEventHandler(JS_CALL_NATIVE_EVENT_REQUEST_SWITCH_PAGE, userData, function (data) {
                common.log("打开搜索供应商页面！");
            });
        })


        //添加供应商或者修改供应商
        $(document).on("pageInit", "#addSupplierPage", function (e, pageId, $page) {
            common.log("current pageId is : " + pageId);
            var flag = common.getQueryString("flag");
            var supplierData = sessionStorage.getItem("supplierData");

            common.connectJsTunnel(function (jsTunnel) {
                common.log("jsTunnel 初始化完成");
                jsTunnel.init();
                jsTunnel.registerHandler(
                    NATIVE_CALL_JS_EVENT_PAGE_GO_BACK,
                    function (data, responseCallback) {
                        common.log("JS Registered Handler received data from Java: = " + data);
                    }
                );
            });

            if (flag == 1 && common.isNotnull(supplierData)) {
                //修改
                var supplier = JSON.parse(supplierData);
                common.log("获取session中存储的供应商信息:" + supplierData);
                $(".concat").val(supplier.contact_name);
                $(".telephone").val(supplier.contact_phone);
                $(".note").val(supplier.desc_memo);
                $(".name").val(supplier.supplier_name);
                if(validateSupplier()){
                     $('#menu').addClass('isRightInput')
                }else{
                     $('#menu').removeClass('isRightInput')
                }

                $(document).on('input','.name,.concat,.note,.telephone',function(){
                    if(validateSupplier()){
                         $('#menu').addClass('isRightInput')
                    }else{
                         $('#menu').removeClass('isRightInput')
                    }
                })


            }

            /* close loading animation */
            $('.indicator-background,.indicator-div').remove();

            $(".confirmBtn").on("click", function () {
                if($('#menu').hasClass('isRightInput')){
                    var supplierDetail = {
                        contact_name: $(".concat").val(),
                        contact_phone: $(".telephone").val(),
                        desc_memo: $(".note").val(),
                        supplier_name: $(".name").val(),
                    }
                   if(!($(".note").parent(".login_input").hasClass("errorInput"))){
                         $(this).attr("disabled","disabled");
                         if (flag == 1) {
                            supplierDetail.supplier_id = JSON.parse(supplierData).supplier_id;
                            supplierModule.updateSupplier(supplierDetail.supplier_id, supplierDetail, function (responseData) {
                                var httpResponse = JSON.parse(responseData.response);
                                if (responseData.ack == 1) {
                                    if (httpResponse.retcode == 200) {
                                        $(document).off("click", "#menu");
                                        sessionStorage.setItem("supplierData", JSON.stringify(supplierDetail));
                                         window.history.go(-1);
                                    }
                                }else if(responseData.ack == 0 && httpResponse.retcode ==4026){
                                    $(".confirmBtn").removeAttr("disabled");
                                    $.toast('权限不足，无法操作')
                                }else if(responseData.ack == 0 && httpResponse.retcode ==4014){
                                    $(".confirmBtn").removeAttr("disabled");
                                   $.toast('该供应商已存在')
                                }else {
                                    $(".confirmBtn").removeAttr("disabled");
                                    $.toast('网络异常', 2000, 'error');
                                }
                                $.hideIndicator();
                            })
                        } else {
                            $(document).off("click", "#menu");
                            var userData = {
                                method: NATIVE_METHOD_GET_USER_INFO,
                                data: ""
                            };
                            common.jsCallNativeEventHandler(JS_CALL_NATIVE_EVENT_EXECUTE_NATIVE_METHOD, userData, function (data){
                                var userInfo = JSON.parse(data.response);
                                supplierDetail.shop_id = userInfo.shopID;
                                supplierModule.addSupplier(supplierDetail, function (responseData) {
                                    var httpResponse = JSON.parse(responseData.response);
                                    if (responseData.ack == 1) {
                                        if (httpResponse.retcode == 200) {
                                             if(flag == "stock"){
                                                window.history.go(-1);
                                             }else{
                                                common.backHomePage();
                                             }
                                        }
                                    }else if(responseData.ack == 0 && httpResponse.retcode ==4026){
                                        $(".confirmBtn").removeAttr("disabled");
                                        $.toast('权限不足，无法操作')
                                    }else if(responseData.ack == 0 && httpResponse.retcode ==4014){
                                        $(".confirmBtn").removeAttr("disabled");
                                        $.toast('该供应商已存在')
                                    }else{
                                        $(".confirmBtn").removeAttr("disabled");
                                        $.toast('网络异常');
                                    }
                                    $.hideIndicator();
                                })
                            })
                        }
                   }else{
                      common.alertTips("请输入正确的信息")
                   }
                }
            });

            $(document).on("click",".backNative",function(){
                var flag =common.getQueryString("flag");
                if((common.isNotnull(flag) && flag == "1") || flag =="stock"){
                     window.history.go(-1);
                }else{
                    var userData = {
                        method: NATIVE_PAGE_CLOSE,
                        data: ""
                    };
                    common.jsCallNativeEventHandler(JS_CALL_NATIVE_EVENT_REQUEST_SWITCH_PAGE, userData, function (data) {
                        common.log("关闭当前页面！");
                    });
                }
            })


            /* supplier page  validate*/
            $(document).on('input','.name,.concat,.note,.telephone',function(){
                if($(this)[0] == document.querySelector('.telephone')){
                    if (!common.regTel($(this).val())) {
                        $(this).parent(".login_input").addClass("errorInput");
                    } else {
                        $(this).parent(".login_input").removeClass("errorInput");
                    }
                }else if($(this)[0] == document.querySelector('.note')){
                    if (!common.regNote($(this).val())) {
                        $(this).parent(".login_input").addClass("errorInput");
                    } else {
                        $(this).parent(".login_input").removeClass("errorInput");
                    }
                }else{
                    if (!common.regName($(this).val())) {
                        $(this).parent(".login_input").addClass("errorInput");
                    } else {
                        $(this).parent(".login_input").removeClass("errorInput");
                    }
                }

                if(validateSupplier()){
                    $('#menu').addClass('isRightInput')
                }else{
                    $('#menu').removeClass('isRightInput')
                }

            });

            $(document).on('blur','.name,.concat,.note,.telephone',function(){
                if($(this)[0] == document.querySelector('.telephone')){
                    if (!common.regTel($(this).val())) {
                        $(this).parent(".login_input").addClass("errorInput");
                        $.toast("电话请输入数字",1300,'toastInfo');
                    } else {
                        $(this).parent(".login_input").removeClass("errorInput");
                    }
                }else if($(this)[0] == document.querySelector('.note')){
                    if (!common.regNote($(this).val())) {
                        $(this).parent(".login_input").addClass("errorInput");
                        $.toast("备注请输入中英文数字或下划线",1300,'toastInfo')
                    } else {
                        $(this).parent(".login_input").removeClass("errorInput");
                    }
                }else{
                    if (!common.regName($(this).val())) {
                        $(this).parent(".login_input").addClass("errorInput");
                        $.toast("供应商名请输入中英文数字或下划线",1300,'toastInfo')
                    } else {
                        $(this).parent(".login_input").removeClass("errorInput");
                    }
                }

            });



            function validateSupplier(){

                var valiteFlag = true;

                if (!common.regName($(".name").val())) {
                    valiteFlag = false;
                }

                if (!common.regName($(".concat").val())) {
                    valiteFlag = false;
                }

                if (!common.regTel($(".telephone").val())) {
                    valiteFlag = false;
                }

                if (valiteFlag) {
                    return true;
                } else {
                    return false;
                }
            }
        });

        /*purchase record*/
        $(document).on('pageInit','#purchaseRecordPage',function(){
           var data = sessionStorage.getItem("supplierData"),
               supplierId ='',
               supplierName ='';
           if(data){
               supplierId = (JSON.parse(data)).supplier_id;
               supplierName = (JSON.parse(data)).supplier_name;
               console.log('获取的供应商信息='+data);
           };

            common.connectJsTunnel(function (jsTunnel) {
                jsTunnel.registerHandler(
                    NATIVE_CALL_JS_EVENT_PAGE_GO_BACK,
                    function (data, responseCallback) {
                        common.log("JS Registered Handler received data from Java: = " + data);
                    }
                );

                var userData = {
                    method: NATIVE_METHOD_GET_USER_INFO,
                    data: ""
                };

                common.jsCallNativeEventHandler(JS_CALL_NATIVE_EVENT_EXECUTE_NATIVE_METHOD, userData, function (data) {
                   var userInfo = JSON.parse(data.response);
                     /*web request for product record*/
                    var supplierProductRecord ={
                          "shop_id":parseInt(userInfo.shopID),
                          "supplier_id":parseInt(supplierId)
                      };
                    var supplierProductRecordData =common.dataTransform('transaction/buy/list','POST',supplierProductRecord);
                    common.jsCallNativeEventHandler(
                       JS_CALL_NATIVE_EVENT_REQUEST_WEB_API,
                       supplierProductRecordData,
                       function(data){
                          var respondData =data.response;
                          console.log('respondBody='+respondData);
                          respondData =JSON.parse(respondData);
                          if(respondData.retcode ==200){
                             var retBody = respondData.retbody.total_stock_in_list,
                             template ='';
                               if(retBody && retBody.length > 0){
                                  $.each(retBody,function(key,data){
                                     template +=$('#purchaseInfomation').html()
                                        .replace('{{productName}}',data.product_name)
                                        .replace('{{productPrice}}','￥'+data.min_price+'-'+data.max_price)
                                        .replace('{{supplierName}}',data.supplier_name)
                                        .replace('{{productNumber}}',data.sum_number)
                                        .replace('{{purchaseTime}}',data.purchase_time)
                                  })
                                  $('.recordInfo').html(template);
                               }else{
                                  $('.noInfo').show()
                               }
                               /* close loading animation */
                               $('.indicator-background,.indicator-div').remove();
                          }else{
                            /* close loading animation */
                            $('.indicator-background,.indicator-div').remove();
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