package com.inwebo.api.sample.entities;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


public class Group implements Serializable {

    private Long id;

    private String name;

    private Long servicePolicy;

    private List<User> users;

    public Group() {
        users = new ArrayList<>();
    }

    public Group(final Long id, final String name, final Long servicePolicy) {
        this();
        this.id = id;
        this.name = name;
        this.servicePolicy = servicePolicy;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Long getServicePolicy() {
        return servicePolicy;
    }

    public List<User> getUsers() {
        return users;
    }
}
