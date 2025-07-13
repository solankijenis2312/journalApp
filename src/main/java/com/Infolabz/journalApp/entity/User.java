package com.Infolabz.journalApp.entity;
import lombok.Builder;
import lombok.Data;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;

@Document(collection = "users")
@Data
@Builder
public class User {

    @Id
    private ObjectId id;

    @Indexed(unique = true)
    private String userName;

    private String password;

    // roles needed for Spring Security
    private List<String> roles = new ArrayList<>();

    @DBRef
    private List<JournalEntry> journalEntries = new ArrayList<>();


    // No-args constructor (required by Spring Data)
    public User() {
        this.journalEntries = new ArrayList<>(); //It initializes journalEntries and roles to empty ArrayLists.
        this.roles = new ArrayList<>();
    }

    // Parameterized constructor
    public User(String userName, String password) {
        this.userName = userName;
        this.password = password;
        this.journalEntries = new ArrayList<>();
        this.roles = new ArrayList<>(roles);
    }

    // Optional: Constructor with roles
    public User(String userName, String password, List<String> roles) {
        this.userName = userName;
        this.password = password;
        this.roles = (roles!= null)? new ArrayList<>(roles) : new ArrayList<>();
        this.journalEntries = new ArrayList<>();
    }

    public User(ObjectId id, String userName, String password, List<String> roles, List<JournalEntry> journalEntries) {
        this.id = id;
        this.userName = userName;
        this.password = password;
        this.roles = (roles!= null)? new ArrayList<>(roles) : new ArrayList<>();
        this.journalEntries = (journalEntries!= null)? new ArrayList<>(journalEntries) : new ArrayList<>();
    }


    public void setRoles(List<String> user) {
        this.roles = new ArrayList<>(roles); // Assign the provided list of roles
    }
}
