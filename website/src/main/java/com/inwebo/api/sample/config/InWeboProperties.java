package com.inwebo.api.sample.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

@ConfigurationProperties(prefix = "inwebo.api", ignoreUnknownFields = false)
public class InWeboProperties {

    @Valid
    private Certificate certificate;

    private int serviceId;

    @NotNull
    private String restBaseUrl;

    public Certificate getCertificate() {
        return certificate;
    }

    public void setCertificate(final Certificate certificate) {
        this.certificate = certificate;
    }

    public int getServiceId() {
        return serviceId;
    }

    public void setServiceId(final int serviceId) {
        this.serviceId = serviceId;
    }

    public String getRestBaseUrl() {
        return restBaseUrl;
    }

    public void setRestBaseUrl(final String restBaseUrl) {
        this.restBaseUrl = restBaseUrl;
    }

    public static class Certificate {

        @NotNull
        private String path;

        @NotNull
        private String password;

        public String getPath() {
            return path;
        }

        public void setPath(final String path) {
            this.path = path;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }
    }
}
