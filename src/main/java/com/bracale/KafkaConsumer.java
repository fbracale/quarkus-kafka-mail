package com.bracale;

import org.eclipse.microprofile.reactive.messaging.*;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException;

import java.io.IOException;

@ApplicationScoped
public class KafkaConsumer {

    @Inject
    EmailServiceQuarkus emailService;

    @Incoming("topic-out")
    public void consumeJsonMessage(String jsonMessage) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            JsonNode rootNode = objectMapper.readTree(jsonMessage);
            String classeNode = rootNode.get("classe").asText();
            if (!"email".equals(classeNode)) {
                throw new IOException("Campo 'classe' é diferente de 'email'");
            }
            JsonNode objetoNode = rootNode.path("objeto");
            if (objetoNode.isMissingNode()) {
                throw new IOException("Campo 'objeto' não encontrado no JSON");
            }
            EmailStructure message = objectMapper.treeToValue(objetoNode, EmailStructure.class);

            System.out.println("...........");
            System.out.println("Mensagem Recebida do Kafka:\n" + message);
            System.out.println("...........");

            emailService.enviarEmailReactive(message).subscribe().with(
                (Void) -> System.out.println("Email enviado com sucesso!"),
                failure -> System.err.println("Falha ao enviar email: " + failure.getMessage())
            );
            
            //System.out.println("Passou pelo EmailService");

        } catch (UnrecognizedPropertyException e) {
            System.err.println("Json mal formatado: " + e.getMessage());
        } catch (IOException e) {
            e.printStackTrace();

        }
    }
}