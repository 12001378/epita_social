package com.epita.social.mapper;

import com.epita.social.model.Profile;
import com.epita.social.payload.DTO.ProfileDTO;

public class profileMapper{

    public static  ProfileDTO profileToProfileDTO(Profile profile){
        ProfileDTO profileDTO = new ProfileDTO();
        profileDTO.setProfileId(profile.getProfile_id());
        profileDTO.setUser(profile.getUser());
        profileDTO.setProfile_picture(profile.getProfile_picture());
        return profileDTO;
    }

    public static  Profile profileDTOToProfile(ProfileDTO profile){
        Profile profileDTO = new Profile();
        profileDTO.setProfile_id(profile.getProfileId());
        profileDTO.setUser(profile.getUser());
        profileDTO.setProfile_picture(profile.getProfile_picture());
        return profileDTO;
    }


}
