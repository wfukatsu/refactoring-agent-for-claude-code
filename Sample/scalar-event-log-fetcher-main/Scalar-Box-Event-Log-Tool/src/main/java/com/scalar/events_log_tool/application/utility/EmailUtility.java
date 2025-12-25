package com.scalar.events_log_tool.application.utility;


import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeBodyPart;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.internet.MimeMultipart;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Properties;

@Component
@Slf4j
public class EmailUtility {

    public final String mailSmtpSslTrust;
    public final String mailSmtpSslProtocols;
    private final String login;
    private final String password;
    private final Boolean mailSmtpAuth;
    private final Boolean mailSmtpStarttlsEnable;
    private final String mailSmtpHost;
    private final Integer mailSmtpPort;

    @Autowired
    public EmailUtility(@Value("${mail.smtp.ssl.trust}") String mailSmtpSslTrust,
                        @Value("${mail.smtp.ssl.protocols}") String mailSmtpSslProtocols,
                        @Value("${mail.sender.login}") String login,
                        @Value("${mail.sender.password}") String password,
                        @Value("${mail.smtp.auth}") Boolean mailSmtpAuth,
                        @Value("${mail.smtp.starttls.enable}") Boolean mailSmtpStarttlsEnable,
                        @Value("${mail.smtp.host}") String mailSmtpHost,
                        @Value("${mail.smtp.port}") Integer mailSmtpPort) {
        this.mailSmtpSslTrust = mailSmtpSslTrust;
        this.mailSmtpSslProtocols = mailSmtpSslProtocols;
        this.login = login;
        this.password = password;
        this.mailSmtpAuth = mailSmtpAuth;
        this.mailSmtpStarttlsEnable = mailSmtpStarttlsEnable;
        this.mailSmtpHost = mailSmtpHost;
        this.mailSmtpPort = mailSmtpPort;
    }

    public void sendEmail(String destinationEmail,
                          String emailSubject, String emailBody) throws MessagingException {

        log.info("sending mail");
        Properties props = new Properties();
        props.put("mail.smtp.auth", mailSmtpAuth);
        props.put("mail.smtp.starttls.enable", mailSmtpStarttlsEnable);
        props.put("mail.smtp.host", mailSmtpHost);
        props.put("mail.smtp.port", mailSmtpPort);
        props.put("mail.smtp.ssl.trust", mailSmtpSslTrust);
        props.setProperty("mail.smtp.ssl.protocols", mailSmtpSslProtocols);

        final Authenticator auth = new SMTPAuthenticator(login, password);
        // Create a Session object to represent a mail session with the specified
        // properties.
        Session session = Session.getDefaultInstance(props, auth);
        log.info("session..");
        // Create a message with the specified information.
        MimeMessage msg = new MimeMessage(session);
        msg.setFrom(new InternetAddress(login));
        msg.setRecipient(Message.RecipientType.TO, new InternetAddress(destinationEmail));
        msg.setSubject(emailSubject, "UTF-8");

        MimeBodyPart messageBodyPart = new MimeBodyPart();

        messageBodyPart.setContent(emailBody, "text/html; charset=UTF-8");

        // creates multi-part
        Multipart multipart1 = new MimeMultipart();
        multipart1.addBodyPart(messageBodyPart);
        msg.setContent(multipart1, "text/html; charset=UTF-8");
        msg.saveChanges();
        Transport.send(msg);
        log.info("mail sent");

    }

    private class SMTPAuthenticator extends Authenticator {
        private final PasswordAuthentication authentication;

        /**
         * This method checks the login credentials of sender.
         */
        public SMTPAuthenticator(final String login, final String password) {
            authentication = new PasswordAuthentication(login, password);
        }

        @Override
        protected PasswordAuthentication getPasswordAuthentication() {
            return authentication;
        }
    }



}