package io.github.pedroermarinho.comandalivreapi.shared.core.presenter.controllers

import io.github.pedroermarinho.comandalivreapi.shared.core.domain.annotations.RequirePermissions
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.dtos.feature.FeatureFilterDTO
import io.github.pedroermarinho.shared.dtos.page.PageDTO
import io.github.pedroermarinho.shared.dtos.page.PageableDTO
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.enums.FeatureEnum
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.response.feature.FeatureResponse
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.usecases.feature.SearchFeatureUseCase
import io.github.pedroermarinho.comandalivreapi.shared.core.infra.mappers.FeatureMapper
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
@RequestMapping("/api/v1/shared/features")
@Tag(name = "Feature", description = "Gerenciamento de feature do sistema")
class FeatureController(
    private val searchFeatureUseCase: SearchFeatureUseCase,
    private val featureMapper: FeatureMapper,
) {
    @RequirePermissions(
        any = [FeatureEnum.ADMIN_DASHBOARD_ACCESS],
    )
    @Operation(
        summary = "Buscar todas as feature com paginação",
        description = "Buscar todas as feature com paginação",
    )
    @GetMapping
    fun getAll(
        @RequestParam(defaultValue = "0") pageNumber: Int,
        @RequestParam(defaultValue = "10") pageSize: Int,
        @RequestParam(required = false) sort: List<String>?,
        @RequestParam(required = false) direction: String?,
        @RequestParam(required = false) group: UUID?,
        @RequestParam(required = false) excludeGroup: UUID?,
    ): ResponseEntity<PageDTO<FeatureResponse>> {
        val pageable =
            PageableDTO(
                sort = sort,
                direction = direction,
                pageNumber = pageNumber,
                pageSize = pageSize,
            )
        val filter = FeatureFilterDTO(group = group, excludeGroup = excludeGroup)
        val result = searchFeatureUseCase.getAll(pageable, filter).getOrThrow()
        return ResponseEntity.ok(result.map { featureMapper.toResponse(it) })
    }
}
