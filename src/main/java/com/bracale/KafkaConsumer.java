package com.bracale;

import org.apache.kafka.common.header.internals.RecordHeaders;
import org.eclipse.microprofile.reactive.messaging.*;

import io.smallrye.reactive.messaging.kafka.Record;
import io.smallrye.reactive.messaging.kafka.api.OutgoingKafkaRecordMetadata;

import org.jboss.logging.Logger;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException;

import java.io.IOException;
import java.io.InvalidClassException;

@ApplicationScoped
public class KafkaConsumer {

    private final Logger logger = Logger.getLogger(KafkaConsumer.class.getName());

    @Inject
    EmailServiceQuarkus emailService;

    @Inject
    @Channel("dlq")
    Emitter<String> dlqEmitter;

    @Incoming("topic-in")
    public void kafkaTopicConsumer(Record<String, String> record) {
        
        logger.infof("Mensagem recebida do Kafka: %s - %s", record.key(), record.value());
        
        ObjectMapper objectMapper = new ObjectMapper();

        try {
            JsonNode rootNode = objectMapper.readTree(record.value());
            String classeNode = rootNode.get("classe").asText();
            if (!"email".equals(classeNode)) {
                throw new InvalidClassException("Campo 'classe' possui valor diferente de 'email' [key: " + record.key() + "]");
            }
            JsonNode objetoNode = rootNode.path("objeto");
            if (objetoNode.isMissingNode()) {
                throw new NoSuchFieldException("Campo 'objeto' não encontrado no JSON [key: " + record.key() + "]");
            }
            EmailStructure message = objectMapper.treeToValue(objetoNode, EmailStructure.class);

            emailService.enviarEmailReactive(message).subscribe().with(
                (Void) -> logger.info("Email enviado com sucesso!"),
                failure -> logger.errorf("Falha ao enviar email [key: %s]: %s", record.key(), failure.getMessage())
            );
            

        } catch (UnrecognizedPropertyException e) {
            String errorMessage = String.format("Campo não reconhecido no JSON [key: %s]: %s", record.key(), e.getMessage());
            logger.error(errorMessage);
            sendToDlq(record, errorMessage);
        } catch (NoSuchFieldException | IOException e) {
            logger.error(e.getMessage());
            sendToDlq(record, e.getMessage());
        } catch (IllegalArgumentException e) {
            String errorMessage = String.format("Argumento inválido [key: %s]: %s", record.key(), e.getMessage());
            logger.error(errorMessage);
            sendToDlq(record, errorMessage);
        }
    }

    private void sendToDlq(Record<String, String> record, String errorMessage) {
        OutgoingKafkaRecordMetadata<String> headers = OutgoingKafkaRecordMetadata.<String> builder()
                                                    .withKey(record.key())
                                                    .withHeaders(new RecordHeaders().add("dead-letter-reason", errorMessage.getBytes()))
                                                    .build();
        dlqEmitter.send(Message.of(record.value(), Metadata.of(headers)));
    }
}