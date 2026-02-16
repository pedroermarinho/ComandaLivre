package io.github.pedroermarinho.user.presenter.controllers

import io.github.pedroermarinho.user.domain.annotations.RequirePermissions
import io.github.pedroermarinho.shared.dtos.page.PageDTO
import io.github.pedroermarinho.shared.dtos.page.PageableDTO
import io.github.pedroermarinho.user.domain.enums.FeatureEnum
import io.github.pedroermarinho.user.domain.forms.user.FeatureGroupForm
import io.github.pedroermarinho.user.domain.request.user.AssignUserToGroupRequest
import io.github.pedroermarinho.user.domain.response.feature.FeatureResponse
import io.github.pedroermarinho.user.domain.response.group.GroupResponse
import io.github.pedroermarinho.user.domain.usecases.group.*
import io.github.pedroermarinho.user.infra.mappers.FeatureMapper
import io.github.pedroermarinho.user.infra.mappers.GroupMapper
import io.github.pedroermarinho.user.infra.mappers.UserMapper
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
@RequestMapping("/api/v1/shared/groups")
@Tag(name = "Grupos", description = "Gerenciamento de grupos de acesso")
class GroupController(
    private val addFeatureToGroupUseCase: AddFeatureToGroupUseCase,
    private val removeFeatureFromGroupUseCase: RemoveFeatureFromGroupUseCase,
    private val searchGroupPermissionUseCase: SearchGroupPermissionUseCase,
    private val createGroupUseCase: CreateGroupUseCase,
    private val searchGroupUseCase: SearchGroupUseCase,
    private val addUserToGroupUseCase: AddUserToGroupUseCase,
    private val searchUserGroupUseCase: SearchUserGroupUseCase,
    private val removeUserFromGroupUseCase: RemoveUserFromGroupUseCase,
    private val groupMapper: GroupMapper,
    private val featureMapper: FeatureMapper,
    private val userMapper: UserMapper,
) {
    @RequirePermissions(
        any = [FeatureEnum.ADMIN_DASHBOARD_ACCESS],
    )
    @Operation(
        summary = "Buscar todas os grupos com paginação",
        description = "Buscar todas os grupo com paginação",
    )
    @GetMapping
    fun getAll(
        @RequestParam(defaultValue = "0") pageNumber: Int,
        @RequestParam(defaultValue = "10") pageSize: Int,
        @RequestParam(required = false) sort: List<String>?,
        @RequestParam(required = false) direction: String?,
    ): ResponseEntity<PageDTO<GroupResponse>> {
        val pageable =
            PageableDTO(
                sort = sort,
                direction = direction,
                pageNumber = pageNumber,
                pageSize = pageSize,
            )
        val result = searchGroupUseCase.getAll(pageable).getOrThrow()
        return ResponseEntity.ok(result.map { groupMapper.toResponse(it) })
    }

    @RequirePermissions(
        all = [FeatureEnum.USER_ROLE_MANAGEMENT],
        any = [FeatureEnum.ADMIN_DASHBOARD_ACCESS],
    )
    @PostMapping
    fun create(
        @RequestBody @Valid form: FeatureGroupForm,
    ): ResponseEntity<Unit> {
        createGroupUseCase.execute(form).getOrThrow()
        return ResponseEntity.status(HttpStatus.CREATED).build()
    }

    @RequirePermissions(
        any = [FeatureEnum.ADMIN_DASHBOARD_ACCESS],
    )
    @GetMapping("/{groupId}/features")
    fun getGroupFeatures(
        @PathVariable groupId: UUID,
        @RequestParam(defaultValue = "0") pageNumber: Int,
        @RequestParam(defaultValue = "10") pageSize: Int,
        @RequestParam(required = false) sort: List<String>?,
        @RequestParam(required = false) direction: String?,
    ): ResponseEntity<PageDTO<FeatureResponse>> {
        val pageable =
            PageableDTO(
                sort = sort,
                direction = direction,
                pageNumber = pageNumber,
                pageSize = pageSize,
            )
        val result =
            searchGroupPermissionUseCase
                .getAll(groupId, pageable)
                .getOrThrow()
        return ResponseEntity.ok(result.map { featureMapper.toResponse(it) })
    }

    @RequirePermissions(
        all = [FeatureEnum.USER_ROLE_MANAGEMENT],
        any = [FeatureEnum.ADMIN_DASHBOARD_ACCESS],
    )
    @PostMapping("/{groupId}/features/{featureId}")
    fun addFeatureToGroup(
        @PathVariable groupId: UUID,
        @PathVariable featureId: UUID,
    ): ResponseEntity<Unit> {
        addFeatureToGroupUseCase
            .execute(
                groupPublicId = groupId,
                featurePublicId = featureId,
            ).getOrThrow()
        return ResponseEntity.status(HttpStatus.CREATED).build()
    }

    @RequirePermissions(
        all = [FeatureEnum.USER_ROLE_MANAGEMENT],
        any = [FeatureEnum.ADMIN_DASHBOARD_ACCESS],
    )
    @DeleteMapping("/{groupId}/features/{featureId}")
    fun removeFeatureFromGroup(
        @PathVariable groupId: UUID,
        @PathVariable featureId: UUID,
    ): ResponseEntity<Unit> {
        removeFeatureFromGroupUseCase.execute(groupId, featureId).getOrThrow()
        return ResponseEntity.noContent().build()
    }

    @RequirePermissions(
        all = [FeatureEnum.USER_ROLE_MANAGEMENT],
        any = [FeatureEnum.ADMIN_DASHBOARD_ACCESS],
    )
    @PostMapping("/{groupId}/users/{userId}")
    fun addUserToGroup(
        @PathVariable groupId: UUID,
        @PathVariable userId: UUID,
        @RequestBody @Valid form: AssignUserToGroupRequest?,
    ): ResponseEntity<Unit> {
        addUserToGroupUseCase.execute(userId = userId, groupId = groupId, form = form).getOrThrow()
        return ResponseEntity.status(HttpStatus.CREATED).build()
    }

    @RequirePermissions(
        all = [FeatureEnum.USER_ROLE_MANAGEMENT],
        any = [FeatureEnum.ADMIN_DASHBOARD_ACCESS],
    )
    @DeleteMapping("/{groupId}/users/{userId}")
    fun removeUserFromGroup(
        @PathVariable groupId: UUID,
        @PathVariable userId: UUID,
    ): ResponseEntity<Unit> {
        removeUserFromGroupUseCase.execute(groupId = groupId, userId = userId).getOrThrow()
        return ResponseEntity.noContent().build()
    }
}
