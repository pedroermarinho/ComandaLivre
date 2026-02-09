package io.github.pedroermarinho.comandalivre

import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import

@Import(TestcontainersConfiguration::class)
@SpringBootTest
class ComandalivreServiceApplicationTests {

	@Test
	fun contextLoads() {
	}

}
