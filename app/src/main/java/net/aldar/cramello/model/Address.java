package net.aldar.cramello.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import net.aldar.cramello.model.response.governorate.Area;
import net.aldar.cramello.model.response.governorate.Governorate;

public class Address {
    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("title")
    @Expose
    private String title;
    @SerializedName("owner")
    @Expose
    private Integer owner;
    @SerializedName("address_type")
    @Expose
    private Integer addressType;
    @SerializedName("block")
    @Expose
    private String block;
    @SerializedName("street")
    @Expose
    private String street;
    @SerializedName("area")
    @Expose
    private Integer area;
    @SerializedName("avenue")
    @Expose
    private String avenue;
    @SerializedName("floor")
    @Expose
    private String floor;
    @SerializedName("building")
    @Expose
    private String building;
    @SerializedName("apartment_no")
    @Expose
    private String apartment;
    @SerializedName("additional_notes")
    @Expose
    private String additionalNotes;

    private Area selectedArea;
    private Governorate selectedGovernorate;

    public Address(String title, Integer owner, Integer addressType, String block, String street,
                   Integer area, String avenue, String floor, String building, String apartment, String additionalNotes) {
        this.title = title;
        this.owner = owner;
        this.addressType = addressType;
        this.block = block;
        this.street = street;
        this.area = area;
        this.avenue = avenue;
        this.floor = floor;
        this.building = building;
        this.apartment = apartment;
        this.additionalNotes = additionalNotes;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Integer getOwner() {
        return owner;
    }

    public void setOwner(Integer owner) {
        this.owner = owner;
    }

    public Integer getAddressType() {
        return addressType;
    }

    public void setAddressType(Integer addressType) {
        this.addressType = addressType;
    }

    public String getBlock() {
        return block;
    }

    public void setBlock(String block) {
        this.block = block;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public Integer getArea() {
        return area;
    }

    public void setArea(Integer area) {
        this.area = area;
    }

    public String getAvenue() {
        return avenue;
    }

    public void setAvenue(String avenue) {
        this.avenue = avenue;
    }

    public String getFloor() {
        return floor;
    }

    public void setFloor(String floor) {
        this.floor = floor;
    }

    public String getBuilding() {
        return building;
    }

    public void setBuilding(String building) {
        this.building = building;
    }

    public String getApartment() {
        return apartment;
    }

    public void setApartment(String apartment) {
        this.apartment = apartment;
    }

    public String getAdditionalNotes() {
        return additionalNotes;
    }

    public void setAdditionalNotes(String additionalNotes) {
        this.additionalNotes = additionalNotes;
    }

    public Area getSelectedArea() {
        return selectedArea;
    }

    public void setSelectedArea(Area selectedArea) {
        this.selectedArea = selectedArea;
    }
}
