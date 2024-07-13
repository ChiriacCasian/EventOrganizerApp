package server.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.Properties;

@Configuration
public class MailConfiguration {
    /**
     * @return returns the configured JavaMailSender
     */
    @Bean
    public JavaMailSender getJavaMailSender() {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost("smtp.gmail.com");
        mailSender.setPort(587);


        Properties props = mailSender.getJavaMailProperties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.debug", "true");

        return mailSender;
    }

    private static String email;

    private static String password;

    /**
     * sets the email of mail config
     * @param email email of the config
     */
    public static void setEmail(String email) {
        MailConfiguration.email = email;
    }

    /**
     * sets the password of mail config
     * @param password password of the config
     */
    public static void setPassword(String password) {
        MailConfiguration.password = password;
    }
}
