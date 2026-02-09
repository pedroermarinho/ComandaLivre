package io.github.pedroermarinho.prumodigital

import org.springframework.boot.fromApplication
import org.springframework.boot.with


fun main(args: Array<String>) {
	fromApplication<PrumodigitalServiceApplication>().with(TestcontainersConfiguration::class).run(*args)
}
