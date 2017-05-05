package com.deva.androiduser2.hi_focus.Product;

/**
 * Created by androiduser2 on 8/4/16.
 */
public class ProductList {
    private String model;
    private String color;
    private String model1;


    public ProductList(String model, String color,String model1) {
        super();

        this.model = model;
        this.color = color;
        this.model1 = model1;



    }

    public void setProductName(String Med1) {
        this.model = Med1;
    }

    public void setProductId(String Med1) {
        this.model1 = Med1;
    }

    public void setProductImage(String Med2) {
        this.color = Med2;
    }

    public String getProductId() {
        return model1;
    }

    public String getProductName() {
        return model;
    }

    public String getProductImage() {
        return color;
    }




}
