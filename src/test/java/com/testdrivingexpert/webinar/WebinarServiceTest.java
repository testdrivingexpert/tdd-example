package com.testdrivingexpert.webinar;

import org.junit.Before;
import org.junit.Test;

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
}
