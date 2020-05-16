package com.testdrivingexpert.webinar;

import java.util.ArrayList;
import java.util.List;

public class WebinarService {
    private Webinar registeredWebinar;
    private List<Participant> registeredParticipants = new ArrayList<>();

    public void registerParticipant(Participant toRegister, String webinarName) {
        if (registeredWebinar == null || !registeredWebinar.getName().equals(webinarName)) {
            throw new IllegalArgumentException(String.format("Webinar with name '%s' does not exist", webinarName));
        }

        registeredParticipants.add(toRegister);
    }

    public void registerWebinar(Webinar toRegister) {
        registeredWebinar = toRegister;
    }

    public List<Participant> getRegisteredParticipants(String webinarName) {
        return new ArrayList<>(registeredParticipants);
    }
}
