package com.Infolabz.journalApp.controller;

import com.Infolabz.journalApp.entity.JournalEntry;
import com.Infolabz.journalApp.entity.User;
import com.Infolabz.journalApp.respository.UserRepository;
import com.Infolabz.journalApp.service.JournalEntryService;
import com.Infolabz.journalApp.service.UserService;
import org.springframework.security.core.Authentication;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @GetMapping
    public List<User> getAllUsers()
    {
        return  userService.getAll(); //Service layer retrieves all users from database.Returns list as JSON response
    }

    @PostMapping
    public void createUser(@RequestBody User user) //Spring converts JSON to User object via @RequestBody
    {
        userService.saveNewUser(user); //Service layer saves user data to database
    }

    @PutMapping
    public ResponseEntity<?> updateUser(@RequestBody User user) {
        Authentication authentication= SecurityContextHolder.getContext().getAuthentication();
        String userName= authentication.getName();
        User userInDb = userService.findByuserName(userName); //fetches the existing user data from the databse using authenticated username.
        userInDb.setUserName(user.getUserName());
        userInDb.setPassword(user.getPassword());
        userService.saveNewUser(userInDb); //  Saves the updated user information.will encode password inside service
        return new ResponseEntity<>(HttpStatus.NO_CONTENT); //request was successfull but no content return -> 204 NO_CONTENT.
    }

    @DeleteMapping
    public ResponseEntity<?> deleteUserById()
    {
        Authentication authentication= SecurityContextHolder.getContext().getAuthentication();
        String userName= authentication.getName();
        userService.deleteUserByUserName(userName);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

}
