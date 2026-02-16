package io.github.pedroermarinho.gateway.services

import io.github.pedroermarinho.gateway.dtos.UserAuthDTO
import io.github.pedroermarinho.shared.exceptions.NotFoundException
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.stereotype.Service

@Service
class CurrentUserService{
     fun getLoggedUser(): Result<UserAuthDTO> =
        runCatching {
            val principal = SecurityContextHolder.getContext().authentication.principal

            return if (principal is Jwt) {
                Result.success(convertToUserDTO(principal))
            } else {
                Result.failure(NotFoundException("Usuário não encontrado"))
            }
        }

    private fun convertToUserDTO(userInfo: Jwt): UserAuthDTO {
        val pictures = userInfo.claims["pictures"] as String?
        return UserAuthDTO(
            sub = userInfo.claims["sub"] as String,
            email = userInfo.claims["email"] as String,
            emailVerified = userInfo.claims["email_verified"] as Boolean,
            name = userInfo.claims["name"] as String,
            picture = pictures?.takeIf { it.isNotBlank() },
        )
    }
}
