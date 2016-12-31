package ru.wutiarn.tg.openvote

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.scheduling.annotation.EnableAsync

@SpringBootApplication
@EnableAsync
open class OpenVoteBotApplication {
    @Autowired
    fun runBot(messageRouter: OpenVoteMessageRouter) {
        messageRouter.run()
    }
}