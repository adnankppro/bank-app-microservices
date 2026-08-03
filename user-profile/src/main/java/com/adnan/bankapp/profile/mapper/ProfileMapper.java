package com.adnan.bankapp.profile.mapper;

import com.adnan.bankapp.profile.dto.ProfileResponse;
import com.adnan.bankapp.profile.dto.UpdateProfileRequest;
import com.adnan.bankapp.profile.entity.Profile;
import com.bankapp.common.dto.CreateProfileRequest;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring")
public interface ProfileMapper {

    public Profile toProfile(CreateProfileRequest request);

    public ProfileResponse toResponse(Profile profile);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    public void updateUser(@MappingTarget Profile profile, UpdateProfileRequest request);

}
