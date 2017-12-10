package com.honeywell.wholesale;

import android.app.Application;
import android.test.ApplicationTestCase;
import android.util.Log;

import com.honeywell.wholesale.framework.database.CustomerDAO;
import com.honeywell.wholesale.framework.model.Customer;

import java.util.concurrent.CountDownLatch;

/**
 * Created by e887272 on 9/7/16.
 */

public class CustomerTest extends ApplicationTestCase<Application> {

    CountDownLatch signal = null;
    public CustomerTest() {
        super(Application.class);
    }

    @Override
    public void setUp() throws Exception {
        createApplication();
        signal = new CountDownLatch(1);
        super.setUp();
    }

    @Override
    public void tearDown() throws Exception {
        super.tearDown();
    }

    public void testCustomerGroup() throws Exception{
        Customer customerA1 = new Customer("customeId1", "contactName", "11",
                "contactPhone", "address", "customerName", "invoiceTitle", "memo", "pinyin",
                "initials");
        customerA1.setGroup("a");

        Customer customerA2 = new Customer("customeId1", "contactName", "11",
                "contactPhone", "address", "customerName", "invoiceTitle", "memo", "pinyin",
                "initials");
        customerA2.setGroup("a");

        Customer customerA3 = new Customer("customeId1", "contactName", "11",
                "contactPhone", "address", "customerName", "invoiceTitle", "memo", "pinyin",
                "initials");
        customerA3.setGroup("a");

        Customer customerB1 = new Customer("customeId1", "contactName", "11",
                "contactPhone", "address", "customerName", "invoiceTitle", "memo", "pinyin",
                "initials");
        customerB1.setGroup("b");

        Customer customerB2 = new Customer("customeId1", "contactName", "11",
                "contactPhone", "address", "customerName", "invoiceTitle", "memo", "pinyin",
                "initials");
        customerB2.setGroup("b");

        Customer customerB3 = new Customer("customeId1", "contactName", "11",
                "contactPhone", "address", "customerName", "invoiceTitle", "memo", "pinyin",
                "initials");
        customerB3.setGroup("b");

        CustomerDAO.addCustomer(customerA1, "a");
        CustomerDAO.addCustomer(customerA2, "a");
        CustomerDAO.addCustomer(customerA3, "a");
        CustomerDAO.addCustomer(customerB1, "a");
        CustomerDAO.addCustomer(customerB2, "a");
        CustomerDAO.addCustomer(customerB3, "a");

        String jsonGroupStr = CustomerDAO.getAllCustomerWithGroup("11");
        Log.v("CustomerTest", jsonGroupStr);

        assertNotNull(jsonGroupStr);

    }


}
