/**
 * (c) Copyright 2016 Administrator. All Rights Reserved.
 * 2016-05-19
 *
 */
(function () {
    require(['zepto', 'common', 'empModule', 'shopModule','salesModule', 'sha256'], function ($, common, empModule, shopModule,salesModule, sha256) {

        var isNative = true;
            addImg = false,
            editImg = false,
            addImageResult ="",
            editImageResult="";

        /* account list */
        function accountList(){
            var accountList ="";
            common.connectJsTunnel(function (jsTunnel) {
               /* core */
               if(isNative){
                jsTunnel.init();
               }
               isNative =false;
               /* core */
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
                    var userInfo = JSON.parse(data.response), companyAccount = userInfo.companyAccount;
                    if(userInfo.role ==0){
                        $("#menu").css("visibility","visible")
                    }else{
                         $("#menu").css("visibility","hidden")
                    }
                    /* record user information*/
                    sessionStorage.setItem("userInformation",data.response);
                    empModule.queryEmployeeList(companyAccount, function (accountData) {
                        var httpResponse = JSON.parse(accountData.response);
                        if (accountData.ack == 1){
                        var tempHtml ='<div class="item-content" style="padding-left:1rem;border-bottom: 1px solid #ccc;">'
                                      + '<div class="item-inner">'
                                      +'<div class="item-title totalLeftTime" style="font-size:0.8rem">'+'剩余总月数：'+httpResponse.retbody.totalTime+'</div>'
                                      +'</div>'
                                   +'</div>';
                            accountList = httpResponse.retbody.userList;
                            console.log("用户集合" + JSON.stringify(accountList));
                            if(accountList && accountList.length > 0) {
                                $.each(accountList, function (i, account) {
                                    var roleName = "";
                                    if (account.role_id == 0) {
                                        roleName = "老板";
                                    } else if (account.role_id == 1) {
                                        roleName = "店员"
                                    } else{
                                        roleName = "店员"
                                    }
                                     tempHtml += $("#accountListTemplate").html()
                                        .replace("{{employee_name}}", account.employee_name)
                                        .replace("{{roleName}}", roleName)
                                        .replace("{{employeeArraySequence}}",i)
                                        .replace("../../images/icons/employee.png",((account.pic_src)[0]?(account.pic_src)[0]:"../../images/icons/employee.png"))
                                        .replace("{{leftTime}}",account.residue_date[0]+"个月"+account.residue_date[2]+"天")
                                        .replace("{{expireTime}}",account.expire_date)
                                });
                                $("#box").html(tempHtml);
                                /* save total time */
                               sessionStorage.setItem("totalTime",httpResponse.retbody.totalTime);
                            } else {
                                $(".noDataView").show();
                            }
                        }else{
                            $.toast('服务器连接失败', 2000, 'error');
                        }
                    });
                });
            });

            $(document).off("click", ".account-detail-li");
               $(document).on("click", ".account-detail-li", function () {
                   $(document).off("click", ".account-detail-li");
                   var employeeArraySequence = $(this).find(".employeeArraySequence").val();
                   var employeeArraySequenceInt = parseInt(employeeArraySequence);
                   common.log("缓存员工信息:" + JSON.stringify(accountList[employeeArraySequenceInt]));
                   sessionStorage.setItem("accountDetail", JSON.stringify(accountList[employeeArraySequenceInt]));
                   $.router.load("#account_detail_page");
               });

            $(document).off("click", "#addAccountBtn");
            $(document).on("click", "#addAccountBtn", function () {
                  $(document).off("click", "#addAccountBtn");
                  $.router.load('#addUserPage');
            });

            $(document).off("click",".backPageBtns");
            $(document).on("click",".backPageBtns",function(){
                $(document).off("click",".backPageBtns");
                common.backHomePage();
            });

        };

        /* add account */
        function addAccount(){
            var employeeRole = 1,
                shopId = '',
                roleTrue = false,
                imgUrlArray = [],
                deleteImageUrl = '';

             $("#addUserPage input").val("");
             $("#addUserPage .roleText").val("店员");
             $('#saveShopBtn').removeClass('isRightInput');
             $('.showImg,.deleteImg').remove();
             $("#showUserImg").html('<img src="{{imgSrc}}" class="showImg"/><img src="../../images/icons/delete-day2.png" class="deleteImg"/>')
             $('#userImage').show();


             /* get  user information */
              var  userInformation = sessionStorage.getItem("userInformation");
              console.log("获取到的店铺的信息="+userInformation);
              if(userInformation){
                 var userInfo = JSON.parse(userInformation);
              };

             /* popup page for choice role */
//                $(document).off('click','.choice');
//                $(document).on('click','.choice',function(){
//                    $.popup('.popup-role');
//                });
//
//                $(document).off('click','.role');
//                $(document).on('click','.role',function(){
//                    $('.roleText').val($(this).text());
//                    roleTrue =true;
//                    if($(this).val() === "老板"){
//                        employeeRole = 0;
//                    }else{
//                        employeeRole = 1;
//                    }
//                    $.closeModal('.popup-role');
//                });
//
//
//                $(document).off('click',"#closeModal");
//                $(document).on('click','#closeModal',function(){
//                    $.closeModal('.popup-role');
//                });

                /* validate input */
                $(document).off('input','#addUserPage .name,#addUserPage .password,#addUserPage .telephone,#addUserPage .roleText');
                $(document).on('input','#addUserPage .name,#addUserPage .password,#addUserPage .telephone,#addUserPage .roleText',function(){
                       if($(this)[0] == document.querySelector('#addUserPage .telephone')){
                           if (!common.regTel($(this).val())) {
                               $(this).parent(".login_input").addClass("errorAdd");
                           } else {
                               $(".telephone").parent(".login_input").removeClass("errorAdd");
                           }
                       }else if($(this)[0] == document.querySelector('#addUserPage .name')){
                           if (!common.regName($(this).val())) {
                               $(this).parent(".login_input").addClass("errorAdd");
                           } else {
                               $(this).parent(".login_input").removeClass("errorAdd");
                           }
                       }else if($(this)[0] == document.querySelector('#addUserPage .password')){
                           if (!common.regUserPassword($(this).val())) {
                              $(this).parent(".login_input").addClass("errorAdd");
                           } else {
                              $(this).parent(".login_input").removeClass("errorAdd");
                           }
                       }

                       if(validateUserInfo()){
                           $('#saveUserBtn').addClass('isRightInput')
                       }else{
                           $('#saveUserBtn').removeClass('isRightInput')
                       }
                });

                $(document).off('blur','#addUserPage .name,#addUserPage .password,#addUserPage .telephone,#addUserPage .roleText');
                $(document).on('blur','#addUserPage .name,#addUserPage .password,#addUserPage .telephone,#addUserPage .roleText',function(){
                       if($(this)[0] == document.querySelector('#addUserPage .telephone')){
                           if (!common.regTel($(this).val())) {
                               $(this).parent(".login_input").addClass("errorAdd");
                               $.toast("电话请输入数字",1300,'toastInfo');
                           } else {
                               $(".telephone").parent(".login_input").removeClass("errorAdd");
                           }
                       }else if($(this)[0] == document.querySelector('#addUserPage .name')){
                           if (!common.regName($(this).val())) {
                               $(this).parent(".login_input").addClass("errorAdd");
                               $.toast("用户名请输入中英文数字或下划线",1300,'toastInfo')
                           } else {
                               $(this).parent(".login_input").removeClass("errorAdd");
                           }
                       }else if($(this)[0] == document.querySelector('#addUserPage .password')){
                           if (!common.regUserPassword($(this).val())) {
                              $(this).parent(".login_input").addClass("errorAdd");
                              $.toast("密码请输入英文数字或下划线",1300,'toastInfo')
                           } else {
                              $(this).parent(".login_input").removeClass("errorAdd");
                           }
                       }
                });

              function validateUserInfo() {
                 var name = $("#addUserPage .name").val(),
                     password = $("#addUserPage .password").val(),
                     contact_phone = $("#addUserPage .telephone").val(),
                     roleText = $('#addUserPage .roleText').val();

                 var validateFlag = true;
                 if (!common.regName(name)) {
                     validateFlag = false;
                 }
                 if (!common.regUserPassword(password)) {
                     validateFlag = false;
                 }
                 if (!common.regTel(contact_phone)) {
                     validateFlag = false;
                 }
                 if (common.isNullString(roleText)){
                     validateFlag = false;
                 }
                 if (validateFlag) {
                     return true;
                 } else {
                     return false;
                 }
              }

             $(document).off("click","#userImage");
             $(document).on('click','#userImage',function(){
                 addImg =true;
                 var userData = {
                     method: NATIVE_METHOD_GET_USER_INFO,
                     data: ""
                 };
                 common.jsCallNativeEventHandler(JS_CALL_NATIVE_EVENT_EXECUTE_NATIVE_METHOD,userData,function (data){
                     var userInfo = JSON.parse(data.response);
                     var shopId = parseInt(userInfo.shopID);
                     var productEvent = {
                         shopID: shopId
                     };
                     var imageUploadPageEvent = {
                         method: NATIVE_PAGE_IMAGE_UPLOAD,
                         data: JSON.stringify(productEvent)
                     };
                     common.jsCallNativeEventHandler(JS_CALL_NATIVE_EVENT_REQUEST_SWITCH_PAGE, imageUploadPageEvent, function(data){
                     });
                 });
             });

             common.connectJsTunnel(function (jsTunnel) {
                /* upload image feedback */
                jsTunnel.registerHandler(
                    NATIVE_CALL_JS_EVENT_PRODUCT_IMAGE_UPLOAD_RESULT,
                    function(data, responseCallback){
                       var imgUrl = data.imgURL;
                       if(addImg){
                          addImageResult = imgUrl;
                          imgUrlArray[0] = addImageResult;
                           var template = $('#showUserImg').html().replace('{{imgSrc}}',addImageResult);
                                $('#userImage').hide();
                                $('.addUserImage').html(template);
                                addImg =false;
                       }else if(editImg){
                          editImageResult = imgUrl;
                          var template =$('#showUserImgs').html().replace('{{imgSrc}}',editImageResult);
                                 $('#userImages').hide();
                                 $('.addUserImages').html(template);
                           editImg = false;
                       }
                    }
                );
             });


             /* delete image */
             $(document).on('click','.deleteImg',function(){
                 imgUrlArray = [];
                 deleteImageUrl = $(".showImg").attr("src");
                 $('.showImg,.deleteImg').remove();
                 $("#showUserImg").html('<img src="{{imgSrc}}" class="showImg"/><img src="../../images/icons/delete-day2.png" class="deleteImg"/>')
                 $('#userImage').show();
                 addImageResult ="";
             });

             /* save the new user information */
             $("#addUserPage .confirmBtn").off("click");
             $("#addUserPage .confirmBtn").on("click",function(){
                if(validateUserInfo()){
                    $(this).attr("disabled","disabled");
                     common.connectJsTunnel(function (jsTunnel) {
                         var account = {
                             company_account: userInfo.companyAccount,
                             login_name: $(".name").val(),
                             password: sha256($(".password").val()),
                             role_id: employeeRole,
                             shop_id: userInfo.shopID,
                             employee_phone: $(".telephone").val(),
                             pic_src_list:imgUrlArray,
                         }
                         common.log("保存用户account=" + JSON.stringify(account));
                         empModule.addEmp(account, function (data) {
                             var httpResponse = JSON.parse(data.response);
                             if (data.ack == 1 && httpResponse.retcode == "200"){
                                    $("#addUserPage .confirmBtn").removeAttr("disabled");
                                    common.backPageHistory();
                                    addImageResult ="";
                             }else if(httpResponse.retcode == 4012){
                                 $("#addUserPage .confirmBtn").removeAttr("disabled");
                                 $.toast("用户已存在")
                             }else if(httpResponse.retcode == 4026){
                                 $("#addUserPage .confirmBtn").removeAttr("disabled");
                                 $.toast("权限不足，无法添加")
                             }else if(httpResponse.retcode == 4024){
                                 $("#addUserPage .confirmBtn").removeAttr("disabled");
                                 $.toast("老板已存在，请添加店员")
                             }else{
                                 $("#addUserPage .confirmBtn").removeAttr("disabled");
                                 $.toast("网络异常");
                             }
                         });
                     });
                }else{
                   $.toast("请输入正确信息")
                }
             });

             $(document).off("click",".backPageBtn");
             $(document).on("click",".backPageBtn",function(){
                 $(document).off("click",".backPageBtn");
                 common.backPageHistory();
             });
        };

        /* account detail */
        function accountDetail(){
            var accountDetail = sessionStorage.getItem("accountDetail");
            var userTotalTime = parseInt(sessionStorage.getItem("totalTime"));
            var userOldMouth = "";
            $(".leftMouths").css("borderColor","buttonface");
            $("#setUserTime").css("display","block");
            $("#delete").css("opacity",1);
            common.log("打开员工详情accountDetail = " + accountDetail);
            if (accountDetail) {
                var account = JSON.parse(accountDetail);
                userOldMouth = parseInt(account.residue_date[0]);
                console.log("用户的老的时间="+userOldMouth);
                if (account.role_id == 0) {
                    roleName = "老板";
                    $("#setUserTime").css("display","none");
                } else if (account.role_id == 1) {
                    roleName = "店员";
                }else {
                    roleName = "店员";
                }

                $(".detail-username").text(account.employee_name);
                $("#roleName").text(roleName);
                $("#phone").text(account.employee_phone);
                $("#login_name").text(account.login_name);
                $(".userLeftTime").text("剩余："+account.residue_date[0]+"个月"+account.residue_date[2]+"天");
                $(".userExpireTime").text("到期时间："+account.expire_date);
                $("#totalTime").text(userTotalTime);
                $(".leftMouths").val(account.residue_date[0]);
                $(".leftDays").text(account.residue_date[2]+"天");

                if((account.pic_hd_src).length>0){
                    $('.employeeImage').attr("src",(account.pic_hd_src)[0]);
                }else{
                    $('.employeeImage').attr("src",'../../images/icons/employee.png');
                }
            }
             !function(){
                $(".moreBtn").on("click",function(e){
                       $('.more').toggleClass("toggleCss");
                       e.cancelBubble = true;
                });

                $("#account_detail_page").not($(".moreBtn")).click(function(){
                       $('.more').removeClass("toggleCss")
                });
                common.deletePopup();
             }();

            var userInfo = {};
            var userData = {
                method: NATIVE_METHOD_GET_USER_INFO,
                data: ""
            };
            common.connectJsTunnel(function (jsTunnel) {
                common.jsCallNativeEventHandler(JS_CALL_NATIVE_EVENT_EXECUTE_NATIVE_METHOD, userData, function (data) {
                    userInfo = JSON.parse(data.response), companyAccount = userInfo.companyAccount;
                    console.log("用户拿到的数据="+data.response);
                    if(userInfo.role ==1){
                         $("#setUserTime").css("display","none");
                    }
                    if (userInfo.loginName == account.login_name) {
                        //如果是老板自己,则删除功能对自己隐藏,防止误操作
                        $("#delete").css("opacity",0);
                        $('#delete').off("click");
                    }
                });
            });

            // 删除用户
            $(document).off("click",".deleteBtn");
            $(document).on("click", ".deleteBtn", function () {
                var deleteEmployee = {
                    employee_id: parseInt(JSON.parse(accountDetail).employee_id)
                }
                var data = common.dataTransform('employee/delete', 'POST', deleteEmployee);
                common.jsCallNativeEventHandler(JS_CALL_NATIVE_EVENT_REQUEST_WEB_API, data, function (responseData) {
                    common.log("删除用户请求结束:" + JSON.stringify(responseData));
                    var jsonData = JSON.parse(responseData.response);
                    if (responseData.ack == 1) {
                        window.history.go(-1);
                    }else if(jsonData.retcode ==4026){
                         $.toast("权限不足,删除用户失败");
                    }else {
                        $.toast("网络异常");
                    }
                });
            });

            /* edit password */
            $(document).off("click", "#updateCust");
            $(document).on("click", "#updateCust", function (){
                $(document).off("click", "#updateCust");
                $.router.load("#editUserPage");
            });

             $(document).off("click",".backPageBtn");
             $(document).on("click",".backPageBtn",function(){
                 $(document).off("click",".backPageBtn");
                 common.backPageHistory();
             });

             /* set User time */
             ~function(){
                 $(document).on("click","#setUserTime",function(){
                     $.popup(".popup-setTime");
                 });

                 $(document).on("click",".confirmSet",function(){
                     $.closeModal(".popup-setTime");
                 });

                 $(document).off("click",".minus");
                 $(document).on("click",".minus",function(){
                    var leftMouths = parseInt($(".leftMouths").val());
                    var totalLeftTime = parseInt($("#totalTime").text());
                     if(leftMouths && (leftMouths >1) ){
                        leftMouths = leftMouths -1;
                        totalLeftTime = totalLeftTime +1;
                        $(".leftMouths").val(leftMouths);
                        $("#totalTime").text(totalLeftTime);
                        $(".leftMouths").css("borderColor","buttonface")
                     }
                 });

                 $(document).off("click",".plus");
                 $(document).on("click",".plus",function(){
                   var leftMouths = parseInt($(".leftMouths").val());
                   var totalLeftTime = parseInt($("#totalTime").text());
                     if((leftMouths >= 0)&& (totalLeftTime > 1)){
                        leftMouths++;
                        totalLeftTime = totalLeftTime -1;
                        $(".leftMouths").val(leftMouths);
                         $("#totalTime").text(totalLeftTime);
                        $(".leftMouths").css("borderColor","buttonface")
                     }
                 });

                 /* save edit user time */
                 $(document).off("click",".confirmSet")
                 $(document).on("click",".confirmSet",function(){
                    if($(".leftMouths").css("borderColor") != "red" ){
                         var editEmployeeTime = {
                             employee_id: parseInt(account.employee_id),
                             month_num : parseInt(parseInt($(".leftMouths").val()) - userOldMouth)
                         }
                         var data = common.dataTransform('expiredays/modify', 'POST', editEmployeeTime);
                         common.jsCallNativeEventHandler(JS_CALL_NATIVE_EVENT_REQUEST_WEB_API, data, function (responseData) {
                             common.log("修改用户时长请求结束:" + JSON.stringify(responseData));
                             var jsonData = JSON.parse(responseData.response),
                                 retBody = (jsonData.retbody)[0];
                             if(responseData.ack == 1 && jsonData.retcode == 200) {
                                   $(".userLeftTime").text("剩余："+(retBody.residue_date)[0]+"个月"+(retBody.residue_date)[2]+"天");
                                   $(".userExpireTime").text("到期时间："+retBody.expire_date);
//                                   var accountDetail = sessionStorage.getItem("accountDetail");
//                                   var account = JSON.parse(accountDetail);
                                   userTotalTime = parseInt($("#totalTime").text());
                                   account.residue_date = retBody.residue_date;
                                   account.expire_date = retBody.expire_date;
                                   sessionStorage.setItem("totalTime",userTotalTime);
                                   sessionStorage.setItem("accountDetail",JSON.stringify(account));
                                   userOldMouth = parseInt((retBody.residue_date)[0]);
                                   $.toast("设置月份成功",1300);
                                   $.closeModal(".popup-setTime");
                             }else if(jsonData.retcode == 4037){
                                   $.toast("设置月份不能超出总月数",1300);
                             }else {
                                 $.toast("网络异常");
                             }
                         });
                     }else{
                        $.toast("请输入正确月数")
                     }
                 });


                 /* time link */
                 $(document).on("input",".leftMouths",function(){
                       var totalTime ="";
                       if(common.regNumber($(this).val())){
                           totalTime =  userTotalTime + parseInt(userOldMouth - parseInt($(this).val()));
                           if((parseInt($(this).val()) < userOldMouth + userTotalTime) && (totalTime >0)){
                               $("#totalTime").text(totalTime);
                               $(".leftMouths").css("borderColor","buttonface");
                            }else if(totalTime > userTotalTime){
                               $("#totalTime").text(userTotalTime);
                               $(this).val(userOldMouth);
                            }else{
                               $(this).val(userOldMouth);
                               $("#totalTime").text(userTotalTime);
                            }
                       }else{
                          $(".leftMouths").css("borderColor","red")
                          $("#totalTime").text(userTotalTime);
                       }
                  });

             }();

             /* cancel setting time */
             $(document).on("click",".cancelSet",function(){
                  $.closeModal(".popup-setTime");
                  $("#totalTime").text(userTotalTime);
                  $(".leftMouths").val(account.residue_date[0]);
                  $(".leftDays").text(account.residue_date[2]+"天");
             });
        };

        /* edit account */
        function editAccount(){
               var imgUrlArray = [],
                   deleteImageUrl = '',
                   industryId = '',
                   employeeRole ="",
                   loginUser = "",
                   EmployeeInfo ={},
                   userShopId = "",
                   useOldShopId ="";

                 $("#editUserPage input").val("");
                 $('.showImgs,.deleteImgs').remove();
                 $('#saveUserBtns').removeClass('isRightInput');
                 $("#showUserImgs").html('<img src="{{imgSrc}}" class="showImgs"/><img src="../../images/icons/delete-day2.png" class="deleteImgs"/>')
                 $('#userImages').show();
                 $(".changeShop").show();

                /* validate input */
                $(document).off('input','#editUserPage .name,#editUserPage .password,#editUserPage .telephone,#editUserPage .roleTexts');
                $(document).on('input','#editUserPage .name,#editUserPage .password,#editUserPage .telephone, #editUserPage .roleTexts',function(){
                       if($(this)[0] == document.querySelector('#editUserPage .telephone')){
                           if (!common.regTel($(this).val())) {
                               $(this).parent(".login_input").addClass("errorAdd");
                           } else {
                               $(".telephone").parent(".login_input").removeClass("errorAdd");
                           }
                       }else if($(this)[0] == document.querySelector('#editUserPage .name')){
                           if (!common.regName($(this).val())) {
                               $(this).parent(".login_input").addClass("errorAdd");
                           } else {
                               $(this).parent(".login_input").removeClass("errorAdd");
                           }
                       }else if($(this)[0] == document.querySelector('#editUserPage .password')){
                           if (!common.regUserPassword($(this).val())) {
                              $(this).parent(".login_input").addClass("errorAdd");
                           } else {
                              $(this).parent(".login_input").removeClass("errorAdd");
                           }
                       }

                       if(validateUserInfo()){
                           $('#saveUserBtns').addClass('isRightInput')
                       }else{
                           $('#saveUserBtns').removeClass('isRightInput')
                       }
                });

                $(document).off('blur','#editUserPage .name,#editUserPage .password,#editUserPage .telephone,#editUserPage .roleTexts');
                $(document).on('blur','#editUserPage .name,#editUserPage .password,#editUserPage .telephone, #editUserPage .roleTexts',function(){
                       if($(this)[0] == document.querySelector('#editUserPage .telephone')){
                           if (!common.regTel($(this).val())) {
                               $(this).parent(".login_input").addClass("errorAdd");
                               $.toast("电话请输入数字",1300,'toastInfo');
                           } else {
                               $(".telephone").parent(".login_input").removeClass("errorAdd");
                           }
                       }else if($(this)[0] == document.querySelector('#editUserPage .name')){
                           if (!common.regName($(this).val())) {
                               $(this).parent(".login_input").addClass("errorAdd");
                               $.toast("用户名请输入中英文数字或下划线",1300,'toastInfo')
                           } else {
                               $(this).parent(".login_input").removeClass("errorAdd");
                           }
                       }else if($(this)[0] == document.querySelector('#editUserPage .password')){
                           if (!common.regUserPassword($(this).val())) {
                              $(this).parent(".login_input").addClass("errorAdd");
                              $.toast("密码请输入英文数字或下划线",1300,'toastInfo')
                           } else {
                              $(this).parent(".login_input").removeClass("errorAdd");
                           }
                       }
                });


                $(document).off('click','.deleteImgs');
                $(document).on('click','.deleteImgs',function(){
                     imgUrlArray = [];
                     deleteImageUrl = $(".showImgs").attr("src");
                     $('.showImgs,.deleteImgs').remove();
                     $("#showUserImgs").html('<img src="{{imgSrc}}" class="showImgs"/><img src="../../images/icons/delete-day2.png" class="deleteImgs"/>')
                     $("#userImages").show();
                     editImageResult="";
                });

                 /* show employee information */
                   var getEmployee = sessionStorage.getItem("accountDetail");
                   if (getEmployee) {
                       var user = JSON.parse(getEmployee),
                           useOldShopId = user.shop_id;
                           console.log("店员的店铺老Id="+useOldShopId);
                       $("#editUserPage .name").val(user.login_name);
                       $("#editUserPage .password").val("");
                       $("#editUserPage .telephone").val(user.employee_phone);

                       employeeRole = user.role_id;
                       /* judge the role */
                       if(user.role_id == 0){
                            $("#editUserPage .roleTexts").val("老板");
                       }else{
                            $("#editUserPage .roleTexts").val("员工");
                       }
                       /* show image */
                       if((user.pic_hd_src).length > 0){
                           var template =$('#showUserImgs').html().replace('{{imgSrc}}',(user.pic_hd_src)[0]);
                           $('#userImages').hide();
                           $('.addUserImages').html(template);
                           imgUrlArray[0] = (user.pic_hd_src)[0];
                       }
                       /* judge the user if it is himself */
                       console.log("获取到的用户名"+user.login_name);
                       console.log("当前的用户名="+loginUser);
                       if(user.login_name == loginUser ){
                           $(document).off('click','.choices');
                       }else{
                            $(document).on('click','.choices',function(){
                               $.popup('.popup-roles');
                            });
                       }

                       if(validateUserInfo()){
                            $('#saveShopBtns').addClass('isRightInput')
                       }else{
                            $('#saveShopBtns').removeClass('isRightInput')
                       };
                   };


                   var userData = {
                       method: NATIVE_METHOD_GET_USER_INFO,
                       data: ""
                   };
                   common.jsCallNativeEventHandler(JS_CALL_NATIVE_EVENT_EXECUTE_NATIVE_METHOD, userData, function (data) {
                       console.log("用户的信息="+data.response);
                       var userInfo = JSON.parse(data.response);
                       if(userInfo.role != 0){
                             $(".changeShop").hide();
                       }
                       var shopListRequest ={
                            "company_account":userInfo.companyAccount
                       },tempHtml ="";
                         var shopListRequestData =common.dataTransform('shop/query','POST',shopListRequest);
                            common.jsCallNativeEventHandler(
                               JS_CALL_NATIVE_EVENT_REQUEST_WEB_API,
                               shopListRequestData,
                               function(data){
                                  var respondData =data.response;
                                  respondData =JSON.parse(respondData);
                                  if(respondData.retcode ==200){
                                    shopInfoArray = respondData.retbody;
                                    $.each(respondData.retbody,function(key,dataInfo){
                                       tempHtml += $("#shopsTemplate").html()
                                                   .replace("{{shopId}}",dataInfo.shop_id)
                                                   .replace("{{shopName}}",dataInfo.shop_name);

                                       if(useOldShopId == dataInfo.shop_id){
                                             $('.changeShop').text(dataInfo.shop_name);
                                       }
                                    });
                                     $("#shopArrays").html(tempHtml);
                                  }else{
                                       $.toast("网络异常");
                                  }
                               }
                            )

                       loginUser = userInfo.loginName;
                        if(getEmployee){
                            EmployeeInfo = JSON.parse(getEmployee)
                        }else{
                            EmployeeInfo = {};
                        }

                     $.each(shopInfoArray,function(key,data){
                         if(useOldShopId == data.shop_id){
                              $('.changeShop').text(data.shop_name);
                              return;
                         }
                     })

                     $(document).off('click','#userImages');
                     $(document).on('click','#userImages',function(){
                       editImg =true;

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



            /* popup page for choice role */
//            $(document).off('click','.choices');
//            $(document).on('click','.choices',function(){
//                $.popup('.popup-roles');
//            });
//
//            $(document).off('click','.roles');
//            $(document).on('click','.roles',function(){
//                $('#editUserPage .roleTexts').val($(this).text());
//                if($(this).text() === "老板"){
//                    employeeRole = 0;
//                }else{
//                    employeeRole = 1;
//                }
//                $.closeModal('.popup-roles');
//            });
//
//            $(document).off('click',"#closeModals");
//            $(document).on('click','#closeModals',function(){
//                $.closeModal('.popup-roles');
//            });

            /* popup page for choice shop*/
            $(document).off("click",".changeShop");
            $(document).on("click",".changeShop",function(){
                $.popup('.popup-shops');
            });

            $(document).off("click","#shopModel");
            $(document).on("click","#shopModel",function(){
               $.closeModal(".popup-shops");
            });

            $(document).off('click',".shops");
            $(document).on('click','.shops',function(){
                $('.changeShop').text($(this).text());
                userShopId = $(this).data("shopid");
                $.closeModal('.popup-shops');
            });



              function validateUserInfo() {
                 var name = $("#editUserPage .name").val(),
                     password = $("#editUserPage .password").val(),
                     contact_phone = $("#editUserPage .telephone").val(),
                     roleText = $('#editUserPage .roleTexts').val();

                 var validateFlag = true;
                 if (!common.regName(name)) {
                     validateFlag = false;
                 }
                 if (!common.regUserPassword(password)) {
                     validateFlag = false;
                 }
                 if (!common.regTel(contact_phone)) {
                     validateFlag = false;
                 }
                 if (common.isNullString(roleText)){
                     validateFlag = false;
                 }

                 if (validateFlag) {
                     return true;
                 } else {
                     return false;
                 }
              }

              /* get  user information */
             var  userInformation = sessionStorage.getItem("userInformation");
             console.log("获取到的店铺的信息="+userInformation);
             if(userInformation){
                 var userInfo = JSON.parse(userInformation);
             };


            $("#editUserPage .confirmBtn").off("click");
            $("#editUserPage .confirmBtn").on("click",function(){
               if(validateUserInfo()){
                 $(this).attr("disabled","disabled");
                 common.connectJsTunnel(function (jsTunnel) {
                    if(deleteImageUrl){
                        salesModule.deletePic(deleteImageUrl, function (data) {
                       });
                    };

                     if(editImageResult != ""){
                         imgUrlArray[0] = editImageResult
                     }
                     var account = {
                         employee_name:user.employee_name,
                         company_account: userInfo.companyAccount,
                         login_name: $("#editUserPage .name").val(),
                         password: sha256($("#editUserPage .password").val()),
                         role_id: employeeRole,
                         shop_id: userShopId? userShopId : userInfo.shopID,
                         employee_phone: $("#editUserPage .telephone").val(),
                         pic_src_list:imgUrlArray,
                         employee_id:EmployeeInfo.employee_id,
                         residue_date:user.residue_date,
                         expire_date:user.expire_date
                     }
                     common.log("编辑用户account=" + JSON.stringify(account));
                     empModule.updateEmp(account, function (data) {
                         var httpResponse = JSON.parse(data.response);
                         if(data.ack == 1 && httpResponse.retcode == 200){
                                $("#editUserPage .confirmBtn").removeAttr("disabled");
                                account.pic_hd_src = imgUrlArray;
                                sessionStorage.setItem("accountDetail",JSON.stringify(account));
                                editImageResult = "";
                                common.backPageHistory();
                         }else if(httpResponse.retcode == 4012){
                              $("#editUserPage .confirmBtn").removeAttr("disabled");
                              $.toast("用户已存在，请跟换用户名");
                         }else if(httpResponse.retcode == "4026"){
                              $("#editUserPage .confirmBtn").removeAttr("disabled");
                              $.toast("权限不足，无法修改");
                         }
                         else{
                              $("#editUserPage .confirmBtn").removeAttr("disabled");
                              $.toast("网络异常");
                         }
                     });
                 });
               }else{
                 $.toast("请输入正确的信息")
               }
             });

            $(document).off("click",".backPageBtn");
            $(document).on("click",".backPageBtn",function(){
                common.backPageHistory();
            })
        };

        $(document).on("pageInit", "#accountManagePage", function (e, pageId, $page){
              accountList();
        });

        /* SPA */
        $(document).on("pageAnimationStart","#addUserPage,#accountManagePage,#account_detail_page,#editUserPage",function(){
            $('.login_input input').val('');
            $('.roleText').val("老板");
            $('#saveUserBtn').removeClass('isRightInput');

            addAccount();
            accountDetail();
            editAccount();
        });
        $.init();
    });
})();