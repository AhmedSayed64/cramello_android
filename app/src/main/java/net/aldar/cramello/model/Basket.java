package net.aldar.cramello.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import net.aldar.cramello.model.response.basket.BasketLine;

import java.util.List;

public class Basket {
    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("status")
    @Expose
    private Integer status;
    @SerializedName("owner")
    @Expose
    private Integer owner;
    @SerializedName("lines")
    @Expose
    private List<BasketLine> lines = null;
    @SerializedName("area")
    @Expose
    private Integer area;
    @SerializedName("voucher")
    @Expose
    private String voucher;
    @SerializedName("voucher_discount")
    @Expose
    private Double voucherDiscount;
    @SerializedName("voucher_code")
    @Expose
    private String voucherCode;
    @SerializedName("basket_total")
    @Expose
    private Double basketTotal;
    @SerializedName("basket_total_no_discount")
    @Expose
    private Double basketTotalNoDiscount;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getOwner() {
        return owner;
    }

    public void setOwner(Integer owner) {
        this.owner = owner;
    }

    public List<BasketLine> getLines() {
        return lines;
    }

    public void setLines(List<BasketLine> lines) {
        this.lines = lines;
    }

    public Integer getArea() {
        return area;
    }

    public void setArea(Integer area) {
        this.area = area;
    }

    public String getVoucher() {
        return voucher;
    }

    public void setVoucher(String voucher) {
        this.voucher = voucher;
    }

    public Double getVoucherDiscount() {
        return voucherDiscount;
    }

    public void setVoucherDiscount(Double voucherDiscount) {
        this.voucherDiscount = voucherDiscount;
    }

    public String getVoucherCode() {
        return voucherCode;
    }

    public void setVoucherCode(String voucherCode) {
        this.voucherCode = voucherCode;
    }

    public Double getBasketTotal() {
        return basketTotal;
    }

    public void setBasketTotal(Double basketTotal) {
        this.basketTotal = basketTotal;
    }

    public Double getBasketTotalNoDiscount() {
        return basketTotalNoDiscount;
    }

    public void setBasketTotalNoDiscount(Double basketTotalNoDiscount) {
        this.basketTotalNoDiscount = basketTotalNoDiscount;
    }
}
