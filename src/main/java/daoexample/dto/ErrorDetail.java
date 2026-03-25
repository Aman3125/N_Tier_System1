package daoexample.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ErrorDetail {

    @JsonProperty("code")
    private int code;

    @JsonProperty("details")
    private String details;

    public ErrorDetail() {}

    public ErrorDetail(int code, String details) {
        this.code = code;
        this.details = details;
    }

    public int getCode() { return code; }
    public void setCode(int code) { this.code = code; }

    public String getDetails() { return details; }
    public void setDetails(String details) { this.details = details; }
}