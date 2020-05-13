package com.testdrivingexpert.webinar;

import java.util.Collections;
import java.util.List;

public class WebinarService {
    private Webinar registeredWebinar;
    private Participant registeredParticipant;

    public void registerParticipant(Participant toRegister, String webinarName) {
        if (registeredWebinar == null || !registeredWebinar.getName().equals(webinarName)) {
            throw new IllegalArgumentException(String.format("Webinar with name '%s' does not exist", webinarName));
        }

        registeredParticipant = toRegister;
    }

    public void registerWebinar(Webinar toRegister) {
        registeredWebinar = toRegister;
    }

    public List<Participant> getRegisteredParticipants() {
        return Collections.singletonList(registeredParticipant);
    }
}
