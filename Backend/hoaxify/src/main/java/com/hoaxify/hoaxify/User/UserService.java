package com.hoaxify.hoaxify.User;

import com.hoaxify.hoaxify.error.NotFoundException;
import com.hoaxify.hoaxify.file.FileService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.UUID;

@Service
public class UserService {

    UserRepository userRepository;

    BCryptPasswordEncoder passwordEncoder;

    FileService fileService;

    public UserService(UserRepository userRepository, FileService fileService) {
        this.userRepository = userRepository;
        this.fileService = fileService;
        this.passwordEncoder = new BCryptPasswordEncoder();
    }

    public User save(User user){
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    public Page<User> getUsers(User loggedInUser, Pageable pageable) {
        if (loggedInUser != null)
            return userRepository.findByUsernameNot(loggedInUser.getUsername(), pageable);
        return userRepository.findAll(pageable);
    }

    public User getByUsername(String username) {
        User userInDB = userRepository.findByUsername(username);
        if (userInDB==null) throw new NotFoundException(username + " not found!");
        return userInDB;
    }

    public User update(long id, UserUpdateVM userUpdate) {
        User inDb = userRepository.getById(id);
        inDb.setDisplayName(userUpdate.getDisplayName());

        if(userUpdate.getImage() != null){
            String savedImageName ;
            try {
                savedImageName = fileService.saveProfileImage(userUpdate.getImage());
                fileService.deleteProfileImage(inDb.getImage());
                inDb.setImage(savedImageName);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return userRepository.save(inDb);
    }
}
