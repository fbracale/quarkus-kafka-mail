package com.bracale;

import java.util.List;

public class EmailStructure {

    private String from;
    private String to;
    private String subject;
    private String body;
    private String cc;
    private List<String> appixNames;
    private List<String> appixBodies;

    //getters and setters
    public String getFrom() {
        return from;
    }
    
    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getCc() {
        return cc;
    }

    public void setCc(String cc) {
        this.cc = cc;
    }

    public List<String> getAppixNames() {
        return appixNames;
    }

    public void setAppixNames(List<String> appixNames) {
        this.appixNames = appixNames;
    }

    public List<String> getAppixBodies() {
        return appixBodies;
    }

    public void setAppixBodies(List<String> appixBodies) {
        this.appixBodies = appixBodies;
    }


    public EmailStructure() {
    }

    @Override
    public String toString() {
        return "EmailStructure{" +
                ", from='" + from + '\'' +
                ", to='" + to + '\'' +
                ", subject='" + subject + '\'' +
                ", body='" + body + '\'' +
                ", cc='" + cc + '\'' +
                ", appixNames=" + appixNames +
                ", appixBodies=" + appixBodies +
                '}';
    }
}
