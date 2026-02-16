package io.github.pedroermarinho.user.presenter.controllers

import io.github.pedroermarinho.user.domain.annotations.RequirePermissions
import io.github.pedroermarinho.shared.dtos.page.PageDTO
import io.github.pedroermarinho.shared.dtos.page.PageableDTO
import io.github.pedroermarinho.user.domain.dtos.user.UserFilterDTO
import io.github.pedroermarinho.user.domain.dtos.user.UserRegistrationsPerDayDTO
import io.github.pedroermarinho.user.domain.enums.FeatureEnum
import io.github.pedroermarinho.user.domain.forms.user.UpdateUserForm
import io.github.pedroermarinho.user.domain.response.user.UserResponse
import io.github.pedroermarinho.user.domain.response.user.UserSummaryResponse
import io.github.pedroermarinho.user.domain.usecases.user.CurrentUserUseCase
import io.github.pedroermarinho.user.domain.usecases.user.SearchUserUseCase
import io.github.pedroermarinho.user.domain.usecases.user.UpdateUserUseCase
import io.github.pedroermarinho.user.infra.mappers.UserMapper
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
@RequestMapping("/api/v1/shared/users")
@Tag(name = "Usuários", description = "Gerenciamento de usuários do sistema")
class UserController(
    private val currentUserUseCase: CurrentUserUseCase,
    private val searchUserUseCase: SearchUserUseCase,
    private val updateUserUseCase: UpdateUserUseCase,
    private val userMapper: UserMapper,
) {
    @RequirePermissions(
        any = [FeatureEnum.ADMIN_DASHBOARD_ACCESS],
    )
    @Operation(
        summary = "Buscar todos os usuário com paginação",
        description = "Buscar todos os usuário com paginação",
    )
    @GetMapping
    fun getAll(
        @RequestParam(defaultValue = "0") pageNumber: Int,
        @RequestParam(defaultValue = "10") pageSize: Int,
        @RequestParam(required = false) sort: List<String>? = listOf("name"),
        @RequestParam(required = false) direction: String?,
        @RequestParam(required = false) search: String?,
        @RequestParam(required = false) group: UUID?,
        @RequestParam(required = false) excludeGroup: UUID?,
    ): ResponseEntity<PageDTO<UserResponse>> {
        val pageable =
            PageableDTO(
                sort = sort,
                direction = direction,
                pageNumber = pageNumber,
                pageSize = pageSize,
                search = search,
            )
        val filter = UserFilterDTO(group = group, excludeGroup = excludeGroup)
        val result = searchUserUseCase.getAll(pageable, filter).getOrThrow()
        return ResponseEntity.ok(result.map { userMapper.toResponse(it) })
    }

    @RequirePermissions(
        any = [FeatureEnum.ADMIN_DASHBOARD_ACCESS],
    )
    @Operation(
        summary = "Buscar a quantidade de usuários ativos no sistema",
        description = "Buscar a quantidade de usuários ativos no sistema",
    )
    @GetMapping("/count")
    fun count(): ResponseEntity<Long> {
        val result = searchUserUseCase.count().getOrThrow()
        return ResponseEntity.ok(result)
    }

    @RequirePermissions(
        any = [FeatureEnum.ADMIN_DASHBOARD_ACCESS],
    )
    @Operation(
        summary = "Buscar registros de usuários nos últimos dias",
        description = "Retorna a quantidade de usuários cadastrados por dia em um intervalo de dias especificado",
    )
    @GetMapping("/registrations")
    fun getUserRegistrationsLastDays(
        @RequestParam(defaultValue = "7") days: Long,
    ): ResponseEntity<List<UserRegistrationsPerDayDTO>> {
        val result = searchUserUseCase.getUserRegistrationsLastDays(days).getOrThrow()
        return ResponseEntity.ok(result)
    }

    @Operation(summary = "Autenticação de usuário", description = "Autenticação de usuário no sistema")
    @PostMapping("/auth")
    fun auth(): ResponseEntity<UserResponse> {
        val result = currentUserUseCase.getOrCreate().getOrThrow()
        return ResponseEntity.ok(userMapper.toResponse(result))
    }

    @Operation(summary = "Buscar perfil por ID", description = "Buscar perfil de usuário por ID")
    @GetMapping("/{id}")
    fun getById(
        @PathVariable id: UUID,
    ): ResponseEntity<UserSummaryResponse> {
        val result = searchUserUseCase.getById(id).getOrThrow()
        return ResponseEntity.ok(userMapper.toSummaryResponse(result))
    }

    @Operation(summary = "Buscar perfil por email", description = "Buscar perfil de usuário por email")
    @GetMapping("/email/{email}")
    fun getByEmail(
        @PathVariable email: String,
    ): ResponseEntity<UserSummaryResponse> {
        val result = searchUserUseCase.getByEmail(email).getOrThrow()
        return ResponseEntity.ok(userMapper.toSummaryResponse(result))
    }

    @Operation(summary = "Atualizar perfil do usuário logado", description = "Atualizar perfil do usuário logado")
    @PatchMapping
    fun update(
        @Valid @RequestBody form: UpdateUserForm,
    ): ResponseEntity<Unit> {
        val result = updateUserUseCase.execute(form).getOrThrow()
        return ResponseEntity.ok(result)
    }
}
