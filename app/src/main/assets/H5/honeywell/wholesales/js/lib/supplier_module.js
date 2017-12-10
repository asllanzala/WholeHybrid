define(['zepto', 'common'], function ($, common) {

    /**
     * 查询供应商列表
     * @param {Object} shop_id
     * @param {Object} callback
     */
    function initSupplierList(shop_id, callback) {
        var data = common.dataTransform('supplier/query', 'POST', {
            "shop_id": shop_id,
            "page_length": 10000,
            "require_deleted":false
        });
        //call native method
        common.jsCallNativeEventHandler(JS_CALL_NATIVE_EVENT_REQUEST_WEB_API, data, function (responseData) {
            callback && callback(responseData);
        })
    }

    /**
     * 添加供应商
     * @param {Object} shop_id
     * @param {Object} product_id
     * @param {Object} callback
     */
    function addSupplier(supplier, callback) {
        var data = common.dataTransform('supplier/add', 'POST', supplier);
        //call native method
        common.jsCallNativeEventHandler(JS_CALL_NATIVE_EVENT_REQUEST_WEB_API, data, function (responseData) {
            callback && callback(responseData);
        })
    }

    /**
     * 修改供应商
     * @param {Object} shop_id
     * @param {Object} supplier_name
     * @param {Object} contact_name
     * @param {Object} contact_phone
     * @param {Object} supplier_id
     * @param {Object} callback
     */
    function updateSupplier(sid,supplier, callback) {
        var supplierDetail = {
            contact_name: supplier.contact_name,
            contact_phone: supplier.contact_phone,
            desc_memo: supplier.desc_memo,
            address: supplier.address,
            bank_info: supplier.bank_info,
            supplier_name: supplier.supplier_name,
            supplier_id:sid
        }
        console.log("传入native底层的supperlier数据:"+JSON.stringify(supplierDetail));
        var data = common.dataTransform('supplier/update', 'POST', supplierDetail);
        //call native method
        common.jsCallNativeEventHandler(JS_CALL_NATIVE_EVENT_REQUEST_WEB_API, data, function (responseData) {
            callback && callback(responseData);
        })
    }

    /**
     * 删除供应商
     * @param {Object} supplier_id
     * @param {Object} callback
     */
    function deleteSupplier(supplier_id, callback) {
        var data = common.dataTransform('supplier/delete', 'POST', {
            "supplier_id": supplier_id
        });
        //call native method
        common.jsCallNativeEventHandler(JS_CALL_NATIVE_EVENT_REQUEST_WEB_API, data, function (responseData) {
            callback && callback(responseData);
        })
    }

    //end
    return {
        addSupplier: addSupplier,
        updateSupplier: updateSupplier,
        deleteSupplier: deleteSupplier,
        initSupplierList: initSupplierList
    }
});