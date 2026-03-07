package com.pornput.rbactemplate.unit.controllers

import com.pornput.rbactemplate.controllers.PingController
import spock.lang.Specification

class PingControllerTest extends Specification {

    def pingController = new PingController()

    def "ping should return pong"() {
        when:
        def result = pingController.ping()

        then:
        result == "pong"
    }
}
