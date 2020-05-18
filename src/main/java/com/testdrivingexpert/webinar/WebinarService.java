package com.testdrivingexpert.webinar;

import java.util.*;

public class WebinarService {
    private final List<Webinar> registeredWebinars = new ArrayList<>();
    private final Map<String, List<Participant>> registeredParticipants = new HashMap<>();

    public void registerParticipant(Participant toRegister, String webinarName) {
        if (!findWebinarWithName(webinarName).isPresent()) {
            throw new IllegalArgumentException(String.format("Webinar with name '%s' does not exist", webinarName));
        }

        List<Participant> participants = registeredParticipants.computeIfAbsent(webinarName, s -> new ArrayList<>());
        participants.add(toRegister);
    }

    public void registerWebinar(Webinar toRegister) {
        String name = toRegister.getName();

        if (findWebinarWithName(name).isPresent()) {
            throw new IllegalArgumentException(String.format("Duplicate webinar with name '%s'", name));
        }
        registeredWebinars.add(toRegister);
    }

    public List<Participant> getRegisteredParticipants(String webinarName) {
        return new ArrayList<>(registeredParticipants.get(webinarName));
    }

    private Optional<Webinar> findWebinarWithName(String webinarName) {
        return registeredWebinars.stream()
                .filter(webinar -> webinar.getName().equals(webinarName))
                .findFirst();
    }
}
