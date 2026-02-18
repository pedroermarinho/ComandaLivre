package io.github.pedroermarinho.user.presenter.grpcs

import io.github.pedroermarinho.shared.DispatcherProvider
import io.github.pedroermarinho.shared.dtos.user.UserAuthDTO
import io.github.pedroermarinho.shared.grpc.messages.v1.CreateUserRequest
import io.github.pedroermarinho.shared.grpc.messages.v1.UserResponse
import io.github.pedroermarinho.user.domain.usecases.user.CreateUserUseCase
import org.springframework.stereotype.Controller
import io.github.pedroermarinho.shared.grpc.user.v1.UserServiceGrpcKt
import io.github.pedroermarinho.user.domain.usecases.user.SearchUserUseCase
import kotlinx.coroutines.withContext

@Controller
class UserGrpc (
    private val createUserUseCase: CreateUserUseCase,
    private val searchUserUseCase: SearchUserUseCase,
    private val dispatchers: DispatcherProvider
): UserServiceGrpcKt.UserServiceCoroutineImplBase() {


    override suspend fun createUser(request: CreateUserRequest): UserResponse {
        return withContext(dispatchers.io) {

            val user = searchUserUseCase.getBySub(request.sub).getOrNull()

            if (user != null){
              return@withContext  UserResponse.newBuilder()
                    .setId(user.id.publicId.toString())
                    .setEmail(user.email)
                    .setName(user.name)
                    .build()
            };

            val createdUser = createUserUseCase.execute(UserAuthDTO(
                sub = request.sub,
                email = request.email,
                name = request.name,
                picture = if (request.hasPicture()) request.picture else null
            )).getOrThrow()

            val newUser = searchUserUseCase.getById(createdUser.internalId).getOrThrow()

            UserResponse.newBuilder()
                .setId(newUser.id.publicId.toString())
                .setEmail(newUser.email)
                .setName(newUser.name)
                .build()
        }
    }
}
