package com.testdrivingexpert.webinar;

import org.junit.Before;
import org.junit.Test;
import org.mockito.*;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

public class WebinarServiceTest {
    @InjectMocks
    private WebinarService tested;

    @Mock
    private EmailSender emailSenderMock;

    @Captor
    private ArgumentCaptor<Map<String, String>> emailParametersCaptor;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
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

    @Test
    public void shouldSendConfirmationEmailToRegisteredParticipant() {
        givenRegisteredWebinar("oop");
        whenRegisteringParticipant("palo@here.com", "oop");

        verify(emailSenderMock).sendEmail(eq("palo@here.com"), eq("verify-email-oop"), any());
    }

    @Test
    public void shouldSendEmailTwiceWhenUserRegisters2ndTimeWithDifferentToken() {
        givenRegisteredWebinar("oop");

        whenRegisteringParticipant("palo@here.com", "oop");
        String token1 = assertTokenWasSentToParticipant("palo@here.com", "oop");

        reset(emailSenderMock);

        whenRegisteringParticipant("palo@here.com", "oop");
        String token2 = assertTokenWasSentToParticipant("palo@here.com", "oop");

        assertNotEquals("Token should be different", token1, token2);
    }

    @Test
    public void shouldNotRememberParticipantTwiceWhenResendingEmail() {
        givenRegisteredWebinar("java");
        givenRegisteredParticipant("suzy@mail.com", "java");

        whenRegisteringParticipant("suzy@mail.com", "java");

        List<Participant> registered = tested.getRegisteredParticipants("java");
        assertThat(registered).hasSize(1);
    }

    @Test
    public void shouldSendThankYouEmailAfterConfirmingEmailAddress() {
        givenRegisteredWebinar("tdd");
        givenRegisteredParticipant("peter@yahoo.com", "tdd");
        String token = assertTokenWasSentToParticipant("peter@yahoo.com", "tdd");

        tested.confirmEmail("peter@yahoo.com", token, "tdd");

        verify(emailSenderMock).sendEmail(eq("peter@yahoo.com"), eq("thank-you-tdd"), emailParametersCaptor.capture());
        assertEmailParameterValue("webinarName", "tdd");
    }

    @Test
    public void shouldRefuseUnknownWebinarWhenConfirming() {
        givenRegisteredWebinar("tdd");
        givenRegisteredParticipant("peter@yahoo.com", "tdd");
        String token = assertTokenWasSentToParticipant("peter@yahoo.com", "tdd");

        assertThatThrownBy(() -> tested.confirmEmail("peter@yahoo.com", token, "BAD_WEBINAR"))
                .isInstanceOf(InvalidTokenException.class);

        verify(emailSenderMock, never()).sendEmail(any(), startsWith("thank-you-"), any());
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

    private String assertTokenWasSentToParticipant(String email, String webinarName) {
        verify(emailSenderMock).sendEmail(eq(email), eq("verify-email-" + webinarName), emailParametersCaptor.capture());
        String token = emailParametersCaptor.getValue().get("token");
        assertNotNull("Token was not sent by email", token);
        return token;
    }

    private void assertMatchingEmailAddresses(List<Participant> registered, String... emails) {
        assertThat(registered)
                .isNotEmpty()
                .extracting("email")
                .containsExactly(emails);
    }

    private void assertEmailParameterValue(String name, String expectedValue) {
        String value = emailParametersCaptor.getValue().get(name);
        assertNotNull("Parameter " + name + " not found in email parameters", value);
        assertEquals("Invalid value of parameter " + name, expectedValue, value);
    }
}
