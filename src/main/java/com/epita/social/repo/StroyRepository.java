package com.epita.social.repo;

import com.epita.social.model.Story;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface StroyRepository extends JpaRepository<Story, UUID> {

    List<Story> findByProfileId(UUID profileId);


}
