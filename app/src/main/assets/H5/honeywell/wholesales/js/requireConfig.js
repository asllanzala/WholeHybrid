!function () {
    requirejs.config({
        baseUrl: "../../../honeywell/wholesales/",
        paths: {
            zepto: "vendor/zepto.min",
            sui: "lib/sm",
            suiExtend: "lib/sm-extend.min",
            template: "vendor/template",
            common: "lib/common",
            salesModule: "lib/sales_module",
            shopModule: "lib/shop_module",
            empModule: "lib/employee_module",
            customerModule: "lib/customer_module",
            ncename: "js/NativeCallJsEventName",
            jcnename: "js/JsCallNativeEventName",
            supplierModule: "lib/supplier_module",
            ajaxFile: "lib/ajaxFile",
            bdTemplate:"vendor/baiduTemplate",
        },
        shim: {
            template: {
                exports: "template", init: function () {
                    return this.Template
                }
            },bdTemplate: {
                exports: "bdTemplate", init: function () {
                    return this.bdTemplate
                }
            }, ajaxFile: {
                exports: "ajaxFile", init: function () {
                    return this.ajaxFile
                }
            }, zepto: {exports: "$"}, sui: {deps: ["zepto"]}, suiExtend: {deps: ["zepto", "sui"]}
        },
        urlArgs: "bust=" + (new Date).getTime()
    })
}();