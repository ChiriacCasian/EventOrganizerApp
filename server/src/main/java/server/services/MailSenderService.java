package server.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.stereotype.Service;

@Service
public class MailSenderService {

    @Autowired
    private JavaMailSender mailSender;

    /**
     * Sets the mail sender
     *
     * @param mailSender mail sender
     */
    public void setMailSender(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    /**
     * Sends new mail
     *
     * @param to  email to be sent to
     * @param sub inviteCode of the message
     * @param bod body
     * @return returns the inviteCode of the Event
     */
    public String sendNewMail(String to, String sub, String bod) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(sub);
        message.setCc(System.getProperty("from.email"));
        message.setText(bod);
        ((JavaMailSenderImpl) mailSender).setUsername(System.getProperty("from.email"));
        ((JavaMailSenderImpl) mailSender).setPassword(System.getProperty("from.password"));
        mailSender.send(message);

        return sub;
    }
}
