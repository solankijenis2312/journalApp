//This is a service layer class that handles business logic and interacts with the repository
package com.Infolabz.journalApp.service;
import com.Infolabz.journalApp.entity.JournalEntry;
import com.Infolabz.journalApp.entity.User;
import com.Infolabz.journalApp.respository.JournalEntryRepository;
import com.Infolabz.journalApp.respository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



@Component
@Slf4j
public class JournalEntryService {

    @Autowired
    private JournalEntryRepository journalEntryRepository;

    @Autowired
    private UserService userService;

    @Autowired  // Added this annotation
    private UserRepository userRepository;  // Now properly injected


    @Transactional
    public JournalEntry saveEntry(JournalEntry journalEntry, String userName) {
        try {
            User user = userRepository.findByuserName(userName);
            if (user == null) {
                throw new RuntimeException("User '" + userName + "' not found.");
            }
            journalEntry.setUser(user);

            JournalEntry saved = journalEntryRepository.save(journalEntry);

            if(user.getJournalEntries() == null) {
                user.setJournalEntries(new ArrayList<>());
            }

            user.getJournalEntries().add(saved);
            userRepository.save(user);

            return saved;
        }
        catch (Exception e) {

            throw new RuntimeException("Failed to save journal entry for user " + userName, e);
        }
    }

    // Added new method to find entry by ID and user
    public Optional<JournalEntry> findByIdAndUser(ObjectId id, String userName) {
        User user = userRepository.findByuserName(userName);
        if (user == null || user.getJournalEntries() == null) {
            return Optional.empty();
        }

        // Check if user has this entry
        boolean userHasEntry = user.getJournalEntries().stream()
                .anyMatch(entry -> entry.getId().equals(id));

        if (userHasEntry) {
            return journalEntryRepository.findById(id);
        }
        return Optional.empty();
    }

    //
    public void saveEntry(JournalEntry journalEntry){
        journalEntryRepository.save(journalEntry); //Simply saves the JournalEntry to the database.
    }

    public List<JournalEntry> getAll(){ //Retrieves all JournalEntry documents from the journal_entries collection
        return journalEntryRepository.findAll();
    }

    public Optional<JournalEntry> findById(ObjectId id){
        return journalEntryRepository.findById(id);
    }

    @Transactional
    public boolean deleteById(ObjectId id, String userName)
    {
        boolean removed= false;
        try
        {
            User user = userService.findByuserName(userName);
            removed = user.getJournalEntries().removeIf(x -> x.getId().equals(id));
            if (removed) {
                userService.saveUser(user);
                journalEntryRepository.deleteById(id);
            }
        }
        catch (Exception e)
        {
            log.error("Error",e);
            throw new RuntimeException("An error ocuured when deleting the entry",e);
        }
        return removed;
    }
}