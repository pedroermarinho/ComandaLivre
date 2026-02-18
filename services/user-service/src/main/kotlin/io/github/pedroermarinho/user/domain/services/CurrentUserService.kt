package io.github.pedroermarinho.user.domain.services

import com.github.f4b6a3.uuid.UuidCreator
import io.github.pedroermarinho.shared.dtos.user.UserAuthDTO
import io.github.pedroermarinho.shared.exceptions.NotFoundException
import io.github.pedroermarinho.shared.exceptions.NotImplementedException
import org.springframework.stereotype.Service
import org.springframework.web.client.HttpServerErrorException
import kotlin.uuid.Uuid

@Service
class CurrentUserService {
    fun getLoggedUser(): Result<UserAuthDTO>{
        // TODO: implementação necessaria
        return Result.success(
            UserAuthDTO(
                sub = UuidCreator.getTimeOrderedEpoch().toString(),
                email = "pedroermarinho@gmail.com",
                name = "Pedro Marinho",
                picture = null
            )
        )
    }
}
