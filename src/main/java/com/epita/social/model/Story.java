package com.epita.social.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.hibernate.annotations.JdbcType;
import org.hibernate.type.descriptor.jdbc.VarcharJdbcType;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Story {

    @Id
    @GeneratedValue
    @JdbcType(VarcharJdbcType.class)
    private UUID storyId;
    private String caption;
    private String media;
    @JdbcType(VarcharJdbcType.class)
    private UUID profileId;
    @JdbcType(VarcharJdbcType.class)
    private UUID liked;
    @JdbcType(VarcharJdbcType.class)
    private UUID commented;
    private LocalDateTime createdAt = LocalDateTime.now();
    private String author;

}
