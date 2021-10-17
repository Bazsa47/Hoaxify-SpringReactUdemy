package com.hoaxify.hoaxify.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;

@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    AuthUserService authUserService;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable();

        //egy olyan entry pointtal térünk vissza, ami nem tartalmazza a wwwauthenticate headert, így nem fog felugrani a bejelentkezés
        http.httpBasic().authenticationEntryPoint(new BasicAuthenticationEntryPoint());

        http
                .authorizeRequests().antMatchers(HttpMethod.POST,"/api/1.0/login").authenticated()
                .antMatchers(HttpMethod.PUT,"/api/1.0/users/{id:[0-9]+}").authenticated()
                .and().authorizeRequests().anyRequest().permitAll();

        //nem generál minden felhasználónak sessiont
        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);

    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(authUserService).passwordEncoder(passwordEncoder());
    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }
}
