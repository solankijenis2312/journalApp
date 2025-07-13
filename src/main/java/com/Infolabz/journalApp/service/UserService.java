//This is a service layer class that handles business logic and interacts with the repository
package com.Infolabz.journalApp.service;
import com.Infolabz.journalApp.entity.User;
import com.Infolabz.journalApp.respository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service //Marks this class as a Spring service component. This allows Spring to detect and manage it as a bean.
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    //private static final Logger logger= LoggerFactory.getLogger(UserService.class); //means: create a logger object for the JournalEntryService class using SLF4J.This logger can be used to print log messages like logger.info(), logger.error(), etc., which helps track the program's flow or errors.It identifies where the log came from by tagging it with the class name.


    public List<User> getAll() //Retrieves all users from database
    {
      return userRepository.findAll(); //return  List of User entities
    }

    @Transactional
    public boolean saveNewUser(User user) {
        try {
            User existingUser = userRepository.findByuserName(user.getUserName());
            if (existingUser != null) {
                log.warn("Attempt to create duplicate username: " + user.getUserName());
                return false; // Indicating failure due to duplicate
            }

            if (!user.getPassword().startsWith("$2a$")) {
                user.setPassword(passwordEncoder.encode(user.getPassword()));
            }
            user.setRoles(Arrays.asList("USER"));
            userRepository.save(user); // If this throws an exception, it will be caught below
            return true; // Successfully saved
        } catch (Exception e) {
            // Log the full exception details. This is crucial for debugging!
            log.error("Error occurred for {} :",user.getUserName(),e);
            log.warn("balablablaa");
            log.info("balablablaa");
            log.debug("balablablaa");
            log.trace("balablablaa");

            return false; // Indicating general failure
        }
    }

    public void saveAdmin(User user)
    {
        // Always set roles and then save
        if (!user.getPassword().startsWith("$2a$")) {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        }
        user.setRoles(Arrays.asList("USER","ADMIN")); // Set roles regardless of password encoding status
        userRepository.save(user); // Always save the user to persist changes
    }

    public void saveUser(User user)
    {
        userRepository.save(user);
    }



    public Optional<User> findById(ObjectId id){ //Optional<User> to handle null cases gracefully

        return userRepository.findById(String.valueOf(id)); //Uses repository's findById() to find a specific entry
    }

    public void deleteById(ObjectId id){ //Permanently deletes user from database
               userRepository.deleteById(String.valueOf(id));

    }

    public User findByuserName(String userName) {
        return userRepository.findByuserName(userName);
    }

    public void deleteUserByUserName(String userName) {
        User userToDelete = userRepository.findByuserName(userName);
        if (userToDelete != null) {
            userRepository.delete(userToDelete);
        }
    }
}
