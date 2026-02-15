package io.github.pedroermarinho.gateway

import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import java.time.LocalDateTime

@Component
@EnableScheduling
class LogGeneratorTest {

    private val logger = LoggerFactory.getLogger(LogGeneratorTest::class.java)
    private var counter = 1

    // Executa a cada 1000 milissegundos (1 segundo)
//    @Scheduled(fixedRate = 1000)
//    fun generateHeartbeatLog() {
//        logger.info("⏳ [Heartbeat OTLP] Gerando log de teste automático #$counter - ${LocalDateTime.now()}")
//        counter++
//    }
}