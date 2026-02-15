package io.github.pedroermarinho.comandalivreapi.helpers

import org.springframework.security.oauth2.jwt.JwtDecoder

/**
 * Interface para facilitar a criação de testes com autenticação
 * Fornece métodos convenientes para configuração de JWT
 */
interface AuthTestHelper {
    val jwtDecoder: JwtDecoder

    fun setupJwt(
        sub: String = "",
        name: String = "",
        email: String = "",
        emailVerified: Boolean = true,
        featureFlag: Boolean = false,
    )

    /**
     * Configura JWT com informações de um usuário do TestObjectFactory
     */
    fun setupJwtFromUser(user: TestObjectFactory.UserInfo) {
        setupJwt(sub = user.sub, name = user.name, email = user.email)
    }
}
