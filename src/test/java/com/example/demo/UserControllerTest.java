package com.example.demo;


import com.example.demo.controllers.UserController;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.CreateUserRequest;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class UserControllerTest {

    private CartRepository cartRepository = mock(CartRepository.class);

    private BCryptPasswordEncoder encoder = mock(BCryptPasswordEncoder.class);

    private UserController userController;

    private UserRepository userRepository = mock(UserRepository.class);



    @Before
    public void setUp() {
        userController = new UserController();
        Utils.injectObjects(userController, "userRepository", userRepository);
        Utils.injectObjects(userController, "cartRepository", cartRepository);
        Utils.injectObjects(userController, "bCryptPasswordEncoder", encoder);
    }

    @Test
    public void createUserTest() {
        when(encoder.encode("Password")).thenReturn("encodedPassword");
        CreateUserRequest user = new CreateUserRequest();
        user.setUsername("User");
        user.setPassword("password123");
        user.setConfirmPassword("password123");
        ResponseEntity<User> userResponseEntity = userController.createUser(user);

        Assert.assertNotNull(userResponseEntity);
        Assert.assertEquals(200, userResponseEntity.getStatusCodeValue());
        User user1 = userResponseEntity.getBody();
        Assert.assertNotNull(user1);
    }

    @Test
    public void createUserWithShortPassword_shouldGiveError() throws Exception{
        when(encoder.encode("Password")).thenReturn("encodedPassword");
        CreateUserRequest user = new CreateUserRequest();
        user.setUsername("User");
        user.setPassword("pass");
        user.setConfirmPassword("pass");

        ResponseEntity<User> userResponseEntity = userController.createUser(user);
        Assert.assertNotNull(userResponseEntity);
        Assert.assertEquals(400, userResponseEntity.getStatusCodeValue());
    }

    @Test
    public void createUserWithNotMatchedConfirmPassword_shouldGiveError() throws Exception{
        when(encoder.encode("Password")).thenReturn("encodedPassword");
        CreateUserRequest user = new CreateUserRequest();
        user.setUsername("User");
        user.setPassword("password123");
        user.setConfirmPassword("password12");

        ResponseEntity<User> userResponseEntity = userController.createUser(user);
        Assert.assertNotNull(userResponseEntity);
        Assert.assertEquals(400, userResponseEntity.getStatusCodeValue());
    }


    @Test
    public void findUserById_Test() {
        long id = 1L;
        User user = new User();
        user.setUsername("User");
        user.setPassword("pass");
        user.setId(id);

        when(userRepository.findById(id)).thenReturn(Optional.of(user));
        ResponseEntity<User> userResponseEntity = userController.findById(id);
        Assert.assertNotNull(userResponseEntity);
        Assert.assertEquals(200, userResponseEntity.getStatusCodeValue());
    }

    @Test
    public void findById_ErrorTest() throws Exception {
        long id = 1L;
        User user = new User();
        user.setUsername("User");
        user.setPassword("pass");
        user.setId(id);

        when(userRepository.findById(id)).thenReturn(Optional.of(user));
        Assert.assertEquals(404,userController.findById(2L).getStatusCodeValue());
    }


    @Test
    public void findUserByUserNameTest() {
        long id = 1L;
        User user = new User();
        user.setUsername("User");
        user.setPassword("pass");
        user.setId(id)
        ;
        when(userRepository.findByUsername("User")).thenReturn(user);
        ResponseEntity<User> response = userController.findByUserName("User");
        Assert.assertNotNull(response);
        Assert.assertEquals(200, response.getStatusCodeValue());
    }



    @Test
    public void findByUserName_ErrorTest() throws Exception {
        User user = new User();
        user.setUsername("test");
        user.setPassword("password");

        when(userRepository.findByUsername("test")).thenReturn(user);
        Assert.assertEquals(404, userController.findByUserName("wrong_username").getStatusCodeValue());

    }

}
