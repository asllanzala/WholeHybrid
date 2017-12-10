package com.honeywell.wholesale.ui.transaction.preorders.network;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by H154326 on 17/5/2.
 * Email: yang.liu6@honeywell.com
 */

public class PreOrderUpdateRequest {

    @SerializedName("shop_id")
    private int shopId;
    @SerializedName("preorder_customer")
    private PreOrderCustomer preOrderCustomer;
    @SerializedName("preorder_product")
    private PreOrderProduct preOrderProduct;
    @SerializedName("total_price")
    private String totalPrice;
    @SerializedName("address")
    private String address;
    @SerializedName("contact_name")
    private String contactName;
    @SerializedName("contact_phone")
    private String contactPhone;
    @SerializedName("remark")
    private String remark;
    @SerializedName("discount")
    private String discount;
    @SerializedName("customer_company_name")
    private String customerCompanyName;
    @SerializedName("customer_phone")
    private String customerPhone;
    @SerializedName("invoice_title")
    private String invoiceTitle;

    public PreOrderUpdateRequest() {
    }

    public int getShopId() {
        return shopId;
    }

    public void setShopId(int shopId) {
        this.shopId = shopId;
    }

    public PreOrderCustomer getPreOrderCustomer() {
        return preOrderCustomer;
    }

    public void setPreOrderCustomer(PreOrderCustomer preOrderCustomer) {
        this.preOrderCustomer = preOrderCustomer;
    }

    public PreOrderProduct getPreOrderProduct() {
        return preOrderProduct;
    }

    public void setPreOrderProduct(PreOrderProduct preOrderProduct) {
        this.preOrderProduct = preOrderProduct;
    }

    public String getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(String totalPrice) {
        this.totalPrice = totalPrice;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getContactName() {
        return contactName;
    }

    public void setContactName(String contactName) {
        this.contactName = contactName;
    }

    public String getContactPhone() {
        return contactPhone;
    }

    public void setContactPhone(String contactPhone) {
        this.contactPhone = contactPhone;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getDiscount() {
        return discount;
    }

    public void setDiscount(String discount) {
        this.discount = discount;
    }

    public String getCustomerCompanyName() {
        return customerCompanyName;
    }

    public void setCustomerCompanyName(String customerCompanyName) {
        this.customerCompanyName = customerCompanyName;
    }

    public String getCustomerPhone() {
        return customerPhone;
    }

    public void setCustomerPhone(String customerPhone) {
        this.customerPhone = customerPhone;
    }

    public String getInvoiceTitle() {
        return invoiceTitle;
    }

    public void setInvoiceTitle(String invoiceTitle) {
        this.invoiceTitle = invoiceTitle;
    }

    private class PreOrderCustomer{
        @SerializedName("customer_id")
        private int customeId;
        @SerializedName("customer_name")
        private String customerName;

        public PreOrderCustomer() {
        }

        public int getCustomeId() {
            return customeId;
        }

        public void setCustomeId(int customeId) {
            this.customeId = customeId;
        }

        public String getCustomerName() {
            return customerName;
        }

        public void setCustomerName(String customerName) {
            this.customerName = customerName;
        }
    }

    private class PreOrderProduct{
        @SerializedName("product_id")
        private int productId;
        @SerializedName("product_sku")
        private ProductSku productSku;

        public PreOrderProduct() {
        }

        public int getProductId() {
            return productId;
        }

        public void setProductId(int productId) {
            this.productId = productId;
        }

        public ProductSku getProductSku() {
            return productSku;
        }

        public void setProductSku(ProductSku productSku) {
            this.productSku = productSku;
        }

        private class ProductSku{
            @SerializedName("inventory_id")
            private int inventoryId;
            @SerializedName("sale_price")
            private String salePrice;
            @SerializedName("warehouse_sku_list")
            private warehouseSkuList warehouseSkuList;

            public ProductSku() {
            }

            public int getInventoryId() {
                return inventoryId;
            }

            public void setInventoryId(int inventoryId) {
                this.inventoryId = inventoryId;
            }

            public String getSalePrice() {
                return salePrice;
            }

            public void setSalePrice(String salePrice) {
                this.salePrice = salePrice;
            }

            public ProductSku.warehouseSkuList getWarehouseSkuList() {
                return warehouseSkuList;
            }

            public void setWarehouseSkuList(ProductSku.warehouseSkuList warehouseSkuList) {
                this.warehouseSkuList = warehouseSkuList;
            }

            private class warehouseSkuList{
                @SerializedName("warehouse_inventory_id")
                private int warehouseInventoryId;
                @SerializedName("quantity")
                private String quantity;


                public warehouseSkuList() {
                }

                public int getWarehouseInventoryId() {
                    return warehouseInventoryId;
                }

                public void setWarehouseInventoryId(int warehouseInventoryId) {
                    this.warehouseInventoryId = warehouseInventoryId;
                }

                public String getQuantity() {
                    return quantity;
                }

                public void setQuantity(String quantity) {
                    this.quantity = quantity;
                }
            }
        }
    }
    public String getJsonString() {
        return new Gson().toJson(this);
    }

    public JSONObject getJsonObject() {
        try {
            return new JSONObject(getJsonString());
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }
}
