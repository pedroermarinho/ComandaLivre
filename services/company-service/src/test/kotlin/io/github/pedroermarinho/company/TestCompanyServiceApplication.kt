package io.github.pedroermarinho.company

import org.springframework.boot.fromApplication
import org.springframework.boot.with


fun main(args: Array<String>) {
	fromApplication<CompanyServiceApplication>().with(TestcontainersConfiguration::class).run(*args)
}
