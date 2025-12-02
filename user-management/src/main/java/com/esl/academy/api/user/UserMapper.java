package com.esl.academy.api.user;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

import java.util.List;

import static com.esl.academy.api.user.UserDto.*;

@Mapper
public interface UserMapper {
    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    @Mapping(target = "status", constant = "PENDING")
    @Mapping(target = "userId", ignore = true)
    @Mapping(target = "passwordHash", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "modifiedBy", ignore = true)
    @Mapping(target = "firstName", source = "name", qualifiedByName = "extractFirstName")
    @Mapping(target = "lastName", source = "name", qualifiedByName = "extractLastName")
    User toUserEntity(CreateUserInvitationRequest dto);

    UserDto toUserDto(User user);

    List<UserDto> toUserDtoList(List<User> users);
    User toUserEntity(CreateSuperAdminInvitationRequest dto);

    @Named("extractFirstName")
    default String extractFirstName(String fullName) {
        return fullName.trim().split(" ", 2)[0];
    }

    @Named("extractLastName")
    default String extractLastName(String fullName) {
        String[] parts = fullName.trim().split(" ", 2);
        return parts.length > 1 ? parts[1] : "";
    }
}
