package com.testdrivingexpert.webinar;

import java.security.SecureRandom;
import java.util.*;
import java.util.stream.Collectors;

public class WebinarService {
    private final List<Webinar> registeredWebinars = new ArrayList<>();
    private final Map<String, List<RegisteredParticipant>> registeredParticipants = new HashMap<>();

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
        String token = String.valueOf(secureRandom.nextLong());

        List<RegisteredParticipant> participants = registeredParticipants.computeIfAbsent(webinarName, s -> new ArrayList<>());
        if (!isRegisteredParticipant(email, participants)) {
            participants.add(new RegisteredParticipant(toRegister, token));
        }

        Map<String, String> parameters = Collections.singletonMap("token", token);
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
        return registeredParticipants.get(webinarName).stream()
                .map(RegisteredParticipant::getParticipant).collect(Collectors.toList());
    }

    private Optional<Webinar> findWebinarWithName(String webinarName) {
        return registeredWebinars.stream()
                .filter(webinar -> webinar.getName().equals(webinarName))
                .findFirst();
    }

    private boolean isRegisteredParticipant(String email, List<RegisteredParticipant> participants) {
        return participants.stream().anyMatch(participant -> participant.getEmail().equals(email));
    }

    public void confirmEmail(String email, String token, String webinarName) {
        RegisteredParticipant found = findParticipantByWebinarAndEmail(webinarName, email)
                .orElseThrow(InvalidTokenException::new);

        if (!found.getToken().equals(token)) {
            throw new InvalidTokenException();
        }

        found.setEmailConfirmed(true);
        Map<String, String> parameters = Collections.singletonMap("webinarName", webinarName);
        emailSender.sendEmail(email, "thank-you-" + webinarName, parameters);
    }

    private Optional<RegisteredParticipant> findParticipantByWebinarAndEmail(String webinarName, String email) {
        List<RegisteredParticipant> participants = registeredParticipants.get(webinarName);
        if (participants == null) {
            return Optional.empty();
        }
        return participants.stream()
                .filter(participant -> participant.getEmail().equals(email))
                .findFirst();
    }

    public void sendEmailAboutWebinarStarting(String webinarName) {
        Map<String, String> parameters = Collections.singletonMap("webinarName", webinarName);
        registeredParticipants.get(webinarName).stream()
                .filter(RegisteredParticipant::isEmailConfirmed)
                .forEach(p -> emailSender.sendEmail(p.getEmail(), "webinar-starts", parameters));
    }
}
