package net.aldar.cramello.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class FirebaseData {
    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("registration_id")
    @Expose
    private String registrationId;
    @SerializedName("device_id")
    @Expose
    private String deviceId;
    @SerializedName("active")
    @Expose
    private Boolean active;
    @SerializedName("date_created")
    @Expose
    private String dateCreated;
    @SerializedName("type")
    @Expose
    private String type;

    public FirebaseData(String name, String registrationId, String deviceId, String type) {
        this.name = name;
        this.registrationId = registrationId;
        this.deviceId = deviceId;
        this.type = type;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRegistrationId() {
        return registrationId;
    }

    public void setRegistrationId(String registrationId) {
        this.registrationId = registrationId;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public String getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(String dateCreated) {
        this.dateCreated = dateCreated;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
