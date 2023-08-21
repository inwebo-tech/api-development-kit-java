package com.inwebo.api.sample.entities;

import java.io.Serializable;


public class Authentification2StepForm implements Serializable {

    public static final String GOOD_PASSWORD = "12345";

    private String login;

    private String password;

    public Authentification2StepForm() {
    }

    public Authentification2StepForm(final String login, final String password) {
        this();
        this.login = login;
        this.password = password;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(final String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(final String password) {
        this.password = password;
    }
}
