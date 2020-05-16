package com.testdrivingexpert.webinar;

public class RegisteredParticipant {
    private Participant participant;
    private String token;
    private boolean emailConfirmed;

    public RegisteredParticipant(Participant participant, String token) {
        this.participant = participant;
        this.token = token;
    }

    public Participant getParticipant() {
        return participant;
    }

    public String getToken() {
        return token;
    }

    public String getEmail() {
        return participant.getEmail();
    }

    public boolean isEmailConfirmed() {
        return emailConfirmed;
    }

    public void setEmailConfirmed(boolean emailConfirmed) {
        this.emailConfirmed = emailConfirmed;
    }
}
