package com.deva.androiduser2.hi_focus;

/**
 * Created by androiduser2 on 16/4/16.
 */
public class GetAllDetails {
    private String model;
    private String color;
    private String model1;
    private String model2;
    private String model3;
    private String model4;
    private String model5;
    private String model6;
    private String model7;


    private String inProgress;

    private String getClosed;



    private String ticketStatusName;



    public GetAllDetails(String model, String color,String model1,String model2,String model3,String model4,String model5,String model6,String model7,String model8,String model9,String inProgress) {
        super();

        this.model = model;
        this.color = color;
        this.model1 = model1;
        this.model2 = model2;
        this.model3 = model3;
        this.model4 = model4;
        this.model5 = model5;
        this.model6 = model6;
        this.model7 = model7;
        this.getClosed = model8;
        this.ticketStatusName = model9;
        this.inProgress= inProgress;


    }

    public String getTicketStatusName() {
        return ticketStatusName;
    }

    public void setTicketStatusName(String ticketStatusName) {
        this.ticketStatusName = ticketStatusName;
    }
    public String getInProgress() {
        return inProgress;
    }

    public void setInProgress(String inProgress) {
        this.inProgress = inProgress;
    }


    public String getGetClosed() {
        return getClosed;
    }

    public void setGetClosed(String getClosed) {
        this.getClosed = getClosed;
    }

    public void setProductName(String Med1) {
        this.model = Med1;
    }

    public void setProductId(String Med1) {
        this.model1 = Med1;
    }

    public void setProductTicket(String Med2) {
        this.color = Med2;
    }

    public void setProductMobile(String Med3) {
        this.model2 = Med3;
    }

    public void setProductStatus(String Med4) {
        this.model3 = Med4;
    }

    public void setProductDate(String Med5) {
        this.model4 = Med5;
    }

    public void setEmailId(String Med6) {
        this.model5 = Med6;
    }

    public void setAssignedTo(String Med7) {
        this.model6 = Med7;
    }

    public void setAssignedToName(String Med8) {
        this.model7 = Med8;
    }

    public String getProductId() {
        return model1;
    }

    public String getProductName() {
        return model;
    }

    public String getProductTicket() {
        return color;
    }

    public String getProductMobile() {
        return model2;
    }

    public String getProductStatus() {
        return model3;
    }

    public String getProductDate() {
        return model4;
    }
    public String getEmail() {
        return model5;
    }

    public String getAssignedTo() {
        return model6;
    }
    public String getAssignedToName() {
        return model7;
    }
}
