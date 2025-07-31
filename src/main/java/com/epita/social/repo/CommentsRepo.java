package com.epita.social.repo;

import com.epita.social.model.Comments;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface CommentsRepo extends JpaRepository<Comments, UUID> {

}
