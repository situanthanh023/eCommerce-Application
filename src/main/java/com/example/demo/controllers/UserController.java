package com.example.demo.controllers;

import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.CreateUserRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    private final Logger log = LoggerFactory.getLogger(UserController.class);

    @GetMapping("/id/{id}")
    public ResponseEntity<User> findById(@PathVariable Long id) {
        return ResponseEntity.of(userRepository.findById(id));
    }

    @GetMapping("/{username}")
    public ResponseEntity<User> findByUserName(@PathVariable String username) {
        User user = userRepository.findByUsername(username);
        return user == null ? ResponseEntity.notFound().build() : ResponseEntity.ok(user);
    }


    @PostMapping("/create")
    public ResponseEntity createUser(@RequestBody CreateUserRequest createUserRequest) {
        log.debug("Input create user request : {}", createUserRequest);
        User user = new User();
        user.setUsername(createUserRequest.getUsername());


        String password = createUserRequest.getPassword();


        String confirmPassword = createUserRequest.getConfirmPassword();

        boolean isPasswordLengthAcceptable = password.length() >= 7;


        boolean isConfirmedPasswordMatched = password.equals(confirmPassword);

        if (!isPasswordLengthAcceptable) {
            log.error("Unable to create user  " + user.getUsername() + ",  invalid password");
            return ResponseEntity.badRequest().body("Length of password must be greater than 7");
        } else if (!isConfirmedPasswordMatched) {
            log.error("Unable to create user " + user.getUsername() + ", password mismatch");
            return ResponseEntity.badRequest().build();
        }


        String encodedPassword = bCryptPasswordEncoder.encode(createUserRequest.getPassword());
        log.info("Encoded password stored");
        user.setPassword(encodedPassword);


        Cart cart = new Cart();
        cartRepository.save(cart);
        user.setCart(cart);
        userRepository.save(user);

        log.info("user created successfully,  user  " + user.getUsername());
        return ResponseEntity.ok(user);
    }

}
