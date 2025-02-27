package com.bracale;

import java.util.List;

import io.quarkus.mailer.Mail;
import io.quarkus.mailer.reactive.ReactiveMailer;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

//Reactive Quarkus Mailer API

@ApplicationScoped
public class EmailServiceQuarkus {

    private String defaultFrom = "no-reply@reactive-mail.com";
    
    @Inject
    ReactiveMailer reactiveMailerailer;
    public Uni<Void> enviarEmailReactive(EmailStructure emailStructure) {
        
        if (emailStructure.getTo() == null || emailStructure.getTo().isEmpty()) {
            return Uni.createFrom().failure(new IllegalArgumentException("Campo obrigatório 'destinatário': nulo ou vazio!"));
        }

        Mail m = new Mail();
        m.setFrom((emailStructure.getFrom().isEmpty()? defaultFrom:emailStructure.getFrom()));
        m.setTo(List.of(emailStructure.getTo().split("\\s*,\\s*")));
        if (!emailStructure.getCc().isEmpty()) m.setCc(List.of(emailStructure.getCc().split("\\s*,\\s*")));
        m.setSubject(emailStructure.getSubject());
        m.setText(emailStructure.getBody());

        return reactiveMailerailer.send(m);
    }
}
