package com.app.auth.auth_service.service;

import com.app.auth.auth_service.exception.UserNotFoundException;
import com.app.auth.auth_service.model.User;
import com.app.auth.auth_service.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public User getById(Long id) {
        return userRepository.findById(id).orElseThrow(() -> new UserNotFoundException("User not found with : " + id));
    }

}
