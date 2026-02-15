package com.pornput.rbactemplate.unit.services

import com.pornput.rbactemplate.entities.Role
import com.pornput.rbactemplate.entities.User
import com.pornput.rbactemplate.repositories.UserRepository
import com.pornput.rbactemplate.services.CustomUserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import spock.lang.Specification;

class CustomUserDetailsServiceTest extends Specification {
    def userRepsitory = Mock(UserRepository)
    def userService = new CustomUserDetailsService(userRepsitory)

    def "loadUserByUsername should successfully when user exists"() {
        given:
        def username = "Pornput"
        def existsUser = User.builder()
                .username("Pornput")
                .role(new Role(name: "ADMIN"))
                .password("12345678")
                .enabled(true)
                .build()

        1 * userRepsitory.findByUsername(username) >> Optional.of(existsUser)

        when:
        def result = userService.loadUserByUsername(username)

        then:
        verifyAll(result){
            getUsername() == username
            getAuthorities()
                    .getAt(0)
                    .getAuthority() == "ROLE_ADMIN"
            getPassword() == "12345678"
            isAccountNonLocked()
            isEnabled()
            isCredentialsNonExpired()
            isAccountNonExpired()
        }
    }

    def "loadUserByUsername should throw exception when user not exists"() {
        given:
        def username = "Pornput"
        1 * userRepsitory.findByUsername(username) >> Optional.empty()

        when:
        userService.loadUserByUsername(username)

        then:
        def ex = thrown(UsernameNotFoundException)
        ex.getMessage() == "User not found: " + username
    }
}
