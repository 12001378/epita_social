package com.epita.social.repo;

import com.epita.social.model.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface PostRepo extends JpaRepository<Post, UUID> {

    @Query("SELECT p FROM Post p WHERE p.profile_id IN :followingProfileIds AND p.profile_id <> :currentProfileId ORDER BY p.createdAt DESC")
    List<Post> findFeedPosts(@Param("followingProfileIds") List<UUID> followingProfileIds, @Param("currentProfileId") UUID currentProfileId);


}
