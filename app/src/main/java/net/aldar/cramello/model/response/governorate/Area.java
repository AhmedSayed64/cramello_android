package net.aldar.cramello.model.response.governorate;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Area {
    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("order")
    @Expose
    private Integer order;
    @SerializedName("status")
    @Expose
    private Integer status;
    @SerializedName("name_en")
    @Expose
    private String nameEn;
    @SerializedName("name_ar")
    @Expose
    private String nameAr;
    @SerializedName("fee")
    @Expose
    private Double fee;
    @SerializedName("branches")
    @Expose
    private List<Integer> branches = null;
    @SerializedName("is_available")
    @Expose
    private Boolean isAvailable;
    @SerializedName("area_minimum_order")
    @Expose
    private String area_minimum_order;
    @SerializedName("delivery_time_en")
    @Expose
    private String deliveryTimeEn;
    @SerializedName("delivery_time_ar")
    @Expose
    private String deliveryTimeAr;
    @SerializedName("governorate")
    @Expose
    private Governorate governorate = null;
    @SerializedName("is_busy")
    @Expose
    private Boolean is_busy;
    @SerializedName("is_closed")
    @Expose
    private Boolean is_closed;

    public Boolean getIs_busy() {
        return is_busy;
    }

    public void setIs_busy(Boolean is_busy) {
        this.is_busy = is_busy;
    }

    public Boolean getIs_closed() {
        return is_closed;
    }

    public void setIs_closed(Boolean is_closed) {
        this.is_closed = is_closed;
    }

    public Governorate getGovernorate() {
        return governorate;
    }

    public void setGovernorate(Governorate governorate) {
        this.governorate = governorate;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getOrder() {
        return order;
    }

    public void setOrder(Integer order) {
        this.order = order;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getNameEn() {
        return nameEn;
    }

    public void setNameEn(String nameEn) {
        this.nameEn = nameEn;
    }

    public String getNameAr() {
        return nameAr;
    }

    public void setNameAr(String nameAr) {
        this.nameAr = nameAr;
    }

    public Double getFee() {
        return fee;
    }

    public void setFee(Double fee) {
        this.fee = fee;
    }

    public List<Integer> getBranches() {
        return branches;
    }

    public void setBranches(List<Integer> branches) {
        this.branches = branches;
    }

    public Boolean getAvailable() {
        return isAvailable;
    }

    public void setAvailable(Boolean available) {
        isAvailable = available;
    }

    public String getArea_minimum_order() {
        return area_minimum_order;
    }

    public void setArea_minimum_order(String area_minimum_order) {
        this.area_minimum_order = area_minimum_order;
    }

    public String getDeliveryTimeEn() {
        return deliveryTimeEn;
    }

    public void setDeliveryTimeEn(String deliveryTimeEn) {
        this.deliveryTimeEn = deliveryTimeEn;
    }

    public String getDeliveryTimeAr() {
        return deliveryTimeAr;
    }

    public void setDeliveryTimeAr(String deliveryTimeAr) {
        this.deliveryTimeAr = deliveryTimeAr;
    }
}
