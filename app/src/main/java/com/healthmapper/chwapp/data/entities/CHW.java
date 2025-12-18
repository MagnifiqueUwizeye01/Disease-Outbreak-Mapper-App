package com.healthmapper.chwapp.data.entities;

public class CHW {
    private String chwId;
    private String fullName;
    private String phoneNumber;
    private String email;
    private String nationalId;
    private String chwCode;
    private String password;

    public CHW(String chwId, String fullName, String phoneNumber,
               String email, String nationalId, String chwCode, String password) {
        this.chwId = chwId;
        this.fullName = fullName;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.nationalId = nationalId;
        this.chwCode = chwCode;
        this.password = password;
    }

    // Default constructor
    public CHW() {}

    // Getters and Setters
    public String getChwId() { return chwId; }
    public void setChwId(String chwId) { this.chwId = chwId; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getNationalId() { return nationalId; }
    public void setNationalId(String nationalId) { this.nationalId = nationalId; }

    public String getChwCode() { return chwCode; }
    public void setChwCode(String chwCode) { this.chwCode = chwCode; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
}