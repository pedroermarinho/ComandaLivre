package io.github.pedroermarinho.comandalivreapi.config

import com.redis.testcontainers.RedisContainer
import io.github.pedroermarinho.comandalivreapi.annotations.IntegrationTest
import io.restassured.RestAssured
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Tag
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.testcontainers.containers.GenericContainer
import org.testcontainers.containers.MinIOContainer
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.containers.wait.strategy.Wait
import org.testcontainers.utility.DockerImageName

@Tag("integration")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@IntegrationTest
abstract class AbstractIntegrationTest {
    @LocalServerPort
    var port: Int = 0

    @BeforeEach
    fun setup() {
        RestAssured.port = port
    }

    companion object {
        @JvmStatic
        private val postgresContainer =
            PostgreSQLContainer(DockerImageName.parse("postgres:17-alpine3.21"))
                .waitingFor(Wait.forListeningPort())
                .apply { start() }

        @JvmStatic
        private val minioContainer =
            MinIOContainer(DockerImageName.parse("minio/minio"))
                .waitingFor(Wait.forHttp("/minio/health/live").forStatusCode(200))
                .apply { start() }

        @JvmStatic
        private val mailhogContainer =
            GenericContainer(DockerImageName.parse("mailhog/mailhog"))
                .withExposedPorts(1025, 8025)
                .waitingFor(Wait.forListeningPort())
                .apply { start() }

        @JvmStatic
        private val redisContainer =
            RedisContainer(DockerImageName.parse("redis"))
                .waitingFor(Wait.forListeningPort())
                .apply { start() }

        @JvmStatic
        @DynamicPropertySource
        fun properties(registry: DynamicPropertyRegistry) {
            registry.add("spring.datasource.url", postgresContainer::getJdbcUrl)
            registry.add("spring.datasource.username", postgresContainer::getUsername)
            registry.add("spring.datasource.password", postgresContainer::getPassword)
            registry.add("spring.flyway.url", postgresContainer::getJdbcUrl)
            registry.add("spring.flyway.user", postgresContainer::getUsername)
            registry.add("spring.flyway.password", postgresContainer::getPassword)

            // Configuração do pool de conexões para evitar "too many clients"
            registry.add("spring.datasource.hikari.maximum-pool-size") { "5" }
            registry.add("spring.datasource.hikari.minimum-idle") { "2" }
            registry.add("spring.datasource.hikari.connection-timeout") { "30000" }
            registry.add("spring.datasource.hikari.idle-timeout") { "600000" }
            registry.add("spring.datasource.hikari.max-lifetime") { "1800000" }

            registry.add("application.aws.s3.endpoint", minioContainer::getS3URL)
            registry.add("application.aws.s3.access-key", { "minioadmin" })
            registry.add("application.aws.s3.secret-key", { "minioadmin" })
            registry.add("application.aws.s3.bucket-name", { "test-bucket" })

            registry.add("spring.data.redis.host", redisContainer::getHost)
            registry.add("spring.data.redis.port", redisContainer::getFirstMappedPort)

            registry.add("spring.mail.host", mailhogContainer::getHost)
            registry.add("spring.mail.port", mailhogContainer::getFirstMappedPort)
        }
    }
}
