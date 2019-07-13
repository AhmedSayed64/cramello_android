package net.aldar.cramello.model.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Video implements Serializable {

    private final static long serialVersionUID = -3947823308299462746L;
    @SerializedName("video")
    @Expose
    private String video;
    @SerializedName("order")
    @Expose
    private Integer order;
    @SerializedName("status")
    @Expose
    private Integer status;

    public String getVideo() {
        return video;
    }

    public void setVideo(String video) {
        this.video = video;
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

}
