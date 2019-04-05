package net.aldar.cramello.model.response.basket;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Validation {
    @SerializedName("line")
    @Expose
    private Integer line;
    @SerializedName("errors")
    @Expose
    private List<String> errors = null;

    public Integer getLine() {
        return line;
    }

    public void setLine(Integer line) {
        this.line = line;
    }

    public List<String> getErrors() {
        return errors;
    }

    public void setErrors(List<String> errors) {
        this.errors = errors;
    }
}
