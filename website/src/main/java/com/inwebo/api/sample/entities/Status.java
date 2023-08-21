package com.inwebo.api.sample.entities;


public enum Status {

    ACTIF(0),
    INACTIF(1);

    private long status;

    private Status(final long status) {
        this.status = status;
    }

    public static Status from(final long value) {
        for (final Status status : Status.values()) {
            if (value == status.getStatus()) {
                return status;
            }
        }
        throw new EnumConstantNotPresentException(Status.class, String.valueOf(value));
    }

    public long getStatus() {
        return status;
    }
}
