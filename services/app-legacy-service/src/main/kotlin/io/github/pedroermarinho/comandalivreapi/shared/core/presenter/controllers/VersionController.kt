package io.github.pedroermarinho.comandalivreapi.shared.core.presenter.controllers

import io.github.pedroermarinho.comandalivreapi.shared.core.domain.enums.PlatformEnum
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.forms.version.VersionForm
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.response.version.VersionResponse
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.usecases.version.CreateVersionUseCase
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.usecases.version.GetLatestVersionUseCase
import io.github.pedroermarinho.comandalivreapi.shared.core.infra.mappers.VersionMapper
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.servlet.support.ServletUriComponentsBuilder
import java.net.URI

@RestController
@RequestMapping("/api/v1/shared/app-versions")
class VersionController(
    private val getLatestVersionUseCase: GetLatestVersionUseCase,
    private val createVersionUseCase: CreateVersionUseCase,
    private val versionMapper: VersionMapper,
) {
    @GetMapping("/{platform}")
    fun getLatestVersion(
        @PathVariable platform: PlatformEnum,
    ): ResponseEntity<VersionResponse> {
        val result = getLatestVersionUseCase.execute(platform).getOrThrow()
        return ResponseEntity.ok(versionMapper.toResponse(result))
    }

    @PostMapping
    fun create(
        @Valid @RequestBody form: VersionForm,
    ): ResponseEntity<Unit> {
        createVersionUseCase.execute(form).getOrThrow()
        val location: URI =
            ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{platform}")
                .buildAndExpand(form.platform)
                .toUri()
        return ResponseEntity.created(location).build()
    }
}
