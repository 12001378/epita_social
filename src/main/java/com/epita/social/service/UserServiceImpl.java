package com.epita.social.service;

import com.epita.social.exception.GlobleException;
import com.epita.social.exception.UserException;
import com.epita.social.model.Profile;
import com.epita.social.model.User;
import com.epita.social.repo.UserRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final GlobleException globleException;
    private final UserRepo userRepo;

    @Override
    public User save(User user) throws Exception {
        return userRepo.save(user);
    }

    @Override
    public boolean checkUserExiststance(String email) throws UserException {
        User user = userRepo.findByEmail(email);
        return user != null ? true : false;
    }

    @Override
    public User findByEmail(String email) throws UserException {
        User user = userRepo.findByEmail(email);
        if (user == null) {
            throw new UserException("User not found");
        }
        return user;
    }

    @Override
    public User findById(UUID id) throws Exception {
        return userRepo.findById(id).orElseThrow();
    }

}
