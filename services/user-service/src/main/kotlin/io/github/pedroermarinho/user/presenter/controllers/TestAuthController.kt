package io.github.pedroermarinho.user.presenter.controllers

import com.nimbusds.jose.JWSAlgorithm
import com.nimbusds.jose.JWSHeader
import com.nimbusds.jose.crypto.MACSigner
import com.nimbusds.jwt.JWTClaimsSet
import com.nimbusds.jwt.SignedJWT
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import java.util.*
import org.springframework.context.annotation.Profile
import org.springframework.web.bind.annotation.*

/**
 * Controller para geração de tokens JWT de teste.
 *
 * **ATENÇÃO**: Este controller é APENAS para testes e desenvolvimento! Ele só está ativo quando o
 * profile 'jmeter' está habilitado.
 *
 * Nunca use este controller em produção!
 */
@Profile("stress")
@RestController
@RequestMapping("/api/v1/test/auth")
@Tag(
        name = "Test Auth",
        description = "Geração de tokens JWT para testes (apenas em profile jmeter)"
)
class TestAuthController {

    // Chave secreta para assinar os tokens (256 bits = 32 bytes)
    // Em um cenário real, isso seria configurado externamente
    private val secretKey = ByteArray(32) { it.toByte() }

    /** Request para geração de token */
    data class TokenRequest(
            val sub: String, // Firebase UID simulado
            val email: String, // Email do usuário
            val name: String, // Nome completo
            val roles: List<String> =
                    listOf( // Roles/grupos do usuário
                            "default_user_cl",
                            "default_user_pd"
                    ),
            val emailVerified: Boolean = true, // Email verificado
            val picture: String? = null // URL da foto (opcional)
    )

    /** Response com o token gerado */
    data class TokenResponse(
            val token: String, // Token JWT
            val expiresIn: Long, // Tempo de expiração em milissegundos
            val tokenType: String = "Bearer"
    )

    /**
     * Gera um token JWT de teste.
     *
     * Este endpoint permite criar tokens JWT válidos para testes de carga sem depender do Firebase
     * Authentication.
     *
     * @param request Dados do usuário para incluir no token
     * @return Token JWT e informações de expiração
     */
    @Operation(
            summary = "Gerar token JWT de teste",
            description =
                    "Gera um token JWT válido para testes. Apenas disponível em profile 'jmeter'."
    )
    @PostMapping("/token")
    fun generateToken(@RequestBody request: TokenRequest): TokenResponse {
        val now = Date()
        val expiresIn = 3600000L // 1 hora em milissegundos
        val expiration = Date(now.time + expiresIn)

        // Criar claims
        val claimsSet =
                JWTClaimsSet.Builder()
                        .subject(request.sub)
                        .claim("email", request.email)
                        .claim("email_verified", request.emailVerified)
                        .claim("name", request.name)
                        .claim("roles", request.roles)
                        .apply { request.picture?.let { claim("pictures", it) } }
                        .issueTime(now)
                        .expirationTime(expiration)
                        .issuer("test-issuer")
                        .build()

        // Criar JWT assinado
        val signedJWT = SignedJWT(JWSHeader(JWSAlgorithm.HS256), claimsSet)

        // Assinar
        val signer = MACSigner(secretKey)
        signedJWT.sign(signer)

        return TokenResponse(token = signedJWT.serialize(), expiresIn = expiresIn)
    }

    /**
     * Gera múltiplos tokens de uma vez.
     *
     * Útil para testes de carga onde você precisa de vários usuários.
     *
     * @param count Quantidade de tokens a gerar
     * @param prefix Prefixo para os usuários (padrão: "test-user")
     * @return Lista de tokens gerados
     */
    @Operation(
            summary = "Gerar múltiplos tokens JWT",
            description =
                    "Gera múltiplos tokens JWT para simular vários usuários em testes de carga."
    )
    @PostMapping("/tokens/batch")
    fun generateBatchTokens(
            @RequestParam(defaultValue = "10") count: Int,
            @RequestParam(defaultValue = "test-user") prefix: String
    ): List<TokenResponse> {
        return (1..count).map { i ->
            val request =
                    TokenRequest(
                            sub = "$prefix-$i",
                            email = "$prefix$i@test.com",
                            name = "Test User $i"
                    )
            generateToken(request)
        }
    }

    /** Endpoint de health check para verificar se o controller está ativo. */
    @Operation(
            summary = "Health check",
            description =
                    "Verifica se o controller de teste está ativo (profile jmeter habilitado)."
    )
    @GetMapping("/health")
    fun health(): Map<String, Any> {
        return mapOf(
                "status" to "ok",
                "profile" to "jmeter",
                "message" to
                        "Test auth controller is active. Use POST /token to generate test JWT tokens."
        )
    }
}
