package com.epita.social.payload.DTO;

import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.UUID;

@Data
@RequiredArgsConstructor
public class postDTO {

    private UUID postId;
    private String content;
    private boolean likedByCurrentUser;
}
