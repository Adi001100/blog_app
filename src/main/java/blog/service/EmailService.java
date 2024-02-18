package blog.service;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {
    private final Log log = LogFactory.getLog(this.getClass());
    private final JavaMailSender javaMailSender;

    public static final String NEW_USER_ACCOUNT_VERIFICATION = "NEW USER ACCOUNT VERIFICATION";
    public static final String DELETED_REGISTRATION = "DELETED YOUR REGISTRATION";

    public static final String DEAR = "Dear ";

    @Value("${spring.mail.username}")
    private String emailFrom;


    @Autowired
    public EmailService(JavaMailSender javaMailSender) {
        this.javaMailSender = javaMailSender;
    }

    public void sendEmailAboutComment(String email, String username) {

        try {
            SimpleMailMessage mailMessage = new SimpleMailMessage();
            mailMessage.setFrom(emailFrom);
            mailMessage.setTo(email);
            mailMessage.setSubject("New comment to post");
            mailMessage.setText(DEAR + username + "! There is a new comment to your post");
            javaMailSender.send(mailMessage);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            log.error(e);
            log.error("Error when sending email to the address below: " + email);
            throw new RuntimeException(e.getMessage());
        }
    }

    public void sendEmailAboutLike(String email, String username) {

        try {
            SimpleMailMessage mailMessage = new SimpleMailMessage();
            mailMessage.setFrom(emailFrom);
            mailMessage.setTo(email);
            mailMessage.setSubject("New like to post");
            mailMessage.setText(DEAR + username + "! There is a new like to your post");
            javaMailSender.send(mailMessage);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            log.error(e);
            log.error("Error when sending email to the address below: " + email);
            throw new RuntimeException(e.getMessage());
        }
    }

    public void sendValidationEmail(String name, String to, String token) {
        try {
            SimpleMailMessage mailMessage = new SimpleMailMessage();
            mailMessage.setSubject(NEW_USER_ACCOUNT_VERIFICATION);
            mailMessage.setFrom(emailFrom);
            mailMessage.setTo(to);
            mailMessage.setText(DEAR + name + " please verify your email address using the link below \n http://localhost:8080/api/users/" + token);
            javaMailSender.send(mailMessage);
        } catch (Exception exception) {
            throw new RuntimeException(exception.getMessage());
        }

    }

    public void sendEmailWrongValidation(String name, String to) {
        try {
            SimpleMailMessage mailMessage = new SimpleMailMessage();
            mailMessage.setSubject(DELETED_REGISTRATION);
            mailMessage.setFrom(emailFrom);
            mailMessage.setTo(to);
            mailMessage.setText("Dear " + name + " Your registration has been deleted, because you didn't verified your registration");
            javaMailSender.send(mailMessage);
        } catch (Exception exception) {
            throw new RuntimeException(exception.getMessage());
        }
    }
}
