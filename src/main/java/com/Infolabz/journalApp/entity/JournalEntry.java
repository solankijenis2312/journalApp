package com.Infolabz.journalApp.entity;

import lombok.*;
import org.bson.types.ObjectId; //MongoDB's unique identifier type
import org.springframework.data.annotation.Id; //Spring Data annotation for primary key
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document; //Annotation to map this class to a MongoDB collection

import java.time.LocalDateTime;
import java.util.Date;

@Document(collection = "journal_entries") //tells Spring Data MongoDB that this class represents a document in the "journal_entries" collection
@Data
public class JournalEntry {


    @Id //-> you map as primary key then write @Id
    private ObjectId id;

    @NonNull
    private String title;
    private String content;
    private LocalDateTime date;

    @DBRef  // Creates a reference to the User document
    private User user;


    public JournalEntry() {
        this.date = LocalDateTime.now(); //This line ensures that when a new JournalEntry object is created without specifying a date, it automatically gets initialized with the current date and time.
    }

    public JournalEntry(String title, String content) {
        this.title = title;
        this.content = content;
        this.date = LocalDateTime.now();
    }


    public void setUser(User user) {
        this.user = user;
    }

}
