package io.github.pedroermarinho.user.domain.services

import io.github.pedroermarinho.user.domain.dtos.user.UserAuthDTO
import org.springframework.stereotype.Service

@Service
interface CurrentUserService {
    fun getLoggedUser(): Result<UserAuthDTO>
}
