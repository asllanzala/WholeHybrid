define(['zepto', 'common'], function($, common) {
	/**
	 * 查询店铺列表
	 * @param {Object} company_account
	 * @param {Object} callback
	 */
	function queryShopList(company_account, callback) {
		var data = common.dataTransform('shop/query', 'POST', {
			'company_account': company_account
		});
		//call native method
		common.jsCallNativeEventHandler(JS_CALL_NATIVE_EVENT_REQUEST_WEB_API, data, function(responseData) {
			callback && callback(responseData);
		})
	}
	/**
	 * 开店
	 * @param {Object} shop
	 */
	function addShop(shop, callback) {
		var data = common.dataTransform('shop/add', 'POST', shop);
		//call native method
		common.jsCallNativeEventHandler(JS_CALL_NATIVE_EVENT_REQUEST_WEB_API, data, function(responseData) {
			callback && callback(responseData);
		})
	}
	/**
	 * 删除店铺
	 * @param {Object} shop_id
	 * @param {Object} callback
	 */
	function deleteShop(shop_id, callback) {
		var data = common.dataTransform('shop/delete', 'POST', {
			"shop_id": shop_id
		});
		//call native method
		common.jsCallNativeEventHandler(JS_CALL_NATIVE_EVENT_REQUEST_WEB_API, data, function(responseData) {
			callback && callback(responseData);
		})
	}
	
	/**
	 * 修改店铺
	 * @param {Object} shop
	 * @param {Object} callback
	 */
	function updateShop(shop, callback) {
		var data = common.dataTransform('shop/update', 'POST', shop);
		//call native method
		common.jsCallNativeEventHandler(JS_CALL_NATIVE_EVENT_REQUEST_WEB_API, data, function(responseData) {
			callback && callback(responseData);
		})
	}
	//end
	return {
		queryShopList: queryShopList,
		addShop: addShop,
		deleteShop:deleteShop,
		updateShop:updateShop
	}
});