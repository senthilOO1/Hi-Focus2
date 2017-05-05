package com.deva.androiduser2.hi_focus;

/**
 * Created by PRMobile on 5/30/16.
 */
public class SetandGetProducts {

    int id;
    String productName,model,sinumber,description,quantity;

    public SetandGetProducts(int id, String productName, String model, String sinumber, String description, String quantity) {
        this.id = id;
        this.productName = productName;
        this.model = model;
        this.sinumber = sinumber;
        this.description = description;
        this.quantity = quantity;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getSinumber() {
        return sinumber;
    }

    public void setSinumber(String sinumber) {
        this.sinumber = sinumber;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }
}
