package net.aldar.cramello.model.response.basket;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import net.aldar.cramello.model.response.product.Product;

public class BasketLine {
    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("basket")
    @Expose
    private Integer basket;
    @SerializedName("product")
    @Expose
    private Product product;
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

    public BasketLine(Integer id, Integer basket, Double amount, Integer quantity, Double voucherDiscount, String comment) {
        this.id = id;
        this.basket = basket;
        this.amount = amount;
        this.quantity = quantity;
        this.voucherDiscount = voucherDiscount;
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

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
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

}
