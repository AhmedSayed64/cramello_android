package net.aldar.cramello.model.request;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class BasketValidationRequest {
    @SerializedName("pk")
    @Expose
    private Integer pk;

    public BasketValidationRequest(Integer pk) {
        this.pk = pk;
    }

    public Integer getPk() {
        return pk;
    }

    public void setPk(Integer pk) {
        this.pk = pk;
    }
}
