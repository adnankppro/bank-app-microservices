package com.adnan.bankapp.profile.mapper;

import com.adnan.bankapp.profile.dto.ProfileResponse;
import com.adnan.bankapp.profile.entity.Profile;
import com.bankapp.common.dto.CreateProfileRequest;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ProfileMapper {

    public Profile toProfile(CreateProfileRequest request);

    public ProfileResponse toResponse(Profile profile);

}
