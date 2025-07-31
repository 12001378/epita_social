package com.epita.social.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.hibernate.annotations.JdbcType;
import org.hibernate.type.descriptor.jdbc.VarcharJdbcType;

import java.util.UUID;

@Entity
@Data
@RequiredArgsConstructor
public class Comments {

    @Id
    @GeneratedValue
    @JdbcType(VarcharJdbcType.class)
    private UUID commentId;
    private String comment;
    @JdbcType(VarcharJdbcType.class)
    private UUID  profile_id;
    @JdbcType(VarcharJdbcType.class)
    private UUID post;
    private long comment_likes;
}
