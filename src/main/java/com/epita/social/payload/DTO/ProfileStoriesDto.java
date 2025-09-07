package com.epita.social.payload.DTO;

import com.epita.social.model.Story;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@RequiredArgsConstructor
public class ProfileStoriesDto {
    private UUID profileId;
    private String username;
    private List<Story> stories;

    public ProfileStoriesDto(UUID profileId, String username, List<Story> collect) {
        this.profileId = profileId;
        this.username = username;
        this.stories = collect;
    }
}
