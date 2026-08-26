package com.automation.mobile.config;

public class UserDataManager {

    private final UserDataReader userDataReader;

    public UserDataManager() {
        this.userDataReader = new UserDataReader();
    }

    public String getValidUserEmail() {
        return userDataReader.get("validUserEmail");
    }

    public String getValidUserPassword() {
        return userDataReader.get("validUserPassword");
    }
}