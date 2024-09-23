package vn.hoidanit.jobhunter.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PutMapping;
import vn.hoidanit.jobhunter.domain.User;
import vn.hoidanit.jobhunter.service.UserService;
import vn.hoidanit.jobhunter.util.error.IdInvalidException;

import java.util.List;

@RestController
public class UserController {
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    public UserController(UserService userService,
            PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/users")
    public ResponseEntity<User> createNewUser(@RequestBody User userPostMan) {

        String hashPassword = this.passwordEncoder.encode(userPostMan.getPassword());
        userPostMan.setPassword(hashPassword);
        User newUser = this.userService.handleCreateUser(userPostMan);

        return ResponseEntity.status(HttpStatus.CREATED).body(newUser);
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable("id") long id)
            throws IdInvalidException {
        if (id >= 1500) {
            throw new IdInvalidException("id khong lon hơ 1500");
        }
        this.userService.handleDeleteUser(id);
        return ResponseEntity.noContent().build();
        // return ResponseEntity.status(HttpStatus.OK).body("ngo");
    }

    // fetch user by id
    @GetMapping("/users/{id}")
    public ResponseEntity<User> getUserById(@PathVariable("id") long id) {
        User fetchUser = this.userService.fetchUserById(id);
        return ResponseEntity.status(HttpStatus.OK).body(fetchUser);
    }

    // fetch all user
    @GetMapping("/users")
    public ResponseEntity<List<User>> getAllUser() {
        List<User> allUser = this.userService.fetchAllUser();
        return ResponseEntity.status(HttpStatus.OK).body(allUser);
    }

    // update user
    @PutMapping("/users")
    public ResponseEntity<User> updateUser(@RequestBody User newUser) {
        User oldUser = this.userService.handleUpdateUser(newUser);
        // return ResponseEntity.status(HttpStatus.OK).body(oldUser);
        return ResponseEntity.ok(oldUser);
    }

}
