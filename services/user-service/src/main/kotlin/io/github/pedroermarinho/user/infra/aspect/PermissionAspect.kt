package io.github.pedroermarinho.user.infra.aspect

import io.github.oshai.kotlinlogging.KotlinLogging
import io.github.pedroermarinho.user.domain.annotations.RequirePermissions
import io.github.pedroermarinho.shared.exceptions.UnauthorizedException
import io.github.pedroermarinho.user.domain.usecases.group.SearchUserGroupUseCase
import io.github.pedroermarinho.user.domain.usecases.user.CurrentUserUseCase
import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.springframework.stereotype.Component

@Aspect
@Component
class PermissionAspect(
    private val currentUserUseCase: CurrentUserUseCase,
    private val searchUserGroupUseCase: SearchUserGroupUseCase,
) {
    private val log = KotlinLogging.logger {}

    @Around("@annotation(requirePermissions)")
    fun checkPermissions(
        joinPoint: ProceedingJoinPoint,
        requirePermissions: RequirePermissions,
    ): Any? {
        val user = currentUserUseCase.getUser().getOrThrow()

        val allPermissions = requirePermissions.all.toList()
        val anyPermissions = requirePermissions.any.toList()

        val hasAll =
            if (allPermissions.isNotEmpty()) {
                searchUserGroupUseCase.hasAllPermissions(user.id.internalId, allPermissions)
            } else {
                true
            }

        val hasAny =
            if (anyPermissions.isNotEmpty()) {
                searchUserGroupUseCase.hasAnyPermission(user.id.internalId, anyPermissions)
            } else {
                true
            }

        val allowed = hasAll && hasAny

        log.info(
            "Usuário {id: ${user.id.internalId}, publicId:${user.id.publicId}, email:${user.email}, sub: ${user.sub} } solicitou acesso a ${joinPoint.signature} | all=$allPermissions any=$anyPermissions => allowed=$allowed",
        )

        if (!allowed) {
            log.warn("Acesso negado para o usuário ${user.id.internalId}: ${requirePermissions.message}")
            val permissions = searchUserGroupUseCase.getFeatureKeysByUserId(user.id.internalId).getOrNull()
            log.warn("Permissões do usuário ${user.id.internalId}: $permissions")
            throw UnauthorizedException(requirePermissions.message)
        }

        return joinPoint.proceed()
    }
}
