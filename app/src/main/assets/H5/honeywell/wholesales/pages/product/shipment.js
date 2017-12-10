/**
 * Created by caihong on 16/7/15.
 */
(function () {
    require(['zepto', 'common', 'salesModule', 'template', 'customerModule'], function ($, common, salesModule, template, custModule) {
        //计算
        var smProHeight = parseFloat($(".shipMent_proInfo").height()) + parseFloat($(".bar").height());
        $(".sm-shopInfo ").css("height", $(window).height() - smProHeight).css("top", smProHeight);

        var shopId = sessionStorage.getItem("shopId"), scanProductInfo = sessionStorage.getItem("scanProduct");
        var product_code = sessionStorage.getItem("mData"),product_id ="";
        var customerId = 0,visitorId ='',userInfo ={}, custInfo = {} ,productInfo= {},inventoryNumber = '';// 0表示散客
        var orderListInfo =[];
        $(document).on("pageInit", "#shipMentPage", function (e, pageId, $page) {

            /* H5 progress */

            common.log("获取到的商品信息product=" + scanProductInfo);
             if(scanProductInfo){
                productInfo = JSON.parse(scanProductInfo);
                product_code = productInfo.product_code;
                product_id = parseInt(productInfo.product_id);
                showProductInfo(productInfo);

                 /* get url customerId form scan */
                var urlCustomerId = sessionStorage.getItem("urlCustomerId");
                var urlCustomerName = sessionStorage.getItem("urlCustomerName");
                if(urlCustomerId){
                    customerId =  parseInt(urlCustomerId);
                    $(".client_name").val(urlCustomerName);
                    $(".sm-cusName").html(urlCustomerName);
                     if(isNaN(parseInt((urlCustomerId)[0]))){
                            visitorId = urlCustomerId;
                     }else{
                           showCustomerSale(urlCustomerId,product_id);
                     }
                }
                $('.indicator-background,.indicator-div').remove();
                inventoryNumber = productInfo.quantity
            };

            /* Native progress*/
            common.connectJsTunnel(function (jsTunnel) {
                jsTunnel.init();
                var userData = {
                    method: NATIVE_METHOD_GET_USER_INFO,
                    data: ""
                };

                common.jsCallNativeEventHandler(JS_CALL_NATIVE_EVENT_EXECUTE_NATIVE_METHOD, userData, function (data) {
                    userInfo = JSON.parse(data.response);
                    console.log('获取的用户信息='+data)
                });
                 jsTunnel.registerHandler(
                     NATIVE_CALL_JS_EVENT_TRANSFER_DATA,
                     function (allData ,responseCallback){
                      console.log("获取到原生传入的数据"+JSON.stringify(allData));
                      var data = allData[allData.length -1];
                      if(allData.length >1){
                         allData.splice(allData.length -1,1);
                         orderListInfo = allData;
                         console.log("编辑后的新数组"+JSON.stringify(orderListInfo));
                      }
                      product_id = data.productId;

                      /* judge  input customer type,visitor or customer */
                      if(isNaN(parseInt((data.customerId)[0]))){
                            visitorId = data.customerId;
                            customerId = data.customerId;
                      }else{
                           customerId = data.customerId
                      }

                      /* http request for product detail */
                      var productData ={
                          "shop_id":data.shopId,
                          "product_code": String(data.productId),
                          "product_id":parseInt(data.productId)
                      }
                      salesModule.querySaleDetail(productData, function (httpResponse) {
                           var data = JSON.parse(httpResponse.response);
                           if(httpResponse.ack == 1 && data.retcode == 200) {
                                var showProduct = data.retbody;
                                showProductInfo(showProduct);
                                productInfo.product_name = showProduct.product_name;
                                productInfo.pic_src = showProduct.pic_src;
                                productInfo.unit = showProduct.unit;
                                inventoryNumber = showProduct.quantity;
                                $('.indicator-background,.indicator-div').remove();
                           }else{
                                $('.indicator-background,.indicator-div').remove();
                                common.alertTips('网络异常')
                           }
                      });

                      /*show customer information*/
                      product_code =data.productId;
                      $(".client_name").val(data.customerName);
                      $(".sm-cusName").html(data.customerName);
                      if(customerId >0){
                         showCustomerSale(customerId,data.productId);
                      }

                      /* transform data to cart JSON */
                      custInfo.contact_name =data.contactName;
                      custInfo.contact_phone =data.contactPhone;
                      custInfo.invoiceTitle = data.invoiceTitle;
                      custInfo.address = data.address;

                      /*cancel event entrust*/
                      $(document).off('click', '.open-customer');
                      $('.indicator-background,.indicator-div').remove();
                 });
            });

            /* set input cursor to right and keep */
            $(document).on("click",".qty,.product_price,.total_money",function(){
                var txtFocus = $(this)[0];
                var userAgent= window.navigator.userAgent.toLowerCase();
                var isWwebkit = /webkit/.test(userAgent);//谷歌
                if(isWwebkit){
                    txtFocus.setSelectionRange((txtFocus.value).length,(txtFocus.value).length);
                }
            })


            /* show product information*/
            function showProductInfo(data){
                var productInfo = data
                $(".product_name").html(productInfo.product_name);
                $(".product_price").val(productInfo.standard_price);

                var sellNumber = parseInt(productInfo.stock_quantity);
                if(sellNumber > 10) {
                    sellNumber = 10;
                }else if(sellNumber < 1){
                    sellNumber = 1;
                }
                $(".qty").val(sellNumber);

                 //商品信息
                $("#standard_price").html("¥" + productInfo.standard_price);
                $("#allSellNum").html((productInfo.sale_number? productInfo.sale_number+productInfo.unit:''));
                $("#number").html(productInfo.stock_quantity + productInfo.unit);
                $("#stock_price").html("¥" +productInfo.stock_price);

                var qty = $(".qty").val(), price = $(".product_price").val();
                $(".total_money").val((price * qty).toFixed(2));
            }

            /* fast settlement */

             $(document).on("click","#nowToClear",function(){
                var productNumber = $(".qty").val();
                if(isNaN(parseInt(productNumber))){
                    productNumber = 0;
                }
                if(inventoryNumber >= parseInt(productNumber)){
                      if(shipmentValidate()){
                           if(isNaN($('.total_money').val()) || isNaN($('.product_price').val())){
                              common.alertTips("输入错误,请重新输入")
                           }else{
                              var cartItemJson = {
                                 "employeeId": userInfo.employeeId,
                                 "userName": userInfo.userName,
                                 "visitorId": visitorId ? visitorId :'',
                                 "customerId": customerId ? customerId: 0,
                                 "customerName": $(".client_name").val()? $(".client_name").val():'',
                                 "invoiceTitle": custInfo.invoiceTitle,
                                 "contactName":custInfo.contact_name,
                                 "contactPhone": custInfo.contact_phone,
                                 "address": custInfo.address != null ? custInfo.address : '',
                                 "productCode": product_code,
                                 "productName": productInfo.product_name,
                                 "unitPrice": $(".product_price").val(),
                                 "totalNumber": parseInt($(".total_number").val()),
                                 "imageUrl": productInfo.pic_src,
                                 "unit": productInfo.unit,
                                 "shopId": userInfo.shopID,
                                 "shopName": userInfo.shopName,
                                 "productId":product_id
                              };

                              if(orderListInfo.length >0){
                                 var productArray = [];
                                 $.each(orderListInfo,function(key,info){
                                      if(cartItemJson.productId == orderListInfo[key].productId){
                                          orderListInfo[key].totalNumber = parseInt(orderListInfo[key].totalNumber) + parseInt(cartItemJson.totalNumber);
                                      }
                                      productArray.push(orderListInfo[key].productId);
                                 });
                                 if(productArray.indexOf(cartItemJson.productId) == -1){
                                      orderListInfo.push(cartItemJson);
                                 }
                              }else{
                                  orderListInfo.push(cartItemJson);
                              }
                              if($(".total_number").val() > 0) {
                                 sessionStorage.setItem("inCartAllProduct2",JSON.stringify(orderListInfo));
                                 location.href="../order/sureOrder.html";
                              }else{
                                 $.toast("加入购物车的商品数量有误!");
                              }
                           }
                      }else{
                        $.toast('请输入正确的信息')
                      }
                }else{
                  $.toast("库存不足，快去补货吧！")
                }
             });

            //确认加入购物篮
            $(document).on("click", "#addToCart", function () {
                var productNumber = $(".qty").val();
                if(isNaN(parseInt(productNumber))){
                    productNumber = 0;
                }
                if(inventoryNumber >= parseInt(productNumber)){
                  if(shipmentValidate()){
                       if(isNaN($('.total_money').val()) || isNaN($('.product_price').val())){
                          common.alertTips("输入错误,请重新输入")
                       }else{
                          var cartItemJson = {
                             "employeeId": userInfo.employeeId,
                             "userName": userInfo.userName,
                             "visitorId": visitorId ? visitorId :'',
                             "customerId": customerId ? customerId: 0,
                             "customerName": $(".client_name").val()? $(".client_name").val():'',
                             "invoiceTitle": custInfo.invoiceTitle,
                             "contactName":custInfo.contact_name,
                             "contactPhone": custInfo.contact_phone,
                             "address": custInfo.address != null ? custInfo.address : '',
                             "productCode": product_code,
                             "productName": productInfo.product_name,
                             "unitPrice": $(".product_price").val(),
                             "totalNumber": parseInt($(".total_number").val()),
                             "imageUrl": productInfo.pic_src,
                             "unit": productInfo.unit,
                             "shopId": userInfo.shopID,
                             "shopName": userInfo.shopName,
                             "productId":product_id
                          };
                             if ($(".total_number").val() > 0) {
                                 var addToCartEvent = {
                                     method: NATIVE_METHOD_CART_ADD_TO,
                                     data: JSON.stringify(cartItemJson)
                                 };
                                 console.log("传入的购物车的信息="+JSON.stringify(cartItemJson))
                                 common.jsCallNativeEventHandler(JS_CALL_NATIVE_EVENT_EXECUTE_NATIVE_METHOD, addToCartEvent, function (data) {
                                     common.log("保存购物车完成。");
                                 });
                                 var userData = {
                                     method: NATIVE_PAGE_DASHBOARD_CART,
                                     data: ""
                                 };
                                 common.jsCallNativeEventHandler(JS_CALL_NATIVE_EVENT_REQUEST_SWITCH_PAGE, userData, function (data) {
                                     common.log("跳转到主页面完成！");
                                 });
                             } else {
                                 $.toast("加入购物车的商品数量有误!");
                             }
                       }
                  }else{
                    $.toast('请输入正确的信息')
                  }t
                }else{
                  $.toast("库存不足，快去补货吧！")
                }
            });
            var cusCounter = 1;
            //打开选择客户的选择页面
            $(document).on('click', '.open-customer', function () {
                cusCounter++;
                common.log("获取客户列表begin:");
                $.popup('.popup-customer');

                var getCustomerList = {
                    method: NATIVE_METHOD_CUSTOMER_GET_ALL,
                    data: ""
                };
                common.jsCallNativeEventHandler(JS_CALL_NATIVE_EVENT_EXECUTE_NATIVE_METHOD, getCustomerList, function (responseData) {
                    if (responseData.ack == 1 && common.isNotnull(responseData.response)) {
                        var customersGroupArray = JSON.parse(responseData.response);
                        common.log("底层获取到的客户的清单="+responseData.response)
                        var templateText ="";
                        $.each(customersGroupArray, function (key, customerList) {
                            templateText += '<li class="list-group-title">' + key + '</li>';
                            if (customerList && customerList.length > 0) {
                                $.each(customerList, function (k, customer) {
                                    templateText += '<li class="shiment-cust" data-name="' + customer.contact_name + '" ' +
                                        'data-cid="' + customer.customer_id + '"  data-phone="' + customer.contact_phone + '" data-address="' + customer.address + '">' +
                                        '<div class="item-content"><div>' +
                                        '<input type="hidden" class="invoiceTitle" value="'+customer.invoice_title+'"/>' +
                                        '<input  type="hidden" class="memo" value="'+customer.memo+'"/><div class="customer-name">' + customer.customer_name + '</div>' +
                                        '<div class="customer-concatInfo">' + customer.contact_name + "," + customer.contact_phone + '</div></div></div></li>'
                                })
                            } else {
                                templateText = "";
                            }

                        })
                        $("#box").html(templateText);
                    } else {
                        $.toast("获取客户列表失败", 2000, 'error');
                    }
                });
            });

            /* show  customer sale information*/
            function showCustomerSale(customerId,product_id){
                $(".shopInfo").css("display", "block");
                custModule.queryCustomerRecord(customerId,product_id,function(responseData) {
                    var httpResponse = JSON.parse(responseData.response);
                    if (responseData.ack == 1 && httpResponse.retcode ==200)  {
                        var retData = httpResponse.retbody;
                        $("#sale_dt").html(common.isNotnull(retData.sale_dt) ? retData.sale_dt : '-');
                        $("#last_sale_price").html(common.isNotnull(retData.sale_price) ?'￥'+ retData.sale_price : '-');
                        $("#last_sale_num").html(common.isNotnull(retData.sale_num) ? retData.sale_num + retData.unit: '-');
                        if(retData.sale_price){
                            $(".product_price").val(retData.sale_price);
                        }
                    } else {
                        $.toast("本地网络连接有误", 2000, 'error');
                    }
                });
            }

            $(document).on("click", ".customer_list .shiment-cust", function (event) {
                $(".radio-icon").addClass("icon-form-checkbox").removeClass("icon-check-checked");
                var chname = $(this).find(".customer-name").text();
                common.log("选择的客户姓名是：" + chname);
                $(".sm-prodctInfo").css("border-top", ".3rem solid #EFEFF4");
                var cid = $(this).data("cid");
                customerId = cid;
                custInfo = {
                    contact_name: $(this).data("name"),
                    contact_phone: $(this).data("phone"),
                    customer_id: cid,
                    address: $(this).data("address"),
                    invoiceTitle: $(this).find(".invoiceTitle").val(),
                    customerNotes: $(this).find(".memo").val()
                }
                common.log("dd" + JSON.stringify(custInfo));

                $(".client_name").val(chname);
                $(".sm-cusName").html(chname);
                $.closeModal(".popup-customer");
                //获取选择用户的采购信息
                showCustomerSale(customerId,product_id);

                event.stopPropagation();
                event.preventDefault();
            });

            var urlCustomerIdIno = sessionStorage.getItem("urlCustomerId");
            if(urlCustomerIdIno){
                $(document).off('click','.open-customer');
            }

            //取消客户选择，关闭Popup
            $(document).on('click','.closePopup',function(){
                $.closeModal(".popup-customer");
            })

            //首次进入默认计算总额
            var qty = $(".qty").val(), price = $(".product_price").val();
            $(".total_money").val((price * qty).toFixed(2));

            //根据用户输入的饿单价和数量计算总额
            function changeTotal() {
                var price = parseFloat($(".product_price").val());
                var qty = parseFloat($(".qty").val());
                if (isNaN(qty) || isNaN(price)) {
                    $(".total_money").val('');
                } else {
                    if(isNaN($(".total_money").val())){
                      $(".total_money").val('');
                    }else{
                      $(".total_money").val((parseFloat(price * qty)).toFixed(2));
                    }
                }
            };

             /*total money and product price calculation*/
               $(document).on("input",".qty,.product_price",function(){
                    changeTotal();
               });

            //总价联动
            $(document).on("input", ".total_money", function () {
                var totalMoney = parseFloat($(".total_money").val()) / parseInt($(".qty").val());
                if(isNaN(totalMoney)){
                    $(".product_price").val('')
                }else{
                    $(".product_price").val(totalMoney);
                }
            });

            $(document).on("click", ".shopInfo .sp_total", function () {
                $(".sm-shopCon").toggle();
                if ($(this).find(".item-after .icon").hasClass("icon-down")) {
                    $(this).find(".item-after .icon").removeClass("icon-down").addClass("icon-up")
                } else {
                    $(this).find(".item-after .icon").removeClass("icon-up").addClass("icon-down")
                }
            })
            $(document).on("click", ".sm-prodctInfo .sp_total", function () {
                $(".sm-productCon").toggle();
                if ($(this).find(".item-after .icon").hasClass("icon-down")) {
                    $(this).find(".item-after .icon").removeClass("icon-down").addClass("icon-up")
                } else {
                    $(this).find(".item-after .icon").removeClass("icon-up").addClass("icon-down")
                }
            });

            $(document).on("click", ".label-checkbox", function (event) {
                common.log("选择new customer");
                if (document.getElementById("newCust-radio").checked) {
                    $("#newCust-radio").removeAttr("checked");
                    $(".radio-icon").addClass("icon-form-checkbox").removeClass("icon-check-checked");
                } else {
                    $("#newCust-radio").attr("checked", "true");
                    $(".radio-icon").removeClass("icon-form-checkbox").addClass("icon-check-checked");
                    $.closeModal(".popup-customer");
                }
                $(".client_name").val("散客/新客户");
                $(".shopInfo").css("display", "none");
                event.stopPropagation();
                event.preventDefault();
            });

           $(document).on('input',".item-input input",function(){
                shipmentValidate()
           })

          //表单输入验证
            function shipmentValidate(){

                var valiteFlag = true;

                if (!common.regMoney($('.total_money').val())) {
                    $('.total_money').parent(".item-input").addClass("error");
                    valiteFlag = false;
                } else {
                    $('.total_money').parent(".item-input").removeClass("error");
                }

                if (!common.regNumber($('.total_number').val())) {
                    $('.total_number').parent(".item-input").addClass("error");
                    valiteFlag = false;
                } else {
                    $('.total_number').parent(".item-input").removeClass("error");
                }

                if (!common.regMoney($('.product_price').val())) {
                    $('.product_price').parent(".item-input").addClass("error");
                    valiteFlag = false;
                } else {
                    $('.product_price').parent(".item-input").removeClass("error");
                }

                if (valiteFlag) {
                    return true;
                } else {
                    return false;
                }

            }

          $(document).on("click",".qualityMinus",function(){
              var originalNum =  parseInt($(".qty").val());
              if(originalNum >= 1){
                originalNum--;
              }
              $(".qty").val(originalNum);
              changeTotal()
          });

          $(document).on("click",".qualityAdd",function(){
                var originalNum =  parseInt($(".qty").val());
                originalNum++;
                $(".qty").val(originalNum);
                changeTotal()
          });


        });
        //回退到主页面
        $(document).on("click", ".backHomeBtn", function () {

          if(common.getQueryString('from') == "h5"){
                window.history.go(-1);
          }
          else{
             var userData = {
                 method: NATIVE_PAGE_DASHBOARD_HOME,
                 data: ""
             };
             common.jsCallNativeEventHandler(JS_CALL_NATIVE_EVENT_REQUEST_SWITCH_PAGE, userData, function (data) {
                 common.log("回退到主页面！");
             });
          }

        });

        $(document).on("pageInit", "#transitionPage", function () {
            common.log("transitionPage init");
            $(document).on("click", ".sortIcon", function () {
                common.log("xxx");
                $(".transaction_menu").toggle();
            })
        });




        $.init();
    })
})()