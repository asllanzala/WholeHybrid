   /**
 * Created by caihong on 16/7/15.
 */
(function () {
    require(['zepto', 'common', 'salesModule', 'template', 'customerModule'], function ($, common, salesModule, template, custModule) {
        var  switchKey = 0,
             switchKeys = 0,
             validateProductInfo ="",
             price_special =false,
             add_product_sku_list =[],
             isEdit =false;
        var  getShopId = common.getQueryString("shopId");
        if(!getShopId){
           getShopId = sessionStorage.getItem("shopId");
           console.log("店铺id="+getShopId);
        }

        /* add product page */
        function addProduct(){
            var userInfo = {};
            var imgUrlArray = [],
                deleteImageUrl = '',
                deleteIndex = '',
                addImage = '',
                productId ='',
                saveType ='',
                categoryArray= [],
                inventory_list_edit ={},
                inventory_list =[];


             // 如果是更新商品页面的话,设置默认值
            var flag = common.getQueryString("flag"),
                product_code = sessionStorage.getItem("mData");
//                product_number = sessionStorage.getItem("productNumber");

            if(product_code){
                $(".pcode").val(product_code);
            }
            console.log("编辑商品1")
//
//            if(product_number){
//                $(".pNumber").val(product_number);
//            }
            $('#productCategory').css('color', '#ccc');
            common.connectJsTunnel(function (jsTunnel) {
                jsTunnel.init();
                // 这里注册一个处理器来监听图片上传完的回传数据。
                jsTunnel.registerHandler(
                    NATIVE_CALL_JS_EVENT_PRODUCT_IMAGE_UPLOAD_RESULT,
                    function (data, responseCallback) {
                        common.log("图片上传成功：" + JSON.stringify(data));
                        var imgUrl = data.imgURL;
                        common.log("imgUrl=" + imgUrl);
                        var imgLocalPath = data.imgLocalPath;
                        common.log("imgLocalPath=" + imgLocalPath);

                        imgUrlArray.push(imgUrl);
                        showImage(imgUrlArray.length - 1, imgUrl);
                    }
                );
                /* warehouse list  */
                if(!sessionStorage.getItem("scanProduct")){
                    common.pullDown_warehouse.sentRequest(getShopId,getProductNumber);
                    common.pullDown_warehouse.closeEvent();
                    common.pullDown_warehouse.openModal();
                }else{
                    getProductCategory();
                }
            });
            //类别选择 动态更改样式
            $('#productCategory').change(function(){
                var categoryId = $('#productCategory').val();
                categoryArray[0] = parseInt($('#productCategory').val()) ;
                if(categoryId != -1){
                    $('#productCategory').css('color', '#000');
                }else {
                    $('#productCategory').css('color', '#ccc');
                }
            });

            function getProductCategory(){
                salesModule.queryCategory(userInfo.employeeId, function (data) {
                    if (data.ack == 1) {
                        var categoryApiResponse = JSON.parse(data.response);
                        var categoryList = categoryApiResponse.retbody;
                        var categoryTemp = "";
                        for (var key in categoryList) {
                            var category = categoryList[key];
                            categoryTemp += "<option value=" + category.category_id + ">" + category.category_name + "</option>";
                        }

                        $("#productCategory").append(categoryTemp);
                        common.getProductTag(initData);
                    } else {
                         $.alert("网络异常");
                    }
                });
            }

            function getProductNumber(){
                /* get auto setting product_number */
                if(!sessionStorage.getItem("scanProduct")){
                    var numberData = {
                        shop_id:getShopId,
                        size:1
                    }
                    salesModule.getAutoSettingProductNumber(numberData,function(data){
                        var respondData =data.response;
                        respondData =JSON.parse(respondData);
                        if(respondData.retcode ==200){
                             $(".pNumber").val(respondData.retbody.product_number_list.length >0 ? respondData.retbody.product_number_list[0]:"")
                        }else if(respondData.retcode == 4047){
                            $.toast("已达到货号上限，请重新设置",1000);
                        }else if(respondData.retcode == 4049 || respondData.retcode == 4046){

                        }else {
                            $.toast('网络异常',1000);
                        }
                        getProductCategory()
                    });
                }
            }


            function showImage(arrayIndex, imageUrl){
                //获取imglist 元素里包含的 pro-img 长度
                var imageCounter = $(".imgList .pro-img").length;
                if (imageCounter < 3) {
                    common.log("//动态创建添加的图片元素");
                    //动态创建添加的图片元素
                    imageCounter++;
                    var proImgHtml = $("#addProductImg").html()
                                    .replace("{{imgUrl}}",imageUrl)
                                    .replace("{{arrayIndex}}",arrayIndex)
                                    .replace("{{img}}",imageUrl)

                    $(".imgList").css("display", "inline-block").append(proImgHtml);

                    common.log(proImgHtml);
                    if (imageCounter >= 3) {
                        imageCounter = 0;
                        $("#uploadImageBtn1").hide();
                    }
                }
            }

            function initData(){
                if (flag && flag == 1) {
                    var productInfo = sessionStorage.getItem("scanProduct");
                    common.log('获取的缓存的信息='+productInfo );
                    $(".title").html("编辑商品");

                    if(productInfo){
                        price_special = true;
                        isEdit =true;
                        productInfo = JSON.parse(productInfo);
                        if(productInfo.inventory_list[0].sku_value_id1){
                            $(".standard_price").val(productInfo.standard_price_range ? productInfo.standard_price_range :"").attr("disabled","disabled").css({"color":"#ccc","backgroundColor":"#fff","borderColor":"#ccc"});
                            $(".stock_price").val(productInfo.stock_price_range ? productInfo.stock_price_range :"").attr("disabled","disabled").css({"color":"#ccc","backgroundColor":"#fff","borderColor":"#ccc"});
                            if(checkedProductNumber(productInfo.inventory_list)){
                                    $(".pNumber").attr("disabled","disabled").css({"color":"#ccc","backgroundColor":"#fff","borderColor":"#ccc"});
                            }
                          /* init sku param */
                          for(var j =0;j< productInfo.sku_list.length;j++){
                                if(productInfo.sku_list[j].sku_key_name == $(".color_name").text()){
                                    var color_value = "";
                                    for(var k =0;k < productInfo.sku_list[j].sku_value_list.length;k++){
                                          if(k< productInfo.sku_list[j].sku_value_list.length-1){
                                               color_value +=  productInfo.sku_list[j].sku_value_list[k].sku_value_name +",";
                                          }else{
                                               color_value +=  productInfo.sku_list[j].sku_value_list[k].sku_value_name;
                                          }
                                    }
                                    $(".color_name").siblings(".value_icon").text(color_value);
                                }else if(productInfo.sku_list[j].sku_key_name == $(".size_name").text()){
                                    var size_value = "";
                                    for(var k =0;k< productInfo.sku_list[j].sku_value_list.length;k++){
                                          if(k< productInfo.sku_list[j].sku_value_list.length-1){
                                               size_value +=  productInfo.sku_list[j].sku_value_list[k].sku_value_name +",";
                                          }else{
                                               size_value +=  productInfo.sku_list[j].sku_value_list[k].sku_value_name;
                                          }
                                    }
                                    $(".size_name").siblings(".value_icon").text(size_value);
                                }else if(productInfo.sku_list[j].sku_key_name == $(".standard_name").text()){
                                   var standard_value ="";
                                   for(var k =0;k< productInfo.sku_list[j].sku_value_list.length;k++){
                                         if(k< productInfo.sku_list[j].sku_value_list.length-1){
                                              standard_value +=  productInfo.sku_list[j].sku_value_list[k].sku_value_name +",";
                                         }else{
                                              standard_value +=  productInfo.sku_list[j].sku_value_list[k].sku_value_name;
                                         }
                                   }
                                    $(".standard_name").siblings(".value_icon").text(standard_value);
                                }
                          }
                           /* save product detail */
                          sessionStorage.setItem("choiceSkuParam",JSON.stringify(productInfo.sku_list));
                          sessionStorage.setItem("modified_inventory_list",JSON.stringify(productInfo.inventory_list));
                        }else{
                            $(".standard_price").val(productInfo.inventory_list[0].standard_price ? productInfo.inventory_list[0].standard_price :"");
                            $(".stock_price").val(productInfo.inventory_list[0].stock_price ? productInfo.inventory_list[0].stock_price :0).attr("disabled","disabled").css({"color":"#ccc","backgroundColor":"#fff","borderColor":"#ccc"})
                        }
                        $(".unit").val(productInfo.unit_name ? productInfo.unit_name:"");
                        $(".product_name").val(productInfo.product_name);
                        $(".originalStoke").val(productInfo.total_quantity ? productInfo.total_quantity : 0).attr("disabled","disabled").css({"color":"#ccc","backgroundColor":"#fff","borderColor":"#ccc"})
                        $(".describe").val(productInfo.describe);
                        $("#productCategory").val((productInfo.categorys).length >0 ? (productInfo.categorys)[0].category_id:-1);
                        $(".pNumber").val(productInfo.product_number);
                        $(".pcode").val(productInfo.product_code);
                        $(".pull_down_title_warehouse").attr("data-warehouseid", productInfo.warehouse_id);
                        $(".title_info_warehouse").text(productInfo.warehouse_name).css("color","#ccc");

                        if((productInfo.categorys).length >0){
                            categoryArray.push((productInfo.categorys)[0].category_id);
                        }

                        productId = productInfo.inventory_list[0].product_id;

                        $("#finishAddProduct").text("完成");
                        $("#saveInfo").hide();

                        validateProductInfo = function(){
                              var isProductName = true,isProductSaleType=true;
                                if($(".product_name").val() =="") {
                                    isProductName = false;
                                }
                                if($(".title_info_saleType").text()  == "" ){
                                      $(".title_info_saleType").parents(".login_input").css("borderColor","red");
                                      isProductSaleType =false
                                }else{
                                      $(".title_info_saleType").parents(".login_input").css("borderColor","#ccc");
                                }
                              return isProductName && isProductSaleType ;
                        }

                        // 初始化图片
                        imgUrlArray = productInfo.pic_src;
                        originalImg = productInfo.pic_src;
                        if(imgUrlArray.length >0){
                            for (var i = 0; i< imgUrlArray.length;i++) {
                                var oneImageUrl = imgUrlArray[i];
                                showImage(i, oneImageUrl);
                            }
                        }

                        if(productInfo.tag_list && (productInfo.tag_list).length > 0 ){
                             $.each(productInfo.tag_list,function(key,value){
                                    if(value.tag_key_name == "销售类型"){
                                         $(".title_info_saleType").text(value.tag_value_list[0].tag_value_name);
                                         $(".pull_down_title_saleType").attr("data-warehouseid",value.tag_value_list[0].tag_value_id)
                                    }
                                    for(var i=0;i<$(".pull_down_title_department").length;i++){
                                        if(value.tag_key_name == $(".title_name_department").eq(i).text()){
                                            $(".title_info_department").eq(i).text(value.tag_value_list[0].tag_value_name);
                                            $(".pull_down_title_department").eq(i).attr("data-warehouseid",value.tag_value_list[0].tag_value_id)
                                        }
                                    }
                             })
                        }
                    }
                }
                // 设置商品列表选择器的颜色
                var categoryId = $('#productCategory').val();
                if (common.isNotnull(categoryId) && categoryId != -1) {
                    $('#productCategory').css('color', '#000');
                } else {
                    $('#productCategory').css('color', '#ccc');
                }
                 //关闭加载动画
                $('.indicator-background,.indicator-div').remove();
            }

            //上传图片
            var openView =true;
            $(document).on("click", ".uploadImageBtn", function (){
                    if(openView){
                            openView =false;
                            var userData = {
                                method: NATIVE_METHOD_GET_USER_INFO,
                                data: ""
                            };
                            common.jsCallNativeEventHandler(JS_CALL_NATIVE_EVENT_EXECUTE_NATIVE_METHOD,userData, function (data) {
                                common.log("用户信息：" + JSON.stringify(data));
                                userInfo = JSON.parse(data.response);
                                var currentShopId = parseInt(userInfo.shopID);

                                if (common.isNull(product_code)) {
                                    product_code = "unknow";
                                };

                                var productEvent = {
                                    productID:product_code,
                                    shopID: currentShopId
                                };

                                var imageUploadPageEvent = {
                                    method: NATIVE_PAGE_IMAGE_UPLOAD,
                                    data: JSON.stringify(productEvent)
                                };

                                common.jsCallNativeEventHandler(JS_CALL_NATIVE_EVENT_REQUEST_SWITCH_PAGE, imageUploadPageEvent, function() {

                                });
                                setTimeout(function(){
                                    openView = true;
                                },2000);
                            });
                    }
                });

            //删除图片
            $(document).on("click", ".deleteImg", function () {
                var thisImg = $(this).data("img");
                $.each(imgUrlArray,function(key,data){
                    if(thisImg ==imgUrlArray[key]){
                        imgUrlArray.splice(key,1);
                    }
                });
                $("#uploadImageBtn1").show().css("display", "inline-block;");
                $(this).parent(".deleteImgCon").remove();
                if($(".deleteImgCon").length <1 ){
                    $(".imgList").hide();
                }
            });

            //单价验证
            $(document).on("input",".standard_price,.stock_price,.originalStoke",function(){
                common.inputMoney($(this));
            });

            //添加商品
            ~function(){
                var finishSentTime = new Array();
                $(document).on("click", "#finishAddProduct", function () {
                      saveType =2;
                      finishSentTime.push(new Date());
                      if (finishSentTime.length > 1 && (finishSentTime[finishSentTime.length - 1].getTime() - finishSentTime[finishSentTime.length - 2].getTime() < 1000))
                      {return; } else {saveInfo();}
                });
    
                 var saveSentTime = new Array();
                 $(document).on("click", "#saveInfo", function () {
                        saveType =1;
                        saveSentTime.push(new Date());
                        if (saveSentTime.length > 1 && (saveSentTime[saveSentTime.length - 1].getTime() - saveSentTime[saveSentTime.length - 2].getTime() < 1000))
                        {return; } else {saveInfo();}
                 });
            }();

            function saveInfo(){
              if(validateColor()){
                if (validateProductInfo()) {
                    isSave =true;
                    if(categoryArray[0] == -1){
                        categoryArray =[]
                    }

                    var inventory_list = sessionStorage.getItem("modified_inventory_list");
                    if(inventory_list){
                        inventory_list =JSON.parse(inventory_list);
                    }else{
                        if(price_special){
                            var productInfo = sessionStorage.getItem("scanProduct");
                                productInfo = JSON.parse(productInfo);
                            var  inventory_list_edit ={
                                inventory_id:productInfo.inventory_list[0].inventory_id,
                                product_code: $(".pcode").val(),
                                product_number: $(".pNumber").val(),
                                standard_price: $(".standard_price").val(),
                                pic_src_list:productInfo.inventory_list[0].pic_src_list
                            }
                           var  inventory_list = [];
                           inventory_list.push(inventory_list_edit);
                        }else{
                           inventory_list = []
                        }
                    }

                    for(var t=0;t<$(".hasChoice1").length;t++){
                        var add_product_sku_list_value_id = $(".hasChoice1").eq(t).data("sku_value_id");
                        add_product_sku_list.push(add_product_sku_list_value_id);
                    }

                    var  tag_value_list = [];
//                    if(parseInt($(".pull_down_title_department").data("warehouseid")) && parseInt($(".pull_down_title_saleType").data("warehouseid")) ){
//                        tag_value_list = [ parseInt($(".pull_down_title_saleType").data("warehouseid")),parseInt($(".pull_down_title_department").data("warehouseid"))]
//                    }else if( parseInt($(".pull_down_title_saleType").data("warehouseid")) && !(parseInt($(".pull_down_title_department").data("warehouseid")))){
//                        tag_value_list = [ parseInt($(".pull_down_title_saleType").data("warehouseid")) ];
//                    }else if( parseInt($(".pull_down_title_department").data("warehouseid")) && !(parseInt($(".pull_down_title_saleType").data("warehouseid")))){
//                        tag_value_list = [ parseInt($(".pull_down_title_department").data("warehouseid")) ];
//                    }
                   ~function(){
                        tag_value_list.push(parseInt($(".pull_down_title_saleType").data("warehouseid")));
                        for(var i=0;i< $(".pull_down_title_department").length;i++){
                             if(parseInt($(".pull_down_title_department").eq(i).data("warehouseid"))){
                                 tag_value_list.push(parseInt($(".pull_down_title_department").eq(i).data("warehouseid")))
                             }
                        }
                   }();
                    var product = {
                        product_name: $(".product_name").val(),
                        standard_price: price_special?"":$(".standard_price").val(),
                        unit: $(".unit").val(),
                        category_ids:categoryArray,
                        quantity:$(".originalStoke").val(),
                        product_code:$(".pcode").val(),
                        product_number:$(".pNumber").val(),
                        stock_price: price_special ? "":$(".stock_price").val(),
                        describe: $(".describe").val(),
                        pic_src_list: imgUrlArray,
                        sku_value_list:add_product_sku_list,
                        inventory_list:inventory_list,
                        warehouse_id:parseInt($(".pull_down_title_warehouse").data("warehouseid")),
                        tag_value_list:tag_value_list
                    }
                    common.log("即将添加的商品：product = " + JSON.stringify(product));

                    var imageUploadPageEvent = {
                        method: NATIVE_PAGE_DASHBOARD_HOME,
                        data: product
                    };
                    common.log("获取添加或者编辑商品的标识flag=" + flag);
                    if (flag && flag == 1) {
                        product.product_id = productId;
                        salesModule.updateProduct(userInfo.shopID, product, function (data) {
                            var httpResponse = JSON.parse(data.response);
                            if (data.ack == 1) {
                                sessionStorage.setItem("changedProductNumber",$(".pNumber").val());
                                sessionStorage.setItem("changedProductCode",$(".pcode").val());
                                $.toast("商品编辑成功");
                                setTimeout(function(){
                                     window.history.go(-1);
                                },1300)
                            }else if(httpResponse.retcode ==180000013 ){
                                $.toast("货号或条码已存在，请更换");
                                 $(".pNumber").parent(".login_input").addClass("errorForm");
                                 $(".pcode").parent(".login_input").addClass("errorForm");
                            } else if(httpResponse.retcode ==4056){
                                $.toast("标签已被修改或删除");
                            }else {
                                $.toast("网络异常");
                            }
                        })
                     }else {
                        if(checkedProductNumber(product.inventory_list)){
                            product.product_number ="";
                            $(".pNumber").val("").attr("disabled","disabled").css({"color":"#ccc","backgroundColor":"#fff","borderColor":"#ccc"});
                        }
                        salesModule.addProduct(userInfo.shopID, product, function (data) {
                            var httpResponse = JSON.parse(data.response);
                            if (data.ack == 1) {
                                  if(saveType ==1){
                                        $.toast("商品添加成功");
                                         setTimeout(function(){
                                             var imageUploadPageEvent = {
                                                 method: NATIVE_PAGE_DASHBOARD_HOME,
                                                 data: ""
                                             };
                                             common.jsCallNativeEventHandler(JS_CALL_NATIVE_EVENT_REQUEST_SWITCH_PAGE, imageUploadPageEvent, function (data) {
                                                 common.log("打开主页面完成！");
                                             });
                                         },1300);
                                  }else if(saveType ==2){
                                       sessionStorage.removeItem("mData");
                                       sessionStorage.removeItem("choiceSkuParam");
                                       sessionStorage.removeItem("modified_inventory_list");
                                       location.reload();
                                  }
                            }else if( httpResponse.retcode ==180000013){
                                    $.toast("货号或条码已存在，请更换");
                                    $(".pNumber").parent(".login_input").addClass("errorForm");
                                    $(".pcode").parent(".login_input").addClass("errorForm");
                            }else if(httpResponse.retcode ==4056){
                                 $.toast("标签已被修改或删除");
                            } else {
                                $.toast("网络异常");
                            }
                        })
                    }
                } else {
                    $.toast('请输入正确的必填信息',1300,'toastInConfirm')
                }
               }else{
                    $.toast('请输入正确的信息',1300,'toastInConfirm');
               }
            };

            //退回上一界面
            $(document).on("click",".backPage",function(){
                backToNativeOrH5()
            });

            function backToNativeOrH5(){
                  if(flag && flag == 1){
                     window.history.go(-1);
                     sessionStorage.removeItem("choiceSkuParam");
                     sessionStorage.removeItem("inventory_list");
                  }else{
                     common.backHomePage();
                  }
            }

            validateProductInfo = function (){
                  var isProductName = true,isStandardPrice = true,isStockPrice =true,isProductSaleType=true;
                  if ($(".product_name").val()  =="") {
                      isProductName = false;
                  }

                  if (!common.regMoney($(".standard_price").val())) {
                      isStandardPrice = false;
                  }

                  if (!common.regMoney($(".stock_price").val())) {
                      isStockPrice = false;
                  }

                  console.log("类型的值="+$(".title_info_saleType").text());
                  if($(".title_info_saleType").text()  == "" ){
                      $(".title_info_saleType").parents(".login_input").css("borderColor","red");
                      isProductSaleType = false
                  }else{
                      $(".title_info_saleType").parents(".login_input").css("borderColor","#ccc");
                  }
                  return isProductName && isStandardPrice && isStockPrice && isProductSaleType
            }

            function validateColor(){
                var validateColor =true,
                    product_name = $(".product_name").parent().css("borderColor") != "red",
                    standard_price = $(".standard_price").parent().css("borderColor") != "red",
                    stock_price = $(".stock_price").parent().css("borderColor") != "red",
                    unit = $(".unit").parent().css("borderColor") != "red",
                    productCategory = $(".border_bottom").parents("#categoryDiv").css("borderColor") != "red",
                    pcode = $(".pcode").parent().css("borderColor") != "red",
                    pNumber = $(".pNumber").parent().css("borderColor") != "red";
//                    describe = $(".describe").parent().css("borderColor") != "red";
                    originalStoke = $(".originalStoke").parent().css("borderColor") !="red";
                if( product_name && standard_price && stock_price && unit && productCategory && pcode && pNumber &&  originalStoke ){
                    return  validateColor;
                }else{
                    return  !validateColor;
                }
            }

            /* blur validate  */
            $(document).on('blur','.product_name,.standard_price,.unit,#productCategory,.pcode,.pNumber,.describe,.stock_price,.originalStoke',function(){
                   if($(this)[0] == document.querySelector('.product_name')){
                       if (!($(this).val())) {
                           $(this).parent().css("borderColor","red");
                           $.toast('商品名不能为空',1300,'toastInfo')
                       } else {
                           $(this).parent().css("borderColor","#ccc");
                       }
                   }else if($(this)[0] == document.querySelector('.standard_price')){
                       if (common.regMoney($(this).val())) {
                            if($(this).val().indexOf(".") < 0 && ($(this).val())[0] == 0){
                                 $(this).parent().css("borderColor","red");
                                 $.toast("单价最多2位小数",1300,'toastInfo')
                            }else{
                                $(this).parent().css("borderColor","#ccc");
                            }
                       } else {
                           $(this).parent().css("borderColor","red");
                           $.toast("单价最多2位小数",1300,'toastInfo')
                       }
                   }else if($(this)[0] == document.querySelector('.stock_price')){
                       if (common.regMoney($(this).val())) {
                            if($(this).val().indexOf(".") < 0 && ($(this).val())[0] == 0){
                                $(this).parent().css("borderColor","red");
                                $.toast("进价最多2位小数",1300,'toastInfo')
                            }else{
                                $(this).parent().css("borderColor","#ccc");
                            }
                       } else {
                            $(this).parent().css("borderColor","red");
                            $.toast("进价最多2位小数",1300,'toastInfo')
                       }
                   }else if($(this)[0] == document.querySelector('.unit')){
                       if($(this).val() !=""){
                           if (!common.regInvoice($(this).val())) {
                              $(this).parent().css("borderColor","red");
                               $.toast("单位请输入中英文",1300,'toastInfo')
                           } else {
                              $(this).parent().css("borderColor","#ccc");
                           }
                       }else{
                            $(this).parent().css("borderColor","#ccc");
                       }

                   }else if($(this)[0] == document.querySelector('#productCategory')){
                       if($(this).val() !=""){
                           if (!common.regAddress($(this).val())) {
                              $(this).parents("#categoryDiv").css("borderColor","red");
                           } else {
                              $(this).parent("#categoryDiv").css("borderColor","#ccc");
                           }
                       }else{
                           $(this).parents("#categoryDiv").css("borderColor","#ccc");
                       }
                   }
                   else if($(this)[0] == document.querySelector('.pcode')){
                       if($(this).val() !=""){
                           if (!common.regPassword($(this).val())) {
                              $(this).parent().css("borderColor","red");
                              $.toast("条形码请输入数字,字母",1300,'toastInfo')
                           } else {
                              $(this).parent().css("borderColor","#ccc");
                           }
                       }else{
                            $(this).parent().css("borderColor","#ccc");
                       }
                   }
                   else if($(this)[0] == document.querySelector('.pNumber')){
                      if($(this).val() !=""){
                          if (!common.regProductNumber($(this).val())) {
                             $(this).parent().css("borderColor","red");
                             $.toast("货号请输入数字,字母和- . /",1300,'toastInfo')
                          } else {
                             $(this).parent().css("borderColor","#ccc");
                          }
                      }else{
                           $(this).parent().css("borderColor","#ccc");
                      }
                   }
                   else if($(this)[0] == document.querySelector('.describe')){
//                        if($(this).val() !=""){
//                           if (!common.regDescribe($(this).val())) {
//                              $(this).parent().css("borderColor","red");
//                              $.toast("描述请输入中英文和标点符号",1300,'toastInfo')
//                           } else {
//                              $(this).parent().css("borderColor","#ccc");
//                           }
//                        }else{
//                            $(this).parent().css("borderColor","#ccc");
//                        }
                   }else if($(this)[0] == document.querySelector('.originalStoke')){
                        if($(this).val() !=""){
                           if (!common.regMoney($(this).val())) {
                              $(this).parent().css("borderColor","red");
                              $.toast("库存请输入不超过二位正小数",1300,'toastInfo')
                           } else {
                              $(this).parent().css("borderColor","#ccc");
                           }
                        }else{
                            $(this).parent().css("borderColor","#ccc");
                        }
                   }
            });

            $(document).on('input','.product_name,.standard_price,.unit,#productCategory,.pcode,.pNumber,.describe,.stock_price,.originalStoke',function(){
                   if($(this)[0] == document.querySelector('.product_name')){
                       if (!($(this).val())) {
                           $(this).parent().css("borderColor","red");
                       } else {
                           $(this).parent().css("borderColor","#ccc");
                       }
                   }else if($(this)[0] == document.querySelector('.standard_price')){
                       if (common.regMoney($(this).val())) {
                            if($(this).val().indexOf(".") < 0 && ($(this).val())[0] == 0){
                                 $(this).parent().css("borderColor","red");
                            }else{
                                $(this).parent().css("borderColor","#ccc");
                            }
                       } else {
                           $(this).parent().css("borderColor","red");
                       }
                   }else if($(this)[0] == document.querySelector('.stock_price')){
                       if (common.regMoney($(this).val())) {
                            if($(this).val().indexOf(".") < 0 && ($(this).val())[0] == 0){
                                $(this).parent().css("borderColor","red");
                            }else{
                                $(this).parent().css("borderColor","#ccc");
                            }
                       } else {
                            $(this).parent().css("borderColor","red");
                       }
                   }else if($(this)[0] == document.querySelector('.unit')){
                       if($(this).val() !=""){
                           if (!common.regInvoice($(this).val())) {
                              $(this).parent().css("borderColor","red");
                           } else {
                              $(this).parent().css("borderColor","#ccc");
                           }
                       }else{
                            $(this).parent().css("borderColor","#ccc");
                       }

                   }else if($(this)[0] == document.querySelector('#productCategory')){
                       if($(this).val() !=""){
                           if (!common.regAddress($(this).val())) {
                              $(this).parents("#categoryDiv").css("borderColor","red");
                           } else {
                              $(this).parent("#categoryDiv").css("borderColor","#ccc");
                           }
                       }else{
                           $(this).parents("#categoryDiv").css("borderColor","#ccc");
                       }
                   }
                   else if($(this)[0] == document.querySelector('.pcode')){
                       if($(this).val() !=""){
                           if (!common.regPassword($(this).val())) {
                              $(this).parent().css("borderColor","red");
                           } else {
                              $(this).parent().css("borderColor","#ccc");
                           }
                       }else{
                            $(this).parent().css("borderColor","#ccc");
                       }
                   }
                   else if($(this)[0] == document.querySelector('.pNumber')){
                      if($(this).val() !=""){
                          if (!common.regProductNumber($(this).val())) {
                             $(this).parent().css("borderColor","red");
                          } else {
                             $(this).parent().css("borderColor","#ccc");
                          }
                      }else{
                           $(this).parent().css("borderColor","#ccc");
                      }
                   }
                   else if($(this)[0] == document.querySelector('.describe')){
                        if($(this).val() !=""){
                           if (!common.regDescribe($(this).val())) {
                              $(this).parent().css("borderColor","red");
                           } else {
                              $(this).parent().css("borderColor","#ccc");
                           }
                        }else{
                            $(this).parent().css("borderColor","#ccc");
                        }
                   }else if($(this)[0] == document.querySelector('.originalStoke')){
                        if($(this).val() !=""){
                           if (!common.regMoney($(this).val())) {
                              $(this).parent().css("borderColor","red");
                           } else {
                              $(this).parent().css("borderColor","#ccc");
                           }
                        }else{
                            $(this).parent().css("borderColor","#ccc");
                        }
                   }
            });

            /* input animation */
//            $(document).on("click",".pcode",function(){
//                 var scroll = $(this).offset().top -58;
//                 $("#addProductPage .bar-nav~.content").scrollTop(scroll);
//            });
//
//            $(document).on("click",".describe,.unit",function(){
//                var scroll = $(this).offset().top;
//                $("#addProductPage .bar-nav~.content").scrollTop(scroll);
//            });

            /* sku switch */
//            $(document).on("click",".button_switch",function(){
//                if(switchKey == 0){
//                    $(".sku_TotallySet").css("visibility","visible");
//                    $(".sku_properties").animate({
//                      height:"6.5rem",
//                    }, 200, 'ease-out',function(){
//                        switchKey = 1;
//                    });
//
//                }else{
//                    $(".sku_TotallySet").css("visibility","hidden");
//                    $(".sku_properties").animate({
//                      height:"0rem",
//                    }, 200, 'ease-out',function(){
//                         switchKey = 0;
//                    });
//                }
//            });

            /* sku switch */
            $(document).on("click",".common_switch",function (e) {
                var checkbox = $(".switch_checkbox");
                var switch_content = $(".switch_content");
                var content_circle = $(".content_circle");
                common.switchResult(checkbox,switch_content,content_circle,resultCallback(checkbox));
            });

            function resultCallback(checkbox) {
                if(!checkbox.prop("checked")){
                     $(".sku_TotallySet").css("visibility","visible");
                     $(".sku_properties").animate({
                       height:"6.5rem",
                     }, 200, 'ease-out');
                }else{
                     $(".sku_TotallySet").css("visibility","hidden");
                     $(".sku_properties").animate({
                       height:"0rem",
                     }, 200, 'ease-out');
                }
            };

            /* total set stock and price*/

            $(document).on("click",".sku_TotallySet",function(){
                $.router.load("#sku_setPrice");
            });

            /* set sku param */

            $(document).on("click",".sku_properties",function(){
                if(!isEdit){
                    $.router.load("#sku_setParam");
                }
            });

            /* checked if the sku product has product number */
            function checkedProductNumber(inventory_list){
                if(inventory_list.length >0 ){
                    for(var i=0;i<inventory_list.length;i++){
                        if(inventory_list[i].product_number){
                            return true;
                        }
                    }
                }
            }

            /* choice warehouse */
            $(document).on("click",".choice_warehouse",function(){
                $.popup(".popup-choiceWarehouse");
            });

            $(document).on("click",".bar_backButton",function(){
                $.closeModal(".popup-choiceWarehouse");
            });


        }

        /* set sku_param page*/
        function set_sku_param(){
            var paramIndex = 0;
             /* request company sku data */
                var companySKu =common.dataTransform('sku/query','POST',{});
                common.jsCallNativeEventHandler(
                    JS_CALL_NATIVE_EVENT_REQUEST_WEB_API,
                    companySKu,
                    function(data){
                       var respondData =data.response;
                       respondData =JSON.parse(respondData);
                       var retBody = respondData.retbody;
                       if(respondData.retcode ==200){
                            var companySku =retBody;
                            console.log('获取到的公司sku='+companySku);

                            var checkedCompanySku = sessionStorage.getItem("choiceSkuParam");
                            if(checkedCompanySku){
                                console.log('获取到选中的公司sku='+companySku);
                                checkedCompanySku = JSON.parse(checkedCompanySku);
                            }
                             /* 重绘页面dom */
                               var template ="";
                               $.each(companySku,function(key,value){
                                 template += $("#set_param").html()
                                             .replace("{{sku_key_name}}",value.sku_key_name)
                                             .replace("{{sku_key_id}}",value.sku_key_id)
                               });
                               $(".sku_list").html(template);
                               console.log("获取元素的data属性="+$(".sku_item").data("sku_key_id"));
                               $.each(companySku,function(key,value){
                                   var templates="";
                                   if(value.sku_value_list.length >0){
                                       $.each(value.sku_value_list,function(key1,value1){
                                            templates += $("#set_param_value").html()
                                                        .replace("{{sku_value_id}}",value1.sku_value_id)
                                                        .replace("{{sku_value_name}}",value1.sku_value_name)
                                       });
                                   $(".item_list").eq(key).html(templates);
                                   }
                               });

                               /* traverse for checked sku_value  */
                               if(checkedCompanySku){
                                   for(var i =0; i<$(".sku_item").length; i++){
                                       for(var j =0 ;j<checkedCompanySku.length; j++){
                                           if($(".sku_item").eq(i).data("sku_key_id") == checkedCompanySku[j].sku_key_id){
                                                $(".sku_item").eq(i).addClass("hasChoice2");
                                                var item_label = $(".sku_item").eq(i).children(".item_list").children(".item_label");
                                                var item_label_list = checkedCompanySku[j].sku_value_list;
                                                for(var k= 0; k<item_label.length; k++){
                                                    for(var h=0; h<item_label_list.length;h++){
                                                        if(item_label.eq(k).data("sku_value_id") == item_label_list[h].sku_value_id){
                                                            item_label.eq(k).addClass("hasChoice1").children("input").prop("checked",true);
                                                        }
                                                    }
                                                }
                                           }
                                       }
                                   }
                               }
                       }else{
                            $.toast("网络异常");
                       }
                    }
                )

            /* edit sku_value */
            $(document).off("click",".item_edit");
            $(document).on("click",".item_edit",function(){
              var choices = $(this).parent().siblings(".item_list").children(".item_label").children("input");
                  $(this).siblings("label").css("visibility","visible");
                  if($(this).text() =="编辑"){
                     choices.prop("checked",false);
                     $(this).parents(".item_title").siblings(".item_list").children(".item_label").removeClass("hasChoice1");
                     $(this).parents(".sku_item").removeClass("hasChoice2");
                  }

              if($(this).text() == "删除"){
                var _this = $(this),
                    deleteSku_valueIdArray =[];
                var  delete_checked = $(this).parents(".item_title").siblings(".item_list").children(".item_label").children("input:checked");

                if(delete_checked.length >0){
                    for(var i =0;i<delete_checked.length;i++){
                         deleteSku_valueIdArray.push(delete_checked.eq(i).parent().data("sku_value_id"));
                    }

                    $.confirm("确认删除?","温馨提示",function(){

                        var deleteSku_value ={
                             sku_value_ids:deleteSku_valueIdArray
                        };
                        var deleteSku_valueData =common.dataTransform('sku_value/delete','POST',deleteSku_value);
                         common.jsCallNativeEventHandler(
                            JS_CALL_NATIVE_EVENT_REQUEST_WEB_API,
                            deleteSku_valueData,
                            function(data){
                               var respondData =data.response;
                               respondData =JSON.parse(respondData);
                               var retBody =respondData.retbody;
                               var ifExist_sku_stock =false;
                               if(respondData.retcode ==200){
                                  if(retBody.fail_list.length>0){
                                     for(var k=0;k< delete_checked.length;k++){
                                         for(var h=0;h<retBody.fail_list.length;h++){
                                              if((retBody.fail_list)[h] == delete_checked.eq(k).parent().data("sku_value_id")){
                                                    delete_checked.eq(k).prop("checked",false);
                                                    ifExist_sku_stock =true;
                                              }else{
                                                 _this.parent().siblings(".item_list").children(".item_label").children("input:checked").parent().remove();
                                                 _this.siblings("label").children("input:checked").prop("checked",false);
                                              }
                                         }
                                     }
                                     if(ifExist_sku_stock){
                                         $.toast("选中的sku属性中存在库存，请先删除有库存的货品",10000)
                                     }
                                  }else{
                                     _this.parent().siblings(".item_list").children(".item_label").children("input:checked").parent().remove();
                                     _this.siblings("label").children("input:checked").prop("checked",false);
                                  }
                               }else{
                                    $.toast("网络异常");
                               }
                            }
                         )

                    },function(){},"取消","确认");
                }else{
                      $.toast("请选中需要删除的SKU属性")
                }
              }
              $(this).text("删除");
            });

             /* single choice */
            $(document).off("click",".item_content");
            $(document).on("click",".item_content",function(){
                var checkBox = $(this).siblings("input");
                var allChoiceButton =  $(this).parents(".item_list").siblings(".item_title").children("label").children("input");

                if(checkBox.prop("checked")){
                     checkBox.prop("checked",false);
                     if($(this).parents(".item_list").siblings(".item_title").children(".item_edit").text() =="编辑"){
                         $(this).parent().removeClass("hasChoice1");
                         if($(this).parents(".item_list").children(".item_label").children("input:checked").length ==0){
                              $(this).parents(".sku_item").removeClass("hasChoice2")
                         }
                     }
                }else{
                     checkBox.prop("checked",true);
                     if($(this).parents(".item_list").siblings(".item_title").children(".item_edit").text() =="编辑"){
                         $(this).parent().addClass("hasChoice1");
                         $(this).parents(".sku_item").addClass("hasChoice2")
                     }
                }

                var totalLength = $(this).parents(".item_list").children("label").children("input").length,
                    trueLength = $(this).parents(".item_list").children("label").children("input:checked").length;

                if(trueLength ==0){
                    $(this).parents(".item_list").siblings(".item_title").children("")
                }

                if(trueLength < totalLength){
                    allChoiceButton.prop("checked",false);
                }else{
                    allChoiceButton.prop("checked",true);
                }
                console.log("父元素的id="+$(this).parent().data("sku_value_id"));
            });

            $(document).off("click",".item_label>input");
            $(document).on("click",".item_label>input",function(){
                var allChoiceButton =  $(this).parents(".item_list").siblings(".item_title").children("label").children("input");
                var totalLength = $(this).parents(".item_list").children("label").children("input").length,
                    trueLength = $(this).parents(".item_list").children("label").children("input:checked").length;

                if($(this).prop("checked")){
                    if($(this).parents(".item_list").siblings(".item_title").children(".item_edit").text() =="编辑"){
                        $(this).parent().addClass("hasChoice1");
                        $(this).parents(".sku_item").addClass("hasChoice2")
                    }
                }else{
                    if($(this).parents(".item_list").siblings(".item_title").children(".item_edit").text() =="编辑"){
                        $(this).parent().removeClass("hasChoice1")
                        if($(this).parents(".item_list").children(".item_label").children("input:checked").length ==0){
                           $(this).parents(".sku_item").removeClass("hasChoice2")
                        }
                    }
                }

                if(trueLength < totalLength){
                    allChoiceButton.prop("checked",false);
                }else{
                    allChoiceButton.prop("checked",true);
                }
            });

            /* all choice and */
            $(document).off("click",".item_allChoice")
            $(document).on("click",".item_allChoice",function(){
                var inputCollect = $(this).parents(".item_title").siblings(".item_list").children(".item_label").children("input");
                var allChoiceButton = $(this).siblings("input");
                if(allChoiceButton.prop("checked") ==false){
                    if($(this).parent().siblings(".item_edit").text()=="编辑"){
                        $(this).parents(".sku_item").addClass("hasChoice2");
                        inputCollect.siblings(".item_content").addClass("hasChoice1");
                    }
                   inputCollect.prop("checked",true);
                }else{
                   inputCollect.siblings(".item_content").removeClass("hasChoice1");
                   if($(this).parent().siblings(".item_edit").text()=="编辑"){
                        $(this).parents(".sku_item").removeClass("hasChoice2");
                        inputCollect.prop("checked",false);
                   }
                }
            });

            $(document).off("click",".allChoiceButton")
            $(document).on("click",".allChoiceButton",function(){
                var inputCollect = $(this).parents(".item_title").siblings(".item_list").children(".item_label").children("input");
                if($(this).prop("checked") == true){
                   inputCollect.prop("checked",true);
                   if($(this).parent().siblings(".item_edit").text()=="编辑"){
                       inputCollect.addClass("hasChoice1");
                       $(this).parents(".sku_item").addClass("hasChoice2");
                   }
                }else{
                   inputCollect.prop("checked",false);
                   if($(this).parent().siblings(".item_edit").text()=="编辑"){
                       inputCollect.siblings(".item_content").removeClass("hasChoice1");
                       $(this).parents(".sku_item").removeClass("hasChoice2");
                   }
                }
            });

            /* add sku value*/
            $(document).off("click",".add_button")
            $(document).on("click",".add_button",function(){
                $(".sku_newValue").val("");
                $.popup(".popup-newValue");
                paramIndex = $(this).parents(".sku_item").index();
            });

            $(document).on("click",".cancelSet",function(){
                $.closeModal(".popup-newValue");
            });

            $(document).off("click",".confirmSet")
            $(document).on("click",".confirmSet",function(){
                if(validate_sku_value()){
                   var newValue =$(".sku_newValue").val();
                   var addSku_value ={
                        sku_key_id: $(".sku_item").eq(paramIndex).data("sku_key_id"),
                        sku_value_name:newValue
                   };
                   var addSku_valueData =common.dataTransform('sku_value/add','POST',addSku_value);
                    common.jsCallNativeEventHandler(
                       JS_CALL_NATIVE_EVENT_REQUEST_WEB_API,
                       addSku_valueData,
                       function(data){
                          var respondData =data.response;
                          console.log("添加成功返回="+respondData);
                          respondData =JSON.parse(respondData);
                          var retBody = respondData.retbody;
                          if(respondData.retcode ==200){
                            $(".item_list").eq(paramIndex).append('<label class="item_label" data-sku_value_id="'+retBody.sku_value_id+'"><input type="checkbox" /><button class="item_content">'+newValue+'</button></label>');
//                            var newInputIndex = $(".item_list").eq(paramIndex).children("label").length - 1;
//                            $(".item_list").eq(paramIndex).children("label").children("input").eq(newInputIndex).prop("checked",true);
//                            $(".item_list").parents(".sku_item").addClass("hasChoice2");
                            $.closeModal(".popup-newValue");
                          }else if(respondData.retcode ==180000012){
                               $.toast("SKU属性重复");
                               $(".sku_newValue").val("")
                          }else{
                               $.toast("网络异常");
                          }
                       }
                    )
                }else{
                    $.toast("请输入正确的sku属性",1000)
                }
            });

            function validate_sku_value(){
                var newValue =$(".sku_newValue").val();
                if(common.regName(newValue)){
                    return true
                }else{
                    return false;
                }
            }

            /* back page */
            $(document).off("click",".backPageBtns");
            $(document).on("click",".backPageBtns",function(){
                    window.history.back();
            });

            /* confirm sku_value */
            $(document).off("click","#confirmSku_value");
            $(document).on("click","#confirmSku_value",function(){
                 var sku_listArray = [],
                     sku_param_list =[];


                for(var r=0;r<$(".name_text").length;r++){
                  $(".name_text").eq(r).siblings(".value_icon").text("");
                }

                 for(var i=0; i<$(".hasChoice2").length; i++){
                     var sku_paramObject ={},sku_value_list =[],showParamObject={sku_param:""};

                     sku_paramObject.sku_key_id = parseInt($(".hasChoice2").eq(i).data("sku_key_id"));
                     sku_paramObject.sku_key_name = $(".hasChoice2").eq(i).children(".item_title").children(".item_describe").text();
                     showParamObject.sku_key_name = $(".hasChoice2").eq(i).children(".item_title").children(".item_describe").text();

                     var eachItem = $(".hasChoice2").eq(i).children(".item_list").children(".hasChoice1");
                     for(var j=0;j<eachItem.length;j++){
                         var sku_valueObject ={};
                         sku_valueObject.sku_value_name = eachItem.eq(j).children(".item_content").text();
                         sku_valueObject.sku_value_id = parseInt(eachItem.eq(j).data("sku_value_id")) ;
                         sku_value_list.push(sku_valueObject);
                         if(j<eachItem.length-1){
                            showParamObject.sku_param += eachItem.eq(j).children(".item_content").text()+"、";
                         }else{
                            showParamObject.sku_param += eachItem.eq(j).children(".item_content").text();
                         }
                     }
                     sku_paramObject.sku_value_list = sku_value_list;
                     sku_listArray.push(sku_paramObject);
                     sku_param_list.push(showParamObject);
                 }
               console.log("完成组合的sku值="+JSON.stringify(sku_listArray));

               /* etit addProduct page sku param */
               for(var r=0;r<$(".name_text").length;r++){
                    for(var t=0;t< sku_param_list.length;t++){
                        if(sku_param_list[t].sku_key_name == $(".name_text").eq(r).text()){
                            $(".name_text").eq(r).siblings(".value_icon").text(sku_param_list[t].sku_param);
                        }
                    }
               }

                /* clear disable input */
               $(".stock_price").val("").removeAttr("disabled").css({"color":"#000","backgroundColor":"#fff"});
               $(".standard_price").val("").removeAttr("disabled").css({"color":"#000","backgroundColor":"#fff"});
               $(".originalStoke").val("").removeAttr("disabled").css({"color":"#000","backgroundColor":"#fff"});

               /* cache the choice sku param  */

               sessionStorage.setItem("choiceSkuParam",JSON.stringify(sku_listArray));
               sessionStorage.removeItem("modified_inventory_list");
               window.history.back();
            });
        }

        /* total set sku price and stock */
        function set_sku_priceAndStock(){


            /* 设置sku属性后，重绘dom */
            var checkedCompanySku = sessionStorage.getItem("choiceSkuParam");
            if(checkedCompanySku){
                checkedCompanySku = JSON.parse(checkedCompanySku);
                var createInventoryLists = createInventoryList();
                var modified_inventory_list =sessionStorage.getItem("modified_inventory_list");
                if(modified_inventory_list){
                    createInventoryLists = JSON.parse(modified_inventory_list);
                }
                var domTemplate1 ="";
                $.each(createInventoryLists,function(key,value){
                    domTemplate1 += $("#set_list").html()
                                   .replace("{{sku_value_name}}",value.sku_value_name ? value.sku_value_name:value.sku_name )
                                   .replace("{{sku_inventory_object}}",JSON.stringify(value))

                });
                $(".set_content").html(domTemplate1);


                /* 自动添加货号 */
                if(!sessionStorage.getItem("scanProduct")){
                    var total_autoProductNumber = createInventoryList().length;
                    if(total_autoProductNumber >0){
                        var total_autoProductNumberData ={
                            size:total_autoProductNumber +1,
                            shop_id:getShopId
                        };
                        salesModule.getAutoSettingProductNumber(total_autoProductNumberData,function(data){
                            var respondData =data.response;
                            respondData =JSON.parse(respondData);
                            if(respondData.retcode ==200){
                               if(respondData.retbody.product_number_list.length >0 ){
                                    var product_number_list = respondData.retbody.product_number_list.slice(1);
                                    $.each(product_number_list,function(key,value){
                                        $(".detail_productCode").eq(key).val(value ? value:"");
                                    });
                               }
                            }else if(respondData.retcode == 4047){
                                 $.toast("已达到货号上限，请重新设置",1000);
                            }else if(respondData.retcode == 4049 || respondData.retcode == 4046){
                                 return true;
                            }else {
                                $.toast('网络异常',1000);
                            }
                        });
                    }
                }

                if(modified_inventory_list){
                    $.each(JSON.parse(modified_inventory_list),function(key,value){
                         $(".number_value").eq(key).val(value.quantity ? value.quantity:"");
                         $(".detail_barcode").eq(key).val(value.product_code ? value.product_code:"");
                         $(".detail_productCode").eq(key).val(value.product_number ? value.product_number:"");
                         $(".detail_price").eq(key).val(value.standard_price).attr("placeholder","请输入统一售价");
                         $(".detail_cost").eq(key).val(value.stock_price).attr("placeholder","请输入统一进价");
                         $(".set-item").eq(key).attr("data-inventory_id",value.inventory_id);
                    });
                }
                if(switchKeys ==1){
                    $(".hiddenBefore").css({
                        "height":"2.4rem",
                        "borderBottom":"1px solid #ccc"
                    });
                }
                if(isEdit){
                    $(".stock_detail").find("input").attr("disabled","disabled");
                }
            }

            var cachePriceData = sessionStorage.getItem("cachePriceData");
            if(cachePriceData){
                cachePriceData = JSON.parse(cachePriceData);
                var domTemplate2 ="";
                $.each(cachePriceData,function(key,value){
                    domTemplate2 += $("#set_list").html()
                                    .replace("{{sku_value_name}}",value.sku_value_name)
                                    .replace("{{number_value}}",value.sku_value_name)
                });
            }

            /* 添加时重绘dom */
            function createInventoryList(){
                if(checkedCompanySku.length ==1){
                        InventoryList1 = [];
                        for(var k =0 ;k< checkedCompanySku[0].sku_value_list.length;k++){
                            var InventoryList1_object ={
                                sku_value_name:"",
                                sku_value_id1: 0};
                            InventoryList1_object.sku_value_name = checkedCompanySku[0].sku_value_list[k].sku_value_name;
                            InventoryList1_object.sku_value_id1 =  checkedCompanySku[0].sku_value_list[k].sku_value_id;
                            InventoryList1.push(InventoryList1_object);
                        }
                    return InventoryList1;
                }else if(checkedCompanySku.length ==2){
                    var InventoryList2 = [];
                    for(var i =0;i<checkedCompanySku[0].sku_value_list.length;i++){
                        for(var j=0;j<checkedCompanySku[1].sku_value_list.length;j++){
                             var InventoryList2_object = {sku_value_name:"",
                                                         sku_value_id1:0,
                                                         sku_value_id2:0 };
                             InventoryList2_object.sku_value_name = checkedCompanySku[0].sku_value_list[i].sku_value_name +","+checkedCompanySku[1].sku_value_list[j].sku_value_name;
                             InventoryList2_object.sku_value_id1 = checkedCompanySku[0].sku_value_list[i].sku_value_id;
                             InventoryList2_object.sku_value_id2 = checkedCompanySku[1].sku_value_list[j].sku_value_id;
                             InventoryList2.push(InventoryList2_object);
                        }
                    }
                    return InventoryList2;
                }else if(checkedCompanySku.length ==3){
                    var InventoryList3 = [];
                    for(var l =0;l<checkedCompanySku[0].sku_value_list.length;l++){
                        for(var m=0;m<checkedCompanySku[1].sku_value_list.length;m++){
                             for(var n=0; n< checkedCompanySku[2].sku_value_list.length;n++){
                                    var InventoryList3_object= {sku_value_name:"",sku_value_id1:0,sku_value_id2:0,sku_value_id3:0};
                                     InventoryList3_object.sku_value_name = checkedCompanySku[0].sku_value_list[l].sku_value_name +","+checkedCompanySku[1].sku_value_list[m].sku_value_name+","+checkedCompanySku[2].sku_value_list[n].sku_value_name;
                                     InventoryList3_object.sku_value_id1 = checkedCompanySku[0].sku_value_list[l].sku_value_id;
                                     InventoryList3_object.sku_value_id2 = checkedCompanySku[1].sku_value_list[m].sku_value_id;
                                     InventoryList3_object.sku_value_id3 = checkedCompanySku[2].sku_value_list[n].sku_value_id;
                                     InventoryList3.push(InventoryList3_object);
                             }
                        }
                    }
                    return InventoryList3;
                }
            }

                /* switch for setting barcode and product code */
                $(document).off("click",".common_switch_sku");
                $(document).on("click",".common_switch_sku",function (e) {
                    var checkbox = $(".switch_checkbox_sku");
                    var switch_content = $(".switch_content_sku");
                    var content_circle = $(".content_circle_sku");
                    common.switchResult(checkbox,switch_content,content_circle,resultCallback(checkbox));
                });

                function resultCallback(checkbox) {
                    if(!checkbox.prop("checked")){
                         $(".hiddenBefore").animate({
                             height:"2.4rem",
                             borderBottom:"1px solid #ccc"
                         },200,"ease-out")
                    }else{
                         $(".hiddenBefore").animate({
                             height:"0",
                             borderBottom:"0"
                         },200,"ease-out")
                    }
                };

                var totalSetIndex = 0;
                /* switch for total set price cost and stock  */
                $(document).off("click",".totalStock,.totalCost,.totalPrice");
                $(document).on("click",".totalStock,.totalCost,.totalPrice",function(){
                   if(!isEdit){
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
                    if(!isEdit){
                        if(validate_inputValue()){
                             if(totalSetIndex ==0){
                                 $(".number_value").val($(".input_value").val()).removeClass("borderRed").attr("placeholder","请输入统一库存");
                             }else if(totalSetIndex ==1){
                                 $(".detail_cost").val($(".input_value").val()).removeClass("borderRed").attr("placeholder","请输入统一进价");
                             }else if(totalSetIndex ==2){
                                 $(".detail_price").val($(".input_value").val()).removeClass("borderRed").attr("placeholder","请输入统一零售价");
                             }
                             $(".input_value").val("");
                        }else{
                            $.toast("请输入正确的信息",1300);
                            $(".input_value").val("");
                        }
                    }
                });

                $(document).on("input",".input_value",function(){
                     common.inputMoney($(this));
                          common.inputMoney($(this));
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

                $(document).on("click",".number_value",function(){
                    var txtFocus = $(this)[0];
                    var userAgent= window.navigator.userAgent.toLowerCase();
                    var isWwebkit = /webkit/.test(userAgent);//谷歌
                    if(isWwebkit){
                        txtFocus.setSelectionRange((txtFocus.value).length,(txtFocus.value).length);
                    }
                });

                /* validate each input value */
                $(document).off("input",".detail_barcode");
                $(document).on("input",".detail_barcode",function(){
                   if($(this).val() !=""){
                      if (!common.regPassword($(this).val())) {
                         $(this).addClass("borderRed");
                      } else {
                         $(this).removeClass("borderRed");
                      }
                   }else{
                       $(this).removeClass("borderRed");
                   }
                });

                $(document).off("input",".detail_productCode");
                $(document).on("input",".detail_productCode",function(){
                   if($(this).val() !=""){
                      if (!common.regProductNumber($(this).val())) {
                         $(this).addClass("borderRed");
                      } else {
                         $(this).removeClass("borderRed");
                      }
                   }else{
                       $(this).removeClass("borderRed");
                   }
                });


                $(document).off("input",".detail_price,.detail_cost");
                $(document).on("input",".detail_price,.detail_cost",function(){
                   common.inputMoney($(this));
                   if(common.regMoney($(this).val())){
                      if($(this).val().indexOf(".") < 0 && ($(this).val())[0] == 0){
                          $(this).addClass("borderRed");
                      }else{
                          $(this).removeClass("borderRed");
                      }
                   }else{
                         $(this).addClass("borderRed");
                   }
                   $(this).attr("placeholder","");
                });

                $(document).off("input",".number_value")
                $(document).on("input",".number_value",function(){
                     common.inputMoney($(this));
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
                   if(!isEdit){
                        var stock = $(this).siblings("input");
                        if(stock.hasClass("borderRed")){
                            $(this).siblings("input").val("").removeClass("borderRed");
                        }else{
                            if(parseFloat(stock.val()) >= 1){
                             var calculateValue =  parseFloat(stock.val())-1;
                             $(this).siblings("input").val(calculateValue.toFixed(2));
                            }
                        }
                   }
                });

                $(document).off("click",".number_plus");
                $(document).on("click",".number_plus",function(){
                    if(!isEdit){
                        var stock = $(this).siblings("input");
                        if(stock.hasClass("borderRed")){
                            $(this).siblings("input").val("").removeClass("borderRed");
                        }else{
                            if(parseFloat(stock.val()) >=0){
                             var calculateValue =  parseFloat(stock.val())+1;
                             $(this).siblings("input").val(calculateValue.toFixed(2));
                            }
                        }
                    }
                });

                /* submit */
                $(document).off("click",".confirmButton");
                $(document).on("click",".confirmButton",function(){
                    var emptyPrice = $("input[placeholder='请输入价格']").length;
                    var inventory_list = [];
                    var stock_price_list =[],
                        standard_price_list =[],
                        stock_number_value= 0;
                    var stock_price_query ="",
                        standard_price_query ="";

                    if($(".detail_content").hasClass("borderRed") || $(".number_value").hasClass("borderRed")){
                        $.toast("请输入正确的信息")
                    }else if(emptyPrice >0){
                        $($("input[placeholder='请输入价格']").addClass("borderRed"));
                        $.toast("请输入单价和零售价");
                    }else{
                        for(var i=0;i<$(".set-item").length;i++){
                             var set_quantity = $(".set-item").eq(i).children(".item_titles").children(".title_number").children(".number_value").val() ? parseFloat($(".set-item").eq(i).children(".item_titles").children(".title_number").children(".number_value").val()) :0;
                             stock_price_list.push($(".set-item").eq(i).children(".item_stock_price").children(".detail_cost").val());
                             standard_price_list.push($(".set-item").eq(i).children(".item_standard_price").children(".detail_price").val());
                             stock_number_value += set_quantity;

                             var sku_inventory_object = JSON.parse($(".set-item").eq(i).children(".saveData").text());
                             sku_inventory_object.quantity = set_quantity;
                             sku_inventory_object.product_number = $(".set-item").eq(i).children(".item_productCode").children(".detail_productCode").val();
                             sku_inventory_object.product_code =  $(".set-item").eq(i).children(".item_barcode").children(".detail_barcode").val();
                             sku_inventory_object.standard_price = $(".set-item").eq(i).children(".item_standard_price").children(".detail_price").val();
                             sku_inventory_object.stock_price = $(".set-item").eq(i).children(".item_stock_price").children(".detail_cost").val();
                             inventory_list.push(sku_inventory_object);
                        };

                        if(stock_price_list.length >0){
                             stock_price_query = Math.min.apply(null,stock_price_list)+" - "+ Math.max.apply(null,stock_price_list);
                             $(".stock_price").val(stock_price_query).attr("disabled","disabled").css({"color":"#ccc","backgroundColor":"#fff","borderColor":"#ccc"});
                             price_special =true;
                        }
                        if(standard_price_list.length >0){
                             standard_price_query  = Math.min.apply(null,standard_price_list)+" - "+ Math.max.apply(null,standard_price_list);
                             $(".standard_price").val(standard_price_query).attr("disabled","disabled").css({"color":"#ccc","backgroundColor":"#fff","borderColor":"#ccc"});
                             price_special =true;
                        }
                        if(stock_number_value > 0){
                             $(".originalStoke").val(stock_number_value.toFixed(2)).attr("disabled","disabled").css({"color":"#ccc","backgroundColor":"#fff","borderColor":"#ccc"});
                        }
                        console.log("完成价格设置="+JSON.stringify(inventory_list));
                        sessionStorage.setItem("inventory_list",JSON.stringify(inventory_list));

                        /* change price validate */
                        validateProductInfo = function (){
                              var isProductName = true,isProductSaleType=true;
                              if($(".product_name").val() =="") {
                                  isProductName = false;
                              }
                              if($(".title_info_saleType").text()  == "" ){
                                    $(".title_info_saleType").parents(".login_input").css("borderColor","red");
                                    isProductSaleType =false
                              }else{
                                    $(".title_info_saleType").parents(".login_input").css("borderColor","#ccc");
                              }
                              return isProductName && isProductSaleType ;
                        }

                        $(".standard_price").parent().css("borderColor","#ccc");
                        $(".stock_price").parent().css("borderColor","#ccc");
                        /* 缓存修改后的组合数据 */
                        sessionStorage.setItem("modified_inventory_list",JSON.stringify(inventory_list));
                        window.history.back();
                    }
                });

                 /* delete sku_value product */
                $(document).off("click",".detail_delete");
                if(isEdit){
                    $(document).on("click",".detail_delete",function(){
                       var _this =$(this);
                       var deleteData ={
                            inventory_id:parseInt($(this).parents(".set-item").data("inventory_id"))
                       };
                        $.confirm("确认删除?","温馨提示",function(){
                         var deleteDataRequest =common.dataTransform('sku_inventory/delete','POST',deleteData);
                              common.jsCallNativeEventHandler(
                                 JS_CALL_NATIVE_EVENT_REQUEST_WEB_API,
                                 deleteDataRequest,
                                 function(data){
                                     var respondData =data.response;
                                     respondData =JSON.parse(respondData);
                                     if(respondData.retcode ==200){
                                          _this.parents(".set-item").remove();
                                          var minus_stock = parseInt($(".originalStoke").val()) - parseInt(_this.parent().siblings(".item_titles").find(".number_value").val());
                                          $(".originalStoke").val(minus_stock);
                                          if($(".set-item").length < 1){
                                              $.toast("货品删除成功");
                                              setTimeout(function(){
                                                common.backHomePage();
                                              },1000);
                                          }
                                     }else if(respondData.retcode == 180000023){
                                           $.toast("该货品在其它仓库有库存，无法删除");
                                     }else{
                                          $.toast("网络异常,删除记录失败");
                                     }
                                 }
                              )
                        },function(){},"取消","确认");
                    });
                }else{
                   $(document).on("click",".detail_delete",function(){
                     var _this =$(this);
                        $.confirm("确认删除?","温馨提示",function(){
                             _this.parents(".set-item").remove();
                        },function(){},"取消","确认");
                   });
                }


            /* back page */
            $(document).off("click",".backPageBtn")
            $(document).on("click",".backPageBtn",function(){
                  window.history.back();
            });
        }

        $(document).on("pageInit","#addProductPage",function(){
            addProduct();
        });

        $(document).on("pageInit","#sku_setPrice",function(){
            set_sku_priceAndStock();
        });

        $(document).on("pageInit","#sku_setParam",function(){
            set_sku_param();
        })
        $.init();
    })
})()