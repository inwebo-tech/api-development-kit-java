package com.inwebo.api.sample.entities;

import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import static com.inwebo.api.sample.entities.Role.USER;
import static com.inwebo.api.sample.entities.Status.INACTIF;


public class User implements Serializable {

    private Long id;

    @NotEmpty
    private String login;

    @NotEmpty
    private String firstName;

    @NotEmpty
    private String lastName;

    @Email
    private String email;

    private String phone;

    @NotNull
    private Role role = USER;

    @NotNull
    private Status status = INACTIF;

    private boolean bookmarks;

    private CodeType codeType;

    private List<Group> groups = new ArrayList<>();

    public User() {
    }

    public User(final Long id,
                final String login,
                final String firstName,
                final String lastName,
                final String email,
                final String phone,
                final Role role,
                final Status status) {
        this();
        this.id = id;
        this.login = login;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phone = phone;
        this.role = role;
        this.status = status;
    }

    public User(final String login,
                final String firstName,
                final String lastName,
                final String email,
                final String phone,
                final Role role,
                final Status status) {
        this(null, login, firstName, lastName, email, phone, role, status);
    }

    public boolean isNew() {
        return (this.id == null);
    }

    public long getId() {
        return id;
    }

    public List<Group> getGroups() {
        return groups;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(final String login) {
        this.login = login;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(final String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(final String lastName) {
        this.lastName = lastName;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(final Role role) {
        this.role = role;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(final Status status) {
        this.status = status;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(final String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(final String phone) {
        this.phone = phone;
    }

    public boolean getBookmarks() {
        return bookmarks;
    }

    public void setBookmarks(final boolean bookmarks) {
        this.bookmarks = bookmarks;
    }

    public CodeType getCodeType() {
        return codeType;
    }

    public void setCodeType(final CodeType codeType) {
        this.codeType = codeType;
    }
}
