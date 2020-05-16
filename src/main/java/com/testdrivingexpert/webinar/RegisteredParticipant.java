package com.testdrivingexpert.webinar;

public class RegisteredParticipant {
    private Participant participant;
    private String token;

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
}
