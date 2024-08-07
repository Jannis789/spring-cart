package com.shop.cart.service;

import com.shop.cart.model.User;
import com.shop.cart.model.Role;
import com.shop.cart.repository.UserRepository;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.transaction.annotation.Transactional;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;
    public Optional<User> getUserByName(String name) {
        return userRepository.findByUsername(name);
    }

    @Transactional(readOnly = true)
    public User getUserWithRoles(Long userId) {
        User user = userRepository.findById(userId)
                                  .orElseThrow(() -> new EntityNotFoundException("User not found"));
        Hibernate.initialize(user.getRoles()); 
        return user;
    }
    
}
