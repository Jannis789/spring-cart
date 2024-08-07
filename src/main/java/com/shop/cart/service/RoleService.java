package com.shop.cart.service;

import com.shop.cart.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
@Service
public class RoleService {

    @Autowired
    private RoleRepository roleRepository;
}

