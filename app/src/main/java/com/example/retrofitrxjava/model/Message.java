package com.example.retrofitrxjava.model;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.HashMap;
import java.util.Map;

@IgnoreExtraProperties
public class Message {
    public String author;
    public String body;
    public String documentId;
    public String avatar;

    public Message(String author, String body, String avatar, String documentId) {
        this.author = author;
        this.body = body;
        this.documentId = documentId;
        this.avatar = avatar;
    }

    public Message() {
    }

    @Exclude
    public Map toMap(){
        HashMap result = new HashMap();
        result.put("documentId",documentId);
        result.put("author", author);
        result.put("body", body);
        result.put("avatar", avatar);
        return result;
    }

    @Override
    public String toString() {
        return "Message{" +
                "author='" + author + '\'' +
                ", body='" + body + '\'' +
                ", documentId='" + documentId + '\'' +
                '}';
    }

    public String getDocumentId() {
        return documentId;
    }

    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

}
