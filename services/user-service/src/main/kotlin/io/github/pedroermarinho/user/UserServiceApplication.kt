package io.github.pedroermarinho.user

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.runApplication
import org.springframework.scheduling.annotation.EnableAsync
import org.springframework.transaction.annotation.EnableTransactionManagement

@SpringBootApplication
@EnableTransactionManagement
@EnableAsync
@ConfigurationPropertiesScan
class UserServiceApplication

fun main(args: Array<String>) {
	runApplication<UserServiceApplication>(*args)
}
