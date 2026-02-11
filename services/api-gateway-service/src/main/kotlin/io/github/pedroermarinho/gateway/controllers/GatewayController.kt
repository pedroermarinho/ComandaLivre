package io.github.pedroermarinho.gateway.controllers

import io.github.pedroermarinho.gateway.dtos.LabServiceDTO
import io.github.pedroermarinho.gateway.dtos.ServiceInput
import org.springframework.graphql.data.method.annotation.Argument
import org.springframework.graphql.data.method.annotation.MutationMapping
import org.springframework.graphql.data.method.annotation.QueryMapping
import org.springframework.stereotype.Controller
import java.util.UUID

@Controller
class GatewayController {

    private val services = mutableListOf(
        LabServiceDTO("1", "API Gateway Service", "Ponto de entrada único", true),
        LabServiceDTO("2", "Company Service", "Gestão de empresas", true)
    )

    @QueryMapping
    fun hello(): String = "Olá do Laboratório Comanda Livre! GraphQL Master funcionando. 🚀"

    @QueryMapping
    fun allServices(): List<LabServiceDTO> = services

    @QueryMapping
    fun findServiceById(@Argument id: String): LabServiceDTO? = services.find { it.id == id }

    @QueryMapping
    fun featuredService(): LabServiceDTO = services.first()

    @MutationMapping
    fun createService(@Argument input: ServiceInput): LabServiceDTO {
        val newService = LabServiceDTO(
            id = UUID.randomUUID().toString(),
            name = input.name,
            description = input.description,
            active = input.active ?: true
        )
        services.add(newService)
        return newService
    }

    @MutationMapping
    fun deleteService(@Argument id: String): Boolean {
        return services.removeIf { it.id == id }
    }
}