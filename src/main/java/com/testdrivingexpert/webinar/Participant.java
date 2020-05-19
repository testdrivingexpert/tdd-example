package com.testdrivingexpert.webinar;

public class Participant {
    private String email;

    public Participant(String email) {
        this.email = email;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public String toString() {
        return "Participant{" +
                "email='" + email + '\'' +
                '}';
    }
}
