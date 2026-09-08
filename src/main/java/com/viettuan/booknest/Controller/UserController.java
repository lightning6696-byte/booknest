package com.viettuan.booknest.Controller;

import com.viettuan.booknest.entity.User;
import com.viettuan.booknest.service.UserService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(
            UserService userService
    ) {
        this.userService = userService;
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public User getUserById(
            @PathVariable Long id
    ) {
        return userService.getUserById(id);
    }

    @PostMapping
    public User createUser(
            @RequestBody User user
    ) {
        return userService.createUser(user);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public User updateUser(
            @PathVariable Long id,
            @RequestBody User user
    ) {
        return userService.updateUser(id, user);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteUser(
            @PathVariable Long id
    ) {
        userService.deleteUser(id);
    }
}