package io.github.pedroermarinho.user.domain.services

import io.github.pedroermarinho.shared.dtos.user.UserAuthDTO
import io.github.pedroermarinho.shared.exceptions.NotFoundException
import io.github.pedroermarinho.shared.exceptions.NotImplementedException
import org.springframework.stereotype.Service
import org.springframework.web.client.HttpServerErrorException

@Service
class CurrentUserService {
    fun getLoggedUser(): Result<UserAuthDTO>{
        // TODO: implementação necessaria
        return Result.failure(NotImplementedException("Ainda precisa ser desenvolvido"))
    }
}
