package com.hoaxify.hoaxify;

import com.hoaxify.hoaxify.User.*;
import com.hoaxify.hoaxify.error.ApiError;
import com.hoaxify.hoaxify.shared.GenericResponse;
import org.apache.commons.io.FileUtils;
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
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.support.BasicAuthenticationInterceptor;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.util.Base64;
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

    public <T> ResponseEntity<T> putUser(long id, HttpEntity<?> requestEntity, Class<T> responseType){
        String path= API_1_0_USERS + "/"+id;
        return testRestTemplate.exchange(path,HttpMethod.PUT,requestEntity,responseType);
    }

    private void authenticate(String username) {
        //Vmi a headerrel
        testRestTemplate.getRestTemplate().getInterceptors().add(new BasicAuthenticationInterceptor(username,"P4ssword"));
    }

    private UserUpdateVM createValidUserUpdateVM() {
        UserUpdateVM updateUser = new UserUpdateVM();
        updateUser.setDisplayName("nesDisplayName");
        return updateUser;
    }

    public <T> ResponseEntity<T> getUserByName(String username, Class<T> responseType){
        String path = API_1_0_USERS+"/"+username;
        return testRestTemplate.getForEntity(path,responseType);
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

    @Test
    public void getUserByUsername_whenUserExists_receiveOk() {
        String username = "test-user";
        userService.save(TestUtil.getUser(username));
        ResponseEntity<Object> result = getUserByName(username, Object.class);
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    public void getUserByUsername_whenUserExists_receiveUserWithoutPassword() {
        String username = "test-user";
        userService.save(TestUtil.getUser(username));
        ResponseEntity<String> result = getUserByName(username, String.class);
        assertThat(result.getBody().contains("password")).isFalse();
    }

    @Test
    public void getUserByUsername_whenUserDoesNotExists_receiveNotFound() {
        ResponseEntity<Object> result = getUserByName("notexisting", Object.class);
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    public void getUserByUsername_whenUserDoesNotExists_receiveApiError() {
        ResponseEntity<ApiError> result = getUserByName("notexisting", ApiError.class);
        assertThat(result.getBody().getMessage().contains("notexisting")).isTrue();
    }

    @Test
    public void putUser_whenUnauthorizedUserSendsTheRequest_receiveUnauthorized() {
        ResponseEntity<Object> response = putUser(123,null,Object.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    public void putUser_whenAuthorizedUserSendsUpdateForAnotherUser_receiveForbidden() {
        User user = userService.save(TestUtil.getUser("user1"));
        authenticate(user.getUsername());

        long anotherUserId = user.getId()+123;
        ResponseEntity<Object> response = putUser(anotherUserId,null,Object.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    public void putUser_whenUnauthorizedUserSendsTheRequest_receiveApiError() {
        ResponseEntity<ApiError> response = putUser(123,null,ApiError.class);
        assertThat(response.getBody().getUrl()).contains("users/123");
    }

    @Test
    public void putUser_whenAuthorizedUserSendsUpdateForAnotherUser_receiveApiError() {
        User user = userService.save(TestUtil.getUser("user1"));
        authenticate(user.getUsername());

        long anotherUserId = user.getId()+123;
        ResponseEntity<ApiError> response = putUser(anotherUserId,null,ApiError.class);
        assertThat(response.getBody().getUrl()).contains("users/"+anotherUserId);
    }

    @Test
    public void putUser_withValidRequestBodyFromAuthorizedUser_ReceiveOk() {
        User user = userService.save(TestUtil.getUser("user1"));
        authenticate(user.getUsername());
        UserUpdateVM updateUser = createValidUserUpdateVM();

        HttpEntity<UserUpdateVM> requestEntity = new HttpEntity<>(updateUser);
        ResponseEntity<Object> response = putUser(user.getId(),requestEntity,Object.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    public void putUser_withValidRequestBodyFromAuthorizedUser_displayNameUpdated() {
        User user = userService.save(TestUtil.getUser("user1"));
        authenticate(user.getUsername());
        UserUpdateVM updateUser = createValidUserUpdateVM();

        HttpEntity<UserUpdateVM> requestEntity = new HttpEntity<>(updateUser);
        putUser(user.getId(),requestEntity,Object.class);

        User userInDB = userRepository.findByUsername("user1");
        assertThat(userInDB.getDisplayName()).isEqualTo(updateUser.getDisplayName());
    }

    @Test
    public void putUser_withValidRequestBodyFromAuthorizedUser_receiveUserVMWithUpdatedDisplayName() {
        User user = userService.save(TestUtil.getUser("user1"));
        authenticate(user.getUsername());

        UserUpdateVM updateUser = createValidUserUpdateVM();

        HttpEntity<UserUpdateVM> requestEntity = new HttpEntity<>(updateUser);
        ResponseEntity<UserVM> response = putUser(user.getId(), requestEntity, UserVM.class);

        assertThat(response.getBody().getDisplayName()).isEqualTo(updateUser.getDisplayName());
    }

    /*@Test
    public void putUser_withValidRequestBodyWithSupportedImageFromAuthorizedUser_receiveUserVMWithRandomImageName() throws IOException {
        User user = userService.save(TestUtil.getUser("user1"));
        authenticate(user.getUsername());
        UserUpdateVM updateUser = createValidUserUpdateVM();

        ClassPathResource imageResource = new ClassPathResource("profile.png");

        byte[] imageArr = FileUtils.readFileToByteArray(imageResource.getFile());
        String imageString = Base64.getEncoder().encodeToString(imageArr);
        updateUser.setImage(imageString);

        HttpEntity<UserUpdateVM> requestEntity = new HttpEntity<>(updateUser);
        ResponseEntity<UserVM> response = putUser(user.getId(), requestEntity, UserVM.class);

        assertThat(response.getBody().getImage()).isNotEqualTo("profile-image.png");
    } */




}
