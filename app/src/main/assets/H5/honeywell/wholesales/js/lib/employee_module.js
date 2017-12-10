define(['zepto', 'common'], function($, common) {
	/**
	 * 添加员工
	 * @param {Object} emp
	 * @param {Object} callback
	 */
	function addEmp(emp, callback) {
		var data = common.dataTransform('employee/add', 'POST', emp);
		//call native method
		common.jsCallNativeEventHandler(JS_CALL_NATIVE_EVENT_REQUEST_WEB_API, data, function(responseData) {
			callback && callback(responseData);
		})
	}

	/**
	 * 查询员工列表
	 * @param {Object} company_account
	 * @param {Object} callback
	 */
	function queryEmployeeList(company_account, callback) {
		var data = common.dataTransform('employee/query', 'POST', {
			"company_account": company_account
		});
		//call native method
		common.jsCallNativeEventHandler(JS_CALL_NATIVE_EVENT_REQUEST_WEB_API, data, function(responseData) {
			callback && callback(responseData);
		})
	}
	/**
	 * 修改员工
	 * @param {Object} emp
	 * @param {Object} callback
	 */
	function updateEmp(emp, callback) {
		var data = common.dataTransform('employee/modify', 'POST', emp);
		//call native method
		common.jsCallNativeEventHandler(JS_CALL_NATIVE_EVENT_REQUEST_WEB_API, data, function(responseData) {
			callback && callback(responseData);
		})
	}
	/**
	 * 删除员工
	 * @param {Object} employee_id
	 * @param {Object} callback
	 */
	function deleteEmployee(employee_id, callback) {
		var data = common.dataTransform('employee/delete_employee', 'POST', {
			"employee_id": employee_id
		});
		//call native method
		common.jsCallNativeEventHandler(JS_CALL_NATIVE_EVENT_REQUEST_WEB_API, data, function(responseData) {
			callback && callback(responseData);
		})
	}
	//end
	return {
		addEmp: addEmp,
		queryEmployeeList: queryEmployeeList,
		updateEmp: updateEmp,
		deleteEmployee: deleteEmployee
	}
});