package com.hoaxify.hoaxify;

import com.hoaxify.hoaxify.User.User;
import com.hoaxify.hoaxify.User.UserRepository;
import com.hoaxify.hoaxify.User.UserService;
import com.hoaxify.hoaxify.error.ApiError;
import org.apiguardian.api.API;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.support.BasicAuthenticationInterceptor;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class LoginControllerTest {

    @Autowired
    TestRestTemplate testRestTemplate;

    @Autowired
    UserRepository userRepository;

    @Autowired
    UserService userService;

    private static final String API_1_0_LOGIN ="/api/1.0/login";

    public <T> ResponseEntity<T> login(Class<T> responseType){
        return testRestTemplate.postForEntity(API_1_0_LOGIN,null,responseType);
    }

    public <T> ResponseEntity<T> login(ParameterizedTypeReference<T> responseType){
        return testRestTemplate.exchange(API_1_0_LOGIN, HttpMethod.POST,null,responseType);
    }

    private void authenticate() {
        //Vmi a headerrel
        testRestTemplate.getRestTemplate().getInterceptors().add(new BasicAuthenticationInterceptor("test-user","P4ssword"));
    }

    @Before
    public void cleanup(){
        userRepository.deleteAll();
        testRestTemplate.getRestTemplate().getInterceptors().clear();
    }

    @Test
    public void postLogin_withoutUserCredentials_receiveUnauthorized() {
        ResponseEntity<Object> login = login(Object.class);
        assertThat(login.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    public void postLogin_withIncorrectUserCredentials_receiveUnauthorized() {
        authenticate();
        ResponseEntity<Object> login = login(Object.class);
        assertThat(login.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    public void postLogin_withoutUserCredentials_receiveApiError() {
        ResponseEntity<ApiError> login = login(ApiError.class);
        assertThat(login.getBody().getUrl()).isEqualTo(API_1_0_LOGIN);
    }

    @Test
    public void postLogin_withoutUserCredentials_receiveApiErrorWithoutValidationErrors() {
        ResponseEntity<String> login = login(String.class);
        assertThat(login.getBody().contains("validationErrors")).isFalse();
    }

    @Test
    public void postLogin_withIncorrectUserCredentials_receiveUnauthorizedWithoutWWWAuthenticationHeader() {
        authenticate();
        ResponseEntity<Object> login = login(Object.class);
        assertThat(login.getHeaders().containsKey("WWW-Authenticate")).isFalse();
    }

    @Test
    public void postLoginWithValidCredentials_receiveOK() {
        userService.save(TestUtil.getUser());
        authenticate();

        ResponseEntity<Object> login = login(Object.class);
        assertThat(login.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    public void postLoginWithValidCredentials_receiveLoggedInUserId() {
        User user = userService.save(TestUtil.getUser());
        authenticate();
        ResponseEntity<Map<String,Object>> login = login(new ParameterizedTypeReference<Map<String, Object>>() {});

        Map<String,Object> body = login.getBody();

        Integer id = (Integer)body.get("id");
        assertThat(id).isEqualTo(user.getId());
    }

    @Test
    public void postLoginWithValidCredentials_receiveLoggedInUsersImage() {
        User user = userService.save(TestUtil.getUser());
        authenticate();
        ResponseEntity<Map<String,Object>> login = login(new ParameterizedTypeReference<Map<String, Object>>() {});

        Map<String,Object> body = login.getBody();

        String image = (String)body.get("image");
        assertThat(image).isEqualTo(user.getImage());
    }

    @Test
    public void postLoginWithValidCredentials_receiveLoggedInUserDisplayname() {
        User user = userService.save(TestUtil.getUser());
        authenticate();
        ResponseEntity<Map<String,Object>> login = login(new ParameterizedTypeReference<Map<String, Object>>() {});

        Map<String,Object> body = login.getBody();

        String display = (String)body.get("displayName");
        assertThat(display).isEqualTo(user.getDisplayName());
    }

    @Test
    public void postLoginWithValidCredentials_receiveLoggedInUserUsername() {
        User user = userService.save(TestUtil.getUser());
        authenticate();
        ResponseEntity<Map<String,Object>> login = login(new ParameterizedTypeReference<Map<String, Object>>() {});

        Map<String,Object> body = login.getBody();

        String name = (String)body.get("username");
        assertThat(name).isEqualTo(user.getUsername());
    }

    @Test
    public void postLoginWithValidCredentials_notReceiveLoggedInUserPassword() {
        User user = userService.save(TestUtil.getUser());
        authenticate();
        ResponseEntity<Map<String,Object>> login = login(new ParameterizedTypeReference<Map<String, Object>>() {});

        Map<String,Object> body = login.getBody();
        assertThat(body.containsKey("password")).isFalse();
    }
}
