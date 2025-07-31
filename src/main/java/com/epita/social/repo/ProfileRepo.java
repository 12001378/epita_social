package com.epita.social.repo;

import com.epita.social.model.Profile;
import com.epita.social.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Set;
import java.util.UUID;

public interface ProfileRepo extends JpaRepository<Profile, UUID> {

    Profile findByUser(User user) throws Exception;
    List<Profile> findByUsername(String username) throws Exception;
    @Query("SELECT DISTINCT p FROM Profile p WHERE p.username LIKE %:username%")
    public Set<Profile> findByQuery(@Param("username") String username);

}
