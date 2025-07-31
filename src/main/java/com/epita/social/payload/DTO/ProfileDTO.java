package com.epita.social.payload.DTO;

import com.epita.social.model.User;
import jakarta.persistence.Column;
import jakarta.persistence.Lob;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.UUID;

@Data
@RequiredArgsConstructor
public class ProfileDTO {

    private UUID profileId;
    private byte[] profile_picture;
    private User user;

}
