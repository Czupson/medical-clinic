package com.Czupson.medical_clinic.mapper;

import com.Czupson.medical_clinic.dto.user.CreateUserCommand;
import com.Czupson.medical_clinic.dto.user.UpdateUserCommand;
import com.Czupson.medical_clinic.dto.user.UserDto;
import com.Czupson.medical_clinic.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "patient", ignore = true)
    User toUser(CreateUserCommand command);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "patient", ignore = true)
    User toUser(UpdateUserCommand command);

    UserDto toDto(User user);
}