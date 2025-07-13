package com.Infolabz.journalApp.config;

import com.Infolabz.journalApp.service.UserDetailsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration //When Spring processes this class, it will create and configure the beans defined within it.
@EnableWebSecurity
public class SpringSecurity extends WebSecurityConfigurerAdapter { //UserDetailsServiceImpl is a custom service that is responsible for loading user-specific data during authentication

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .authorizeRequests()
                .antMatchers("/journal/**","/user/**").authenticated()
                .antMatchers("/admin/**").hasRole("ADMIN")
                .anyRequest().permitAll()
                .and()
                .httpBasic();
        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and().csrf().disable();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService) //to retrieve user data from database information during the authentication process.
                .passwordEncoder(passwordEncoder()); //encoded password stored in database compared during basic authentication when entered password through -> Auth -> Basic Authentication.
    }

    @Bean
    public PasswordEncoder passwordEncoder() { //hat should be used to verify the user's password during authentication.
        return new BCryptPasswordEncoder(); //BCrypt is a strong, adaptive hash function


    }
}