package com.hoaxify.hoaxify.User;

import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class UniqueUsernameValidator implements ConstraintValidator<UniqueUsername,String> {

    @Autowired
    UserRepository userRepository;

    @Override
    public boolean isValid(String s, ConstraintValidatorContext constraintValidatorContext) {
        User inDB = userRepository.findByUsername(s);
        return inDB == null;
    }
}
