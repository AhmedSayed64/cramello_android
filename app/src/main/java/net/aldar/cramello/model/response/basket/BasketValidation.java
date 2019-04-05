package net.aldar.cramello.model.response.basket;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class BasketValidation {
    @SerializedName("errors")
    @Expose
    private Integer errors;
    @SerializedName("validation")
    @Expose
    private List<Validation> validation = null;

    public Integer getErrors() {
        return errors;
    }

    public void setErrors(Integer errors) {
        this.errors = errors;
    }

    public List<Validation> getValidation() {
        return validation;
    }

    public void setValidation(List<Validation> validation) {
        this.validation = validation;
    }

}
