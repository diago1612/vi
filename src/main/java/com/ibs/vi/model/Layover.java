package com.ibs.vi.model;

import java.time.Duration;

public class Layover {
    private String duration;
    private boolean selfTransfer;

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public boolean isSelfTransfer() {
        return selfTransfer;
    }

    public void setSelfTransfer(boolean selfTransfer) {
        this.selfTransfer = selfTransfer;
    }
}
