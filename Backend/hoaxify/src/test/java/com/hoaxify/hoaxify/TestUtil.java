package com.hoaxify.hoaxify;

import com.hoaxify.hoaxify.User.User;

public class TestUtil {
    public static User getUser() {
        User user = new User();
        user.setUsername("test-user");
        user.setDisplayName("test-display");
        user.setPassword("P4ssword");
        user.setImage("profile-image.png");
        return user;
    }

    public static User getUser(String username) {
        User user = getUser();
        user.setUsername(username);
        return user;
    }
}
