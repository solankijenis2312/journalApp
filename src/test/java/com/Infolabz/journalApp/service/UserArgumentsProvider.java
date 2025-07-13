package com.Infolabz.journalApp.service;

import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.junit.jupiter.params.provider.ArgumentsSource;
import org.springframework.beans.factory.annotation.Autowired;

import com.Infolabz.journalApp.entity.User;



import java.util.stream.Stream;

public class UserArgumentsProvider implements ArgumentsProvider {

    @Autowired
    private User user;

    @Override
    public Stream<? extends Arguments> provideArguments(ExtensionContext context) {
        return Stream.of(
                Arguments.of(new User("yashvi", "yashvi123"),
                Arguments.of(new User("khushi", "khushi123"))
        ));
    }
}
