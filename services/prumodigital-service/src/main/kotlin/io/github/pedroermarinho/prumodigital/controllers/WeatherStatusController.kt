package io.github.pedroermarinho.prumodigital.controllers

import io.github.pedroermarinho.comandalivreapi.prumodigital.core.domain.response.weatherstatus.WeatherStatusResponse
import io.github.pedroermarinho.comandalivreapi.prumodigital.core.domain.usecases.weatherstatus.SearchWeatherStatusUseCase
import io.github.pedroermarinho.comandalivreapi.prumodigital.core.infra.mappers.WeatherStatusMapper
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/prumodigital/weather-status")
@Tag(name = "Status do Clima", description = "Endpoints para gerenciar os status do clima")
class WeatherStatusController(
    private val searchWeatherStatusUseCase: SearchWeatherStatusUseCase,
    private val weatherStatusMapper: WeatherStatusMapper,
) {
    @GetMapping
    @Operation(summary = "Lista todos os status de clima")
    fun getAll(): ResponseEntity<List<WeatherStatusResponse>> {
        val result = searchWeatherStatusUseCase.getAll().getOrThrow()
        return ResponseEntity.ok(result.map { weatherStatusMapper.toResponse(it) })
    }
}
