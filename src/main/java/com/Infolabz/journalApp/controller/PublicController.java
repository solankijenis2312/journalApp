package com.Infolabz.journalApp.controller;

import com.Infolabz.journalApp.entity.User;
import com.Infolabz.journalApp.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

//This controller handles publicly accessible endpoints that do not require authentication.

@RestController
@RequestMapping("/public")
public class PublicController {

    @Autowired
    private UserService userService;

    @GetMapping("/health-check")
    public String healthCheck() {
        return "OK";
    }

    @PostMapping("/create-user")
    public ResponseEntity<?> createUser(@RequestBody User user) {
        boolean saved = userService.saveNewUser(user); // Call the service method

        if (saved) {
            // User successfully created
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(Map.of(
                            "status", "success",
                            "message", "User created successfully"



                    ));
        } else {

            return ResponseEntity.status(HttpStatus.BAD_REQUEST) // Or HttpStatus.CONFLICT (409) for duplicate usernames
                    .body(Map.of(
                            "status", "error",
                            "message", "Failed to create user. Please check the username or server logs."
                    ));
        }
    }
}
