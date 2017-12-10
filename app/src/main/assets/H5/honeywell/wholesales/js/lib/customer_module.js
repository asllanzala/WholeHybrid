define(['zepto', 'common'], function ($, common) {
    /**
     * 添加客户
     * @param {Object} shop_id
     * @param {Object} customer
     * @param {Object} callback
     */
    function addCustomer(shop_id, customer, callback) {
        var data = common.dataTransform('customer/add', 'POST', {
            "shop_id": shop_id,
            "customer_name": customer.customer_name,
            "contact_name": customer.contact_name,
            "contact_phone": customer.contact_phone,
            "memo": customer.memo,
            "address": customer.address,
            "invoice_title": customer.invoice_title
        });
        //call native method
        common.jsCallNativeEventHandler(JS_CALL_NATIVE_EVENT_REQUEST_WEB_API, data, function (responseData) {
            callback && callback(responseData);
        })
    }

    /**
     * 删除客户
     * @param {Object} customer_id
     * @param {Object} callback
     */
    function deleteCustomer(customer_id, callback) {
        var data = common.dataTransform('customer/delete', 'POST', {
            "customer_id": customer_id
        });
        //call native method
        common.jsCallNativeEventHandler(JS_CALL_NATIVE_EVENT_REQUEST_WEB_API, data, function (responseData) {
            callback && callback(responseData);
        })
    }

    /**
     * 修改客户
     * @param {Object} shop_id
     * @param {Object} customer
     * @param {Object} callback
     */
    function updateCustomer(shop_id, customer, callback) {
        
        var data = common.dataTransform('customer/update', 'POST', {
            //"shop_id": shop_id,
            //"customer_name": customer.customer_name,
            //"contact_phone": customer.customer_phone,
            "customer_id": customer.customer_id,
            "shop_id":shop_id,
            "customer_name": customer.customer_name,
            "contact_name": customer.contact_name,
            "contact_phone": customer.contact_phone,
            "memo": customer.memo,
            "address": customer.address,
            "invoice_title": customer.invoice_title
        });

        common.log("传入native的customer 数据=" + JSON.stringify(data));
        //call native method
        common.jsCallNativeEventHandler(JS_CALL_NATIVE_EVENT_REQUEST_WEB_API, data, function (responseData) {
            callback && callback(responseData);
        })
    }

    /**
     * 查询用户采购信息
     * @param shop_id
     * @param customer
     * @param callback
     */
    function queryCustomerRecord(customer_id,product_id,callback) {
        var data = common.dataTransform('statistic/sale_record', 'POST', {
            "customer_id": customer_id,
            "product_id": product_id
        });
        //call native method
        common.jsCallNativeEventHandler(JS_CALL_NATIVE_EVENT_REQUEST_WEB_API, data, function (responseData) {
            callback && callback(responseData);
        })
    }

    //end
    return {
        addCustomer: addCustomer,
        deleteCustomer: deleteCustomer,
        updateCustomer: updateCustomer,
        queryCustomerRecord: queryCustomerRecord
    }
});