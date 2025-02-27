package com.bracale;

import io.quarkus.runtime.StartupEvent;
import org.eclipse.microprofile.reactive.messaging.*;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.io.IOException;
import java.nio.file.NoSuchFileException;

@ApplicationScoped
public class KafkaProducer {

    @Inject
    @Channel("topic-in")
    Emitter<String> jsonEmitter;

    void onStart(@Observes StartupEvent ev) {
        sendJsonMessage();
    }

    private void sendJsonMessage() {
        ObjectMapper objectMapper = new ObjectMapper();

        String filePath = "json/test.json";
        String filePath2 = "json/test-only-to.json";
        String filePath3 = "json/test-empty-all.json";

        try {
            if (!Files.exists(Paths.get(filePath)) || !Files.exists(Paths.get(filePath2)) || !Files.exists(Paths.get(filePath3))) {
                throw new NoSuchFileException(filePath);
            }
            byte[] jsonData = Files.readAllBytes(Paths.get(filePath));
            JsonNode rootNode = objectMapper.readTree(jsonData);
            jsonEmitter.send(rootNode.toString());

            byte[] jsonData2 = Files.readAllBytes(Paths.get(filePath2));
            JsonNode rootNode2 = objectMapper.readTree(jsonData2);
            jsonEmitter.send(rootNode2.toString());
            byte[] jsonData3 = Files.readAllBytes(Paths.get(filePath3));
            JsonNode rootNode3 = objectMapper.readTree(jsonData3);
            jsonEmitter.send(rootNode3.toString());

        } catch (NoSuchFileException e) {
            System.err.println("Arquivo JSON não encontrado: " + e.getMessage());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
