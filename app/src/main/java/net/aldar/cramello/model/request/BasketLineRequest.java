package net.aldar.cramello.model.request;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class BasketLineRequest {
    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("basket")
    @Expose
    private Integer basket;
    @SerializedName("product")
    @Expose
    private Integer product;
    @SerializedName("amount")
    @Expose
    private Double amount;
    @SerializedName("quantity")
    @Expose
    private Integer quantity;
    @SerializedName("voucher_discount")
    @Expose
    private Double voucherDiscount;
    @SerializedName("comment")
    @Expose
    private String comment;

    private boolean synced = false;

    public BasketLineRequest(Integer basket, Integer product, Double amount, Integer quantity, String comment) {
        this.basket = basket;
        this.product = product;
        this.amount = amount;
        this.quantity = quantity;
        this.comment = comment;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getBasket() {
        return basket;
    }

    public void setBasket(Integer basket) {
        this.basket = basket;
    }

    public Integer getProduct() {
        return product;
    }

    public void setProduct(Integer product) {
        this.product = product;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public Double getVoucherDiscount() {
        return voucherDiscount;
    }

    public void setVoucherDiscount(Double voucherDiscount) {
        this.voucherDiscount = voucherDiscount;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public boolean isSynced() {
        return synced;
    }

    public void setSynced(boolean synced) {
        this.synced = synced;
    }
}
