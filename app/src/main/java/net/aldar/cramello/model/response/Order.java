package net.aldar.cramello.model.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import net.aldar.cramello.model.Address;
import net.aldar.cramello.model.response.basket.BasketLine;

import java.util.List;

public class Order {
    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("number")
    @Expose
    private String number;
    @SerializedName("created")
    @Expose
    private String created;
    @SerializedName("total_excl_tax")
    @Expose
    private Double totalExclTax;
    @SerializedName("total_incl_tax")
    @Expose
    private Double totalInclTax;
    @SerializedName("basket")
    @Expose
    private Integer basket;
    @SerializedName("lines")
    @Expose
    private List<BasketLine> lines = null;
    @SerializedName("shipping_price")
    @Expose
    private Double shippingPrice;
    @SerializedName("payment_method_display")
    @Expose
    private String paymentMethodDisplay;
    @SerializedName("shipping_address")
    @Expose
    private Address shippingAddress;
    @SerializedName("payment_method")
    @Expose
    private Integer paymentMethod;
    @SerializedName("email")
    @Expose
    private String email;
    @SerializedName("phone_number")
    @Expose
    private String phoneNumber;
    @SerializedName("transaction")
    @Expose
    private Integer transaction;
    @SerializedName("migs_transaction")
    @Expose
    private Integer migsTransaction;
    @SerializedName("status")
    @Expose
    private Integer status;
    @SerializedName("view_status")
    @Expose
    private Integer viewStatus;
    @SerializedName("first_name")
    @Expose
    private String firstName;
    @SerializedName("last_name")
    @Expose
    private String lastName;
    @SerializedName("voucher_discount")
    @Expose
    private Double voucherDiscount;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }

    public Double getTotalExclTax() {
        return totalExclTax;
    }

    public void setTotalExclTax(Double totalExclTax) {
        this.totalExclTax = totalExclTax;
    }

    public Double getTotalInclTax() {
        return totalInclTax;
    }

    public void setTotalInclTax(Double totalInclTax) {
        this.totalInclTax = totalInclTax;
    }

    public Integer getBasket() {
        return basket;
    }

    public void setBasket(Integer basket) {
        this.basket = basket;
    }

    public List<BasketLine> getLines() {
        return lines;
    }

    public void setLines(List<BasketLine> lines) {
        this.lines = lines;
    }

    public Double getShippingPrice() {
        return shippingPrice;
    }

    public void setShippingPrice(Double shippingPrice) {
        this.shippingPrice = shippingPrice;
    }

    public String getPaymentMethodDisplay() {
        return paymentMethodDisplay;
    }

    public void setPaymentMethodDisplay(String paymentMethodDisplay) {
        this.paymentMethodDisplay = paymentMethodDisplay;
    }

    public Address getShippingAddress() {
        return shippingAddress;
    }

    public void setShippingAddress(Address shippingAddress) {
        this.shippingAddress = shippingAddress;
    }

    public Integer getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(Integer paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public Integer getTransaction() {
        return transaction;
    }

    public void setTransaction(Integer transaction) {
        this.transaction = transaction;
    }

    public Integer getMigsTransaction() {
        return migsTransaction;
    }

    public void setMigsTransaction(Integer migsTransaction) {
        this.migsTransaction = migsTransaction;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public Integer getViewStatus() {
        return viewStatus;
    }

    public void setViewStatus(Integer viewStatus) {
        this.viewStatus = viewStatus;
    }

    public Double getVoucherDiscount() {
        return voucherDiscount;
    }

    public void setVoucherDiscount(Double voucherDiscount) {
        this.voucherDiscount = voucherDiscount;
    }
}
