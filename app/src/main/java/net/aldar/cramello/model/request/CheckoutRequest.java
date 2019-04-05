package net.aldar.cramello.model.request;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import net.aldar.cramello.model.Address;

public class CheckoutRequest {
    @SerializedName("basket")
    @Expose
    private Integer basket;
    @SerializedName("payment_method")
    @Expose
    private Integer paymentMethod;
    @SerializedName("shipping_address")
    @Expose
    private Address shippingAddress;

    public CheckoutRequest(Integer basket, Integer paymentMethod, Address shippingAddress) {
        this.basket = basket;
        this.paymentMethod = paymentMethod;
        this.shippingAddress = shippingAddress;
    }

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

    public Address getShippingAddress() {
        return shippingAddress;
    }

    public void setShippingAddress(Address shippingAddress) {
        this.shippingAddress = shippingAddress;
    }
}
