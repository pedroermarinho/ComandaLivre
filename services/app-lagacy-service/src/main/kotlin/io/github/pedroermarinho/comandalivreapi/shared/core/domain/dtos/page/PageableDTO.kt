package io.github.pedroermarinho.comandalivreapi.shared.core.domain.dtos.page

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "Dados de paginação para consulta")
data class PageableDTO(
    @param:Schema(description = "Número da página atual (zero-indexed)", example = "0")
    val pageNumber: Int = 0,
    @param:Schema(description = "Tamanho da página", example = "10")
    val pageSize: Int = 10,
    @param:Schema(description = "Termo de pesquisa (opcional)", example = "produto")
    val search: String? = null,
    @param:Schema(description = "Campo(s) para ordenação (opcional)", example = "[\"publicId\"]")
    val sort: List<String>? = null,
    @param:Schema(description = "Direção da ordenação, ex: 'asc' ou 'desc'", example = "asc")
    val direction: String? = "asc",
) {
    @get:Schema(description = "Offset calculado (pageNumber * pageSize)", example = "0")
    val offset: Long
        get() = pageNumber * pageSize.toLong()
}
