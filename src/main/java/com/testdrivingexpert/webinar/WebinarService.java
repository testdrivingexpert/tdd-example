package com.testdrivingexpert.webinar;

import java.util.List;

public class WebinarService {
    public void registerParticipant(Participant toRegister, String webinarName) {
        throw new IllegalArgumentException(String.format("Webinar with name '%s' does not exist", webinarName));
    }

    public void registerWebinar(Webinar toRegister) {

    }

    public List<Participant> getRegisteredParticipants() {
        return null;
    }
}
