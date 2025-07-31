package com.epita.social.service;

import com.epita.social.model.User;

import java.util.UUID;

public interface UserService {

    User save(User user) throws Exception;
    boolean checkUserExiststance(String email) throws Exception;
    User findByEmail(String email) throws Exception;
    User findById(UUID id) throws Exception;
}
