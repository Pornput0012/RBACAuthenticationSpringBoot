package com.pornput.rbactemplate.controllers

import com.pornput.rbactemplate.jwt.JwtPrincipal
import spock.lang.Specification

class UserControllerTest extends Specification {

    def userController = new UserController()

    def "profile should return greeting message with username"() {
        given:
        def principal = Mock(JwtPrincipal) {
            getUsername() >> "Pornput"
        }

        when:
        def result = userController.profile(principal)

        then:
        result == "Hello Pornput"
    }

    def "dashboard should return admin dashboard message"() {
        when:
        def result = userController.dashboard()

        then:
        result == "Admin dashboard"
    }
}
