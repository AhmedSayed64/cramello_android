package net.aldar.cramello.model.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class CheckoutResponse {
    @SerializedName("basket")
    @Expose
    private Integer basket;
    @SerializedName("payment_method")
    @Expose
    private Integer paymentMethod;
    @SerializedName("payment_url")
    @Expose
    private String payment_url;
    @SerializedName("order")
    @Expose
    private Integer order;
    @SerializedName("order_number")
    @Expose
    private String orderNumber;

    public Integer getBasket() {
        return basket;
    }

    public void setBasket(Integer basket) {
        this.basket = basket;
    }

    public Integer getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(Integer paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getPayment_url() {
        return payment_url;
    }

    public void setPayment_url(String payment_url) {
        this.payment_url = payment_url;
    }

    public Integer getOrder() {
        return order;
    }

    public void setOrder(Integer order) {
        this.order = order;
    }

    public String getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(String orderNumber) {
        this.orderNumber = orderNumber;
    }
}
