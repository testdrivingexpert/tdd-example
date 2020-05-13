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
        Webinar webinar = new Webinar("tdd");
        tested.registerWebinar(webinar);

        Participant participant = new Participant("my@email.com");
        tested.registerParticipant(participant, "tdd");

        List<Participant> registered = tested.getRegisteredParticipants();
        assertThat(registered)
                .isNotEmpty()
                .extracting("email")
                .containsExactly("my@email.com");
    }
}
