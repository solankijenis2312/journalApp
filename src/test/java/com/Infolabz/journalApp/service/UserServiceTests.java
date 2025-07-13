package com.Infolabz.journalApp.service;

import com.Infolabz.journalApp.entity.User;
import com.Infolabz.journalApp.respository.UserRepository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class UserServiceTests {


    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JournalEntryService journalEntryService;

    @BeforeAll
    static void setup()
    {
        System.out.println("Start before All test started..");
    }


    @Disabled
    @Test
    public void testFindByUserName() {

        assertEquals(4, 2 + 2);
        assertNotNull(userRepository.findByuserName("Dev")); //user repository will be null because then bean is created spring at the run time implementation in UserRepository Interface. means inject bean or instance during the run time automatically.
        User user= userRepository.findByuserName("yashvi");
        assertTrue(!user.getJournalEntries().isEmpty()); //means yashvi journal entries not empty.
    }


    @Disabled
    @ParameterizedTest
    @ValueSource(strings= { //@ValueSource -> Supplies one simple parameter per test run (like String, int, etc.)
            "yashvi",
            "Dev",
            "kavee",
            //"Aarchi" //get error because this name is not saved in databse.
    })

    public void testFindByUserName1(String name) {

        assertNotNull(userRepository.findByuserName(name),"failed for "+name); //means yashvi journal entries not empty.
    }

    @Disabled
    @ParameterizedTest
    @ArgumentsSource(UserArgumentsProvider.class) // Injects test arguments from a custom class (like a UserArgumentsProvider.class).
    public void testFindByUserName2(User user)
    {
        assertTrue(userService.saveNewUser(user));
    }

    @Disabled
    @ParameterizedTest
    @CsvSource({ //@CsvSource -> Supplies multiple parameters per run, separated by commas.
            "1,2,3",
            "5,5,10",
            //"5,3,9" //get a error because 5+3=8 not a 9

    })
    public void test(int a, int b, int exprected)
    {
        assertEquals(exprected, a+b);
    }




}