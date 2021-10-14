package com.hoaxify.hoaxify;

import com.hoaxify.hoaxify.User.User;
import com.hoaxify.hoaxify.User.UserRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import static org.assertj.core.api.Assertions.assertThat;
@RunWith(SpringRunner.class)
@DataJpaTest
@ActiveProfiles("test")
public class UserRepositoryTest {

    @Autowired
    TestEntityManager testEntityManager;

    @Autowired
    UserRepository userRepository;

    @Test
    public void findByUsernameWhenUserExists_returnUser() {
        testEntityManager.persist(TestUtil.getUser());

        User indb = userRepository.findByUsername("test-user");

        assertThat(indb).isNotNull();
    }

    @Test
    public void findByUsernameWhenUserNotExists_returnNull() {
        User indb = userRepository.findByUsername("test-user2");
        assertThat(indb).isNull();
    }
}
