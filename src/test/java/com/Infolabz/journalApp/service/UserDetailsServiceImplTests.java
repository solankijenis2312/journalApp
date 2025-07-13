package com.Infolabz.journalApp.service;

import com.Infolabz.journalApp.entity.User;
import com.Infolabz.journalApp.respository.UserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.userdetails.UserDetails;
import static org.mockito.Mockito.*;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.ArrayList;

@SpringBootTest
public class UserDetailsServiceImplTests {

    @InjectMocks //Create an instance of the class you want to test.Automatically inject any mock objects (created with @Mock or @Spy) into the dependencies of that instance.
    private UserDetailsServiceImpl userDetailsService;

    @Mock //Its main purpose is to create a mock object for a dependency that is NOT managed by Spring's ApplicationContext.It creates a "dummy" version of a class or interface
    private UserRepository userRepository;

    //@MockBean -> Spring Boot automatically injects the created mock instance into the field where @MockBean is used, and also into any other beans in the context that depend on it.

    @BeforeEach
    void setup()
    {
        MockitoAnnotations.initMocks(this); //initialize the all mocks of this class and inject it. means userRepository initialize and inject into userDetailsService.
    }
    @Test
    void loadUserByUsernameTest()
    {
        when(userRepository.findByuserName(ArgumentMatchers.anyString())).thenReturn(User.builder().userName("Dev").password("Dev123").roles(new ArrayList<>()).build());
        UserDetails user = userDetailsService.loadUserByUsername("Dev");
        Assertions.assertNotNull(user);
    }
}
