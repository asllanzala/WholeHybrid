!function () {
    requirejs.config({
        baseUrl: "../../js",
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
            ncename: "NativeCallJsEventName",
            jcnename: "JsCallNativeEventName",
            supplierModule: "lib/supplier_module",
            sha256: "lib/sha256"
        },
        shim: {
            template: {
                exports: "template", init: function () {
                    return this.Template;
                }
            },
            zepto: {exports: "$"},
            sui: {deps: ["zepto"]}, suiExtend: {deps: ["zepto", "sui"]}
        },
        urlArgs: "bust=" + (new Date).getTime()
    })
}();