package com.deva.androiduser2.hi_focus.Product.Ticket;

/**
 * Created by PRMobile on 5/30/16.
 */
public class AssignedModel {

    String id,username;


    public AssignedModel(String id, String username) {
        this.id = id;
        this.username = username;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
