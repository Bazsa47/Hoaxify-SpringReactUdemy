package com.hoaxify.hoaxify;

import com.hoaxify.hoaxify.User.User;
import com.hoaxify.hoaxify.User.UserRepository;
import com.hoaxify.hoaxify.User.UserService;
import com.hoaxify.hoaxify.error.ApiError;
import com.hoaxify.hoaxify.shared.GenericResponse;
import org.apache.coyote.Response;
import org.apiguardian.api.API;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.support.BasicAuthenticationInterceptor;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class UserControllerTest {

    @Autowired
    TestRestTemplate testRestTemplate;
    @Autowired
    UserRepository userRepository;
    @Autowired
    UserService userService;

    private static final String API_1_0_USERS = "/api/1.0/users";

    public <T> ResponseEntity<T> postSignup(Object request, Class<T> response){
        return testRestTemplate.postForEntity(API_1_0_USERS,request,response);
    }

    public <T> ResponseEntity<T> getUsers(ParameterizedTypeReference<T> responseType){
        return testRestTemplate.exchange(API_1_0_USERS,HttpMethod.GET,null,responseType);
    }

    public <T> ResponseEntity<T> getUsers(String path, ParameterizedTypeReference<T> responseType){
        return testRestTemplate.exchange(path,HttpMethod.GET,null,responseType);
    }

    private void authenticate(String username) {
        //Vmi a headerrel
        testRestTemplate.getRestTemplate().getInterceptors().add(new BasicAuthenticationInterceptor(username,"P4ssword"));
    }

    @Before
    public void cleanUp(){
        userRepository.deleteAll();
        testRestTemplate.getRestTemplate().getInterceptors().clear();
    }

    @Test
    public void postUserIsValid_recieveOk() {
        User user = TestUtil.getUser();
        ResponseEntity<Object> objectResponseEntity = postSignup(user,Object.class);
        assertThat(objectResponseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    public void postUserIsValid_recieveSuccessMessage() {
        User user = TestUtil.getUser();
        ResponseEntity<GenericResponse> objectResponseEntity = postSignup(user,GenericResponse.class);
        assertThat(objectResponseEntity.getBody().getMessage()).isNotNull();
    }

    @Test
    public void postUser_whenUserIsValid_userSavedToDatabase() {
        User user = TestUtil.getUser();
        postSignup(user,Object.class);
        assertThat(userRepository.count()).isEqualTo(1);
    }

    @Test
    public void postUser_whenUserIsValid_passwordIsHashedInDatabase() {
        User user = TestUtil.getUser();
        testRestTemplate.postForEntity(API_1_0_USERS, user, Object.class);
        List<User> users = userRepository.findAll();
        User userInDb = users.get(0);
        assertThat(userInDb.getPassword()).isNotEqualTo(user.getPassword());
    }

    @Test
    public void postUser_whenUserHasNullUsername_recieveBadRequest() {
        User user = TestUtil.getUser();
        user.setUsername(null);
        ResponseEntity<Object> objectResponseEntity = postSignup(user, Object.class);
        assertThat(objectResponseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    public void postUser_whenUserHasNullDisplayname_recieveBadRequest() {
        User user = TestUtil.getUser();
        user.setDisplayName(null);
        ResponseEntity<Object> objectResponseEntity = postSignup(user, Object.class);
        assertThat(objectResponseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    public void postUser_whenUserHasNullPassword_recieveBadRequest() {
        User user = TestUtil.getUser();
        user.setPassword(null);
        ResponseEntity<Object> objectResponseEntity = postSignup(user, Object.class);
        assertThat(objectResponseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    public void postUser_whenUserHasUsernameWithLessThanRequired_recieveBadRequest() {
        User user = TestUtil.getUser();
        user.setUsername("abc");
        ResponseEntity<Object> objectResponseEntity = postSignup(user, Object.class);
        assertThat(objectResponseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    public void postUser_whenUserHasDisplaynameWithLessThanRequired_recieveBadRequest() {
        User user = TestUtil.getUser();
        user.setDisplayName("abc");
        ResponseEntity<Object> objectResponseEntity = postSignup(user, Object.class);
        assertThat(objectResponseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    public void postUser_whenUserHasPasswordWithLessThanRequired_recieveBadRequest() {
        User user = TestUtil.getUser();
        user.setPassword("P4sswd");
        ResponseEntity<Object> objectResponseEntity = postSignup(user, Object.class);
        assertThat(objectResponseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    public void postUser_whenUserHasUsernameThatExceedsTheMaximumLength_recieveBadRequest() {
        User user = TestUtil.getUser();
        String valueOf256Chars = IntStream.rangeClosed(1,256).mapToObj(x -> "a").collect(Collectors.joining());
        user.setUsername(valueOf256Chars);
        ResponseEntity<Object> objectResponseEntity = postSignup(user, Object.class);
        assertThat(objectResponseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    public void postUser_whenUserHasDisplaynameThatExceedsTheMaximumLength_recieveBadRequest() {
        User user = TestUtil.getUser();
        String valueOf256Chars = IntStream.rangeClosed(1,256).mapToObj(x -> "a").collect(Collectors.joining());
        user.setDisplayName(valueOf256Chars);
        ResponseEntity<Object> objectResponseEntity = postSignup(user, Object.class);
        assertThat(objectResponseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    public void postUser_whenUserHasPasswordThatExceedsTheMaximumLength_recieveBadRequest() {
        User user = TestUtil.getUser();
        String valueOf256Chars = IntStream.rangeClosed(1,256).mapToObj(x -> "a").collect(Collectors.joining());
        user.setPassword(valueOf256Chars+ "A1");
        ResponseEntity<Object> objectResponseEntity = postSignup(user, Object.class);
        assertThat(objectResponseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    public void postUser_whenUserHasPasswordWithAllLowercase_recieveBadRequest() {
        User user = TestUtil.getUser();
        String valueOf256Chars = IntStream.rangeClosed(1,256).mapToObj(x -> "a").collect(Collectors.joining());
        user.setPassword("alllowercase");
        ResponseEntity<Object> objectResponseEntity = postSignup(user, Object.class);
        assertThat(objectResponseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    public void postUser_whenUserHasPasswordWithAllUppercase_recieveBadRequest() {
        User user = TestUtil.getUser();
        String valueOf256Chars = IntStream.rangeClosed(1,256).mapToObj(x -> "a").collect(Collectors.joining());
        user.setPassword("ALLUPPERCASE");
        ResponseEntity<Object> objectResponseEntity = postSignup(user, Object.class);
        assertThat(objectResponseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    public void postUser_whenUserHasPasswordWithAllNumber_recieveBadRequest() {
        User user = TestUtil.getUser();
        String valueOf256Chars = IntStream.rangeClosed(1,256).mapToObj(x -> "a").collect(Collectors.joining());
        user.setPassword("12345678");
        ResponseEntity<Object> objectResponseEntity = postSignup(user, Object.class);
        assertThat(objectResponseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    public void postUSerWhenUserIsInvalidWeReceiveApiError() {
        User user = new User();
        ResponseEntity<ApiError> responseEntity = postSignup(user, ApiError.class);
        assertThat(responseEntity.getBody().getUrl()).isEqualTo(API_1_0_USERS);
    }

    @Test
    public void postUSerWhenUserIsInvalidWeReceiveApiErrorWithValidationErrors() {
        User user = new User();
        ResponseEntity<ApiError> responseEntity = postSignup(user, ApiError.class);
        assertThat(responseEntity.getBody().getValidationErrors().size()).isEqualTo(3);
    }

    @Test
    public void postUserWhenUserHasNullUsername_receiveMessageOfNullErrorForUsername() {
        User user = TestUtil.getUser();
        user.setUsername(null);
        ResponseEntity<ApiError> response = postSignup(user,ApiError.class);
        Map<String,String> validationErrors = response.getBody().getValidationErrors();
        assertThat(validationErrors.get("username")).isEqualTo("Username cannot be null");
    }

    @Test
    public void postUserWhenUserHasNullPassword_receiveGenericMessageForNullError() {
        User user = TestUtil.getUser();
        user.setPassword(null);
        ResponseEntity<ApiError> response = postSignup(user,ApiError.class);
        Map<String,String> validationErrors = response.getBody().getValidationErrors();
        assertThat(validationErrors.get("password")).isEqualTo("Cannot be null");
    }

    @Test
    public void postUserWhenUserHasInvalidLengthUsername_receiveGenericMessageOfSizeError() {
        User user = TestUtil.getUser();
        user.setUsername("abc");
        ResponseEntity<ApiError> response = postSignup(user,ApiError.class);
        Map<String,String> validationErrors = response.getBody().getValidationErrors();
        assertThat(validationErrors.get("username")).isEqualTo("It must have minimum 4 and maximum 255 characters!");
    }

    @Test
    public void postUserWhenUserHasInvalidPasswordPattern_receiveMessageOfPasswordError() {
        User user = TestUtil.getUser();
        user.setPassword("allowercase");
        ResponseEntity<ApiError> response = postSignup(user,ApiError.class);
        Map<String,String> validationErrors = response.getBody().getValidationErrors();
        assertThat(validationErrors.get("password")).isEqualTo("Password must have at least on uppercase, on lowercase letter and one number!");
    }

    @Test
    public void postUserWhenOtherUserHasSameUsername_expectBadRequest() {
        userRepository.save(TestUtil.getUser());
        User user = TestUtil.getUser();
        ResponseEntity<Object> objectResponseEntity = postSignup(user, Object.class);
        assertThat(objectResponseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    public void postUserWhenOtherUserHasSameUsername_expectMessageOfDuplicateUsername() {
        userRepository.save(TestUtil.getUser());
        User user = TestUtil.getUser();
        ResponseEntity<ApiError> response = postSignup(user,ApiError.class);
        Map<String,String> validationErrors = response.getBody().getValidationErrors();
        assertThat(validationErrors.get("username")).isEqualTo("Username already taken!");
    }

    @Test
    public void getUsers_whenThereAreNoUsersInDatabase_weReceiveOk() {
        ResponseEntity<Object> forEntity = getUsers(new ParameterizedTypeReference<Object>(){});
        assertThat(forEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    public void getUsers_whenThereAreNoUsersInDb_receivePageWithZeroItems() {
        ResponseEntity<TestPage<Object>> response = getUsers(new ParameterizedTypeReference<TestPage<Object>>(){});
        assertThat(response.getBody().getNumberOfElements()).isEqualTo(0);
    }

    @Test
    public void getUsers_whenThereIsAUsersInDb_receivePageWithUser() {
        userRepository.save(TestUtil.getUser());
        ResponseEntity<TestPage<Object>> response = getUsers(new ParameterizedTypeReference<TestPage<Object>>(){});
        assertThat(response.getBody().getTotalElements()).isEqualTo(1);
    }

    @Test
    public void getUsers_whenThereIsAUsersInDb_receiveUserWithoutPassword() {
        userRepository.save(TestUtil.getUser());
        ResponseEntity<TestPage<Map<String,Object>>> response = getUsers(new ParameterizedTypeReference<TestPage<Map<String,Object>>>(){});
        Map<String,Object> entity = response.getBody().getContent().get(0);
        assertThat(entity.containsKey("password")).isFalse();
    }

    @Test
    public void getUsersWhenPageIsRequestedForThreeItemsPerPageWhereDatabaseHasTwentyUsers_receiveThreeUsers() {
        IntStream.rangeClosed(1,20).mapToObj(i -> "test-user-"+i)
                .map(username -> TestUtil.getUser(username))
                .forEach(user -> userRepository.save(user));
        String path = API_1_0_USERS +"?page=0&size=3";
        ResponseEntity<TestPage<Object>> response = getUsers(path, new ParameterizedTypeReference<TestPage<Object>>() {});
        assertThat(response.getBody().getContent().size()).isEqualTo(3);
    }

    @Test
    public void getUsers_whenPageSizeNotProvided_receivePageSizeAsTen() {
        ResponseEntity<TestPage<Object>> response = getUsers(new ParameterizedTypeReference<TestPage<Object>>(){});
        assertThat(response.getBody().getSize()).isEqualTo(10);
    }

    @Test
    public void getUsers_whenPageSizeisGreaterThanHundred_receivePageSizeAsHundred() {
        String path= API_1_0_USERS +"?size=500";
        ResponseEntity<TestPage<Object>> response = getUsers(path, new ParameterizedTypeReference<TestPage<Object>>(){});
        assertThat(response.getBody().getSize()).isEqualTo(100);
    }

    @Test
    public void getUsers_whenPageSizeisNegative_receivePageSizeAsTen() {
        String path= API_1_0_USERS +"?size=-5";
        ResponseEntity<TestPage<Object>> response = getUsers(path, new ParameterizedTypeReference<TestPage<Object>>(){});
        assertThat(response.getBody().getSize()).isEqualTo(10);
    }

    @Test
    public void getUsers_whenPageisNegative_receiveFirstPage() {
        String path= API_1_0_USERS +"?page=-5";
        ResponseEntity<TestPage<Object>> response = getUsers(path, new ParameterizedTypeReference<TestPage<Object>>(){});
        assertThat(response.getBody().getNumber()).isEqualTo(0);
    }

    @Test
    public void getUsers_WhenUSerLoggedIn_receivePageWithoutLoggedInUser() {
        userService.save(TestUtil.getUser("user1"));
        userService.save(TestUtil.getUser("user2"));
        userService.save(TestUtil.getUser("user3"));
        authenticate("user1");
        ResponseEntity<TestPage<Object>> response = getUsers( new ParameterizedTypeReference<TestPage<Object>>(){});
        assertThat(response.getBody().getTotalElements()).isEqualTo(2);
    }
}
