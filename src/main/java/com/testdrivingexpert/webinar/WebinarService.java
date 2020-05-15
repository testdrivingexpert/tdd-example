package com.testdrivingexpert.webinar;

public class WebinarService {
    public void registerParticipant(Participant toRegister, String webinarName) {
        throw new IllegalArgumentException("Webinar with name 'non-existent-webinar' does not exist");
    }
}
