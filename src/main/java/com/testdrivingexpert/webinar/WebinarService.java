package com.testdrivingexpert.webinar;

import java.security.SecureRandom;
import java.util.*;

public class WebinarService {
    private final List<Webinar> registeredWebinars = new ArrayList<>();
    private final Map<String, List<Participant>> registeredParticipants = new HashMap<>();

    private final EmailSender emailSender;

    private final SecureRandom secureRandom = new SecureRandom();

    public WebinarService(EmailSender emailSender) {
        this.emailSender = emailSender;
    }

    public void registerParticipant(Participant toRegister, String webinarName) {
        if (!findWebinarWithName(webinarName).isPresent()) {
            throw new IllegalArgumentException(String.format("Webinar with name '%s' does not exist", webinarName));
        }

        String email = toRegister.getEmail();

        List<Participant> participants = registeredParticipants.computeIfAbsent(webinarName, s -> new ArrayList<>());
        if (!isRegisteredParticipant(email, participants)) {
            participants.add(toRegister);
        }

        Map<String, String> parameters = Collections.singletonMap("token", String.valueOf(secureRandom.nextLong()));
        emailSender.sendEmail(email, "verify-email-" + webinarName, parameters);
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

    private boolean isRegisteredParticipant(String email, List<Participant> participants) {
        return participants.stream().anyMatch(participant -> participant.getEmail().equals(email));
    }

    public void confirmEmail(String email, String token, String webinarName) {
        List<Participant> participants = registeredParticipants.get(webinarName);
        if (participants == null) {
            throw new InvalidTokenException();
        }
        Map<String, String> parameters = Collections.singletonMap("webinarName", webinarName);
        emailSender.sendEmail(email, "thank-you-" + webinarName, parameters);
    }
}
