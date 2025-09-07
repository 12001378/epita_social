

package com.epita.social.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.hibernate.annotations.JdbcType;
import org.hibernate.type.descriptor.jdbc.VarcharJdbcType;
import jakarta.persistence.Transient;
import java.time.LocalDateTime;
import java.util.*;

@Entity
@Data
@RequiredArgsConstructor
public class Post {
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Post post = (Post) o;
        return Objects.equals(post_id, post.post_id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(post_id);
    }

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
    @ManyToMany(fetch = FetchType.EAGER)
    private Set<User> liked = new HashSet<>();

    private boolean archived = false;
    private String author;

    @Transient
    private boolean likedByCurrentUser = false;
    
        @Transient
        private boolean savedByCurrentUser = false;
}
