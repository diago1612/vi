package com.ibs.vi.view;

import lombok.Data;

import java.util.Date;

public class RouteErrorResponseView {
    private  Integer status = 400;
    private  Date date = new Date();
    private  String error = "Bad Request";
    private String message;
    private String path;

    public RouteErrorResponseView(String message, String path){
        this.message = message;
        this.path = path;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
