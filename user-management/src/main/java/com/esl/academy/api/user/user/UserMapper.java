package com.esl.academy.api.user.user;

import com.esl.academy.api.user.User;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface UserMapper {

    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    UserDto map(User user);

    User map(UserDto dto);
}
