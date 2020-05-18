package com.testdrivingexpert.webinar;

import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class WebinarServiceTest {
    private WebinarService tested;

    @Before
    public void setUp() {
        tested = new WebinarService();
    }

    @Test
    public void shouldRefuseToRegisterParticipantForWrongWebinar() {
        Participant participant = new Participant("my@email.com");

        assertThatThrownBy(() -> tested.registerParticipant(participant, "non-existent-webinar"))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Webinar with name 'non-existent-webinar' does not exist");
    }

    @Test
    public void shouldRefuseToRegisterParticipantForWrongWebinar_case2() {
        Participant participant = new Participant("my@email.com");

        assertThatThrownBy(() -> tested.registerParticipant(participant, "maybe-this-one"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Webinar with name 'maybe-this-one' does not exist");
    }
    
    @Test
    public void shouldAllowToRegisterWebinarAndRegisterParticipant() {
        givenRegisteredWebinar("tdd");

        Participant participant = new Participant("my@email.com");
        tested.registerParticipant(participant, "tdd");

        List<Participant> registered = tested.getRegisteredParticipants("tdd");
        assertMatchingEmailAddresses(registered, "my@email.com");
    }

    @Test
    public void shouldAllowRegisteringMultipleParticipants() {
        String webinarName = "tdd";
        givenRegisteredWebinar(webinarName);

        whenRegisteringParticipant("my@email.com", webinarName);
        whenRegisteringParticipant("john@yahoo.com", webinarName);

        List<Participant> registered = tested.getRegisteredParticipants(webinarName);
        assertMatchingEmailAddresses(registered, "my@email.com", "john@yahoo.com");
    }

    @Test
    public void shouldAllowRegisteringMultipleWebinarsWithUniqueNames() {
        givenRegisteredWebinar("tdd");
        givenRegisteredWebinar("oop");

        whenRegisteringParticipant("my@email.com", "tdd");
        whenRegisteringParticipant("john@yahoo.com", "oop");

        List<Participant> registeredTdd = tested.getRegisteredParticipants("tdd");
        assertMatchingEmailAddresses(registeredTdd, "my@email.com");

        List<Participant> registeredOop = tested.getRegisteredParticipants("oop");
        assertMatchingEmailAddresses(registeredOop, "john@yahoo.com");
    }

    @Test
    public void shouldRefuseRegisteringTwoWebinarsWithTheSameName() {
        givenRegisteredWebinar("tdd");

        assertThatThrownBy(() -> whenRegisteringWebinar("tdd"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Duplicate webinar with name 'tdd'");
    }

    @Test
    public void shouldNotDeleteExistingParticipantsAfterDuplicateRegisteringOfSameWebinar() {
        givenRegisteredWebinar("oop");
        givenRegisteredParticipant("palo@here.com", "oop");

        assertThatThrownBy(() -> whenRegisteringWebinar("oop"));

        List<Participant> registeredOop = tested.getRegisteredParticipants("oop");
        assertMatchingEmailAddresses(registeredOop, "palo@here.com");
    }

    //////////////////////////////////////////////////////
    private void givenRegisteredWebinar(String webinarName) {
        Webinar webinar = new Webinar(webinarName);
        tested.registerWebinar(webinar);
    }

    private void givenRegisteredParticipant(String email, String webinarName) {
        whenRegisteringParticipant(email, webinarName);
    }

    private void whenRegisteringWebinar(String webinarName) {
        givenRegisteredWebinar(webinarName);
    }
    
    private void whenRegisteringParticipant(String email, String webinarName) {
        Participant participant = new Participant(email);
        tested.registerParticipant(participant, webinarName);
    }

    private void assertMatchingEmailAddresses(List<Participant> registered, String... emails) {
        assertThat(registered)
                .isNotEmpty()
                .extracting("email")
                .containsExactly(emails);
    }
}
