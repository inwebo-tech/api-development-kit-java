package com.inwebo.api.sample.entities;


public class AuthentificationTestForm {

    private CallType callType;
    private String login;
    private String otp;

    public AuthentificationTestForm() {
    }

    public AuthentificationTestForm(final CallType callType) {
        this();
        this.callType = callType;
    }

    public CallType getCallType() {
        return callType;
    }

    public void setCallType(final CallType callType) {
        this.callType = callType;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(final String login) {
        this.login = login;
    }

    public String getOtp() {
        return otp;
    }

    public void setOtp(String otp) {
        this.otp = otp;
    }
}
