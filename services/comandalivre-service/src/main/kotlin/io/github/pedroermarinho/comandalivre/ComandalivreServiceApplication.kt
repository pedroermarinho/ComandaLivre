package io.github.pedroermarinho.comandalivre

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class ComandalivreServiceApplication

fun main(args: Array<String>) {
	runApplication<ComandalivreServiceApplication>(*args)
}
