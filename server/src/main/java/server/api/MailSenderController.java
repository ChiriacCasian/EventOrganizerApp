package server.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import server.services.MailSenderService;

@RestController
@RequestMapping("/api")
public class MailSenderController {
    private MailSenderService senderService;


    /**
     * Constructs the sender service
     *
     * @param senderService sender service
     */
    @Autowired
    public MailSenderController(MailSenderService senderService) {
        this.senderService = senderService;
    }

    /**
     * Sends a mail
     *
     * @param mail mail to send to
     * @param sub inviteCode to include in mail
     * @param bod body of the mail
     * @param from from this mail
     * @param password password of the mail app
     * @return returns invite Code
     */
    @PostMapping("/mail")
    public String sendMail(@RequestParam("mail") String mail,
                           @RequestParam("sub") String sub,
                           @RequestParam("bod") String bod,
                           @RequestParam("from") String from,
                           @RequestParam("password") String password){
        System.setProperty("from.email", from);
        System.setProperty("from.password", password);
        return senderService.sendNewMail(mail, sub, bod);
    }
}
