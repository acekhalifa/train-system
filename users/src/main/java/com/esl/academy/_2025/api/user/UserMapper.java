package com.esl.academy._2025.api.user;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "dateCreated", source = "dateCreated")
    UserDto.UserResponse toResponse(User user);

    List<UserDto.UserResponse> toResponseList(List<User> users);
}
