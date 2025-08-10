package com.epita.social.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.hibernate.annotations.JdbcType;
import org.hibernate.type.descriptor.jdbc.VarcharJdbcType;

import java.time.LocalDateTime;
import java.util.*;

@Entity
@Data
@RequiredArgsConstructor
public class Post {

    @Id
    @GeneratedValue
    @JdbcType(VarcharJdbcType.class)
    private UUID post_id;

    private String caption;

    private String image;

    private String video;

    @ElementCollection
    private List<String> mediaUrls;

    private String location;

    private LocalDateTime createdAt;

    @JdbcType(VarcharJdbcType.class)
    private UUID  profile_id;
    @ManyToMany
    private Set<Comments> comments;
    @ManyToMany
    private Set<User> liked = new HashSet<>();
    private boolean archived = false;

}
