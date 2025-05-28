package com.ibs.vi.model;

import java.time.Duration;

public class Layover {
    private String duration;
    private String selfTransfer;

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getSelfTransfer() {
        return selfTransfer;
    }

    public void setSelfTransfer(String selfTransfer) {
        this.selfTransfer = selfTransfer;
    }
}
