package com.ibs.vi.view;

public class BasicResponseView {
    private Object response;

    public BasicResponseView() {
        this.response = true;
    }

    public BasicResponseView(Object response) {
        this.response = response;
    }

    public Object getResponse() {
        return response;
    }

    public void setResponse(Object response) {
        this.response = response;
    }
}
