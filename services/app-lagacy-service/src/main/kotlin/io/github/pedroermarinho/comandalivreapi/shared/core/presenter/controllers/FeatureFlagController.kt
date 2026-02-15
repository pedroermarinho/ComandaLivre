package io.github.pedroermarinho.comandalivreapi.shared.core.presenter.controllers

import io.github.pedroermarinho.comandalivreapi.shared.core.domain.annotations.RequirePermissions
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.dtos.page.PageDTO
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.dtos.page.PageableDTO
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.enums.FeatureEnum
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.response.featureflag.FeatureFlagResponse
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.usecases.featureflag.SearchFeatureFlagUseCase
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.usecases.featureflag.UpdateFeatureFlagUseCase
import io.github.pedroermarinho.comandalivreapi.shared.core.infra.mappers.FeatureFlagMapper
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
@RequestMapping("/api/v1/shared/feature-flags")
@Tag(name = "Feature Flags", description = "Gerenciamento de feature flags do sistema")
class FeatureFlagController(
    private val searchFeatureFlagUseCase: SearchFeatureFlagUseCase,
    private val updateFeatureFlagUseCase: UpdateFeatureFlagUseCase,
    private val featureFlagMapper: FeatureFlagMapper,
) {
    @RequirePermissions(
        any = [FeatureEnum.ADMIN_DASHBOARD_ACCESS],
    )
    @Operation(
        summary = "Buscar todas as feature flags com paginação",
        description = "Buscar todas as feature flags com paginação",
    )
    @GetMapping
    fun getAll(
        @RequestParam(defaultValue = "0") pageNumber: Int,
        @RequestParam(defaultValue = "10") pageSize: Int,
        @RequestParam(required = false) sort: List<String>?,
        @RequestParam(required = false) direction: String?,
    ): ResponseEntity<PageDTO<FeatureFlagResponse>> {
        val pageable =
            PageableDTO(
                sort = sort,
                direction = direction,
                pageNumber = pageNumber,
                pageSize = pageSize,
            )
        val result = searchFeatureFlagUseCase.getAll(pageable).getOrThrow()
        return ResponseEntity.ok(result.map { featureFlagMapper.toResponse(it) })
    }

    @RequirePermissions(
        any = [FeatureEnum.ADMIN_DASHBOARD_ACCESS],
    )
    @Operation(summary = "Ativar uma feature", description = "Ativar uma feature")
    @PutMapping("/{id}/enable")
    fun enable(
        @PathVariable id: UUID,
    ): ResponseEntity<Unit> {
        val result = updateFeatureFlagUseCase.changeEnabled(id, true).getOrThrow()
        return ResponseEntity.ok(result)
    }

    @RequirePermissions(
        any = [FeatureEnum.ADMIN_DASHBOARD_ACCESS],
    )
    @Operation(summary = "Desativar uma feature", description = "Desativar uma feature")
    @PutMapping("/{publicId}/disable")
    fun disable(
        @PathVariable publicId: UUID,
    ): ResponseEntity<Unit> {
        val result = updateFeatureFlagUseCase.changeEnabled(publicId, false).getOrThrow()
        return ResponseEntity.ok(result)
    }
}
