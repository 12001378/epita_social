package com.epita.social.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.hibernate.annotations.JdbcType;
import org.hibernate.type.descriptor.jdbc.VarcharJdbcType;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@Entity
@Data
@RequiredArgsConstructor
public class Profile {

    @Id
    @GeneratedValue
    @JdbcType(VarcharJdbcType.class)
    private UUID profile_id;
    @OneToOne
    @JdbcType(VarcharJdbcType.class)
    @JoinColumn(nullable = false)
    private User user;

    @ManyToMany
    @JoinTable(
            name = "profile_followers",
            joinColumns = @JoinColumn(name = "profile_id"),
            inverseJoinColumns = @JoinColumn(name = "followers_id")
    )
    private Set<User> followers;
    @ManyToMany
    @JoinTable(
            name = "profile_following",
            joinColumns = @JoinColumn(name = "profile_id"),
            inverseJoinColumns = @JoinColumn(name = "following_id")
    )
    private Set<User> following;

    private String profile_bio;
    @Lob
    @Column(name = "profile_picture", columnDefinition = "LONGBLOB")
    private byte[] profile_picture;
    @Lob
    @Column(name = "profile_background", columnDefinition = "LONGBLOB")
    private byte[] profile_background;

    @OneToMany
    @JdbcType(VarcharJdbcType.class)
    private Set<Post> posts;
    @OneToMany
    @JdbcType(VarcharJdbcType.class)
    private Set<Post> savedPosts;
    @OneToMany
    @JdbcType(VarcharJdbcType.class)
    private Set<Post> taggedPosts;
    @OneToMany
    @JdbcType(VarcharJdbcType.class)
    private Set<User> follow_requests;
    private boolean accountType = false;
    private String username;
    @OneToMany
    @JdbcType(VarcharJdbcType.class)
    private List<Story> story;


}
