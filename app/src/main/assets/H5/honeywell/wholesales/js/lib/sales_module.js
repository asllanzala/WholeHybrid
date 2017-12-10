define(['zepto', 'common'], function ($, common) {
    /**
     * 查询商品列表
     * @param {Object} shop_id
     * @param {Object} callback
     */
    function initSaleList(shop_id, callback) {
        var data = common.dataTransform('inventory/product_list', 'POST', {
            "shop_id": shop_id
        });
        //call native method
        common.jsCallNativeEventHandler(JS_CALL_NATIVE_EVENT_REQUEST_WEB_API, data, function (responseData) {
            callback && callback(responseData);
        })
    }

    /**
     * 查询商品详情
     * @param {Object} shop_id
     * @param {Object} product_id
     * @param {Object} callback
     */
    function querySaleDetail(productData,callback) {
        var data = common.dataTransform('sku_product/detail','POST',productData);
        //call native method
        common.jsCallNativeEventHandler(JS_CALL_NATIVE_EVENT_REQUEST_WEB_API, data, function (responseData) {
            callback && callback(responseData);
        })
    }

    /**
     * 搜索商品
     * @param {Object} sname
     * @param {object} shop_id
     */
    function searchSalesList(shop_id, sname, callback) {
        var data = common.dataTransform('inventory/search', 'POST', {
            "shop_id": shop_id,
            "product_name": sname
        });
        //call native method
        common.jsCallNativeEventHandler(JS_CALL_NATIVE_EVENT_REQUEST_WEB_API, data, function (responseData) {
            callback && callback(responseData);
        })
    }

    /**
     * 修改商品
     * @param {Object} shop_id
     * @param {Object} sales
     * @param {Object} callback
     */
    function updateProduct(shop_id, product, callback) {
        var data = common.dataTransform('sku_product/update', 'POST', {
            "shop_id": shop_id,
            "product_name": product.product_name,
            "standard_price": product.standard_price,
            "category_ids": product.category_ids,
            "unit":product.unit,
            "product_id": product.product_id,
            "quantity": product.quantity,
            "product_code": product.product_code,
            "product_number":product.product_number,
            "describe": product.describe,
            "pic_src_list": product.pic_src_list,
            "sku_value_list":product.sku_value_list,
            "inventory_list":product.inventory_list,
            "warehouse_id":product.warehouse_id,
            'tag_value_list':product.tag_value_list
        });
        //call native method
        common.jsCallNativeEventHandler(JS_CALL_NATIVE_EVENT_REQUEST_WEB_API, data, function (responseData) {
            callback && callback(responseData);
        })
    }

    /**
     * 修改库存{"product_code": xxx,"number":xxxx,"shop_id":xxx}
     * @param {Object} shopId
     * @param {Object} productCode
     * @param {Object} number
     * @param {Object} callback
     */

    function updateQuantity(shopId, productId, number, callback) {
        var data = common.dataTransform('inventory/take', 'POST', {
            "product_id": productId,
            "count": number,
            "shop_id": shopId
        });
        //call native method
        common.jsCallNativeEventHandler(JS_CALL_NATIVE_EVENT_REQUEST_WEB_API, data, function (responseData) {
            callback && callback(responseData);
        })
    }

    /**
     * 删除商品
     * @param {Object} shop_id
     * @param {Object} pid
     * @param {Object} callback
     */
    function deleteProduct(shop_id, pid, callback) {
        var data = common.dataTransform('sku_product/delete', 'POST', {
            "product_id":pid,
            "shop_id": shop_id
        });
        //call native method
        common.jsCallNativeEventHandler(JS_CALL_NATIVE_EVENT_REQUEST_WEB_API, data, function (responseData) {
            callback && callback(responseData);
        });
    }

    /**
     * 添加 商品
     "product_code": "1234111111126",
     "product_name": "Nike",
     "color":"red",
     "weight":"80",
     "number": "9",
     "category": "clothes",
     "unit": "箱",
     "shop_id": "1",
     "describe": "衣服详细说明",
     "standard_price": "110000001111000",
     "stock_price": "88",
     "pic_src_list":["/static/thumb/14697734619981.gif","/static/thumb/14697635869234.png"]
     * @param {Object} shop_id
     * @param {Object} product
     * @param {Object} callback
     */
    function addProduct(shop_id, product, callback) {
        var data = common.dataTransform('sku_product/add', 'POST', {
            "shop_id": shop_id,
            "product_code": product.product_code,
            "product_number":product.product_number,
            "product_name": product.product_name,
            "standard_price": product.standard_price + "",
            "category_ids": product.category_ids,
            "unit": product.unit,
            "stock_price": product.stock_price + "",
            "quantity": product.quantity,
            "describe": product.describe,
            "pic_src_list": product.pic_src_list,
            "sku_value_list":product.sku_value_list,
            "inventory_list":product.inventory_list,
            "warehouse_id":product.warehouse_id,
            'tag_value_list':product.tag_value_list
        });
        //call native method
        common.jsCallNativeEventHandler(JS_CALL_NATIVE_EVENT_REQUEST_WEB_API, data, function (responseData) {
            callback && callback(responseData);
        })
    }

    /**
     * 补登进价
     * @param {Object} stockInfo
     * @param {Object} callback
     */
    function stockInConfirm(stockInfo, callback) {
        var data = common.dataTransform('transaction/buy/confirm', 'POST', stockInfo);
        common.log(JSON.stringify(data), stockInfo);
        common.jsCallNativeEventHandler(JS_CALL_NATIVE_EVENT_REQUEST_WEB_API, data, function (responseData) {
            callback && callback(responseData);
        })
    }

    /**
     * 进货
     * @param {Object} pductArray
     * @param {Object} callback
     */
    function buyProduct(produceDetail, callback) {
        var data = common.dataTransform('product/buy','POST', produceDetail);
        //call native method
        common.jsCallNativeEventHandler(JS_CALL_NATIVE_EVENT_REQUEST_WEB_API, data, function (responseData) {
            callback && callback(responseData);
        })
    }

    /**
     * 出货
     * @param {Object} pductArray
     * @param {Object} callback
     */
    function shipSales(orderDetail, callback) {
        var data = common.dataTransform('product/sale', 'POST', orderDetail);
        //call native method
        common.jsCallNativeEventHandler(JS_CALL_NATIVE_EVENT_REQUEST_WEB_API, data, function (responseData) {
            callback && callback(responseData);
        })
    }

    /**
     * 上传图片
     * @param {Object} file_data
     * @param {Object} product_code
     * @param {Object} callback
     */
    function uploadPic(file_data, product_code, callback) {
        var data = common.dataTransform('pic/upload', 'POST', {
            "file_data": file_data,
            "product_code": product_code
        });
        //call native method
        common.jsCallNativeEventHandler(JS_CALL_NATIVE_EVENT_REQUEST_WEB_API, data, function (responseData) {
            callback && callback(responseData);
        })
    }

    /**
     * 删除图片
     * @param {Object} picName
     * @param {Object} callback
     */
    function deletePic(picName, callback) {
        var data = common.dataTransform('pic/delete', 'POST', {
            "pic_name": picName
        });
        //call native method
        common.jsCallNativeEventHandler(JS_CALL_NATIVE_EVENT_REQUEST_WEB_API, data, function (responseData) {
            callback && callback(responseData);
        })
    }

    //查询分类
    function queryCategory(employee_id, callback) {
        var data = common.dataTransform('category/query', 'POST', {
            "employee_id": employee_id
        });
        common.jsCallNativeEventHandler(JS_CALL_NATIVE_EVENT_REQUEST_WEB_API, data, function (responseData) {
            callback && callback(responseData);
        })
    }

    /* get auto setting product_number */
    function getAutoSettingProductNumber(number,callback) {
        var data = common.dataTransform('product/number/generate', 'POST', number);
        common.jsCallNativeEventHandler(JS_CALL_NATIVE_EVENT_REQUEST_WEB_API, data, function (responseData) {
            callback && callback(responseData);
        })
    }

    /* get account list information */
    function getAccountListInfo(callback){
        var data = common.dataTransform('account/list', 'POST',{});
        common.jsCallNativeEventHandler(JS_CALL_NATIVE_EVENT_REQUEST_WEB_API, data, function (responseData) {
            callback && callback(responseData);
        })
    }

    //end
    return {
        initSaleList: initSaleList,
        querySaleDetail: querySaleDetail,
        searchSalesList: searchSalesList,
        updateProduct: updateProduct,
        updateQuantity: updateQuantity,
        deleteProduct: deleteProduct,
        addProduct: addProduct,
        buyProduct: buyProduct,
        stockInConfirm: stockInConfirm,
        shipSales: shipSales,
        deletePic: deletePic,
        uploadPic: uploadPic,
        queryCategory: queryCategory,
        getAutoSettingProductNumber:getAutoSettingProductNumber,
        getAccountListInfo:getAccountListInfo
    }
});