package com.pornput.rbactemplate.mapper;

import com.pornput.rbactemplate.model.rbac.response.RegisterResponse;
import com.pornput.rbactemplate.model.rbac.User;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface UserMapper {
    UserMapper MAPPER = Mappers.getMapper(UserMapper.class);

    RegisterResponse mapRegisterResponse(User user);
}
