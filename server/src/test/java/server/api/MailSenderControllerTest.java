package server.api;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import server.services.MailSenderService;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class MailSenderControllerTest {
    MailSenderController controller;
    MailSenderService mailSenderService;
    JavaMailSenderImpl mailSender;

    @Test
    void mailSender() {
        mailSender = Mockito.mock(JavaMailSenderImpl.class);
        mailSenderService = new MailSenderService();
        mailSenderService.setMailSender(mailSender);
        controller = new MailSenderController(mailSenderService);
        assertEquals(controller.sendMail("test@test.com", "TEST", "TEST",
                        "TEST", "TEST"),
                "TEST");
    }
}
