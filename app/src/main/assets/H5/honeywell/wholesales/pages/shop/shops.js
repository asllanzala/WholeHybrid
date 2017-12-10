/**
 * (c) Copyright 2016 Administrator. All Rights Reserved.
 * 2016-05-19
 *
 */
(function () {
    require(['zepto', 'common', 'shopModule', 'salesModule'], function ($, common, shopModule, salesModule) {

        var currentShopId = "",
            isNative = true,
            addImg = false,
            editImg = false,
            addImageResult ="",
            editImageResult="";



        /* shop list page */
        function shopList(){

            var template =$('#shopTemplate').html();

            common.connectJsTunnel(function (jsTunnel) {
                if(isNative){
                    jsTunnel.init();
                    isNative =false;
                }

                var userData = {
                    method: NATIVE_METHOD_GET_USER_INFO,
                    data: ""
                };
                common.jsCallNativeEventHandler(JS_CALL_NATIVE_EVENT_EXECUTE_NATIVE_METHOD, userData, function (data) {
                    var userInfo = JSON.parse(data.response);
                    shopModule.queryShopList(userInfo.companyAccount, function (shopData){
                        var templateText = "";
                        var httpResponse = JSON.parse(shopData.response);
                        console.log('获取到的店铺的信息='+shopData.response);
                        if (shopData.ack == 1){
                            var shopList = httpResponse.retbody;
                            if (shopList && shopList.length > 0) {
                                $.each(shopList, function (i,shop) {
                                    templateText += template
                                        .replace("{{shop_id}}",shop.shop_id)
                                        .replace("{{shop_name}}",shop.shop_name)
                                        .replace("{{shop_address}}",shop.shop_address)
                                        .replace("{{shop_industry}}",shop.industry_desc)
                                        .replace("{{shop_industryid}}",shop.industry_id)
                                        .replace("{{contact_phone}}",shop.contact_phone)
                                        .replace("{{shop_img}}",((shop.pic_hd_src)[0]?(shop.pic_hd_src)[0]:""))
                                        .replace("../../images/icons/shopImg.png",((shop.pic_src)[0]?(shop.pic_src)[0]:"../../images/icons/shopImg.png"));
                                });
                               $("#box").html(templateText);
                            }else {
                               $("#box").html(($('#noInfo').html()));
                            };
                        }else{
                            $.toast('网络异常', 2000, 'error');
                        };
                    });
                });
            });

            $(document).off("click", ".shopDetail");
            $(document).on("click", ".shopDetail", function () {
              $(document).off("click", ".shopDetail");
              var shopDetail = {
                    "shop_address": $(this).data("address"),
                    "shop_id": $(this).data("currentshopid"),
                    "contact_phone": $(this).find(".cphone").text(),
                    "shop_name": $(this).find(".shopName").text(),
                    "shop_industry":$(this).data("industry"),
                    "industry_id":$(this).data("industryid"),
                    "shop_img":$(this).data('img')
                }
                currentShopId = $(this).data("currentshopid");
                sessionStorage.setItem("shopDetail", JSON.stringify(shopDetail));
                common.log('店铺的信息='+JSON.stringify(shopDetail));
                $.router.load("#shopDetailPage")
            });
            $(document).off("click", "#addShopBtn");
            $(document).on("click", "#addShopBtn", function () {
                $(document).off("click", "#addShopBtn");
                $.router.load("#addShopPage");
            });



            $(document).off("click", ".backPageBtns");
            $(document).on("click", ".backPageBtns", function() {
                $(document).off("click", ".backPageBtns");
                common.backHomePage();
            });
        }

        /* shop detail page */
        function shopDetail(){

         !function(){
            $(".moreBtn").on("click",function(e){
                   $('.more').toggleClass("toggleCss");
                   e.cancelBubble = true;
            });

            $("#shopDetailPage").not($(".moreBtn")).click(function(){
                   $('.more').removeClass("toggleCss")
            });
            common.deletePopup();
         }();

            $("#delete").css("opacity",1);
            var shopDetailStr = sessionStorage.getItem("shopDetail");
            console.log("获取到的缓存="+shopDetailStr);
            if(common.isNotnull(shopDetailStr)){
                var shop = JSON.parse(shopDetailStr);
                $("#shopName").text(shop.shop_name);
                $(".shopAddress").text(shop.shop_address);
                $(".shopMobile").text(shop.contact_phone);
                if(shop.shop_img){
                    $('.showInformation').css('background','url('+shop.shop_img+') no-repeat center');
                    $('.showInformation').css('backgroundSize','cover');
                }else{
                    $('.showInformation').css('background','#f76b00');
                }


                var userData = {
                    method: NATIVE_METHOD_GET_USER_INFO,
                    data: ""
                };
                common.connectJsTunnel(function (jsTunnel) {
                    common.jsCallNativeEventHandler(JS_CALL_NATIVE_EVENT_EXECUTE_NATIVE_METHOD, userData, function (data) {
                     var  userInformation = JSON.parse(data.response);
                        if (userInformation.shopID == shop.shop_id) {
                            //如果是老板自己,则删除功能对自己隐藏,防止误操作
                            $("#delete").css("opacity",0);
                            $('#delete').off("click");
                        }
                    });
                });

            };

            /* delete shop */
            $(document).off("click", ".deleteBtn")
            $(document).on("click", ".deleteBtn", function () {
                var deleteShop = {
                    shop_id: '' + JSON.parse(shopDetailStr).shop_id
                }
                var data = common.dataTransform('shop/delete', 'POST', deleteShop);
                common.jsCallNativeEventHandler(JS_CALL_NATIVE_EVENT_REQUEST_WEB_API, data, function (responseData) {
                    var jsonData = JSON.parse(responseData.response);
                    if (responseData.ack == 1) {
                        window.history.go(-1);
                    }else if(jsonData.retcode ==4026){
                        $.toast("权限不足,删除失败");
                    } else {
                        $.toast(responseData.msg);
                    }
                });
            });

            $(document).off("click", "#backBtn");
            $(document).on("click", "#backBtn", function(){
                window.history.go(-1);
            });

            /* load edit shop page */
            $(document).off("click", "#updateCust");
            $(document).on("click", "#updateCust", function () {
                $(document).off("click", "#updateCust");
                $.router.load("#editShopPage")
            });

            $(document).off("click",".backPageBtn");
              $(document).on("click",".backPageBtn",function(){
                   $(document).off("click",".backPageBtn");
                   common.backPageHistory();
            });
        }

        /* add shop page */
        function addShop(){
            var imgUrlArray = [],
                deleteImageUrl = '',
                industryId = '';

            $("#addShopPage input").val("");
            $('#saveShopBtn').removeClass('isRightInput');
            $('.showImg,.deleteImg').remove();
            $("#showShopImg").html('<img src="{{imgSrc}}" class="showImg"/><img src="../../images/icons/delete-day2.png" class="deleteImg"/>')
            $('#shopImage').show();

            common.connectJsTunnel(function (jsTunnel){
                /* upload image feedback*/
                jsTunnel.registerHandler(
                    NATIVE_CALL_JS_EVENT_PRODUCT_IMAGE_UPLOAD_RESULT,
                    function(data, responseCallback){
                        var imgUrl = data.imgURL;
                        if(addImg){
                           addImageResult = imgUrl;
                           imgUrlArray[0] = addImageResult;
                            var template = $('#showShopImg').html().replace('{{imgSrc}}',addImageResult);
                                 $('#shopImage').hide();
                                 $('.addShopImage').html(template);
                                 addImg =false;
                        }else if(editImg){
                           editImageResult = imgUrl;
                           var template =$('#showShopImgs').html().replace('{{imgSrc}}',editImageResult);
                                  $('#shopImages').hide();
                                  $('.addShopImages').html(template);
                            editImg = false;
                        }
                    }
                );
            });

            /* operation of image */
            $(document).off('click','.deleteImg')
            $(document).on('click','.deleteImg',function(){
                 imgUrlArray = [];
                 deleteImageUrl = $(".showImg").attr("src");
                  $('.showImg, .deleteImg').remove();
                  $("#showShopImg").html('<img src="{{imgSrc}}" class="showImg"/><img src="../../images/icons/delete-day2.png" class="deleteImg"/>')
                  $('#shopImage').show();
                  addImageResult ="";
            });

            $(document).off('click','#shopImage');
            $(document).on('click','#shopImage',function(){
                addImg =true;
                var userData = {
                    method: NATIVE_METHOD_GET_USER_INFO,
                    data: ""
                };
                common.jsCallNativeEventHandler(JS_CALL_NATIVE_EVENT_EXECUTE_NATIVE_METHOD, userData, function (data) {
                    var userInfo = JSON.parse(data.response);
                    var shopId = parseInt(userInfo.shopID);
                    var productEvent = {
                        shopID: shopId
                    };
                    var imageUploadPageEvent = {
                        method: NATIVE_PAGE_IMAGE_UPLOAD,
                        data: JSON.stringify(productEvent)
                    };
                    common.jsCallNativeEventHandler(JS_CALL_NATIVE_EVENT_REQUEST_SWITCH_PAGE, imageUploadPageEvent, function (data) {
                    });
                });
            });

            /* form validate */
           $(document).on('input','.name,.address,.telephone',function(){
               if($(this)[0] == document.querySelector('.telephone')){
                   if (!common.regTel($(this).val())) {
                       $(this).parent(".login_input").addClass("errorAdd");
                   } else {
                       $(".telephone").parent(".login_input").removeClass("errorAdd");
                   }
               }else{
                   if (!common.regName($(this).val())) {
                       $(this).parent(".login_input").addClass("errorAdd");
                   } else {
                       $(this).parent(".login_input").removeClass("errorAdd");
                   }
               }

               if(validateShopInfo()){
                   $('#saveShopBtn').addClass('isRightInput')
               }else{
                   $('#saveShopBtn').removeClass('isRightInput')
               }
           });

           $(document).on('blur','.name,.address,.telephone',function(){
              if($(this)[0] == document.querySelector('.telephone')){
                  if (!common.regTel($(this).val())) {
                      $(this).parent(".login_input").addClass("errorAdd");
                      $.toast("电话请输入数字",1300,'toastInfo')
                  } else {
                      $(".telephone").parent(".login_input").removeClass("errorAdd");
                  }
              }else{
                  if (!common.regName($(this).val())) {
                      $(this).parent(".login_input").addClass("errorAdd");
                      $.toast("姓名或地址请输入中英文数字或下划线",1300,'toastInfo')
                  } else {
                      $(this).parent(".login_input").removeClass("errorAdd");
                  }
              }
          });


            function validateShopInfo() {
                var shop_name = $(".name").val(),
                    shop_address = $(".address").val(),
                    contact_phone = $(".telephone").val();
//                    industry = $('.industry').val();

                var validateFlag = true;
                if (!common.regName(shop_name)) {
                    validateFlag = false;
                }
                if (!common.regAddress(shop_address)) {
                    validateFlag = false;
                }
                if (!common.regTel(contact_phone)) {
                    validateFlag = false;
                }
//                if (common.isNullString(industry)){
//                    validateFlag = false;
//                }
                if (validateFlag) {
                    return true;
                } else {
                    return false;
                }
            }

            function shopErroCode(code) {
                if (code == '4000') {
                    $.toast('服务器连接失败', 2000, 'error');
                } else if (code == '4001') {
                    $.toast('店铺已存在', 2000, 'error');
                } else if (code == '4019') {
                    $.toast('店铺不存在', 2000, 'error');
                } else {
                    // 网络异常
                    $.toast('网络异常', 2000, 'error');
                }
            }

             $("#addShopPage .confirmBtn").off("click");
             $("#addShopPage .confirmBtn").on("click",function (e) {

                if (validateShopInfo()) {
                    /* delete img in server */
                    $(this).attr("disabled","disabled");
                    if(deleteImageUrl){
                        salesModule.deletePic(deleteImageUrl, function (data) {
                      });
                    };
                    /* sent web request */
                    var userData = {
                        method: NATIVE_METHOD_GET_USER_INFO,
                        data: ""
                    };
                    common.jsCallNativeEventHandler(JS_CALL_NATIVE_EVENT_EXECUTE_NATIVE_METHOD, userData, function (data) {
                        var userInfo = JSON.parse(data.response);

                        var shop = {
                            shop_name: $(".name").val(),
                            shop_address: $(".address").val(),
                            contact_phone: $(".telephone").val(),
//                            industry_id :industryId,
                            pic_src_list:imgUrlArray,
                            company_account: userInfo.companyAccount,
                            contact_name: userInfo.username,
                        }
                        shopModule.addShop(shop, function (responseData) {
                            var httpResponse = JSON.parse(responseData.response);
                            if (responseData.ack == 1) {
                                if (httpResponse.retcode == 200) {
                                    $("#addShopPage .confirmBtn").removeAttr("disabled");
                                    common.backPageHistory();
                                    addImageResult ="";
                                }
                            }else if(httpResponse.retcode ==4026){
                                 $("#addShopPage .confirmBtn").removeAttr("disabled");
                                 $.toast("权限不足,添加店铺失败")
                            }else if(httpResponse.retcode ==4011){
                                 $("#addShopPage .confirmBtn").removeAttr("disabled");
                                 $.toast("店铺已存在，请更换店铺名")
                            }else{
                                $("#addShopPage .confirmBtn").removeAttr("disabled");
                                shopErroCode(httpResponse.retcode);
                            }
                        });
                    })
                }else{
                  $.toast('请输入正确的信息')
                }
                e.stopPropagation();
                e.preventDefault();
            });


                /* popup page for choice industry */
                $("#addShopPage").on('click','.choice',function(){
                    $.popup('.popup-industry');
                });

                $("#addShopPage").on('click','.industries',function(){
                    $('.industry').val($(this).text());
                    industryId =$(this).data('industryid');
                    $.closeModal('.popup-industry');
                });

                $("#addShopPage").on('click','#closeModal',function(){
                    $.closeModal('.popup-industry');
                });

                var industryData ={};
                var industryDataRequest = common.dataTransform("category/query_industrys", 'POST', industryData);
                 /*web request for industry array*/
                 common.jsCallNativeEventHandler(
                    JS_CALL_NATIVE_EVENT_REQUEST_WEB_API,
                    industryDataRequest,
                    function(data){
                       var respondData =data.response;
                       common.log('respondBody='+respondData);
                       respondData =JSON.parse(respondData);
                       var retBody = respondData.retbody.industrys,
                           template ='';
                       if(respondData.retcode ==200){
                            if(retBody && retBody.length> 0){
                               $.each(retBody,function(key,data){
                                  template +=$('#industryTemplate').html()
                                  .replace('{{industries}}',data.industry_desc)
                                  .replace('{{industryid}}',data.industry_id)
                               });
                               $('#industryArray').html(template);
                            }
                       }else{
                           common.alertTips("网络异常")
                       }
                    }
                 );

              $(document).off("click",".backPageBtn");
              $(document).on("click",".backPageBtn",function(){
                   $(document).off("click",".backPageBtn");
                   common.backPageHistory();
              });
        }

        /* edit shop page */
        function editShop(){
            var imgUrlArray = [],
                deleteImageUrl = '',
                industryId = '';

                $("#editShopPage input").val("");
                $('.showImgs,.deleteImgs').remove();
                $("#showShopImgs").html('<img src="{{imgSrc}}" class="showImgs"/><img src="../../images/icons/delete-day2.png" class="deleteImgs"/>')
                $('#shopImages').show();

            var shopDetailJsonStr = sessionStorage.getItem("shopDetail");
            common.log("获取缓存的需要修改的shop=" + shopDetailJsonStr);
            if (shopDetailJsonStr) {
                var shop = JSON.parse(shopDetailJsonStr);
                $("#editShopPage .name").val(shop.shop_name);
                $("#editShopPage .address").val(shop.shop_address);
                $("#editShopPage .telephone").val(shop.contact_phone);
                $("#editShopPage .industry").val(shop.shop_industry);
                industryId = shop.industry_id;
                if(shop.shop_img){
                    var template =$('#showShopImgs').html().replace('{{imgSrc}}',shop.shop_img);
                    $('#shopImages').hide();
                    $('.addShopImages').html(template);
                    imgUrlArray[0] = shop.shop_img;
                }
                 if(validateShopInfo()){
                     $('#saveShopBtns').addClass('isRightInput')
                 }else{
                     $('#saveShopBtns').removeClass('isRightInput')
                 };
            };

            /* operation of image */
            $(document).off('click','.deleteImgs');
            $(document).on('click','.deleteImgs',function(){
                 imgUrlArray = [];
                 deleteImageUrl = $(".showImgs").attr("src");
                 $('.showImgs,.deleteImgs').remove();
                 $("#showShopImgs").html('<img src="{{imgSrc}}" class="showImgs"/><img src="../../images/icons/delete-day2.png" class="deleteImgs"/>')
                 $("#shopImages").show();
                 editImageResult="";
            });

            $(document).off('click','#shopImages');
            $(document).on('click','#shopImages',function(){
                editImg =true;
                var userData = {
                    method: NATIVE_METHOD_GET_USER_INFO,
                    data: ""
                };
                common.jsCallNativeEventHandler(JS_CALL_NATIVE_EVENT_EXECUTE_NATIVE_METHOD, userData, function (data) {
                    var userInfo = JSON.parse(data.response);
                    var shopId = parseInt(userInfo.shopID);
                    var productEvent = {
                        shopID: shopId
                    };
                    var imageUploadPageEvent = {
                        method: NATIVE_PAGE_IMAGE_UPLOAD,
                        data: JSON.stringify(productEvent)
                    };
                    common.jsCallNativeEventHandler(JS_CALL_NATIVE_EVENT_REQUEST_SWITCH_PAGE, imageUploadPageEvent, function (data) {
                        common.log("打开图片上传页面完成！");
                    });
                });
            });

            /* form validate */
           $("#editShopPage").on('input','#editShopPage .name,#editShopPage .address,#editShopPage .telephone',function(){
               if($(this)[0] == document.querySelector('#editShopPage .telephone')){
                   if (!common.regTel($(this).val())) {
                       $(this).parent(".login_input").addClass("errorAdd");
                   } else {
                       $(".telephone").parent(".login_input").removeClass("errorAdd");
                   }
               }else{
                   if (!common.regName($(this).val())) {
                       $(this).parent(".login_input").addClass("errorAdd");
                   } else {
                       $(this).parent(".login_input").removeClass("errorAdd");
                   }
               }

               if(validateShopInfo()){
                   $('#editShopPage #saveShopBtns').addClass('isRightInput')
               }else{
                   $('#editShopPage #saveShopBtns').removeClass('isRightInput')
               }
           });

           $("#editShopPage").on('blur','#editShopPage .name,#editShopPage .address,#editShopPage .telephone',function(){
              if($(this)[0] == document.querySelector('#editShopPage .telephone')){
                  if (!common.regTel($(this).val())) {
                      $(this).parent(".login_input").addClass("errorAdd");
                      $.toast("电话请输入数字",1300,'toastInfo');
                  } else {
                      $(".telephone").parent(".login_input").removeClass("errorAdd");
                  }
              }else{
                  if (!common.regName($(this).val())) {
                      $(this).parent(".login_input").addClass("errorAdd");
                      $.toast("姓名或地址请输入中英文数字或下划线",1300,'toastInfo');
                  } else {
                      $(this).parent(".login_input").removeClass("errorAdd");
                  }
              }

           });


            function validateShopInfo() {
                var shop_name = $("#editShopPage .name").val(),
                    shop_address = $("#editShopPage .address").val(),
                    contact_phone = $("#editShopPage .telephone").val(),
                    industry = $('#editShopPage .industry').val();

                var validateFlag = true;
                if (!common.regName(shop_name)) {
                    validateFlag = false;
                }
                if (!common.regAddress(shop_address)) {
                    validateFlag = false;
                }
                if (!common.regTel(contact_phone)) {
                    validateFlag = false;
                }
//                if (common.isNullString(industry)){
//                    validateFlag = false;
//                }
                if (validateFlag) {
                    return true;
                } else {
                    return false;
                }
            }

            function shopErroCode(code) {
                if (code == '4000') {
                    $.toast('服务器连接失败', 2000, 'error');
                } else if (code == '4001') {
                    $.toast('店铺已存在', 2000, 'error');
                } else if (code == '4019') {
                    $.toast('店铺不存在', 2000, 'error');
                } else {
                    // 网络异常
                    $.toast('网络异常', 2000, 'error');
                }
            }

             $("#editShopPage .confirmBtn").off("click");
             $("#editShopPage .confirmBtn").on("click",function (e) {
                if (validateShopInfo()) {
                    $(this).attr("disabled","disabled");
                    /* delete img in server */
                    if(deleteImageUrl){
                        salesModule.deletePic(deleteImageUrl, function (data) {
                      });
                    };
                    /* sent web request */
                    var userData = {
                        method: NATIVE_METHOD_GET_USER_INFO,
                        data: ""
                    };
                    common.jsCallNativeEventHandler(JS_CALL_NATIVE_EVENT_EXECUTE_NATIVE_METHOD, userData, function (data) {
                        var userInfo = JSON.parse(data.response);
                            var shopDetailJsonStr = sessionStorage.getItem("shopDetail");
                            var oldShopData = JSON.parse(shopDetailJsonStr);

                            if(editImageResult != ""){
                                imgUrlArray[0] = editImageResult
                            }
                            var shop = {
                                "shop_name": $("#editShopPage .name").val(),
                                "shop_address": $("#editShopPage .address").val(),
                                "contact_phone": $("#editShopPage .telephone").val(),
                                "shop_industry": $("#editShopPage .industry").val(),
                                "pic_src_list":imgUrlArray,
//                                "industry_id" :industryId,
                                "company_id": userInfo.companyAccount,
                                "contact_name": userInfo.username,
                                "shop_id": oldShopData.shop_id
                            }

                            var  shopDetail = {
                                "shop_address":$("#editShopPage .address").val(),
                                "shop_id":oldShopData.shop_id,
                                "contact_phone":$("#editShopPage .telephone").val(),
                                "shop_name": $("#editShopPage .name").val(),
                                "shop_industry": $("#editShopPage .industry").val(),
                                "industry_id":industryId,
                                "shop_img":editImageResult ? editImageResult : imgUrlArray[0]
                            }
                            shopModule.updateShop(shop, function (responseData) {
                                var httpResponse = JSON.parse(responseData.response);
                                if (responseData.ack == 1 && httpResponse.retcode == 200) {
                                     $("#editShopPage .confirmBtn").removeAttr("disabled");
                                     sessionStorage.setItem("shopDetail", JSON.stringify(shopDetail));
                                     editImageResult = "";
                                     common.backPageHistory();
                                }else if(httpResponse.retcode ==4026){
                                    $("#editShopPage .confirmBtn").removeAttr("disabled");
                                    $.toast("权限不足,修改店铺失败")
                                }else if(httpResponse.retcode ==4011){
                                    $("#editShopPage .confirmBtn").removeAttr("disabled");
                                    $.toast("店铺名已存在，请更改店铺名")
                                }else {
                                    $("#editShopPage .confirmBtn").removeAttr("disabled");
                                    common.log("返回错误, httpResponse.retcode=" + httpResponse.retcode);
                                    shopErroCode(httpResponse.retcode);
                                }
                            });
                    })
                }else{
                  $.toast("请输入正确的信息")
                }
                e.stopPropagation();
                e.preventDefault();
            });


            /* popup page for choice industry */
            $("#editShopPage").on('click','#editShopPage .choice',function(){
                $.popup('.popup-industry');
            });

            $("#editShopPage").on('click','#editShopPage .industries',function(){
                $('.industry').val($(this).text());
                industryId =$(this).data('industryid');
                $.closeModal('.popup-industry');
            });

            $("#editShopPage").on('click','#closeModal',function(){
                $.closeModal('.popup-industry');
            });

            var industryData ={};
            var industryDataRequest = common.dataTransform("category/query_industrys", 'POST', industryData);
             /*web request for industry array*/
             common.jsCallNativeEventHandler(
                JS_CALL_NATIVE_EVENT_REQUEST_WEB_API,
                industryDataRequest,
                function(data){
                   var respondData =data.response;
                   common.log('respondBody='+respondData);
                   respondData =JSON.parse(respondData);
                   var retBody = respondData.retbody.industrys,
                       template ='';
                   if(respondData.retcode ==200){
                        if(retBody && retBody.length> 0){
                           $.each(retBody,function(key,data){
                              template +=$('#industryTemplates').html()
                              .replace('{{industries}}',data.industry_desc)
                              .replace('{{industryid}}',data.industry_id)
                           });
                           $('#industryArrays').html(template);
                        }
                   }else{
                       common.alertTips("网络异常")
                   }
                }
             );

             $(document).off("click",".backPageBtn");
               $(document).on("click",".backPageBtn",function(){
                    common.backPageHistory();
             });
        }

        $(document).on("pageInit", "#shopListPage", function () {
            shopList();
        });

        /* SPA */
        $(document).on("pageAnimationStart","#shopListPage,#shopDetailPage,#addShopPage,#editShopPage",function(){
            shopDetail();
            addShop();
            editShop();
        });
        $.init();
    });
})();