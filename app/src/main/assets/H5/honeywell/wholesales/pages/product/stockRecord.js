
(function () {
    require(['zepto', 'common', 'salesModule', 'supplierModule'], function ($, common, salesModule, supplierModule) {

       /*purchase record*/
       $(document).on("pageInit","#purchaseRecordPage",function(){
           var product_id = parseInt(sessionStorage.getItem("product_id"));
           var deleteRecord = 0;
           common.connectJsTunnel(function (jsTunnel) {
               jsTunnel.init();
               var userData = {
                   method: NATIVE_METHOD_GET_USER_INFO,
                   data: ""
               };

               common.jsCallNativeEventHandler(JS_CALL_NATIVE_EVENT_EXECUTE_NATIVE_METHOD, userData, function (data) {
                  var userInfo = JSON.parse(data.response);
                    /*web request for product record*/
                   var supplierProductRecord ={
                         "shop_id":parseInt(userInfo.shopID),
                         "product_id":product_id
                   };
                   var supplierProductRecordData =common.dataTransform('transaction/buy/list','POST',supplierProductRecord);
                   common.jsCallNativeEventHandler(
                      JS_CALL_NATIVE_EVENT_REQUEST_WEB_API,
                      supplierProductRecordData,
                      function(data){
                         var respondData =data.response;
                         console.log('respondBody='+respondData);
                         respondData =JSON.parse(respondData);
                         var retBody = respondData.retbody.total_stock_in_list,
                             template ='';
                         if(respondData.retcode ==200){
                              if(retBody && retBody.length > 0){
                                 $.each(retBody,function(key,data){
                                    template +=$('#purchaseInfomation').html()
                                       .replace("{{product_purchase_id}}",data.purchase_summary_id)
                                       .replace('{{productName}}',data.product_name)
                                       .replace('{{productPrice}}','￥'+data.min_price+" - "+data.max_price)
                                       .replace('{{supplierName}}',data.supplier_name)
                                       .replace('{{productNumber}}',data.sum_number)
                                       .replace('{{purchaseTime}}',data.purchase_time)
                                       .replace("{{index}}",key)
                                       .replace("{{number}}",data.sum_number)
                                 })
                                 $('.recordInfo').html(template);
                              }else{
                                 $('.noInfo').show()
                              }
                         }else{
                           $.toast("网络异常");
                         }
                      }
                   )
               });

              /* delete stock in history */
              $(document).off("click",".deleteButton");
              $(document).on("click",".deleteButton",function(){
                var stockInDelete ={
                     purchase_summary_id:$(this).parents(".purchaseInfo").data("product_purchase_id")
                },
                    index = parseInt($(this).data("index")),
                    stockInNum =parseInt($(this).data("number")),
                    supplierName = $(this).parent().siblings(".supplierNameDiv").find(".supplierName").text();

                  $.confirm("取消入库将对商品库存和入库价产生影响，是否确认","温馨提示",function(){
                      var stockInDeleteData =common.dataTransform('product/buy/rollback','POST',stockInDelete);
                         common.jsCallNativeEventHandler(
                            JS_CALL_NATIVE_EVENT_REQUEST_WEB_API,
                            stockInDeleteData,
                            function(data){
                               var respondData =data.response;
                               respondData =JSON.parse(respondData);
                               if(respondData.retcode ==200){
                                    $(".purchaseInfo").eq(index).empty();
                                    if(($(".purchaseInfo").length) < 1){
                                        $(".recordInfo").html("<div class='noInfo' style='display:block'><div class='content-block'>无进货记录，快去进货吧 >_< !</div></div>")
                                    }
                               }else if(respondData.retcode ==4034){
                                    $.toast("暂无最近的入库订单");
                               }else if(respondData.retcode ==4035 || 180000010){
                                    $.toast("货品已卖出，无法取消");
                               }else if(respondData.retcode ==4016 ){
                                    $.toast("货品已卖出，库存不足");
                               }else{
                                    $.toast("网络异常,删除记录失败");
                               }
                            }
                         )
                  },function(){
                  }, "取消", "确认");
              });
           });

       });
       $.init();
    });
})();



