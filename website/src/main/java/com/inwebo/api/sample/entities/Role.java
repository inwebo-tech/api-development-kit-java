package com.inwebo.api.sample.entities;


public enum Role {

    USER(0),
    MANAGER(1),
    ADMIN(2);

    private long role;

    private Role(final long role) {
        this.role = role;
    }

    public static Role from(final long value) {
        for (final Role status : Role.values()) {
            if (value == status.getRole()) {
                return status;
            }
        }
        throw new EnumConstantNotPresentException(Role.class, String.valueOf(value));
    }

    public long getRole() {
        return role;
    }
}
