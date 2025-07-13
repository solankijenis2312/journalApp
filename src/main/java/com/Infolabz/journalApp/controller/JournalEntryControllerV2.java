package com.Infolabz.journalApp.controller;

import com.Infolabz.journalApp.entity.JournalEntry;
import com.Infolabz.journalApp.entity.User;
import com.Infolabz.journalApp.service.JournalEntryService;
import com.Infolabz.journalApp.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@RestController //@RestController: Tells Spring, “this class handles HTTP requests and returns data (like JSON).”
@RequestMapping("/journal") //add mapping on entire class.
public class JournalEntryControllerV2 { //special type of component that handle http request.


    @Autowired
    private JournalEntryService journalEntryService; //inject the JournalEntryService instance into ->  JournalEntryControllerV2

    @Autowired
    private UserService userService;

    @GetMapping
    public ResponseEntity<?> getAllJournalEntriesOfUser() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String userName = authentication.getName();
            log.info("Attempting to get all journal entries for authenticated user: {}", userName);

            User user = userService.findByuserName(userName);
            log.info("DEBUG: User '{}' found. Raw journal entries list from user object: {}", userName, user.getJournalEntries());
            // *** CRITICAL NULL CHECK HERE ***
            if (user == null) {
                log.error("Authenticated user '{}' not found in database by UserService.findByuserName. This should not happen if authentication succeeded.", userName);
                return new ResponseEntity<>("User profile not found. Please ensure your account is correctly set up.", HttpStatus.UNAUTHORIZED);
            }

            List<JournalEntry> all = user.getJournalEntries();

            if (all != null && !all.isEmpty()) {
                log.info("Found {} journal entries for user {}", all.size(), userName);
                return new ResponseEntity<>(all, HttpStatus.OK);
            } else {
                log.info("No journal entries found for user {}", userName);
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            log.error("Error retrieving journal entries for user (current auth name: {}): {}",
                    SecurityContextHolder.getContext().getAuthentication().getName(), e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An unexpected error occurred while retrieving journal entries.");
        }
    }


    //@RequestBody -> Take the JSON sent in the request body and convert it into a Java object.” -> look like JournalEntry myEntry = new JournalEntry(2, "My Day", "It was great.");
    // JournalEntryControllerV2.java
    @PostMapping
    public ResponseEntity<?> createEntry(@RequestBody JournalEntry myEntry) { //Spring automatically deserializes the JSON request body into a JournalEntry Java object.
        try {
            // 1. Get authentication safely
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null || !authentication.isAuthenticated()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body("Authentication required");
            }

            // 2. Validate required fields
            if (myEntry.getTitle() == null || myEntry.getTitle().trim().isEmpty()) {
                return ResponseEntity.badRequest()
                        .body("Title is required");
            }

            // 3. Process the entry
            String userName = authentication.getName(); //Gets the authenticated username.
            JournalEntry savedEntry = journalEntryService.saveEntry(myEntry, userName);

            // 4. Return successful response
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(savedEntry);

        } catch (DataAccessException e) {
            log.error("Database error while creating entry for user: {}",
                    SecurityContextHolder.getContext().getAuthentication().getName(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Database error occurred");
        } catch (Exception e) {
            log.error("Unexpected error creating journal entry", e);
            return ResponseEntity.badRequest()
                    .body("Invalid request data");
        }
    }

    //Purpose: To retrieve a single journal entry by its ID, ensuring it belongs to the authenticated user.
    @GetMapping("/id/{id}")
    public ResponseEntity<JournalEntry> getJournalEntryById(@PathVariable String id) {
        try {
            ObjectId myid = new ObjectId(id);  // Convert string to ObjectId
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String userName = authentication.getName();
            User user = userService.findByuserName(userName);

            List<JournalEntry> collect = user.getJournalEntries().stream().filter(x -> x.getId().equals(myid)).collect(Collectors.toList());

            if(!collect.isEmpty()) {
                return new ResponseEntity<>(collect.get(0), HttpStatus.OK);
            }
            Optional<JournalEntry> journalEntry = journalEntryService.findById(myid);
            if(journalEntry.isPresent())
            {
                return new ResponseEntity<>(journalEntry.get(), HttpStatus.OK);
            }

            return new ResponseEntity<>(HttpStatus.NOT_FOUND);

        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    // To delete a journal entry by its ID, ensuring it belongs to the authenticated user.
    @DeleteMapping("/id/{id}")
    public ResponseEntity<?> deleteJournalEntryById(@PathVariable ObjectId id) { // <<-- Problematic parameter type
        try {
            ObjectId myId = id;

            String userName = SecurityContextHolder.getContext().getAuthentication().getName();
            boolean removed= journalEntryService.deleteById(myId, userName);
            if(removed)
            {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
            else
            {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }

        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("id/{id}")
    public ResponseEntity<?> updateJournalEntryById(@PathVariable String id, @RequestBody JournalEntry newEntry) {
        try {
            ObjectId myId = new ObjectId(id);
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String userName = authentication.getName();
            log.info("Attempting to update journal entry with ID {} for user {}.", id, userName);

            User user = userService.findByuserName(userName);

            if (user == null) {
                log.error("Authenticated user '{}' not found in database for update operation.", userName);
                return new ResponseEntity<>("User profile not found. Please ensure your account is correctly set up.", HttpStatus.UNAUTHORIZED);
            }
            log.info("User '{}' found. Checking for journal entry ownership.", userName);

            List<JournalEntry> collect = user.getJournalEntries().stream().filter(x -> x.getId().equals(myId)).collect(Collectors.toList());

            if (!collect.isEmpty()) {
                log.info("Journal entry with ID {} found in user {}'s journalEntries list. Proceeding to fetch from main collection.", id, userName);
                Optional<JournalEntry> journalEntry1 = journalEntryService.findById(myId);

                if (journalEntry1.isPresent()) {
                    log.info("Journal entry with ID {} found in main collection. Proceeding to update.", id);
                    JournalEntry old = journalEntry1.get();
                    old.setTitle(newEntry.getTitle() != null && !newEntry.getTitle().trim().isEmpty() ? newEntry.getTitle() : old.getTitle());
                    old.setContent(newEntry.getContent() != null && !newEntry.getContent().trim().isEmpty() ? newEntry.getContent() : old.getContent());
                    journalEntryService.saveEntry(old);
                    log.info("Journal entry with ID {} updated successfully.", id);
                    return new ResponseEntity<>(old, HttpStatus.OK);
                } else {
                    log.warn("Journal entry with ID {} NOT found in main 'journal_entries' collection, despite being in user's list. Data inconsistency?", id);
                }
            } else {
                log.warn("Journal entry with ID {} NOT found in user {}'s journalEntries list.", id, userName);
            }
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);

        } catch (IllegalArgumentException e) {
            log.error("Invalid ObjectId format provided for update: {}", id, e);
            return new ResponseEntity<>("Invalid Journal Entry ID format.", HttpStatus.BAD_REQUEST);
        } catch (Exception e) { // Add this catch-all for unexpected issues
            log.error("An unexpected error occurred during update for ID {}: {}", id, e.getMessage(), e);
            return new ResponseEntity<>("An unexpected server error occurred.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    }






