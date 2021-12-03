package com.payprovider.withdrawal.mapper;

import com.payprovider.withdrawal.dto.UserDto;
import com.payprovider.withdrawal.model.User;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserDto userToUserDto(User user);

    List<UserDto> userToUserDtoList(List<User> user);
}
