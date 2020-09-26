package edu.uci.ics.AESMERAL.service.billing.models.ResponseModels;

import com.fasterxml.jackson.annotation.JsonProperty;
import edu.uci.ics.AESMERAL.service.billing.core.ThumbnailModel;

public class ThumbnailResponseModel extends MicroserviceResponseModel {
    @JsonProperty(value = "thumbnails",required = true)
    private ThumbnailModel [] thumbnails;

    public ThumbnailResponseModel() {
    }

    public ThumbnailResponseModel(@JsonProperty(value = "resultCode",required = true) int resultCode,
                                  @JsonProperty(value = "message", required = true) String message,
                                  @JsonProperty(value = "thumbnails",required = true) ThumbnailModel[] thumbnails) {
        super(resultCode, message);
        this.thumbnails = thumbnails;
    }

    public ThumbnailModel[] getThumbnails() {
        return thumbnails;
    }

    public void setThumbnails(ThumbnailModel[] thumbnails) {
        this.thumbnails = thumbnails;
    }
}
