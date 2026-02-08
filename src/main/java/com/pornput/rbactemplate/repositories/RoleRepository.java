package com.pornput.rbactemplate.repositories;

import com.pornput.rbactemplate.model.rbac.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(String username);
}
