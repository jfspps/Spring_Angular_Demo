package com.example.springangular.service;

import org.springframework.stereotype.Service;

import javax.mail.Session;
import java.util.Properties;

import static com.example.springangular.constants.EmailConstant.*;

@Service
public class EmailService {

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
