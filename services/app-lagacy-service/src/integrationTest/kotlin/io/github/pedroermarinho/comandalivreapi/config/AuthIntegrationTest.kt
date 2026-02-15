package io.github.pedroermarinho.comandalivreapi.config

import io.github.pedroermarinho.comandalivreapi.shared.core.domain.usecases.featureflag.StatusFeatureFlagUseCase
import org.mockito.kotlin.any
import org.mockito.kotlin.whenever
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.security.oauth2.jwt.JwtDecoder
import org.springframework.test.context.bean.override.mockito.MockitoBean
import java.time.Instant
import java.util.*

abstract class AuthIntegrationTest : AbstractIntegrationTest() {
    @MockitoBean
    private lateinit var statusFeatureFlagUseCase: StatusFeatureFlagUseCase

    abstract val jwtDecoder: JwtDecoder

    fun setupJwt(
        sub: String = "auth-sub-${UUID.randomUUID()}",
        name: String = "Test User",
        email: String = "test.user@example.com",
        emailVerified: Boolean = true,
        featureFlag: Boolean = false,
    ) {
        super.setup()
        val jwt =
            Jwt(
                "mock-token",
                Instant.now(),
                Instant.now().plusSeconds(3600),
                mapOf("alg" to "none"),
                mapOf("sub" to sub, "name" to name, "email" to email, "email_verified" to emailVerified),
            )
        whenever(jwtDecoder.decode(any())).thenReturn(jwt)
        whenever(statusFeatureFlagUseCase.isEnabled(any())).thenReturn(featureFlag)
    }
}
