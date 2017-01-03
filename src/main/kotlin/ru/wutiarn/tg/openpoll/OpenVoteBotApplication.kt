package ru.wutiarn.tg.openpoll

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.scheduling.annotation.EnableAsync

@SpringBootApplication
@EnableAsync
open class OpenPollBotApplication {
    @Autowired
    fun runBot(messageRouter: OpenPollMessageRouter) {
        messageRouter.run()
    }
}