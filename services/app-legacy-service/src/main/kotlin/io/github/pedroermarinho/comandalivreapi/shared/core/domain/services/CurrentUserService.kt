package io.github.pedroermarinho.comandalivreapi.shared.core.domain.services

import io.github.pedroermarinho.comandalivreapi.shared.core.domain.dtos.user.UserAuthDTO
import org.springframework.stereotype.Service

@Service
interface CurrentUserService {
    fun getLoggedUser(): Result<UserAuthDTO>
}
