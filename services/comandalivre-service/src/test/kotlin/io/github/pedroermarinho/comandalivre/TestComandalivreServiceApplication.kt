package io.github.pedroermarinho.comandalivre

import org.springframework.boot.fromApplication
import org.springframework.boot.with


fun main(args: Array<String>) {
	fromApplication<ComandalivreServiceApplication>().with(TestcontainersConfiguration::class).run(*args)
}
