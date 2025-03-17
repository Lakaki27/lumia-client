package com.lumia;

public class ProductResponse {
    private boolean success;
    private String productName;
    private double productPrice;
    private String productBarcode;

    public ProductResponse(boolean success, String productName, double productPrice, String productBarcode) {
        this.success = success;
        this.productName = productName;
        this.productPrice = productPrice;
        this.productBarcode = productBarcode;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public double getProductPrice() {
        return productPrice;
    }

    public void setProductPrice(double productPrice) {
        this.productPrice = productPrice;
    }

    public String getProductBarcode() {
        return productBarcode;
    }

    public void setProductBarcode(String productBarcode) {
        this.productBarcode = productBarcode;
    }
}
