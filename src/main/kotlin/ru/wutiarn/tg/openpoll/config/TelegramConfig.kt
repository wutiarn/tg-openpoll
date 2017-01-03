package ru.wutiarn.tg.openpoll.config

import com.pengrad.telegrambot.TelegramBot
import com.pengrad.telegrambot.TelegramBotAdapter
import okhttp3.OkHttpClient
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.util.concurrent.TimeUnit

@Configuration
open class TelegramConfig(@Value("\${telegram.token}") val telegramToken: String) {
    @Bean
    open fun bot(): TelegramBot {
        val client = OkHttpClient().newBuilder().readTimeout(70, TimeUnit.SECONDS).build()
        val bot = TelegramBotAdapter.buildCustom(telegramToken, client)
        return bot
    }
}