package io.github.pedroermarinho.user.domain.valueobject

import io.github.pedroermarinho.shared.exceptions.BusinessLogicException

@JvmInline
value class UserName private constructor(
    val value: String,
) {
    companion object {
        operator fun invoke(value: String): UserName {
            if (value.isBlank()) {
                throw BusinessLogicException("Nome de usuário não pode ser vazio")
            }
            if (value.length < 3) {
                throw BusinessLogicException("Nome de usuário deve ter no mínimo 3 caracteres")
            }
            return UserName(value)
        }

        fun restore(persistedValue: String): UserName = UserName(persistedValue)
    }
}
