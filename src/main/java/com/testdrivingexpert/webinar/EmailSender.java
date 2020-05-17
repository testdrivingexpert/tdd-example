package com.testdrivingexpert.webinar;

import java.util.Map;

public interface EmailSender {
    void sendEmail(String email, String template, Map<String, String> parameters);
}
