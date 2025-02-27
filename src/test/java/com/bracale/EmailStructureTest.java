package com.bracale;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;

public class EmailStructureTest {

    @Test
    public void testGettersAndSetters() {
        EmailStructure email = new EmailStructure();
        email.setFrom("from@example.com");
        email.setTo("to@example.com");
        email.setSubject("Test Subject");
        email.setBody("Test Body");
        email.setCc("cc@example.com");
        email.setAppixNames(Arrays.asList("Appix1", "Appix2"));
        email.setAppixBodies(Arrays.asList("Body1", "Body2"));

        assertEquals("from@example.com", email.getFrom());
        assertEquals("to@example.com", email.getTo());
        assertEquals("Test Subject", email.getSubject());
        assertEquals("Test Body", email.getBody());
        assertEquals("cc@example.com", email.getCc());
        assertEquals(Arrays.asList("Appix1", "Appix2"), email.getAppixNames());
        assertEquals(Arrays.asList("Body1", "Body2"), email.getAppixBodies());
    }
}