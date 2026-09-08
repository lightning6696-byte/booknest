package com.viettuan.booknest.service;

import com.viettuan.booknest.entity.Role;
import com.viettuan.booknest.entity.User;
import com.viettuan.booknest.exception.BadRequestException;
import com.viettuan.booknest.exception.ConflictException;
import com.viettuan.booknest.exception.ResourceNotFoundException;
import com.viettuan.booknest.repository.RoleRepository;
import com.viettuan.booknest.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(
            UserRepository userRepository,
            RoleRepository roleRepository,
            PasswordEncoder passwordEncoder
    ) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User getUserById(Long id) {

        return userRepository
                .findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Không tìm thấy người dùng có id: " + id
                        )
                );
    }

    public User createUser(User user) {

        if (user.getFullName() == null
                || user.getFullName().isBlank()) {

            throw new BadRequestException(
                    "Họ tên không được để trống"
            );
        }

        if (user.getEmail() == null
                || user.getEmail().isBlank()) {

            throw new BadRequestException(
                    "Email không được để trống"
            );
        }

        if (user.getPassword() == null
                || user.getPassword().isBlank()) {

            throw new BadRequestException(
                    "Mật khẩu không được để trống"
            );
        }

        if (userRepository.existsByEmail(user.getEmail())) {

            throw new ConflictException(
                    "Email đã tồn tại: " + user.getEmail()
            );
        }

        Role readerRole = roleRepository
                .findByName("READER")
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Không tìm thấy role READER"
                        )
                );

        user.setPassword(
                passwordEncoder.encode(
                        user.getPassword()
                )
        );

        user.setRole(readerRole);
        user.setActive(true);

        return userRepository.save(user);
    }

    public User updateUser(Long id, User user) {

        User existingUser = userRepository
                .findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Không tìm thấy người dùng có id: " + id
                        )
                );

        if (user.getFullName() == null
                || user.getFullName().isBlank()) {

            throw new BadRequestException(
                    "Họ tên không được để trống"
            );
        }

        if (user.getEmail() == null
                || user.getEmail().isBlank()) {

            throw new BadRequestException(
                    "Email không được để trống"
            );
        }

        if (
                !existingUser.getEmail().equals(user.getEmail())
                        && userRepository.existsByEmail(user.getEmail())
        ) {

            throw new ConflictException(
                    "Email đã tồn tại: " + user.getEmail()
            );
        }

        existingUser.setFullName(
                user.getFullName()
        );

        existingUser.setEmail(
                user.getEmail()
        );

        if (
                user.getPassword() != null
                        && !user.getPassword().isBlank()
        ) {

            existingUser.setPassword(
                    passwordEncoder.encode(
                            user.getPassword()
                    )
            );
        }

        existingUser.setActive(
                user.getActive()
        );

        existingUser.setRole(
                user.getRole()
        );

        return userRepository.save(
                existingUser
        );
    }

    public void deleteUser(Long id) {

        User user = userRepository
                .findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Không tìm thấy người dùng có id: " + id
                        )
                );

        userRepository.delete(user);
    }
}