package ru.wutiarn.tg.openvote.config

import org.redisson.Redisson
import org.redisson.api.RedissonClient
import org.redisson.config.Config
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
open class RedisConfig(@Value("\${redis.url}") val redisUrl: String) {
    @Bean
    open fun redisson(): RedissonClient {
        val config = Config()
        config.useSingleServer().setAddress(redisUrl)
        return Redisson.create(config)
    }
}