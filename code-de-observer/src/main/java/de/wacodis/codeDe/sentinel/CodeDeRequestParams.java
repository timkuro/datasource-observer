package de.wacodis.codeDe.sentinel;

import org.joda.time.DateTime;

import java.util.List;

public class CodeDeRequestParams {

    public String parentIdentifier;
    public DateTime startDate;
    public DateTime endDate;
    public List<Float> bbox;
    public List<Byte> cloudCover;

    public CodeDeRequestParams(String parentIdentifier, DateTime startDate, DateTime endDate, List<Float> bbox, List<Byte> cloudCover) {
        this.parentIdentifier = parentIdentifier;
        this.startDate = startDate;
        this.endDate = endDate;
        this.bbox = bbox;
        this.cloudCover = cloudCover;
    }

    public String getParentIdentifier() {
        return parentIdentifier;
    }

    public void setParentIdentifier(String parentIdentifier) {
        this.parentIdentifier = parentIdentifier;
    }

    public DateTime getStartDate() {
        return startDate;
    }

    public void setStartDate(DateTime startDate) {
        this.startDate = startDate;
    }

    public DateTime getEndDate() {
        return endDate;
    }

    public void setEndDate(DateTime endDate) {
        this.endDate = endDate;
    }

    public List<Float> getBbox() {
        return bbox;
    }

    public void setBbox(List<Float> bbox) {
        this.bbox = bbox;
    }

    public List<Byte> getCloudCover() {
        return cloudCover;
    }

    public void setCloudCover(List<Byte> cloudCover) {
        this.cloudCover = cloudCover;
    }
}
