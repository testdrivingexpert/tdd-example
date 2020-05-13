package com.testdrivingexpert.webinar;

public class WebinarService {
    public void registerParticipant(Participant toRegister, String webinarName) {
        throw new IllegalArgumentException(String.format("Webinar with name '%s' does not exist", webinarName));
    }
}
