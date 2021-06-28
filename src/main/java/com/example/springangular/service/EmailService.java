package com.example.springangular.service;

import org.springframework.stereotype.Service;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Date;
import java.util.Properties;

import static com.example.springangular.constants.EmailConstant.*;

@Service
public class EmailService {

    private Message createEmail(String firstName, String password, String email) throws MessagingException {
        Message message = new MimeMessage(getEmailSession());

        message.setFrom(new InternetAddress(FROM_EMAIL));
        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(email, false));
        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(CC_EMAIL, false));
        message.setSubject(EMAIL_SUBJECT);
        message.setText("Hello " + firstName + ",\n\nYour new account password is: " + password +
                "\n\n The support team");
        message.setSentDate(new Date());
        message.saveChanges();
        return message;
    }

    private Session getEmailSession(){
        Properties properties = System.getProperties();

        properties.put(SMTP_HOST, GMAIL_SMTP_SERVER);
        properties.put(SMPT_AUTH, true);
        properties.put(SMTP_PORT, DEFAULT_PORT);
        properties.put(SMTP_STARTTLS_ENABLE, true);
        properties.put(SMTP_STARTTLS_REQURED, true);

        return Session.getInstance(properties, null);
    }
}
