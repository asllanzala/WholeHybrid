define(['zepto', 'template', 'sui', 'suiExtend', 'ncename', 'jcnename'], function ($, Template) {
    $.config = {
//        router: false,
        //routerFilter: function($link) {
        //	// 某个区域的 a 链接不想使用路由功能
        //	if ($link.is('.disable-router a')) {
        //		return false;
        //	}
        //	return true;
        //},
        showPageLoadingIndicator: function () {
            return true;
        }
    };
    /**
     * 获取cache的内容
     * @param {Object} name
     */
    function getLsInfo(name) {
        return localStorage.getItem(name);
    }

    /**
     * 存储内容到浏览器本地
     * @param {Object} name
     * @param {Object} value
     */
    function setLsInfo(name, value) {
        return localStorage.setItem(name, value);
    }

    /**
     * 解析模板 并填充模板内容
     * @param {Object} dlength
     * @param {Object} tlHtml
     * @param {Object} box
     */
    function ansyTemplate(data, tlHtml, box) {
        if (data.retbody && data.retbody.length > 0) {
            var template = new Template({
                template: tlHtml,
                data: {
                    listGroup: data //listGroup为自定义命名空间，data为数据json
                },
                handlers: {},
                dataEmptyHandler: false, //是否把undefined, null 处理为空字符串
                callback: function (result) {
                }
            });
            $("#box").css({
                "position": "relative",
                "top": "0 ",
                "left": "0",
                "transform": "none",
                "-webkit-transform": "none"
            });
            box.innerHTML = template.init();
        } else {
            emptyHtmlInfo(box, "box")
        }
    }

    /**
     * 建立元素
     * @param {Object} obj
     * @param {Object} idname
     */
    function emptyHtmlInfo(obj, idname) {
        $("#" + idname + "").html('<img src="../../resources/honeywell/wholesales/images/nosale.png"/><p style="text-align:center;color:#e0e0e0;padding-top:20px;">赶紧添加吧</p>');
        $("#" + idname + "").css({
            "position": "absolute",
            "top": "50% ",
            "left": "50%",
            "transform": "translate(-50% , -70% )",
            "-webkit-transform": "translate(-50% , -70% )"
        });
    }

    /**
     * confirm提示信息
     * @param {Object} cxt
     * @param {Object} callBack
     */
    function confirmTips(cxt, callBack) {
        $.confirm(cxt, callBack);
    }

    /**
     * 提示信息 传入内容
     * @param {Object} cxt
     */
    function alertTips(cxt) {
        $.alert(cxt);
    }

    /**
     * 判断元素是否为空
     * @param {Object} obj
     */
    function isNotnull(obj) {
        return (obj && obj != null && obj != "null" && obj != undefined && obj != "undefined");
    }

    /**
     * 判断元素是空
     * @param {Object} obj
     */
    function isNull(obj) {
        return (obj == null || obj == undefined || obj == "undefined");
    }

    /**
     * 判断元素是空
     * @param {Object} obj
     */
    function isNullString(obj) {
        return (obj == null || obj == undefined || obj == "undefined" || obj == "");
    }

    /**
     * 提示信息  有回调函数
     * @param {Object} cxt
     * @param {Object} callBack
     */
    function warningTips(cxt, callBack) {
        $.alert(cxt, callBack);
    }

    /**
     * 截取页面跳转参数
     * @param {Object} name
     */
    function getQueryString(name) {
        var reg = new RegExp("(^|\\?|&)" + name + "=([^&]*)(\\s|&|$)", "i");
        if (reg.test(location.href)){
           return unescape(RegExp.$2.replace(/\+/g, " "));
        }
        return null;
    }

    /**
     * 封装请求数据
     * @param {Object} url
     * @param {Object} methodType
     * @param {Object} rbody
     */
    function dataTransform(url, methodType, dataObj) {
        var data = {
            urlMethod: url,
            apiMethod: methodType,
            requestBody: JSON.stringify(dataObj)
        };
        return data;
    }

       function setupWebViewJavascriptBridge(callback) {
       if (window.WebViewJavascriptBridge) { return callback(WebViewJavascriptBridge); }
       if (window.WVJBCallbacks) { return window.WVJBCallbacks.push(callback); }
       window.WVJBCallbacks = [callback];
       var WVJBIframe = document.createElement('iframe');
       WVJBIframe.style.display = 'none';
       WVJBIframe.src = 'wvjbscheme://__BRIDGE_LOADED__';
       document.documentElement.appendChild(WVJBIframe);
       setTimeout(function() { document.documentElement.removeChild(WVJBIframe) }, 0)
       }
       /**
     * 调用native的方法，然后回调
     * @param {Object} command
     * @param {Object} data
     * @param {Object} callback
     */
    function jsCallNativeEventHandler(command, data, callback) {

       setupWebViewJavascriptBridge(function(bridge) {

                                    /* Initialize your app here */
                                    bridge.callHandler(command, data, function responseCallback(responseData) {
                                                       callback && callback(responseData);
                                                       })
                                    })
       window.JsTunnel.callNativeEventHandler(command, data, function (response) {
            var response = JSON.parse(response);
            callback && callback(response);
        });
    }

    function backHomePage() {
        var userData = {
            method: NATIVE_PAGE_DASHBOARD_HOME,
            data: ""
        };
        jsCallNativeEventHandler(JS_CALL_NATIVE_EVENT_REQUEST_SWITCH_PAGE, userData, function (data) {
            log("打开主页面完成！");
        });
    }

    //返回上一界面
    function goBackPage(){
        connectJsTunnel(function(jsTunnel) {
            // 初始化混合开发通道
            jsTunnel.init();

            // 注册页面后退事件
            jsTunnel.registerHandler(
                NATIVE_CALL_JS_EVENT_PAGE_GO_BACK,
                function(data, responseCallback) {
                    log("后退成功="+data)
                }
            );
        })
    }


    function backPageHistory() {
        window.history.go(-1);
    }

    /**
     * 加载loading
     */

    function loadingTips() {
        $.showPreloader('loading.....');
    }

    function connectJsTunnel(callback) {
       /*for ios start*/
       setupWebViewJavascriptBridge(callback)
       /*for ios end*/
       if (window.JsTunnel) {
            callback(JsTunnel)
        } else {
            // JS还没加载完，就等“JsTunnelReady”事件
            document.addEventListener(
                'JsTunnelReady',
                function () {
                    callback(JsTunnel)
                },
                false
            );
        }
    }

    function getNativeUserInfo(jsTunnel, callback) {
        var userData = {
            method: NATIVE_METHOD_GET_USER_INFO,
            data: ""
        };
        $.showIndicator();
        jsCallNativeEventHandler(JS_CALL_NATIVE_EVENT_EXECUTE_NATIVE_METHOD, userData, function (data) {
            log("getNativeUserInfo(" + JSON.stringify(data) + ")");
            //sessionStorage.setItem("userInfo", data);
            callback && callback(data);
            $.hideIndicator();
        })

    }

    //赊账时长
    function creditTime(time){
        var numTime =parseInt(time);
        var nowDate =new Date();
        var creditTime = nowDate -numTime;
        creditTime = parseInt(creditTime /(1000*60*60*24));
        if(creditTime < 1){
            return "已一天"

        }else{
            return "已"+creditTime+"天";
        }
    }


    //浮点时间转换;
    function translateTime(time){
        var transtime =parseInt(time);
        var date = new Date(transtime);
        var year = date.getFullYear() + '-';
        var month = (date.getMonth()+1 < 10 ? ('0'+(date.getMonth()+1) +'-'): (date.getMonth()+1)+'-');
        var day = date.getDate() < 10 ? '0'+ date.getDate():date.getDate()+' ';
        var hours = date.getHours() <10 ? '0'+ date.getHours()+':' : date.getHours()+ ':';
        var min = date.getMinutes() <10 ? '0'+ date.getMinutes()+':': date.getMinutes()+ ":";
        var second = date.getSeconds() <10 ? '0'+date.getSeconds() : date.getSeconds() ;

        return year+month+day+" "+hours+min+second;
    }

    //过滤特殊字符
    function filterSpecialCharacter(s)
    {   //过滤中文(\\u4e00-\\u9fa5)
        var pattern = new RegExp("[`~!@#$^&*()=|{}':;',\\[\\] <>/?~！@#￥……&*（）——|{}【】‘；：”“'。，、？]")
        var rs = "";
        for (var i = 0; i < s.length; i++) {
            rs = rs+s.substr(i, 1).replace(pattern, '');
        }
        return rs;
    }

    //log
    function log(obj){
       console.log(obj)
    }


    /*
      表单验证
    */
       //客户名,商品名,供应商名(中文,英文,数字,_)
      function regName(data){
         var regName = new RegExp('^[a-zA-Z0-9_\u4e00-\u9fa5]+$');
           if(!(regName.test(data)&& data.length >0)){
               return ''
           }else{
             return true
           }
      }

       //联系人(中文,英文,数字,_)
       function regContact(data){
           var regContact =new RegExp('^[a-zA-Z0-9_\u4e00-\u9fa5]+$');
             if(!(regContact.test(data) && data.length >0)){
                return ''
             }else{
               return true
             }

       }

       //用户名(中文,英文,数字,_)
       function regUser(data){
           var regUser =new RegExp('^[a-zA-Z0-9_\u4e00-\u9fa5]+$');
              if(!(regUser.test(data) && data.length >0)){
                   return ''
              }else{
                return true
              }
       }

       //密码 (英文,数字,_)
       function regPassword(data){
         var regPassword =new RegExp('^[a-zA-Z0-9]+$');
            if(!(regPassword.test(data) && data.length >0)){
                return ''
            }else{
             return true
            }
       }

       //手机/固定电话
        function regTel(data){
          var regTel = new RegExp('^(\\+86)?1(3[0-9]|4[57]|5[0-35-9]|7[0135678]|8[0-9])\\d{8}$');
          var regTels = new RegExp('^1(3[0-9]|4[57]|5[0-35-9]|7[0135678]|8[0-9])\\d{8}$')
          var regFix = new RegExp('([0-9]{3,4}-)?[0-9]{7,8}');
              if(data.indexOf('+') != -1 ){
                   if(!(regTel.test(data))){
                        return ''
                   }else{
                        return true
                   }
              }else if(data.length <14){
                    if(!(regTels.test(data) || regFix.test(data))){
                        return ''
                    }else{
                        return true
                    }
              }
        }

       //地址,个人信息,备忘(中文,英文,数字,_)
         function regAddress(data){
           var regAddress = new RegExp('^[a-zA-Z0-9_\u4e00-\u9fa5]+$');
           if(!(regAddress.test(data))){
                return ''
           }else{
             return true
            }
         }

         function regDescribe(data){
                var regDescribe = new RegExp('^[,;\(\)\*\+\.\[\]\?\\/\^\{\}\|-&@!#`~:<>=-a-zA-Z0-9_\u4e00-\u9fa5|\u3002|\uff1f|\uff01|\uff0c|\u3001|\uff1b|\uff1a|\u201c|\u201d|\u2018|\u2019|\uff08|\uff09|\u300a|\u300b|\u3008|\u3009|\u3010|\u3011|\u300e|\u300f|\u300c|\u300d|\ufe43|\ufe44|\u3014|\u3015|\u2026|\u2014|\uff5e|\ufe4f]+$');
                if(!(regDescribe.test(data))){
                     return ''
                }else{
                  return true
                 }
          }

         function regNote(data){
           var regAddress = new RegExp('^[a-zA-Z0-9_\ \u4e00-\u9fa5]+$');
           if(!(regAddress.test(data) || data =='')){
               return ''
           }else{
            return true
           }
         }

       //发票抬头
       function regInvoice(data){
          var regInvoice = new RegExp('^[a-zA-Z\u4e00-\u9fa5]+$');
          if(!(regInvoice.test(data))){
                return ''
          }else{
            return true
          }
       }

       function regMoney(data){
          var regMoney = new RegExp('(^[1-9]([0-9]+)?(\.[0-9]{1,2})?$)|(^(0){1}$)|(^[0-9]\.[0-9]([0-9])?$)');
           if(!(regMoney.test(data))){
               return ''
           }else{
           return true
          }
       }

       function regMoney_special(data){
          var regMoney = new RegExp('(^(-)?[1-9]([0-9]+)?(\.[0-9]{1,2})?$)|(^(-)?(0){1}$)|(^(-)?[0-9]\.[0-9]([0-9])?$)');
             if(regMoney.test(data)){
                 return true
             }else{
               return false
            }
       }

       //数目（开头不为0的数字）
       function regNumber(data){
           var regNumber = new RegExp('^[1-9]*[1-9][0-9]*$');
              if(!(regNumber.test(data))){
                  return ''
               }else{
                  return true
              }
       }

       function regStockNumber(data){
           var regNumber = new RegExp('^(-)?[0-9][0-9]*$');
              if(!(regNumber.test(data))){
                  return ''
               }else{
                  return true
              }
       }

       function regProductNumber(data){
          var reg =new RegExp('^[a-zA-Z0-9\.\\-/]+$');
             if(!(reg.test(data))){
                 return ''
              }else{
                 return true
             }
       }

      function regUserPassword(data){
        var reg =new RegExp('^(?=.*\\d)(?=.*[a-z])(?=.*[A-Z])[0-9a-zA-Z]{6,16}$');
           if(!(reg.test(data))){
               return ''
            }else{
               return true
        }
      }



       var regExp ={
            regUser:new RegExp('^[a-zA-Z0-9_]+$'),
            regPassword:new RegExp('^[a-zA-Z0-9_]+$')
       }


    /*popup --删除框*/
    function deletePopup(){
        $('#delete').on("click",function(e){
            $(".alertDiv").toggleClass('action');
            e.cancelBubble = true;
        });
        $(".page").not($("#delete")).on("click",function(){
            $(".alertDiv").removeClass('action');
        });
        $(".alertTitle,.emptyDiv").on("click",function(e){
            $(".alertDiv").addClass('action');
            e.cancelBubble = true;
        });
    }
     /* switch component */
     function switchResult(checkbox,switch_content,content_circle) {
         if(checkbox.prop("checked")){
             content_circle.animate({
                 left: "0"
             },150,"ease-out",function () {
                 checkbox.prop("checked",false);
             });
             switch_content.animate({
                 backgroundColor:"#ccc"
             },150,"ease-in")
         }else{
             content_circle.animate({
                 left: "1.85rem"
             },150,"ease-in",function () {
                 checkbox.prop("checked",true);
             });
             switch_content.animate({
                 backgroundColor:"#ffc700"
             },150,"ease-out")
         }
     }

     function inputMoney(money){
         var strandPrice = money.val();
         var priceCharArray = strandPrice.split("");

         var firstChar = priceCharArray[0];
         var secondChar = priceCharArray[1];

         if('0' == firstChar && isNotnull(secondChar) && '.' != secondChar) {
             priceCharArray.splice(0, 1);
             strandPrice = priceCharArray.join("");
             money.val(strandPrice);
         } else if ('.' == firstChar) {
             strandPrice = "0" + strandPrice;
             priceCharArray = strandPrice.split("");
             money.val(strandPrice);
         }

         if(!isNaN(strandPrice)){
             var pointIndex =strandPrice.indexOf(".");
             if(pointIndex > 0){
                 var decimalValue = strandPrice.substr(pointIndex + 1);
                 // 小数点后的位数小于等于两位
                 if(decimalValue.length <= 2) {
                     return;
                 }
             }
         }

         // 去掉非数字型字符
         if(isNaN(strandPrice)){
             //字符串转化为数组
             var length = priceCharArray.length - 1;
             for(var i = length; i>=0; i--) {
                 if(isNaN(priceCharArray[i]) && "." != priceCharArray[i]) {
                     priceCharArray.splice(i, 1);
                 } else if ("." == priceCharArray[i]) {
                     if(hasPoint) {
                         priceCharArray.splice(prePointIndex, 1);
                     } else {
                         hasPoint = true;
                         prePointIndex = i;
                     }
                 }
             }
             strandPrice = priceCharArray.join("");
         }

         // 去掉多余的小数点
         var hasPoint = false;
         for(var i = 0; i <= length; i++) {
             if ("." == priceCharArray[i]) {
                 if(hasPoint) {
                     priceCharArray.splice(i, 1);
                 } else {
                     hasPoint = true;
                 }
             }
         }
         strandPrice = priceCharArray.join("");
         var pointIndex = strandPrice.indexOf(".");
         // 有小数点的时候再处理，约束小数点后只显示2位
         if(pointIndex > 0){
             var decimalValue = strandPrice.substr(pointIndex + 1);
             // 小数点后的位数大于两位
             if(decimalValue.length > 2) {
                 //限制用户输入,默认最多小数点后面两位
                 strandPrice = strandPrice.substr(0, strandPrice.length-1);
             }
         }
         strandPrice = filterSpecialCharacter(strandPrice);
         money.val(strandPrice);
     }

     var pullDown_warehouse = (function () {
            var pullDown = {
                    closeEvent:
                     function () {
                        var _this = this;
                        $(document).on("click",".pull_down_item_warehouse",function(){
                             $(".pull_down_item_warehouse").removeClass("warehouse_choiced");
                             $(this).addClass("warehouse_choiced");
                             $('.title_info_warehouse').text($(this).text());
                             $(".pull_down_title_warehouse").attr("data-warehouseid",$(this).data("warehouseid"));
                             _this.closeModel();
                        });
                        $(document).on("click",".pull_down_content_warehouse",function(){
                             _this.closeModel();
                        })
                     },
                   closeModel:
                    function () {
                        $(".pull_down_content_warehouse").animate({
                            height: "0",
                        }, 200, "ease-out");
                    },
                   openModal:
                     function () {
                         $(document).on("click", ".pull_down_title_warehouse", function () {
                             $(".pull_down_content_department").css("height","0");
                             $(".pull_down_content_saleType").css("height","0");
                             $(".pull_down_content_warehouse").animate({
                                 height: "100%",
                             }, 200, "ease-out");
                         });
                     },
                    sentRequest:
                      function (shopId,fn) {
                          var _this = this;
                          jsCallNativeEventHandler(
                              JS_CALL_NATIVE_EVENT_REQUEST_WEB_API,
                              dataTransform('warehouses/list', 'POST', {shop_id:shopId}),
                              function (data) {
                                  var respondData = data.response,
                                      template = "";
                                  respondData = JSON.parse(respondData);
                                  if (respondData.retcode == 200) {
                                        console.log("仓库列表="+JSON.stringify(respondData.retbody.warehouses));
                                      if (respondData.retbody.warehouses && (respondData.retbody.warehouses).length > 0) {
                                          $.each(respondData.retbody.warehouses, function (key, value) {
                                              template += '<div class="pull_down_item_warehouse" data-warehouseid="' + value.warehouse_id + '">' + value.warehouse_name + '</div>'
                                          });
                                          $(".content_list_warehouse").html(template);
                                          $(".pull_down_title_warehouse").attr("data-warehouseid",respondData.retbody.default_warehouse_id);
                                          $(".title_info_warehouse").text(respondData.retbody.default_warehouse_name);
                                          _this.initWarehouse($(".pull_down_title_warehouse").data("warehouseid"));
                                      }
                                  }else{
                                      $.toast('网络异常');
                                  }
                                  fn();
                              }
                          )
                      },
                    initWarehouse:
                      function(warehouse_id){
                        for(var i=0;i<$(".pull_down_item_warehouse").length;i++){
                            if($(".pull_down_item_warehouse").eq(i).data("warehouseid") == warehouse_id){
                                $(".pull_down_item_warehouse").eq(i).addClass("warehouse_choiced");
                                return;
                            }
                        }
                      }
            };
            return pullDown;
     })();

         var pullDown_saleType = (function () {
                     var pullDown = {
                             closeEvent:
                              function () {
                                 var _this = this;
                                 $(document).on("click",".pull_down_item_saleType",function(){
//                                      $(".pull_down_item_saleType").removeClass("warehouse_choiced");
//                                      $(this).addClass("warehouse_choiced");
                                      $(".title_info_saleType").text($(this).text());
                                      $(".pull_down_title_saleType").attr("data-warehouseid",$(this).data("warehouseid"));
                                      _this.closeModel();
                                      if($(".title_info_saleType").text() !=""){
                                           $(".title_info_saleType").parents(".login_input").css("borderColor","#ccc");
                                      }
                                 });
                                 $(document).on("click",".pull_down_content_saleType",function(){
                                      _this.closeModel();
                                 })
                              },
                            closeModel:
                             function () {
                                 $(".pull_down_content_saleType").animate({
                                     height: "0",
                                 }, 200, "ease-out");
                             },
                            openModal:
                              function () {
                                  $(document).on("click", ".pull_down_title_saleType", function () {
                                      $(".pull_down_content_department").css("height","0");
                                      $(".pull_down_content_warehouse").css("height","0");
                                      $(".pull_down_content_saleType").animate({
                                          height: "100%",
                                      }, 200, "ease-out");

                                  });
                              },
                             initWarehouse:
                               function(warehouse_id){
                                 for(var i=0;i<$(".pull_down_item_saleType").length;i++){
                                     if($(".pull_down_item_saleType").eq(i).data("warehouseid") == warehouse_id){
                                         $(".pull_down_item_saleType").eq(i).addClass("warehouse_choiced");
                                         return;
                                     }
                                 }
                               }
                     };
                     return pullDown;
         })();



    function pullDown_department(key) {
          var pullDown = {
                  closeEvent:
                   function () {
                      var _this = this;
                      $(".pull_down_content_department").eq(key).find(".pull_down_item_department").on("click",function(){
                           $(".title_info_department").eq(key).text($(this).text());
                           $(".pull_down_title_department").eq(key).attr("data-warehouseid",$(this).data("warehouseid"));
                           _this.closeModel(key);
                      });
                      $(".pull_down_content_department").eq(key).on("click",function(){
                           _this.closeModel(key);
                      });
                   },
                 closeModel:
                  function (key) {
                      $(".pull_down_content_department").eq(key).animate({
                          height: "0",
                      }, 200, "ease-out");
                  },
                 openModal:
                   function () {
                       $(".pull_down_title_department").eq(key).on("click",function () {
                           $(".pull_down_content_warehouse").css("height","0");
                           $(".pull_down_content_saleType").css("height","0");
                           $(".pull_down_content_department").css("height","0");
                           $(".pull_down_content_department").eq(key).animate({
                               height: "100%",
                           }, 200, "ease-out");
                       });
                   },
          };
          pullDown.closeEvent();
          pullDown.openModal();
    }

     //获取商品的标签属性
     function getProductTag(fn) {
         jsCallNativeEventHandler(
             JS_CALL_NATIVE_EVENT_REQUEST_WEB_API,
             dataTransform('tag/query', 'POST', {}),
             function (data) {
                 var respondData = data.response;
                 respondData = JSON.parse(respondData);
                 if (respondData.retcode == 200) {
                       console.log("标签列表="+JSON.stringify(respondData.retbody));
                       var template_name ="",template_list="",saleType_key ="",newData =null;
                     if (respondData.retbody && (respondData.retbody).length > 0) {
                            newData = respondData.retbody;
                            $.each(respondData.retbody,function(key,value){
                                if(value.tag_key_name == "销售类型"){
                                     var template = '';
                                         saleType_key = key;
                                     $.each(value.tag_value_list, function (k,v) {
                                         template += '<div class="pull_down_item_saleType" data-warehouseid="' + v.tag_value_id + '">' + v.tag_value_name + '</div>'
                                         fn();
                                     });
                                     $(".content_list_saleType").html(template);
                                     pullDown_saleType.closeEvent();
                                     pullDown_saleType.openModal();

                                     newData.splice(key,1);
                                     console.log("编辑后的新数组="+JSON.stringify(newData));
                                     if(newData && newData.length >0 ){
                                            $.each(newData,function(k,v){
                                                template_name += $("#product_tag").html()
                                                     .replace("{{tag_name}}",v.tag_key_name);
                                                template_list += $("#product_tag_list").html();
                                            });

                                            $(".product_tags").html(template_name);
                                            $(".product_tag_list").html(template_list);

                                            $.each(newData,function(k,v){
                                                 var value_item ="";
                                                 $.each(v.tag_value_list,function(num,data){
                                                    value_item += '<div class="pull_down_item_department" data-warehouseid="' + data.tag_value_id + '">' + data.tag_value_name + '</div>'
                                                 });
                                                 $(".content_list_department").eq(k).html(value_item);
                                                 $(".pull_down_content_department").eq(k).css("top",6.3+parseInt(k+1)*2.1+"rem");
                                                 pullDown_department(k);
                                                 fn();
                                            });
                                     }
                                }
                            });
                     }
                 }else{
                     $.toast('网络异常');
                 }
                 fn();
             }
         )
     };

     /* 重写toFixed() 方法 */
         Number.prototype.toFixed = function (exponent) {
             if(this>0){
                 return parseInt(this * Math.pow(10, exponent) + 0.5) / Math.pow(10, exponent);
             }else{
                 return parseInt(this * Math.pow(10, exponent) - 0.5) / Math.pow(10, exponent);
             }
         }


    return {
        getLsInfo: getLsInfo,
        setLsInfo: setLsInfo,
        ansyTemplate: ansyTemplate,
        confirmTips: confirmTips,
        alertTips: alertTips,
        emptyHtmlInfo: emptyHtmlInfo,
        isNotnull: isNotnull,
        isNull: isNull,
        isNullString: isNullString,
        warningTips: warningTips,
        getQueryString: getQueryString,
        dataTransform: dataTransform,
        jsCallNativeEventHandler: jsCallNativeEventHandler,
        loadingTips: loadingTips,
        connectJsTunnel: connectJsTunnel,
        getNativeUserInfo: getNativeUserInfo,
        backHomePage: backHomePage,
        backPageHistory: backPageHistory,
        goBackPage:goBackPage,
        translateTime:translateTime,
        creditTime:creditTime,
        filterSpecialCharacter:filterSpecialCharacter,
        log:log,
        regName:regName,
        regContact:regContact,
        regUser:regUser,
        regPassword:regPassword,
        regTel:regTel,
        regAddress:regAddress,
        regInvoice:regInvoice,
        regMoney:regMoney,
        regNumber:regNumber,
        regExp:regExp,
        regNote:regNote,
        regDescribe:regDescribe,
        deletePopup:deletePopup,
        setupWebViewJavascriptBridge:setupWebViewJavascriptBridge,
        regStockNumber:regStockNumber,
        regMoney_special:regMoney_special,
        switchResult:switchResult,
        regProductNumber:regProductNumber,
        inputMoney:inputMoney,
        pullDown_warehouse:pullDown_warehouse,
        pullDown_saleType:pullDown_saleType,
        getProductTag:getProductTag,
        regUserPassword:regUserPassword
    }
})
