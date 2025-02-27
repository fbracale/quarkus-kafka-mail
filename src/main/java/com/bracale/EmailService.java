package com.bracale;

import jakarta.enterprise.context.ApplicationScoped;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.PasswordAuthentication;
import javax.mail.SendFailedException;

//Classic JavaMail API

@ApplicationScoped
public class EmailService {

    public void enviarEmail(EmailStructure emailStructure) {
        // MailHog ServerConfig
        Properties props = new Properties();
        props.put("mail.smtp.host", "localhost");
        props.put("mail.smtp.port", "1025");
        props.put("mail.smtp.auth", "false");
        props.put("mail.smtp.starttls.enable", "false");

        // Auth
        Session session = Session.getInstance(props, new javax.mail.Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication("", "");
            }
        });

        try {
            if (emailStructure.getTo() == null || emailStructure.getTo().isEmpty()) {
                throw new IllegalArgumentException("Campo obrigatório 'destinatário': nulo ou vazio!");
            }
            Message message = new MimeMessage(session);
            message.addFrom(InternetAddress.parse(emailStructure.getFrom()));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(emailStructure.getTo()));
            message.setRecipients(Message.RecipientType.CC, InternetAddress.parse(emailStructure.getCc()));
            message.setSubject(emailStructure.getSubject());
            message.setText(emailStructure.getBody());
            //message.setContent(mail.getBody(),"text/html;charset=utf-8");

            Transport.send(message);
            System.out.println("E-mail enviado com sucesso!");

        } catch (SendFailedException e) {
            System.out.println("Falha ao enviar o e-mail: " + e.getMessage());
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
        }
    }
}
