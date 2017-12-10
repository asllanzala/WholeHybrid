/**
 * (c) Copyright 2016 Administrator. All Rights Reserved.
 * 2016-05-19
 *
 */
(function () {
    require(['zepto', 'common', 'sha256'], function ($, common, sha256) {
        var localUserList = null;
        $(document).on("pageInit", "#loginPage", function (e, pageId, $page) {
            //TODO 这个判断当前浏览器的语言环境的方法可抽取为公共方法
            //var type = navigator.appName, lang = "";
            //if (type == "Netscape") {
            //    lang = navigator.language
            //} else {
            //    lang = navigator.userLanguage
            //}
            ////取得浏览器语言的前两个字母
            //var lang = lang.substr(0, 2)
            //common.log("lang=" + lang);
            //// 英语
            //if (lang == "en") {
            //    $.getJSON("../../i18n/en.json", function (data) {
            //        common.log("获取配置文件:" +JSON.stringify(data));
            //    })
            //} else if (lang == "zh") {
            //    // 中文 - 不分繁体和简体
            //    $.getJSON("../../i18n/zh.json", function (data) {
            //        common.log("获取配置文件:" +JSON.stringify(data));
            //    })
            //}

            //判断是否为第一次登陆
            common.connectJsTunnel(function (jsTunnel) {
                // jsTunnel 初始化完成
                jsTunnel.init();
                // 获取本地登陆客户清单
                var getAllUsersEvent = {
                    method: NATIVE_METHOD_LOGIN_GET_LOCAL_USER_LIST,
                    data: ""
                };
                //调用底层方法,获取数据
                common.jsCallNativeEventHandler(JS_CALL_NATIVE_EVENT_EXECUTE_NATIVE_METHOD, getAllUsersEvent, function (data) {
                    if (data.ack == 1) {
                        localUserList = eval(data.response);
                        loginUserTemp = "";
                        if (!(localUserList && localUserList.length > 0)) {
                            return;
                        }
//                        $(".icon").show();
                        for (var i = 0; i < localUserList.length; i++){
                            if (i == 0) {
                                //上次成功登陆的用户
                                $(".company_account").val(JSON.parse(localUserList[i]).companyAccount);
//                                $(".login_name").val(JSON.parse(localUserList[i]).loginName);
//                                $(".password").val(JSON.parse(localUserList[i]).password);
//                                $("#empLogin").css({"background":"red","color":"white"})
                            }
                            loginUserTemp += "<p class='loginedUser' data-userJson='" + localUserList[i] + "'>" + JSON.parse(localUserList[i]).loginName + "</p>";
                        }
//                        $(".logined_userList").append(loginUserTemp);
                    } else {
                        common.log("没有获取到本地用户ID");
                    }

                });
            });

            // login info validate
            var validateUser = function () {
                var loginName = $(".login_name").val();
                var password = $(".password").val();
                var companyAccount = $(".company_account").val();

                var isCompanyAccountValidate = false;
                var isLoginNameValidate = false;
                var isPasswordValidate = false;

                if (!common.regUser(companyAccount)) {
//                    $(".company_account").parent(".login_input").addClass("error");
                    isCompanyAccountValidate = false;
                } else {
//                    $(".company_account").parent(".login_input").removeClass("error");
                    isCompanyAccountValidate = true;
                }

                if (!common.regUser(loginName)) {
//                    $(".login_name").parent(".login_input").addClass("error");
                    isLoginNameValidate = false;
                } else {
//                    $(".login_name").parent(".login_input").removeClass("error");
                    isLoginNameValidate = true;
                }

                if (!common.regPassword(password)) {
//                    $(".password").parent(".login_input").addClass("error");
                    isPasswordValidate = false;
                } else {
//                    $(".password").parent(".login_input").removeClass("error");
                    isPasswordValidate = true;
                }

                return isLoginNameValidate && isPasswordValidate && isCompanyAccountValidate;
            }

            //验证当前输入内容
            $(document).on('input','.login_name',function(){
                if (!common.regUser($(this).val())) {
                    $(this).parent(".login_input").addClass("error");
                } else {
                    $(this).parent(".login_input").removeClass("error");
                }
            })

            $(document).on('input','.company_account',function(){
                if (!common.regUser($(this).val())) {
                    $(this).parent(".login_input").addClass("error");
                } else {
                    $(this).parent(".login_input").removeClass("error");
                }
            })

            $(document).on('input','.password',function(){
                if (!common.regPassword($(this).val())) {
                    $(this).parent(".login_input").addClass("error");
                } else {
                    $(this).parent(".login_input").removeClass("error");
                }
            })


            //输入完成后修改登陆按钮样式
            $(document).on("input",".login_form input",function(){
                if(validateUser()){
                     $("#empLogin").css({"background":"red","color":"white"})
                }else{
                      $("#empLogin").css({"background":"#f3f3f3","color":"#a1a1a1"})
                }
            });

            // login in
            var empLogin = document.getElementById("empLogin");
            empLogin.addEventListener("click", function () {
                if (validateUser()) {
                    common.log("验证通过");

                    // 验证通过后显示loading
                    $.showIndicator();
                    var userInfoJson = {
                        'company_account': $(".company_account").val(),
                        'login_name': $(".login_name").val(),
                        'password': sha256($(".password ").val())
                    };

                    common.log('userInfoJson' + sha256($(".password ").val()));
                    var loginData = {
                        method: NATIVE_METHOD_LOGIN,
                        data: JSON.stringify(userInfoJson)
                    };
                    common.jsCallNativeEventHandler(JS_CALL_NATIVE_EVENT_EXECUTE_NATIVE_METHOD, loginData, function (responseData) {
                        var httpResponse = JSON.parse(responseData.response);
                        if (responseData.ack == 1 && httpResponse.retcode == 200) {
                            var shop_list = httpResponse.retbody.shop_list;
                            if (shop_list != null) {
                                sessionStorage.setItem("shopId", shop_list[0].shop_id);
                            }
                        } else {
                            common.log("返回错误, httpResponse.retcode=" + httpResponse.retcode);
                            if (httpResponse.retcode == '4000') {
                                $.toast('服务器连接错误', 1300);
                            } else if (httpResponse.retcode == '4001') {
                                $.toast('密码错误', 1300);
                                $(".password").parent(".login_input").addClass("error");
                            } else if (httpResponse.retcode == '4003') {
                                $.toast('用户已被注册', 1300);
                                $(".login_name").parent(".login_input").addClass("error");
                            } else if (httpResponse.retcode == '4004' || httpResponse.retcode == '4013') {
                                $.toast('用户未注册', 1300);
                                $(".company_account").parent(".login_input").addClass("error");
                                $(".login_name").parent(".login_input").addClass("error");
                            } else if (httpResponse.retcode == '4042'){
                                $.toast('账户已过期，请充值时间', 1300);
                            }else if(httpResponse.retcode == '4090'){
                                $.toast('用户不存在或密码错误', 1300);
                            }
                            else {
                                $.toast('网络错误', 2000);
                            }
                        }
                        $.hideIndicator();
                    })
                } else {
                    common.log("验证失败");
                }
            }, true);

            var currentName = "";
            var documentPhone = document.getElementById("login_name");

            documentPhone.addEventListener("click", function () {
                $("#loginPage").off("click");
            }, true);

            // 历史用户列表
            $(document).on("focus", "#login_name", function (event) {
                if (localUserList && localUserList.length > 0) {
                    $(".logined_userList").show();
                }
                event.stopPropagation();
                event.preventDefault();
                return false;
            }).on("input propertychange", "#login_name", function (event) {
                var inputPhone = $(this).val();
                var storeUsers = $(".loginedUser");

                if (storeUsers && storeUsers.length > 0) {
                    $(".logined_userList").show();
                    $.each(storeUsers, function (i, item) {
                        var usersotre = $(item).text();
                        // 搜索空字符串的时候也显示全部用户列表
                        if (usersotre.indexOf(inputPhone) > -1) {
                            $(item).show();
                        } else {
                            $(item).hide();
                            $(".logined_userList").hide();
                        }
                    })
                } else {
                    $(".logined_userList").hide();
                }
                $(".password").val("");

            }).on("blur", "#login_name", function (event) {
                // 点击页面任何地方都隐藏用户列表
                $("#loginPage").on("click", function (event) {
                    $(".logined_userList").hide();
                })
            })

            // 历史用户的下拉按钮
            $(document).on("click", ".icon-down", function () {
                $(".logined_userList").toggle();
            })

            // 选中某一个历史用户,把这个用户值填充到输入框中
            $(document).on("click", ".loginedUser", function () {
                $(".logined_userList").hide();
                var userJsonStr = $(this).attr("data-userJson");
                var userJson = JSON.parse(userJsonStr);
                // 把从历史列表里选择的用户填充到用户名输入框里面
                $(".company_account").val(userJson.companyAccount);
                $(".login_name").val(userJson.loginName);
                $(".password").val(userJson.password);
                $(".login_form div").removeClass('error');
                $("#empLogin").css({"background":"red","color":"white"});
            })
        })
        $.init();
    });
})();