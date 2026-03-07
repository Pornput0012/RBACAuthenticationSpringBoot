package com.pornput.rbactemplate.mapper;

import com.pornput.rbactemplate.model.rbac.response.RegisterResponse;
import com.pornput.rbactemplate.entities.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {
    @Mapping(source = "id", target = "userId")
    RegisterResponse mapRegisterResponse(User user);
}
